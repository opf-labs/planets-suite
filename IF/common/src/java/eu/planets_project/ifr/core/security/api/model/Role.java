/**
 * 
 */
package eu.planets_project.ifr.core.security.api.model;

/**
 * This is the Role entity bean definition.  It is held in a Many-To-Many relationship with the User entity.
 *
 * @see eu.planets_project.ifr.core.security.api.services.UserManager
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public interface Role {

	/** 
	 * Get the ID, a unique primary key.
	 *  
	 * @return The Role id as a Long
	 */
	public Long getId();

	/** 
	 * Get the Role name.
	 *  
	 * @return The Role name as a string
	 */
	public String getName();

	/**
	 * Get the full description of the Role.
	 * 
	 * @return The role description
	 */
	public String getDescription();

	/**
	 * Set the ID for this role, usually done automatically.
	 * 
	 * @param id
	 */
	public void setId(Long id);

	/**
	 * Set this name for this role.
	 * @param name
	 */
	public void setName(String name);

	/**
	 * Set the description for this role.
	 * @param description
	 */
	public void setDescription(String description);

	/**
	 * Compare two Roles. Names must be unique.
	 * @param o
	 * @return boolean indicating equality (true means objects are equal)
	 */
	public boolean equals(Object o);
}
