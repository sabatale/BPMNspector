package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class Ext098 {

	@Test
	public void testConstraintCancelFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "fail_cancel.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:startEvent[parent::bpmn:process][0]: Only messageEventDefininitions, timerEventDefinitions, conditionalEventDefinitions and signalEventDefinitions are allowed for top-level process start events\r\n//bpmn:cancelEventDefinition[0]: A cancel EndEvent is only allowed in a transaction sub-process");
	}

	@Test
	public void testConstraintCompensateFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "fail_compensate.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:startEvent[parent::bpmn:process][0]: Only messageEventDefininitions, timerEventDefinitions, conditionalEventDefinitions and signalEventDefinitions are allowed for top-level process start events");
	}

	@Test
	public void testConstraintErrorFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "fail_error.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:startEvent[parent::bpmn:process][0]: Only messageEventDefininitions, timerEventDefinitions, conditionalEventDefinitions and signalEventDefinitions are allowed for top-level process start events");
	}

	@Test
	public void testConstraintEscalationFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "fail_escalation.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:startEvent[parent::bpmn:process][0]: Only messageEventDefininitions, timerEventDefinitions, conditionalEventDefinitions and signalEventDefinitions are allowed for top-level process start events");
	}

	@Test
	public void testConstraintEscalationRefFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "fail_escalation_ref.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:startEvent[parent::bpmn:process][0]: Only messageEventDefininitions, timerEventDefinitions, conditionalEventDefinitions and signalEventDefinitions are allowed for top-level process start events");
	}

	@Test
	public void testConstraintLinkFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "fail_link.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:startEvent[parent::bpmn:process][0]: Only messageEventDefininitions, timerEventDefinitions, conditionalEventDefinitions and signalEventDefinitions are allowed for top-level process start events");
	}

	@Test
	public void testConstraintMultipleFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "fail_multiple.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:startEvent[parent::bpmn:process][0]: Only messageEventDefininitions, timerEventDefinitions, conditionalEventDefinitions and signalEventDefinitions are allowed for top-level process start events");
	}

	@Test
	public void testConstraintTerminateFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "fail_terminate.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:startEvent[parent::bpmn:process][0]: Only messageEventDefininitions, timerEventDefinitions, conditionalEventDefinitions and signalEventDefinitions are allowed for top-level process start events");
	}

	@Test
	public void testConstraintConditionalSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "success_conditional.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertTrue(valid);
		assertEquals(SchematronBPMNValidator.getErrors(), "");
	}

	@Test
	public void testConstraintMessageSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "success_message.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertTrue(valid);
		assertEquals(SchematronBPMNValidator.getErrors(), "");
	}

	@Test
	public void testConstraintMultipleSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "success_multiple.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertTrue(valid);
		assertEquals(SchematronBPMNValidator.getErrors(), "");
	}

	@Test
	public void testConstraintNoneSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "success_none.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertTrue(valid);
		assertEquals(SchematronBPMNValidator.getErrors(), "");
	}

	@Test
	public void testConstraintSignalSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "success_signal.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertTrue(valid);
		assertEquals(SchematronBPMNValidator.getErrors(), "");
	}

	@Test
	public void testConstraintTimerSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
				+ "success_timer.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertTrue(valid);
		assertEquals(SchematronBPMNValidator.getErrors(), "");
	}
}