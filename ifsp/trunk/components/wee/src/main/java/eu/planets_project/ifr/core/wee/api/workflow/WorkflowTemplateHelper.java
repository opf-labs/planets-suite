package eu.planets_project.ifr.core.wee.api.workflow;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.planets_project.services.PlanetsService;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.utils.DigitalObjectUtils;

/**
 * @author <a href="mailto:andrew.lindley@arcs.ac.at">Andrew Lindley</a>
 * @since 18.12.2008
 * Contains information and operations which are the same for all objects implementing
 * the workflowTemplate interface
 *
 */
public abstract class WorkflowTemplateHelper implements Serializable{

	private Map<PlanetsService, ServiceCallConfigs> serviceInvocationConfigs = new HashMap<PlanetsService, ServiceCallConfigs>();
	private List<DigitalObject> data = new ArrayList<DigitalObject>();
	
	/* All services with a Planets interface can be used within a given worklowTemplate */
	private static final String[] supportedPlanetsServiceTypes = 
			{"eu.planets_project.services.identify.Identify",
			"eu.planets_project.services.characterise.Characterise",
			"eu.planets_project.services.characterise.DetermineProperties",
			"eu.planets_project.services.compare.BasicCompareFormatPropertie",
			"eu.planets_project.services.migrate.Migrate",
			"eu.planets_project.services.modify.Modify",
			"eu.planets_project.services.migrate.MigrateAsync"};
	
	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#getDeclaredWFServices()
	 */
	@SuppressWarnings("unchecked")
	public List<Field> getDeclaredWFServices(){
		Class clazz = this.getClass();
		List<Field> ret = new ArrayList<Field>();
		
		//e.g. look for public and private Fields
		for(Field f : clazz.getDeclaredFields()){			
			//check if the declared Service in the ServiceTemplate is supported
			//e.g. eu.planets_project.services.identify.Identify
			if(this.isServiceTypeSupported(f)){
				ret.add(f);
			}
		}
		/*
		 for(int i=0; i<clazz.getDeclaredFields().length; i++){
			System.out.println(clazz.getDeclaredFields()[i].getType().getCanonicalName());
		 }
		*/
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#getDeclaredWFServiceNames()
	 */
	public List<String> getDeclaredWFServiceNames(){
		List<String> ret = new ArrayList<String>();
		for(Field f : this.getDeclaredWFServices()){
			ret.add(f.getName());
		}
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#getSupportedServiceTypes
	 */
	public List<String> getSupportedServiceTypes(){
		return Arrays.asList(supportedPlanetsServiceTypes);
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#isServiceTypeSupported(java.lang.reflect.Field)
	 */
	public boolean isServiceTypeSupported(Field f){
		if(getSupportedServiceTypes().contains(f.getType().getCanonicalName())){
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#setServiceCallConfigs(eu.planets_project.services.PlanetsService, eu.planets_project.ifr.core.wee.impl.workflow.ServiceCallConfigs)
	 */
	public void setServiceCallConfigs(PlanetsService forService, ServiceCallConfigs serCallConfigs){
		this.serviceInvocationConfigs.put(forService, serCallConfigs);
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#getServiceCallConfigs(eu.planets_project.services.PlanetsService)
	 */
	public ServiceCallConfigs getServiceCallConfigs(PlanetsService forService){
		return this.serviceInvocationConfigs.get(forService);
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#getData()
	 */
	public List<DigitalObject> getData(){
		return data;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#setData(java.util.List)
	 */
	public void setData(List<DigitalObject> data){
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
        List<File> files = DigitalObjectUtils.getDigitalObjectsAsFiles(objects,
                folder);
        for (File f : files) {
            try {
                urls.add(f.toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

}
