package eu.planets_project.tb.gui.backing.exp.utils;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.xml.sax.SAXException;

import eu.planets_project.ifr.core.wee.api.utils.WorkflowConfigUtil;
import eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf;
import eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf.Services;
import eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf.Template;
import eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf.Services.Service;
import eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf.Services.Service.Parameters;
import eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf.Services.Service.Parameters.Param;
import eu.planets_project.ifr.core.wee.api.wsinterface.WftRegistryService;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.gui.backing.exp.ExpTypeExecutablePP.ServiceBean;
import eu.planets_project.tb.gui.backing.exp.ExpTypeExecutablePP.ServiceParameter;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.services.util.wee.WeeRemoteUtil;

/**
 * This class allows to have state on a given workflow configuration (e.g. methods for getting a temp link for
 * the configuration file) as well as util methods for generating and validating workflow configuration related
 * information
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 23.12.2009
 *
 */
public class ExpTypeWeeUtils{
	
	private static Log log = LogFactory.getLog(ExpTypeWeeUtils.class);
	private DataHandler dh = new DataHandlerImpl();
	private WftRegistryService wftRegistryService = WeeRemoteUtil.getInstance().getWeeRegistryService();
	private ExpTypeWeeBean weeBean;
	private WorkflowConfigUtil wfConfigUtil;
	//two represenations of the wfconfiguration
	private WorkflowConf wfConf = null;
	private String wfConfXML = null;
	private int currXMLConfigHashCode = 0;
	 //for caching purposes when exposing the current xml configuration
    private String currXMLConfigTempFileURI;
    private HashMap<Integer,Boolean> isValidConf = new HashMap<Integer,Boolean>();
	
	public ExpTypeWeeUtils(ExpTypeWeeBean bean){
		this.weeBean = bean;
		wfConfigUtil = new WorkflowConfigUtil();
	}
	
	public void setWeeWorkflowConf(WorkflowConf wfConfig){
		this.wfConf = wfConfig;
		this.wfConfXML = null;
		try {
			if(wfConf != null){
				//try to get the xml represenation
				wfConfXML = wfConfigUtil.marshalWorkflowConfigToXMLTemplate(wfConfig);
			}
		} catch (Exception e) {
			log.error("unable to marshall Workflow Config to XML represenation",e);
		} 
	}
    
    public WorkflowConf getWeeWorkflowConf(){
    	return this.wfConf;
    }
    
    /**
     * Tries to build a WorkflowConf object from the provided elements i.e.
     * TemplateName, Services, Parameters, etc. This method does not call setWorkflowConf
     * @return
     * @throws Exception in the case of an invalid WorkflowConf
     */
    public WorkflowConf buildWorkflowConf(String wfTemplateName, List<ServiceBean> sbs) throws Exception{
    	
    	//1.add the template retrieved from the uploaded wfconfig
    	Template serTempl = new Template();
    	serTempl.setClazz(wfTemplateName);
    	
    	Services services = new Services();
    	//2.browse through the provided services and build the Services object
    	for(ServiceBean sb : sbs){
    		Service service = new Service();
    		service.setId(sb.getServiceId());
    		service.setEndpoint(sb.getServiceEndpoint());
    		
    		Parameters parameters = new Parameters();
    		//3. iterate over all parameters that have been created/altered
    		for(ServiceParameter param : sb.getServiceParameters()){
    			Param parameter = new Param();
    			parameter.setName(param.getName());
    			parameter.setValue(param.getValue());
    			parameters.getParam().add(parameter);
    		}
    		if(parameters.getParam().size()>0){
    			//there needs to be a Parameter element only if there's a param for being xsd compliant
    			service.setParameters(parameters);
    		}
    		
    		services.getService().add(service);
    	}
    	
    	String sWFConfig = this.buildXMLConfig(serTempl, services);
    	return wfConfigUtil.unmarshalWorkflowConfig(sWFConfig);
    	
    }
    
    /**
     * @see #buildWorkflowConf(String, List)
     * @param serTempl
     * @param services
     * @return
     * @throws Exception
     */
    public WorkflowConf buildWorkflowConf(Template serTempl, Services services) throws Exception{
    	String sWFConfig = this.buildXMLConfig(serTempl, services);
    	return wfConfigUtil.unmarshalWorkflowConfig(sWFConfig);
    }
    
    /**
     * Is the same as buildWorkflowConf but returns the xml representation
     * instead of the WorkflowConf object. This may be offered for downloading.
     * @see buildWorkflowConfFromCurrentConfiguration
     * @return
     * @throws Exception 
     */
    public String buildXMLConfig(Template serTempl, Services services) throws Exception{
    	WorkflowConf conf = new WorkflowConf();
    	
    	conf.setServices(services);
    	conf.setTemplate(serTempl);

		try {
			return wfConfigUtil.marshalWorkflowConfigToXMLTemplate(conf);
		} catch (Exception e) {
			log.debug("Unable to build the XMLWorkflowConfiguration",e);
			throw e;
		}
    }
    
    
    /**
     * Builds the currentXMLConfig from the given service/param configuration
     * and writes it to a temporary file that's accessible via an external url ref.
     * This can be used within the browser to download the currentXMLConfig
     * @return
     */
    public String getTempFileDownloadLinkForCurrentXMLConfig(){
		if(this.wfConf==null){
			return null;
		}
		//save it's hashcode - for caching purposes
		if((this.currXMLConfigHashCode!=-1)&&(this.currXMLConfigHashCode!=this.wfConfXML.hashCode())){
			this.currXMLConfigHashCode =  wfConfXML.hashCode();
			
			try {
				//get a temporary file
				File f = dh.createTempFileInExternallyAccessableDir();
				Writer out = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(f), "UTF-8" ) );
				out.write(wfConfXML);
				out.close();
				currXMLConfigTempFileURI = ""+dh.getHttpFileRef(f);
				return currXMLConfigTempFileURI;
			} catch (Exception e) {
				log.debug("Error getting TempFileDownloadLinkForCurrentXMLConfig "+e);
				return null;
			}
		}
		else{
			//FIXME: still missing to check if this temp file ref still exists
			//returned the cached object
			return this.currXMLConfigTempFileURI;
		}
		
    }
    
    /**
     * Checks if the xml wf configuration provided to this util class by setWeeWorkflowConfig is valid.
     * @return
     */
    public boolean isValidCurrentConfiguration(){
    	if(this.wfConfXML!=null){
	    	if(isValidConf.containsKey(this.wfConfXML.hashCode())){
	    		return isValidConf.get(this.wfConfXML.hashCode());
	    	}
	    	else{
	    		boolean b = this.isValidXMLConfig(this.wfConfXML);
	    		isValidConf.put(this.wfConfXML.hashCode(), b);
	    		return b;
	    	}
    	}else{
    		return false;
    	}
    }
    
    
	private void checkValidXMLConfig(InputStream xmlWFConfig) throws Exception{
		InputStream bis = getClass().getClassLoader().getResourceAsStream(
				"planets_wdt.xsd");
		try {
			SchemaFactory factory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(new StreamSource(bis));
			Validator validator = schema.newValidator();
			// Validate file against schema
			XMLOutputter outputter = new XMLOutputter();
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(xmlWFConfig);
			validator.validate(new StreamSource(new StringReader(outputter
					.outputString(doc.getRootElement()))));
		} catch (Exception e) {
			String err = "The provided xmlWFConfig is not valid against the currently used planets_wdt_xsd schema";
			log.debug(err,e);
			throw new Exception (err,e);
		}
		finally{
			bis.close();
		}
	}
	
	/**
	 * Check if the provided wfXMLConfiguration is valid against the currently used schema
	 * @param xmlWFConfig
	 * @return
	 */
	public boolean isValidXMLConfig(String xmlWFConfig){
		InputStream ins = new ByteArrayInputStream(xmlWFConfig.getBytes());
		try{
			checkValidXMLConfig(ins);
			ins.close();
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * A util method to check if any provided wfTemplateQName is available on the WFTRegistry
	 * @param wfTemplateQName
	 * @return
	 */
	public boolean isTemplateAvailableInWftRegistry(String wfTemplateQName){
		//check if the template is still available on the wee system
		if (wftRegistryService.getAllSupportedQNames().contains(wfTemplateQName)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Is the currently used template available on the WFTRegistry
	 * @return
	 */
	public boolean isTemplateAvailableInWftRegistry(){
		if(this.wfConf!=null){
			return this.isTemplateAvailableInWftRegistry(this.wfConf.getTemplate().getClazz());
		}else{
			return false;
		}
	}
	
}
