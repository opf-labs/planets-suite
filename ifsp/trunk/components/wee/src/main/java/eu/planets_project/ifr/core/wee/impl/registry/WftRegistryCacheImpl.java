package eu.planets_project.ifr.core.wee.impl.registry;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.soap.SOAPException;

import eu.planets_project.ifr.core.storage.api.DataManagerLocal;
import eu.planets_project.ifr.core.wee.impl.mockup.DataRegistryMockup;
import eu.planets_project.ifr.core.wee.impl.utils.RegistryUtils;

public class WftRegistryCacheImpl {
	
	private static WftRegistryCacheImpl instance = null;
	private Map<String,URI> cacheQNameURI = new HashMap<String,URI>();
	private DataManagerLocal dataManager;
	private static final String wfTemplateDir = RegistryUtils.getWeeWFTemplateDir();
	
	private WftRegistryCacheImpl(){
		dataManager = new DataRegistryMockup();
		loadAllRegisteredWFTemplatQNames();
	}
	
	public static synchronized WftRegistryCacheImpl getInstance(){
		if (instance == null){
			instance = new WftRegistryCacheImpl();
		}
		return instance;
	}
	
	public void addElementToCache(URI pdURI){
		String QName = RegistryUtils.convertRegistryPDURIPathToQName(pdURI);
		if(QName != null){
			this.cacheQNameURI.put(QName, pdURI);
		}
	}

	/**
	 *  Loads the data registry to populate the list of existing registered workflowTemplates
	 *  and caches the QName with the data PDURI location
	 */
	private void loadAllRegisteredWFTemplatQNames(){
		try {
			URI[] list = dataManager.findFilesWithExtension(new URI(wfTemplateDir), "java");
			if(list!=null){
				for(URI uri:list){
					addElementToCache(uri);
				}
			}
		} catch (SOAPException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	
	public Map<String,URI> getAllWorkflowTemplates(){
		return this.cacheQNameURI;
	}

}
