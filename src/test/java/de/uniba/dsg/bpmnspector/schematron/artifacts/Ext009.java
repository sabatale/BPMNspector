package de.uniba.dsg.bpmnspector.schematron.artifacts;

import api.Violation;

/**
 * Test class for testing Constraint EXT.009
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext009 extends AbstractArtifactTest {

    @Override
    protected void assertViolation(Violation v) {
        assertViolation(v, "(//bpmn:messageFlow[@sourceRef])[1]", 7);
    }

    @Override
    protected String getErrorMessage() {
        return "An Artifact MUST NOT be a source for a Message Flow";
    }

    @Override
    protected String getExtNumber() {
        return "009";
    }
}
