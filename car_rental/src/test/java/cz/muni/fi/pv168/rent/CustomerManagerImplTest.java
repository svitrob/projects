package cz.muni.fi.pv168.rent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsNull.nullValue;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;


public class CustomerManagerImplTest {
    
    private CustomerManagerImpl manager;
    private DataSource dataSource;

    private final String validName = "Peter Vasko";
    private final String validAddress = "Veseleho č.4";
    private final String validPhone = "678 481 238";
    private final String validEmail = "peter.vasko@gmail.com";
    
    private static DataSource prepareDataSource() throws SQLException {          
        BasicDataSource dataSource = new BasicDataSource();     
        dataSource.setUrl("jdbc:derby:memory:vehiclemgr-test;create=true");
        return dataSource;
    }
    
    @Before
    public void setUp() throws SQLException {
        dataSource = prepareDataSource();
        DBUtils.executeSqlScript(dataSource, CustomerManager.class.getResource("/createTables.sql"));
        manager = new CustomerManagerImpl(dataSource);                     
    }
    
    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(dataSource, CustomerManager.class.getResource("/dropTables.sql"));       
    } 
        
    @Test
    public void setUp_CustomerManagerInitializationTest() {
        assertThat(manager, is(not(nullValue())));
        assertThat(manager.getAllCustomers(), is(not(nullValue())));
        assertThat(manager.getAllCustomers(), is(empty()));
        assertThat(manager.getAllCustomers(), hasSize(0));                 
    } 
    
    @Test(expected = IllegalArgumentException.class)
    public void createCustomer_PassingNull_ExceptionShouldBeThrown() {    
        manager.createCustomer(null);                
    }
    
    
    @Test(expected = IllegalArgumentException.class)
    public void createCustomer_WithSetId_ExceptionShouldBeThrown() {    
        Customer customer = newCustomer(validName , validAddress, 
                                        validPhone, validEmail);        
        customer.setId(1L);        
        manager.createCustomer(customer);                
    }        
            
    @Test(expected = IllegalArgumentException.class)
    public void createCustomer_NullName_ExceptionShouldBeThrown() {       
        manager.createCustomer(newCustomer(null, 
                                           validAddress, 
                                           validPhone, 
                                           validEmail));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createCustomer_NullAddress_ExceptionShouldBeThrown() {       
        manager.createCustomer(newCustomer(validName, 
                                           null, 
                                           validPhone, 
                                           validEmail));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createCustomer_NullPhone_ExceptionShouldBeThrown() {       
        manager.createCustomer(newCustomer(validName, 
                                           validAddress, 
                                           null, 
                                           validEmail));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createCustomer_NullEmail_ExceptionShouldBeThrown() {       
        manager.createCustomer(newCustomer(validName, 
                                           validAddress, 
                                           validPhone, 
                                           null));
    }
           
    @Test
    public void createCustomer_ValidCustomer_CustomerShouldBeCreated() {
        final Customer expected = newCustomer("John Smith", "Hlavna 63", "336484", "meno@email.com");        
        
        manager.createCustomer(expected);        
        final Long id = expected.getId();
        final Customer actual = manager.getCustomerById(id);
                
        assertThat(expected.getId(), is(not(nullValue())));                                         
        assertThat(expected, is(actual));        
        assertThat(expected, not(sameInstance(actual)));    
        assertThat(manager.getAllCustomers(), hasSize(1));        
    }                                      
        
    @Test(expected = IllegalArgumentException.class)
    public void createCustomer_AlreadyExistingCustomer_CustomerShouldNotBeCreated() {
        
        final Customer customer1 = newCustomer("John Smith" , "Maple St. 3", "836 484 147", "john.smith@gmail.com");
        final Customer customer2 = newCustomer("Alan Herold", "Main St. 27", "368 987 124", "alan.herold@gmail.com");
        final Customer customer3 = newCustomer("Tom Gober",   "Forest St. 51", "758 174 994", "tom.gober@gmail.com");
        
        manager.createCustomer(customer1);   
        Long id = customer1.getId();
        assertThat(manager.getAllCustomers(), is(not(nullValue())));
        assertThat(manager.getCustomerById(id), is(not(nullValue())));
        assertThat(customer1, is(manager.getCustomerById(id)));
        assertThat(manager.getAllCustomers(), hasSize(1)); 
        assertThat(manager.getAllCustomers(), containsInAnyOrder(customer1));                       
        
        manager.createCustomer(customer2);   
        id = customer2.getId();
        assertThat(manager.getAllCustomers(), is(not(nullValue())));
        assertThat(manager.getCustomerById(id), is(not(nullValue())));
        assertThat(customer2, is(manager.getCustomerById(id)));
        assertThat(manager.getAllCustomers(), hasSize(2));          
        assertThat(manager.getAllCustomers(), containsInAnyOrder(customer1, customer2));                               
        
        manager.createCustomer(customer3);   
        id = customer3.getId();
        assertThat(manager.getAllCustomers(), is(not(nullValue())));
        assertThat(manager.getCustomerById(id), is(not(nullValue())));
        assertThat(customer3, is(manager.getCustomerById(id)));
        assertThat(manager.getAllCustomers(), hasSize(3));          
        assertThat(manager.getAllCustomers(), containsInAnyOrder(customer1, customer2, customer3));                               
        
        // ReAdding customer1
        manager.createCustomer(customer1); 
    }
        
    @Test
    public void deleteCustomer_ValidCustomer_CustomerShouldBeRemoved() {       
        
        final Customer customer1 = newCustomer("John Smith" , "Maple St. 3", "836 484 147", "john.smith@gmail.com");
        final Customer customer2 = newCustomer("Alan Herold", "Main St. 27", "368 987 124", "alan.herold@gmail.com");
        final Customer customer3 = newCustomer("Tom Gober",   "Forest St. 51", "758 174 994", "tom.gober@gmail.com");
                       
        manager.createCustomer(customer1);
        Long id = customer1.getId();        
        assertThat(manager.getAllCustomers(), is(not(nullValue())));
        assertThat(manager.getCustomerById(id), is(not(nullValue())));
        assertThat(customer1, is(manager.getCustomerById(id))); 
        assertThat(manager.getAllCustomers(), hasSize(1));
        assertThat(manager.getAllCustomers(), containsInAnyOrder(customer1));
        
        manager.createCustomer(customer2);
        id = customer2.getId();
        assertThat(manager.getAllCustomers(), is(not(nullValue())));
        assertThat(manager.getCustomerById(id), is(not(nullValue())));
        assertThat(customer2, is(manager.getCustomerById(id))); 
        assertThat(manager.getAllCustomers(), hasSize(2));
        assertThat(manager.getAllCustomers(), containsInAnyOrder(customer1, customer2));
               
        manager.createCustomer(customer3);
        id = customer3.getId();
        assertThat(manager.getAllCustomers(), is(not(nullValue())));
        assertThat(manager.getCustomerById(id), is(not(nullValue())));
        assertThat(customer3, is(manager.getCustomerById(id))); 
        assertThat(manager.getAllCustomers(), hasSize(3));
        assertThat(manager.getAllCustomers(), containsInAnyOrder(customer1, customer2, customer3));        
                       
        //  Delete customer1
        manager.deleteCustomer(customer1);
        assertThat(manager.getAllCustomers(), is(not(nullValue())));
        assertThat(manager.getCustomerById(customer1.getId()), is(nullValue()));                                             
        
        assertThat(manager.getCustomerById(customer2.getId()), is(not(nullValue())));                        
        assertThat(manager.getCustomerById(customer2.getId()), is(customer2));
        
        assertThat(manager.getCustomerById(customer3.getId()), is(not(nullValue())));                        
        assertThat(manager.getCustomerById(customer3.getId()), is(customer3)); 
        
        assertThat(manager.getAllCustomers(), hasSize(2)); 
        assertThat(manager.getAllCustomers(), containsInAnyOrder(customer2, customer3));             
    }
    
    /*
    @Test
    public void updateCustomer_updateName() {
        
        final Customer customer1 = new Customer("John Smith" , "Hlavna 63", "336 484 147", "john.smith@gmail.com");
        final Customer customer2 = new Customer("Alan Herold", "Hlavna 27", "368 987 124", "alan.herold@gmail.com");
        final Customer customer3 = new Customer("Tom Gober", "Hlavna 51", "758 174 994", "tom.gober@gmail.com");
                
        manager.createCustomer(customer1);
        manager.createCustomer(customer2);      
        manager.createCustomer(customer3); 
        
        customer = manager.getCustomerById(customer1.getId());
        customer.setFullName("Dennis Ritchie");
        manager.updateCustomer(customer);
        assertEquals("Dennis Ritchie", customer.getFullName());
        assertEquals("Hlavna 63", customer.getAddress());
        assertEquals("3384", customer.getPhone());
        assertEquals("meno@email.com", customer.getEmail());
        
        customer = manager.getCustomerById(customer.getId());
        customer.setAddress("Kovacska 5");
        manager.updateCustomer(customer);
        assertEquals("Dennis Ritchie", customer.getFullName());
        assertEquals("Kovacska 5", customer.getAddress());
        assertEquals("3384", customer.getPhone());
        assertEquals("meno@email.com", customer.getEmail());
        
        customer = manager.getCustomerById(customer.getId());
        customer.setPhone("117854");
        manager.updateCustomer(customer);
        assertEquals("Dennis Ritchie", customer.getFullName());
        assertEquals("Kovacska 5", customer.getAddress());
        assertEquals("117854", customer.getPhone());
        assertEquals("meno@email.com", customer.getEmail());
        
        customer = manager.getCustomerById(customer.getId());
        customer.setEmail("smith10@gmail.com");
        manager.updateCustomer(customer);
        assertEquals("Dennis Ritchie", customer.getFullName());
        assertEquals("Kovacska 5", customer.getAddress());
        assertEquals("117854", customer.getPhone());
        assertEquals("smith10@gmail.com", customer.getEmail());
        
        assertEquals(customer2, manager.getCustomerById(customer2.getId()));
    }     
    */
    
    @Test(expected = IllegalArgumentException.class)
    public void deleteCustomer_PassingNull_ExceptionShouldBeThrown() {    
        manager.deleteCustomer(null);
    }        
    
    @Test
    public void deleteCustomerNotInList() {
        
        final Customer customer1 = newCustomer("John Smith" , "Maple St. 3", "836 484 147", "john.smith@gmail.com");
        final Customer customer2 = newCustomer("Alan Herold", "Main St. 27", "368 987 124", "alan.herold@gmail.com");
        final Customer customer3 = newCustomer("Tom Gober",   "Forest St. 51", "758 174 994", "tom.gober@gmail.com");
        
        manager.createCustomer(customer1);
        assertNotNull(manager.getCustomerById(customer1.getId()));
        assertEquals(1, manager.getAllCustomers().size());
        manager.createCustomer(customer2);
        assertNotNull(manager.getCustomerById(customer2.getId()));
        assertEquals(2, manager.getAllCustomers().size());
        
        try {
            manager.deleteCustomer(customer3);
        } catch (IllegalArgumentException e) {
            //OK
        }
        assertEquals(2, manager.getAllCustomers().size());
    }       
    
    @Test
    public void getCustomerById_ValidId_ShouldReturnCustomer() {
        
       final Customer customer = newCustomer("John Smith" , "Maple St. 3", "836 484 147", "john.smith@gmail.com");        
        
        manager.createCustomer(customer);
        Long id = customer.getId();
        
        assertThat(manager.getCustomerById(id), is(not(nullValue())));
        assertThat(customer, is(manager.getCustomerById(id)));
        assertThat(manager.getAllCustomers(), hasItem(customer));            
    }    
    
    @Test
    public void getCustomerById_InvalidId_ShouldReturnNull() {
        
        final Customer customer1 = newCustomer("John Smith" , "Maple St. 3", "836 484 147", "john.smith@gmail.com");
        final Customer customer2 = newCustomer("Alan Herold", "Main St. 27", "368 987 124", "alan.herold@gmail.com");
        
        manager.createCustomer(customer1);
        manager.createCustomer(customer2);
        Long id = customer2.getId();        
        manager.deleteCustomer(customer2);
                
        assertThat(manager.getCustomerById(customer2.getId()), is(nullValue()));
        assertThat(customer2, is(not(manager.getCustomerById(customer2.getId()))));
        assertThat(manager.getAllCustomers(), not(hasItem(customer2)));                
    }    
    
    @Test
    public void getAllCustomers() {
        
        final Customer customer1 = newCustomer("John Smith" , "Maple St. 3", "836 484 147", "john.smith@gmail.com");
        final Customer customer2 = newCustomer("Alan Herold", "Main St. 27", "368 987 124", "alan.herold@gmail.com");
        final Customer customer3 = newCustomer("Tom Gober",   "Forest St. 51", "758 174 994", "tom.gober@gmail.com");
        List<Customer> list = new ArrayList<>();
                               
        manager.createCustomer(customer1);
        list.add(customer1);
        
        manager.createCustomer(customer2);
        list.add(customer2);
        
        manager.createCustomer(customer3);
        list.add(customer3);
        
        Collection<Customer> allCustomers = manager.getAllCustomers();
        
        assertNotNull(allCustomers);
        assertTrue(allCustomers.containsAll(list));
    }
    
    
    private Customer newCustomer(String name, 
                                 String address, 
                                 String phone, 
                                 String email) {
        Customer customer = new Customer();        
        customer.setName(name);
        customer.setAddress(address);
        customer.setPhone(phone);
        customer.setEmail(email);        
        return customer;
    }        
}