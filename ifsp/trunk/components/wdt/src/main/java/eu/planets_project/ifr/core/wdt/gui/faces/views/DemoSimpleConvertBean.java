package eu.planets_project.ifr.core.wdt.gui.faces.views;


import java.util.List;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Collection;
import java.net.MalformedURLException;
import java.net.URL;

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
import eu.planets_project.ifr.core.wdt.common.services.characterisation.*;
import eu.planets_project.ifr.core.wdt.common.services.magicTiff2Jpeg.*;
import eu.planets_project.ifr.core.wdt.common.services.openXMLMigration.*;
import eu.planets_project.ifr.core.wdt.common.services.reportGeneration.*;
//new service interface - call by value
//import eu.planets_project.ifr.core.wdt.common.services.jpeg2tiff.*;

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
 *    characterization workflow bean 
 *    demonstrates a workflow comprising a characterization followed by a migration
 * 	  @author Rainer Schmidt, ARC
 */
public class DemoSimpleConvertBean extends AbstractWorkflowBean implements PlanetsService, WorkflowBean {
	
	public static String TIFF2JPEG = "tiff2jpeg";
	public static String DOC2OPENXML = "doc2openxml";
	
	private Log logger = PlanetsLogger.getLogger(this.getClass(), "resources/log/wdt-log4j.xml");	
	
	private String viewId = null;
	
	//backing for drop down box
	private List<SelectItem> charServiceItems = null;
	private List<SelectItem> migServiceItems = null;
	private SelectItem currentCharServiceItem = null;	
	private SelectItem currentMigServiceItem = null;		
	
	//services
	private List<Service> charServices = null;
	private List<Service> migServices = null;

	
	private ServiceRegistry registry = null;	
	private String reportLoc= "";
	
	public DemoSimpleConvertBean() {
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
		charServices = registry.lookupServices(new Service(null, null, null, null, "",null));
		migServices = registry.lookupServices(new Service(null, null, null, null, "uuid:253246f0-ff2f-11dc-95ff-0800200c9a66/migration",null));
		
		charServiceItems.addAll( (Collection)toSelectItem(charServices) );
		migServiceItems.addAll( (Collection)toSelectItem(migServices) );
	}
	
	/**
	* removes service urls
	*/
	public void resetServices() {
		charServiceItems = new ArrayList<SelectItem>();
		charServiceItems.add( new SelectItem("please choose a service") );
		currentCharServiceItem = charServiceItems.get(0);
		migServiceItems = new ArrayList<SelectItem>();
		migServiceItems.add( new SelectItem("please choose a service") );
		currentMigServiceItem = migServiceItems.get(0);		
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
	
	//public void setMigServies(List services) {
	//	this.migServices = services;
	//}
		
	public List<SelectItem> getMigServiceItems() {
		return migServiceItems;
	}
	
	public String getCurrentMigServiceItem() {
		String service = (String) currentMigServiceItem.getValue();
		return service;
	}	
	
	public void toggleMigServiceItems(ValueChangeEvent vce) {
		String selectedService = (String) vce.getNewValue();
		//point currentCharService to new selection
		for( SelectItem indexService : migServiceItems) {
			if( indexService.getValue().toString().equals(selectedService)) currentMigServiceItem = indexService;
		}
		logger.debug("currentMigServiceItem: " + currentMigServiceItem.getValue().toString() );
	}

	public String invokeService() {
				
		ReportGenerationService report = null;
		int reportID = -1;
		
		try {

			PlanetsService pa = null;
			
			Service charService = this.getService(currentCharServiceItem, charServices);
			Service migService = this.getService(currentMigServiceItem, migServices);
			
			//locate simple characterization service
			SimpleCharacterisationService_Service simple_locator = 
				new SimpleCharacterisationService_Service(new URL(charService.getEndpoint()),charService.getQName() );
			SimpleCharacterisationService simpleChar = simple_locator.getSimpleCharacterisationServicePort();

			//locate preservation action service
			if(migService.getName().compareToIgnoreCase(TIFF2JPEG) > -1)	{		
				Tiff2JpegActionService tiff2jpeg_locator =
				  new Tiff2JpegActionService(new URL(migService.getEndpoint()),migService.getQName() );
			  pa = (PlanetsService) new Tiff2JpegPlanetsService(tiff2jpeg_locator.getTiff2JpegActionPort());
			} else if(migService.getName().compareToIgnoreCase(DOC2OPENXML) > -1) {
				OpenXMLMigration_Service openxml_locator = 
				  new OpenXMLMigration_Service(new URL(migService.getEndpoint()),migService.getQName() );
			  pa = (PlanetsService) new OpenXMLPlanetsService(openxml_locator.getOpenXMLMigrationPort());
			} else {
				throw new PlanetsException("WDT: Unable to find a service locator for service: "+migService.getName());
			}
			
			//start report			
			ReportGenerationService_Service report_locator = new ReportGenerationService_Service();
			report = report_locator.getReportGenerationServicePort();			
			reportID = report.startReport();
			
						
			for (int i=0; i<inputData.size();i++){
				
				String fileLoc = inputData.get(i);
				//logger.debug("file Path: "+fileLoc);
					
				String mimeType = simpleChar.characteriseFile(fileLoc);				
				fileLoc = fileLoc.substring("file:/".length(), fileLoc.length());
				
				logger.debug("fileLoc: "+fileLoc);				
				logger.debug("mimeType: "+mimeType+" serviceName: "+migService.getName());
				
				//check for file type and service pathway
				if (!( mimeType.equalsIgnoreCase("image/tiff") &&  migService.getName().toLowerCase().contains((CharSequence)"tiff2") ||
						   mimeType.equalsIgnoreCase("application/msword") &&  migService.getName().toLowerCase().contains((CharSequence)"doc2") )) {
				
					report.appendCDATA(reportID, "<fieldset><legend><b>File:</b><i> "+fileLoc+"</i></legend><table><tr><td>"+
						"<b>File Format Information: </b>" + mimeType + "<br>" +
						"<b>Service Information: </b>" + migService.getName() + "<br>" +
						"<b>Conversion Status: </b><font color=#FF0000>File not migrated - Type Mismatch</font><br>"+
						"</td></tr></table></fieldset>");
					continue;
				}
				
				//migration
				try {
					String out = pa.invokeService(fileLoc);
					String fileName = out.substring(out.lastIndexOf('/'), out.length()); 
					logger.debug("migrated: "+fileLoc+" pa service returns: "+out);
						
					report.appendCDATA(reportID, "<fieldset><legend><b>File:</b><i>"+fileLoc+"</i></legend><table><tr><td>"+
					  "<b>File Format Information:</b>"+mimeType+"<br>" +
						"<b>Conversion Status: </b><font color=#00CC00>File successfuly converted</font> <br>" +
						"<b>Converted File URI:</b><a href="+"\""+out+"\""+" target=_blank>"+fileName+"</a>" +
						"</td></tr></table></fieldset>");
				} catch(Exception e) {
					report.appendCDATA(reportID, "<fieldset><legend><b>File:</b><i>"+fileLoc+"</i></legend><table><tr><td>"+
					  "<b>File Format Information:</b>"+mimeType+"<br>" +
						"<b>Conversion Status: </b><font color=#FF0000>Error migrating file</font><br>" +
						"<b>Caused by:</b>"+e.getMessage()+
						"</td></tr></table></fieldset>");
				}
			}
		
		} catch (Exception e) {
			logger.error("workflow bean error: "+e);
			e.printStackTrace();
		} finally {
			reportLoc = report.finalizeReport(reportID);
			logger.debug("report location: "+reportLoc);
		}
		return reportLoc ;
	}
	
	
		public String getReportLoc() {
		return reportLoc;
	}
	
	public void setReportLoc(String reportLoc) {
		this.reportLoc = reportLoc;
	}

}

