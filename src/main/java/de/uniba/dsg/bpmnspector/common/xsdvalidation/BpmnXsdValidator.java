package de.uniba.dsg.bpmnspector.common.xsdvalidation;

import api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Does the bpmn xsd validation step
 *
 * @author Andreas Vorndran
 * @author Philipp Neugebauer
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class BpmnXsdValidator extends AbstractXsdValidator {

    private Schema schema;
    private static final Logger LOGGER = LoggerFactory.getLogger(BpmnXsdValidator.class.getSimpleName());

    {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setResourceResolver(new ResourceResolver());
        try {
            schema = schemaFactory.newSchema(new Source[] {
                    resolveResourcePaths("DC.xsd"),
                    resolveResourcePaths("DI.xsd"),
                    resolveResourcePaths("BPMNDI.xsd"),
                    resolveResourcePaths("BPMN20.xsd") });
        } catch (FileNotFoundException | SAXException e) {
            LOGGER.debug("schemafactory couldn't create schema, cause: {}", e);
        }
    }

    @Override
    public void validateAgainstXsd(File xmlFile, ValidationResult validationResult)
            throws IOException, SAXException, ValidationException {
        LOGGER.debug("file based xsd validation started: {}", xmlFile.getName());
        StreamSource src = new StreamSource(xmlFile);
        validateUsingStreamSource(src, new Resource(xmlFile.toPath()), validationResult);
    }

    @Override
    public void validateAgainstXsd(InputStream stream, Resource resource, ValidationResult validationResult)
            throws IOException, SAXException, ValidationException {
        LOGGER.debug("stream based xsd validation started: {}", resource.getResourceName());
        StreamSource src = new StreamSource(stream);
        validateUsingStreamSource(src, resource, validationResult);
    }

    private void validateUsingStreamSource(StreamSource src, Resource resource, ValidationResult validationResult)
            throws IOException, SAXException, ValidationException {
        List<SAXParseException> xsdErrorList = new ArrayList<>();
        Validator validator = schema.newValidator();
        validator.setErrorHandler(new XsdValidationErrorHandler(xsdErrorList));
        try {
            validator.validate(src);
            for (SAXParseException saxParseException : xsdErrorList) {
                Location location = createLocation(resource, saxParseException.getLineNumber(),
                        saxParseException.getColumnNumber());
                Violation violation = new Violation(location, saxParseException.getMessage(), "XSD-Check");
                if (!validationResult.getViolations().contains(violation)) {
                    validationResult.addViolation(violation);
                    LOGGER.info("xsd violation in {} at {} found", resource.getResourceName(), saxParseException.getLineNumber());
                }
            }
        } catch (CharConversionException e) {
            // Thrown if file encoding is not valid
            String msg = "Resource " + resource.getResourceName()
                    + " does not have claimed encoding - further processing is not possible.";
            Location location = createLocation(resource, 1, 1);
            Violation violation = new Violation(location, msg, "XSD-Check");
            if (!validationResult.getViolations().contains(violation)) {
                validationResult.addViolation(violation);
                LOGGER.info(msg);
            }
            throw new ValidationException("Cancel validation as checked resource does not have claimed encoding.");
        } catch (SAXParseException e) {
            // if process is not well-formed exception is not processed via the error handler
            Location location = createLocation(resource, e.getLineNumber(), e.getColumnNumber());
            Violation violation = new Violation(location, e.getMessage(), "XSD-Check");
            validationResult.addViolation(violation);
            String msg = String.format("Resource %s is not well-formed at line %d: %s", resource.getResourceName(), e.getLineNumber(),
                    e.getMessage());
            LOGGER.info(msg);
            throw new ValidationException("Cancel Validation as checked File is not well-formed.");
        }
    }
}
