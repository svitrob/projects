package cz.muni.fi.pv168.rent;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.sql.DataSource;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsNull.nullValue;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;


public class ReservationManagerImplTest {
    
    private ReservationManagerImpl manager;    
    private DataSource dataSource;
         
    final Calendar validStartDate = new GregorianCalendar(2014, 1, 1);
    final Calendar validEndDate = new GregorianCalendar(2014, 5, 5);        
    final Vehicle validVehicle1 = newVehicle(new BigDecimal(2L), Vehicle.Brand.AUDI);
    final Vehicle validVehicle2 = newVehicle(new BigDecimal(1L), Vehicle.Brand.FORD);
    final Vehicle validVehicle3 = newVehicle(new BigDecimal(4L), Vehicle.Brand.SKODA);
    final Customer validCustomer1 = newCustomer("John Smith" , "Maple St. 3", "836 484 147", "john.smith@gmail.com");
    final Customer validCustomer2 = newCustomer("Alan Herold", "Main St. 27", "368 987 124", "alan.herold@gmail.com");
    final Customer validCustomer3 = newCustomer("Tom Gober",   "Forest St. 51", "758 174 994", "tom.gober@gmail.com");         
    
    private static DataSource prepareDataSource() throws SQLException {          
        BasicDataSource dataSource = new BasicDataSource();     
        dataSource.setUrl("jdbc:derby:memory:vehiclemgr-test;create=true");
        return dataSource;
    }
    
    @Before
    public void setUp() throws SQLException {
        dataSource = prepareDataSource();
        DBUtils.executeSqlScript(dataSource, ReservationManager.class.getResource("/createTables.sql"));
        manager = new ReservationManagerImpl(dataSource);                     
    }
    
    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(dataSource, ReservationManager.class.getResource("/dropTables.sql"));       
    }      
    
    @Test(expected = IllegalArgumentException.class)
    public void createReservation_PassingNull_ExceptionShouldBeThrown() {    
        manager.createReservation(null);                
    } 
    
    @Test(expected = IllegalArgumentException.class)
    public void createReservation_WithSetId_ExceptionShouldBeThrown() {            
        Reservation reservation = newReservation(validVehicle1, validCustomer1, validStartDate, validEndDate, null, "info");
        reservation.setId(1L);
        manager.createReservation(reservation);                
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createReservation_NullVehicle_ExceptionShouldBeThrown() {       
        manager.createReservation(newReservation(null, 
                                                 validCustomer1, 
                                                 validStartDate, 
                                                 validEndDate, 
                                                 null, 
                                                 "info"));             
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createReservation_NullCustomer_ExceptionShouldBeThrown() {       
        manager.createReservation(newReservation(validVehicle1, 
                                                 null, 
                                                 validStartDate, 
                                                 validEndDate, 
                                                 null, 
                                                 "info"));             
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createReservation_NullStartDate_ExceptionShouldBeThrown() {       
        manager.createReservation(newReservation(validVehicle1,  
                                                 validCustomer1, 
                                                 null, 
                                                 validStartDate, 
                                                 null, 
                                                 "info"));             
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createReservation_NullEndDate_ExceptionShouldBeThrown() {       
        manager.createReservation(newReservation(validVehicle1,
                                                 validCustomer1, 
                                                 validStartDate, 
                                                 null, 
                                                 null, 
                                                 "info"));             
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createReservation_WithSetRealEndDate_ExceptionShouldBeThrown() {       
        manager.createReservation(newReservation(validVehicle1,
                                                 validCustomer1, 
                                                 validStartDate, 
                                                 validEndDate, 
                                                 validEndDate, 
                                                 "info"));             
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createReservation_NullInfo_ExceptionShouldBeThrown() {       
        manager.createReservation(newReservation(validVehicle1,
                                                 validCustomer1, 
                                                 validStartDate, 
                                                 validEndDate, 
                                                 null, 
                                                 null));             
    }
    
    @Test
    public void createReservation_ValidReservation_ReservationShouldBeCreated() {
                       
        manager.getVehicleManager().createVehicle(validVehicle1);
        manager.getCustomerManager().createCustomer(validCustomer1);
        final Reservation reservation1 = newReservation(validVehicle1, validCustomer1, validStartDate, validEndDate, null, "info");        
        manager.createReservation(reservation1);        
        
        final Long id = reservation1.getId();
        final Reservation actual = manager.getReservationById(id);                   
        
        assertThat(reservation1.getId(), is(not(nullValue())));                                         
        assertThat(reservation1, is(actual));        
        assertThat(reservation1, not(sameInstance(actual)));    
        assertThat(manager.getAllReservations(), hasSize(1));        
    } 
    
    @Test(expected = IllegalArgumentException.class)
    public void createReservation_AlreadyExistingReservation_ReservationShouldNotBeCreated() {
       
        manager.getVehicleManager().createVehicle(validVehicle1);
        manager.getCustomerManager().createCustomer(validCustomer1);
        final Reservation reservation1 = newReservation(validVehicle1 , validCustomer1, validStartDate, validEndDate, null, "info");        
        manager.createReservation(reservation1);                        
        Long id = reservation1.getId();
        assertThat(manager.getAllReservations(), is(not(nullValue())));                
        assertThat(manager.getAllReservations(), hasSize(1)); 
        assertThat(manager.getAllReservations(), hasItem(reservation1));               
        assertThat(reservation1, equalTo(manager.getReservationById(id)));
        
        manager.getVehicleManager().createVehicle(validVehicle2);
        manager.getCustomerManager().createCustomer(validCustomer2);
        final Reservation reservation2 = newReservation(validVehicle2 , validCustomer2, validStartDate, validEndDate, null, "info");        
        manager.createReservation(reservation2);   
        id = reservation2.getId();
        assertThat(manager.getAllReservations(), is(not(nullValue())));
        assertThat(manager.getAllReservations(), hasSize(2));          
        assertThat(manager.getAllReservations(), containsInAnyOrder(reservation1, reservation2));               
        assertThat(reservation2, is(manager.getReservationById(id)));                        
        
        manager.getVehicleManager().createVehicle(validVehicle3);
        manager.getCustomerManager().createCustomer(validCustomer3);
        final Reservation reservation3 = newReservation(validVehicle3 , validCustomer3, validStartDate, validEndDate, null, "info");        
        manager.createReservation(reservation3);   
        id = reservation3.getId();
        assertThat(manager.getAllReservations(), is(not(nullValue())));
        assertThat(manager.getAllReservations(), hasSize(3));          
        assertThat(manager.getAllReservations(), containsInAnyOrder(reservation1, reservation2, reservation3));               
        assertThat(reservation3, is(manager.getReservationById(id)));                   
        
        // ReAdding customer1
        manager.createReservation(reservation1); 
    }
    
    @Test
    public void deleteReservation_ValidReservation_ReservationShouldBeRemoved() {       
              
        manager.getVehicleManager().createVehicle(validVehicle1);
        manager.getCustomerManager().createCustomer(validCustomer1);    
        final Reservation reservation1 = newReservation(validVehicle1 , validCustomer1, validStartDate, validEndDate, null, "info");
        manager.createReservation(reservation1);                
        Long id = reservation1.getId();        
        assertThat(manager.getAllReservations(), is(not(nullValue())));
        assertThat(manager.getReservationById(id), is(not(nullValue())));
        assertEquals(reservation1, manager.getReservationById(id)); 
        assertThat(manager.getAllReservations(), hasSize(1));
        assertThat(manager.getAllReservations(), containsInAnyOrder(reservation1));
        
        manager.getVehicleManager().createVehicle(validVehicle2);
        manager.getCustomerManager().createCustomer(validCustomer2);  
        final Reservation reservation2 = newReservation(validVehicle2 , validCustomer2, validStartDate, validEndDate, null, "info");
        manager.createReservation(reservation2);
        id = reservation2.getId();
        assertThat(manager.getAllReservations(), is(not(nullValue())));
        assertThat(manager.getReservationById(id), is(not(nullValue())));
        assertThat(reservation2, is(manager.getReservationById(id))); 
        assertThat(manager.getAllReservations(), hasSize(2));
        assertThat(manager.getAllReservations(), containsInAnyOrder(reservation1, reservation2));
        
        manager.getVehicleManager().createVehicle(validVehicle3);
        manager.getCustomerManager().createCustomer(validCustomer3); 
        final Reservation reservation3 = newReservation(validVehicle3 , validCustomer3, validStartDate, validEndDate, null, "info");
        manager.createReservation(reservation3);
        id = reservation3.getId();        
        assertThat(manager.getAllReservations(), is(not(nullValue())));
        assertThat(manager.getReservationById(id), is(not(nullValue())));
        assertThat(reservation3, is(manager.getReservationById(id))); 
        assertThat(manager.getAllReservations(), hasSize(3));
        assertThat(manager.getAllReservations(), containsInAnyOrder(reservation1, reservation2, reservation3));
                                                 
        //  Delete customer1
        manager.deleteReservation(reservation1);
        assertThat(manager.getAllReservations(), is(not(nullValue())));
        assertThat(manager.getReservationById(reservation1.getId()), is(nullValue()));                                             
        
        assertThat(manager.getReservationById(reservation2.getId()), is(not(nullValue())));                        
        assertThat(manager.getReservationById(reservation2.getId()), is(reservation2));
        
        assertThat(manager.getReservationById(reservation3.getId()), is(not(nullValue())));                        
        assertThat(manager.getReservationById(reservation3.getId()), is(reservation3)); 
        
        assertThat(manager.getAllReservations(), hasSize(2)); 
        assertThat(manager.getAllReservations(), containsInAnyOrder(reservation2, reservation3));             
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void deleteReservation_PassingNull_ExceptionShouldBeThrown() {    
        manager.deleteReservation(null);
    }
    
    @Test
    public void getReservationById_ValidId_ShouldReturnReservation() {
                                
        manager.getVehicleManager().createVehicle(validVehicle1);
        manager.getCustomerManager().createCustomer(validCustomer1);
        final Reservation reservation1 = newReservation(validVehicle1 , validCustomer1, validStartDate, validEndDate, null, "info");
        manager.createReservation(reservation1);
        
        Long id = reservation1.getId();                
        assertThat(manager.getReservationById(id), is(not(nullValue())));
        assertThat(reservation1, is(manager.getReservationById(id)));        
        assertThat(manager.getAllReservations(), hasItem(reservation1));            
    }
    
    @Test
    public void getReservationById_InvalidId_ShouldReturnNull() {
                                  
        manager.getVehicleManager().createVehicle(validVehicle1);
        manager.getCustomerManager().createCustomer(validCustomer1);        
        final Reservation reservation1 = newReservation(validVehicle1 , validCustomer1, validStartDate, validEndDate, null, "info");
        manager.createReservation(reservation1);
        
        manager.getVehicleManager().createVehicle(validVehicle2);
        manager.getCustomerManager().createCustomer(validCustomer2);   
        final Reservation reservation2 = newReservation(validVehicle2 , validCustomer2, validStartDate, validEndDate, null, "info");  
        manager.createReservation(reservation2);
        
        Long notValidId = reservation2.getId();        
        manager.deleteReservation(reservation2);
                
        assertThat(manager.getReservationById(notValidId), is(nullValue()));
        assertThat(reservation2, is(not(manager.getReservationById(notValidId))));
        assertThat(manager.getAllReservations(), not(hasItem(reservation2)));                
    }    
    
    @Test
    public void getAllReservation_AddingReservations_ShouldRetrunAll() {        
                        
        //  validVehicle1 must receives id from db/manager
        manager.getVehicleManager().createVehicle(validVehicle1);
        assertThat(validVehicle1.getId(), is(not(nullValue())));            
        //  validCustomer1 must receive id from db/manager
        manager.getCustomerManager().createCustomer(validCustomer1);
        assertThat(validCustomer1.getId(), is(not(nullValue())));        
        //  reservation1 receives id from db/manager
        final Reservation reservation1 = newReservation(validVehicle1 , validCustomer1, validStartDate, validEndDate, null, "info");                
        manager.createReservation(reservation1);
        assertThat(reservation1.getId(), is(not(nullValue())));                       
        //  assertions validating operation                        
        assertThat(manager.getAllReservations(), is(not(nullValue())));
        assertThat(manager.getAllReservations(), hasSize(1));                           
        assertEquals(reservation1.getStartDate(), manager.getReservationById(reservation1.getId()).getStartDate());         
        assertThat(manager.getAllReservations(), containsInAnyOrder(reservation1));  
                 
        //  validVehicle2 must receives id from db/manager
        manager.getVehicleManager().createVehicle(validVehicle2);
        assertThat(validVehicle2.getId(), is(not(nullValue()))); 
        //  validCustomer2 receives id from db/manager
        manager.getCustomerManager().createCustomer(validCustomer2);
        assertThat(validCustomer2.getId(), is(not(nullValue()))); 
        //  reservation2 receives id from db/manager
        final Reservation reservation2 = newReservation(validVehicle2 , validCustomer2, validStartDate, validEndDate, null, "info");
        manager.createReservation(reservation2); 
        assertThat(reservation2.getId(), is(not(nullValue()))); 
        //  assertions validating operation    
        assertThat(manager.getAllReservations(), is(not(nullValue())));
        assertThat(manager.getAllReservations(), hasSize(2));       
        assertEquals(reservation2.getStartDate(), manager.getReservationById(reservation2.getId()).getStartDate());         
        assertThat(manager.getAllReservations(), containsInAnyOrder(reservation1, reservation2));  
           
        //  validVehicle3 must receives id from db/manager
        manager.getVehicleManager().createVehicle(validVehicle3);
        assertThat(validVehicle3.getId(), is(not(nullValue())));
        //  validCustomer3 receives id from db/manager
        manager.getCustomerManager().createCustomer(validCustomer3);
        assertThat(validCustomer3.getId(), is(not(nullValue())));
        //  reservation3 receives id from db/manager
        final Reservation reservation3 = newReservation(validVehicle3 , validCustomer3, validStartDate, validEndDate, null, "info");
        manager.createReservation(reservation3);
        assertThat(reservation3.getId(), is(not(nullValue()))); 
        //  assertions validating operation    
        assertThat(manager.getAllReservations(), is(not(nullValue())));
        assertThat(manager.getAllReservations(), hasSize(3)); 
        assertEquals(reservation3.getStartDate(), manager.getReservationById(reservation3.getId()).getStartDate());         
        assertThat(manager.getAllReservations(), containsInAnyOrder(reservation1, reservation2, reservation3));                
    }    
    
    
    private Reservation newReservation(Vehicle vehicle, 
                                       Customer customer, 
                                       Calendar startDate, 
                                       Calendar endDate, 
                                       Calendar realEndDate, 
                                       String info) {        
        Reservation reservation = new Reservation();
        reservation.setId(null);        
        reservation.setVehicle(vehicle);
        reservation.setCustomer(customer);
        reservation.setStartDate(startDate);
        reservation.setEndDate(endDate);
        reservation.setRealEndDate(realEndDate);
        reservation.setInfo(info);        
        return reservation;
    }
   
    private static Vehicle newVehicle(BigDecimal price,Vehicle.Brand brand) {
        Vehicle vehicle = new Vehicle();             
        vehicle.setPrice(price);
        vehicle.setBrand(brand);        
        return vehicle;
    }
    
    private Customer newCustomer(String fullName, 
                                    String address, 
                                    String phone, 
                                    String email) {
        Customer customer = new Customer(); 
        customer.setId(null);
        customer.setName(fullName);
        customer.setAddress(address);
        customer.setPhone(phone);
        customer.setEmail(email);        
        return customer;
    }
    
}
