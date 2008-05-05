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
//import eu.planets_project.ifr.core.wdt.api.WorkflowBean;
import eu.planets_project.ifr.core.wdt.gui.faces.FileBrowser;
import eu.planets_project.ifr.core.wdt.gui.faces.TemplateContainer;
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
import eu.planets_project.ifr.core.wdt.common.services.registry.*;
	
/**
 *    characterization workflow bean 
 *    demonstrates a workflow comprising a characterization followed by a migration
 * 	  @author Rainer Schmidt, ARC
 */
public class DemoSimpleConvertBean1 extends AbstractWorkflowBean implements PlanetsService, WorkflowBean {
	
	private Log logger = PlanetsLogger.getLogger(this.getClass(), "resources/log/wdt-log4j.xml");	
	
	private String viewId = null;
	
	private List<SelectItem> firstServices = null;
	private List<SelectItem> secondServices = null;
	private SelectItem currentFirstService = null;
	private SelectItem currentSecService = null;
	
	private ServiceRegistry registry = null;
	
	private String reportLoc="";
	public String getReportLoc() {
		return reportLoc;
	}

	public void setReportLoc(String reportLoc) {
		this.reportLoc = reportLoc;
	}
	public DemoSimpleConvertBean1() {
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
		firstServices.addAll( (Collection)toSelectItem(registry.lookupServices(new Service(null, null, null, "first",null))) );
		secondServices.addAll( (Collection)toSelectItem(registry.lookupServices(new Service(null, null, null, "second",null))) );
	}
	
	/**
	* removes service urls
	*/
	public void resetServices() {
		firstServices = new ArrayList<SelectItem>();
		firstServices.add( new SelectItem("please choose a service") );
		currentFirstService = firstServices.get(0);
		secondServices = new ArrayList<SelectItem>();
		secondServices.add( new SelectItem("please choose a service") );
		currentSecService = secondServices.get(0);		
	}
		
	public List<SelectItem> getFirstServices() {
		return firstServices;
	}
	
	public String getCurrentFirstService() {
		String service = (String) currentFirstService.getValue();
		return service;
	}	
	
	public void toggleFirstServices(ValueChangeEvent vce) {
		String selectedService = (String) vce.getNewValue();
		//point currentCharService to new selection
		for( SelectItem indexService : firstServices) {
			if( indexService.getValue().toString().equals(selectedService)) currentFirstService = indexService;
		}
    logger.debug("currentCharService: " + currentFirstService.getValue().toString() );
	}
	
	//public void setMigServies(List services) {
	//	this.migServices = services;
	//}
		
	public List<SelectItem> getSecondServices() {
		return secondServices;
	}
	
	public String getCurrentSecService() {
		String service = (String) currentSecService.getValue();
		return service;
	}	
	
	public void toggleSecServices(ValueChangeEvent vce) {
		String selectedService = (String) vce.getNewValue();
		//point currentCharService to new selection
		for( SelectItem indexService : secondServices) {
			if( indexService.getValue().toString().equals(selectedService)) currentSecService= indexService;
		}
		logger.debug("currentMigService: " + currentSecService.getValue().toString() );
	}
	

	public String invokeService() {
		try {
			logger.debug("--------------------: Start to invoke Service:"+currentFirstService.getLabel().toString());
			List allServs = registry.lookupServices("");
			Service serv0 = (Service)allServs.get(0); //SimpleCharachterization
			Service serv1 = (Service)allServs.get(1); //Tiff2Jpeg
			Service serv2 = (Service)allServs.get(2); //OpenXML
			Service serv3 = (Service)allServs.get(3); //DataManager
			Service serv4 = (Service)allServs.get(4); //ReportGenerator
			
//			ServiceRegistryManager_Service reg_locator =
//				new ServiceRegistryManager_Service();
//			ServiceRegistryManager registry = reg_locator.getServiceRegistryManagerPort();
//			
//			ServiceList services = registry.findServices("admin", "admin", "");
//			List<PsService> allservies = services.getService(); 
//			logger.debug("--------------------: Start to read service from registry:"+allservies.size());
//			for (int i=0; i<allservies.size();i++){
//				PsService currServ = allservies.get(i);
//				logger.debug("--------------------Service name: "+currServ.getName()
//							 +"---Key:"+currServ.getKey()+"------Classification:"+currServ.getClassifications()
//							 +"---Service ID:"+currServ.getServiceId()+"----class:"+currServ.getClass().getSimpleName());
//			}
			
			
			SimpleCharacterisationService_Service simple_locator = 
				new SimpleCharacterisationService_Service(new URL(serv0.getEndpoint()),serv0.getQName() );
			SimpleCharacterisationService simple = simple_locator.getSimpleCharacterisationServicePort();
			
			Tiff2JpegActionService tiff2jpeg_locator =
				new Tiff2JpegActionService(new URL(serv1.getEndpoint()),serv1.getQName() );
			Tiff2Jpeg tiff2jpeg = tiff2jpeg_locator.getTiff2JpegActionPort();
			
			OpenXMLMigration_Service openxml_locator = 
				new OpenXMLMigration_Service(new URL(serv2.getEndpoint()),serv2.getQName() );
			OpenXMLMigration openxml = openxml_locator.getOpenXMLMigrationPort();
			
			//TODO: When data manager is ready use this 
//			DataManager_Service data_locator = new DataManager_Service(new URL(serv3.getEndpoint()),serv3.getQName() );
//			DataManager datam = data_locator.getDataManagerPort();
			
			ReportGenerationService_Service report_locator =
				new ReportGenerationService_Service(new URL(serv4.getEndpoint()),serv4.getQName() );
			ReportGenerationService report = report_locator.getReportGenerationServicePort();
			
			int reportID = report.startReport();
			
			//TODO service names will be read from Service registry
			if (currentFirstService.getLabel().toString().equalsIgnoreCase("SimpleCharacterisation@localhost")
					&& (currentSecService.getLabel().toString().equalsIgnoreCase("Tiff2Jpeg@localhost") ||
							currentSecService.getLabel().toString().equalsIgnoreCase("OpenXML@localhost")	)){
				String fileExtension = "";
				for (int i=0; i<inputData.size();i++){
					String fileLoc = inputData.get(i);
//					logger.debug("--------------------file Path:"+fileLoc);
					
					fileExtension = simple.characteriseFile(fileLoc);
					fileLoc =fileLoc.substring("file:/".length(), fileLoc.length());
//					logger.debug("--------------------file Path:"+fileLoc);
					if (fileExtension.equalsIgnoreCase("application/msword")) { 
//						logger.debug("--------------------It is MS/word: "+fileExtension+"----"+openxml.isConfigValid());
						//TODO: return is pointer to file and I must find the location and copy it to out put folder
						openxml.convertFileRef(fileLoc);
						//TODO: assign to Datamanager
						report.appendCDATA(reportID, "<fieldset><legend><b>File:</b><i>"
								+fileLoc+",</i></legend>" +
								"<table><tr><td><b>File Format Information:</b>"+fileExtension+",<br>" +
								"<b>Conversion Status: </b><font color=#00CC00>File successfuly converted</font> <br>" +
								"<b>Converted File URI:</b><a href="+"$DataManagerResponse.registryPath"+", target=_blank>"+
								" $DataManagerResponse.registryPath"+"</a></td></tr></table>,</fieldset>");
					}else if(fileExtension.equalsIgnoreCase("image/tiff")){ 
//						logger.debug("--------------------It is image/tiff: "+fileLoc);
						tiff2jpeg.convertFile(fileLoc);
						//TODO: assign to Datamanager
						report.appendCDATA(reportID, "<fieldset><legend><b>File:</b><i>"
								+fileLoc+",</i></legend>" +
								"<table><tr><td><b>File Format Information:</b>"+fileLoc+",<br>" +
								"<b>Conversion Status: </b><font color=#00CC00>File successfuly converted</font> <br>" +
								"<b>Converted File URI:</b><a href="+"$DataManagerResponse.registryPath"+", target=_blank>"+
								" $DataManagerResponse.registryPath"+"</a></td></tr></table></fieldset>");
					}else {
//						logger.debug("--------------------It is something else: "+fileLoc);
						report.appendCDATA(reportID, "<fieldset><legend><b>File:</b><i>, "
								+fileLoc+",</i></legend><table><tr><td><b>File Format"+
								"Information: </b>'"+
								"$CharResponse.return"+
								"<br><b>Conversion Status:"+"</b><font color=#FF0000>File could not be converted</font> <br>"+
								"</td></tr></table></fieldset>");
					}
				}
			}else { // error
				logger.error("---ERROR:----"+"First Service must be SimpleCharacterisation and second one must be Tiff2Jpeg or OpenXML");
			}
			reportLoc = report.finalizeReport(reportID);
			logger.debug("---------REPORT Location----"+reportLoc);
			
		} catch (Exception e) {
			logger.error("---ERROR:----"+e.getMessage());
			e.fillInStackTrace();
		}
		return reportLoc ;
	}


}

