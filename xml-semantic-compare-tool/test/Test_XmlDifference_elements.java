import Model.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for differences in elements.
 * 
 * @author Svitana Robert
 * @author Pástor Lukáš
 * @version 05/6/2013
 */
public class Test_XmlDifference_elements {
    
    public Test_XmlDifference_elements() {
    }        
    
    @Test
    public void test_elements_missingElement() {
        
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml",
                                                      "test/Samples/contacts-element-missing.xml");  
        
        assertNotNull("Error creating XmlComparator.", comparison );
        assertFalse( comparison.compareXmlDocuments() );           
                            
        assertEquals("Incorrect number of differences :", 1, comparison.getDifferences().size());  
        
        XmlDifference difference = comparison.getDifferences().get(0);
        assertEquals("Expected element missing difference was not found.", Model.XmlDifferenceType.ELEMENT_MISSING_DIFFERENCE, difference.getType());                       
        assertEquals("Missing element should be named :", difference.getExpectedNode().getNodeName(), "xt:hobby");                                    
        assertEquals("Different name of the parent element :", "xt:hobbies", difference.getExpectedNode().getParentNode().getNodeName()); 
    }
        
    @Test
    public void test_elements_missingTwoElement() {
        
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml",
                                                      "test/Samples/contacts-element-missing-two.xml");  
         
        assertNotNull("Error creating XmlComparator.", comparison );
        assertFalse( comparison.compareXmlDocuments() );
                
        assertEquals("Incorrect number of differences :", 2, comparison.getDifferences().size());  
        
        XmlDifference difference;
        
        difference = comparison.getDifferences().get(0);
        assertEquals("Expected difference was not found :", Model.XmlDifferenceType.ELEMENT_MISSING_DIFFERENCE, difference.getType());                       
        assertEquals("Different name of the attribute :", "xt:hobby", difference.getExpectedNode().getNodeName());                             
        assertEquals("Different element name of the missing attribute :", "xt:hobbies", difference.getExpectedNode().getParentNode().getNodeName());
        
        difference = comparison.getDifferences().get(1);
        assertEquals("Expected difference was not found :", Model.XmlDifferenceType.ELEMENT_MISSING_DIFFERENCE, difference.getType());                       
        assertEquals("Different name of the attribute :", "xt:hobby", difference.getExpectedNode().getNodeName());                             
        assertEquals("Different element name of the missing attribute :", "xt:hobbies", difference.getExpectedNode().getParentNode().getNodeName());                
    }
    
    @Test
    public void test_elements_differentElementNames() {
        
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml",
                                                      "test/Samples/contacts-element-name-different.xml");  
         
        assertNotNull("Error creating XmlComparator.", comparison );
        assertFalse( comparison.compareXmlDocuments() );           
                
        assertEquals("Incorrect number of differences :", 1, comparison.getDifferences().size());
        
        XmlDifference difference;
        
        difference = comparison.getDifferences().get(0);
        assertEquals("Expected difference was not found :", Model.XmlDifferenceType.TAGNAME_DIFFERENCE, difference.getType());
        assertEquals("Different name of the element :", "xt:e-mails", difference.getFoundNode().getNodeName());
    }
    
    @Test
    public void test_elements_emptyElement() {
        
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml",
                                                      "test/Samples/contacts-element-empty.xml");  
         
        assertNotNull("Error creating XmlComparator.", comparison );
        assertTrue( comparison.compareXmlDocuments() );                                                    
    }   
    
}