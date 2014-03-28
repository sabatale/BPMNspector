package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class Ext006 {

	@Test
	public void testConstraintAssociationFail() throws Exception {
		File f = new File(
				"D:\\Philipp\\BA\\testprocesses\\006\\Fail_association.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, false);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:sequenceFlow[@targetRef][0]: An Artifact MUST NOT be a target for a Sequence Flow");
	}

	@Test
	public void testConstraintGroupFail() throws Exception {
		File f = new File(
				"D:\\Philipp\\BA\\testprocesses\\006\\Fail_group.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, false);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:sequenceFlow[@targetRef][0]: An Artifact MUST NOT be a target for a Sequence Flow");
	}

	@Test
	public void testConstraintTextAnnotationFail() throws Exception {
		File f = new File(
				"D:\\Philipp\\BA\\testprocesses\\006\\Fail_text_annotation.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, false);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:sequenceFlow[@targetRef][0]: An Artifact MUST NOT be a target for a Sequence Flow");
	}
}
