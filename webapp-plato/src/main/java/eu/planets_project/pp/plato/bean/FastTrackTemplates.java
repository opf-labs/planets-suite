/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/
package eu.planets_project.pp.plato.bean;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remove;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;


@Scope(ScopeType.SESSION)
@Name("fastTrackTemplates")
public class FastTrackTemplates {

    @Out(required=false)
    private List<FastTrackTemplate> templateList = new ArrayList<FastTrackTemplate>();
    
    @In(required=false)
    @Out(required=false)
    private FastTrackTemplate fastTrackTemplate = null;;
    
    private String directory;
    
    public FastTrackTemplates() {
        
    }
    
    public void init() {
        directory = "data/templates/fasttrack";
        
        URL url = Thread.currentThread().getContextClassLoader().getResource(directory);
        
        File dir = new File(url.getFile());
        
        templateList = iterateFiles(dir.listFiles());
    }
    
    private List<FastTrackTemplate> iterateFiles(File[] files) {
        
        List<FastTrackTemplate> list = new ArrayList<FastTrackTemplate>();
        
        if (files == null) {
            return list;
        }
        
        for (File f : files) {
            if (f.isDirectory()) {
                list.addAll(iterateFiles(f.listFiles()));
            } else {          
                FastTrackTemplate ftt  = new FastTrackTemplate();
                ftt.setAbsolutePath(f.getAbsolutePath());
                
                String absolutePath = f.getAbsolutePath();
                int start = absolutePath.lastIndexOf(directory) + directory.length();
                
                
                String displayString = absolutePath.substring(start+1, absolutePath.length()-f.getName().length()-1);
                
                ftt.setDisplayString(displayString);
                list.add(ftt);
            }
        }
        
        return list;
    }
    

    public List<FastTrackTemplate> getTemplateList() {
        return templateList;
    }

    public void setTemplateList(List<FastTrackTemplate> templateList) {
        this.templateList = templateList;
    }

    public FastTrackTemplate getFastTrackTemplate() {
        return fastTrackTemplate;
    }

    public void setFastTrackTemplate(FastTrackTemplate fastTrackTemplate) {
        this.fastTrackTemplate = fastTrackTemplate;
    }
    
    @Destroy
    @Remove
    public void destroy() {
    }    
}
