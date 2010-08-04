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

package eu.planets_project.pp.plato.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.validator.Length;

/**
 * This class represents a file, or more generally a digital object, that has been uploaded
 * by the user and shall be stored in the database.
 *
 * @author Hannes Kulovits
 */
@Entity
@Inheritance
@DiscriminatorColumn(name = "type")
@DiscriminatorValue("DO")
public class DigitalObject implements Serializable, ITouchable {

    private static final long serialVersionUID = -163440832511570828L;

    @Id
    @GeneratedValue
    protected int id;

    /**
     * Name of the object.
     */
    protected String fullname = "";

    /**
     * In most cases this is the mime-type of the object.
     */
    protected String contentType = "";
    
    @OneToOne(cascade=CascadeType.ALL)
    protected ChangeLog changeLog = new ChangeLog();
    
    @OneToOne(cascade=CascadeType.ALL)
    protected ByteStream data = new ByteStream();
    
    public ByteStream getData() {
        return data;
    }

    public void setData(ByteStream data) {
        this.data = data;
    }    

    @Length(max = 2000000)
    @Column(length = 2000000)
    protected String jhoveXMLString;

    @Length(max = 2000000)
    @Column(length = 2000000)
    protected String fitsXMLString;
    
    public String getFitsXMLString() {
        return fitsXMLString;
    }

    public void setFitsXMLString(String fitsXMLString) {
        this.fitsXMLString = fitsXMLString;
    }

    /**
        * Used only to get the real size of the upload in Mb
        * @author riccardo
        */
    @Transient
    Double dataInMB;

    @OneToOne(cascade=CascadeType.ALL)
    protected XcdlDescription xcdlDescription = null;

    /**
     * Detailed information about the sample record's format.
     */
    @OneToOne(cascade=CascadeType.ALL)
    protected FormatInfo formatInfo = new FormatInfo();
    
    public String getDataInMB(){
        return data.getDataInMB();
    }

    /**
     * @return true if the upload contains data.
     */
    public boolean isDataExistent() {
        return data.isDataExistent();
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Transient
    public DigitalObject clone() {
        DigitalObject u = new DigitalObject();
        u.setContentType(this.contentType);
        u.setFullname(this.fullname);
        u.setData(data.clone());
        return u;
    }

    public ChangeLog getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(ChangeLog value) {
        changeLog = value;
    }

    public boolean isChanged(){
        return changeLog.isAltered();
    }
    
    public void touch() {
        changeLog.touch();
    }

    /**
     * @see ITouchable#handleChanges(IChangesHandler)
     */
    public void handleChanges(IChangesHandler h) {
        h.visit(this);
        formatInfo.handleChanges(h);
        if (xcdlDescription != null) 
            xcdlDescription.handleChanges(h);
    }

    
    public String getJhoveXMLString() {
        return jhoveXMLString;
    }

    public void setJhoveXMLString(String jhoveXMLString) {
        this.jhoveXMLString = jhoveXMLString;
    }

    public XcdlDescription getXcdlDescription() {
        return xcdlDescription;
    }

    public void setXcdlDescription(XcdlDescription xcdlDescription) {
        this.xcdlDescription = xcdlDescription;
    }

    public FormatInfo getFormatInfo() {
        return formatInfo;
    }

    public void setFormatInfo(FormatInfo value) {
        formatInfo = value;
    }    
    

    @Override
    /**
     * checks only the ID, if it exists - if it doesnt exist, it checks object identity.
     */
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o instanceof DigitalObject) {
            int id2 = ((DigitalObject) o).getId();
            boolean result =  ((id != 0) && (id == id2));
            return result;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return new Long(id).hashCode();
    }    
    
}
