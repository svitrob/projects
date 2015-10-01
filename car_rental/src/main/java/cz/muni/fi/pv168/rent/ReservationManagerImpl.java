package cz.muni.fi.pv168.rent;

import cz.muni.fi.pv168.common.ServiceFailureException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.LoggerFactory;

public final class ReservationManagerImpl implements ReservationManager {
    
    public final static org.slf4j.Logger logger = 
            LoggerFactory.getLogger(ReservationManagerImpl.class.getName());
    
    private final DataSource dataSource;      
    private final VehicleManager vehicleManager;
    private final CustomerManager customerManager;

    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("Strings");
    
    public ReservationManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.vehicleManager = new VehicleManagerImpl(dataSource);
        this.customerManager = new CustomerManagerImpl(dataSource);
    }
       
    public VehicleManager getVehicleManager() {
        return vehicleManager;
    }

    public CustomerManager getCustomerManager() {
        return customerManager;
    }
            
    public DataSource getDataSource() {
        return dataSource;
    }    
    
    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set.");
        }
    }

    @Override
    public void createReservation(Reservation reservation) {
        
        checkDataSource();
        final String query = "INSERT INTO Reservation "
                           + "(vehicle, customer, startDate, "
                           + "endDate, realEndDate, info) "
                           + "VALUES(?,?,?,?,?,?)";               
        
        if (reservation == null)                 
            throw new IllegalArgumentException(bundle.getString("ExNullRes"));       //Reservation can not be null.            
                
        if (reservation.getId() != null)                
            throw new IllegalArgumentException(bundle.getString("ExResId"));      //("Reservation id can not be already assigned.");             
                        
        if (reservation.getCustomer() == null)                
            throw new IllegalArgumentException(bundle.getString("ExResCustomerNull"));  //("Reservation customer can not be null.");
        
        if (reservation.getCustomer().getId() == null)                
            throw new IllegalArgumentException(bundle.getString("ExResCustIdNull"));   //("Reservation customer id can not be null.");   
        
        if (reservation.getVehicle() == null)                
            throw new IllegalArgumentException(bundle.getString("ExResVehicleNull"));   //("Reservation vehicle can not be null."); 
        
        if (reservation.getVehicle().getId() == null)                
            throw new IllegalArgumentException(bundle.getString("ExResVehIdNull"));    //("Reservation vehicle id can not be null.");
        
        if (reservation.getStartDate() == null)                
            throw new IllegalArgumentException(bundle.getString("ExStartDateNull"));   //("Reservation starting date can not be null.");
              
        if(reservation.getStartDate().compareTo(reservation.getEndDate()) > 0)
            throw new IllegalArgumentException(bundle.getString("ExEndBeforeStart")); //("Ending date is set before starting date");
        
        if (reservation.getEndDate() == null)                
            throw new IllegalArgumentException(bundle.getString("ExEndDateNull"));  //("Reservation ending date can not be null.");
                    
        if (reservation.getRealEndDate() != null)                
            throw new IllegalArgumentException(bundle.getString("ExRealEndDateSet"));  //("Reservation real date can not be already set.");                                            
          
        List<Reservation> reservations = (ArrayList)getAllReservations();
        for(Reservation res : reservations) {
            if(res.getVehicle().equals(reservation.getVehicle())) {
                if (reservation.getStartDate().compareTo(res.getStartDate()) <= 0) {
                    if (reservation.getEndDate().compareTo(res.getStartDate()) < 0) {
                        //OK
                    } else {
                        throw new IllegalArgumentException(bundle.getString("ExExistingRes"));   //("There is already a reservation for this vehicle");
                    }
                } else {
                    if (reservation.getStartDate().compareTo(res.getEndDate()) >= 0) {
                        //OK
                    } else {
                        throw new IllegalArgumentException(bundle.getString("ExExistingRes"));   //("There is already a reservation for this vehicle");
                    }
                }
            }  
        }       
        
        PreparedStatement statement = null;
        Connection connection = null;                
        
        try {      
            
                connection = dataSource.getConnection();
                connection.setAutoCommit(false);

                statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);                                
                
                statement.setLong(1, reservation.getVehicle().getId());                                                                
                statement.setLong(2, reservation.getCustomer().getId());            

                Calendar startDate = reservation.getStartDate();            
                statement.setTimestamp(3, new Timestamp(startDate.getTime().getTime()), startDate);          

                Calendar endDate = reservation.getEndDate();
                statement.setTimestamp(4, new Timestamp(endDate.getTime().getTime()), endDate);                 

                Calendar realEndDate = reservation.getRealEndDate();
                statement.setTimestamp(5, null, endDate);
                
                statement.setString(6, reservation.getInfo());                                   
                int modified = statement.executeUpdate(); 

                String message;      
                switch (modified) {                
                    case 0  :   
                                message = bundle.getString("Entity") + " " + reservation.toString() + " " + bundle.getString("NotExisting");                            
                                throw new ServiceFailureException(message);                         
                    case 1  :        
                                ResultSet results = statement.getGeneratedKeys();                            
                                if (results.next()) {                                                                
                                    reservation.setId(results.getLong(1));                      
                                    message = bundle.getString("Entity") + " " + reservation.toString() + " " + bundle.getString("AddedInDB");
                                    connection.commit();
                                    logger.debug(message); 
                                }                            
                                break;
                    default :
                                message = reservation.toString() + ": " + bundle.getString("ExMultipleRows") + ".";                                                                     
                                throw new ServiceFailureException(message);                                   
                }     
                
        } catch (SQLException e) { 
            
                String message = bundle.getString("ExDbInsertErr1") + " " + 
                        reservation.toString() + " " + bundle.getString("ExDbInsertErr2") 
                        + " " + e.getMessage();
                logger.warn(message, e);
                throw new ServiceFailureException(message, e); 
            
        } finally {
            
                DBUtils.doRollbackQuietly(connection);
                DBUtils.closeQuietly(connection, statement);
            
        }           
    }

    @Override
    public void deleteReservation(Reservation reservation) {
        
        checkDataSource();        
        final String query = "DELETE FROM Reservation WHERE id=?";
        
        if (reservation == null)                 
            throw new IllegalArgumentException(bundle.getString("ExNullRes"));             
        
        if(reservation.getId() == null)
            throw new IllegalArgumentException(bundle.getString("ExResId"));  //("Reservation id can not be null.");
        
        PreparedStatement statement = null;
        Connection connection = null;        
        
        try {      
            
                connection = dataSource.getConnection();
                connection.setAutoCommit(false);

                statement = connection.prepareStatement(query);
                statement.setLong(1, reservation.getId());

                int modified = statement.executeUpdate();
                String message;

                switch (modified) {                
                    case 0  :   
                                message = bundle.getString("Entity") + " " + reservation.toString() + " " + bundle.getString("NotExisting");                                                       
                                throw new IllegalArgumentException(message);                         
                    case 1  :        
                                message = bundle.getString("Entity") + " " + reservation.toString() + " " + bundle.getString("RemovedFromDB");  
                                connection.commit();
                                logger.debug(message);                            
                                break;
                    default :
                                message = reservation.toString() + ": " + bundle.getString("ExMultipleRows") + ".";                                 
                                throw new ServiceFailureException(message);                                   
                }        
                
        } catch(SQLException e) {
            
                String message = reservation.toString() + ": " + 
                        bundle.getString("ExDeletingFromDb") + ": "  + e.getMessage();
                logger.warn(message, e);   
                throw new ServiceFailureException(message, e);  
                
        } finally {
            
                DBUtils.doRollbackQuietly(connection);
                DBUtils.closeQuietly(connection, statement);
                
        }
    }

    @Override
    public void updateReservation(Reservation reservation) {
        
        checkDataSource();                
        final String query = "UPDATE Reservation SET "
                           + "vehicle = ?, "
                           + "customer = ?, "
                           + "startDate = ?, "
                           + "endDate = ?, "
                           + "realEndDate = ?, "
                           + "info = ? "
                           + " WHERE Id = ?";
        
        if (reservation == null)
            throw new IllegalArgumentException(bundle.getString("ExNullRes"));
        
        if (reservation.getId() == null)                
            throw new IllegalArgumentException(bundle.getString("ExResId"));
        
        if (reservation.getCustomer() == null)                
            throw new IllegalArgumentException(bundle.getString("ExResCustomerNull")); 
        
        if (reservation.getCustomer().getId() == null)                
             throw new IllegalArgumentException(bundle.getString("ExResCustIdNull"));      
        
        if (reservation.getVehicle() == null)                
            throw new IllegalArgumentException(bundle.getString("ExResVehicleNull"));  
        
        if (reservation.getVehicle().getId() == null)                
            throw new IllegalArgumentException(bundle.getString("ExResVehIdNull"));                
      
        if (reservation.getStartDate() == null)                
            throw new IllegalArgumentException(bundle.getString("ExStartDateNull"));
        
        if(reservation.getStartDate().compareTo(reservation.getEndDate()) > 0)
            throw new IllegalArgumentException(bundle.getString("ExEndBeforeStart")); //("Ending date is set before starting date");              
                
        if (reservation.getEndDate() == null)                
            throw new IllegalArgumentException(bundle.getString("ExEndDateNull"));
        
        if(reservation.getRealEndDate() != null) {
            if(reservation.getStartDate().compareTo(reservation.getRealEndDate()) > 0)
                throw new IllegalArgumentException(bundle.getString("ExRealEndBeforeStart"));   //("Real ending date is set before starting date");
        }
                            
        PreparedStatement statement = null;  
        Connection connection = null;              
        
        try {
            
                connection = dataSource.getConnection();
                connection.setAutoCommit(false);

                statement = connection.prepareStatement(query);
                statement.setLong(1, reservation.getVehicle().getId());
                statement.setLong(2, reservation.getCustomer().getId());

                Timestamp timestamp = new Timestamp(reservation.getStartDate().getTimeInMillis());
                statement.setTimestamp(3, timestamp);

                timestamp = new Timestamp(reservation.getEndDate().getTimeInMillis());
                statement.setTimestamp(4, timestamp);

                if (reservation.getRealEndDate() != null) {
                    timestamp = new Timestamp(reservation.getRealEndDate().getTimeInMillis());
                    statement.setTimestamp(5, timestamp);
                } else {
                    statement.setTimestamp(5, null);
                }
                
                statement.setString(6, reservation.getInfo());
                statement.setLong(7, reservation.getId());                   

                int modified = statement.executeUpdate();
                String message; 

                switch (modified) {                
                    case 0  :   
                                message = bundle.getString("Entity") + " " + reservation.toString() + " " + bundle.getString("NotExisting");                       
                                throw new ServiceFailureException(message);                         
                    case 1  :        
                                message = bundle.getString("Entity") + " " + reservation.toString() + " " + bundle.getString("Updated");                                                  
                                connection.commit();
                                logger.debug(message); 
                                break;
                    default :
                                message = reservation.toString() + ": " + bundle.getString("ExMultipleRows") + ".";                                                                                
                                throw new ServiceFailureException(message);                                   
                }  
                
        } catch(SQLException e) {
            
                String message = bundle.getString("ExUpdatingDb") + ": " + reservation.getId() + " : " + e.getMessage();
                logger.warn(message, e);
                throw new ServiceFailureException(message, e);  
                
        } finally {
            
                DBUtils.doRollbackQuietly(connection);
                DBUtils.closeQuietly(connection, statement);
                
        }
    }

    @Override
    public Reservation getReservationById(Long id) {
        
        checkDataSource();
        final String query = "SELECT * FROM Reservation WHERE id = ?";                    
        
        if (id == null)            
            throw new IllegalArgumentException(bundle.getString("ExNullResId")); 
        
        if (id <= 0L)                
            throw new IllegalArgumentException(bundle.getString("ExResNegVal"));
                
        PreparedStatement statement = null;
        Connection connection = null;        
        
        try {
            
                connection = dataSource.getConnection();
                statement = connection.prepareStatement(query);
                statement.setLong(1, id);

                String message = null;  
                Reservation reservation = null;            
                ResultSet rset = statement.executeQuery();            

                if(rset.next()) {              
                    reservation = resultSetToReservation(rset);
                    if(rset.next()) {
                        throw new ServiceFailureException("Multiple entities with id : " + id + " retreived from database.");                    
                    }                                
                } 
                return reservation;
                
        } catch(SQLException e) {
            
                String message = "Error when getting entity with id : " + id + " from database.";
                logger.warn(message, e);
                throw new ServiceFailureException(message, e);   
              
        } finally {
            
                DBUtils.closeQuietly(connection, statement);
            
        }
    }

    @Override
    public Collection<Reservation> getReservationsByVehicle(Vehicle vehicle) {
        
        checkDataSource();
        final String query = "SELECT * "
                           + "FROM Reservation "
                           + "WHERE vehicle = ?";
        
        if (vehicle == null)                 
            throw new IllegalArgumentException("Vehicle can not be null.");            
        
        if (vehicle.getId() != null)                
            throw new IllegalArgumentException("Vehicle id can not be already assigned.");  
        
        PreparedStatement statement = null;
        Connection connection = null; 
        
        try {

                connection = dataSource.getConnection();
                statement = connection.prepareStatement(query);
                statement.setLong(1, vehicle.getId());

                String message = null;  
                List<Reservation> reservations = new ArrayList<>();            
                ResultSet rows = statement.executeQuery();            

                while (rows.next()) {
                    reservations.add(resultSetToReservation(rows));
                }              
                return reservations;
                
        } catch(SQLException e) {
            
                String message = "Error when getting all reservations for " + vehicle.toString() + " from database.";
                logger.warn(message, e);
                throw new ServiceFailureException(message, e);  
                
        } finally {
            
                DBUtils.closeQuietly(connection, statement);   
            
        }
    }

    @Override
    public Collection<Reservation> getReservationByCustomer(Customer customer) {
        
        checkDataSource();
        final String query = "SELECT * "
                           + "FROM Reservation "
                           + "WHERE customer = ?";
        
        if (customer == null)                 
            throw new IllegalArgumentException("Customer can not be null.");            
        
        if (customer.getId() != null)                
            throw new IllegalArgumentException("Customer id can not be already assigned.");  
        
        PreparedStatement statement = null;
        Connection connection = null; 
        
        try {
            
                connection = dataSource.getConnection();
                statement = connection.prepareStatement(query);
                statement.setLong(1, customer.getId());

                String message = null;  
                List<Reservation> reservations = new ArrayList<>();            
                ResultSet rows = statement.executeQuery();            

                while (rows.next()) {
                    reservations.add(resultSetToReservation(rows));
                }              
                return reservations;
                
        } catch(SQLException e) {
            
                String message = "Error when getting all reservations for " + customer.toString() + " from database.";
                logger.warn(message, e);
                throw new ServiceFailureException(message, e);  
                
        } finally {
            
                DBUtils.closeQuietly(connection, statement);    
                
        }
    }

    @Override
    public Collection<Reservation> getAllReservations() {
       
        checkDataSource();
        final String query = "SELECT * FROM Reservation";                
        
        PreparedStatement statement = null;
        Connection connection = null;
        
        try {
            
                connection = dataSource.getConnection();            
                statement = connection.prepareStatement(query);
                List<Reservation> reservations = new ArrayList<>();            
                ResultSet rows = statement.executeQuery();

                while(rows.next()) {
                    reservations.add(resultSetToReservation(rows));
                }
                return reservations;
                
        } catch (SQLException e) {
            
                String message = bundle.getString("ExGettingFromDb") + " : " + e.getMessage();
                logger.warn(message, e);
                throw new ServiceFailureException(message, e);
                
        } finally {
            
                DBUtils.closeQuietly(connection, statement);
                
        } 
    }

    @Override
    public Collection<Vehicle> getAvaibleVehicle() {
        
        checkDataSource();
        final String query = "SELECT * FROM Vehicle WHERE id NOT IN (SELECT vehicle FROM Reservation WHERE realEndDate IS NULL)"; 
        
        PreparedStatement statement = null;
        Connection connection = null;
        
         try {
             
                connection = dataSource.getConnection();            
                statement = connection.prepareStatement(query);

                List<Vehicle> vehicles = new ArrayList<>();            
                ResultSet rows = statement.executeQuery();

                while(rows.next()) {
                    vehicles.add(DBUtils.resultSetToVehicle(rows));
                }            
                return vehicles;
                
         } catch (SQLException e) {
             
                String message = "Error when getting all vehicles from database.";                
                logger.warn(message, e);
                throw new ServiceFailureException(message, e); 
            
        } finally {
             
                DBUtils.closeQuietly(connection, statement);
            
        }    
    }
      
    private Reservation resultSetToReservation(ResultSet row) throws SQLException {
        
        Reservation reservation = new Reservation();
        reservation.setId(row.getLong("id"));        
        reservation.setVehicle(vehicleManager.getVehicleById(row.getLong("vehicle")));
        reservation.setCustomer(customerManager.getCustomerById(row.getLong("customer")));
        
        Calendar startDate = new GregorianCalendar();        
        startDate.setTimeInMillis(row.getTimestamp("startDate").getTime());        
        reservation.setStartDate(startDate);
        
        Calendar endDate = new GregorianCalendar();        
        endDate.setTimeInMillis(row.getTimestamp("endDate").getTime());        
        reservation.setEndDate(endDate);
               
        if (row.getTimestamp("realEndDate") == null) {
            reservation.setRealEndDate(null);
        } else {
            Calendar realEndDate = new GregorianCalendar();
            realEndDate.setTimeInMillis(row.getTimestamp("realEndDate").getTime());
            reservation.setRealEndDate(realEndDate);
        }                       
                        
        reservation.setInfo(row.getString("info"));        
        return reservation;
    }   
}
