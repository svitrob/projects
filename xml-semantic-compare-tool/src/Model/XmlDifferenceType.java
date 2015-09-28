package Model;

/**
 * Represents types of differences witch can occur while comparing XML documents.
 *
 * @author Pástor Lukáš
 * @version 05/6/2013
 */
public enum XmlDifferenceType {
    /**
     * If XML documents version is not the same.
     */
    VERSION_DIFFERENCE,
    
    /**
     * If XML documents encoding is not the same.
     */
    ENCODING_DIFFERENCE,
    
    /**
     * If documents or elements points to a different namespace
     */
    NAMESPACE_DIFFERENCE,
    
    /**
     * If name of two XML elements is not the same.
     */
    TAGNAME_DIFFERENCE,
    
    /**
     * If attributes of two XML elements are not the same.
     */
    ATTRIBUTE_MISSING_DIFFERENCE,
    
    /**
     * If attribute value of two XML elements is not the same.
     */
    ATTRIBUTE_VALUE_DIFFERENCE,
    
    /**
     * If element in one XML document was not found in the other.
     */
    ELEMENT_MISSING_DIFFERENCE,
    
    /**
     * If two text elements has different text.
     */
    TEXT_DIFFERENCE        
}
