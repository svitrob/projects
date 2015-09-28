package Model;

import java.util.Objects;
import org.w3c.dom.Node;

/**
 * Provides the datatype class for single XML difference as well as all 
 * necessary properties required to specify location of XML elements where the 
 * difference has occured. 
 * 
 * @author Pástor Lukáš
 * @version 05/6/2013
 */
public class XmlDifference {

    private Node expectedNode;
    private Node foundNode;
    private XmlDifferenceType type;
    
    /**
     * Constructor for class XmlDifference. 
     *
     * @param type Type of XmlDifference (see XmlDifferenceType).
     * @param expectedNode Node to which foundNode is compared required for comparison.
     * @param foundNode Node in which difference has occured.
     * @throws IllegalArgumentException  
     */
    public XmlDifference(XmlDifferenceType type, Node expectedNode, Node foundNode) throws IllegalArgumentException {
        if ( null == type ) {
            throw new IllegalArgumentException("XmlDifference: Difference type is null.");
        }        
        this.expectedNode = expectedNode;
        this.foundNode = foundNode;
        this.type = type;        
    }

    /**
     * Returns node witch was expected in compared XML document. 
     *
     * @return Expected pattern node of difference.     
     */
    public Node getExpectedNode() {
        return expectedNode;
    }

    /**
     * Returns node in witch the difference has been found.
     *
     * @return Node with difference.
     */
    public Node getFoundNode() {
        return foundNode;
    }

    /**
     * Returns type of difference.
     *
     * @return Type of difference.
     */
    public XmlDifferenceType getType() {
        return type;
    }

    /**
     * Returns specific message describing type of difference.
     * 
     * @return 
     */
    @Override
    public String toString() {

        String differenceMessage;

        switch (getType()) {
            case ATTRIBUTE_MISSING_DIFFERENCE:
                differenceMessage = "ATTRIBUTE_MISSING_DIFFERENCE" + getExpectedNode().getNodeName();
                break;
            case ATTRIBUTE_VALUE_DIFFERENCE:
                differenceMessage = "ATTRIBUTE_VALUE_DIFFERENCE" + getExpectedNode().getNodeName();
                break;
            case TAGNAME_DIFFERENCE:
                differenceMessage = "TAGNAME_DIFFERENCE " + getExpectedNode().getNodeName();
                break;
            case ELEMENT_MISSING_DIFFERENCE:
                differenceMessage = "Element '" + getExpectedNode().getNodeName() + "' is missing.";                                   
                break;
            case VERSION_DIFFERENCE:
                differenceMessage = "Documents have different version.";
                break;
            case ENCODING_DIFFERENCE:
                differenceMessage = "Documents have different encoding.";
                break;
            case TEXT_DIFFERENCE:
                differenceMessage = "Element '" + getExpectedNode().getNodeName() + "has different text than '" + getFoundNode().getNodeName();
                break;
            case NAMESPACE_DIFFERENCE:
                differenceMessage = "Element " + getExpectedNode().getNodeName() + "has different namespace than" + getFoundNode().getNodeName();
            default:
                differenceMessage = "UNKNOWN DIFFERENCE";
                break;
        }
        return differenceMessage;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.expectedNode);
        hash = 83 * hash + Objects.hashCode(this.foundNode);
        hash = 83 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final XmlDifference other = (XmlDifference) obj;
        if (!Objects.equals(this.expectedNode, other.expectedNode)) {
            return false;
        }
        if (!Objects.equals(this.foundNode, other.foundNode)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        return true;
    }        
}