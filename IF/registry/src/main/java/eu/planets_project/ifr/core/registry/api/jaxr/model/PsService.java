package eu.planets_project.ifr.core.registry.api.jaxr.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.JAXRException;
import javax.xml.registry.infomodel.Classification;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.Service;

import eu.planets_project.ifr.core.registry.api.jaxr.JaxrServiceRegistryHelper;

/**
 * Planets service.
 */
public class PsService extends PsRegistryObject {
    private PsOrganization organization;
    private List<PsBinding> bindings = new ArrayList<PsBinding>();

    public PsService() {
        this.organization = null;
    }

    public PsService(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        String res = "";
        try {
            res = " PSService toString(): ";
            res += "  keyid:  " + this.key;
            res += "  name:  " + this.name;
            res += "  orgid: " + this.organization;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;

    }

    /**
     * @param service The JAXR service to convert into a Planets service
     * @return A planets service
     */
    public static PsService of(Service service) {
        try {
            PsService s = new PsService(service.getName().getValue(), service
                    .getDescription().getValue());
            for (Object o : service.getClassifications()) {
                Classification c = (Classification) o;
                s.categories.add(PsCategory.of(c));
            }
            s.key = service.getKey().getId();
            // We should handle the organization setting when creating
            // organizations... else they call each other
            s.organization = PsOrganization.of(service
                    .getProvidingOrganization());
            return s;
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return the organization
     */
    public PsOrganization getOrganization() {
        return organization;
    }

    /**
     * @param organization the organization to set
     */
    public void setOrganization(PsOrganization organization) {
        this.organization = organization;
    }

    /**
     * @return the bindings
     */
    public List<PsBinding> getBindings() {
        return bindings;
    }

    /**
     * @param bindings the bindings to set
     */
    public void setBindings(List<PsBinding> bindings) {
        this.bindings = bindings;
    }

    /**
     * @param blcm The lifecycle manager
     * @param bqm The query manager
     * @return A JAXR service corresponding to this Planets service
     */
    public Service toJaxrService(BusinessLifeCycleManager blcm,
            BusinessQueryManager bqm) {
        try {
            Service service = null;
            /* If its there, retrieve it: */
            if (this.key != null) {
                service = JaxrServiceRegistryHelper.findServiceByKey(this.key, bqm);
                if (service == null) {
                    throw new IllegalStateException(
                            String
                                    .format(
                                            "This service has a key (%s) but could not be retrieved (invalid key?)",
                                            this.key));
                }
                return service;
            }
            /* Else, create a new one: */
            service = blcm.createService(blcm
                    .createInternationalString(this.name));
            Organization jaxrOrganization = this.getOrganization()
                    .toJaxrOrganization(blcm, bqm);
            service.setProvidingOrganization(jaxrOrganization);
            service.setDescription(blcm
                    .createInternationalString(this.description));
            for (PsBinding binding : bindings) {
                service.addServiceBinding(binding.toJaxrBinding(blcm));
            }
            for (PsCategory category : categories) {
                service.addClassification(category.toJaxrClassification(blcm));
            }
            BulkResponse response = blcm.saveServices(Arrays.asList(service));
            service.setKey((Key) response.getCollection().iterator().next());

            return service;
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return null;
    }
}
