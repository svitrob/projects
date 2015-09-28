package cz.muni.fi.pv168.rent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class Reservation {
    
    private Long id;
    private Vehicle vehicle;
    private Customer customer;
    private Calendar startDate;
    private Calendar endDate;
    private Calendar realEndDate;
    private String info;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public Calendar getRealEndDate() {
        return realEndDate;
    }

    public void setRealEndDate(Calendar realEndDate) {
        this.realEndDate = realEndDate;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.id);
        hash = 53 * hash + Objects.hashCode(this.vehicle);
        hash = 53 * hash + Objects.hashCode(this.customer);
        hash = 53 * hash + Objects.hashCode(this.startDate);
        hash = 53 * hash + Objects.hashCode(this.endDate);
        hash = 53 * hash + Objects.hashCode(this.info);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Reservation other = (Reservation) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.vehicle, other.vehicle)) {
            return false;
        }
        if (!Objects.equals(this.customer, other.customer)) {
            return false;
        }
        
        if (this.startDate.compareTo(other.startDate) != 0) {
            return false;
        }     
        if (this.endDate.compareTo(other.endDate) != 0) {
            return false;
        }  
        if(this.realEndDate == null) {
            if(other.realEndDate != null)
                return false;
        } else { 
            if (this.realEndDate.compareTo(other.realEndDate) != 0)
                return false;
        }     
        /*
        if(this.startDate.getTimeInMillis() != other.startDate.getTimeInMillis())
            return false;
        if(this.endDate.getTimeInMillis() != other.endDate.getTimeInMillis())
            return false;
        if(this.getRealEndDate() != null) {
            if(other.getRealEndDate() != null) {
                if(this.endDate.getTimeInMillis() != other.endDate.getTimeInMillis()) {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            if(other.getRealEndDate() == null)
                return false;
        }
        */
        return true;
    }            
    
    @Override
    public String toString() {    
        return getId() + ", " + getVehicle() + ", " + getCustomer() + ", " + FormatCalendar(getStartDate())
                + ", " + FormatCalendar(getEndDate()) + ", " + FormatCalendar(getRealEndDate()) + ", " + getInfo();
    }
    
    private static String FormatCalendar(Calendar cal) { 
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return (cal != null) 
                ? sdf.format(cal.getTime())
                : null;
    }
}
