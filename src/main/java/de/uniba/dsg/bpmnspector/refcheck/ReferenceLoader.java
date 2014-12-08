package de.uniba.dsg.bpmnspector.refcheck;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This class loads the references form the references.xml file. The XML file
 * needs to fit the references.xsd schema. The references will be structured by
 * the classes {@link BPMNElement} and {@link Reference}.
 * 
 * @author Andreas Vorndran
 * @version 1.0
 * @see BPMNElement
 * @see Reference
 * 
 */
public class ReferenceLoader {

	private final List<SAXParseException> XSDErrorList;
	private final Properties language;

	/**
	 * Constructor
	 * 
	 * @param language
	 *            the reference to the language properties
	 */
	public ReferenceLoader(Properties language) {
		this.language = language;
		XSDErrorList = new ArrayList<>();
	}

	/**
	 * This method loads the XML file and gives the data structure of
	 * BPMNElements and References back.
	 * 
	 * @param referencesPath
	 *            path to the XML file with the references
	 * @param XSDPath
	 *            path to the XSD file for the references
	 * @return a hash map with the element names as keys and as value the
	 *         BPMNElements which contain the references
	 * @throws ValidatorException
	 *             if problems occurred while loading, traversing or checking
	 *             the 'references.xml' file against its XSD
	 */
	public Map<String, BPMNElement> load(String referencesPath,
			String XSDPath) throws ValidatorException {
		try (InputStream refPathStream = getClass().getResourceAsStream(referencesPath)) {

			validateReferencesFile(referencesPath, XSDPath);

			Document document = new SAXBuilder().build(refPathStream);
			Element root = document.getRootElement();

			return createReferences(root);

		} catch (JDOMException e) {
			throw new ValidatorException(language.getProperty("loader.jdom"), e);
		} catch (IOException e) {
			throw new ValidatorException(language.getProperty("loader.io"), e);
		}


	}

	private Map<String, BPMNElement> createReferences(Element rootElem) {
		Map<String, BPMNElement> bpmnElements = new HashMap<>();
		// separates all BPMN elements
		List<Element> elements = rootElem.getChildren();
		for (Element element : elements) {
            String elementName = element.getAttributeValue("name");
            String parent = element.getChildText("parent");
            List<String> children = null;
            // separates possible child elements (element names of the
            // element, which inherits the references from the current
            // element)
            List<Element> childrenInFile = element.getChildren("child");
            if (!childrenInFile.isEmpty()) {
                children = new ArrayList<>();
                for (Element child : childrenInFile) {
                    children.add(child.getText());
                }
            }
            // separates the references for the current element
            List<Reference> references = new ArrayList<>();
            List<Element> referencesInFile = element
					.getChildren("reference");
            for (Element reference : referencesInFile) {
                int number = Integer.parseInt(reference
.getAttributeValue("number"));
                String referenceName = reference.getChild("name").getText();
                ArrayList<String> types = null;
                List<Element> typesInFile = reference.getChildren("type");
                if (!typesInFile.isEmpty()) {
                    types = new ArrayList<>();
                    for (Element type : typesInFile) {
                        types.add(type.getText());
                    }
                }
                boolean qname = convertToBoolean(reference
                        .getAttributeValue("qname"));
                boolean attribute = convertToBoolean(reference
                        .getAttributeValue("attribute"));
                boolean special = false;
                String specialAttribute = reference
                        .getAttributeValue("special");
                if (specialAttribute != null) {
                    special = convertToBoolean(specialAttribute);
                }
                Reference bpmnReference = new Reference(number,
                        referenceName, types, qname, attribute, special,
                        language);
                references.add(bpmnReference);
            }
            BPMNElement bpmnElement = new BPMNElement(elementName, parent,
                    children, references, language);
            bpmnElements.put(elementName, bpmnElement);
        }

		// add not yet existing key references of the element children
		Map<String, BPMNElement> missingMap = new HashMap<>();

		for(Map.Entry<String, BPMNElement> entry : bpmnElements.entrySet()) {
			if(entry.getValue().getChildren() != null) {
				for (String childName : entry.getValue().getChildren()) {
					if (!bpmnElements.containsKey(childName)) {
						missingMap.put(childName, entry.getValue());
					}
				}
			}
		}

		bpmnElements.putAll(missingMap);

		return bpmnElements;
	}

	private void validateReferencesFile(String referencesPath, String XSDPath)
			throws ValidatorException, IOException {
		try (InputStream refPathStream = getClass().getResourceAsStream(referencesPath);
				InputStream xsdPathStream = getClass().getResourceAsStream(XSDPath)) {
			SchemaFactory schemaFactory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory
					.newSchema(new StreamSource(xsdPathStream));
			Validator validator = schema.newValidator();
			validator.setErrorHandler(new XSDValidationLoggingErrorHandler());
			validator.validate(new StreamSource(refPathStream));
			if (!XSDErrorList.isEmpty()) {
				StringBuilder xsdErrorText = new StringBuilder(200)
						.append(language.getProperty("loader.xsd.general"))
						.append(System.lineSeparator());
				for (SAXParseException saxParseException : XSDErrorList) {
					xsdErrorText.append(language
							.getProperty("loader.xsd.error.part1"))
							.append(saxParseException.getLineNumber())
							.append(' ')
							.append(language
									.getProperty("loader.xsd.error.part2"))
							.append(saxParseException.getMessage())
							.append(System.lineSeparator());
				}
				throw new ValidatorException(xsdErrorText.toString());
			}
		} catch (SAXException e) {
			throw new ValidatorException(language.getProperty("loader.sax"), e);
		}

	}

	private boolean convertToBoolean(String string) {
        return "true".equals(string);
	}

	/**
	 * Inner class for getting the XSD violations.
	 * 
	 * @author Andreas Vorndran
	 * 
	 */
	private class XSDValidationLoggingErrorHandler implements ErrorHandler {

		@Override
		public void error(SAXParseException exception) throws SAXException {
			XSDErrorList.add(exception);
		}

		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			XSDErrorList.add(exception);
		}

		@Override
		public void warning(SAXParseException exception) throws SAXException {
			XSDErrorList.add(exception);
		}

	}
}