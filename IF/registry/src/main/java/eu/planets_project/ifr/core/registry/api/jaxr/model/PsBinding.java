package eu.planets_project.ifr.core.registry.api.jaxr.model;

import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.JAXRException;
import javax.xml.registry.infomodel.ServiceBinding;

/**
 * Planets Service Binding.
 */
public class PsBinding extends PsRegistryObject {

    private String accessURI;
    private PsService service;
    private PsBinding targetbinding;
    private boolean validateuri;

    public PsBinding(String description, String uri, boolean isValidate,
            PsService service) {
        this.description = description;
        this.accessURI = uri;
        this.validateuri = isValidate;
        this.service = service;
    }

    public PsBinding() {}

    /**
     * @return the accessuri
     */
    public String getAccessURI() {
        return accessURI;
    }

    /**
     * @param accessURI the accessuri to set
     */
    public void setAccessURI(String accessURI) {
        this.accessURI = accessURI;
    }

    /**
     * @return the service
     */
    public PsService getService() {
        return service;
    }

    /**
     * @param service the service to set
     */
    public void setService(PsService service) {
        this.service = service;
    }

    /**
     * @return the targetbinding
     */
    public PsBinding getTargetbinding() {
        return targetbinding;
    }

    /**
     * @param targetbinding the targetbinding to set
     */
    public void setTargetbinding(PsBinding targetbinding) {
        this.targetbinding = targetbinding;
    }

    /**
     * @return the validateuri
     */
    public boolean isValidateuri() {
        return validateuri;
    }

    /**
     * @param validateuri the validateuri to set
     */
    public void setValidateuri(boolean validateuri) {
        this.validateuri = validateuri;
    }

    /**
     * @param blcm The manager
     * @return A JAXR service binding corresponding to this binding
     */
    public ServiceBinding toJaxrBinding(BusinessLifeCycleManager blcm) {
        try {
            ServiceBinding binding = blcm.createServiceBinding();
            binding.setName(blcm.createInternationalString(this.name));
            binding.setDescription(blcm
                    .createInternationalString(this.description));
            binding.setAccessURI(this.accessURI);
            binding.setKey(blcm.createKey(this.key));
            return binding;
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param serviceBinding The JAXR binding to convert into a Planets binding
     * @return A Planets binding for the given JAXR binding
     */
    public static PsBinding of(ServiceBinding serviceBinding) {
        try {
            PsBinding psBinding = new PsBinding();
            psBinding.key = serviceBinding.getKey().getId();
            psBinding.name = serviceBinding.getName().getValue();
            psBinding.description = serviceBinding.getDescription().getValue();
            psBinding.setValidateuri(false);
            psBinding.setAccessURI(serviceBinding.getAccessURI());
            PsService s = PsService.of(serviceBinding.getService());
            psBinding.setService(s);
            return psBinding;
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return null;
    }
}
