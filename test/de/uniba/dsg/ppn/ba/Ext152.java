package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Ext152 {

	SchematronBPMNValidator validator = null;

	@Before
	public void setUp() throws Exception {
		validator = new SchematronBPMNValidator();
	}

	@After
	public void tearDown() throws Exception {
		validator = null;
	}

	@Test
	public void testConstraintFail1() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "152" + File.separator
				+ "fail_1.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:sequenceFlow[0]: A Sequence Flow must not cross the border of a Pool\r\n//bpmn:subProcess[@isForCompensation = 'false' and @triggeredByEvent = 'false'] [parent::*/bpmn:startEvent][0]: If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow");
	}

	@Test
	public void testConstraintFail2() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "152" + File.separator
				+ "fail_2.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:sequenceFlow[1]: A Sequence Flow must not cross the border of a Pool\r\n//bpmn:subProcess[@isForCompensation = 'false' and @triggeredByEvent = 'false'] [parent::*/bpmn:endEvent][0]: If end events are used, all flow nodes must have an outgoing sequence flow");
	}

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "152" + File.separator
				+ "success.bpmn");
		boolean valid = validator.validate(f);
		assertTrue(valid);
		assertEquals(validator.getErrors(), "");
	}
}
