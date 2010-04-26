/**
 * 
 */
package eu.planets_project.tb.impl.properties;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.properties.ManuallyMeasuredProperty;
import eu.planets_project.tb.impl.model.ontology.OntologyHandlerImpl;
import eu.planets_project.tb.impl.system.BackendProperties;
import eu.planets_project.ifr.core.storage.api.DataRegistry;
import eu.planets_project.ifr.core.storage.api.DataRegistryFactory;
import eu.planets_project.ifr.core.storage.api.DataRegistry.DigitalObjectManagerNotFoundException;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException;

/**
 * This class is responsible mainly for two items:
 * a) adding/removing manual measurement property definitions specific for a given user
 * b) storing and loading a specific set of properties that have been applied to a given experiment
 * 
 *  - importing manual properties which the user defined from the user's space
 *  - adding manual property definitions to the user's space
 *  - creating a new set of manual properties derived from the TB3ontology for a user
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 23.04.2010
 *
 */
public class ManuallyMeasuredPropertyHandlerImpl {
	
	//import the data manager where to store user specific information
	
	private static ManuallyMeasuredPropertyHandlerImpl instance;
	//contains all properties pulled in from the extracted TB3Ontology.xml
	private List<ManuallyMeasuredProperty> TB3ontologyProperties;
	private Log log = LogFactory.getLog(ManuallyMeasuredPropertyHandlerImpl.class);
	private DataRegistry dataRegistry;
	
	public static synchronized ManuallyMeasuredPropertyHandlerImpl getInstance(){
		if (instance == null){
			instance = new ManuallyMeasuredPropertyHandlerImpl();
		}
		return instance;
	}
	
	private ManuallyMeasuredPropertyHandlerImpl(){
		//init the dataRegistry object
		dataRegistry = DataRegistryFactory.getDataRegistry();
		//parse the extractedTB3OntologyProperties.xml
		readManualUserPropertiesFromTB3Ontology();
	}
	
	private void readManualUserPropertiesFromTB3Ontology(){
		try{
			log.info("start readingManualUserPropertiesFromTB3Ontology from eu/planets_project/tb/impl/extractedTB3OntologyProperties.xml");
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			java.io.InputStream TB3OntologyXML = getClass().getClassLoader().getResourceAsStream("eu/planets_project/tb/impl/extractedTB3OntologyProperties.xml");
			Document doc = builder.parse(TB3OntologyXML);
			Element root = doc.getDocumentElement();
			
			//read the properties from the xml-file
			TB3ontologyProperties = parseManualPropertiesXML(root,false);

			TB3OntologyXML.close();
			log.info("finished readingManualUserPropertiesFromTB3Ontology. imported: "+TB3ontologyProperties.size()+" elements");
			
		}catch(Exception e){
			log.debug("start readingManualUserPropertiesFromTB3Ontology failed "+e);
		}
	}
	
	private List<ManuallyMeasuredProperty> parseManualPropertiesXML(Element root,boolean bUserCreated){
		List<ManuallyMeasuredProperty> ret = new ArrayList<ManuallyMeasuredProperty>();
		NodeList nProperties = root.getChildNodes();
		for(int i=0;i<nProperties.getLength();i++){
			Node nProperty = nProperties.item(i);
			if(nProperty.getNodeName().equals("property")){
				
				//now iterate over its children to extract name and description
				NodeList nPropChilds = nProperty.getChildNodes();
				String name=null, description=null, tburi =null;
				for(int j=0;j<nPropChilds.getLength();j++){
					Node nPropChild = nPropChilds.item(j);
					if(nPropChild.getNodeName().equals("name")){
						name = nPropChild.getTextContent();
					}
					if(nPropChild.getNodeName().equals("description")){
						description = nPropChild.getTextContent();
						
					}
				}
				NamedNodeMap attributes = nProperty.getAttributes();
				if(attributes.getNamedItem("tburi")!=null){
					tburi = attributes.getNamedItem("tburi").getNodeValue();
				}
				
				//check if name and description were properly extracted
				if((name!=null)&&(description!=null)&&(tburi!=null)){
					//now create the ManuallyMeasuredProperty
					ret.add(new ManuallyMeasuredPropertyImpl(name,description,tburi,bUserCreated));
					log.debug("added propery: "+name+" "+tburi);
				}
				else{
					log.debug("error creating ManuallyMeasuredProperty for property item nr: "+i);
				}
			}
		}
		return ret;
	}
	
	/**
	 * A static utility method to create (!=store) ManuallyMeasuredProperties for a given user
	 * @param userName
	 * @param pName
	 * @param pDescription
	 * @return
	 */
	public static ManuallyMeasuredProperty createUserProperty(String userName, String pName, String pDescription){
		String uri = "planets://testbed/properties/"+userName+"/"+pName;
		ManuallyMeasuredProperty ret = new ManuallyMeasuredPropertyImpl(pName,pDescription,uri,true);
		return ret;
	}
	
	
	/**
	 * Returns a merged list of TB3ontology + user defined manually measured properties
	 */
	public List<ManuallyMeasuredProperty> loadAllManualProperties(String user){
		List<ManuallyMeasuredProperty> ret = new ArrayList<ManuallyMeasuredProperty>();
		ret.addAll(this.loadAllOntologyDerivedManualProperties());
		ret.addAll(this.loadAllUserDefinedManualProperties(user));
		return ret;
	}
	
	/**
	 * Loads all property definitions which are stored as XML in the user's storage area
	 * @return
	 */
	public List<ManuallyMeasuredProperty> loadAllUserDefinedManualProperties(String userName){
		log.info("loading all manually defined user properties for: "+userName);
		List<ManuallyMeasuredProperty> ret = new ArrayList<ManuallyMeasuredProperty>();

		try {
			URI drManagerID = DataRegistryFactory.createDataRegistryIdFromName("/experiment-files/testbed/users/"+userName).normalize();
			URI storageURI =new URI(drManagerID.getScheme(),drManagerID.getAuthority(),drManagerID.getPath()+"/config/userproperties.xml",null,null).normalize();
			
			//retrieve the user specific properties xml file from his storage space
			DigitalObject digoUserProps = dataRegistry.getDigitalObjectManager(drManagerID).retrieve(storageURI);
		
			//parse the XML and extract the Properties
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			java.io.InputStream userDefinedPropsXML = digoUserProps.getContent().getInputStream();
			Document doc = builder.parse(userDefinedPropsXML);
			Element root = doc.getDocumentElement();
			
			//read the properties from the xml-file
			ret = parseManualPropertiesXML(root,true);
			
			userDefinedPropsXML.close();
			
			log.info("successfully extracted "+ret.size() +" manual property definitions for user: "+userName);
			return ret;
			
		} catch (Exception e) {
			log.debug("unable to retrieve user specific manual properties definitions for: "+userName+" "+e);
			return ret;
		}
	}
	
	/**
	 * Returns only the list of properties - i.e. excludes the user created ones.
	 */
	public List<ManuallyMeasuredProperty> loadAllOntologyDerivedManualProperties(){
		return this.TB3ontologyProperties;
	}
	
	/**
	 * Creates and adds a ManuallyMeasuredProperty to the user's list of ManuallyMeasuredProperties.
	 * @param userName
	 * @param pName
	 * @param pDescription
	 */
	public void addManualUserProperty(String userName, String pName, String pDescription){
		ManuallyMeasuredProperty mp = createUserProperty(userName, pName, pDescription);
		this.addManualUserProperty(userName, mp);
	}
	
	/**
	 * Adds a ManuallyMeasuredProperty to the user's list of ManuallyMeasuredProperties.
	 * @param userName
	 * @param p
	 */
	public void addManualUserProperty(String userName, ManuallyMeasuredProperty p){
		
		try {
			//0. check if the xml digital object containing this information is in place already
			if(!isUserPropConfigDigoExisting(userName)){
				createAndStoreEmptyUserConfigDigo(userName);
			}
			//1. parse the xml containing the user defined property definitions
			List<ManuallyMeasuredProperty> userProps = this.loadAllUserDefinedManualProperties(userName);
			
			//2. add new property and write back to disk
			if(userProps.contains(p)){
				log.info("user property already contained - updating definition");
				//in this case we're updating
				userProps.remove(p);
				userProps.add(p);
				
			}else{
				userProps.add(p);
				log.info("added user property: "+p.toString()+"overall user created properties: "+userProps.size());
			}
			
			//3. call update on the file to persist the user's configuration
			updateUserConfigProps(userName,userProps);
			
		} catch (Exception e) {
			log.debug("was not able to add manual user property for user: "+userName+" "+e);
		}
	}

	
	/**
	 * removes a specific property from the user's config space for manual properties
	 * @param userName
	 * @param p
	 */
	public void removeManualUserProperty(String userName, ManuallyMeasuredProperty p){
		try {
			//0. check if the xml digital object containing this information is in place already
			if(!isUserPropConfigDigoExisting(userName)){
				createAndStoreEmptyUserConfigDigo(userName);
			}
			//1. parse the xml containing the user defined property definitions
			List<ManuallyMeasuredProperty> userProps = this.loadAllUserDefinedManualProperties(userName);
			
			//2. add new property and write back to disk
			if(userProps.contains(p)){
				userProps.remove(p);
				log.info("removed user property "+p.toString()+"overall user available user properties: "+userProps.size());
			}else{
				log.info("requested property for removal did not exist in user space");
			}
			
			//3. call update on the file to persist the user's configuration
			updateUserConfigProps(userName,userProps);
			
		} catch (Exception e) {
			log.debug("was not able to remove manual user property for user: "+userName+" "+e);
		}
	}
	
	/**
	 * removes a specific property from the user's config space for manual properties
	 * @param userName
	 * @param name property name or URI
	 */
	public void removeManualUserProperty(String userName, String name){
        //check if name or uri, when uri extract the proeprty's name from it
        if((name!=null)&&(userName!=null)){
        	if(name.lastIndexOf("/")!=-1){
        		name = name.substring(name.lastIndexOf("/")+1);
        	}
        	ManuallyMeasuredProperty p = this.createUserProperty(userName, name, null);
        	this.removeManualUserProperty(userName, p);
        }
	}
	

	/**
	 * removes all user created properties from his config space for manual properties
	 * @param userName
	 */
	public void removeAllManualUserProperties(String userName){
		try{
			//0. check if the xml digital object containing this information is in place already
			if(!isUserPropConfigDigoExisting(userName)){
				createAndStoreEmptyUserConfigDigo(userName);
			}
			//1. create a new empty list
			List<ManuallyMeasuredProperty> userProps = new ArrayList<ManuallyMeasuredProperty>();
	
			//3. call update on the file to persist the user's configuration
			updateUserConfigProps(userName,userProps);
			
		} catch (Exception e) {
			log.debug("was not able to remove all manual user properties for user: "+userName+" "+e);
		}
	}
	
	/**
	 * Checks if we can access the digital object containing the user's manual property definitions
	 * @param userName
	 * @return
	 */
	private boolean isUserPropConfigDigoExisting(String userName){
		try{
			URI drManagerID = DataRegistryFactory.createDataRegistryIdFromName("/experiment-files/testbed/users/"+userName).normalize();
			URI storageURI =new URI(drManagerID.getScheme(),drManagerID.getAuthority(),drManagerID.getPath()+"/config/userproperties.xml",null,null).normalize();
			//retrieve the user specific properties xml file from his storage space
			DigitalObject digoUserProps = dataRegistry.getDigitalObjectManager(drManagerID).retrieve(storageURI);
			return true;
		}catch(Exception e){
			//we need to create the user's digital object containing his manual properties
			return false;
		}
	}

	/**
	 * Creates a digital object for storing a user's manual properties
	 * in the user's config path and returns the digo's data manager URI
	 * @param userName
	 * @return
	 */
	private URI createAndStoreEmptyUserConfigDigo(String userName) throws Exception{

		URI drManagerID = DataRegistryFactory.createDataRegistryIdFromName("/experiment-files/testbed/users/"+userName).normalize();
		URI storageURI =new URI(drManagerID.getScheme(),drManagerID.getAuthority(),drManagerID.getPath()+"/config/userproperties.xml",null,null).normalize();
		
		//Create temp file.
		File temp = File.createTempFile("userproperties", ".xml");
		//create and store the digital object
		DigitalObject userpropertiesXML = new DigitalObject.Builder(Content.byValue(temp)).title("userproperties").build();
		URI uriStored = dataRegistry.getDigitalObjectManager(drManagerID).storeAsNew(storageURI,userpropertiesXML);
		temp.delete();
		log.info("created storage space for user defined manual properties");
		return uriStored;
	}
	
	
	/**
	 * Takes a list of user defined properties and writes them back to the user config space using
	 * the update operation on the digital object manager
	 * @param userName
	 * @param lProps
	 */
	private void updateUserConfigProps(String userName, List<ManuallyMeasuredProperty> lProps) throws Exception{
		URI drManagerID = DataRegistryFactory.createDataRegistryIdFromName("/experiment-files/testbed/users/"+userName).normalize();
		URI storageURI =new URI(drManagerID.getScheme(),drManagerID.getAuthority(),drManagerID.getPath()+"/config/userproperties.xml",null,null).normalize();
		
		//Create temp file, build and write xml content to file
		File temp = File.createTempFile("userproperties", ".xml");
		BufferedWriter writer = new BufferedWriter(new FileWriter(temp));
		writer.write("<userDefinedProperties>\n");
		for(ManuallyMeasuredProperty p : lProps){
			writer.write("<property tburi=\""+p.getURI()+"\">\n");
			writer.write("<name>"+p.getName()+"</name>\n");		
			writer.write("<description>"+p.getDescription()+"</description>\n");	
			writer.write("</property>\n");	
		}
		writer.write("</userDefinedProperties>");
		writer.close();
		
		//build a digital object
		DigitalObject digoManualProps = new DigitalObject.Builder(Content.byValue(temp)).title("userproperties").build();
		//call update on the digital object manager
		URI uriStored = dataRegistry.getDigitalObjectManager(drManagerID).updateExisting(storageURI, digoManualProps);
		temp.delete();
		log.info("updated storage space for user defined manual properties");
	}

}
