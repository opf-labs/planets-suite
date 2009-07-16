package eu.planets_project.ifr.core.wee.api.workflow;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.planets_project.ifr.core.wee.api.ReportingLog.Message;
import eu.planets_project.services.PlanetsService;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.utils.DigitalObjectUtils;

/**
 * @author <a href="mailto:andrew.lindley@arcs.ac.at">Andrew Lindley</a>
 * @since 18.12.2008 Contains information and operations which are the same for
 *        all objects implementing the workflowTemplate interface
 */
public abstract class WorkflowTemplateHelper implements Serializable {

    private Map<PlanetsService, ServiceCallConfigs> serviceInvocationConfigs = new HashMap<PlanetsService, ServiceCallConfigs>();
    private List<DigitalObject> data = new ArrayList<DigitalObject>();

    /*
     * All services with a Planets interface can be used within a given
     * worklowTemplate
     */
    private static final String[] supportedPlanetsServiceTypes = { "eu.planets_project.services.identify.Identify",
            "eu.planets_project.services.characterise.Characterise",
            "eu.planets_project.services.characterise.DetermineProperties",
            "eu.planets_project.services.compare.BasicCompareFormatPropertie",
            "eu.planets_project.services.migrate.Migrate", "eu.planets_project.services.modify.Modify",
            "eu.planets_project.services.migrate.MigrateAsync" };

    /*
     * (non-Javadoc)
     * @seeeu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#
     * getDeclaredWFServices()
     */
    @SuppressWarnings("unchecked")
    public List<Field> getDeclaredWFServices() {
        Class clazz = this.getClass();
        List<Field> ret = new ArrayList<Field>();

        // e.g. look for public and private Fields
        for (Field f : clazz.getDeclaredFields()) {
            // check if the declared Service in the ServiceTemplate is supported
            // e.g. eu.planets_project.services.identify.Identify
            if (this.isServiceTypeSupported(f)) {
                ret.add(f);
            }
        }
        /*
         * for(int i=0; i<clazz.getDeclaredFields().length; i++){
         * System.out.println
         * (clazz.getDeclaredFields()[i].getType().getCanonicalName()); }
         */
        return ret;
    }

    /*
     * (non-Javadoc)
     * @seeeu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#
     * getDeclaredWFServiceNames()
     */
    public List<String> getDeclaredWFServiceNames() {
        List<String> ret = new ArrayList<String>();
        for (Field f : this.getDeclaredWFServices()) {
            ret.add(f.getName());
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * @seeeu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#
     * getSupportedServiceTypes
     */
    public List<String> getSupportedServiceTypes() {
        return Arrays.asList(supportedPlanetsServiceTypes);
    }

    /*
     * (non-Javadoc)
     * @seeeu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#
     * isServiceTypeSupported(java.lang.reflect.Field)
     */
    public boolean isServiceTypeSupported(Field f) {
        if (getSupportedServiceTypes().contains(f.getType().getCanonicalName())) {
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @seeeu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#
     * setServiceCallConfigs(eu.planets_project.services.PlanetsService,
     * eu.planets_project.ifr.core.wee.impl.workflow.ServiceCallConfigs)
     */
    public void setServiceCallConfigs(PlanetsService forService, ServiceCallConfigs serCallConfigs) {
        this.serviceInvocationConfigs.put(forService, serCallConfigs);
    }

    /*
     * (non-Javadoc)
     * @seeeu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#
     * getServiceCallConfigs(eu.planets_project.services.PlanetsService)
     */
    public ServiceCallConfigs getServiceCallConfigs(PlanetsService forService) {
        return this.serviceInvocationConfigs.get(forService);
    }

    /*
     * (non-Javadoc)
     * @see
     * eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#getData()
     */
    public List<DigitalObject> getData() {
        return data;
    }

    /*
     * (non-Javadoc)
     * @see
     * eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#setData
     * (java.util.List)
     */
    public void setData(List<DigitalObject> data) {
        this.data = data;
    }

    /**
     * @param objects The digital objects
     * @param folder The folder to store the files in
     * @return References to the given digital object, stored in the given
     *         folder
     */
    public static List<URL> reference(List<DigitalObject> objects, File folder) {
        List<URL> urls = new ArrayList<URL>();
        List<File> files = DigitalObjectUtils.getDigitalObjectsAsFiles(objects, folder);
        for (File f : files) {
            try {
                urls.add(f.toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    /**
     * @param results URLs to files (output of {@link #reference(List, File)})
     * @return The message linking to the given files, to be passed to a
     *         reporting log
     */
    public static Message link(List<URL> results) {
        List<Parameter> links = new ArrayList<Parameter>();
        for (int i = 0; i < results.size(); i++) {
            URL url = results.get(i);
            String[] strings = url.getFile().split("/");
            links.add(new Parameter("File " + (i + 1), String.format("<a href='%s'>%s</a>", url,
                    strings[strings.length - 1])));
        }
        Parameter[] array = links.toArray(new Parameter[] {});
        return new Message("Results", array);
    }

    /**
     * @param template The template
     * @return A overview message for the template
     */
    public static Message overview(WorkflowTemplate template) {
        return new Message("Overview", new Parameter("Description", template.describe()), new Parameter("Started",
                new Date(System.currentTimeMillis()).toString()));
    }

}
