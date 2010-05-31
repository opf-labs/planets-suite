/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
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
     * @return the phone number
     */
    public String getPhoneNumber();

    /**
     * @return the website address
     */
    public String getWebsite();

    /**
     * @return the password hint
     */
    public String getPasswordHint();

    /**
     * @return the set of roles associated with the user
     */
    public Set<Role> getRoles();

    /**
     * @return true if user account is enabled
     */
    public boolean getAccountEnabled();

    /**
     * @return true if user account has expired
     */
    public boolean getAccountExpired();

    /**
     * @return true is user account locked
     */
    public boolean getAccountLocked();

    /**
     * @return true if user credentials expired
     */
    public boolean getCredentialsExpired();

    /**
     * @return the users full name
     */
    public String getFullName();

    /**
     * @return the confirmation password
     */
    public String confirmPassword();

    /**
     * @return the users associated role names
     */
    public String[] rolesAsStrings();

    /**
     * @return the name of the first role in the list
     */
    public String firstRole();

    /**
     * @param firstrole The first role
     */
    public void firstRole(String firstrole);

    /**
     * @param role The role to add
     */
    public void addRole(Role role);

    /**
     * @param role The role to remove
     */
    public void removeRole(Role role);

    /**
     * @return true if the user a provider
     */
    public boolean appliedAsProvider();

    /**
     * @param appliesAsProvider 
     */
    public void applyingAsProvider(boolean appliesAsProvider);

    /**
     * @param id The id to set
     */
    public void setId(Long id);

    /**
     * @param username The username to set
     */
    public void setUsername(String username);

    /**
     * @param password The password to set
     */
    public void setPassword(String password);

    /**
     * This method takes the "natural" string password as entered by the user
     * and hashes it for the database.  The hash is a simple "unsalted" MD5 hash
     * so it's not particularly secure.
     * 
     * @param password The entered password to be hashed
     * 
     * @throws NoSuchAlgorithmException	Should never happen while MD5 is the identifier
     */
    public void hashPassword(String password) throws NoSuchAlgorithmException;

    /**
     * @param confirmPassword The confirmation password to set
     */
    public void setConfirmPassword(String confirmPassword);

    /**
     * @param firstName The first name to set
     */
    public void setFirstName(String firstName);
    
    /**
     * @param lastName The last name to set
     */
    public void setLastName(String lastName);

    /**
     * @param address The address to set
     */
    public void setAddress(Address address);

    /**
     * @param email The email to set
     */
    public void setEmail(String email);

    /**
     * @param phoneNumber The phone number to set
     */
    public void setPhoneNumber(String phoneNumber);

    /**
     * @param website The website to set
     */
    public void setWebsite(String website);

    /**
     * @param passwordHint The password hint to set
     */
    public void setPasswordHint(String passwordHint);
    
    /**
     * @param roles The roles to set
     */
    public void setRoles(Set<Role> roles);

    /**
     * @param version The version to set
     */
    public void setVersion(Integer version);

    /**
     * @param enabled The desired enablement state
     */
    public void setAccountEnabled(boolean enabled);

    /**
     * @param accountExpired The desired expiration state
     */
    public void setAccountExpired(boolean accountExpired);

    /**
     * @param accountLocked The desired locked state
     */
    public void setAccountLocked(boolean accountLocked);

    /**
     * @param credentialsExpired The desired credentials expired state
     */
    public void setCredentialsExpired(boolean credentialsExpired);
}
