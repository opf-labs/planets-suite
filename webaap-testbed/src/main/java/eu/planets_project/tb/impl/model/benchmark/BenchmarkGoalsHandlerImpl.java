/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package eu.planets_project.tb.impl.model.benchmark;

//Planets Log
/*import org.apache.commons.logging.Log;
import org.apache.commons.logging.Log;
private Log log = LogFactory.getLog(this.getClass());*/
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoalsHandler;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalImpl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
@Deprecated
public class BenchmarkGoalsHandlerImpl implements BenchmarkGoalsHandler{
	
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
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoalsHandler#getAllBenchmarkGoals()
	 */
	public List<BenchmarkGoal> getAllBenchmarkGoals(){
		List<BenchmarkGoal> ret = new Vector<BenchmarkGoal>();
		Iterator<String> sKeys = this.hmBmGoals.keySet().iterator();
		while(sKeys.hasNext()){
			ret.add(getBenchmarkGoal(sKeys.next()));
		}
		return ret;
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoalsHandler#getAllBenchmarkGoals(java.lang.String)
	 */
	public List<BenchmarkGoal> getAllBenchmarkGoals(String sCategoryName){
		//all benchmark goals for a given category
		List<BenchmarkGoal> ret = new Vector<BenchmarkGoal>();
		Iterator<BenchmarkGoal> itTemplates = this.hmBmGoals.values().iterator();
		while(itTemplates.hasNext()){
			BenchmarkGoal template = itTemplates.next(); 
			if(template.getCategory().equals(sCategoryName))
				ret.add(this.getBenchmarkGoal(template.getID()));
		}
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoalsHandler#getAllBenchmarkGoalIDs(java.lang.String)
	 */
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
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoalsHandler#getAllBenchmarkGoalIDs()
	 */
	public List<String> getAllBenchmarkGoalIDs() {
		List<String> ret = new Vector<String>();
		Iterator<BenchmarkGoal> itGoals = this.hmBmGoals.values().iterator();
		while(itGoals.hasNext()){
			BenchmarkGoal goal = itGoals.next(); 
			ret.add(goal.getID());
		}
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoalsHandler#getBenchmarkGoal(java.lang.String)
	 */
	public BenchmarkGoal getBenchmarkGoal(String sID){
		//returns a new BenchmarkGoal instance from a template
		BenchmarkGoalImpl ret = new BenchmarkGoalImpl();
		BenchmarkGoal template = this.hmBmGoals.get(sID);
		if(template !=null){
			ret = ((BenchmarkGoalImpl)this.hmBmGoals.get(sID)).clone();
			/*ret.setCategory(template.getCategory());
			ret.setDefinition(template.getDefinition());
			ret.setDescription(template.getDescription());
			ret.setName(template.getName());
			ret.setScale(template.getScale());
			ret.setType(template.getType());
			ret.setVersion(template.getVersion());
			ret.setID(template.getID());*/
			
			return ret;
		}
		else{
			return null;
		}
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoalsHandler#getCategoryNames()
	 */
	public List<String> getCategoryNames(){
		return this.vCategoryNames;
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.BenchmarkGoalsHandler#buildBenchmarkGoalsFromXML()
	 */
	public void buildBenchmarkGoalsFromXML(){
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
				this.parseBenchmarkGoals(itCategoryNames.next());

			BenchmarkFile.close();
			
		}catch(Exception e){
			//TODO throw new exception and/or write log statement
			System.out.println("BuildBenchmarkGoalsFromXML failed");
		}
	}
	
	
	//e.g. get all items for the category "Text"
	private void parseBenchmarkGoals(String sCategoryName){
		 NodeList categories = root.getElementsByTagName("category");
		 
		 for(int k=0;k<categories.getLength();k++){
			 Node category = categories.item(k);
			 NodeList items = category.getChildNodes();
			 
			 //now check if this is the category we're looking for
			 NamedNodeMap catattributes = category.getAttributes();
			 String sCategory = catattributes.getNamedItem("name").getNodeValue();
			 if(sCategory.equals(sCategoryName)){   
				 //now parse items
				 for(int i=0;i<items.getLength();i++){
					 BenchmarkGoalImpl bmGoal = new BenchmarkGoalImpl();
					 Node item = items.item(i);

					 if(item.getNodeName().equals("item") && item.getNodeType() == Node.ELEMENT_NODE){
						 //Now within an item (=benchmarkGoal)
						 NamedNodeMap attributes = item.getAttributes();
						 bmGoal.setCategory(sCategoryName);
						 bmGoal.setID(attributes.getNamedItem("id").getNodeValue());
						 bmGoal.setType(attributes.getNamedItem("type").getNodeValue());
						 bmGoal.setScale(attributes.getNamedItem("scale").getNodeValue());
						 bmGoal.setVersion(attributes.getNamedItem("version").getNodeValue());
						 //now get childNodesw: definition and description
						 NodeList itemchilds =  item.getChildNodes();
						 for(int j=0;j<itemchilds.getLength();j++){
							 if(itemchilds.item(j).getNodeType() == Node.ELEMENT_NODE){
								 if(itemchilds.item(j).getNodeName().equals("definition")){
									 bmGoal.setDefinition(itemchilds.item(j).getTextContent());
								 }
								 if(itemchilds.item(j).getNodeName().equals("description")){
									 bmGoal.setDescription(itemchilds.item(j).getTextContent());
								 }
								 if(itemchilds.item(j).getNodeName().equals("name")){
									 bmGoal.setName(itemchilds.item(j).getTextContent());
								 }
							 }
						 }
					 
						 if(this.hmBmGoals.containsKey(bmGoal.getID()))
							 this.hmBmGoals.remove(bmGoal.getID());
						 this.hmBmGoals.put(bmGoal.getID(), bmGoal);
					 }
				 }
			 }
			 //End Check if correct category name
		 }
		 
	}
	
	
	private Vector<String> parseCategoryNames(){
		Vector<String> vRet = new Vector<String>();
		NodeList cathegories = root.getChildNodes();
		for(int i=0;i<cathegories.getLength();i++){
			Node category = cathegories.item(i);
			//We only want to add Element Nodes and not Text Nodes
			if((category.getNodeType() == Node.ELEMENT_NODE)&&(category.getNodeName().equals("category"))){
				NamedNodeMap attributes = category.getAttributes();
				vRet.add(attributes.getNamedItem("name").getNodeValue());
			}
		}
		return vRet;
	}


}
