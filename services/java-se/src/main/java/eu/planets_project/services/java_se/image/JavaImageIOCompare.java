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
package eu.planets_project.services.java_se.image;

import java.awt.Color;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.MTOM;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.ifr.core.techreg.properties.ServiceProperties;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.compare.Compare;
import eu.planets_project.services.compare.CompareResult;
import eu.planets_project.services.compare.PropertyComparison;
import eu.planets_project.services.compare.PropertyComparison.Equivalence;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.java_se.image.metrics.KahanSummation;
import eu.planets_project.services.utils.ServicePerformanceHelper;
import eu.planets_project.services.utils.ServiceUtils;

/**
 * The purpose of this is to implement a number of image comparison metrics for quality.
 * These include PSNR, MSE.
 * 
 * http://en.wikipedia.org/wiki/Peak_signal-to-noise_ratio
 *   
 * @author AnJackson
 */
@MTOM
@StreamingAttachment( parseEagerly=true, memoryThreshold=ServiceUtils.JAXWS_SIZE_THRESHOLD )
@WebService(name = JavaImageIOCompare.NAME, 
        serviceName = Compare.NAME, 
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.compare.Compare" )
public class JavaImageIOCompare implements Compare {
    /** Service name */
    public static final String NAME = "JavaImageIOCompare";
    
    private static Logger log = Logger.getLogger(JavaImageIOCompare.class.getName());
    
    /** The PSNR property definition */
    public static final URI PSNR_URI = URI.create("planets:pc/compare/image/psnr");
    public static PropertyComparison buildPsnrProperty( double psnr ) {
        Property.Builder pbPsnr = new Property.Builder( PSNR_URI );
        pbPsnr.name("PSNR");
        pbPsnr.value(""+psnr);
        pbPsnr.type("double");
        pbPsnr.description("Peak Signal-To-Noise Ratio. Is 'Infinity' if the images are identical, and much lower if the images are similar. Typical values for the PSNR in lossy image and video compression are between 30 and 50 dB, where higher is better. Acceptable values for wireless transmission quality loss are considered to be about 20 dB to 25 dB.");
        pbPsnr.unit("dB");
        // Also record equivalence.
        Equivalence eq = Equivalence.DIFFERENT;
        if( Double.isInfinite(psnr) ) eq = Equivalence.EQUAL;
        return new PropertyComparison(pbPsnr.build(), eq );
    }
    

    /* (non-Javadoc)
     * @see eu.planets_project.services.compare.Compare#compare(eu.planets_project.services.datatypes.DigitalObject, eu.planets_project.services.datatypes.DigitalObject, java.util.List)
     */
    public CompareResult compare(DigitalObject first, DigitalObject second,
            List<Parameter> config) {
        // Start timing...
        ServicePerformanceHelper sph = new ServicePerformanceHelper();
        
        // Initialise what will be built:
        List<PropertyComparison> props = new ArrayList<PropertyComparison>();
        ServiceReport sr = null;
        
        // Load the images.
        BufferedImage i1;
        BufferedImage i2;
        try {
            i1 = ImageIO.read( first.getContent().getInputStream() );
            i2 = ImageIO.read( second.getContent().getInputStream() );
        } catch (IOException e) {
            return new CompareResult(null, ServiceUtils.createExceptionErrorReport("IOException reading the images. ", e));
        }
        if( i1 == null || i2 == null ) {
            log.warning("One of the images was null when loaded!");
            return new CompareResult(null, ServiceUtils.createErrorReport("Error reading the images, got a NULL."));
        }
        // Record time take to load the inputs into memory:
        sph.loaded();
        
        // Check comparison is possible: Are the dimensions the same?
        if (i1.getWidth() != i2.getWidth() || i1.getHeight() != i2.getHeight()) {
            // FIXME is this really an error, or rather a 'images are different' result?
            return new CompareResult(null, ServiceUtils.createErrorReport("The image dimensions must match!"));
        }
        // FIXME Check comparison is sensible: are there the same number of channels? This is probably a WARNING?
        if( i1.getColorModel().getNumComponents() != i2.getColorModel().getNumComponents()) {
            System.out.println("The number of colour components does not match. "+i1.getColorModel().getNumComponents()+" != "+i2.getColorModel().getNumComponents());
            log.warning("The number of colour components does not match. "+i1.getColorModel().getNumComponents()+" != "+i2.getColorModel().getNumComponents());
            sr = new ServiceReport(ServiceReport.Type.WARN, ServiceReport.Status.SUCCESS, "Number of colour components was not the same. The missing channels, e.g. the alpha channel, will be assumed to be zero.");
            // FIXME I think this should be more serious, as the results can be rather misleading.
            // The comparison assumes the bit-mask to be zero everywhere, but this did not lead to such a bad PSNR?!
        }

        // Run the comparison:
        double psnr = psnr(i1, i2);
        props.add( buildPsnrProperty(psnr) );
        
        // Halt performance measurement:
        sph.stop();

        // Create a happy service report if no problems occurred.
        if( sr == null) {
            // Also store some service properties:
            sr = new ServiceReport(ServiceReport.Type.INFO, ServiceReport.Status.SUCCESS, 
                    "Comparison succeeded.", sph.getPerformanceProperties() );
        }
        // Return the result:
        return new CompareResult( props, sr );
    }

    /* (non-Javadoc)
     * @see eu.planets_project.services.compare.Compare#convert(eu.planets_project.services.datatypes.DigitalObject)
     */
    public List<Parameter> convert(DigitalObject configFile) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.services.PlanetsService#describe()
     */
    public ServiceDescription describe() {
        ServiceDescription.Builder mds = new ServiceDescription.Builder(NAME, Compare.class.getCanonicalName());
        mds.description("A Raster DigitalObject Comparison Service based on the Java SE built-in ImageIO library.");
        mds.author("Andrew Jackson <Andrew.Jackson@bl.uk>");
        mds.classname(this.getClass().getCanonicalName());
        mds.version("1.0.1");
        mds.tool(JavaImageIOIdentify.tool);
        
        List<URI> ifs = new ArrayList<URI>();
        for( String fmt_name : JavaImageIOIdentify.unique(ImageIO.getReaderFormatNames()) ) {
            ifs.add( FormatRegistryFactory.getFormatRegistry().createExtensionUri( fmt_name ) );
        }
        mds.inputFormats(ifs.toArray(new URI[]{}));

        return mds.build();
    }

    /* --------------------------------------------------------------------------------------- */
    
    /**
     * Computes the peak signal to noise ratio for two images, using all four channels (R,G,B,Alpha).
     * 
     * @param i1
     * @param i2
     * @return
     */
    private static Double psnr( BufferedImage i1, BufferedImage i2 ) {
        // Set up the summations:
        KahanSummation tr = new KahanSummation();
        KahanSummation tg = new KahanSummation();
        KahanSummation tb = new KahanSummation();
        KahanSummation ta = new KahanSummation();

        // TODO Can we cope with comparisons across color spaces?
        // Yes, in that this is what getRBG does, but note that getRGB reduced the depth to 8-bits!
        //i1.getColorModel().getColorSpace();
        // i1.getData().getSample(x, y, b);
        
        // FIXME Use a better accumulator, as this approach suffers overflow issues as a long, and probably underflow problems as a double.

        for (int i = 0; i < i1.getWidth(); i++) {
            for (int j = 0; j < i1.getHeight(); j++) {
                final Color c1 = new Color(i1.getRGB(i, j));
                final Color c2 = new Color(i2.getRGB(i, j));
                final int dr = c1.getRed() - c2.getRed();
                final int dg = c1.getGreen() - c2.getGreen();
                final int db = c1.getBlue() - c2.getBlue();
                final int da = c1.getAlpha() - c2.getAlpha();
                tr.add( dr*dr );
                tg.add( dg*dg );
                tb.add( db*db );
                ta.add( da*da );
            }
        }
        // Compute the mean square error:
        double mse = (tr.getSum() + tg.getSum() + tb.getSum() + ta.getSum()) / (i1.getWidth() * i1.getHeight() * 4);            
        log.info("Mean square error: " + mse);
        if (mse == 0) {
            log.warning("mse == 0 and so psnr will be infinity!");
        }
        // Get the bits per pixel:
        // FIXME This may be approx, and need to be tightened up for the case where channels have variable depths.
        double bpp = i1.getColorModel().getPixelSize()/i1.getColorModel().getNumColorComponents();
        // FIXME Actually, using getRGB reduces each channel to 8 bits:
        bpp = 8;
        // The maximum is therefore:
        double max = Math.pow(2, bpp) - 1;
        // Compute the peak signal to noise ratio:
        double psnr = 10.0 * StrictMath.log10((max * max) / mse);
        log.info("Peak signal to noise ratio: " + psnr);

        return new Double( psnr );
    }
    
}
