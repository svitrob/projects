import Model.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for differences in text elements of XML documents.
 * 
 * @author Svitana Robert
 * @author Pástor Lukáš
 * @version 05/6/2013
 */
public class Test_XmlDifference_text {
    
    public Test_XmlDifference_text() {
    }
            
    @Test
    public void test_text_differentText() {        
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml",
                                                      "test/Samples/contacts-text-different.xml");  
        
        assertNotNull("Error creating XmlComparator.", comparison );
        assertFalse( comparison.compareXmlDocuments( false, false , false ) );           
                
        assertEquals("Incorrect number of differences :", 2, comparison.getDifferences().size());
  
        XmlDifference difference;
        
        difference = comparison.getDifferences().get(0);
        assertEquals("Expected difference was not found :", Model.XmlDifferenceType.TEXT_DIFFERENCE , difference.getType());                       
        assertEquals("Different text should be in element :", "xt:email", difference.getExpectedNode().getNodeName());                             
        assertEquals("Different text should be child of :", "xt:emails", difference.getExpectedNode().getParentNode().getNodeName());
        
        difference = comparison.getDifferences().get(1);
        assertEquals("Expected difference was not found :", Model.XmlDifferenceType.TEXT_DIFFERENCE , difference.getType());                       
        assertEquals("Different text should be in element :", "xt:email", difference.getExpectedNode().getNodeName());                             
        assertEquals("Different text should be child of :", "xt:emails", difference.getExpectedNode().getParentNode().getNodeName());                                           
    }
    
    @Test
    public void test_text_whitespacesDifferences() {        
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml",
                                                      "test/Samples/contacts-text-whitespaces.xml");  
        
        assertNotNull("Error creating XmlComparator.", comparison );
        assertTrue( comparison.compareXmlDocuments( true, true , false ) );                          
        assertEquals("Incorrect number of differences :", 0, comparison.getDifferences().size());
   
        
        assertFalse( comparison.compareXmlDocuments( false, true , false ) );  
        assertEquals("Incorrect number of differences :", 3, comparison.getDifferences().size());
        XmlDifference difference;
        
        difference = comparison.getDifferences().get(0);
        assertEquals("Expected difference was not found :", Model.XmlDifferenceType.TEXT_DIFFERENCE , difference.getType());                       
        assertEquals("Different text should be in element :", "xt:hobby", difference.getExpectedNode().getNodeName());                             
        assertEquals("Different text should be child of :", "xt:hobbies", difference.getExpectedNode().getParentNode().getNodeName());
        
        difference = comparison.getDifferences().get(1);
        assertEquals("Expected difference was not found :", Model.XmlDifferenceType.TEXT_DIFFERENCE , difference.getType());                       
        assertEquals("Different text should be in element :", "xt:hobby", difference.getExpectedNode().getNodeName());                             
        assertEquals("Different text should be child of :", "xt:hobbies", difference.getExpectedNode().getParentNode().getNodeName());  
        
        difference = comparison.getDifferences().get(2);
        assertEquals("Expected difference was not found :", Model.XmlDifferenceType.TEXT_DIFFERENCE , difference.getType());                       
        assertEquals("Different text should be in element :", "xt:hobby", difference.getExpectedNode().getNodeName());                             
        assertEquals("Different text should be child of :", "xt:hobbies", difference.getExpectedNode().getParentNode().getNodeName());                   
    }   
}