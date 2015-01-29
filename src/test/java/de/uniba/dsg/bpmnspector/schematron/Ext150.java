package de.uniba.dsg.bpmnspector.schematron;

import api.ValidationResult;
import api.ValidationException;
import api.Violation;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.150
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext150 extends TestCase {

    private final static String ERRORMESSAGEONE = "If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow";
    private final static String ERRORMESSAGETWO = "If end events are used, all flow nodes must have an outgoing sequence flow";

    @Test
    public void testConstraintNormalSequenceFlowFail1()
            throws ValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_normal_sequence_flow_missing_1.bpmn"), 2);
        assertFirstViolation(
                result.getViolations().get(0),
                "//bpmn:task[@isForCompensation = 'false'] [parent::*/bpmn:startEvent][4]",
                55);
        assertSecondViolation(
                result.getViolations().get(1),
                "//bpmn:subProcess[@isForCompensation = 'false' and @triggeredByEvent = 'false'] [parent::*/bpmn:endEvent][0]",
                7);
    }

    @Test
    public void testConstraintNormalSequenceFlowFail2()
            throws ValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_normal_sequence_flow_missing_2.bpmn"), 2);
        assertFirstViolation(
                result.getViolations().get(0),
                "//bpmn:subProcess[@isForCompensation = 'false' and @triggeredByEvent = 'false'] [parent::*/bpmn:startEvent][0]",
                8);
        assertSecondViolation(
                result.getViolations().get(1),
                "//bpmn:task[@isForCompensation = 'false'] [parent::*/bpmn:endEvent][3]",
                49);
    }

    @Test
    public void testConstraintSequenceFlowInSubProcessFail1()
            throws ValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_sequence_flow_in_sub_process_missing_1.bpmn"),
                2);
        assertFirstViolation(result.getViolations().get(0),
                "//bpmn:parallelGateway[parent::*/bpmn:startEvent][0]", 13);
        assertSecondViolation(
                result.getViolations().get(1),
                "//bpmn:task[@isForCompensation = 'false'] [parent::*/bpmn:endEvent][0]",
                10);
    }

    @Test
    public void testConstraintSequenceFlowInSubProcessFail2()
            throws ValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_sequence_flow_in_sub_process_missing_2.bpmn"),
                2);
        assertViolation(
                result.getViolations().get(0),
                "A Gateway MUST have either multiple incoming Sequence Flows or multiple outgoing Sequence Flows",
                "//bpmn:parallelGateway[0]", 14);
        assertFirstViolation(
                result.getViolations().get(1),
                "//bpmn:callActivity[@isForCompensation = 'false'] [parent::*/bpmn:startEvent][0]",
                38);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("success.bpmn"));
    }

    @Test
    public void testConstraintSuccess2() throws ValidationException {
        verifyValidResult(createFile("success_2.bpmn"));
    }

    @Test
    public void testConstraintLinkEventSuccess() throws ValidationException {
        verifyValidResult(createFile("success_linkevent.bpmn"));
    }

    private void assertFirstViolation(Violation v, String xpath, int line) {
        assertViolation(v, ERRORMESSAGEONE, xpath, line);
    }

    private void assertSecondViolation(Violation v, String xpath, int line) {
        assertViolation(v, ERRORMESSAGETWO, xpath, line);
    }

    @Override
    protected String getExtNumber() {
        return "150";
    }

}