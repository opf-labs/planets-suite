package eu.planets_project.ifr.core.security.impl.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Column;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import eu.planets_project.ifr.core.security.api.model.Address;

/**
 * This is the Address entity bean definition.
 * It is embedded inside the User entity.
 * 
 * @see eu.planets_project.ifr.core.security.api.services.UserManager
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@Embeddable
public class AddressImpl implements Address, Serializable {
    private static final long serialVersionUID = 3617859655330969141L;
    private String address;
    private String city;
    private String province;
    private String country;
    private String postalCode;

    /**
     * Default no-arg constructor
     */
    public AddressImpl() {
    	
    }
    
    /**
     * 
     * @param address
     */
    public AddressImpl(Address address) {
        if( address != null ) {
    	this.address = address.getAddress();
    	this.city = address.getCity();
    	this.province = address.getProvince();
    	this.country = address.getCountry();
    	this.postalCode = address.getPostalCode();
        }
    }
    
    /**
     * Get the first portion of the full address.
     * @return The first portion of the full address, e.g. number and street name.
     */
    @Column(length=150, nullable=true)
    public String getAddress() {
        return address;
    }

    /**
     * Get the name of the city.
     * @return The city name.
     */
    @Column(length=50, nullable=true)
    public String getCity() {
        return city;
    }

    /**
     * Get the name of the province.
     * @return The province.
     */
    @Column(length=100)
    public String getProvince() {
        return province;
    }

    /**
     * Get the name of the country.
     * @return The country name.
     */
    @Column(length=100)
    public String getCountry() {
        return country;
    }

    /**
     * Get the post code.
     * @return The postal code.
     */
    @Column(name="postal_code", length=15, nullable=true)
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Set the first line of the address.
     * @param address e.g. "42 Leopard Lane"
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Set the city.
     * @param city e.g. "Stoke-on-Trent"
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Set the country.
     * @param country e.g. "Bolivia"
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Set the post code.
     * @param postalCode e.g. "LS18 2GS"
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Set the Province.
     * @param province e.g. "West Yorkshire"
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AddressImpl)) return false;

        final AddressImpl address1 = (AddressImpl) o;

        if (address != null ? !address.equals(address1.getAddress()) : address1.getAddress() != null) return false;
        if (city != null ? !city.equals(address1.getCity()) : address1.getCity() != null) return false;
        if (country != null ? !country.equals(address1.getCountry()) : address1.getCountry() != null) return false;
        if (postalCode != null ? !postalCode.equals(address1.getPostalCode()) : address1.getPostalCode() != null) return false;
        if (province != null ? !province.equals(address1.getProvince()) : address1.getProvince() != null) return false;

        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int result;
        result = (address != null ? address.hashCode() : 0);
        result = 29 * result + (city != null ? city.hashCode() : 0);
        result = 29 * result + (province != null ? province.hashCode() : 0);
        result = 29 * result + (country != null ? country.hashCode() : 0);
        result = 29 * result + (postalCode != null ? postalCode.hashCode() : 0);
        return result;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        ToStringBuilder sb =  new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
        sb.append("country", this.country);
        sb.append("address", this.address);
        sb.append("province", this.province);
        sb.append("postalCode", this.postalCode);
        sb.append("city", this.city);
        return sb.toString();
    }

}
