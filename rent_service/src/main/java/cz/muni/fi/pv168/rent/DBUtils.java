package cz.muni.fi.pv168.rent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Set;
import javax.sql.DataSource;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.slf4j.LoggerFactory;

public final class DBUtils {
     
    public final static org.slf4j.Logger logger = 
            LoggerFactory.getLogger("cz.muni.fi.pv168.rent.Error");   
    
    public static DataSource getDataSource() {                   
        BasicDataSource dataSource = new BasicDataSource();     
        dataSource.setUrl("jdbc:derby:memory:vehiclemgr-test;create=true");                
        return dataSource;                                                  
    }
    
    public static DataSource getDataSource(String resourceName) {           
                
        BasicDataSource dataSource = null;
                           
        try {
            Properties connectionProperties = loadProperties(resourceName);
            Set<String> properties = connectionProperties.stringPropertyNames();
            for (String key : new String[] { "DatabaseClass", "DatabaseURL", "Username", "Password" })
                if (!properties.contains(key))
                    throw new IllegalArgumentException("Property file does not contain required key : " + key);  
            
            dataSource = new BasicDataSource();
            dataSource.setDriverClassName(connectionProperties.getProperty("DatabaseClass"));               
            dataSource.setUrl(connectionProperties.getProperty("DatabaseURL"));           
            dataSource.setUsername(connectionProperties.getProperty("Username"));       
            dataSource.setPassword(connectionProperties.getProperty("Password"));    
            
        } catch (IllegalArgumentException e) {
            logger.warn("Can not create data source object : {}", e.getMessage(), e);        
        }                          
        return dataSource;
    }
    
    public static Properties loadProperties(String resourceName) {        
        if (resourceName == null || resourceName.length() == 0)
            throw new IllegalArgumentException("PResource name cannot be empty or null.");
                   
        Properties connectionProperties = null;    
        
        try {
            ClassLoader loader = DBUtils.class.getClassLoader();
            InputStream resourceStream = loader.getResourceAsStream(resourceName);                                            
            connectionProperties = new Properties();
            connectionProperties.load(resourceStream);                                                    
        } catch (IOException | SecurityException e) {
            logger.warn("Error opening {} properties {}", resourceName, e.getMessage(), e);                
        }
        return connectionProperties;
    }    
    
    public static URL loadSqlScript(String resourceName) {        
        if (resourceName == null || resourceName.length() == 0)
            throw new IllegalArgumentException("Resource name cannot be empty or null.");
                   
        URL script = null;        
        try {            
            script = DBUtils.class.getResource(resourceName);                                                           
        } catch (SecurityException e) {
            logger.warn("Error opening {} properties {}", resourceName, e.getMessage(), e);
        }
        System.out.println(script);
        return script;
    }                                 
           
    public static Properties createProperties(String fileName) {
        Properties properties = new Properties();	
 
	try {OutputStream output = new FileOutputStream(fileName); 
        
		properties.setProperty("DatabaseClass", "org.apache.derby.jdbc.ClientDriver");
                properties.setProperty("DatabaseURL","jdbc:derby://localhost:1527/RentServiceDB");                        
                properties.setProperty("Username", "xpastor1");        
                properties.setProperty("Password", "pv168");  		
		properties.store(output, null);
                
	} catch (IOException e) {
		logger.warn("Error creating {} properties : {}", fileName, e.getMessage(), e);
	}         
        return properties;
    }        
    
    /**
     * Close a <code>Connection</code>, avoid closing if null.
     *
     * @param conn Connection to close.
     * @throws SQLException if a database access error occurs
     */
    public static void close(Connection conn) throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    /**
     * Close a <code>ResultSet</code>, avoid closing if null.
     *
     * @param rs ResultSet to close.
     * @throws SQLException if a database access error occurs
     */
    public static void close(ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }
    }

    /**
     * Close a <code>Statement</code>, avoid closing if null.
     *
     * @param stmt Statement to close.
     * @throws SQLException if a database access error occurs
     */
    public static void close(Statement stmt) throws SQLException {
        if (stmt != null) {
            stmt.close();
        }
    }
    
    /**
     * Close a <code>Connection</code>, <code>Statement</code> and
     * <code>ResultSet</code>.  Avoid closing if null and hide any
     * SQLExceptions that occur.
     *
     * @param conn Connection to close.
     * @param stmt Statement to close.
     * @param rs ResultSet to close.
     */
    public static void closeQuietly(Connection conn, Statement stmt, ResultSet rs) {

        try {
            closeQuietly(rs);
        } finally {
            try {
                closeQuietly(stmt);
            } finally {
                closeQuietly(conn);
            }
        }

    }

    /**
     * Close a <code>ResultSet</code>, avoid closing if null and hide any
     * SQLExceptions that occur.
     *
     * @param rs ResultSet to close.
     */
    public static void closeQuietly(ResultSet rs) {
        try {
            close(rs);
        } catch (SQLException e) { // NOPMD
            logger.warn("Error when closing result set.", e);            
            // quiet
        }
    }

    /**
     * Close a <code>Statement</code>, avoid closing if null and hide
     * any SQLExceptions that occur.
     *
     * @param stmt Statement to close.
     */
    public static void closeQuietly(Statement stmt) {
        try {
            close(stmt);
        } catch (SQLException e) { // NOPMD
            logger.warn("Error when closing statement.", e);            
            // quiet
        }
    }
    
    /**
     * Close a <code>Connection</code>, avoid closing if null and hide
     * any SQLExceptions that occur.
     *
     * @param conn Connection to close.
     */
    public static void closeQuietly(Connection conn) {
        try {
            close(conn);
        } catch (SQLException e) { // NOPMD    
            logger.warn("Error when closing connection.", e);               
            // quiet
        }
    }
    
    /**
     * Commits a <code>Connection</code> then closes it, avoid closing if null.
     *
     * @param conn Connection to close.
     * @throws SQLException if a database access error occurs
     */
    public static void commitAndClose(Connection conn) throws SQLException {
        if (conn != null) {
            try {
                conn.commit();
            } finally {
                conn.close();
            }
        }
    }

    /**
     * Commits a <code>Connection</code> then closes it, avoid closing if null
     * and hide any SQLExceptions that occur.
     *
     * @param conn Connection to close.
     */
    public static void commitAndCloseQuietly(Connection conn) {
        try {
            commitAndClose(conn);
        } catch (SQLException e) { // NOPMD
            logger.warn("Error when closing connection.", e);            
            // quiet
        }
    }
    
    /**
     * Rollback any changes made on the given connection.
     * @param conn Connection to rollback.  A null value is legal.
     * @throws SQLException if a database access error occurs
     */
    public static void rollback(Connection conn) throws SQLException {
        if (conn != null) {
            conn.rollback();
        }
    }

    /**
     * Performs a rollback on the <code>Connection</code> then closes it,
     * avoid closing if null.
     *
     * @param conn Connection to rollback.  A null value is legal.
     * @throws SQLException if a database access error occurs
     * @since DbUtils 1.1
     */
    public static void rollbackAndClose(Connection conn) throws SQLException {
        if (conn != null) {
            try {
                conn.rollback();
            } finally {
                conn.close();
            }
        }
    }

    /**
     * Performs a rollback on the <code>Connection</code> then closes it,
     * avoid closing if null and hide any SQLExceptions that occur.
     *
     * @param conn Connection to rollback.  A null value is legal.
     * @since DbUtils 1.1
     */
    public static void rollbackAndCloseQuietly(Connection conn) {
        try {
            rollbackAndClose(conn);
        } catch (SQLException e) { // NOPMD
            logger.warn("Error when rollbacking.", e);            
            // quiet
        }
    }
    
    public static void doRollbackQuietly(Connection conn) {
        if (conn != null) {
            try {
                if (conn.getAutoCommit()) {
                    throw new IllegalStateException("Connection is in the autocommit mode!");
                }
                conn.rollback();
            } catch (SQLException e) {
                logger.warn("Error when doing rollback", e);  
            }
        }
    }
                
    public static void closeQuietly(Connection conn, Statement ... statements) {
        for (Statement st : statements) {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    logger.warn("Error when closing statement", e);       
                }                
            }
        }        
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                logger.warn("Error when switching autocommit mode back to true.", e);                  
            }
            try {
                conn.close();
            } catch (SQLException e) {
                logger.warn("Error when closing connection", e);                
            }
        }
    }                  
        
    public static void executeSqlScript(DataSource dataSource, URL script) {
        
        if (script == null)
            throw new IllegalArgumentException("Script can not be null.");
        
        Connection connection = null;
        try {            
                connection = dataSource.getConnection();
                for (String sqlStatement : readSqlStatements(script))
                    if (!sqlStatement.trim().isEmpty())
                        connection.prepareStatement(sqlStatement).executeUpdate();                                        
                        
        } catch (SQLException e) {       
                logger.warn("Error execution script : {}" + script, e);               
        } finally {
                closeQuietly(connection);
        }
    }
     
    public static String[] readSqlStatements(URL url) {
        try {
            char buffer[] = new char[256];
            StringBuilder result = new StringBuilder();
            InputStreamReader reader = new InputStreamReader(url.openStream(), "UTF-8");
            while (true) {
                int count = reader.read(buffer);
                if (count < 0) {
                    break;
                }
                result.append(buffer, 0, count);
            }
            return result.toString().split(";");
        } catch (IOException e) {
            logger.warn("Error reading resource : {}", url, e);  
            throw new RuntimeException("Cannot read " + url, e);
        }
    }
    
    public static Customer resultSetToCustomer(ResultSet row) throws SQLException {
        Customer customer = new Customer();
        customer.setId(row.getLong("id"));
        customer.setName(row.getString("name"));
        customer.setAddress(row.getString("address"));
        customer.setPhone(row.getString("phone"));
        customer.setEmail(row.getString("email"));        
        return customer;
    }
    
    public static Vehicle resultSetToVehicle(ResultSet row) throws SQLException {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(row.getLong("id"));
        vehicle.setBrand(Vehicle.Brand.valueOf(row.getString("brand").toUpperCase()));        
        vehicle.setPrice(row.getBigDecimal("price"));                 
        return vehicle;
    }              
}
