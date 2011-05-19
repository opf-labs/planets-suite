package eu.planets_project.ifr.core.security.impl.model;

import eu.planets_project.ifr.core.security.api.model.Address;
import eu.planets_project.ifr.core.security.api.model.Role;
import eu.planets_project.ifr.core.security.api.model.User;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.jboss.security.Util;


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
 * @see eu.planets_project.ifr.core.security.api.model.User
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@Entity
@Table(name = "app_user")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class SelfUserImpl implements User, Serializable {
    private static final long serialVersionUID = 3832626162173359411L;
	@SuppressWarnings("unused")
    private static Logger log = Logger.getLogger(SelfUserImpl.class.getName());

    private Long id = null;
    private String username; // required
    private String password; // required
    private String confirmPassword;
    private String firstName; // required
    private String lastName; // required
    private SelfAddressImpl address;
    private String phoneNumber;
    private String email; // required; unique
    private String website;
    private String passwordHint;
    private Integer version;
    private Set<Role> roles = new HashSet<Role>();
    private boolean enabled;
    private boolean accountExpired;
    private boolean accountLocked;
    private boolean credentialsExpired;
    private boolean appliesAsProvider = false;
    /**
     * no arg default.
     */
    public SelfUserImpl() {
    }

    /**
     * Basic constructor sets username only.
     * @param username used to set username property
     */
    public SelfUserImpl(String username) {
       this.username = username;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#getId()
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    public Long getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#getVersion()
     */
    @Version
    public Integer getVersion() {
        return version;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#getUsername()
     */
    @Column(name = "username", length = 50, nullable = false, unique = true)
    public String getUsername() {
        return username;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#getPassword()
     */
    @Column(name = "password", nullable = false)
    public String getPassword() {
        return password;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#getFirstName()
     */
    @Column(name = "first_name", length = 50, nullable = false)
    public String getFirstName() {
        return firstName;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#getLastName()
     */
    @Column(name = "last_name", length = 50, nullable = false)
    public String getLastName() {
        return lastName;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#getAddress()
     */
    @Embedded
    public Address getAddress() {
        return address;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#getEmail()
     */
    @Column(name = "email", nullable = false, unique = true)
    public String getEmail() {
        return email;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#getPhoneNumber()
     */
    @Column(name = "phone_number", nullable = true)
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#getWebsite()
     */
    @Column(name = "website", nullable = true)
    public String getWebsite() {
        return website;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#getPasswordHint()
     */
    public String getPasswordHint() {
        return passwordHint;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#getRoles()
     */
    @ManyToMany(
    		targetEntity=eu.planets_project.ifr.core.security.impl.model.SelfRoleImpl.class,
    		cascade={ CascadeType.PERSIST, CascadeType.MERGE },
    		fetch=FetchType.EAGER)
    @JoinTable(
    		name = "user_role",
    		joinColumns = { @JoinColumn(name = "user_id") },
    		inverseJoinColumns={@JoinColumn(name="role_id")})
    public Set<Role> getRoles() {
        return roles;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#getAccountEnabled()
     */
    @Column(name = "account_enabled", nullable = false)
    public boolean getAccountEnabled() {
        return enabled;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#getAccountExpired()
     */
    @Column(name = "account_expired", nullable = false)
    public boolean getAccountExpired() {
        return accountExpired;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#getAccountLocked()
     */
    @Column(name = "account_locked", nullable = false)
    public boolean getAccountLocked() {
        return accountLocked;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#getCredentialsExpired()
     */
    @Column(name = "credentials_expired", nullable = false)
    public boolean getCredentialsExpired() {
        return credentialsExpired;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#getFullName()
     */
    @Transient
    public String getFullName() {
        return firstName + ' ' + lastName;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#confirmPassword()
     */
    public String confirmPassword() {
        return confirmPassword;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#rolesAsStrings()
     */
    public String[] rolesAsStrings() {
        String[] userRoles = new String[getRoles().size()];
    
        int i = 0;
    
        if (userRoles.length > 0) {
            for (Iterator<Role> it = getRoles().iterator(); it.hasNext();) {
                Role role = it.next();
    
                userRoles[i] = role.getName();
                i++;
            }
        }
    
        if (userRoles.length == 0) {
            userRoles = new String[1];
            userRoles[0] = "user";
        }
    
        return userRoles;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#firstRole()
     */
    public String firstRole() {
        if (!getRoles().isEmpty()) {
            return (getRoles().iterator().next()).getName();
        } else
            return "norole";// TODO ANJ: Should this return NULL?
    
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#firstRole(java.lang.String)
     */
    public void firstRole(String firstrole) {
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#addRole(eu.planets_project.ifr.core.security.api.model.Role)
     */
    public void addRole(Role role) {
        roles.add(role);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#removeRole(eu.planets_project.ifr.core.security.api.model.Role)
     */
    public void removeRole(Role role) {
        roles.remove(role);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#appliedAsProvider()
     */
    public boolean appliedAsProvider() {
        return appliesAsProvider;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#applyingAsProvider(boolean)
     */
    public void applyingAsProvider(boolean appliesAsProvider) {
        this.appliesAsProvider = appliesAsProvider;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#setId(java.lang.Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#setUsername(java.lang.String)
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#setPassword(java.lang.String)
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * This method takes the "natural" string password as entered by the user
     * and hashes it for the database.  The hash is a simple "unsalted" MD5 hash
     * so it's not particularly secure.
     * 
     * @param password	The entered password to be hashed
     * 
     * @throws NoSuchAlgorithmException	Should never happen while MD5 is the identifier
     */
    public void hashPassword(String password) throws NoSuchAlgorithmException {
    	// Instantiate an MD5 digest object
		MessageDigest md = null;
		md = MessageDigest.getInstance("MD5");
		
		// Get the password bytes for hashing
		byte[] passwordBytes = password.getBytes();
		//Get the byte[] hash from the digest algorithm
		byte[] hash = md.digest(passwordBytes);
		// Encode bytes as Base16(hex) String
		this.password = Util.encodeBase16(hash);
    }
    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#setConfirmPassword(java.lang.String)
     */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#setFirstName(java.lang.String)
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#setLastName(java.lang.String)
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#setAddress(eu.planets_project.ifr.core.security.api.model.Address)
     */
    public void setAddress(Address address) {
    	this.address = new SelfAddressImpl(address);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#setEmail(java.lang.String)
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#setPhoneNumber(java.lang.String)
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#setWebsite(java.lang.String)
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#setPasswordHint(java.lang.String)
     */
    public void setPasswordHint(String passwordHint) {
        this.passwordHint = passwordHint;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#setRoles(java.util.Set)
     */
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#setVersion(java.lang.Integer)
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#setAccountEnabled(boolean)
     */
    public void setAccountEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#setAccountExpired(boolean)
     */
    public void setAccountExpired(boolean accountExpired) {
        this.accountExpired = accountExpired;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#setAccountLocked(boolean)
     */
    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.security.api.model.User#setCredentialsExpired(boolean)
     */
    public void setCredentialsExpired(boolean credentialsExpired) {
        this.credentialsExpired = credentialsExpired;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof SelfUserImpl))
            return false;

        final SelfUserImpl user = (SelfUserImpl) o;

        if (username != null ? !username.equals(user.getUsername()) : user
                .getUsername() != null)
            return false;

        return true;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return (username != null ? username.hashCode() : 0);
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this,
                ToStringStyle.DEFAULT_STYLE).append("username", this.username)
                .append("enabled", this.enabled).append("accountExpired",
                        this.accountExpired).append("credentialsExpired",
                        this.credentialsExpired).append("accountLocked",
                        this.accountLocked);

        Set<Role> roles = this.getRoles();
        if (roles != null) {
            sb.append("Granted Roles: ");

            for (Iterator<Role> i = roles.iterator(); i.hasNext();) {
                sb.append(i.next().toString());
                if (i.hasNext())
                    sb.append(", ");
            }
        } else {
            sb.append("No Granted Roles.");
        }
        return sb.toString();
    }

}
