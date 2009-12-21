/*******************************************************************************
 * Copyright (c) 2007, 2008 The Planets Project Partners.
 * 
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * GNU Lesser General Public License v3 which 
 * accompanies this distribution, and is available at 
 * http://www.gnu.org/licenses/lgpl.html
 * 
 *******************************************************************************/
/**
 * 
 */

package eu.planets_project.services.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Logger;

import eu.planets_project.ifr.core.common.conf.PlanetsServerConfig;

//TODO move into utility package 
/**
 * Utility class to provide content via a web server   
 * Requires external access to a web server. JBoss must be started with option <tt>-b 0.0.0.0</tt>.
 * Default values can be overwritten by providing a properties file at <i>/WEB-INF/classes/content.properties</i>  
 */

public class WebContentHelper {
	
    //overwrites default server to resolve content
    public static final String CONTENT_PROPERTIES_PATH = "/WEB-INF/classes/content.properties";
    public static final String WEB_ACCESS_PATH = "web_access_path";
    public static final String TEMP_DIR = "planets_tmp_files";
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String LIFETIME = "content_lifetime_in_seconds";
    
    private static Logger logger = Logger.getLogger(WebContentHelper.class.getName());
    
    private String webAccessPath = null;
    private String workingPath = null;
    private String fs = System.getProperty("file.separator");

	private String host = null;
	private int port = -1;
    private long lifetime = 3600L;
    
    public WebContentHelper() throws UnknownHostException {
    	host = PlanetsServerConfig.getHostname();
    	port = PlanetsServerConfig.getPort();
    	init();
    }
    
    private void init() throws UnknownHostException {
    	    	
    	//default path
    	try {
    		String binPath = new File("").getAbsolutePath();
    		String serverPath = binPath.substring(0,binPath.lastIndexOf(fs));
    		webAccessPath = new File(new File(serverPath), "/server/default/deploy/jboss-web.deployer/ROOT.war").getAbsolutePath();
    		logger.info("default web access path: "+webAccessPath);
    	} catch(Exception e) {
    		logger.info("Could not determine default web access path: "+e);
    	}
    	
    	//overwrite if a config file is available
    	loadProperties();

    	//resolve ip address
    	//host = java.net.InetAddress.getByName(host).getHostAddress();

    	//check webAccessPath
    	if(!new File(webAccessPath).exists()) {
            throw new RuntimeException("web access path is not existing: " + webAccessPath);
		}
		
    	//create tmp dir if not existing
    	workingPath = webAccessPath+fs+TEMP_DIR;
		if(!new File(workingPath).exists()) {
			if(!new File(workingPath).mkdir()) {
	            throw new RuntimeException("Cannot create tmp directory: " + workingPath);				
			}
		}
		
		cleanOutdatedFiles();
    }

	    
    /**
     * Copies a file into a public html directory and returns an http URL for it.
     */
	public URL copyIntoHTMLDirectory(File file) throws java.net.MalformedURLException, java.io.IOException {
		
		String fileName = file.getName();
		String sessionDir = fileName+"_"+System.currentTimeMillis()%10000;
		new File(workingPath+fs+sessionDir).mkdir();
		file.renameTo(new File(workingPath+fs+sessionDir+fs+fileName));
		return new URL("http", host, port, "/"+TEMP_DIR+"/"+sessionDir+"/"+fileName); 
	}
	    
    public String getWebAccessPath() {
		return webAccessPath;
	}
    
	public int getPort() {
		return port;
	}

    public String getHost() {
		return host;
	}
    
    /**
     * Loads properties for accessing the web server from <tt>content.properties</tt>
     * */
    protected void loadProperties() {

        Properties properties = new Properties();        
        
        try {
        	properties.load(this.getClass().getResourceAsStream(CONTENT_PROPERTIES_PATH));
        	if(properties == null) throw new IOException();
        } catch(Exception e) {
        	logger.info("Unable to load content properties file: " + CONTENT_PROPERTIES_PATH);
        	return;
        }
        
        if (properties.containsKey(WEB_ACCESS_PATH)) {
        	webAccessPath = properties.getProperty(WEB_ACCESS_PATH);
        	logger.info("found web access path: "+webAccessPath);
        }
        
        //overwrite, if available
        if (properties.containsKey(HOST)) {
        	host = properties.getProperty(HOST);
        }
        
        //overwrite, if available
        if (properties.containsKey(PORT)) {
        	port = Integer.parseInt(properties.getProperty(PORT));
        }

        //overwrite, if available
        if (properties.containsKey(LIFETIME)) {
        	lifetime = Long.parseLong(properties.getProperty(LIFETIME));
        }
        
    }

    public void cleanOutdatedFiles() {
    	File workingDir = new File(workingPath);
    	logger.info("1_files: "+workingDir.listFiles().length);
    	for (File f : workingDir.listFiles()) {
            long lastModified = f.lastModified();
            logger.info("current_life_time: "+ (System.currentTimeMillis()-lastModified) / 1000);
            if( ((System.currentTimeMillis()-lastModified) / 1000) > lifetime ) {
            	deleteDir(f);
            }
    	}    	
	}

    private boolean deleteDir(File dir) {
    	if (dir.isDirectory()) {
    		String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
            	boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                	return false;
                }
            }
    	}
        return dir.delete();
	}
}
