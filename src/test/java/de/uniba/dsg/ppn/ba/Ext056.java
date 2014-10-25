package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.qos.logback.classic.Level;
import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;
import de.uniba.dsg.ppn.ba.validation.SchematronBPMNValidator;

public class Ext056 {

    private SchematronBPMNValidator validator = null;
    private final static String ERRORMESSAGEONE = "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target";
    private final static String ERRORMESSAGETWO = "A SubProcess must not contain Choreography Activities";
    private final static String XPATHSTRING = "//bpmn:*[./@id = string(//bpmn:sequenceFlow/@targetRef)][0]";

    @Before
    public void setUp() {
        validator = new SchematronBPMNValidator();
        validator.setLogLevel(Level.OFF);
    }

    @After
    public void tearDown() {
        validator = null;
    }

    @Test
    public void testConstraintCallChoreographyFail()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "056" + File.separator
                + "fail_call_choreography.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(2, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(ERRORMESSAGEONE, v.getMessage());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals(11, v.getLine());
        v = result.getViolations().get(1);
        assertEquals(ERRORMESSAGETWO, v.getMessage());
        assertEquals("//bpmn:subProcess[0]", v.getxPath());
        assertEquals(4, v.getLine());
    }

    @Test
    public void testConstraintChoreographyTaskFail()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "056" + File.separator
                + "fail_choreography_task.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(2, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(ERRORMESSAGEONE, v.getMessage());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals(11, v.getLine());
        v = result.getViolations().get(1);
        assertEquals(ERRORMESSAGETWO, v.getMessage());
        assertEquals("//bpmn:subProcess[0]", v.getxPath());
        assertEquals(4, v.getLine());
    }

    @Test
    public void testConstraintChoreographyTaskTransactionFail()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "056" + File.separator
                + "fail_choreography_task_transaction.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(2, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(ERRORMESSAGEONE, v.getMessage());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals(11, v.getLine());
        v = result.getViolations().get(1);
        assertEquals(ERRORMESSAGETWO, v.getMessage());
        assertEquals("//bpmn:transaction[0]", v.getxPath());
        assertEquals(4, v.getLine());
    }

    @Test
    public void testConstraintSubChoreographyFail()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "056" + File.separator
                + "fail_sub_choreography.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(2, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(ERRORMESSAGEONE, v.getMessage());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals(11, v.getLine());
        v = result.getViolations().get(1);
        assertEquals(ERRORMESSAGETWO, v.getMessage());
        assertEquals("//bpmn:subProcess[0]", v.getxPath());
        assertEquals(4, v.getLine());
    }
}
