package cz.muni.fi.pv168.rent;

import java.math.BigDecimal;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsNull.nullValue;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class VehicleManagerImplTest {
    
    private VehicleManagerImpl manager;    
    private DataSource dataSource;
    
    private static final Long validId = 1L;
    private static final BigDecimal validPrice = new BigDecimal(1L);
    private static final Vehicle.Brand validBrand = Vehicle.Brand.AUDI;                        
    
    private static DataSource prepareDataSource() throws SQLException {          
        BasicDataSource dataSource = new BasicDataSource();         
        dataSource.setUrl("jdbc:derby:memory:vehiclemgr-test;create=true");       
        return dataSource;
    }
    
    @Before
    public void setUp() throws SQLException {
        dataSource = prepareDataSource();        
        DBUtils.executeSqlScript(dataSource, VehicleManager.class.getResource("/createTables.sql"));
        manager = new VehicleManagerImpl(dataSource);                        
    }
    
    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(dataSource, VehicleManager.class.getResource("/dropTables.sql"));       
    }
    
    @Test
    public void setUp_VehicleManagerInitializationTest() {
        assertThat(manager, is(not(nullValue())));
        assertThat(manager.getAllVehicles(), is(not(nullValue())));
        assertThat(manager.getAllVehicles(), is(empty()));
        assertThat(manager.getAllVehicles(), hasSize(0));                 
    } 
    
    @Test(expected = IllegalArgumentException.class)
    public void createVehicle_PassingNull_ExceptionShouldBeThrown() {    
        manager.createVehicle(null);
    }
       
    @Test(expected = IllegalArgumentException.class)
    public void createVehicle_NullPrice_ExceptionShouldBeThrown() {         
        manager.createVehicle(newVehicle(null, validBrand));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createVehicle_WithSetId_ExceptionShouldBeThrown() {    
        Vehicle vehicle = newVehicle(validPrice , validBrand);        
        vehicle.setId(1L);        
        manager.createVehicle(vehicle);                
    }   
        
    @Test(expected = IllegalArgumentException.class)
    public void createVehicle_NullBrand_ExceptionShouldBeThrown() {       
        manager.createVehicle(newVehicle(validPrice, null));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createVehicle_NegativePrice_ExceptionShouldBeThrown() {    
        manager.createVehicle(newVehicle(new BigDecimal(-1L), validBrand));
    }      
            
    @Test
    public void createVehicle_ValidVehicle_NewVehicleShouldBeCreated() {
        
        final Vehicle vehicle1 = newVehicle(new BigDecimal(1L), Vehicle.Brand.AUDI);        
        final Vehicle vehicle2 = newVehicle(new BigDecimal(2L), Vehicle.Brand.FORD);
        final Vehicle vehicle3 = newVehicle(new BigDecimal(3L), Vehicle.Brand.RENAULT);                 
        
        manager.createVehicle(vehicle1);  
        assertThat(manager.getAllVehicles(), hasSize(1));      
        assertThat(manager.getAllVehicles(), contains(vehicle1)); // exactly one copy of vehicle, nothing else                        
        
        manager.createVehicle(vehicle2);
        assertThat(manager.getAllVehicles(), hasSize(2));
        assertThat(manager.getAllVehicles(), containsInAnyOrder(vehicle1, vehicle2));
        
        manager.createVehicle(vehicle3);       
        assertThat(manager.getAllVehicles(), hasSize(3));       
        assertThat(manager.getAllVehicles(), containsInAnyOrder(vehicle1, vehicle2, vehicle3));                           
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createVehicle_AlreadyExistingVehicle_VehicleShouldNotBeCreated() {  
        
        final Vehicle vehicle1 = newVehicle(new BigDecimal(1L), Vehicle.Brand.AUDI);        
        final Vehicle vehicle2 = newVehicle(new BigDecimal(2L), Vehicle.Brand.FORD);
        final Vehicle vehicle3 = newVehicle(new BigDecimal(3L), Vehicle.Brand.RENAULT);                          
               
        manager.createVehicle(vehicle1);   
        Long id = vehicle1.getId();         
        assertThat(manager.getAllVehicles(), is(not(nullValue())));
        assertThat(manager.getVehicleById(id), is(not(nullValue())));
        assertThat(vehicle1, is(manager.getVehicleById(id)));
        assertThat(manager.getAllVehicles(), hasSize(1)); 
        assertThat(manager.getAllVehicles(), containsInAnyOrder(vehicle1));                       
                
        manager.createVehicle(vehicle2);
        id = vehicle2.getId();
        assertThat(manager.getAllVehicles(), is(not(nullValue())));
        assertThat(manager.getVehicleById(id), is(not(nullValue())));
        assertThat(vehicle2, is(manager.getVehicleById(id)));
        assertThat(manager.getAllVehicles(), hasSize(2)); 
        assertThat(manager.getAllVehicles(), containsInAnyOrder(vehicle1, vehicle2));                       
        
        manager.createVehicle(vehicle3);  
        id = vehicle3.getId();
        assertThat(manager.getAllVehicles(), is(not(nullValue())));
        assertThat(manager.getVehicleById(id), is(not(nullValue())));
        assertThat(vehicle3, is(manager.getVehicleById(id)));
        assertThat(manager.getAllVehicles(), hasSize(3)); 
        assertThat(manager.getAllVehicles(), containsInAnyOrder(vehicle1, vehicle2, vehicle3));                      
          
        // ReAdding vehicle1
        manager.createVehicle(vehicle1);                       
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void deleteVehicle_PassingNull_ExceptionShouldBeThrown() {    
        manager.deleteVehicle(null);
    }
    
    @Test
    public void deleteVehicle_ValidVehicle_VehicleShouldBeRemoved() {
        
        final Vehicle vehicle1 = newVehicle(new BigDecimal(1L), Vehicle.Brand.AUDI);        
        final Vehicle vehicle2 = newVehicle(new BigDecimal(2L), Vehicle.Brand.FORD);
        final Vehicle vehicle3 = newVehicle(new BigDecimal(3L), Vehicle.Brand.RENAULT); 
                
        manager.createVehicle(vehicle1);
        Long id = vehicle1.getId();
        assertThat(manager.getAllVehicles(), is(not(nullValue())));
        assertThat(manager.getVehicleById(id), is(not(nullValue())));
        assertThat(vehicle1, is(manager.getVehicleById(id))); 
        assertThat(manager.getAllVehicles(), hasSize(1));  
        assertThat(manager.getAllVehicles(), containsInAnyOrder(vehicle1));
                
        manager.createVehicle(vehicle2);
        id = vehicle2.getId();
        assertThat(manager.getAllVehicles(), is(not(nullValue())));
        assertThat(manager.getVehicleById(id), is(not(nullValue())));
        assertThat(vehicle2, is(manager.getVehicleById(id)));    
        assertThat(manager.getAllVehicles(), hasSize(2));       
        assertThat(manager.getAllVehicles(), containsInAnyOrder(vehicle1, vehicle2));
                
        manager.createVehicle(vehicle3);
        id = vehicle3.getId();
        assertThat(manager.getAllVehicles(), is(not(nullValue())));
        assertThat(manager.getVehicleById(id), is(not(nullValue())));
        assertThat(vehicle3, is(manager.getVehicleById(id))); 
        assertThat(manager.getAllVehicles(), hasSize(3));
        assertThat(manager.getAllVehicles(), containsInAnyOrder(vehicle1, vehicle2, vehicle3));
             
        //  Delete vehicle1
        manager.deleteVehicle(vehicle1);        
        assertThat(manager.getAllVehicles(), is(not(nullValue())));
        assertThat(manager.getAllVehicles(), hasSize(2));
        assertThat(manager.getVehicleById(vehicle1.getId()), is(nullValue()));
                
        assertThat(manager.getVehicleById(vehicle2.getId()), is(not(nullValue())));       
        assertThat(manager.getVehicleById(vehicle2.getId()), is(vehicle2));
       
        assertThat(manager.getVehicleById(vehicle3.getId()), is(not(nullValue())));       
        assertThat(manager.getVehicleById(vehicle3.getId()), is(vehicle3));    
        
        assertThat(manager.getAllVehicles(), hasSize(2)); 
        assertThat(manager.getAllVehicles(), containsInAnyOrder(vehicle2, vehicle3));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void deleteVehicle_VehicleDoesNotExist_NothingShouldNotBeDeleted() {
        
        final Vehicle vehicle1 = newVehicle(new BigDecimal(1L), Vehicle.Brand.AUDI);        
        final Vehicle vehicle2 = newVehicle(new BigDecimal(2L), Vehicle.Brand.FORD);                
        final Vehicle vehicle3 = newVehicle(new BigDecimal(3L), Vehicle.Brand.SEAT); 
        
        assertThat(manager.getAllVehicles(), hasSize(0));
        manager.createVehicle(vehicle1);             
        assertThat(manager.getAllVehicles(), hasSize(1));
        assertThat(manager.getAllVehicles(), contains(vehicle1));
        assertThat(vehicle1, is(manager.getVehicleById(vehicle1.getId())));          
        
        manager.createVehicle(vehicle2);       
        assertThat(manager.getAllVehicles(), hasSize(2));      
        assertThat(manager.getAllVehicles(), containsInAnyOrder(vehicle1, vehicle2));
        assertThat(vehicle2, is(manager.getVehicleById(vehicle2.getId())));                                                                        
        
        vehicle3.setId(vehicle1.getId() + vehicle2.getId());
        manager.deleteVehicle(vehicle3);        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getVehiclesById_NullId_ExceptionShouldBeThrown() {                        
        manager.createVehicle(newVehicle(validPrice, validBrand));        
        manager.getVehicleById(null);
    }
    
    @Test
    public void getVehiclesById_ValidId_ShouldReturnVehicle() {
        final Vehicle vehicle = newVehicle(validPrice, validBrand);                                
        
        manager.createVehicle(vehicle);
        Long id = vehicle.getId();        
        
        assertThat(manager.getVehicleById(id), is(not(nullValue())));
        assertThat(vehicle, is(manager.getVehicleById(vehicle.getId())));     
        assertThat(manager.getAllVehicles(), hasItem(vehicle));
    }
    
    @Test
    public void getVehiclesById_InvalidId_ShouldReturnNull() {
        
        final Vehicle vehicle1 = newVehicle(validPrice, validBrand);
        final Vehicle vehicle2 = newVehicle(validPrice, validBrand);                                
        
        manager.createVehicle(vehicle1); 
        manager.createVehicle(vehicle2);
        Long id = vehicle2.getId();
        manager.deleteVehicle(vehicle2);        
        
        assertThat(manager.getAllVehicles(), hasSize(1));
        assertThat(manager.getVehicleById(vehicle2.getId()), is(nullValue()));
        assertThat(vehicle2, is(not(manager.getVehicleById(vehicle2.getId()))));     
        assertThat(manager.getAllVehicles(), not(hasItem(vehicle2)));
    }
    
    @Test
    public void getAllVehicles() {  
        
        final Vehicle vehicle1 = newVehicle(new BigDecimal(1L), Vehicle.Brand.AUDI);        
        final Vehicle vehicle2 = newVehicle(new BigDecimal(2L), Vehicle.Brand.FORD);
        final Vehicle vehicle3 = newVehicle(new BigDecimal(3L), Vehicle.Brand.RENAULT);               
        
        manager.createVehicle(vehicle1);
        assertThat(manager.getAllVehicles(), is(not(nullValue())));      
        assertThat(manager.getAllVehicles(), hasSize(1));
        assertThat(manager.getAllVehicles(), containsInAnyOrder(vehicle1));
                
        manager.createVehicle(vehicle2);
        assertThat(manager.getAllVehicles(), is(not(nullValue())));
        assertThat(manager.getAllVehicles(), hasSize(2));
        assertThat(manager.getAllVehicles(), containsInAnyOrder(vehicle1, vehicle2));
        
        manager.createVehicle(vehicle3);    
        assertThat(manager.getAllVehicles(), is(not(nullValue())));   
        assertThat(manager.getAllVehicles(), hasSize(3));
        assertThat(manager.getAllVehicles(), containsInAnyOrder(vehicle1, vehicle2, vehicle3));                       
    }

    private static Vehicle newVehicle(BigDecimal price,Vehicle.Brand brand) {                            
        return new Vehicle(null, price, brand); 
    }
}

/*
    "contains()" is checking
        - that every specified elemet is in collection
        - that only the specified ones are in collection (no other ones)
        - the specified ordering (sets may fail this condition)
    
    "containsInAnyOrder()" is checking
        - that every specified element is in collection
        - that only specified element is in collection (no other ones)
    
    "hasItems()" is checking
        - that every specified element is in collection       

    "hasItem()" is checking
        - that the specified element is in collection        
                
    "IsIn" is checking
        - that the 
    
        Set<Vehicle> collection = new HashSet();
        List<Vehicle> collection = new ArrayList();
        Vehicle vehicle1 = newVehicle(validId, validPrice, validBrand);
        Vehicle vehicle2 = newVehicle(validId + 1, validPrice, validBrand);
        Vehicle vehicle3 = newVehicle(validId + 2, validPrice, validBrand);
        collection.add(vehicle1);
        collection.add(vehicle1);
        collection.add(vehicle2);
        collection.add(vehicle3);
        assertThat(vehicle1, isIn(collection)); // doesnt contain v1
        assertThat(collection, hasItem(vehicle1)); // doesnt contain v
        assertThat(collection, containsInAnyOrder(vehicle1)); // has more elements        
        assertThat(collection, hasSize(1));        
*/ 
