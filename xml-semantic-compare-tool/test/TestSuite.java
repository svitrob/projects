import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite for all test classes.
 *
 * @author Pástor Lukáš
 * @version 05/6/2013
 */
@RunWith(Suite.class) 
@Suite.SuiteClasses({ 
    Test_XmlDifference_attributes.class, 
    Test_XmlDifference_elements.class,
    Test_XmlDifference_text.class,
    Test_XmlDifference_properties.class,
    Test_XmlComparison.class
}) 

public class TestSuite {
}
    
    