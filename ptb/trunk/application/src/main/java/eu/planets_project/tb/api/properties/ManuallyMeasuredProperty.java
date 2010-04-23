package eu.planets_project.tb.api.properties;

/**
 * Interface for manual properties 
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 23.04.2010
 *
 */
public interface ManuallyMeasuredProperty {
	
	/**
     * @return the property's name
     */
    public String getName();
    
    /**
     * @return a human readable comment - describing the property
     */
    public String getDescription();
    
    /**
     * @return a unique identifier for the manually measured proeprty at hand
     */
    public String getURI();
    
    /**
     * An indicator if the property is user created or derived from the TB (e.g. TB3ontology)
     * @return
     */
    public boolean isUserCreated();
    
}
