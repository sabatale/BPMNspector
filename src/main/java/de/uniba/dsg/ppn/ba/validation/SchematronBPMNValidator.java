package de.uniba.dsg.ppn.ba.validation;

import api.*;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.phloc.schematron.ISchematronResource;
import com.phloc.schematron.pure.SchematronResourcePure;
import de.uniba.dsg.bpmnspector.common.importer.BPMNProcess;
import de.uniba.dsg.bpmnspector.common.importer.ProcessImporter;
import de.uniba.dsg.ppn.ba.helper.ConstantHelper;
import de.uniba.dsg.ppn.ba.preprocessing.PreProcessor;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.located.LocatedElement;
import org.jdom2.output.DOMOutputter;
import org.jdom2.xpath.XPathFactory;
import org.oclc.purl.dsdl.svrl.FailedAssert;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.transform.dom.DOMSource;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of BpmnValidator
 * <p>
 * More information : {@link BpmnValidator}
 * <p>
 * Does the validation process of the xsd and the schematron validation and
 * returns the results of the validation
 *
 * @author Philipp Neugebauer
 * @author Matthias Geiger
 * @version 1.0
 */
public class SchematronBPMNValidator implements BpmnValidator {

    private final PreProcessor preProcessor;
    private final ProcessImporter bpmnImporter;
    private final Ext002Checker ext002Checker;
    private final static Logger LOGGER;

    static {
        LOGGER = (Logger) LoggerFactory.getLogger(SchematronBPMNValidator.class
                .getSimpleName());
    }

    {
        preProcessor = new PreProcessor();
        bpmnImporter = new ProcessImporter();
        ext002Checker = new Ext002Checker();
    }

    @Override
    public Level getLogLevel() {
        return LOGGER.getLevel();
    }

    @Override
    public void setLogLevel(Level logLevel) {
        // FIXME: without phloc libraries
        ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME))
                .setLevel(logLevel);
    }

    @Override
    public List<ValidationResult> validateFiles(List<File> xmlFiles)
            throws ValidationException {
        List<ValidationResult> validationResults = new ArrayList<>();
        for (File xmlFile : xmlFiles) {
            validationResults.add(validate(xmlFile));
        }
        return validationResults;
    }

    @Override
    public ValidationResult validate(File xmlFile)
            throws ValidationException {

        ValidationResult validationResult = new UnsortedValidationResult();
        // Trying to import process
        BPMNProcess process = bpmnImporter
                .importProcessFromPath(Paths.get(xmlFile.getPath()), validationResult);
        if(process != null) {
            validate(process, validationResult);
        }
        return validationResult;

    }

    public ValidationResult validate(BPMNProcess process, ValidationResult validationResult)
            throws ValidationException {
        final ISchematronResource schematronSchema = SchematronResourcePure
                .fromClassPath("validation.sch");
        if (!schematronSchema.isValidSchematron()) {
            LOGGER.debug("schematron file is invalid");
            throw new ValidationException("Invalid Schematron file!");
        }

        LOGGER.info("Validating {}", process.getBaseURI());

        try {
                // EXT.002 checks whether there are ID duplicates - as ID
                // duplicates in a single file are already detected during XSD
                // validation this is only relevant if other processes are imported
                if(!process.getChildren().isEmpty()) {
                    ext002Checker.checkConstraint002(process, validationResult);
                }

                org.jdom2.Document documentToCheck;
                if (process.getChildren() == null || process.getChildren()
                        .isEmpty()) {
                    documentToCheck = process.getProcessAsDoc();
                } else {
                    documentToCheck = preProcessor.preProcess(process);
                }
                DOMOutputter domOutputter = new DOMOutputter();
                Document w3cDoc = domOutputter
                        .output(documentToCheck);
                SchematronOutputType schematronOutputType = schematronSchema
                        .applySchematronValidationToSVRL(new DOMSource(w3cDoc));

                for (Object obj : schematronOutputType.getActivePatternAndFiredRuleAndFailedAssert()) {
                    if (obj instanceof FailedAssert) {
                        handleSchematronErrors(process, validationResult,
                                (FailedAssert) obj);
                    }
                }
        } catch (Exception e) { // NOPMD
            LOGGER.debug("exception during schematron validation. Cause: {}", e);
            throw new ValidationException(
                    "Something went wrong during schematron validation!");
        }

        LOGGER.info("Validating process successfully done, file is valid: {}",
                validationResult.isValid());

        return validationResult;
    }


    /**
     * tries to locate errors in the specific files
     *
     * @param baseProcess
     *            the file where the error must be located
     * @param validationResult
     *            the result of the validation to add new found errors
     * @param failedAssert
     *            the error of the schematron validation
     */
    private void handleSchematronErrors(BPMNProcess baseProcess,
            ValidationResult validationResult, FailedAssert failedAssert) {
        String message = failedAssert.getText().trim();
        String constraint = message.substring(0, message.indexOf('|'));
        String errorMessage = message.substring(message.indexOf('|') + 1);

        int line = -1;
        int column = -1;
        String fileName;
        String location = "";

        // direct usage of xpath expression failedAssert.getLocation() not possible
        // when using JDOM - predicates are not supported correctly.
        // Instead: determine the index of the element to be found and use this
        // accessor in the list of found elements
        Pattern p = Pattern.compile("\\[\\d+\\]");
        Matcher m = p.matcher(failedAssert.getLocation());
        int xpathIndex = 0;
        if(m.find()) {
            xpathIndex = Integer.parseInt(
                    m.group(0).replace("[", "").replace("]", ""));
            location = failedAssert.getLocation().replace(m.group(0), "");
        }

        LOGGER.debug("XPath to evaluate: "+location);
        XPathFactory fac = XPathFactory.instance();
        List<Element> elems = fac.compile(location, Filters.element(), null,
                Namespace.getNamespace("bpmn", ConstantHelper.BPMNNAMESPACE)).evaluate(
                baseProcess.getProcessAsDoc());
        LOGGER.debug("Number of found elems: "+elems.size());
        if(elems.size()>=xpathIndex+1) {
            line = ((LocatedElement) elems.get(xpathIndex)).getLine();
            column = ((LocatedElement) elems.get(xpathIndex)).getColumn();
            fileName = baseProcess.getBaseURI();
            location = failedAssert.getLocation();
        } else {
            try {
                String xpathId = "";
                if (failedAssert.getDiagnosticReferenceCount() > 0) {
                    xpathId = failedAssert.getDiagnosticReference().get(0)
                            .getText().trim();
                }
                LOGGER.debug("Trying to locate in files: "+xpathId);
                String[] result = searchForViolationFile(xpathId, baseProcess);
                fileName = result[0];
                line = Integer.parseInt(result[1]);
                column = Integer.parseInt(result[2]);
                location = result[3];
            } catch (ValidationException e) {
                fileName = e.getMessage();
                LOGGER.error("Line of affected Element could not be determined.");
            } catch (StringIndexOutOfBoundsException e) {
                fileName = "Element couldn't be found!";
                LOGGER.error("Line of affected Element could not be determined.");
            }
        }

        String logText = String.format(
                "violation of constraint %s found in %s at line %s.",
                constraint, fileName, line);
        LOGGER.info(logText);

        Location violationLocation = new Location(
                Paths.get(fileName),
                new LocationCoordinate(line, column), location);
        Violation violation = new Violation(violationLocation, errorMessage, constraint);
        validationResult.addViolation(violation);
    }

    /**
     * searches for the file and line, where the violation occured
     *
     * @param xpathExpression
     *            the expression, through which the file and line should be
     *            identified
     * @param baseProcess
     *            baseProcess used for validation
     * @return string array with filename, line and xpath expression to find the
     *         element
     * @throws ValidationException
     *             if no element can be found
     */
    // TODO: extract in own object?
    private String[] searchForViolationFile(String xpathExpression,
            BPMNProcess baseProcess) throws ValidationException {
        String fileName = "";
        String line = "-1";
        String column = "-1";
        String xpathObjectId = "";

        String namespacePrefix = xpathExpression.substring(0,
                xpathExpression.indexOf('_'));

        Optional<BPMNProcess> optional = baseProcess.findProcessByGeneratedPrefix(namespacePrefix);
        if(optional.isPresent()) {
            fileName = optional.get().getBaseURI();

            // use ID with generated prefix for lookup
            xpathObjectId = createIdBpmnExpression(xpathExpression);
            LOGGER.debug("Expression to evaluate: "+xpathObjectId);
            XPathFactory fac = XPathFactory.instance();
            List<Element> elems = fac.compile(xpathObjectId, Filters.element(), null,
                    Namespace.getNamespace("bpmn", ConstantHelper.BPMNNAMESPACE)).evaluate(optional.get().getProcessAsDoc());

            if(elems.size()==1) {
                line = String.valueOf(((LocatedElement) elems.get(0)).getLine());
                column = String.valueOf(((LocatedElement) elems.get(0)).getColumn());
                //use ID without prefix  (=original ID) as Violation xPath
                xpathObjectId = createIdBpmnExpression(xpathExpression
                        .substring(xpathExpression.indexOf('_') + 1));
            }
        } else {
            // File not found
        }

        if ("-1".equals(line) || "-1".equals(column)) {
            throw new ValidationException("BPMN Element couldn't be found!");
        }

        return new String[] { fileName, line, column, xpathObjectId };
    }

    /**
     * creates a xpath expression for finding the id
     *
     * @param id
     *            the id, to which the expression should refer
     * @return the xpath expression, which refers the given id
     */
    private static String createIdBpmnExpression(String id) {
        return String.format("//bpmn:*[@id = '%s']", id);
    }
}
