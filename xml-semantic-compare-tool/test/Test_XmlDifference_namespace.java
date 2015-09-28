
import Model.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for differences in XML elements namespaces
 *
 * @author Lukáš Wojnar
 * @version 06/6/2013
 */
public class Test_XmlDifference_namespace {

    public Test_XmlDifference_namespace() {
    }
    
    @Test
    public void test_namespace_differentPrefixButSameLocation() {
        XmlComparison comparison = new XmlComparison("test/Samples/contacts.xml",
                "test/Samples/contacts-namespace.xml");

        assertNotNull("Error creating XmlComparator.", comparison);
        assertTrue(comparison.compareXmlDocuments());
        assertEquals("Incorrect number of differences :", 0, comparison.getDifferences().size());
    }
    
    @Test
    public void test_namespace_differentPrefixLocation() {
        XmlComparison comparison = new XmlComparison("test/Samples/contacts.xml", "test/Samples/contacts-namespace-two-location.xml");

        assertNotNull("Error creating XmlComparator.", comparison );
        assertFalse( comparison.compareXmlDocuments() );
        
        XmlDifference difference;
        
        difference = comparison.getDifferences().get(0);
        assertEquals("Expected difference was not found :", Model.XmlDifferenceType.NAMESPACE_DIFFERENCE, difference.getType());
    }

    
}