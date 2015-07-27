package de.uniba.dsg.bpmnspector.refcheck;

import api.Location;
import api.LocationCoordinate;
import api.ValidationResult;
import api.Violation;
import de.uniba.dsg.bpmnspector.refcheck.utils.JDOMUtils;
import de.uniba.dsg.bpmnspector.refcheck.utils.ViolationMessageCreator;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.xpath.XPathHelper;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReferenceChecker {

    public static final String CONSTRAINT_REF_TYPE = "REF_TYPE";
    public static final String CONSTRAINT_REF_EXISTENCE = "REF_EXISTENCE";

    private final Map<String, BPMNElement> bpmnRefElements;

    public ReferenceChecker(Map<String, BPMNElement> bpmnRefElements) {
        this.bpmnRefElements = bpmnRefElements;
    }

    /**
     * This method validates the current element against the checking reference.
     * Some already existing information will be expected as parameters.
     *
     * @param elements
     *            the elements of the root file (= <code>bpmnFile</code>)
     * @param importedElements
     *            the elements which have been defined in imported files
     * @param validationResult
     *            the validation result for adding found violations
     * @param currentElement
     *            the current element to validate
     * @param line
     *            the line of the reference in the root file for the violation
     *            message
     * @param column
     *            the column of the reference
     * @param checkingReference
     *            the reference to validate against
     * @param referencedId
     *            the referenced ID
     * @param ownPrefix
     *        the namespace prefix of the current element
     */
    public void validateReferenceType(Map<String, Element> elements,
            Map<String, Map<String, Element>> importedElements,
            ValidationResult validationResult, Element currentElement, int line,
            int column, Reference checkingReference, String referencedId, String ownPrefix) {
        if (checkingReference.isQname()) {
            // Check whether the QName is prefixed
            if (referencedId.contains(":")) { // reference ID is prefixed and
                // therefore probably to find in an imported file
                String[] parts = referencedId.split(":");
                String prefix = parts[0];
                String importedId = parts[1];

                String namespace = "";

                for (Namespace nsp : currentElement.getNamespacesInScope()) {
                    if (nsp.getPrefix().equals(prefix)) {
                        namespace = nsp.getURI();
                        break;
                    }
                }

                Map<String, Element> relevantImportedElements = importedElements
                        .get(namespace);

                // Checking whether the namespace is used for the root and/or the imported file
                // to determine the correct element to be checked
                if (ownPrefix.equals(prefix)) { // namespace is used for the root file
                    // check whether element with ID importedId exists in root or another file
                    if (elements.containsKey(importedId)) { // elem is in root file
                        checkTypeAndAddViolation(validationResult, line, column, currentElement, checkingReference,
                                elements.get(importedId));
                    } else if (relevantImportedElements != null
                            && relevantImportedElements.containsKey(importedId)) { // element is found in the imported file
                        checkTypeAndAddViolation(validationResult, line, column, currentElement, checkingReference,
                                relevantImportedElements.get(importedId));
                    } else {
                        createAndAddExistenceViolation(validationResult, line, column, currentElement, checkingReference);
                    }

                } else if (relevantImportedElements != null) { // NOPMD
                    // namespace is used by an imported file
                    if (relevantImportedElements.containsKey(importedId)) {
                        checkTypeAndAddViolation(validationResult, line, column, currentElement, checkingReference,
                                relevantImportedElements.get(importedId));
                    } else {
                        createAndAddExistenceViolation(validationResult, line, column, currentElement, checkingReference);
                    }
                } else {
                    // invalid namespace: not used in any file case if the namespace is not used for the root file or an
                    // imported file import does not exist or is no BPMN file (as it has to be)
                    createAndAddExistenceViolation(validationResult, line, column, currentElement, checkingReference);
                }

            } else { // no QName prefix - the elem has to be in the root file
                if (elements.containsKey(referencedId)) {
                    checkTypeAndAddViolation(validationResult, line, column, currentElement, checkingReference,
                            elements.get(referencedId));
                } else {
                    createAndAddExistenceViolation(validationResult, line, column, currentElement, checkingReference);
                }
            }

        } else { // reference uses IDREF - ref has to exist in root file
            if (elements.containsKey(referencedId)) {
                checkTypeAndAddViolation(validationResult, line, column, currentElement, checkingReference,
                        elements.get(referencedId));
            } else {
                createAndAddExistenceViolation(validationResult, line, column, currentElement, checkingReference);
            }
        }
    }



    /**
     * This method validates the type of the referenced element against the
     * checking reference and adds a violation if found.
     *
     * @param validationResult
     *            the violation list for adding found violations
     * @param line
     *            the line of the reference in the root file for the violation
     *            message
     * @param column
     *            the column of the reference
     * @param currentElement
     *            the current element to validate
     * @param checkingReference
     *            the reference to validate against
     * @param referencedElement
     *            the referenced element to validate
     */
    private void checkTypeAndAddViolation(ValidationResult validationResult,int line, int column,
                                          Element currentElement, Reference checkingReference,
                                          Element referencedElement) {
        boolean validType = false;
        List<String> referencedTypes = checkingReference.getTypes();
        // do not check references which are only for existence validation
        if (referencedTypes != null) {
            // find all possible types (with subtypes/children)
            List<String> types = new ArrayList<>(referencedTypes);
            boolean childfound;
            do {
                childfound = false;
                List<String> typesCopy = new ArrayList<>(types);
                for (String type : typesCopy) {
                    if (bpmnRefElements.containsKey(type)) {
                        BPMNElement bpmnElement = bpmnRefElements.get(type);
                        List<String> children = bpmnElement.getChildren();
                        if (children != null) {
                            for (String child : children) {
                                if (!typesCopy.contains(child)) {
                                    types.add(child);
                                    childfound = true;
                                }
                            }
                        }
                    }
                }
            } while (childfound);
            // validate if the referenced element has one of the correct types
            for (String type : types) {
                if (referencedElement.getName().equals(type)) {
                    validType = true;
                    break;
                }
            }

            if (!validType) {
                createAndAddReferenceTypeViolation(validationResult, line, column,
                        currentElement, checkingReference, referencedElement,
                        types);
            }
        }
    }

    /**
     * Creates and adds a found reference type violation to the list of
     * violations.
     *
     * @param validationResult
     *            the validationResult for adding the found violation
     * @param line
     *            the line of the reference in the root file
     * @param column
     *            the column of the reference
     * @param currentElement
     *            the element causing the violation
     * @param checkingReference
     *            the violated reference
     * @param referencedElement
     *            the actually referenced element
     * @param types
     *            a list of allowed reference types
     */
    private void createAndAddReferenceTypeViolation(ValidationResult validationResult, int line, int column,
            Element currentElement, Reference checkingReference, Element referencedElement, List<String> types) {

        String message = ViolationMessageCreator.createTypeViolationMessage(currentElement.getName(), line,
                checkingReference.getName(), referencedElement.getName(), types.toString());

        Location location = new Location(Paths.get(JDOMUtils.getUriFromElement(currentElement).replace("file:/","")),
                new LocationCoordinate(line, column));
        Violation violation = new Violation(location, message, CONSTRAINT_REF_TYPE);
        validationResult.addViolation(violation);
    }

    /**
     * Adds a found existence violation to the list.
     *
     * @param validationResult
     *            the validation result for adding the found violation
     * @param line
     *            the line of the reference in the root file
     * @param column
     *            the column of the reference in the root file
     * @param currentElement
     *            the element causing the violation
     * @param checkingReference
     *            the violated reference
     */
    public void createAndAddExistenceViolation(ValidationResult validationResult,
                                               int line, int column, Element currentElement, Reference checkingReference) {
        String message = ViolationMessageCreator
                .createExistenceViolationMessage(currentElement.getName(), checkingReference.getName(), line,
                        ViolationMessageCreator.DEFAULT_MSG, XPathHelper.getAbsolutePath(currentElement));
        Location location = new Location(Paths.get(JDOMUtils.getUriFromElement(currentElement).replace("file:/", "")),
                new LocationCoordinate(line, column));
        Violation violation = new Violation(location, message, CONSTRAINT_REF_EXISTENCE);
        validationResult.addViolation(violation);
    }
}