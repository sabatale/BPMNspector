package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Ext006 {

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
	public void testConstraintAssociationFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "006" + File.separator
				+ "Fail_association.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:sequenceFlow[@targetRef][0]: An Artifact MUST NOT be a target for a Sequence Flow\r\n//bpmn:*[./@id = string(//bpmn:sequenceFlow/@targetRef)][0]: For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target\r\n//bpmn:sequenceFlow[@targetRef][0]: The target element of the sequence flow must reference the SequenceFlow definition using their incoming attributes.");
	}

	@Test
	public void testConstraintGroupFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "006" + File.separator
				+ "Fail_group.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:sequenceFlow[@targetRef][0]: An Artifact MUST NOT be a target for a Sequence Flow\r\n//bpmn:*[./@id = string(//bpmn:sequenceFlow/@targetRef)][0]: For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target\r\n//bpmn:sequenceFlow[@targetRef][0]: The target element of the sequence flow must reference the SequenceFlow definition using their incoming attributes.");
	}

	@Test
	public void testConstraintTextAnnotationFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "006" + File.separator
				+ "Fail_text_annotation.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:sequenceFlow[@targetRef][0]: An Artifact MUST NOT be a target for a Sequence Flow\r\n//bpmn:*[./@id = string(//bpmn:sequenceFlow/@targetRef)][0]: For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target\r\n//bpmn:sequenceFlow[@targetRef][0]: The target element of the sequence flow must reference the SequenceFlow definition using their incoming attributes.");
	}
}
