package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Ext079 extends TestCase {

    @Test
    public void testConstraintFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(createFile("fail.bpmn"),
                1);
        Violation v = result.getViolations().get(0);
        assertEquals(
                "InputOutputSpecifications are not allowed in SubProcesses",
                v.getMessage());
        assertEquals("//bpmn:subProcess[0]", v.getxPath());
        assertEquals(7, v.getLine());
    }

    @Test
    public void testConstraintSuccess() throws BpmnValidationException {
        verifyValidResult(createFile("Success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "079";
    }
}
