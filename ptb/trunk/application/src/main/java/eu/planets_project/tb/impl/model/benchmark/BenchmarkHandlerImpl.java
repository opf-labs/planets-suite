package eu.planets_project.tb.impl.model.benchmark;

//Planets Logger
/*import org.apache.commons.logging.Log;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
private Log log = PlanetsLogger.getLogger(this.getClass(),"testbed-log4j.xml");*/
import eu.planets_project.tb.api.model.benchmark.BenchmarkHandler;
import eu.planets_project.tb.api.model.benchmark.Benchmark;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkImpl;

import javax.ejb.Stateless;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.PrintStream;
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
public class BenchmarkHandlerImpl implements BenchmarkHandler{
	
	private Element root;
	private Vector<String> vCategoryNames;
	//HashMap<Id,Benchmark>
	private HashMap<String,Benchmark> hmBmGoals; 	
	private static BenchmarkHandlerImpl instance;
	
	private BenchmarkHandlerImpl(){
		//initialize Variables
		vCategoryNames = new Vector<String>();
		hmBmGoals = new HashMap<String,Benchmark>();
		
		//parse XML and build up BenchmarkObjectivesList
		buildBenchmarksFromXML();
	}
	
	public static synchronized BenchmarkHandlerImpl getInstance(){
		if (instance == null){
			instance = new BenchmarkHandlerImpl();
		}
		return instance;
	}
	
	/**
	 * @param readXML: indicates if the XML source shall be parsed and read again for changes
	 * @return
	 */
	public static synchronized BenchmarkHandlerImpl getInstance(boolean readXML){
		if (readXML)
			instance = new BenchmarkHandlerImpl();
		return instance;
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkHandler#getAllBenchmarkGoals()
	 */
	public List<Benchmark> getAllBenchmarks(){
		List<Benchmark> ret = new Vector<Benchmark>();
		Iterator<String> sKeys = this.hmBmGoals.keySet().iterator();
		while(sKeys.hasNext()){
			ret.add(getBenchmark(sKeys.next()));
		}
		return ret;
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkHandler#getAllBenchmarkGoals(java.lang.String)
	 */
	public List<Benchmark> getAllBenchmarks(String sCategoryName){
		//all benchmark goals for a given category
		List<Benchmark> ret = new Vector<Benchmark>();
		Iterator<Benchmark> itTemplates = this.hmBmGoals.values().iterator();
		while(itTemplates.hasNext()){
			Benchmark template = itTemplates.next(); 
			if(template.getCategory().equals(sCategoryName))
				ret.add(this.getBenchmark(template.getID()));
		}
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkHandler#getAllBenchmarkGoalIDs(java.lang.String)
	 */
	public List<String> getAllBenchmarkIDs(String sCategoryName){
		List<String> ret = new Vector<String>();
		Iterator<Benchmark> itGoals = this.hmBmGoals.values().iterator();
		while(itGoals.hasNext()){
			Benchmark goal = itGoals.next(); 
			if(goal.getCategory().equals(sCategoryName))
				ret.add(goal.getID());
		}
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkHandler#getAllBenchmarkGoalIDs()
	 */
	public List<String> getAllBenchmarkIDs() {
		List<String> ret = new Vector<String>();
		Iterator<Benchmark> itGoals = this.hmBmGoals.values().iterator();
		while(itGoals.hasNext()){
			Benchmark goal = itGoals.next(); 
			ret.add(goal.getID());
		}
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkHandler#getBenchmarkGoal(java.lang.String)
	 */
	public Benchmark getBenchmark(String sID){
		//returns a new Benchmark instance from a template
		BenchmarkImpl ret = new BenchmarkImpl();
		Benchmark template = this.hmBmGoals.get(sID);
		if(template !=null){
			ret.setCategory(template.getCategory());
			ret.setDefinition(template.getDefinition());
			ret.setDescription(template.getDescription());
			ret.setName(template.getName());
			ret.setScale(template.getScale());
			ret.setType(template.getType());
			ret.setVersion(template.getVersion());
			ret.setID(template.getID());
			
			return ret;
		}
		else{
			return null;
		}
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkHandler#getCategoryNames()
	 */
	public List<String> getCategoryNames(){
		return this.vCategoryNames;
	}
	
	
	public void buildBenchmarksFromXML(){
		try{
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			//Document doc = builder.parse(new File("C:/DATA/Implementation/SVN_Planets/ptb/trunk/application/src/main/resources/eu/planets_project/tb/impl/BenchmarkGoals.xml"));
			java.io.InputStream BenchmarkFile = getClass().getClassLoader().getResourceAsStream("eu/planets_project/tb/impl/BenchmarkGoals.xml");
			Document doc = builder.parse(BenchmarkFile);
			root = doc.getDocumentElement();
			
			//read Category Names (e.g. Text, Image, etc.)
			this.vCategoryNames = this.parseCategoryNames();
			Iterator<String> itCategoryNames = vCategoryNames.iterator();
			//parse benchmark goals
			while(itCategoryNames.hasNext())
				this.parseBenchmarks(itCategoryNames.next());
			
			BenchmarkFile.close();
			
		}catch(Exception e){
			//TODO throw new exception and/or write log statement
			System.out.println("BuildBenchmarkGoalsFromXML failed");
		}
	}
	
	
	//e.g. get all items for the category "Text"
	private void parseBenchmarks(String sCategoryName){
		 NodeList categories = root.getElementsByTagName(sCategoryName);
		 
		 for(int k=0;k<categories.getLength();k++){
			 Node category = categories.item(k);
			 NodeList items = category.getChildNodes();
			 //now parse items
			
			 for(int i=0;i<items.getLength();i++){
				 BenchmarkImpl bmGoal = new BenchmarkImpl();
				 Node item = items.item(i);

				 if(item.getNodeName().equals("item") && item.getNodeType() == Node.ELEMENT_NODE){
					
					 //Now within an item (=benchmarkGoal)
					 NamedNodeMap attributes = item.getAttributes();
					 bmGoal.setCategory(sCategoryName);
					 bmGoal.setID(attributes.getNamedItem("id").getNodeValue());
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


}
