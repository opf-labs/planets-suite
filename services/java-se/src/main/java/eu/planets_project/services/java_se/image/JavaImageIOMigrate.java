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

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.jws.WebService;
import javax.xml.ws.soap.MTOM;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.MigrationPath;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.ServicePerformanceHelper;
import eu.planets_project.services.utils.ServiceUtils;

/**
 * Convert Microsoft DOC to ODF, using Open Office.
 *
 */
// Web Service Annotations, copied in from the inherited interface.
@WebService(
        name = JavaImageIOMigrate.NAME, 
        serviceName = Migrate.NAME,
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.migrate.Migrate" )
@MTOM
@StreamingAttachment( parseEagerly=true, memoryThreshold=ServiceUtils.JAXWS_SIZE_THRESHOLD )
public class JavaImageIOMigrate implements Migrate {
	
    /** Service name */
    public static final String NAME = "JavaImageIOMigrate";

    private static final FormatRegistry format = FormatRegistryFactory.getFormatRegistry();
    
    private static Logger log = Logger.getLogger(JavaImageIOMigrate.class.getName());

    /**
     * @see eu.planets_project.services.migrate.Migrate#describe()
     */
    public ServiceDescription describe() {    	
        ServiceDescription.Builder sd = new ServiceDescription.Builder( NAME, Migrate.class.getCanonicalName());
        sd.author("Andrew Jackson <Andrew.Jackson@bl.uk>");
        sd.description("A wrapper for the migrations supported by the Java SE built-in ImageIO library.");
        sd.classname(this.getClass().getCanonicalName());
        sd.version("1.0.1");
        sd.tool(JavaImageIOIdentify.tool);
        
        // Migration Paths: List all combinations:
        List<MigrationPath> paths = new ArrayList<MigrationPath>();
        for ( String i : JavaImageIOIdentify.unique( ImageIO.getReaderFormatNames() ) ) {
                for (String j : JavaImageIOIdentify.unique( ImageIO.getWriterFormatNames() )) {
                    if (!i.equalsIgnoreCase(j) ) {
                    
                        MigrationPath p = new MigrationPath(
                                format.createExtensionUri(i), 
                                format.createExtensionUri(j), 
                                null );
                        paths.add(p);
                    }
                    
                }
        }
        sd.paths(paths.toArray(new MigrationPath[]{}));
        
        return sd.build();
    }
    
    /**
     * @see eu.planets_project.services.migrate.Migrate#migrate(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.net.URI, eu.planets_project.services.datatypes.Parameter)
     */
    public MigrateResult migrate(DigitalObject dob, URI inputFormat,
            URI outputFormat, List<Parameter> parameters) {
        // Start timing...
        ServicePerformanceHelper sph = new ServicePerformanceHelper();
        
        BufferedImage image = null;

        // There must be content:
        if( dob.getContent() == null ) {
            return this.returnWithErrorMessage("The Content of the DigitalObject should not be NULL.", null);
        }
        // Both by-reference and by-value can be read as input streams:
        try {
            image = ImageIO.read( dob.getContent().getInputStream() );
        } catch ( Exception e) {
            return returnWithErrorMessage("Exception reading the image - unsupported or invalid input? ", e);
        }
        // Record time take to load the input into memory:
        sph.loaded();
        
        // If that failed, then report an error.
        if( image == null ) {
            return returnWithErrorMessage("Failed to read the image - unsupported or invalid input? ", null);
        }

        // Pick up the output format:
        //Format format = new Format(outputFormat);
        Set<String> extensionsForURI = format.getExtensions(outputFormat);
        if(extensionsForURI.isEmpty() ) {
            return this.returnWithErrorMessage("Unsupported output format: "+outputFormat,null);
        } else {
            log.info("Outputing image to format: "+format.toString());
        }
        
        String extension = extensionsForURI.iterator().next();
        File outfile = null;
        try {
            outfile = File.createTempFile("imageio", extension);

            // FIXME If writing an RBGA image, this simple write method can accidentally convert to CMYK for JPEG, as that is the only way to represent four channels.
            /*
            ImageWriter iw = ImageIO.getImageWritersByFormatName(extension).next();
            ImageTypeSpecifier its =  iw.getDefaultWriteParam().getDestinationType();
            its.getColorModel().hasAlpha();
            its.getColorModel().getNumComponents();
            // Compare with...
            image.getColorModel().hasAlpha();
            // Write:
            iw.setOutput(new FileImageOutputStream(outfile));
            ImageWriteParam iwp = iw.getDefaultWriteParam();
            iwp.setDestinationType(its);
            iw.write(null, new IIOImage(image, null, null), iwp);
            */
            
            // Write using the simple approach:
            ImageIO.write(image, extension, outfile );
        } catch ( Exception e) {
            return this.returnWithErrorMessage("Could not create image in the new format. ",e);
        }

        //try to build digital object with content by reference
        /*
        try {        	        	
            WebContentHelper webHandler = new WebContentHelper();
        	URL contentUrl = webHandler.copyIntoHTMLDirectory(outfile);
        	log.info("got content URL: "+contentUrl);
        	
            ServiceReport rep = new ServiceReport(Type.INFO, Status.SUCCESS, "OK");
        	DigitalObject ndo = new DigitalObject.Builder(Content.byReference(contentUrl)).build();
            return new MigrateResult(ndo, rep);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not return content by reference. ", e);
        }
        */
        ServiceReport rep = new ServiceReport(Type.INFO, Status.SUCCESS, "OK", sph.getPerformanceProperties() );
        DigitalObject ndo = new DigitalObject.Builder(Content.byReference(outfile)).build();
        return new MigrateResult(ndo, rep);
    }

    /**
     * 
     * @param message
     * @return
     */
    private MigrateResult returnWithErrorMessage(String message, Exception e ) {
        if( e == null ) {
            return new MigrateResult(null, ServiceUtils.createErrorReport(message));
        } else {
            return new MigrateResult(null, ServiceUtils.createExceptionErrorReport(message, e));
        }
    }

    /*
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {return (BufferedImage)image;}

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();

        // Determine if the image has transparent pixels
        boolean hasAlpha = hasAlpha(image);

        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha == true) {transparency = Transparency.BITMASK;}

            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
        } 
        catch (HeadlessException e) {} //No screen

        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha == true) {type = BufferedImage.TYPE_INT_ARGB;}
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }

    public static boolean hasAlpha(Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {return ((BufferedImage)image).getColorModel().hasAlpha();}

        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {pg.grabPixels();} catch (InterruptedException e) {}

        // Get the image's color model
        return pg.getColorModel().hasAlpha();
    }
     */
}
