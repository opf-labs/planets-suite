package eu.planets_project.tb.impl.model.benchmark;

//Planets Logger
/*import org.apache.commons.logging.Log;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
private Log log = PlanetsLogger.getLogger(this.getClass(),"testbed-log4j.xml");*/
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoalsHandler;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalImpl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * @author alindley
 * 
 * An example of the underlying XML structure:
 * <BenachmarkGoals>
 * 	<Text>
 * 		<item id="nop1" name="number of pages" type="java.lang.Integer" scale="1..n">
 * 			<definition>identifies the number of pages</definition>
 * 			<description>Count the number of pages including the front-page</description>
 * 		</item>
 * 	</Text>
 * </BenachmarkGoals>
 **/

public class BenchmarkGoalsHandlerImpl implements BenchmarkGoalsHandler{
	
	private List<BenchmarkGoal> benchmarkGoals = new Vector<BenchmarkGoal>();
	private Element root;
	private Vector<String> vCategoryNames;
	//HashMap<Id,BenchmarkGoal>
	private HashMap<String,BenchmarkGoal> hmBmGoals; 
	private static BenchmarkGoalsHandlerImpl instance;
	
	
	private BenchmarkGoalsHandlerImpl(){
		//initialize Variables
		vCategoryNames = new Vector<String>();
		hmBmGoals = new HashMap<String,BenchmarkGoal>();
		
		//parse XML and build up BenchmarkObjectivesList
		buildBenchmarkGoalsFromXML();
	}
	
	public static synchronized BenchmarkGoalsHandlerImpl getInstance(){
		if (instance == null){
			instance = new BenchmarkGoalsHandlerImpl();
		}
		return instance;
	}
	
	/**
	 * @param readXML: indicates if the XML source shall be parsed and read again for changes
	 * @return
	 */
	public static synchronized BenchmarkGoalsHandlerImpl getInstance(boolean readXML){
		if (readXML)
			instance = new BenchmarkGoalsHandlerImpl();
		return instance;
	}

	public void buildBenchmarkGoalsFromXML(){
		try{
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			Document doc = builder.parse(new File("C:/DATA/Implementation/SVN_Planets/ptb/trunk/application/src/main/resources/eu/planets_project/tb/impl/BenchmarkGoals.xml"));
			root = doc.getDocumentElement();
			
			//read Category Names (e.g. Text, Image, etc.)
			this.vCategoryNames = this.parseCategoryNames();
			Iterator<String> itCategoryNames = vCategoryNames.iterator();
			//parse benchmark objectives
			while(itCategoryNames.hasNext())
				this.parseBenchmarkGoals(itCategoryNames.next());
			
		}catch(Exception e){
			//TODO throw new exception and/or write log statement
		}
	}
	
	//e.g. get all items for the category "Text"
	private void parseBenchmarkGoals(String sCategoryName){
		 NodeList categories = root.getElementsByTagName(sCategoryName);
		 
		 for(int k=0;k<categories.getLength();k++){
			 Node category = categories.item(k);
			 NodeList items = category.getChildNodes();
			 //now parse items
			
			 for(int i=0;i<items.getLength();i++){
				 BenchmarkGoalImpl bmGoal = new BenchmarkGoalImpl();
				 Node item = items.item(i);

				 if(item.getNodeName().equals("item") && item.getNodeType() == Node.ELEMENT_NODE){
					
					 //Now within an item (=benchmarkGoal)
					 NamedNodeMap attributes = item.getAttributes();
					 bmGoal.setCategory(sCategoryName);
					 bmGoal.setXMLID(attributes.getNamedItem("id").getNodeValue());
					 bmGoal.setName(attributes.getNamedItem("name").getNodeValue());
					 bmGoal.setType(attributes.getNamedItem("type").getNodeValue());
					 bmGoal.setScale(attributes.getNamedItem("scale").getNodeValue());
					 bmGoal.setVersion(attributes.getNamedItem("version").getNodeValue());
					 //now get childNodesw: definition and description
					 NodeList itemchilds =  item.getChildNodes();
					 for(int j=0;j<itemchilds.getLength();j++){
						if(itemchilds.item(j).getNodeType() == Node.ELEMENT_NODE){
							if(itemchilds.item(j).getNodeName().equals("definition"))
								bmGoal.setDefinition(itemchilds.item(j).getTextContent());
							if(itemchilds.item(j).getNodeName().equals("description"))
								bmGoal.setDescription(itemchilds.item(j).getTextContent());
						}
					 }
					 
					 if(this.hmBmGoals.containsKey(bmGoal.getID()))
					 	this.hmBmGoals.remove(bmGoal.getID());
					 this.hmBmGoals.put(bmGoal.getID(), bmGoal);
				 }
		      }
		 }
		 
	}
	
	public List<BenchmarkGoal> getAllBenchmarkGoals(){
		List<BenchmarkGoal> ret = new Vector<BenchmarkGoal>();
		Iterator<BenchmarkGoal> itGoals = this.hmBmGoals.values().iterator();
		while(itGoals.hasNext()){
			ret.add(itGoals.next());
		}
		return ret;
	}
	
	//all benchmark goals for a given category
	public List<BenchmarkGoal> getAllBenchmarkGoals(String sCategoryName){
		List<BenchmarkGoal> ret = new Vector<BenchmarkGoal>();
		Iterator<BenchmarkGoal> itGoals = this.hmBmGoals.values().iterator();
		while(itGoals.hasNext()){
			BenchmarkGoal goal = itGoals.next(); 
			if(goal.getCategory().equals(sCategoryName))
				ret.add(goal);
		}
		return ret;
	}
	
	public List<String> getAllBenchmarkGoalIDs(String sCategoryName){
		List<String> ret = new Vector<String>();
		Iterator<BenchmarkGoal> itGoals = this.hmBmGoals.values().iterator();
		while(itGoals.hasNext()){
			BenchmarkGoal goal = itGoals.next(); 
			if(goal.getCategory().equals(sCategoryName))
				ret.add(goal.getID());
		}
		return ret;
	}
	
	public BenchmarkGoal getBenchmarkGoal(String sID){
		return this.hmBmGoals.get(sID);
	}
	
	private Vector<String> parseCategoryNames(){
		Vector<String> vRet = new Vector<String>();
		NodeList cathegories = root.getChildNodes();
		for(int i=0;i<cathegories.getLength();i++){
			Node category = cathegories.item(i);
			//We only want to add Element Nodes and not Text Nodes
			if(category.getNodeType() == Node.ELEMENT_NODE){
				vRet.add(category.getNodeName());
			}
		}
		return vRet;
	}
	
	public List<String> getCategoryNames(){
		return this.vCategoryNames;
	}

}
