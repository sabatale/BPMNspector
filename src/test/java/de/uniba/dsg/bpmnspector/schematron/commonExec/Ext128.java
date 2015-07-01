package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.128
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext128 extends TestCase {

    private static final String ERR_MSG = "An operationRef must be present if the process should be executable.";

    @Test
    public void testConstraintFailBoundary() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT128_failure_boundary.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:messageEventDefinition[ancestor::bpmn:process[@isExecutable='true']][0]", 24);
    }

    @Test
    public void testConstraintFailEndEvent() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT128_failure_endEvent.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:messageEventDefinition[ancestor::bpmn:process[@isExecutable='true']][0]", 11);
    }

    @Test
    public void testConstraintFailIntCatch() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT128_failure_intCatch.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:messageEventDefinition[ancestor::bpmn:process[@isExecutable='true']][0]", 15);
    }

    @Test
    public void testConstraintFailIntThrow() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT128_failure_intThrow.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:messageEventDefinition[ancestor::bpmn:process[@isExecutable='true']][0]", 15);
    }

    @Test
    public void testConstraintFailStartEvent() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT128_failure_startEvent.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:messageEventDefinition[ancestor::bpmn:process[@isExecutable='true']][0]", 8);
    }

//    @Test
//    public void testConstraintSuccessBoundary() throws ValidationException {
//        verifyValidResult(createFile("EXT128_success_boundary.bpmn"));
//    }
//
//    @Test
//    public void testConstraintSuccessEndEvent() throws ValidationException {
//        verifyValidResult(createFile("EXT128_success_endEvent.bpmn"));
//    }
//
//    @Test
//    public void testConstraintSuccessIntCatchw() throws ValidationException {
//        verifyValidResult(createFile("EXT128_success_intCatch.bpmn"));
//    }
//
//    @Test
//    public void testConstraintSuccessIntThrow() throws ValidationException {
//        verifyValidResult(createFile("EXT128_success_intThrow.bpmn"));
//    }
//
//    @Test
//    public void testConstraintSuccessNotExecutable() throws ValidationException {
//        verifyValidResult(createFile("EXT128_success_notExecutable.bpmn"));
//    }
//
//    @Test
//    public void testConstraintSuccessStartEvent() throws ValidationException {
//        verifyValidResult(createFile("EXT128_success_startEvent.bpmn"));
//    }

    @Override
    protected String getExtNumber() {
        return "128";
    }
}