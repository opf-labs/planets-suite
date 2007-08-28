package eu.planets_project.tb.impl.model.benchmark;

//Planets Logger
/*import org.apache.commons.logging.Log;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
private Log log = PlanetsLogger.getLogger(this.getClass(),"testbed-log4j.xml");*/


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;

public class BencharmkGoalsHandlerImpl {
	
	public static void main (String[] args) {
		

	    try {
	  
	      DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
	
	      DocumentBuilder builder = dbfactory.newDocumentBuilder();
	   
	      File f1 = new File("C:/DATA/Implementation/SVN_Planets/ptb/trunk/application/src/main/resources/eu/planets_project/tb/impl/BencharmGoals.xml");
	      Document doc = builder.parse(f1);
	      System.out.println("absolute path "+f1.getAbsolutePath());
	      System.out.println("canonical path "+f1.getCanonicalPath());
	      System.out.println("can read? "+f1.canRead());
	      
	      Element root = doc.getDocumentElement();
	     
	      System.out.print("gedNodeValue: "+root.getFirstChild().getNodeValue());
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	}

}
