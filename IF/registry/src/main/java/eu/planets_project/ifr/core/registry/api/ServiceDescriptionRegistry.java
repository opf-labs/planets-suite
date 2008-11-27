package eu.planets_project.ifr.core.registry.api;

import java.util.List;

import eu.planets_project.ifr.core.registry.api.model.ServiceRegistryMessage;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * Draft/Work-in-progress: Registry interface based on the new
 * ServiceDescription objects, supporting query by example.
 * @see ServiceDescription
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public interface ServiceDescriptionRegistry {

    /**
     * @param serviceDescription The service description to register
     * @return A status message
     */
    ServiceRegistryMessage register(ServiceDescription serviceDescription);

    /**
     * Query by example registry lookup.
     * @param example The sample service description
     * @return The services for which all non-null values correspond to the
     *         values of the given sample object
     */
    List<ServiceDescription> query(ServiceDescription example);

    /**
     * Clears the registry of all entries.
     * @return A status message
     */
    ServiceRegistryMessage clear();

    // **********************************************************************

    // TODO Do XML-based methods make sense at all here?
    // /**
    // * @param xmlServiceDescription The service description XML to register
    // * @return A status message
    // */
    // ServiceRegistryMessage register(String xmlServiceDescription);

    // TODO Do XML-based methods make sense at all here?
    // /**
    // * Query by example registry lookup.
    // * @param sampleXmlServiceDescription The sample service description XML
    // * @return The services for which all non-null values correspond to the
    // * values of the given sample object
    // */
    // List<ServiceDescription> query(String sampleXmlServiceDescription);

    // **********************************************************************
}
