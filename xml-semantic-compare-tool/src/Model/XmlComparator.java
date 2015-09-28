package Model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Interface provides methods for main functionality of XmlSemanticCompareTool as well as 
 * settings for influencing the behavior of methods performing comparison.
 * 
 * @author Pástor Lukáš
 * @version 05/6/2013
 */
public interface XmlComparator {
      
    /**
     * Provides the class implementation of XmlComparator interface and initiate
     * process of XML comparing and generating XML differences with default
     * settings.
     *
     * @return True if no differences were found, false otherwise.
     */
    public boolean compareXmlDocuments();
    
    /**
     * Provides the class implementation of XmlComparator interface and initiate
     * process of XML comparing and generating XML differences with custom
     * settings.
     *
     * @param trimText Whether leading and trailing whites space character in
     * text elements should removed
     * @param strictElementOrder Whether element ordering is strict or not
     * important
     * @param checkAttributeValues Whether the attribute is a of type ID.
     * @return True if no differences were found, false otherwise.
     */
    public boolean compareXmlDocuments(boolean checkWhitespace, boolean checkElementOrder, boolean checkAttributeValues);      
    
    /**
     * Compares the version of two XML documents. In case of version inequality
     * new XML difference is generated to comparison difference list.
     *
     * @param documentA first document for version check.
     * @param documentB second document for version check.
     * @return True if XML documents have the same version.
     * @throws IllegalArgumentException  
     */
    public boolean equalsDocumentVersion(Document documentA, Document documentB) throws IllegalArgumentException;
    
    /**
     * Compares the encoding of two XML documents. In case of encoding
     * inequality new XML difference is generated to comparison difference list.
     *
     * @param documentA first document for encoding check.
     * @param documentB second document for encoding check.
     * @return True if two XML documents have the same encoding.
     * @throws IllegalArgumentException  
     */
    public boolean equalsDocumentEncoding(Document documentA, Document documentB);
    
    /**
     * Compares two XML elements. Returns true if following methods returns true
     * : equalsElementName(); equalsElementAttributes(); (according to
     * checkAttributeValues) equalsElementText(); (if true == isTextElement())
     *
     * @param elementA First element to be compared.
     * @param elementB Second element to be compared.
     * @param applyRecursion Whether apply recursive calls on child elements
     * @return True if two XML elements are equal, false otherwise.
     * @throws IllegalArgumentException  
     */
    public boolean equalsElements(Element elementA, Element elementB, boolean applyRecursion);
    
    /**
     * Compares name space of two XML elements.
     *
     * @param elementA first element for comparison.
     * @param elementB second element for comparison.
     * @return True if two elements have name space.
     * @throws IllegalArgumentException  
     */
    public boolean equalsElementNamespace(Element elementA, Element elementB) throws IllegalArgumentException;
    
    /**
     * Compares two tag name of two XML elements.
     *
     * @param elementA first element for comparison.
     * @param elementB second element for comparison.
     * @return True if two elements have name equal.
     * @throws IllegalArgumentException  
     */
    public boolean equalsElementName(Element elementA, Element elementB);
    
    /**
     * Compares attributes of two XML elements. If checkAttributeValues is true
     * attribute values inequality is considered XML difference.
     *
     * @param elementA first element for comparison.
     * @param elementB second element for comparison.
     * @return True if two elements have all attributes equal.
     * @throws IllegalArgumentException 
     * @throws NullPointerException  
     */
    public boolean equalsElementAttributes(Element elementA, Element elementB);
    
    /**
     * Compare text of two text elements. If any element is not a text element
     * or null an exception is thrown. If checkWhitespace flag is on the leading
     * and trailing white space characters are trimmed. Otherwise text is
     * compared as is.
     *
     * @param elementA first text element
     * @param elementB second text element   
     * @return True if element content is equal, false otherwise
     * @throws IllegalArgumentException  
     */
    public boolean equalsElementText(Element elementA, Element elementB);
    
    /**
     * Checks if element is a text element i.e. has a single child whose type is
     * text node.
     *
     * @param element element to be checked.
     * @return True if element is a text element, false otherwise.
     * @throws IllegalArgumentException  
     */
    public boolean isTextElement(Element element) throws IllegalArgumentException;    
}
