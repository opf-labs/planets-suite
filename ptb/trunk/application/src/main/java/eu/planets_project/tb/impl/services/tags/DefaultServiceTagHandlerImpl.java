/**
 * 
 */
package eu.planets_project.tb.impl.services.tags;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.api.services.tags.DefaultServiceTagHandler;
import eu.planets_project.tb.api.services.tags.ServiceTag;
import eu.planets_project.tb.impl.CommentManagerImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalsHandlerImpl;

/**
 * @author Andrew Lindley, ARC
 * Parses a list of all supported ServiceTags from the back-end ressource files
 * This class does not retrieve custom client created tags
 */
public class DefaultServiceTagHandlerImpl implements DefaultServiceTagHandler{

	//Map<Id,DefaultServiceTag>
	private Map<String,ServiceTag> hmDefaultTags; 	
	private Element root;
	private static DefaultServiceTagHandlerImpl instance;
	private static Log log;
	
	private DefaultServiceTagHandlerImpl(){
		log = LogFactory.getLog(this.getClass());
		hmDefaultTags = new HashMap<String,ServiceTag>();
		
		//parse XML and build up a list of default tags
		buildTagsFromXML();
	}
	
	public static synchronized DefaultServiceTagHandlerImpl getInstance(){
		if (instance == null){
			instance = new DefaultServiceTagHandlerImpl();
		}
		return instance;
	}
	
	/**
	 * @param readXML: indicates if the XML source shall be parsed and read again for changes
	 * @return
	 */
	public static synchronized DefaultServiceTagHandlerImpl getInstance(boolean readXML){
		if (readXML)
			instance = new DefaultServiceTagHandlerImpl();
		return instance;
	}
	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.tags.DefaultServiceTagHandler#getAllIDsAndTags()
	 */
	public Map<String, ServiceTag> getAllIDsAndTags() {
		return this.hmDefaultTags;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.tags.DefaultServiceTagHandler#getAllTags()
	 */
	public Collection<ServiceTag> getAllTags() {
		return this.hmDefaultTags.values();
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.tags.DefaultServiceTagHandler#getMaxPriority(int)
	 * This information is currently not extracted from the underlying file
	 */
	public int getMaxPriority(int i) {
		return 3;
		
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.tags.DefaultServiceTagHandler#getMinPriority(int)
	 * This information is currently not extracted from the underlying file
	 */
	public int getMinPriority(int i) {
		return 1;
		
	}
	
	public void buildTagsFromXML() {
		try{
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			//Document doc = builder.parse(new File("C:/DATA/Implementation/SVN_Planets/ptb/trunk/application/src/main/resources/eu/planets_project/tb/impl/ServiceAnnotationTags.xml"));
			java.io.InputStream TagsFile = getClass().getClassLoader().getResourceAsStream("eu/planets_project/tb/impl/ServiceAnnotationTags.xml");
			Document doc = builder.parse(TagsFile);
			root = doc.getDocumentElement();
			
			//parse default tags
			parseTags();
			
			TagsFile.close();
			
		}catch(Exception e){
			//TODO throw new exception and/or write log statement
			log.error("BuildServiceTagsFromXML failed");
		}
		
	}
	
	private void parseTags(){
		 int defaultPriority = 2; 
		 NodeList tags = root.getElementsByTagName("tag");
		 
		 for(int k=0;k<tags.getLength();k++){
			 ServiceTagImpl serTag = new ServiceTagImpl();
			 Node tag = tags.item(k);
			 NodeList tag_children = tag.getChildNodes();
			 
			 NamedNodeMap tagattributes = tag.getAttributes();
			 try{
				 //priority not mandatory - may have a default value
				 int priority = Integer.parseInt(tagattributes.getNamedItem("priority").getNodeValue());
				 serTag.setPriority(priority);
			 }
			 catch(Exception e){
				 serTag.setPriority(defaultPriority);
			 }
			 
			 for(int i=0;i<tag_children.getLength();i++){
				Node child = tag_children.item(i);
				 if(child.getNodeName().equals("name") && child.getNodeType() == Node.ELEMENT_NODE){
					 //set name
					 serTag.setName(child.getTextContent());
				 }
				 if(child.getNodeName().equals("description") && child.getNodeType() == Node.ELEMENT_NODE){
					 //set description
					 serTag.setDescription(child.getTextContent());
				 }
			 }
			 //store tag, name is id
			 this.hmDefaultTags.put(serTag.getName(), serTag);
		 }
		 
	}

}
