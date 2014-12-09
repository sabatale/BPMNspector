package de.uniba.dsg.bpmnspector.refcheck.utils;

import de.uniba.dsg.bpmnspector.common.importer.BPMNProcess;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filter;
import org.jdom2.filter.Filters;
import org.jdom2.util.IteratorIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.*;

public class JDOMUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JDOMUtils.class.getSimpleName());

    /**
     * This method puts all elements with an id found in the given document into
     * a hash map. The key is the id, the value the element.
     *
     * @param document
     *            the document to get the elements from
     * @return the hash map with elements reachable through their id
     */
    public static Map<String, Element> getAllElements(Document document) {
        Map<String, Element> elements = new HashMap<>();
        Element rootNode = document.getRootElement();
        Filter<Element> filter = Filters.element();
        IteratorIterable<Element> list = rootNode.getDescendants(filter);

        while (list.hasNext()) {
            Element element = list.next();
            Attribute id = element.getAttribute("id");
            // put the element if it has an id
            if (id != null) {
                String id_value = id.getValue();
                if (id_value != null && !id_value.equals("")) {
                    elements.put(id_value, element);
                }
            }
        }

        return elements;
    }

    /**
     * Map which uses the namespace-URI as an ID and another
     * Map as value. The inner HashMap contains all Elements accessible via
     * the ID as key {@see getAllElements()}
     *
     * @param elementsMap
     *              the Map to be populated
     * @param process
     *              the BPMNProcess to be added
     * @param alreadyVisited
     *              a List of filenames of already added files
     */
    public static void getAllElementsGroupedByNamespace(
            Map<String, Map<String, Element>> elementsMap,
            BPMNProcess process, List<String> alreadyVisited) {
        if(!alreadyVisited.contains(process.getBaseURI())) {
             addElementsFromSingleDocument(elementsMap,
                    process.getProcessAsDoc());
            alreadyVisited.add(process.getBaseURI());
            for (BPMNProcess child : process.getChildren()) {
                getAllElementsGroupedByNamespace(elementsMap, child, alreadyVisited);
            }
        }
    }

    private static void addElementsFromSingleDocument(
            Map<String, Map<String, Element>> groupedElements, Document doc) {
        String targetNamespace = doc.getRootElement().getAttributeValue(
                "targetNamespace");
        Map<String, Element> docElements = getAllElements(doc);

        if (groupedElements.containsKey(targetNamespace)) {
            Map<String, Element> previousElems = groupedElements
                    .get(targetNamespace);
            previousElems.putAll(docElements);
        } else {
            groupedElements.put(targetNamespace, docElements);
        }
    }

    /**
     * Gets the baseURI String from the given element
     * (i.e., determines the location of the file containing the element)
     *
     * @param element
     *            the element
     * @return the URI string - null if the base URI cannot be restored
     */
    public static String getUriFromElement(Element element) {
        try {
            return element.getXMLBaseURI().toString();

        } catch (URISyntaxException e) {
            LOGGER.error("Base URI of current element " + element.getName()
                    + " could not be restored.");
            return null;
        }
    }
}
