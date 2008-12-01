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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Target;
import org.hibernate.annotations.Type;
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
 * @see eu.planets_project.ifr.core.security.api.services.UserManager
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@Entity
@Table(name = "app_user")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class UserImpl implements User, Serializable {
    private static final long serialVersionUID = 3832626162173359411L;
	private static Log log = LogFactory.getLog(UserImpl.class);

    private Long id;
    private String username; // required
    private String password; // required
    private String confirmPassword;
    private String firstName; // required
    private String lastName; // required
    private AddressImpl address;
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
    private String firstrole;

    public UserImpl() {
    	log.info("UserImpl::UserImpl()");
    }

    public UserImpl(String username) {
    	log.info("UserImpl::UserImpl(String username)");
        this.username = username;
    }

    /**
     * @return
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    public Long getId() {
        return id;
    }

    /**
     * @return
     */
    @Version
    public Integer getVersion() {
        return version;
    }

    /**
     * @return
     */
    @Column(name = "username", length = 50, nullable = false, unique = true)
    public String getUsername() {
        return username;
    }

    /**
     * @return
     */
    @Column(name = "password", nullable = false)
    public String getPassword() {
        return password;
    }

    /**
     * @return
     */
    @Column(name = "first_name", length = 50, nullable = false)
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return
     */
    @Column(name = "last_name", length = 50, nullable = false)
    public String getLastName() {
        return lastName;
    }

    /**
     * @return
     */
    @Embedded
    @Target(AddressImpl.class)
    public Address getAddress() {
        return address;
    }

    /**
     * @return
     */
    @Column(name = "email", nullable = false, unique = true)
    public String getEmail() {
        return email;
    }

    /**
     * @return
     */
    @Column(name = "phone_number", nullable = true)
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @return
     */
    @Column(name = "website", nullable = true)
    public String getWebsite() {
        return website;
    }

    /**
     * @return
     */
    @Column(name = "password_hint", nullable = true)
    public String getPasswordHint() {
        return passwordHint;
    }

    /**
     * @return
     */
    @ManyToMany(
    		targetEntity=eu.planets_project.ifr.core.security.impl.model.RoleImpl.class,
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
     * @return
     */
    @Column(name = "account_enabled", nullable = false)
    @Type(type="yes_no")
    public boolean getAccountEnabled() {
        return enabled;
    }

    /**
     * @return
     */
    @Column(name = "account_expired", nullable = false)
    @Type(type="yes_no")
    public boolean getAccountExpired() {
        return accountExpired;
    }

    /**
     * @return
     */
    @Column(name = "account_locked", nullable = false)
    @Type(type="yes_no")
    public boolean getAccountLocked() {
        return accountLocked;
    }

    /**
     * @return
     */
    @Column(name = "credentials_expired", nullable = false)
    @Type(type="yes_no")
    public boolean getCredentialsExpired() {
        return credentialsExpired;
    }

    /**
     * @return
     */
    @Transient
    public String getFullName() {
        return firstName + ' ' + lastName;
    }

    /**
     * @return
     */
    public String confirmPassword() {
        return confirmPassword;
    }

    /**
     * @return
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
     * @return
     */
    public String firstRole() {
        if (!getRoles().isEmpty()) {
            return (getRoles().iterator().next()).getName();
        } else
            return "norole";// TODO ANJ: Should this return NULL?
    
    }

    /**
     * @param firstrole
     */
    public void firstRole(String firstrole) {
        this.firstrole = firstrole;
    }

    /**
     * @param role
     */
    public void addRole(Role role) {
        roles.add(role);
    }

    /**
     * @param role
     */
    public void removeRole(Role role) {
        getRoles().remove(role);
    }

    /**
     * @return
     */
    public boolean appliedAsProvider() {
        return appliesAsProvider;
    }

    /**
     * @param appliesAsProvider
     */
    public void applyingAsProvider(boolean appliesAsProvider) {
        this.appliesAsProvider = appliesAsProvider;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @param password
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
     * @param confirmPassword
     */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /**
     * @param firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @param lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @param address
     */
    public void setAddress(Address address) {
    	log.info("UserImpl.setAddress(Address address)");
    	this.address = new AddressImpl(address);
    }

    /**
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @param phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @param website
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     * @param passwordHint
     */
    public void setPasswordHint(String passwordHint) {
        this.passwordHint = passwordHint;
    }

    /**
     * @param roles
     */
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    /**
     * @param version
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * @param enabled
     */
    public void setAccountEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @param accountExpired
     */
    public void setAccountExpired(boolean accountExpired) {
        this.accountExpired = accountExpired;
    }

    /**
     * @param accountLocked
     */
    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    /**
     * @param credentialsExpired
     */
    public void setCredentialsExpired(boolean credentialsExpired) {
        this.credentialsExpired = credentialsExpired;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserImpl))
            return false;

        final UserImpl user = (UserImpl) o;

        if (username != null ? !username.equals(user.getUsername()) : user
                .getUsername() != null)
            return false;

        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return (username != null ? username.hashCode() : 0);
    }

    /* (non-Javadoc)
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
