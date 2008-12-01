/**
 * 
 */
package eu.planets_project.ifr.core.security.api.model;

import java.security.NoSuchAlgorithmException;
import java.util.Set;

/**
 * This is the User entity bean definition.  
 * It defines a Planets User along with the mapping into the persistence layer.
 * 
 * <p>
 * Like the Hibernate code in the Admin component, we include the full data object in the API.
 * This is because it is not possible to abstract this into a pure interface due to the cross-table dependance upon the Role object.
 * Attempting to abstract the User fails because the User returns Role and can't return RoleImpl in their place.
 * Attempting to build up the persistance logic in a abstract interface is not supported by the persistance API.
 * </p>
 * 
 * @see eu.planets_project.ifr.core.security.api.services.UserManager
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public interface User {
    /**
     * @return the user id
     */
    public Long getId();

    /**
     * @return the version of the user
     */
    public Integer getVersion();

    /**
     * @return the username
     */
    public String getUsername();

    /**
     * @return the password
     */
    public String getPassword();

    /**
     * @return the users first name
     */
    public String getFirstName();

    /**
     * @return the users last name (surname)
     */
    public String getLastName();

    /**
     * @return the user's address
     */
    public Address getAddress();

    /**
     * @return the user's email address
     */
    public String getEmail();

    /**
     * @return
     */
    public String getPhoneNumber();

    /**
     * @return
     */
    public String getWebsite();

    /**
     * @return
     */
    public String getPasswordHint();

    /**
     * @return
     */
    public Set<Role> getRoles();

    /**
     * @return
     */
    public boolean getAccountEnabled();

    /**
     * @return
     */
    public boolean getAccountExpired();

    /**
     * @return
     */
    public boolean getAccountLocked();

    /**
     * @return
     */
    public boolean getCredentialsExpired();

    /**
     * @return
     */
    public String getFullName();

    /**
     * @return
     */
    public String confirmPassword();

    /**
     * @return
     */
    public String[] rolesAsStrings();

    /**
     * @return
     */
    public String firstRole();

    /**
     * @param firstrole
     */
    public void firstRole(String firstrole);

    /**
     * @param role
     */
    public void addRole(Role role);

    /**
     * @param role
     */
    public void removeRole(Role role);

    /**
     * @return
     */
    public boolean appliedAsProvider();

    /**
     * @param appliesAsProvider
     */
    public void applyingAsProvider(boolean appliesAsProvider);

    /**
     * @param id
     */
    public void setId(Long id);

    /**
     * @param username
     */
    public void setUsername(String username);

    /**
     * @param password
     */
    public void setPassword(String password);

    /**
     * This method takes the "natural" string password as entered by the user
     * and hashes it for the database.  The hash is a simple "unsalted" MD5 hash
     * so it's not particularly secure.
     * 
     * @param password	The entered password to be hashed
     * 
     * @throws NoSuchAlgorithmException	Ahould never happen while MD5 is the identifier
     */
    public void hashPassword(String password) throws NoSuchAlgorithmException;

    /**
     * @param confirmPassword
     */
    public void setConfirmPassword(String confirmPassword);

    /**
     * @param firstName
     */
    public void setFirstName(String firstName);
    
    /**
     * @param lastName
     */
    public void setLastName(String lastName);

    /**
     * @param address
     */
    public void setAddress(Address address);

    /**
     * @param email
     */
    public void setEmail(String email);

    /**
     * @param phoneNumber
     */
    public void setPhoneNumber(String phoneNumber);

    /**
     * @param website
     */
    public void setWebsite(String website);

    /**
     * @param passwordHint
     */
    public void setPasswordHint(String passwordHint);
    
    /**
     * @param roles
     */
    public void setRoles(Set<Role> roles);

    /**
     * @param version
     */
    public void setVersion(Integer version);

    /**
     * @param enabled
     */
    public void setAccountEnabled(boolean enabled);

    /**
     * @param accountExpired
     */
    public void setAccountExpired(boolean accountExpired);

    /**
     * @param accountLocked
     */
    public void setAccountLocked(boolean accountLocked);

    /**
     * @param credentialsExpired
     */
    public void setCredentialsExpired(boolean credentialsExpired);
}
