package cz.muni.fi.pv168.rent;

import java.util.Collection;

public interface CustomerManager {
    
    public void createCustomer(Customer customer);
    
    public void deleteCustomer(Customer customer);
    
    public void updateCustomer(Customer customer);
    
    public Customer getCustomerById(Long id);
    
    public Collection<Customer> getAllCustomers();
}
