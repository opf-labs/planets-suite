/**
 * 
 */
package eu.planets_project.services.sanselan;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.SanselanConstants;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.MigrationPath;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.Tool;
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
@Stateless
@Local(Migrate.class)
@Remote(Migrate.class)

// Web Service Annotations, copied in from the inherited interface.
@WebService(
        name = SanselanMigrate.NAME, 
        serviceName = Migrate.NAME,
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.migrate.Migrate" )
public class SanselanMigrate implements Migrate {

    /** The service name */
    public static final String NAME = "SanselanMigrate";
    
    Logger log = Logger.getLogger(SanselanMigrate.class.getName());
    
    // Parameter Stuff:
    static String TIFF_UNCOMPRESSED = "NONE";
    static String TIFF_LZW = "LZW";
    static String TIFF_PACKBITS = "PACKBITS";
    static HashMap<String,String> parmap = new HashMap<String,String>();
    static {
        parmap.put(SanselanConstants.PARAM_KEY_COMPRESSION, "Compression");
    }

    /**
     * @see eu.planets_project.services.migrate.Migrate#describe()
     */
    public ServiceDescription describe() {
        ServiceDescription.Builder sd = new ServiceDescription.Builder( NAME, Migrate.class.getCanonicalName());
        sd.author("Andrew Jackson <Andrew.Jackson@bl.uk>");
        sd.description("A wrapper for the migrations supported by the pure Java Sanselan image library (v0.94).");
        sd.classname(this.getClass().getCanonicalName());
        sd.tool(Tool.create(null, "Apache Sanselan", "0.94", null, "http://incubator.apache.org/sanselan/"));
        sd.furtherInfo(URI.create("http://incubator.apache.org/sanselan/"));
        sd.version("0.1");
        
        // Migration Paths: List all combinations:
        List<MigrationPath> paths = new ArrayList<MigrationPath>();
        ImageFormat[] fmts = ImageFormat.getAllFormats();
        for (ImageFormat i : fmts) {
            /**
             * See {@link http://incubator.apache.org/sanselan/site/formatsupport.html} for
             * details of supported formats.
             */
            if (!i.extension.equalsIgnoreCase("unknown")
                    && !i.extension.equalsIgnoreCase("jbig2") 
                    && !i.extension.equalsIgnoreCase("jpeg")) {
                for (ImageFormat j : fmts) {
                    if (!j.extension.equalsIgnoreCase("unknown")
                            && !j.extension.equalsIgnoreCase("jpeg") 
                            && !j.extension.equalsIgnoreCase("jbig2") 
                            && !j.extension.equalsIgnoreCase("ico")
                            && !j.extension.equalsIgnoreCase("psd")
                            && !j.extension.equalsIgnoreCase(i.extension)) {
                    
                        // Optionally, set up parameters:
                        List<Parameter> pars = null;
                        if(j.extension.equalsIgnoreCase("tiff")) {
                            pars = new Vector<Parameter>();
                            Parameter compression = new Parameter.Builder(
                                    parmap .get(SanselanConstants.PARAM_KEY_COMPRESSION), TIFF_LZW)
                                    .type("[NONE,LZW,PACKBITS]")
                                    .description("The compression method, one of 'NONE', 'LZW', 'PACKBITS'.")
                                    .build();
                            pars.add(compression);
                        }
                        
                        FormatRegistry format = FormatRegistryFactory.getFormatRegistry();
                        MigrationPath p = new MigrationPath(
                                format.createExtensionUri(i.extension), 
                                format.createExtensionUri(j.extension), 
                                pars);
                        paths.add(p);
                    }
                    
                }
            }
        }
        sd.paths(paths.toArray(new MigrationPath[]{}));
        
        return sd.build();
    }
    
    /**
     * @see eu.planets_project.services.migrate.Migrate#migrate(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.net.URI, eu.planets_project.services.datatypes.Parameter)
     */
    @SuppressWarnings("unchecked")
    public MigrateResult migrate(DigitalObject dob, URI inputFormat,
            URI outputFormat, List<Parameter> parameters) {
        
    	// Start timing...
        ServicePerformanceHelper sph = new ServicePerformanceHelper();
        
        BufferedImage image = null;
        
        // Can only cope if the object is 'simple':
        if( dob.getContent() == null ) {
            return this.returnWithErrorMessage("The Content of the DigitalObject should not be NULL.", null);
        }
        // Both by-reference and by-value can be read as input streams:
        try {
            image = Sanselan.getBufferedImage(dob.getContent().getInputStream());
        } catch (ImageReadException e) {
            return returnWithErrorMessage("Could not read the image. ", e);
        } catch (IOException e) {
            return returnWithErrorMessage("IOException reading the image. ", e);
        }
        
        // Record time take to load the input into memory:
        sph.loaded();

        // Pick up the output format:
        ImageFormat format = null;
        for( ImageFormat f : ImageFormat.getAllFormats() ) {
            if (FormatRegistryFactory.getFormatRegistry().createExtensionUri(
                    f.extension).equals(outputFormat))
                format = f;
        }
        if( format == null ) {
            return this.returnWithErrorMessage("Unsupported output format: "+outputFormat,null);
        } else {
            log.info("Outputing image to format: "+format.toString());
        }
        
        Map params = new HashMap();

        if(parameters!=null) {
            // set optional parameters if you like
            for( Parameter p : parameters ) { 
                if( p.getName().equals( parmap.get(SanselanConstants.PARAM_KEY_COMPRESSION )) ) {
                    if( p.getValue().equalsIgnoreCase(TIFF_UNCOMPRESSED)) {
                        params.put(SanselanConstants.PARAM_KEY_COMPRESSION, TiffConstants.TIFF_COMPRESSION_UNCOMPRESSED);
                    }
                    if( p.getValue().equalsIgnoreCase(TIFF_LZW)) {
                        params.put(SanselanConstants.PARAM_KEY_COMPRESSION, TiffConstants.TIFF_COMPRESSION_LZW);
                    }
                    if( p.getValue().equalsIgnoreCase(TIFF_PACKBITS)) {
                        params.put(SanselanConstants.PARAM_KEY_COMPRESSION, TiffConstants.TIFF_COMPRESSION_PACKBITS);
                    }
                }
            }
        }

        byte[] bytes;
        try {
            bytes = Sanselan.writeImageToBytes(image, format, params);
        } catch (ImageWriteException e) {
            return this.returnWithErrorMessage("Could not write the Image. ",e);
        } catch (IOException e) {
            return this.returnWithErrorMessage("Could not write the Image. ",e);
        }
        
        ServiceReport rep = new ServiceReport(Type.INFO, Status.SUCCESS, "OK", sph.getPerformanceProperties());
        DigitalObject ndo = new DigitalObject.Builder(Content.byValue(bytes)).build();
        return new MigrateResult( ndo, rep );
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

}
