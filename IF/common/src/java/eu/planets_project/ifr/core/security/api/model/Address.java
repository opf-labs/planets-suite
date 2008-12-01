/**
 * 
 */
package eu.planets_project.ifr.core.security.api.model;

/**
 * This is the Address entity bean interface definition.
 * 
 * @see eu.planets_project.ifr.core.security.api.services.UserManager
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public interface Address {

    /**
     * Get the first portion of the full address.
     * @return The first portion of the full address, e.g. number and street name.
     */
    public String getAddress();

    /**
     * Get the name of the city.
     * @return The city name.
     */
    public String getCity();

    /**
     * Get the name of the province.
     * @return The province.
     */
    public String getProvince();

    /**
     * Get the name of the country.
     * @return The country name.
     */
    public String getCountry();

    /**
     * Get the post code.
     * @return The postal code.
     */
    public String getPostalCode();

    /**
     * Set the first line of the address.
     * @param address e.g. "42 Leopard Lane"
     */
    public void setAddress(String address);

    /**
     * Set the city.
     * @param city e.g. "Stoke-on-Trent"
     */
    public void setCity(String city);

    /**
     * Set the country.
     * @param country e.g. "Bolivia"
     */
    public void setCountry(String country);

    /**
     * Set the post code.
     * @param postalCode e.g. "LS18 2GS"
     */
    public void setPostalCode(String postalCode);

    /**
     * Set the Province.
     * @param province e.g. "West Yorkshire"
     */
    public void setProvince(String province);
}
