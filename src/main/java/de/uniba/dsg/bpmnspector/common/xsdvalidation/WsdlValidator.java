package de.uniba.dsg.bpmnspector.common.xsdvalidation;

import api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * validator for the schema validation of wsdl files
 *
 * @author Philipp Neugebauer
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class WsdlValidator extends AbstractXsdValidator {
    private Schema schema;
    private static final Logger LOGGER = LoggerFactory.getLogger(
            WsdlValidator.class
                    .getSimpleName());


    {
        SchemaFactory schemaFactory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            schema = schemaFactory
                    .newSchema(resolveResourcePaths("wsdl20.xsd"));
        } catch (FileNotFoundException | SAXException e) {
            LOGGER.debug("schemafactory couldn't create schema, cause: {}", e);
        }
    }

    @Override
    public void validateAgainstXsd(File xmlFile,
            ValidationResult validationResult)
            throws IOException, SAXException, ValidationException {
        LOGGER.debug("WSDL xsd validation started: {}", xmlFile.getName());
        List<SAXParseException> xsdErrorList = new ArrayList<>();
        Validator validator = schema.newValidator();
        validator.setErrorHandler(new XsdValidationErrorHandler(xsdErrorList));
        try {
            validator.validate(new StreamSource(xmlFile));
            for (SAXParseException saxParseException : xsdErrorList) {
                Location location = new Location(xmlFile.toPath(),
                        new LocationCoordinate(saxParseException.getLineNumber(),
                                saxParseException.getColumnNumber()), null);
                Violation violation = new Violation(location, saxParseException.getMessage(), "WSDL-XSD-Check");
                validationResult.addViolation(violation);
                LOGGER.info("WSDL xsd violation in {} at {} found",
                        xmlFile.getName(), saxParseException.getLineNumber());
            }
        } catch (SAXParseException e) {
            // if process is not well-formed exception is not processed via the error handler
            Location location = new Location(xmlFile.toPath(),
                    new LocationCoordinate(e.getLineNumber(),
                            e.getColumnNumber()), null);
            Violation violation = new Violation(location, e.getMessage(), "XSD-Check");
            validationResult.addViolation(violation);
            String msg = String.format("File %s is not well-formed at line %d: %s", xmlFile.getName(),
                    e.getLineNumber(), e.getMessage());
            LOGGER.info(msg);
            throw new ValidationException("Cancel Validation as checked File is not well-formed.");
        }
    }
}
