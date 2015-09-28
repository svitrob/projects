package cz.muni.fi.pv168.rent;

import cz.muni.fi.pv168.common.InternalIntegrityException;
import cz.muni.fi.pv168.common.ServiceFailureException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.LoggerFactory;


public final class VehicleManagerImpl implements VehicleManager {      
    
    public final static org.slf4j.Logger logger = 
            LoggerFactory.getLogger(VehicleManagerImpl.class.getName());
    
    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("Strings");    
    private DataSource dataSource;       

    public VehicleManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }     
    
    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set.");
        }
    }
      
    @Override
    public void createVehicle(Vehicle vehicle)  {   
        
        checkDataSource();
        final String query = "INSERT INTO Vehicle (Brand, Price) VALUES(?, ?)";               
        
        if (vehicle == null)                 
            throw new IllegalArgumentException(bundle.getString("ExNullVehicle"));   //("Vehicle can not be null.");            
        
        if (vehicle.getId() != null)                
            throw new IllegalArgumentException(bundle.getString("ExVehicleId"));  //("Vehicle id can not be already assigned.");
            
        if (vehicle.getPrice() == null)                
            throw new IllegalArgumentException(bundle.getString("ExNullPrice"));  //("Vehicle price can not be null."); 
        
        if (vehicle.getPrice().compareTo(BigDecimal.ZERO) < 0)                
            throw new IllegalArgumentException(bundle.getString("ExNegativePrice"));  //("Vehicle can not have negative price.");
        
        if (vehicle.getBrand() == null)                
            throw new IllegalArgumentException(bundle.getString("ExNullBrand"));  //("Vehicle brand can not be null.");
                                                
        Connection connection = null;                
        PreparedStatement statement = null;
        ResultSet rset = null;
        
        try {            
                connection = dataSource.getConnection();
                connection.setAutoCommit(false);            
                statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);                    
                statement.setString(1, vehicle.getBrand().toString());
                statement.setBigDecimal(2, vehicle.getPrice());                                     
                int modified = statement.executeUpdate(); 

                String message;      
                switch (modified) {                
                    case 0  :   
                                message = bundle.getString("Entity") + " " + vehicle.toString() + " " + bundle.getString("NotExisting"); 
                                throw new ServiceFailureException(message);                         
                    case 1  :        
                                rset = statement.getGeneratedKeys();                            
                                if (rset.next()) {                             
                                    vehicle.setId(rset.getLong(1));                      
                                    message = bundle.getString("Entity") + " " + vehicle.toString() + " " + bundle.getString("AddedInDB");
                                    connection.commit();
                                    logger.debug(message);                                       
                                }                            
                                break;
                    default :
                                message = vehicle.toString() + ": " + bundle.getString("ExMultipleRows") + ".";                                                                          
                                throw new ServiceFailureException(message);                                   
                } 
                connection.setAutoCommit(true);
        } catch (SQLException e) { 
            
                String message = bundle.getString("ExDbInsertErr1") + " " + vehicle.toString() + " " + bundle.getString("ExDbInsertErr2") + " " + e.getMessage();               
                logger.warn(message, e);             
                throw new ServiceFailureException(message, e); 
                
        } finally {
            
                DBUtils.closeQuietly(rset);
                DBUtils.closeQuietly(statement);
                DBUtils.closeQuietly(connection);
                 
        }                                                  
    }

    
    @Override
    public void deleteVehicle(Vehicle vehicle) {
        
        checkDataSource();
        final String query = "DELETE FROM Vehicle WHERE Id = ?";
        
        if (vehicle == null)                 
            throw new IllegalArgumentException(bundle.getString("ExNullVehicle"));
        
        if (vehicle.getId() == null)                
            throw new IllegalArgumentException(bundle.getString("ExNullVehicleId"));  //Vehicle id can not be null
        
        if (vehicle.getId() <= 0L)                
            throw new IllegalArgumentException(bundle.getString("ExNegativePrice"));                
                
        Connection connection = null;                          
        PreparedStatement statement = null;
        
        try {            
                connection = dataSource.getConnection();
                connection.setAutoCommit(false);            
                statement = connection.prepareStatement(query);                
                statement.setLong(1, vehicle.getId());               
                int modified = statement.executeUpdate();

                String message;                 
                switch (modified) {                
                    case 0  :   
                                message = bundle.getString("Entity") + " " + vehicle.toString() + " " + bundle.getString("NotExisting");                      
                                throw new IllegalArgumentException(message);                         
                    case 1  :        
                                message = bundle.getString("Entity") + " " + vehicle.toString() + " " + bundle.getString("RemovedFromDB"); 
                                connection.commit();
                                logger.debug(message);                            
                                break;
                    default :                                                        
                                message = vehicle.toString() + ": " + bundle.getString("ExMultipleRows") + ".";                                   
                                throw new ServiceFailureException(message);                                   
                }  
                connection.setAutoCommit(true);
                
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            
                String message = bundle.getString("ExDeletingFromDb");                         
                logger.warn(message, e);
                throw new InternalIntegrityException(message, e); 
                
        } catch (SQLException e) {
            
                String message = vehicle.toString() + ": " + bundle.getString("ExDeletingFromDb") + ": "  + e.getMessage(); 
                logger.warn(message, e);   
                throw new ServiceFailureException(message, e);   
                
        } finally {   
            
                DBUtils.closeQuietly(statement);
                DBUtils.rollbackAndCloseQuietly(connection); 
                
        }                                                  
    }

    @Override
    public void updateVehicle(Vehicle vehicle) {                  
         
        checkDataSource();
        final String query = "UPDATE Vehicle SET Brand = ?, Price = ? WHERE Id = ?";                  
        
        if (vehicle == null)                
            throw new IllegalArgumentException(bundle.getString("ExNullVehicle"));    
        
        if (vehicle.getId() == null)                
            throw new IllegalArgumentException(bundle.getString("ExNullVehicleId"));
        
        if (vehicle.getId() <= 0L)                
            throw new IllegalArgumentException(bundle.getString("ExNegativePrice"));
                
        if (vehicle.getPrice() == null)                
            throw new IllegalArgumentException(bundle.getString("ExNullPrice")); 
        
        if (vehicle.getPrice().compareTo(BigDecimal.ZERO) < 0)               
            throw new IllegalArgumentException(bundle.getString("ExNegativePrice")); 
        
        if (vehicle.getBrand() == null)
            throw new IllegalArgumentException(bundle.getString("ExNullBrand"));
                             
        Connection connection = null;                       
        PreparedStatement statement = null;
        
        try {                   
                connection = dataSource.getConnection();
                connection.setAutoCommit(false);            
                statement = connection.prepareStatement(query);                                                
                statement.setString(1, vehicle.getBrand().toString());
                statement.setBigDecimal(2, vehicle.getPrice()); 
                statement.setLong(3, vehicle.getId());                        
                int modified = statement.executeUpdate();

                String message;                
                switch (modified) {                
                    case 0  :   
                                message = bundle.getString("Entity") + " " + vehicle.toString() + " " + bundle.getString("NotExisting");                             
                                throw new ServiceFailureException(message);                         
                    case 1  :        
                                message = bundle.getString("Entity") + " " + vehicle.toString() + " " + bundle.getString("Updated");                                                       
                                connection.commit();
                                logger.debug(message);                                   
                                break;
                    default :
                                message = vehicle.toString() + ": " + bundle.getString("ExMultipleRows") + ".";                                                                             
                                throw new ServiceFailureException(message);                                   
                }      
                connection.setAutoCommit(true);
                
        } catch (SQLException e) {
            
                String message = bundle.getString("ExUpdatingDb") + ": " + vehicle.getId() + " : " + e.getMessage();
                logger.warn(message, e);   
                throw new ServiceFailureException(message, e);   
                
        } finally {  
            
                DBUtils.closeQuietly(statement);             
                DBUtils.rollbackAndCloseQuietly(connection);
            
        }                                                                                     
    }

    @Override
    public Vehicle getVehicleById(Long id) {

        checkDataSource();
        final String query = "SELECT * FROM Vehicle WHERE id = ?"; 
                     
        if (id == null)            
            throw new IllegalArgumentException(bundle.getString("ExNullVehicleId"));    
        
        if (id <= 0L)                
            throw new IllegalArgumentException(bundle.getString("ExNegativePrice"));     
                                
        Connection connection = null;                                                                 
        PreparedStatement statement = null; 
        ResultSet rset = null;
        
        try {                                                 
                connection = dataSource.getConnection();            
                statement = connection.prepareStatement(query);  
                statement.setLong(1, id);                                                                        
                rset = statement.executeQuery();                                                                     

                String message; 
                Vehicle vehicle = null;
                if (rset.next()) {                                         
                    vehicle = DBUtils.resultSetToVehicle(rset);
                    if (rset.next()) {  
                        message = "Multiple entities with id : " + id + " retreived from database.";
                        throw new ServiceFailureException(message);
                    }                                
                }
                return vehicle;        
                
        } catch (SQLException e) {
            
                String message = "Error when getting entity with id : " + id + " from database : " + e.getMessage();
                logger.warn(message, e);   
                throw new ServiceFailureException(message, e);
                
        } finally { 
            
                DBUtils.closeQuietly(rset);
                DBUtils.closeQuietly(statement);
                DBUtils.closeQuietly(connection);
                
        } 
    }

    @Override
    public Collection<Vehicle> getAllVehicles() {
        
        checkDataSource();
        final String query = "SELECT * FROM Vehicle";                       
                
        Connection connection = null;                                
        PreparedStatement statement = null; 
        ResultSet rset = null;
        
        try {               
                connection = dataSource.getConnection();           
                statement = connection.prepareStatement(query);                      
                rset = statement.executeQuery();  
                List<Vehicle> vehicles = new ArrayList<>();
                
                while(rset.next()) {                
                    vehicles.add(DBUtils.resultSetToVehicle(rset));
                }                
                return vehicles;
                
        } catch (SQLException e) {
            
                String message = bundle.getString("ExGettingFromDb") + " : " + e.getMessage();
                logger.warn(message, e);   
                throw new ServiceFailureException(message, e);
                
        } finally {
            
            DBUtils.closeQuietly(rset);
            DBUtils.closeQuietly(statement);             
            DBUtils.closeQuietly(connection); 
            
        }                
    } 
}
