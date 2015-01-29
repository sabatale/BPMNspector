package de.uniba.dsg.ppn.ba;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import api.ValidationResult;
import api.ValidationException;
import de.uniba.dsg.bpmnspector.common.util.XmlWriter;
import de.uniba.dsg.ppn.ba.validation.SchematronBPMNValidator;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    private final static Logger LOGGER;
    private final static Level DEBUGLEVEL;

    static {
        DEBUGLEVEL = Level.DEBUG;
        LOGGER = (Logger) LoggerFactory.getLogger(Main.class.getSimpleName());
    }

    public static void main(String... args) {
        SchematronBPMNValidator validator = new SchematronBPMNValidator();
        ArrayList<String> argsAsList = new ArrayList<>(Arrays.asList(args));
        XmlWriter xmlWriter = new XmlWriter();

        if (argsAsList.contains("--debug") || argsAsList.contains("-d")) {
            validator.setLogLevel(DEBUGLEVEL);
            LOGGER.setLevel(DEBUGLEVEL);
            ((Logger) LoggerFactory.getLogger(xmlWriter.getClass()
                    .getSimpleName())).setLevel(DEBUGLEVEL);
            argsAsList.remove("-d");
            argsAsList.remove("--debug");
        }

        LOGGER.info("loglevel is set to {}", LOGGER.getEffectiveLevel());

        if (argsAsList.isEmpty()) {
            LOGGER.error("There must be files to check!");
            System.exit(-1);
        } else {
            for (String parameter : argsAsList) {
                try {
                    File file = new File(parameter);
                    if (!file.isAbsolute()) {
                        file = file.getAbsoluteFile();
                    }
                    ValidationResult result = validator.validate(file);
                    //xmlWriter.writeResult(result,
                    //        new File(file.getParentFile() + File.separator
                    //                + "validation_result_" + file.getName()
                    //                + ".xml"));
                } catch (ValidationException e) {
                    LOGGER.error(e.getMessage());
                //} catch (JAXBException e) {
                //    LOGGER.error("result of validation couldn't be written in xml!");
                }
            }
        }
    }
}
