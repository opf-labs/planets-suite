package eu.planets_project.ifr.core.registry.api.jaxr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.FindQualifier;
import javax.xml.registry.JAXRException;
import javax.xml.registry.JAXRResponse;
import javax.xml.registry.infomodel.Classification;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.Service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.registry.api.jaxr.model.PsCategory;

/**
 * Static helper class for the service registry manager.
 * 
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class JaxrServiceRegistryHelper {

    /** Enforce non-instantiability with a private constructor. */
    private JaxrServiceRegistryHelper() {}

    /***/
    private static Log LOG = LogFactory.getLog(JaxrServiceRegistryHelper.class.getName());

    /**
     * internal method to retrieve service details after a list of services has
     * been returned in another query.
     * 
     * @param key of the service
     * @param bqm The query manager
     * @return a Service Object
     */
    public static Service findServiceByKey(final String key,
            final BusinessQueryManager bqm) {
        if (key == null) {
            throw new IllegalArgumentException("Service key is null!");
        }
        try {
            BulkResponse br = bqm.findServices(null, Arrays
                    .asList(FindQualifier.SORT_BY_NAME_DESC), Arrays
                    .asList("%"), null, null);
            Collection<Service> services = br.getCollection();
            // FIXME this is awful... fix when green
            for (Service service : services) {
                if (service.getKey() != null
                        && service.getKey().getId().compareTo(key) == 0) {
                    return service;
                }
            }
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        LOG.info("No service found for key: " + key);
        return null;
    }

    /**
     * As the service registry manager bean is a stateless bean, the
     * organization that owns a service has to be prefetched and set for the
     * saveService method call.
     * 
     * @param key The org key
     * @param bqm The query manager
     * @return Returns an organization with the given key, or null
     */
    public static Organization fetchOrganizationByKey(final String key,
            final BusinessQueryManager bqm) {
        try {
            BulkResponse br = bqm.findOrganizations(Arrays
                    .asList(FindQualifier.OR_LIKE_KEYS), Arrays.asList("%"),
                    null, null, null, null);
            Collection<Organization> organizations = br.getCollection();
            for (Organization organization : organizations) {
                if (organization.getKey() != null && key != null
                        && organization.getKey().getId().equals(key)) {
                    LOG.info("Fetched organization with key: "
                            + organization.getKey().getId());
                    return organization;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOG.info("Did not find any organization with key: " + key);
        return null;
    }

    /**
     * @param classifications The collection of classifications
     * @return Returns an array list to be used in planets registry jaxb
     *         classes, containing only the IDs of the given classifications
     */
    public static ArrayList<PsCategory> categoryConvert(
            final Collection<Classification> classifications) {
        ArrayList<PsCategory> res = new ArrayList<PsCategory>();
        for (Classification classification : classifications) {
            try {
                PsCategory o = new PsCategory();
                o.id = (classification.getKey().getId());
                res.add(o);
            } catch (JAXRException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    /**
     * @param response The response to check
     * @param message The message to append details to
     * @return Returns a new string consisting of message with appended details
     *         on success or failure of the response
     */
    public static String check(final BulkResponse response, final String message) {
        String result = message;
        try {
            if (successful(response)) {
                result += "successful: " + response.getCollection();
            } else if (response != null) {
                result += "failed: " + response.getExceptions();
            }
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param response The response to check for success
     * @return Retruns true if the response resulted from a successful
     *         transaction, else false.
     */
    public static boolean successful(final BulkResponse response) {
        try {
            return response != null
                    && response.getStatus() == JAXRResponse.STATUS_SUCCESS;
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return false;
    }
}
