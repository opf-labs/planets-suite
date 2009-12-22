/**
 * Copyright (c) 2007, 2008 The Planets Project Partners.
 * 
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * GNU Lesser General Public License v3 which 
 * accompanies this distribution, and is available at 
 * http://www.gnu.org/licenses/lgpl.html
 * 
 */
package eu.planets_project.tb.impl.model.exec;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author AnJackson
 *
 */
@Entity
@XmlRootElement(name = "DigitalObjectRecord")
@XmlAccessorType(XmlAccessType.FIELD)
public class DigitalObjectRecordImpl implements Serializable {
    /** */
    private static final Log log = LogFactory.getLog(DigitalObjectRecordImpl.class);
    
    /** */
    private static final long serialVersionUID = -7437177398324413099L;


    @Id
    @GeneratedValue
    @XmlTransient
    private long id = -1;
    
    
    String uri;
    
    public DigitalObjectRecordImpl( String uri ) {
        this.uri = uri;
    }

}
