import Model.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Attr;

/**
 * Tests for differences of attributes.
 * 
 * @author Svitana Robert
 * @author Pástor Lukáš
 * @version 05/6/2013
 */
public class Test_XmlDifference_attributes {
    
    public Test_XmlDifference_attributes() {
    }
  
    @Test
    public void test_attributes_missingTwoAttributes() {                               
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml",
                                                      "test/Samples/contacts-attribute-more.xml");        
                         
        assertNotNull("Error creating XmlComparator.", comparison );
                              
        assertFalse(comparison.compareXmlDocuments());                    
        assertEquals("Incorrect number of differences :", 2, comparison.getDifferences().size());                   
        
        XmlDifference difference;
                
        difference = comparison.getDifferences().get(0);
        assertEquals("Expected difference was not found :", Model.XmlDifferenceType.ATTRIBUTE_MISSING_DIFFERENCE, difference.getType());                       
        assertEquals("Different name of the attribute :", "priority", difference.getExpectedNode().getNodeName());                             
        assertEquals("Different element name of the missing attribute :", "xt:email",((Attr) difference.getExpectedNode()).getOwnerElement().getNodeName());
        
        difference = comparison.getDifferences().get(1);
        assertEquals("Expected difference was not found :", Model.XmlDifferenceType.ATTRIBUTE_MISSING_DIFFERENCE, difference.getType());
        assertEquals("Different name of the attribute :", "priority", difference.getExpectedNode().getNodeName());
        assertEquals("Different name of the element :", "xt:email", ((Attr) difference.getExpectedNode()).getOwnerElement().getNodeName());
    }
     
    @Test
    public void test_attributes_differentAttributeOrder() {
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml",
                                                      "test/Samples/contacts-attribute-order.xml" );
                
        assertNotNull("Error creating XmlComparator.", comparison );
                                         
        assertTrue(comparison.compareXmlDocuments());
        assertTrue("Found some differences although, none were expected.", comparison.getDifferences().isEmpty() );                                              
    }
     
    @Test
    public void test_attributes_differentAttributeValue() {
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml",
                                                      "test/Samples/contacts-attribute-value.xml" );
        
        assertNotNull("Error creating XmlComparator.", comparison );
        
        // Check attribute values is off
        assertTrue(comparison.compareXmlDocuments(false, false, false));        
        assertEquals("Incorrect number of differences :", 0, comparison.getDifferences().size());                  
        
        // Check attribute values is on
        assertFalse(comparison.compareXmlDocuments(false, false, true));
        assertEquals("Incorrect number of differences :", 1, comparison.getDifferences().size()); 
        
        XmlDifference difference = comparison.getDifferences().get(0);              
        assertEquals("Expected difference was not found :", Model.XmlDifferenceType.ATTRIBUTE_VALUE_DIFFERENCE, difference.getType());
        assertEquals("Different name of the attribute :", "id", difference.getExpectedNode().getNodeName());
        assertEquals("Different name of the element :", "xt:contact", ((Attr) difference.getExpectedNode()).getOwnerElement().getNodeName());                                                            
    }            
}