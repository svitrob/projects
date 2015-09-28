package cz.muni.fi.pv168.rent;

import java.util.Collection;

public interface ReservationManager {
    
    public void createReservation(Reservation reservation);
    
    public void deleteReservation(Reservation reservation);
    
    public void updateReservation(Reservation reservation);
    
    public Reservation getReservationById(Long id);
    
    public Collection<Reservation> getReservationsByVehicle(Vehicle vehicle);
    
    public Collection<Reservation> getReservationByCustomer(Customer customer);
    
    public Collection<Reservation> getAllReservations();
    
    public Collection<Vehicle> getAvaibleVehicle();
}
