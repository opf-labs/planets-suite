package eu.planets_project.ifr.core.wee.impl.utils;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.wee.impl.mockup.DataRegistryMockup;

public class RegistryUtils {
	
	private static RegistryUtils instance =null;
	private RegistryUtils(){
		readProperties();
	}

	/**
	 * Converts e.g. "eu.planets_project.ifr.core.wee.sample.wf.DroidWorkflowTemplate"
	 * to: "eu/planets_project/ifr/core/wee/sample/wft/DroidWorkflowTemplate.java"
	 * Currently no PURI prefix information is considered!
	 * @param sJavaQName
	 * @return
	 */
	public static String convertQNameToRegistryPathPURI(String sJavaQName){
		String ret ="";
		StringTokenizer tokenizer = new StringTokenizer(sJavaQName,".",false);
		int count = tokenizer.countTokens();
		for(int i=0;i<count;i++){
			String t = tokenizer.nextToken();
			if(i<count-1)
				ret+=t+"/";
			else
				ret+=t+".java";
		}
		return ret;
	}
	
	/**
	 * Converts e.g. "eu/planets_project/ifr/core/wee/sample/wft/DroidWorkflowTemplate.java"
	 * to: "eu.planets_project.ifr.core.wee.sample.wf.DroidWorkflowTemplate"
	 * Currently no PURI prefix information is considered!
	 * @param sJavaQName
	 * @return
	 */
	public static String convertRegistryPDURIPathToQName(URI registryPath){
		String ret ="";
		StringTokenizer tokenizer = new StringTokenizer(registryPath+"","/",false);
		int count = tokenizer.countTokens();
		for(int i=0;i<count;i++){
			String t = tokenizer.nextToken();
			if(i<count-1)
				ret+=t+".";
			else{
				if(t.endsWith(".java")){
					t = t.substring(0,t.lastIndexOf(".java"));
				}
				ret+=t;
			}
		}
		return ret;
	}
	
	private static String weeDirBase="",weewfTemplatesDir="",weeTmpDir="";
	private static final Log log = LogFactory.getLog(RegistryUtils.class);
	
	private void readProperties(){
	        Properties properties = new Properties();

	        try {
	            java.io.InputStream ResourceFile = getClass().getClassLoader()
	                    .getResourceAsStream(
	                            "eu/planets_project/ifr/core/wee/BackendResources.properties"
	                    );
	            properties.load(ResourceFile); 
	            
	            //Note: sFileDirBaase = ifserver/bin/../server/default/deploy/jbossweb-tomcat55.sar/ROOT.war
	            weeDirBase = properties.getProperty("Jboss.wee.DirBase");
	            weewfTemplatesDir = properties.getProperty("JBooss.wee.wfTemplatesDir");
	            weeTmpDir = properties.getProperty("JBoss.wee.tmpDir");
	            ResourceFile.close();
	            
	        } catch (IOException e) {
	            log.fatal("read Jboss.wfTemplatesDirBase from BackendResources.properties failed!"+e.toString());
	        }
	}
	
	/**
	 * Jboss.wee.DirBase = ../server/default/data/wee
	 * @return
	 */
	public static String getWeeDirBase(){
		checkInstance();
		return weeDirBase;
	}
	
	/**
	 * JBooss.wee.wfTemplatesDir = /wfTemplates
	 * @return
	 */
	public static String getWeeWFTemplateDir(){
		checkInstance();
		return weewfTemplatesDir;
	}
	
	/**
	 * JBoss.wee.tmpDir = /temp
	 * @return
	 */
	public static String getWeeTmpDir(){
		checkInstance();
		return weeTmpDir;
	}
	
	private static void checkInstance(){
		if (instance ==null)
			instance = new RegistryUtils();
	}
}
