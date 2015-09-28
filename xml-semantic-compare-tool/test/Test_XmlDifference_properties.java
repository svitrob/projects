import Model.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests properties of XML documents like version, encoding.
 * 
 * @author Pástor Lukáš
 * @version 05/6/2013
 */
public class Test_XmlDifference_properties {
    
    public Test_XmlDifference_properties() {
    }
   
    @Test
    public void test_properties_versionDifference() {                               
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml",
                                                      "test/Samples/contacts-properties-version.xml");        
                         
        assertNotNull("Error creating XmlComparator.", comparison );
                              
        assertFalse(comparison.compareXmlDocuments());                    
        assertEquals("Incorrect number of differences :", 1, comparison.getDifferences().size());                          
        assertEquals("Expected difference was not found :", Model.XmlDifferenceType.VERSION_DIFFERENCE, comparison.getDifferences().get(0).getType());                       
    }
    
    @Test
    public void test_properties_encodingDifference() {                               
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml",
                                                      "test/Samples/contacts-properties-encoding.xml");        
                         
        assertNotNull("Error creating XmlComparator.", comparison );
                              
        assertFalse(comparison.compareXmlDocuments());                    
        assertEquals("Incorrect number of differences :", 1, comparison.getDifferences().size());                          
        assertEquals("Expected difference was not found :", Model.XmlDifferenceType.ENCODING_DIFFERENCE, comparison.getDifferences().get(0).getType());                       
    }
}
