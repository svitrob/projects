package Model;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Provides the primary datatype class for the entire XmlSemanticComapreTool.
 * Class represents a single comparison of two XML documents as well as all
 * necessary properties required for generating adequate conclusions about XML
 * documents. Instances are reusable in terms of comparing the same two
 * documents with different settings.
 *
 * @author Pástor Lukáš
 * @version 05/6/2013
 */
public class XmlComparison implements XmlComparator {

    private Document documentA;
    private Document documentB;
    private boolean trimText;
    private boolean strictElementOrder;
    private boolean checkAttributeValues;
    private List<XmlDifference> differences;

    /**
     * Constructor for class XmlComparison. Performs initialization process of
     * all fields to new or default values.
     *
     * @param documentA First document required for comparison
     * @param documentB Second document required for comparison
     * @throws IllegalArgumentException
     */
    public XmlComparison(Document documentA, Document documentB) throws IllegalArgumentException {

        if (null == documentA) {
            throw new IllegalArgumentException("XmlComparison: Document A is null.");
        }
        if (null == documentB) {
            throw new IllegalArgumentException("XmlComparison: Document B is null.");
        }

        this.documentA = documentA;
        this.documentB = documentB;
        this.trimText = false;
        this.strictElementOrder = true;
        this.checkAttributeValues = false;
        this.differences = new ArrayList();
    }

    /**
     * Constructor for class XmlComparison. Performs creating of necessary
     * Document objects as well as initialization process of all fields to new
     * or default values.
     *
     * @param URIdocumentA First document URI required for comparison
     * @param URIdocumentB Second document URI required for comparison
     */
    public XmlComparison(String URIdocumentA, String URIdocumentB) {
        this(createDocumentFromURI(URIdocumentA), createDocumentFromURI(URIdocumentB));
    }

    /**
     * Returns first Document associated with comparison.
     *
     * @return Document First compared document.
     */
    public Document getFirstDocument() {
        return this.documentA;
    }

    /**
     * Returns second Document associated with comparison.
     *
     * @return Document Second compared document.
     */
    public Document getSecondDocument() {
        return this.documentB;
    }

    /**
     * Returns true if text in text elements should be trimmed, false otherwise.
     *
     * @return true if text trimming is on.
     */
    public boolean trimText() {
        return this.trimText;
    }

    /**
     * Returns true if order of elements is the considered a difference.
     *
     * @return true if text trimming is on.
     */
    public boolean strictElementOrder() {
        return this.strictElementOrder;
    }

    /**
     * Returns true if inequality of attribute values are considered a
     * difference.
     *
     * @return true if attribute values comparing is on.
     */
    public boolean checkAttributeValues() {
        return this.checkAttributeValues;
    }

    /**
     * Adds a new difference in differenceList associated with this comparison.
     *
     * @param newDifference new difference to be added.
     * @throws IllegalArgumentException
     */
    public void addDifference(XmlDifference newDifference) throws IllegalArgumentException {
        if (null == newDifference) {
            throw new IllegalArgumentException("AddDifference: New difference is null.");
        }
        this.differences.add(newDifference);
    }

    /**
     * Returns list of all differences collected from the last comparison.
     *
     * @return
     */
    public List<XmlDifference> getDifferences() {
        return Collections.unmodifiableList(differences);
    }

    /**
     * Prints all differences found on standard output stream.
     */
    public void printDifferences() {

        List<XmlDifference> differenceList = getDifferences();

        if (0 != differenceList.size()) {
            for (XmlDifference d : differenceList) {
                System.out.println(d.toString());
            }
        } else {
            System.out.println("No differences found.");
        }
    }

    /**
     * Returns Document object created from specified URI.
     *
     * @param documentURI URI to create the document.
     * @return New document from string.
     * @throws IllegalArgumentException
     */
    public static Document createDocumentFromURI(String documentURI) throws IllegalArgumentException {

        Document document = null;

        if (null == documentURI) {
            throw new IllegalArgumentException("CreateDocument: Document URI is null.");
        }

        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true); // needed in order for namespace functions to work (getLocalName, getPrefix, ..)
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            document = builder.parse(new FileInputStream(documentURI));

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return document;
    }

    /**
     * Returns Document object created from specified string.
     *
     * @param documentSource string to create the document.
     * @return New document from string.
     * @throws IllegalArgumentException
     */
    public static Document createDocumentFormString(String documentSource) throws IllegalArgumentException {

        Document document = null;

        if (null == documentSource) {
            throw new IllegalArgumentException("CreateDocumentFormString: Document string source is null.");
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true); // needed in order for namespace functions to work (getLocalName, getPrefix, ..)
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream bis = new ByteArrayInputStream(documentSource.getBytes());

            document = builder.parse(bis);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return document;
    }

    @Override
    public boolean compareXmlDocuments(boolean trimText, boolean strictElementOrder, boolean checkAttributeValues) {
        this.trimText = trimText;
        this.strictElementOrder = strictElementOrder;
        this.checkAttributeValues = checkAttributeValues;

        if (false == equalsDocumentVersion(documentA, documentB)) {
            addDifference(new XmlDifference(XmlDifferenceType.VERSION_DIFFERENCE, null, null));
        }

        if (false == equalsDocumentEncoding(documentA, documentB)) {
            addDifference(new XmlDifference(XmlDifferenceType.ENCODING_DIFFERENCE, null, null));
        }

        equalsElements(getFirstDocument().getDocumentElement(), getSecondDocument().getDocumentElement(), true);
        return (0 == getDifferences().size());
    }

    @Override
    public boolean compareXmlDocuments() {
        return this.compareXmlDocuments(false, true, false);
    }

    @Override
    public boolean equalsDocumentVersion(Document documentA, Document documentB) throws IllegalArgumentException {

        if (null == documentA) {
            throw new IllegalArgumentException("EqualsDocumentVersion: Document A is null.");
        }

        if (null == documentB) {
            throw new IllegalArgumentException("EqualsDocumentVersion: Document B is null.");
        }

        return documentA.getXmlVersion().equals(documentB.getXmlVersion());
    }

    @Override
    public boolean equalsDocumentEncoding(Document documentA, Document documentB) throws IllegalArgumentException {

        if (null == documentA) {
            throw new IllegalArgumentException("EqualsDocumentVersion: Document A is null.");
        }

        if (null == documentB) {
            throw new IllegalArgumentException("EqualsDocumentVersion: Document B is null.");
        }

        return documentA.getXmlEncoding().equals(documentB.getXmlEncoding());
    }

    @Override
    public boolean equalsElements(Element elementA, Element elementB, boolean applyRecursion) throws IllegalArgumentException {

        if (null == elementA) {
            throw new IllegalArgumentException("EqualsElement: Element A is null.");
        }

        if (null == elementB) {
            throw new IllegalArgumentException("EqualsElement: Element B is null.");
        }

        boolean elementsEquality = equalsElementNamespace(elementA, elementB); // check the namespace location first, if false, bail the rest
        if (true == elementsEquality) {

            elementsEquality = equalsElementName(elementA, elementB);
            if (true == elementsEquality) {

                elementsEquality = equalsElementAttributes(elementA, elementB);
                if (true == elementsEquality) {

                    if (true == isTextElement(elementA)
                            && true == isTextElement(elementB)) {

                        elementsEquality = equalsElementText(elementA, elementB);
                    } else {
                        if (true == applyRecursion) {

                            Node currentNodeA = null;
                            Node currentNodeB = null;
                            NodeList childrenListA = elementA.getChildNodes();
                            NodeList childrenListB = elementB.getChildNodes();

                            int i = 0;
                            while (true) {

                                currentNodeA = childrenListA.item(i);
                                currentNodeB = childrenListB.item(i);

                                if (null == currentNodeA
                                        || null == currentNodeB) {
                                    break;
                                }

                                if (currentNodeA.getNodeType() == Node.ELEMENT_NODE) {
                                    if (currentNodeB.getNodeType() == Node.ELEMENT_NODE) {
                                        elementsEquality = equalsElements((Element) currentNodeA, (Element) currentNodeB, applyRecursion);
                                    } else {
                                        addDifference(new XmlDifference(XmlDifferenceType.ELEMENT_MISSING_DIFFERENCE, currentNodeA, null));
                                    }
                                } else {
                                    if (currentNodeB.getNodeType() == Node.ELEMENT_NODE) {
                                        addDifference(new XmlDifference(XmlDifferenceType.ELEMENT_MISSING_DIFFERENCE, currentNodeB, null));
                                    }
                                }
                                i++;
                            }

                            while (true) {
                                if (null == currentNodeA
                                        && null == currentNodeB) {
                                    break;
                                }

                                if (null == currentNodeA && null != currentNodeB) {
                                    if (currentNodeB.getNodeType() == Node.ELEMENT_NODE) {
                                        addDifference(new XmlDifference(XmlDifferenceType.ELEMENT_MISSING_DIFFERENCE, currentNodeB, null));
                                    }
                                } else {
                                    if (currentNodeA.getNodeType() == Node.ELEMENT_NODE) {
                                        addDifference(new XmlDifference(XmlDifferenceType.ELEMENT_MISSING_DIFFERENCE, currentNodeA, null));
                                    }
                                }

                                i++;
                                currentNodeA = childrenListA.item(i);
                                currentNodeB = childrenListB.item(i);
                            }
                        }
                    }
                }
            }
        }
        return elementsEquality;
    }

    @Override
    public boolean equalsElementNamespace(Element elementA, Element elementB) throws IllegalArgumentException {

        if (null == elementA) {
            throw new IllegalArgumentException("EqualsElementName: elementA is null.");
        }
        if (null == elementB) {
            throw new IllegalArgumentException("EqualsElementName: elementB is null.");
        }
        
        boolean elementNamespaceEquals = elementA.getNamespaceURI().equals(elementB.getNamespaceURI());
        
        if (false == elementNamespaceEquals) {
            addDifference(new XmlDifference(XmlDifferenceType.NAMESPACE_DIFFERENCE, elementA, elementB));
        }

        return elementNamespaceEquals;
    }

    @Override
    public boolean equalsElementName(Element elementA, Element elementB) throws IllegalArgumentException {

        if (null == elementA) {
            throw new IllegalArgumentException("EqualsElementName: elementA is null.");
        }
        if (null == elementB) {
            throw new IllegalArgumentException("EqualsElementName: elementB is null.");
        }

        boolean elementNameEquals = elementA.getLocalName().equals(elementB.getLocalName());

        if (false == elementNameEquals) {
            addDifference(new XmlDifference(XmlDifferenceType.TAGNAME_DIFFERENCE, elementA, elementB));
        }

        return elementNameEquals;
    }

    @Override
    public boolean equalsElementAttributes(Element elementA, Element elementB) throws IllegalArgumentException, NullPointerException {

        boolean equalsAttributesAtoB = true;
        boolean equalsAttributesBtoA = true;

        if (null == elementA) {
            throw new IllegalArgumentException("EqualsElementAttributes: Element A is null.");
        }

        if (null == elementB) {
            throw new IllegalArgumentException("EqualsElementAttributes: Element B is null.");
        }

        NamedNodeMap attributesA = elementA.getAttributes();
        NamedNodeMap attributesB = elementB.getAttributes();

        if (null == attributesA) {
            throw new NullPointerException("EqualsElementAttributes: Attributes of element A is null.");
        }

        if (null == attributesB) {
            throw new NullPointerException("EqualsElementAttributes: Attributes of element B is null.");
        }

        Attr currentAttributeA;
        Attr currentAttributeB;

        for (int i = 0, len = attributesA.getLength(); i < len; i++) {

            currentAttributeA = (Attr) attributesA.item(i);
            if (true == currentAttributeA.getName().startsWith("xmlns")) {
                continue;
            }

            currentAttributeB = (null == currentAttributeA.getNamespaceURI())
                    ? (Attr) attributesB.getNamedItem(currentAttributeA.getName())
                    : (Attr) attributesB.getNamedItemNS(currentAttributeA.getNamespaceURI(), currentAttributeA.getLocalName());

            if (null == currentAttributeB) {
                addDifference(new XmlDifference(XmlDifferenceType.ATTRIBUTE_MISSING_DIFFERENCE, (Node) currentAttributeA, null));
                equalsAttributesAtoB = false;
            } else {
                if (true == checkAttributeValues()) {
                    if (false == currentAttributeA.getValue().equals(currentAttributeB.getValue())) {
                        addDifference(new XmlDifference(XmlDifferenceType.ATTRIBUTE_VALUE_DIFFERENCE, (Node) currentAttributeA, currentAttributeB));
                        equalsAttributesAtoB = false;
                    }
                }
            }
        }

        for (int i = 0, len = attributesB.getLength(); i < len; i++) {

            currentAttributeB = (Attr) attributesB.item(i);
            if (true == currentAttributeB.getName().startsWith("xmlns")) {
                continue;
            }

            currentAttributeA = (null == currentAttributeB.getNamespaceURI())
                    ? (Attr) attributesA.getNamedItem(currentAttributeB.getName())
                    : (Attr) attributesA.getNamedItemNS(currentAttributeB.getNamespaceURI(), currentAttributeB.getLocalName());

            if (null == currentAttributeA) {
                addDifference(new XmlDifference(XmlDifferenceType.ATTRIBUTE_MISSING_DIFFERENCE, (Node) currentAttributeB, null));
                equalsAttributesBtoA = false;
            }
        }

        return equalsAttributesAtoB && equalsAttributesBtoA;
    }

    @Override
    public boolean equalsElementText(Element elementA, Element elementB) throws IllegalArgumentException {

        if (null == elementA) {
            throw new IllegalArgumentException("EqualsElementText: ElementA is null..");
        }

        if (null == elementB) {
            throw new IllegalArgumentException("EqualsElementText: ElementB is null.");
        }

        if (false == isTextElement(elementA)) {
            throw new IllegalArgumentException("EqualsElementText: ElementA is not a text node.");
        }

        if (false == isTextElement(elementB)) {
            throw new IllegalArgumentException("EqualsElementText: ElementB is not a text node.");
        }

        String textA = elementA.getFirstChild().getNodeValue();
        String textB = elementB.getFirstChild().getNodeValue();

        if (true == trimText()) {
            textA = textA.trim();
            textB = textB.trim();
        }

        boolean isTextEqual = textA.equals(textB);

        if (false == isTextEqual) {
            addDifference(new XmlDifference(XmlDifferenceType.TEXT_DIFFERENCE, (Node) elementA, (Node) elementB));
        }

        return isTextEqual;
    }

    @Override
    public boolean isTextElement(Element element) throws IllegalArgumentException {

        if (null == element) {
            throw new IllegalArgumentException("IsTextElement: Element is null.");
        }

        NodeList childList = element.getChildNodes();

        return (1 == childList.getLength()) && (childList.item(0).getNodeType() == Node.TEXT_NODE);
    }
}
