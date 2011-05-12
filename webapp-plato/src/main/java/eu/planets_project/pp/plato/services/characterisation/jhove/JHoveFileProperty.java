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
package eu.planets_project.pp.plato.services.characterisation.jhove;

import java.util.Vector;

/**
 * Class that contains all the information of a Jhove digestion
 * 
 * @author riccardo
 * 
 */
public class JHoveFileProperty {

    String extractionDate; // /jhove/date

    String fileURI; // /jhove/repInfo[uri]

    Long fileSize; // /jhove/repInfo/size

    String format; // /jhove/repInfo/format

    String version; // /jhove/repInfo/version

    String status; // muss enum werden /jhove/repInfo/status

    String mimetype; // /jhove/repInfo/mimeType

    String jhoveModuleName;

    Module module;

    Vector<String> profiles;

    Vector<Property> properties;

    public JHoveFileProperty() {
        super();
    }

    public String getJhoveModuleName() {
        return jhoveModuleName;
    }

    public void setJhoveModuleName(String jhoveModuleName) {
        this.jhoveModuleName = jhoveModuleName;
    }

    public String getFormat() {
        return format;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public Vector<Property> getProperties() {
        return properties;
    }

    public void setProperties(Vector properties) {
        this.properties = properties;
    }

    public void setProfiles(Vector profiles) {
        this.profiles = profiles;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getExtractionDate() {
        return extractionDate;
    }

    public void setExtractionDate(String extractionDate) {
        this.extractionDate = extractionDate;
    }

    public String getFileURI() {
        return fileURI;
    }

    public void setFileURI(String fileURI) {
        this.fileURI = fileURI;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public Vector<String> getProfiles() {
        return profiles;
    }

    @Override
    public String toString() {
        return "EXTR. Date:" + extractionDate + "\n Format-Myme:" + mimetype +
        // "\n Module:"+module.getName()+
                "\n Profiles:" + profiles + "\n Properties:" + properties;
    }

}
