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

package eu.planets_project.pp.plato.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.jboss.seam.faces.FacesMessages;

import eu.planets_project.pp.plato.model.DigitalObject;

/**
 * Starts download of a file. Implemented as singleton.
 *
 * @author Hannes Kulovits
 */
public class Downloader {

    private static Downloader down;

    private static final Log log = PlatoLogger.getLogger(Downloader.class);

    private Downloader(){
    }

    /**
     * Get instance of the Downloader.
     *
     * @return Download instance.
     */
    public static Downloader instance(){
        if(down == null){
            down = new Downloader();
        }
        return down;
    }
    
    public void downloadMM(String xml,String name) {
        download(xml.getBytes(),name,"application/freemind");
    }
    
    public void download(DigitalObject object) {
        download(object.getData().getData(),object.getFullname(),object.getContentType());
    }
    
    public void download(DigitalObject object,String filename) {
        byte[] data;
        try {
            data = FileUtils.getBytesFromFile(new File(filename));
            download(data,
                    object.getFullname(),
                    object.getContentType());
        } catch (IOException e) {
            log.error(e);
        }
    }
    
//    public void download(ByteStream data, String fileName, String contentType){
//        download(data.getData(),fileName,contentType);
//    }

    /**
     * Starts a client side download. All information provided by parameters.
     *
     * @param file data file contains
     * @param fileName name of the file (e.g. report.pdf)
     * @param contentType mime type of the content to be downloaded
     */
    public void download(byte[] file, String fileName, String contentType){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext context = facesContext.getExternalContext();

        HttpServletResponse response = (HttpServletResponse) context
                .getResponse();
        response.setHeader("Content-Disposition", "attachment;filename=\""
                + fileName + "\"");
        response.setContentLength((int) file.length);
        response.setContentType(contentType);

        try {
            ByteArrayInputStream in = new ByteArrayInputStream(file);
            OutputStream out = response.getOutputStream();

            // Copy the contents of the file to the output stream
            byte[] buf = new byte[1024];
            int count;
            while ((count = in.read(buf)) >= 0) {
                out.write(buf, 0, count);
            }
            in.close();
            out.flush();
            out.close();
            facesContext.responseComplete();
        } catch (IOException ex) {
            log.error("Error in downloadFile: " + ex.getMessage());
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR,
                    "Download couldn't be executed");
            ex.printStackTrace();
        }
    }
}
