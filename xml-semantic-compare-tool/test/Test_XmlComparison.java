import Model.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;

/**
 * Tests for XmlComparison methods.
 * 
 * @author Pástor Lukáš
 * @version 05/6/2013
 */
public class Test_XmlComparison {
    
    public Test_XmlComparison() {
    }
   
    @Test(expected = IllegalArgumentException.class)
    public void test_XmlComparator_nullURIdocumentA() {
                                     
        XmlComparison comparison = new XmlComparison( null, "test/Samples/contacts.xml" );
                                         
        assertNotNull("Error creating XmlComparator.", comparison );
        fail("IllegalArgumentException should be thrown.");            
    }
   
    @Test(expected = IllegalArgumentException.class)
    public void test_XmlComparator_nullURIdocumentB() {
                                     
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml", null);
                                         
        assertNotNull("Error creating XmlComparator.", comparison );
        fail("IllegalArgumentException should be thrown.");            
    }
   
    @Test
    public void test_XmlComparator_defaultValues() {
                                     
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml", 
                                                      "test/Samples/contacts-attribute-more.xml" );
                                         
        assertNotNull(comparison.getFirstDocument());
        assertNotNull(comparison.getSecondDocument());
        assertNotNull(comparison.getDifferences());
        assertFalse(comparison.trimText());
        assertTrue(comparison.strictElementOrder());
        assertFalse(comparison.checkAttributeValues());                               
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_XmlComparator_addDifference_nullArgument() {
                     
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml", 
                                                      "test/Samples/contacts-attribute-more.xml" );
        
        comparison.addDifference(null);
        fail("IllegalArgumentException should be thrown.");                        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_XmlComparator_createDocumentFromURI_nullArgument() {
                                     
        Document document = XmlComparison.createDocumentFromURI(null);
        fail("IllegalArgumentException should be thrown.");  
    }   
    
    @Test
    public void test_XmlComparator_createDocumentFromURI_validArgumnet() {
                                     
        Document document = XmlComparison.createDocumentFromURI("test/Samples/contacts.xml");
        assertNotNull(document);
    }   
    
    @Test(expected = IllegalArgumentException.class)
    public void test_XmlComparator_createDocumentFromString_nullArgument() {
                                     
        Document document = XmlComparison.createDocumentFormString(null);
        fail("IllegalArgumentException should be thrown.");  
    }   
    
    @Test
    public void test_XmlComparator_createDocumentFromString_validArgumnet() {
             
        String documentString = "<root><a1>text-a1</a1><a2>text-a2</a2></root>";
        
        Document document = XmlComparison.createDocumentFormString(documentString);
        assertNotNull(document);
    }   
    
    @Test(expected = IllegalArgumentException.class)
    public void test_XmlComparator_equalsDocumentVersion_nullArgumnet1() {
    
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml", 
                                                      "test/Samples/contacts-attribute-more.xml" );
                
        comparison.equalsDocumentVersion(null, comparison.getSecondDocument());
        fail("IllegalArgumentException should be thrown.");  
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_XmlComparator_equalsDocumentVersion_nullArgumnet2() {
    
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml", 
                                                      "test/Samples/contacts-attribute-more.xml" );
                
        comparison.equalsDocumentVersion(comparison.getFirstDocument(), null);
        fail("IllegalArgumentException should be thrown.");  
    }
    
    @Test
    public void test_XmlComparator_equalsDocumentVersion_equalVersion() {
    
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml", 
                                                      "test/Samples/contacts-attribute-more.xml" );
                
        assertTrue(comparison.equalsDocumentVersion(comparison.getFirstDocument(), 
                                                    comparison.getSecondDocument()));          
    }
    
    @Test
    public void test_XmlComparator_equalsDocumentVersion_differentVersion() {
    
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml", 
                                                      "test/Samples/contacts-properties-version.xml" );
                
        assertFalse(comparison.equalsDocumentVersion(comparison.getFirstDocument(), 
                                                     comparison.getSecondDocument()));  
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_XmlComparator_equalsDocumentEncoding_nullArgumnet1() {
    
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml", 
                                                      "test/Samples/contacts-attribute-more.xml" );
                
        comparison.equalsDocumentEncoding(null, comparison.getSecondDocument());
        fail("IllegalArgumentException should be thrown.");  
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_XmlComparator_equalsDocumentEncoding_nullArgumnet2() {
    
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml", 
                                                      "test/Samples/contacts-attribute-more.xml" );
                
        comparison.equalsDocumentEncoding(comparison.getFirstDocument(), null);
        fail("IllegalArgumentException should be thrown.");  
    }
    
    @Test
    public void test_XmlComparator_equalsDocumentEncoding_equalVersion() {
    
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml", 
                                                      "test/Samples/contacts-attribute-more.xml" );
                
        assertTrue(comparison.equalsDocumentEncoding(comparison.getFirstDocument(), 
                                                     comparison.getSecondDocument()));          
    }
    
    @Test
    public void test_XmlComparator_equalsDocumentEncoding_differentVersion() {
    
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml", 
                                                      "test/Samples/contacts-properties-encoding.xml" );
                
        assertFalse(comparison.equalsDocumentEncoding(comparison.getFirstDocument(), 
                                                      comparison.getSecondDocument()));  
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_XmlComparator_equalsElements_nullArgumnet1() {
    
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml", 
                                                      "test/Samples/contacts-properties-encoding.xml" );
                
        Document document = comparison.getFirstDocument();                
        comparison.equalsElements(null, document.getDocumentElement(), false); 
        fail("IllegalArgumentException should be thrown."); 
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_XmlComparator_equalsElements_nullArgumnet2() {
    
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml", 
                                                      "test/Samples/contacts-properties-encoding.xml" );
                
        Document document = comparison.getFirstDocument();                
        comparison.equalsElements(document.getDocumentElement(), null, false); 
        fail("IllegalArgumentException should be thrown."); 
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_XmlComparator_equalsElementName_nullArgumnet1() {
            
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml", 
                                                      "test/Samples/contacts-properties-encoding.xml" );                                
        
        comparison.equalsElementName(null, comparison.getSecondDocument().getDocumentElement());    
        fail("IllegalArgumentException should be thrown."); 
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_XmlComparator_equalsElementName_nullArgumnet2() {
    
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml", 
                                                      "test/Samples/contacts-properties-encoding.xml" );                                
        
        comparison.equalsElementName(comparison.getFirstDocument().getDocumentElement(), null);    
        fail("IllegalArgumentException should be thrown."); 
    }
    
    @Test
    public void test_XmlComparator_equalsElementName_notEqualNames() {
    
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml", 
                                                      "test/Samples/contacts-properties-encoding.xml" );                                
        
        assertTrue(comparison.equalsElementName(comparison.getFirstDocument().getDocumentElement(), 
                                                comparison.getSecondDocument().getDocumentElement()));            
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_XmlComparator_equalsElementAttributes_nullArgument1() {
    
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml", 
                                                      "test/Samples/contacts-properties-encoding.xml" );                                
        
        assertTrue(comparison.equalsElementAttributes(null, comparison.getSecondDocument().getDocumentElement())); 
        fail("IllegalArgumentException should be thrown."); 
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_XmlComparator_equalsElementAttributes_nullArgument2() {
    
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml", 
                                                      "test/Samples/contacts-properties-encoding.xml" );                                
        
        assertTrue(comparison.equalsElementAttributes(comparison.getFirstDocument().getDocumentElement(), null)); 
        fail("IllegalArgumentException should be thrown."); 
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_XmlComparator_equalsElementText_nullArgument1() {
    
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml", 
                                                      "test/Samples/contacts-properties-encoding.xml" );                                
        
        assertTrue(comparison.equalsElementText(null, comparison.getSecondDocument().getDocumentElement())); 
        fail("IllegalArgumentException should be thrown."); 
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_XmlComparator_equalsElementText_nullArgument2() {
    
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml", 
                                                      "test/Samples/contacts-properties-encoding.xml" );                                
        
        assertTrue(comparison.equalsElementText(comparison.getFirstDocument().getDocumentElement(), null)); 
        fail("IllegalArgumentException should be thrown."); 
    }
      
    @Test(expected = IllegalArgumentException.class)
    public void test_XmlComparator_isTextElement_nullArgument() {
    
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml", 
                                                      "test/Samples/contacts-properties-encoding.xml" );                                
        
        assertTrue(comparison.isTextElement(null)); 
        fail("IllegalArgumentException should be thrown."); 
    }
    
    @Test
    public void test_XmlComparator_isTextElement_notTextElement() {
    
        XmlComparison comparison = new XmlComparison( "test/Samples/contacts.xml", 
                                                      "test/Samples/contacts-properties-encoding.xml" );                                
        
        assertFalse(comparison.isTextElement(comparison.getFirstDocument().getDocumentElement()));         
    }   
}