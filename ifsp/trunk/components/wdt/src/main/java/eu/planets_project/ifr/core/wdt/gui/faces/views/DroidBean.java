package eu.planets_project.ifr.core.wdt.gui.faces.views;


import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;

import eu.planets_project.ifr.core.common.api.PlanetsService;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.storage.api.DataManagerLocal;
import eu.planets_project.ifr.core.storage.api.InvocationEvent;
import eu.planets_project.ifr.core.storage.api.WorkflowDefinition;
import eu.planets_project.ifr.core.storage.api.WorkflowExecution;
import eu.planets_project.ifr.core.storage.api.WorkflowManager;
import eu.planets_project.ifr.core.wdt.api.WorkflowBean;
import eu.planets_project.ifr.core.wdt.common.services.reportGeneration.ReportGenerationService;
import eu.planets_project.ifr.core.wdt.common.services.reportGeneration.ReportGenerationService_Service;
import eu.planets_project.ifr.core.wdt.impl.registry.Service;
import eu.planets_project.ifr.core.wdt.impl.registry.WorkflowServiceRegistry;
import eu.planets_project.ifr.core.wdt.impl.wf.AbstractWorkflowBean;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ImmutableContent;
import eu.planets_project.services.identify.Identify;
	
/**
 *    characterization workflow bean 
 *    demonstrates a workflow that identifies mulitple files using a droid service
 * 	  @author Rainer Schmidt, ARC
 */
public class DroidBean extends AbstractWorkflowBean implements PlanetsService, WorkflowBean {
		
	private Log logger = PlanetsLogger.getLogger(this.getClass(), "resources/log/wdt-log4j.xml");	
	
	private String viewId = null;
	
	//backing for drop down box
	private List<SelectItem> charServiceItems = null;
	private SelectItem currentCharServiceItem = null;	
	
	//services
	private List<Service> charServices = null;
	
	private WorkflowServiceRegistry registry = null;	
	private String reportLoc= "";
	
	public DroidBean() {
		super();
		registry = new WorkflowServiceRegistry();
		this.resetServices();
	}

	/**
	* loads services from a service registry 
	*/
	public void lookupServices() {
		
		this.resetServices();
		//registry lookup...
		charServices = registry.lookupServices(new Service(null, null, null, null, "uuid:253246f0-ff2f-11dc-95ff-0800200c9a66/identification",null));
		charServiceItems.addAll( (Collection)toSelectItem(charServices) );
	}
	
	/**
	* removes service urls
	*/
	public void resetServices() {
		charServiceItems = new ArrayList<SelectItem>();
		charServiceItems.add( new SelectItem("please choose a service") );
		currentCharServiceItem = charServiceItems.get(0);
	}
		
	public List<SelectItem> getCharServiceItems() {
		return charServiceItems;
	}
	
	public String getCurrentCharServiceItem() {
		String service = (String) currentCharServiceItem.getValue();
		return service;
	}	
	
	public void toggleCharServiceItems(ValueChangeEvent vce) {
		String selectedService = (String) vce.getNewValue();
		//point currentCharService to new selection
		for( SelectItem indexService : charServiceItems) {
			if( indexService.getValue().toString().equals(selectedService)) currentCharServiceItem = indexService;
		}
    logger.debug("currentCharServiceItem: " + currentCharServiceItem.getValue().toString() );
	}
	
	public boolean isReportAvailable() {
		if(reportLoc != null && !reportLoc.equals("")) {
			logger.debug("report available");
			return true;
		}
		logger.debug("no report available");
		return false;
	}
	
	public String getReportURL() {
		logger.debug("returning reportURL: "+reportLoc);
		return reportLoc;
	}
	
	public String invokeService() {
				
		ReportGenerationService report = null;
		int reportID = -1;
		
		try {
			
			Properties env = new java.util.Properties(); 
			InitialContext ctx = new InitialContext(env);
			Service charService = this.getService(currentCharServiceItem, charServices);
			
			//lookup workflow manager
			WorkflowManager wfManager = (WorkflowManager)ctx.lookup("planets-project.eu/WorkflowManager");
			logger.debug("wfManager: "+wfManager);
			
			//lookup data manager
			DataManagerLocal dataManager = (DataManagerLocal)ctx.lookup("planets-project.eu/DataManager/local");
			logger.debug("dataManager: "+dataManager);
						
			//add the definition to data registry
			WorkflowDefinition wfDef = new WorkflowDefinition("/wfDefs/Level1ConvertBean.def", "Rainer", /*doc*/null);

			try {
				//check if the template already exists in the repository
				wfManager.createWorkflow(wfDef);
			} catch(Exception e) {
				logger.debug("seems that wf definition aready exists in repository");
			}
			
	  	WorkflowExecution wfExec = new WorkflowExecution(wfDef.getId(), "Rainer"); 
	  	String workflowId = wfManager.createWorkflowInstance(wfExec);
			logger.debug("workflowId: "+workflowId);	  	
						
			//start report			
			ReportGenerationService_Service report_locator = new ReportGenerationService_Service();
			report = report_locator.getReportGenerationServicePort();			
			reportID = report.startReport();
			
			//create a droid service
			//Droid_Service locator = 
			//	new Droid_Service(new URL(charService.getEndpoint()),charService.getQName() );
			//Droid droid = locator.getDroidPort();
						//create a characterization service
    	javax.xml.ws.Service service = javax.xml.ws.Service.create(new URL(charService.getEndpoint()), Identify.QNAME);
    	logger.debug("charService URL: "+charService.getEndpoint());
    	Identify droid = service.getPort(Identify.class);
			logger.debug("droid: "+droid);    	
				
			for (int i=0; i<inputData.size();i++) {
				
				String pdURI = inputData.get(i);
				byte[] imageData = null;
								
				try {								
					logger.debug("retrieving data for: "+pdURI);
		    	imageData = dataManager.retrieveBinary(new URI(pdURI));
		    } catch(Exception e) {
					report.appendCDATA(reportID, "<fieldset><legend><b>File:</b><i> "+pdURI+"</i></legend><table><tr><td>"+
						"<b>Status: </b><font color=#FF0000>Error could not retrieve binary data</font><br>" +
						"<b>Caused by:</b>"+e.getMessage()+
						"</td></tr></table></fieldset>");
		    	continue;
		    }
		    	
		    
		    //List<String> rets = droid.identifyBytes(imageData).getTypes();
		    
				Date d1 = new Date();
				URI pronomURI = null;
				//identify data
				try {
				    DigitalObject dob = new DigitalObject.Builder(ImmutableContent.byValue(imageData)).build();
		    	List<URI> rets = droid.identify(dob,null).getTypes();
		    	//todo if rets.length = 0 error
		    	//todo handle multiple pronom ids
		    	for( URI furi : rets ) {
		    		logger.debug("Droid reported: "+furi);
		    		pronomURI = furi;
		    	}					
				} catch(Exception e) {
						report.appendCDATA(reportID, "<fieldset><legend><b>File:</b><i> "+pdURI+"</i></legend><table><tr><td>"+
						"<b>Status: </b><font color=#FF0000>Error could not identify data</font><br>" +
						"</td></tr></table></fieldset>");
		    	continue;
		    }
				Date d2 = new Date();
		    
				//create an event for this
				InvocationEvent event = new InvocationEvent(null, new URI(charService.getEndpoint()), "identifyBytes", new URI(pdURI), pronomURI, d1, d2);      

				report.appendCDATA(reportID, "<fieldset><legend><b>File:</b><i>"+pdURI+"</i></legend><table><tr><td>"+
				"<b>File Format Information:</b>"+pronomURI+"<br>" +
				"</td></tr></table></fieldset>");
			}
		
		} catch (Exception e) {
			logger.error("workflow bean error: "+e);
		} finally {
			reportLoc = report.finalizeReport(reportID);
			logger.debug("report location: "+reportLoc);
		}
		return reportLoc ;
	}
}



//DOM STUFF

/*
			//lookup stateful ejb
			//Context context = new InitialContext();
			//WorkflowManager wfHome = (WorkflowManager)PortableRemoteObject.
			//	narrow(context.lookup("/planets-project.eu/WorkflowManager"), WorkflowManager.class);
			//WorkflowManager wfManager = wfHome.create();
			//....
			
			//creata a workflow document
			Document doc = null;
			//try {
  		//	DOMParser p = new DOMParser();
  		//	p.setFeature("http://xml.org/sax/features/validation",true);
  		//	p.parse("c:\\data\\wfDef.xml");              
  		//	doc = p.getDocument();
			//} catch (IOException io) { logger.error(""+io); 
			//} catch (SAXException s) { logger.error(""+s); 
			
			//try {
  		//	SAXBuilder b = new SAXBuilder(false);  // validierenden Parser nutzen
  		//	doc = b.build(new File("c:\\data\\wfDef.xml"));
			//}
			//catch (Exception e) {
  		//	logger.error(""+e);
			//}
			
			//InputSource source = new InputSource(new FileInputStream("c:\\data\\wfDef.xml"));
      //DOMParser parser = new DOMParser();
      //parser.parse(source);
      
      try {
	      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	      DocumentBuilder builder = factory.newDocumentBuilder();
	  		doc = builder.parse( new File("c:\\data\\wfDef.xml") );
  		} catch (SAXParseException spe) {
    		// Error generated by the parser
    		logger.error("\n** Parsing error"
      	+ ", line " + spe.getLineNumber()
      	+ ", uri " + spe.getSystemId());
    		logger.error("   " + spe.getMessage() );
  
    		// Use the contained exception, if any
    		Exception  x = spe;
    		if (spe.getException() != null)
      		x = spe.getException();
    		logger.error(""+x);

	  	} catch (SAXException sxe) {
	    	// Error generated during parsing
	    	Exception  x = sxe;
	    	if (sxe.getException() != null)
	      	x = sxe.getException();
	    	logger.error(""+x);
	
	  	} catch (ParserConfigurationException pce) {
	    	// Parser with specified options can't be built
	    	logger.error(""+pce);
	  	} catch (IOException ioe) {
	    	// I/O error
	    	logger.error(""+ioe);
	  	}

      
			logger.debug("wf doc: "+doc);

*/
