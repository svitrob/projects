package cz.muni.fi.pv168.rent;

import java.util.Collection;

public interface VehicleManager {
    
    public void createVehicle(Vehicle vehicle);   
    
    public void deleteVehicle(Vehicle vehicle);
    
    public void updateVehicle(Vehicle vehicle);
    
    public Vehicle getVehicleById(Long id);
    
    public Collection<Vehicle> getAllVehicles();        
}
