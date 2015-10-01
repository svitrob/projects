package cz.muni.fi.pv168.rent;

import java.math.BigDecimal;
import java.util.Objects;

public final class Vehicle {

    public static enum Brand {
        SEAT, SKODA, RENAULT, 
        AUDI, FORD, VOLKSWAGEN,         
    }        

    public Vehicle() {
    }        
    
    public Vehicle(Long id, BigDecimal price, Brand brand) {
        this.id = id;
        this.price = price;
        this.brand = brand;
    }
            
    private Long id;
    private BigDecimal price;  
    private Brand brand;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }       

    @Override
    public String toString() {
        return "Vehicle{" + "id=" + id + ", price=" + price + ", brand=" + brand + '}';
    }        

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.id);
        hash = 67 * hash + Objects.hashCode(this.price);
        hash = 67 * hash + Objects.hashCode(this.brand);
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
        final Vehicle other = (Vehicle) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (this.price.compareTo(other.price) != 0) {
            return false;
        }     
        if (this.brand != other.brand) {
            return false;
        }
        return true;
    }        
}
