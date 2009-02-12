/**
 * 
 */
package eu.planets_project.ifr.core.wee.impl.registry;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.soap.SOAPException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.RemoteBinding;

import eu.planets_project.ifr.core.storage.api.DataManagerLocal;
import eu.planets_project.ifr.core.wee.api.wsinterface.WftRegistryService;
import eu.planets_project.services.PlanetsException;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.ifr.core.wee.impl.mockup.DataRegistryMockup;
import eu.planets_project.ifr.core.wee.impl.utils.RegistryUtils;


/**
 * The WorkflowTemplateRegistry Implementation
 * TODO switch to the default data registry as soon as the programatical SSO is solved
 * @author <a href="mailto:andrew.lindley@arcs.ac.at">Andrew Lindley</a>
 * @since 13.11.2008
 *
 */
@Stateless(name = WftRegistryImpl.NAME)
@Remote(WftRegistryService.class)
@RemoteBinding(jndiBinding = "planets-project.eu/"+WftRegistryService.NAME+"/remote")
@WebService(name=WftRegistryImpl.NAME,
			serviceName = WftRegistryService.NAME, 
			targetNamespace = PlanetsServices.NS,
			endpointInterface = "eu.planets_project.ifr.core.wee.api.wsinterface.WftRegistryService")
//@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL, parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
//@SecurityDomain("PlanetsRealm")
public class WftRegistryImpl implements WftRegistryService, Serializable{
	
	public static final String NAME = "WorkflowTemplateRegistry";
	private static final Log log = LogFactory.getLog(WftRegistryImpl.class);
	private static final long serialVersionUID = 5384733679737239315L;
	private static WftRegistryImpl instance = null;
	private DataManagerLocal dataManager;
	//wfTemplateDir: /wfTemplates
	private static final String wfTemplateDir = RegistryUtils.getWeeWFTemplateDir();
	private WftRegistryCacheImpl cache = WftRegistryCacheImpl.getInstance();
	
	private WftRegistryImpl(){
		//retrieve a dataManager object
		//TODO switch back to DataManager as soon as programmatical SSO authentication is solved
		/*try {
	        Context jndiContext = new javax.naming.InitialContext();
	        dataManager = (DataManagerLocal) PortableRemoteObject
	                .narrow(jndiContext
	                        .lookup("planets-project.eu/DataManager/local"),
	                        DataManagerLocal.class);
	        log.debug("dataManager: "+ dataManager);
	    } catch (NamingException e) {
	        log.error("Failure in getting DataManagerLocal: "+ e.toString());
	    }*/
		dataManager = new DataRegistryMockup();
	}
	
	public static synchronized WftRegistryImpl getInstance(){
		if (instance == null){
			instance = new WftRegistryImpl();
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.service.WftRegistryService#getAllSupportedQNames()
	 */
	public ArrayList<String> getAllSupportedQNames() {
		ArrayList<String> ret = new ArrayList<String>();
		if(cache.getAllWorkflowTemplates().size()>0){
			Iterator<String> it = cache.getAllWorkflowTemplates().keySet().iterator();
			while(it.hasNext()){
				ret.add(it.next());
			}
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.service.WftRegistryService#getWFTemplate(java.lang.String)
	 */
	public byte[] getWFTemplate(String workflowTemplateQName)
			throws PlanetsException {
		try {
			if(workflowTemplateQName==null){
				String e = "WorkflowRegistry: please specify the workflowTemplate QName to retrieve its binary";
				log.debug(e);
				throw new PlanetsException(e);
			}
				
			return this.dataManager.retrieveBinary(
					new URI(wfTemplateDir+"/"+RegistryUtils.convertQNameToRegistryPathPURI(workflowTemplateQName)));
		} catch (Exception e) {
			//SOAPException, URISyntaxException
			log.error("Problems finding "+workflowTemplateQName+ "template at URI path: "+wfTemplateDir+"/"+RegistryUtils.convertQNameToRegistryPathPURI(workflowTemplateQName),e);
			throw new PlanetsException("No "+workflowTemplateQName+" template found at: "+wfTemplateDir+"/"+RegistryUtils.convertQNameToRegistryPathPURI(workflowTemplateQName));
		}
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.wee.api.wsinterface.WftRegistryService#registerWorkflowTemplate(java.lang.String, byte[])
	 */
	public void registerWorkflowTemplate(
			String workflowTemplateQName, byte[] javaBinary) throws PlanetsException {
		try {

			//1. upload the binary to the registry
			String path = RegistryUtils.convertQNameToRegistryPathPURI(workflowTemplateQName);
			this.dataManager.storeBinary(new URI(wfTemplateDir+"/"+path), javaBinary);
		
			//2. add QName to local cache
			WftRegistryCacheImpl.getInstance().addElementToCache(new URI(path));
			
		} catch (Exception e) {
			//LoginException, RepositoryException, URISyntaxException
			log.error("Error storing wfTemplate "+workflowTemplateQName+ "template in data registry URI path: "+wfTemplateDir+"/"+RegistryUtils.convertQNameToRegistryPathPURI(workflowTemplateQName),e);
			throw new PlanetsException("An error occured storing: "+workflowTemplateQName+" within the repository");
		}
	}
	
}
