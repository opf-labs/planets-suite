/**
 * 
 */
package eu.planets_project.services;

import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
public interface PlanetsService {

    /**
     * @return the service description for this service
     */
    public ServiceDescription describe();
    
}
