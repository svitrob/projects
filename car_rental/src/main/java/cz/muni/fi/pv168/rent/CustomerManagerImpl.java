package cz.muni.fi.pv168.rent;

import cz.muni.fi.pv168.common.InternalIntegrityException;
import cz.muni.fi.pv168.common.ServiceFailureException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;


public final class CustomerManagerImpl implements CustomerManager {       
  
    public final static Logger logger = 
            LoggerFactory.getLogger(CustomerManagerImpl.class.getName());
    
    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("Strings");
    
    private DataSource dataSource;
    
    public CustomerManagerImpl(DataSource dataSource) {
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
    public void createCustomer(Customer customer) {
        
        checkDataSource();        
        final String query = "INSERT INTO Customer (Name, Address, Phone, Email)" +
                             "VALUES (?, ?, ?, ?)";
        
        if(customer == null)
            throw new IllegalArgumentException(bundle.getString("ExNullCustomer"));  //("Customer can not be null.");
        
        if(customer.getId() != null)
            throw new IllegalArgumentException(bundle.getString("ExCustomerId"));    //("Customer id can not be already assigned.");
        
        if(customer.getName() == null)
            throw new IllegalArgumentException(bundle.getString("ExEmptyCustomerName"));  //("Customer name can not be null.");
        
        if(customer.getName().isEmpty())
            throw new IllegalArgumentException(bundle.getString("ExEmptyCustomerName"));  //("Customer name can not be empty.");
        
        if(customer.getAddress() == null)
            throw new IllegalArgumentException(bundle.getString("ExEmptyCustomerAddress"));//("Customer address can not be null.");
        
        if(customer.getAddress().isEmpty())
            throw new IllegalArgumentException(bundle.getString("ExEmptyCustomerAddress"));//("Customer address can not be empty.");
                
        if(customer.getPhone() == null)
            throw new IllegalArgumentException(bundle.getString("ExEmptyCustomerPhone"));//("Customer phone can not be null.");
        
        if(customer.getPhone().isEmpty())
            throw new IllegalArgumentException(bundle.getString("ExEmptyCustomerPhone"));//("Customer phone can not be empty.");
        
        if(customer.getEmail() == null)
            throw new IllegalArgumentException(bundle.getString("ExEmptyCustomerEmail"));//("Customer email can not be null.");
        
        if(customer.getEmail().isEmpty())
            throw new IllegalArgumentException(bundle.getString("ExEmptyCustomerEmail"));//("Customer email can not be empty.");
                
        Connection connection = null;        
        PreparedStatement statement = null;
        ResultSet rset = null;
        
        try {
            
                connection = dataSource.getConnection();
                connection.setAutoCommit(false);            
                statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);      
                statement.setString(1, customer.getName());
                statement.setString(2, customer.getAddress());
                statement.setString(3, customer.getPhone());
                statement.setString(4, customer.getEmail());            
                int modified = statement.executeUpdate();

                String message;            
                switch (modified) {                
                    case 0  :   
                                message = bundle.getString("Entity") + " " + customer.toString() + " " + bundle.getString("NotExisting") + ".";                                
                                throw new ServiceFailureException(message);                         
                    case 1  :        
                                rset = statement.getGeneratedKeys();                            
                                if (rset.next()) {                             
                                    customer.setId(rset.getLong(1));  
                                    message = bundle.getString("Entity") + " " + customer.toString() + " " + bundle.getString("AddedInDB") + ".";                                    
                                    connection.commit();
                                    logger.debug(message);                   
                                }                            
                                break;
                    default :   
                                message = customer.toString() + ": " + bundle.getString("ExMultipleRows") + ".";                                                   
                                throw new ServiceFailureException(message);                                   
                }      
                connection.setAutoCommit(true);
            
        } catch (SQLException e) {          
            
                String message = bundle.getString("ExDbInsertErr1") + " " + customer.toString() + " " + bundle.getString("ExDbInsertErr2") + " " + e.getMessage();                
                logger.warn(message, e);                
                throw new ServiceFailureException(message, e);  
                
        } finally {
            
                DBUtils.closeQuietly(rset);
                DBUtils.closeQuietly(statement);             
                DBUtils.rollbackAndCloseQuietly(connection);
            
        }
    }
    
    @Override
    public void deleteCustomer(Customer customer) {
        
        checkDataSource();        
        final String query = "DELETE FROM Customer WHERE Id = ?";
        
        if (customer == null)                 
            throw new IllegalArgumentException(bundle.getString("ExNullCustomer"));                      
        
        if(customer.getId() == null)
            throw new IllegalArgumentException(bundle.getString("ExCustomerNullId"));//("Customer id can not be null.");
                
        if (customer.getId() <= 0L)                
            throw new IllegalArgumentException(bundle.getString("ExCustomerNonPositive"));//("Customer id can not be negative nor zero."); 
        
        Connection connection = null;        
        PreparedStatement statement = null;
        
        try {     
            
                connection = dataSource.getConnection();
                connection.setAutoCommit(false);            
                statement = connection.prepareStatement(query);
                statement.setLong(1, customer.getId());            
                int modified = statement.executeUpdate();

                String message;            
                switch (modified) {                
                    case 0  :   
                                message = bundle.getString("Entity") + " " + customer.toString() + " " + bundle.getString("NotExisting") + ".";                                                    
                                throw new IllegalArgumentException(message);                         
                    case 1  :        
                                message = bundle.getString("Entity") + " " + customer.toString() + " " + bundle.getString("RemovedFromDB") + ".";                                
                                connection.commit();
                                logger.debug(message);                                                 
                                break;
                    default :
                                message = customer.toString() + ": " + bundle.getString("ExMultipleRows") + ".";                                              
                                throw new ServiceFailureException(message);                                   
                }   
                connection.setAutoCommit(true);
                
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            
                String message = bundle.getString("ExDeletingFromDb");                
                logger.warn(message, e);   
                throw new InternalIntegrityException(message, e); 
                
        } catch(SQLException e) {
            
                String message = customer.toString() + ": " + bundle.getString("ExDeletingFromDb") + ": " + e.getMessage();                
                logger.warn(message, e);               
                throw new ServiceFailureException(message, e); 
            
        } finally {
            
                DBUtils.closeQuietly(statement);             
                DBUtils.rollbackAndCloseQuietly(connection); 
            
        }
    }
        
    @Override
    public void updateCustomer(Customer customer) {
        
        checkDataSource();                
        final String query = "UPDATE Customer "
                           + "SET Name = ?, Address = ?, Phone = ?, Email = ? "
                           + "WHERE Id = ?";
        
        if (customer == null)
            throw new IllegalArgumentException("Customer can not be null.");
        
        if (customer.getId() == null)                
            throw new IllegalArgumentException("Customer id can not be null.");
        
        if (customer.getId() <= 0L)                
            throw new IllegalArgumentException("Customer id can not be negative nor zero.");
        
        if(customer.getName() == null)
            throw new IllegalArgumentException("Customer name can not be null.");
        
        if(customer.getName().isEmpty())
            throw new IllegalArgumentException("Customer name can not be empty.");
        
        if(customer.getAddress() == null)
            throw new IllegalArgumentException("Customer address can not be null.");
        
        if(customer.getAddress().isEmpty())
            throw new IllegalArgumentException("Customer address can not be empty.");
        
        if(customer.getPhone() == null)
            throw new IllegalArgumentException("Customer phone can not be null.");
        
        if(customer.getPhone().isEmpty())
            throw new IllegalArgumentException("Customer phone can not be empty.");
        
        if(customer.getEmail() == null)
            throw new IllegalArgumentException("Customer email can not be null.");
        
        if(customer.getEmail().isEmpty())
            throw new IllegalArgumentException("Customer email can not be empty.");
                
        Connection connection = null;              
        PreparedStatement statement = null;  
        
        try {
            
                connection = dataSource.getConnection();
                connection.setAutoCommit(false);            
                statement = connection.prepareStatement(query);
                statement.setString(1, customer.getName());
                statement.setString(2, customer.getAddress());
                statement.setString(3, customer.getPhone());
                statement.setString(4, customer.getEmail());
                statement.setLong(5, customer.getId());            
                int modified = statement.executeUpdate();

                String message;             
                 switch (modified) {                
                    case 0  :   
                                message = bundle.getString("Entity") + " " + customer.toString() + " " + bundle.getString("NotExisting") + ".";                                  
                                throw new ServiceFailureException(message);                         
                    case 1  :      
                                message = bundle.getString("Entity") + " " + customer.toString() + " " + bundle.getString("Updated");                                                             
                                connection.commit();
                                logger.debug(message);                            
                                break;
                    default :
                                message = customer.toString() + ": " + bundle.getString("ExMultipleRows") + ".";     
                                throw new ServiceFailureException(message);                                   
                }                 
                connection.setAutoCommit(true);
            
        } catch(SQLException e) {
            
                String message = bundle.getString("ExUpdatingDb") + ": " + customer.getId() + " : " + e.getMessage();  
                logger.warn(message, e);                
                throw new ServiceFailureException(message, e);    

        } finally {
            
                DBUtils.closeQuietly(statement);             
                DBUtils.rollbackAndCloseQuietly(connection); 
            
        }
    }
    
    @Override
    public Customer getCustomerById(Long id) {                
        
        checkDataSource();
        final String query = "SELECT * FROM Customer WHERE Id = ?";                                 
        
        if (id == null)            
            throw new IllegalArgumentException("Customer id can not be null.");            
        
        if (id <= 0L)                
            throw new IllegalArgumentException("Customer can not have negatice or zero id.");   
                        
        Connection connection = null;   
        PreparedStatement statement = null;
        ResultSet rset = null;
        
        try {
            
                connection = dataSource.getConnection();
                statement = connection.prepareStatement(query);
                statement.setLong(1, id);
                rset = statement.executeQuery(); 

                String message;
                Customer customer = null;                                                
                if(rset.next()) {      
                    customer = DBUtils.resultSetToCustomer(rset);                                 
                    if(rset.next()) {
                        message = "Multiple entities with id : " + id + " retreived from database.";
                        throw new ServiceFailureException(message);                    
                    }                                
                } 
                return customer;
                
        } catch(SQLException e) {
            
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
    public Collection<Customer> getAllCustomers() {
        
        checkDataSource();
        final String query = "SELECT * FROM Customer";                
                
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        
        try {
            
                connection = dataSource.getConnection();            
                statement = connection.prepareStatement(query);            
                rset = statement.executeQuery();   
                List<Customer> customers = new ArrayList<>();
                while(rset.next()) {                
                    customers.add(DBUtils.resultSetToCustomer(rset));
                }                                                  
                return customers;  
                
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
