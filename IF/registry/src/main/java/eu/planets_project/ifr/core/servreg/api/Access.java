package eu.planets_project.ifr.core.servreg.api;

import java.util.List;

import javax.faces.context.FacesContext;

import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * Access control in the service registry.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
final class Access {

    private Access() {/* non-instantiable utility class */}

    /**
     * @param candidate The service description to check access rights for
     * @return True, if access to the candidate is either unrestricted or the user is logged in via UI and authorized to
     *         see the description
     */
    static boolean authorized(final ServiceDescription candidate) {
        Property allowedRolesProperty = getProperty(ServiceDescription.AUTHORIZED_ROLES, candidate);
        if (allowedRolesProperty == null) {
            /* No access restriction in this service description: */
            return true;
        }
        /* Check the authorization for this description based on the UI authentication: */
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null && context.getExternalContext() != null) {
            String allowedRoles = allowedRolesProperty.getValue();
            String[] roles = allowedRoles.split(",");
            for (String role : roles) {
                role = role.trim();
                boolean userInRole = context.getExternalContext().isUserInRole(role);
                if (userInRole) {
                    return true;
                }
            }
        }
        /*
         * We have a restriction set, but are not authenticated via the UI: don't hand out restricted services.
         */
        /*
         * TODO: we need a way to access the restricted services via API without being authenticated via UI, e.g. via a
         * different query method, which is not available through the web service.
         */
        return false;
    }

    private static Property getProperty(final String key, final ServiceDescription candidate) {
        List<Property> properties = candidate.getProperties();
        for (Property property : properties) {
            if (property.getName().equals(key)) {
                return property;
            }
        }
        return null;
    }

}