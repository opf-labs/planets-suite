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

package eu.planets_project.pp.plato.services.characterisation.xcl;

import eu.planets_project.pp.plato.model.XcdlDescription;
import eu.planets_project.pp.plato.services.PlatoServiceException;
import eu.planets_project.pp.plato.util.FileUtils;
import eu.planets_project.pp.plato.util.PlatoLogger;
import org.apache.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class XcdlExtractor implements Serializable {

    private static final long serialVersionUID = -1365156283464379907L;
    
    private static final Log log = PlatoLogger.getLogger(XcdlExtractor.class);
    
    
    public XcdlDescription extractProperties(String fullname, String filepath) throws PlatoServiceException {
        File f = new File(filepath);
        
        if (f.length() <= 0) {
            throw new IllegalArgumentException("Digital object must contain some data.");
        }
        
        byte[] data;
        try {
            data = FileUtils.getBytesFromFile(f);
        } catch (IOException e1) {
            throw new IllegalArgumentException("Error reading file.");        
        }
        
        XcdlDescription xcdl = null;
        try {
            xcdl = extract(fullname, data);
        }  catch (Throwable e) {
            throw new PlatoServiceException("XCL property extraction failed.", e);
        }
        
        return xcdl;
    }
    
    public XcdlDescription extractProperties(eu.planets_project.pp.plato.model.DigitalObject object)throws PlatoServiceException {
        
        if ((object == null) || (!object.isDataExistent())) {
            throw new IllegalArgumentException("Digital object must contain some data.");
        }
             
        XcdlDescription xcdl = null;
        try {
            xcdl = extract(object.getFullname(), object.getData().getData());
        } catch (Throwable e) {
            throw new PlatoServiceException("XCL property extraction failed.", e);
        }
        return xcdl;
    }
    
    
    private XcdlDescription extract(String fullname, byte[] data) {
        
//
//        DigitalObject dIn = new DigitalObject.Builder( Content.byValue(data) ).title(fullname).build();
//
//        CoreExtractor extractor = new CoreExtractor("PlatoXcdlExtractor");
//        File xcdlFile = extractor.extractXCDL(dIn, null, null, null);
//
//
        XcdlDescription xcdl = null;
        
//        if (xcdlFile.exists()) {
//            FileInputStream fileInputStream;
//            byte[] binaryXcdl = null;
//            try {
//                fileInputStream = new FileInputStream(xcdlFile);
//                binaryXcdl = new byte[(int) xcdlFile.length()];
//                fileInputStream.read(binaryXcdl);
//                fileInputStream.close();
//
//            } catch (FileNotFoundException e) {
//                return null;
//            } catch (IOException e) {
//                return null;
//            }
//
//            xcdlFile.delete();
//            xcdl = new XcdlDescription();
//            xcdl.getData().setData(binaryXcdl);
//            xcdl.setFullname(fullname + ".xcdl");
//        }
        return xcdl;
    }

        
}
