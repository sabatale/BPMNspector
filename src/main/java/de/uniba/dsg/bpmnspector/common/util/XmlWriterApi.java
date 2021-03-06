package de.uniba.dsg.bpmnspector.common.util;

import api.ValidationResult;
import api.Violation;
import api.Warning;
import de.uniba.dsg.bpmnspector.common.importer.BPMNProcess;
import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;


/**
 * This class is used for writing xml files via jaxb
 *
 * @author Philipp Neugebauer
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class XmlWriterApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlWriterApi.class
            .getSimpleName());

    private final JDOMFactory factory = new DefaultJDOMFactory();
    private final Namespace nsp = Namespace.getNamespace(ConstantHelper.PI_NAMESPACE);

    public static void writeBPMNProcess(BPMNProcess process, Path targetPath) throws IOException {
        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());

        try (Writer writer = Files.newBufferedWriter(targetPath, StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {
            outputter.output(process.getProcessAsDoc(), writer);
        }

    }

    public static Path createXmlReport(ValidationResult result) {
        try {
            String fileName = result.getFoundFiles().get(0).getFileName().toString();
            Path reportPath = FileUtils.createResourcesForReports();

            Path reportFile = FileUtils.createFileForReport(reportPath, fileName, "xml");

            XmlWriterApi xmlWriter = new XmlWriterApi();
            xmlWriter.writeResult(result, reportFile);

            return reportFile;
        } catch ( IOException ioe) {
            LOGGER.error("Creation of XML Report files failed.", ioe);
            return null;
        }
    }

    /**
     * writes the result to the given file
     *
     * @param result
     *            the validation result, which should be written to a file
     * @param file
     *            the file, where the validation result should be written to
     * @throws java.io.IOException
     *             if an error occurs during xml writing process
     */
    private void writeResult(ValidationResult result, Path file) throws IOException {
        Document doc = createDocFromValidationResult(result);
        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());
        outputter.output(doc, new FileOutputStream(file.toFile()));
        LOGGER.info("Report for Resource {} successfully written to {}", result.getResources().get(0).getResourceName(), file.toString());
    }

    private Document createDocFromValidationResult(ValidationResult result) {

        Element root = factory.element("validationResult", nsp);


        root.addContent(createTextElement("valid", String.valueOf(result.isValid())));

        Element checkedResources = factory.element("checkedResources", nsp);
        result.getFoundFiles().forEach(x -> checkedResources.addContent(createTextElement("resource", x.toString())));
        root.addContent(checkedResources);

        if(!result.isValid()) {
            Element resourcesWithViolations = factory.element("resourcesWithViolations", nsp);
            result.getResourcesWithViolations().forEach(x -> resourcesWithViolations.addContent(createTextElement("resource", x.toString())));
            root.addContent(resourcesWithViolations);

            Element violations = factory.element("violations", nsp);
            result.getViolations().forEach(x -> violations.addContent(createViolationElement(x)));
            root.addContent(violations);
        }
        if(result.getWarnings()!=null && !result.getWarnings().isEmpty()) {
            Element warnings = factory.element("warnings", nsp);
            result.getWarnings().forEach(x -> warnings.addContent(createWarningElement(x)));
            root.addContent(warnings);
        }

        return factory.document(root);
    }

    private Element createViolationElement(Violation violation) {
        Element vElem = factory.element("violation", nsp);
        vElem.setAttribute("constraint", violation.getConstraint());
        vElem.setAttribute("fileName", violation.getLocation().getResource().getResourceName());
        if(violation.getLocation().getLocation().getRow()!=-1) {
            vElem.setAttribute("line", String.valueOf(violation.getLocation().getLocation().getRow()));
        }
        if(violation.getLocation().getLocation().getColumn()!=-1) {
            vElem.setAttribute("column", String.valueOf(violation.getLocation().getLocation().getColumn()));
        }
        if(violation.getLocation().getXpath().isPresent()) {
            vElem.addContent(createTextElement("xPath", violation.getLocation().getXpath().get()));
        }
        vElem.addContent(createTextElement("message", violation.getMessage()));
        return vElem;
    }

    private Element createWarningElement(Warning warning) {
        Element wElem = factory.element("warning", nsp);
        wElem.setAttribute("fileName", warning.getLocation().getResource().getResourceName());
        if(warning.getLocation().getLocation().getRow()!=-1) {
            wElem.setAttribute("line", String.valueOf(warning.getLocation().getLocation().getRow()));
        }
        if(warning.getLocation().getLocation().getColumn()!=-1) {
            wElem.setAttribute("column", String.valueOf(warning.getLocation().getLocation().getColumn()));
        }
        if(warning.getLocation().getXpath().isPresent()) {
            wElem.addContent(createTextElement("xPath", warning.getLocation().getXpath().get()));
        }
        wElem.addContent(createTextElement("message", warning.getMessage()));
        return wElem;
    }


    private Element createTextElement(String name, String text) {
        Element element = factory.element(name, nsp);
        element.setText(text);
        return element;
    }
}