package de.uniba.dsg.bpmnspector.schematron.descriptive;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.095
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext095 extends TestCase {

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("Fail.bpmn"),
                1);
        assertViolation(result.getViolations().get(0),
                "(//bpmn:intermediateThrowEvent/bpmn:messageEventDefinition)[1]",
                14);
    }

    @Test
    public void testConstraintEndFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(
                createFile("fail_end.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                "(//bpmn:endEvent/bpmn:messageEventDefinition)[1]", 10);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("Success.bpmn"));
    }

    @Override
    protected String getErrorMessage() {
        return "EventDefinitions defined in a throw event are not allowed to be used somewhere else";
    }

    @Override
    protected String getExtNumber() {
        return "095";
    }
}
