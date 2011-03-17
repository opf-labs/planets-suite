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
package eu.planets_project.services.java_se.image;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.jws.WebService;
import javax.xml.ws.soap.MTOM;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.Tool;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.utils.ServicePerformanceHelper;
import eu.planets_project.services.utils.ServiceUtils;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@WebService(name = JavaImageIOIdentify.NAME, 
        serviceName = Identify.NAME, 
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.identify.Identify" )
@MTOM
@StreamingAttachment( parseEagerly=true, memoryThreshold=ServiceUtils.JAXWS_SIZE_THRESHOLD )
public class JavaImageIOIdentify implements Identify {
    
    /** Service name */
    public static final String NAME = "JavaImageIOIdentify";

    private static final Logger log = Logger.getLogger(JavaImageIOIdentify.class.getName());

    /**
     * @see eu.planets_project.services.identify.Identify#describe()
     */
    public ServiceDescription describe() {
        ServiceDescription.Builder mds = new ServiceDescription.Builder(NAME, Identify.class.getCanonicalName());
        mds.description("A DigitalObject Identification Service based on the Java SE built-in ImageIO library.");
        mds.author("Andrew Jackson <Andrew.Jackson@bl.uk>");
        mds.classname(this.getClass().getCanonicalName());
        mds.version("1.0.1");
        mds.tool(JavaImageIOIdentify.tool);
        
        // FIXME Do This!
        
        // Get list of unique supported read formats
           String[] formatNames = ImageIO.getReaderFormatNames();
           formatNames = unique(formatNames);
           // e.g. png jpeg gif jpg
           
           // Get list of unique supported write formats
           formatNames = ImageIO.getWriterFormatNames();
           formatNames = unique(formatNames);
           // e.g. png jpeg jpg
           
           // Get list of unique MIME types that can be read
           formatNames = ImageIO.getReaderMIMETypes();
           formatNames = unique(formatNames);
           // e.g image/jpeg image/png image/x-png image/gif
           
           // Get list of unique MIME types that can be written
           formatNames = ImageIO.getWriterMIMETypes();
           formatNames = unique(formatNames);
           // e.g. image/jpeg image/png image/x-png
        
        List<URI> ifs = new ArrayList<URI>();
        for( String fmt_name : unique( ImageIO.getReaderFormatNames() ) ) {
            ifs.add( FormatRegistryFactory.getFormatRegistry().createExtensionUri( fmt_name ) );
        }
        mds.inputFormats(ifs.toArray(new URI[]{}));

        return mds.build();
    }

    /**
     * @see eu.planets_project.services.identify.Identify#identify(eu.planets_project.services.datatypes.DigitalObject)
     */
    public IdentifyResult identify(DigitalObject dob, List<Parameter> parameters ) {
        // Start timing...
        ServicePerformanceHelper sph = new ServicePerformanceHelper();
        
        // Initialise the result:
        ImageReader imageReader = null;

        // Can only cope if the object is 'simple':
        if (dob.getContent() == null) {
            return returnWithErrorMessage(ServiceUtils
                    .createErrorReport("The Content of the DigitalObject should not be NULL."));
        }
        // If this is an embedded binary:
        try {
            imageReader = getFormatName(dob.getContent().getInputStream());
            // Record time take to load the input into memory:
            sph.loaded();

            if (imageReader == null || imageReader.getFormatName() == null)
                return returnWithErrorMessage(ServiceUtils.createErrorReport("Could not identify the image."));
            
            List<URI> types = new ArrayList<URI>();
            URI typeURI = FormatRegistryFactory.getFormatRegistry().createExtensionUri(sanitize(imageReader.getFormatName()));
            types.add(typeURI);
            log.fine(String.format("Identified %s as %s", dob, types));
            
            ServiceReport rep = new ServiceReport(Type.INFO, Status.SUCCESS, "OK", sph.getPerformanceProperties() );
            return new IdentifyResult(types,
                    IdentifyResult.Method.PARTIAL_PARSE, rep);
        } catch (IOException e) {
            return returnWithErrorMessage(ServiceUtils.createErrorReport("IOException reading the image: " + e));
        }

    }
    
    private IdentifyResult returnWithErrorMessage(ServiceReport report) {
        List<URI> type = null;
        return new IdentifyResult(type, null, report);
    }
    
    // Returns the format name of the image in the object 'o'.
    // 'o' can be either a File or InputStream object.
    // Returns null if the format is not known.
    private static ImageReader getFormatName(Object o) {
        try {
            // Create an image input stream on the image
            ImageInputStream iis = ImageIO.createImageInputStream(o);
    
            // Find all image readers that recognize the image format
            Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
            if (!iter.hasNext()) {
                // No readers found
                return null;
            }
    
            // Use the first reader
            ImageReader reader = iter.next();
    
            // Close stream
            iis.close();
    
            // Return the format name
            return reader;
        } catch (IOException e) {
        }
        // The image could not be read
        return null;
    }

    
    /**
     * Converts all strings in 'strings' to lowercase
     * and returns an array containing the unique values.
     * All returned values are lowercase.
     * 
     * Note also that JPEG 2000 gets names 'jpeg2000' and 
     * 'jpeg 2000', but not 'jp2', so we map it.
     * 
     * TODO move this to a sub-package .
     * 
     * @param strings
     * @return unique lower case strings
     */
    public static String[] unique(String[] strings) {
        Set<String> set = new HashSet<String>();
        for (int i=0; i<strings.length; i++) {
            set.add(sanitize(strings[i]));
        }
        return (String[])set.toArray(new String[0]);
    }

    /**
     * FIXME Comment on sanitization scheme.
     * 
     * @param format
     * @return
     */
    public static String sanitize( String format ) {
        String name = format.toLowerCase();
        name = name.replace(" ", "-");
        if( "jpeg-2000".equals(name) ) name = "jp2";
        return name;
    }

    /** TODO Move this to a shared place */
    static Tool tool = null;
    static {
        try {
            tool = new Tool( null, "JavaSE & JAI ImageIO", "1.1", "The built-in JavaSE tools, plus the Java Advanced Imaging Image I/O Tools API version 1.1.", URI.create("https://jai-imageio.dev.java.net/").toURL());
        } catch( MalformedURLException e ) {
            log.severe("Malformed URI when initialising the tool info.");
        }
    }

}
