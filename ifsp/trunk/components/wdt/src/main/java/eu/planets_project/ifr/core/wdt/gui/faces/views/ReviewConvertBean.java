package eu.planets_project.ifr.core.wdt.gui.faces.views;


import java.util.List;
import java.util.Iterator;
import java.util.Vector;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.StringTokenizer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;

//Workflow Logging
import javax.naming.InitialContext;
import javax.naming.Context;
import eu.planets_project.ifr.core.storage.api.WorkflowManager;
import eu.planets_project.ifr.core.storage.api.WorkflowDefinition;
import eu.planets_project.ifr.core.storage.api.WorkflowExecution;
import eu.planets_project.ifr.core.storage.api.InvocationEvent;
import eu.planets_project.ifr.core.storage.api.DataManagerLocal;
import java.io.ByteArrayInputStream;
import org.w3c.dom.Document;
import org.jdom.input.SAXBuilder;
import org.xml.sax.SAXException;
import org.apache.xerces.parsers.DOMParser;
import java.io.IOException;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.*;
import org.xml.sax.*;
//import javax.rmi.PortableRemoteObject;

import javax.faces.component.*;
import javax.faces.model.SelectItem;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.common.api.PlanetsService;
import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.wdt.impl.wf.AbstractWorkflowBean;
import eu.planets_project.ifr.core.wdt.impl.wf.WFTemplate;
import eu.planets_project.ifr.core.wdt.impl.registry.Service;
import eu.planets_project.ifr.core.wdt.impl.registry.ServiceRegistry;
import eu.planets_project.ifr.core.wdt.api.WorkflowBean;

import eu.planets_project.ifr.core.wdt.common.faces.JSFUtil;
//import eu.planets_project.ifr.core.wdt.common.services.characterisation.*;
//import eu.planets_project.ifr.core.wdt.common.services.magicTiff2Jpeg.*;
//import eu.planets_project.ifr.core.wdt.common.services.openXMLMigration.*;
import eu.planets_project.ifr.core.wdt.common.services.reportGeneration.*;
//new service interface - call by value
//import eu.planets_project.ifr.core.wdt.common.services.jpeg2tiff.*;
import eu.planets_project.ifr.core.common.services.migrate.BasicMigrateOneBinary;
import eu.planets_project.ifr.core.common.services.identify.BasicIdentifyOneBinary;
import eu.planets_project.ifr.core.common.services.characterise.BasicCharacteriseOneBinaryXCELtoURI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL; 
	
/**
 *    characterization workflow bean <br>
 *    demonstrates a workflow comprising a characterization followed by a migration based on level 1 services
 * 	  @author Rainer Schmidt, ARC
 */
public class ReviewConvertBean extends AbstractWorkflowBean implements PlanetsService, WorkflowBean {
			
	private Log logger = PlanetsLogger.getLogger(this.getClass(), "resources/log/wdt-log4j.xml");	

	private static String OUTPUT_DIR = "migrated_files";
		
	private String viewId = null;
	
	//backing for drop down box
	private List<SelectItem> migServiceItems1 = null;
	private List<SelectItem> migServiceItems2 = null;
	private List<SelectItem> charServiceItems = null;
	private SelectItem currentMigServiceItem1 = null;		
	private SelectItem currentMigServiceItem2 = null;	
	private SelectItem currentCharServiceItem = null;	
	
	//services
	private List<Service> migServices = null;
	private List<Service> charServices = null;
	
	private ServiceRegistry registry = null;	
	private String reportLoc= "";
	
	public ReviewConvertBean() {
		super();
		//get a registry url from a .properties file 
		registry = new ServiceRegistry();
		this.resetServices();
	}

	/**
	* loads services from a service registry 
	*/
	public void lookupServices() {
		
		this.resetServices();
		//registry lookup...
		migServices = registry.lookupServices(new Service(null, null, null, null, "uuid:253246f0-ff2f-11dc-95ff-0800200c9a66/migration",null));
		charServices = registry.lookupServices(new Service(null, null, null, null, "uuid:253246f0-ff2f-11dc-95ff-0800200c9a66/metadataextraction",null));
		
		migServiceItems1.addAll( (Collection)toSelectItem(migServices) );
		migServiceItems2.addAll( (Collection)toSelectItem(migServices) );
		charServiceItems.addAll( (Collection)toSelectItem(charServices) );
		logger.debug("migServiceIdems1 size: "+migServiceItems1.size());		
	}
	
	/**
	* removes service urls
	*/
	public void resetServices() {
		migServiceItems1 = new ArrayList<SelectItem>();
		migServiceItems2 = new ArrayList<SelectItem>();
		charServiceItems = new ArrayList<SelectItem>();
		migServiceItems1.add( new SelectItem("please choose a service") );
		migServiceItems2.add( new SelectItem("please choose a service") );
		charServiceItems.add( new SelectItem("please choose a service") );
		currentMigServiceItem1 = migServiceItems1.get(0);		
		currentMigServiceItem2 = migServiceItems2.get(0);			
		currentCharServiceItem = charServiceItems.get(0);			
	}
	
	//public void setMigServies(List services) {
	//	this.migServices = services;
	//}
	
	/**
	* @return get list of migration service items
	*/	
	public List<SelectItem> getMigServiceItems1() {
		return migServiceItems1;
	}
	
	/**
	* @return get current migration service item
	*/
	public String getCurrentMigServiceItem1() {
		String service = (String) currentMigServiceItem1.getValue();
		return service;
	}	
	
	/**
	* move selected migration service on top of the item list
	*/			
	public void toggleMigServiceItems1(ValueChangeEvent vce) {
		String selectedService = (String) vce.getNewValue();
		//point currentCharService to new selection
		for( SelectItem indexService : migServiceItems1) {
			if( indexService.getValue().toString().equals(selectedService)) currentMigServiceItem1 = indexService;
		}
		logger.debug("currentMigServiceItem1: " + currentMigServiceItem1.getValue().toString() );
	}
	
	/**
	* @return get list of migration service items
	*/	
	public List<SelectItem> getMigServiceItems2() {
		return migServiceItems2;
	}
	
	/**
	* @return get current migration service item
	*/
	public String getCurrentMigServiceItem2() {
		String service = (String) currentMigServiceItem2.getValue();
		return service;
	}	
	
	/**
	* move selected migration service on top of the item list
	*/			
	public void toggleMigServiceItems2(ValueChangeEvent vce) {
		String selectedService = (String) vce.getNewValue();
		//point currentCharService to new selection
		for( SelectItem indexService : migServiceItems2) {
			if( indexService.getValue().toString().equals(selectedService)) currentMigServiceItem2 = indexService;
		}
		logger.debug("currentMigServiceItem2: " + currentMigServiceItem2.getValue().toString() );
	}
	
	/**
	* @return get list of characterization service items
	*/	
	public List<SelectItem> getCharServiceItems() {
		return charServiceItems;
	}
	
	/**
	* @return get current characterization service item
	*/
	public String getCurrentCharServiceItem() {
		String service = (String) currentCharServiceItem.getValue();
		return service;
	}	
	
	/**
	* move selected migration service on top of the item list
	*/			
	public void toggleCharServiceItems(ValueChangeEvent vce) {
		String selectedService = (String) vce.getNewValue();
		//point currentCharService to new selection
		for( SelectItem indexService : charServiceItems) {
			if( indexService.getValue().toString().equals(selectedService)) currentCharServiceItem = indexService;
		}
		logger.debug("currentCharServiceItem: " + currentCharServiceItem.getValue().toString() );
	}
	
	/**
	* @return boolean is true if there is a report available
	*/
	public boolean isReportAvailable() {
		if(reportLoc != null && !reportLoc.equals("")) {
			logger.debug("report available");
			return true;
		}
		logger.debug("no report available");
		return false;
	}
	
	/**
	* @return execution report
	*/
	public String getReportURL() {
		logger.debug("returning reportURL: "+reportLoc);
		return reportLoc;
	}

	/**
	* implements the workflow template exectution method
	*/
	public String invokeService() {
				
		ReportGenerationService report = null;
		int reportID = -1;
		
		try {
			
			Properties env = new java.util.Properties(); 
			InitialContext ctx = new InitialContext(env);
			Service migService1 = this.getService(currentMigServiceItem1, migServices);		
			Service migService2 = this.getService(currentMigServiceItem2, migServices);		
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
			logger.debug("reportID: "+reportID);				
				
			//create migration service 1
			logger.debug("creating 1st service for: "+migService1.getEndpoint());
			javax.xml.ws.Service service = javax.xml.ws.Service.create(new URL(migService1.getEndpoint()), BasicMigrateOneBinary.QNAME);
			logger.debug("mig service created: "+service);
			BasicMigrateOneBinary converter1 = service.getPort(BasicMigrateOneBinary.class);
			logger.debug("converter1: "+converter1);			
			
			//create migration service 2
			logger.debug("creating 2nd service for: "+migService2.getEndpoint());
			service = javax.xml.ws.Service.create(new URL(migService2.getEndpoint()), BasicMigrateOneBinary.QNAME);
			logger.debug("mig service created: "+service);
			BasicMigrateOneBinary converter2 = service.getPort(BasicMigrateOneBinary.class);
			logger.debug("converter2: "+converter2);		
			
			//create XCL Characterization service
			logger.debug("creating char service for: "+charService.getEndpoint());
			service = javax.xml.ws.Service.create(new URL(charService.getEndpoint()), BasicCharacteriseOneBinaryXCELtoURI.QNAME);
			logger.debug("char service created: "+service);
			BasicCharacteriseOneBinaryXCELtoURI extractor = service.getPort(BasicCharacteriseOneBinaryXCELtoURI.class);
			logger.debug("extractor: "+extractor);		
			
			URI xcelPath = null;
												
			for (int i=0; i<inputData.size();i++) {
				
				String pdURI = inputData.get(i);
		    
				byte[] sourceData = null;

				//retrieve binary data								
				try {								
					logger.debug("retrieving data for: "+pdURI);
		    	    sourceData = dataManager.retrieveBinary(new URI(pdURI));
		        } catch(Exception e) {
					report.appendCDATA(reportID, "<fieldset><legend><b>File:</b><i> "+pdURI+"</i></legend><table><tr><td>"+
						"<b>Status: </b><font color=#FF0000>Error could not retrieve binary data</font><br>" +
						"<b>Caused by:</b>"+e.getMessage()+
						"</td></tr></table></fieldset>");
		    	    continue;
		        }
		    
    		    //1st file migration
    		    Date d1 = new Date();
    		    byte[] out1 = null;
    		    
    			try {								
    		        out1 = converter1.basicMigrateOneBinary(sourceData);
    		    } catch(Exception e) {
					report.appendCDATA(reportID, "<fieldset><legend><b>File:</b><i> "+pdURI+"</i></legend><table><tr><td>"+
						"<b>Status: </b><font color=#FF0000>Error could migrate file</font><br>" +
						"<b>Caused by:</b>"+e.getMessage()+
						"</td></tr></table></fieldset>");
    		        continue;
    		    }		    
    		    Date d2 = new Date();
    
                //create output uri    			
                URI[] list = dataManager.list(null);	      
                String fileName = null;
                StringTokenizer st = new StringTokenizer(pdURI);
                while (st.hasMoreTokens()) { fileName = st.nextToken("/");}
                URI resultPath = new URI(list[0]+"/"+OUTPUT_DIR+"/"+workflowId+"/"+fileName+"/" + getNewFileNameFromType( fileName, migService1.getDescription() ) );
                logger.debug("resultPath: "+resultPath);
                
                //store output file
                try {
                	dataManager.storeBinary(resultPath, out1);
                } catch(Exception e) {
            		report.appendCDATA(reportID, "<fieldset><legend><b>File:</b><i> "+pdURI+"</i></legend><table><tr><td>"+
            		"<b>Status: </b><font color=#FF0000>Error storing result file</font><br>" +
            		"<b>Location: </b><font color=#FF0000>"+resultPath+"</font><br>" +						
            		"<b>Caused by:</b>"+e.getMessage()+
            		"</td></tr></table></fieldset>");
            		continue;
                }
	
				InvocationEvent event = new InvocationEvent(null, new URI(migService1.getEndpoint()), "basicMigrateBinary", new URI(pdURI), resultPath, d1, d2);      
				
				String ret = wfManager.createInvocationEvent(event, workflowId);
				logger.debug("wfMan.createIEvent: "+ret);
				
				report.appendCDATA(reportID, "<fieldset><legend><b>File:</b><i>"+pdURI+"</i></legend><table><tr><td>"+
				"<b>Conversion Status: </b><font color=#00CC00>File successfuly converted</font> <br>" +
				"<b>Converted File URI:</b><a href="+"\""+resultPath+"\""+" target=_blank>"+resultPath+"</a>" +
				"</td></tr></table></fieldset>");
				
    		    //2nd file migration
    		    d1 = new Date();
    		    byte[] out2 = null;
    		     		    
    			try {								
    		        out2 = converter2.basicMigrateOneBinary(out1);
    		    } catch(Exception e) {
					report.appendCDATA(reportID, "<fieldset><legend><b>File:</b><i> "+resultPath+"</i></legend><table><tr><td>"+
						"<b>Status: </b><font color=#FF0000>Error could migrate file</font><br>" +
						"<b>Caused by:</b>"+e.getMessage()+
						"</td></tr></table></fieldset>");
    		        continue;
    		    }		    
    		    d2 = new Date();
    
                //create output uri    			
                list = dataManager.list(null);	      
                fileName = null;
                st = new StringTokenizer(resultPath.toString());
                while (st.hasMoreTokens()) { fileName = st.nextToken("/");}
                URI resultPath2 = new URI(list[0]+"/"+OUTPUT_DIR+"/"+workflowId+"/"+fileName+"/" + getNewFileNameFromType( fileName, migService2.getDescription() ) );
                logger.debug("resultPath: "+resultPath2);
                
                //store output file
                try {
                	dataManager.storeBinary(resultPath2, out2);
                } catch(Exception e) {
            		report.appendCDATA(reportID, "<fieldset><legend><b>File:</b><i> "+resultPath+"</i></legend><table><tr><td>"+
            		"<b>Status: </b><font color=#FF0000>Error storing result file</font><br>" +
            		"<b>Location: </b><font color=#FF0000>"+resultPath2+"</font><br>" +						
            		"<b>Caused by:</b>"+e.getMessage()+
            		"</td></tr></table></fieldset>");
            		continue;
                }
	
				event = new InvocationEvent(null, new URI(migService2.getEndpoint()), "basicMigrateBinary", resultPath, resultPath2, d1, d2);      
				
				ret = wfManager.createInvocationEvent(event, workflowId);
				logger.debug("wfMan.createIEvent: "+ret);
				
				report.appendCDATA(reportID, "<fieldset><legend><b>File:</b><i>"+resultPath+"</i></legend><table><tr><td>"+
				"<b>Conversion Status: </b><font color=#00CC00>File successfuly converted</font> <br>" +
				"<b>Converted File URI:</b><a href="+"\""+resultPath2+"\""+" target=_blank>"+resultPath2+"</a>" +
				"</td></tr></table></fieldset>");
				
    		    //XCL Characterisation
    		    
    		    String fType = getExtensionFromType( migService2.getDescription() );
    		    logger.debug("The file type produced by the last service is: "+fType);
    		    if ( ( fType.equals( "pdf" ) ) || ( fType.equals( "tiff" ) ) ||( fType.equals( "png" ) ) ||( fType.equals( "docx" ) ) ) {
        		    xcelPath = new URI(list[0]+"/XCEL/xcel_"+fType+".xcel" );
        		    logger.debug( "The xcelPath URI is: " + xcelPath );
        		    d1 = new Date();
        		    URI xcdlUri = null;
        		     		    
        			try {								
        		        xcdlUri = extractor.basicCharacteriseOneBinaryXCELtoURI(resultPath2, xcelPath);
        		    } catch(Exception e) {
    					report.appendCDATA(reportID, "<fieldset><legend><b>File:</b><i> "+resultPath2+"</i></legend><table><tr><td>"+
    						"<b>Status: </b><font color=#FF0000>Error could not characterize file</font><br>" +
    						"<b>Caused by:</b>"+e.getMessage()+
    						"</td></tr></table></fieldset>");
        		        continue;
        		    }		    
        		    d2 = new Date();
    	
    				event = new InvocationEvent(null, new URI(charService.getEndpoint()), "basicCharacteriseBinaryXCELtoURI", resultPath2, xcdlUri, d1, d2);      
    				
    				ret = wfManager.createInvocationEvent(event, workflowId);
    				logger.debug("wfMan.createIEvent: "+ret);
    				
    				report.appendCDATA(reportID, "<fieldset><legend><b>File:</b><i>"+resultPath2+"</i></legend><table><tr><td>"+
    				"<b>Extraction Status: </b><font color=#00CC00>Metadata successfully extracted</font> <br>" +
    				"<b>Metadata File URI:</b><a href="+"\""+xcdlUri+"\""+" target=_blank>"+xcdlUri+"</a>" +
    				"</td></tr></table></fieldset>");
    			} else { // non-supported XCEL type
    					report.appendCDATA(reportID, "<fieldset><legend><b>File:</b><i> "+resultPath2+"</i></legend><table><tr><td>"+
    						"<b>Status: </b><font color=#FF0000>Error could not characterize file</font><br>" +
    						"<b>because there is no XCEL description for this file type.</b></td></tr></table></fieldset>");
    			}
				
			}
		
		} catch (Exception e) {
			logger.error("workflow bean error: "+e);
		} finally {
			reportLoc = report.finalizeReport(reportID);
			logger.debug("report location: "+reportLoc);
		}
		return reportLoc ;
	}
	
	private String getExtensionFromType( String type ) {
	    String retString = "";
	    int k = type.lastIndexOf( "-" );
	    if ( k > 0 ) {
	        retString = type.substring( k+1 );
	    }
	    return retString;
	}
	
	private String getNewFileNameFromType( String oldName, String type ) {
	    String newName = "output";
	    int k = oldName.lastIndexOf( "." );
	    if ( k > 0 ) {
	        newName = oldName.substring( 0, k+1 ) +  getExtensionFromType( type );
	    }
	    return newName;
	}
	
}
