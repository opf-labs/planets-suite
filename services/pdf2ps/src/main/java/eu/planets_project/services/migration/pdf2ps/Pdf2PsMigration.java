/*
 *
 */
package eu.planets_project.services.migration.pdf2ps;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.apache.commons.io.FileUtils;

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
import eu.planets_project.services.utils.DigitalObjectUtils;
import eu.planets_project.services.utils.ProcessRunner;



/**
 * The Pdf2PsMigration migrates PDF files to PS files and vice versa.
 *
 * @author Sven Schlarb <shsschlarb-planets@yahoo.de>
 */
@Local(Migrate.class)
@Remote(Migrate.class)
@Stateless
@WebService( name = Pdf2PsMigration.NAME ,
        serviceName = Migrate.NAME,
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.migrate.Migrate" )
public final class Pdf2PsMigration implements Migrate, Serializable {

    private static Logger log = Logger.getLogger(Pdf2PsMigration.class.getName());


    /** The dvi ps installation dir */
    public String pdf2ps_install_dir;
    /** The pdf2ps application name */
    public String pdf2ps_app_name;
    /** The output file extension */
    //public String pdf2ps_outfile_ext;
    private File tmpInFile;
    private File tmpOutFile;

    String inputFmtExt = null;
    String outputFmtExt = null;

    /***/
    static final String NAME = "Pdf2PsMigration";


    List<String> inputFormats = null;
    List<String> outputFormats = null;
    HashMap<String, String>  formatMapping = null;

    /***/
    private static final long serialVersionUID = 2127494848765937613L;

    private void init()
    {

        // input formats
        inputFormats = new ArrayList<String>();
        inputFormats.add("pdf");

        // output formats and associated output parameters
        outputFormats = new ArrayList<String>();
        outputFormats.add("ps");

        // Disambiguation of extensions, e.g. {"JPG","JPEG"} to {"JPEG"}
        // FIXIT This should be supported by the FormatRegistryImpl class, but
        // it does not provide the complete set at the moment.
        formatMapping = new HashMap<String, String>();
        formatMapping.put("pdfa","pdf");
    }

    /**
     * {@inheritDoc}
     *
     * @see eu.planets_project.services.migrate.Migrate#migrate(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.net.URI, eu.planets_project.services.datatypes.Parameter)
     */
    public MigrateResult migrate( final DigitalObject digitalObject, URI inputFormat,
            URI outputFormat, List<Parameter> parameters) {

        Properties props = new Properties();
        try {

            String strRsc = "/eu/planets_project/services/migration/pdf2ps/pdf2ps.properties";
            props.load( this.getClass().getResourceAsStream(strRsc));
            // config vars
            this.pdf2ps_install_dir = props.getProperty("pdf2ps.install.dir");
            this.pdf2ps_app_name = props.getProperty("pdf2ps.app.name");

        } catch( Exception e ) {
            // // config vars
            this.pdf2ps_install_dir  = "/usr/bin";
            this.pdf2ps_app_name = "pdf2ps";
        }
        log.info("Using pdf2ps install directory: "+this.pdf2ps_install_dir);
        log.info("Using pdf2ps application name: "+this.pdf2ps_app_name);

        init();
        getExtensions(inputFormat,outputFormat);

        /*
         * We just return a new digital object with the same required arguments
         * as the given:
         */
        byte[] binary = null;
        InputStream inputStream = digitalObject.getContent().getInputStream();

            // write input stream to temporary file
            tmpInFile = DigitalObjectUtils.toFile(digitalObject); // TODO need extension?
            if( !(tmpInFile.exists() && tmpInFile.isFile() && tmpInFile.canRead() ))
            {
                log.severe("[Pdf2PsMigration] Unable to create temporary input file!");
                return null;
            }
            log.info("[Pdf2PsMigration] Temporary input file created: "+tmpInFile.getAbsolutePath());

            // outfile name
            String outFileStr = tmpInFile.getAbsolutePath()+"."+outputFmtExt;
            log.info("[Pdf2PsMigration] Output file name: "+outFileStr);

            // run command
            ProcessRunner runner = new ProcessRunner();
            List<String> command = new ArrayList<String>();
            // setting up command
            command.add(this.pdf2ps_app_name);
            command.add(tmpInFile.getAbsolutePath());
            command.add(outFileStr);
            runner.setCommand(command);
            runner.setInputStream(inputStream);
            log.info("[Pdf2PsMigration] Executing command: "+command.toString() +" ...");
            runner.run();
            int return_code = runner.getReturnCode();
            if (return_code != 0){
                log.severe("[Pdf2PsMigration] Jasper conversion error code: " + Integer.toString(return_code));
                log.severe("[Pdf2PsMigration] " + runner.getProcessErrorAsString());
                //log.severe("[Pdf2PsMigration] Output: "+runner.getProcessOutputAsString());
                return null;
            }

            tmpOutFile = new File(outFileStr);
            // read byte array from temporary file
            if( tmpOutFile.isFile() && tmpOutFile.canRead() )
                try {
                    binary = FileUtils.readFileToByteArray(tmpOutFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            else
                log.severe( "Error: Unable to read temporary file "+tmpOutFile.getAbsolutePath() );

        DigitalObject newDO = null;

        ServiceReport report = new ServiceReport(Type.INFO, Status.SUCCESS, "OK");

        newDO = new DigitalObject.Builder(Content.byValue(binary)).build();

        return new MigrateResult(newDO, report);
    }

    private void getExtensions(URI inputFormat, URI outputFormat)
    {
        if( inputFormat != null && outputFormat != null )
        {
            inputFmtExt = getFormatExt( inputFormat, false );
            outputFmtExt = getFormatExt( outputFormat, true );
        }
    }

        /**
     * Gets one extension from a set of possible extensions for the incoming
     * request planets URI (e.g. planets:fmt/ext/jpeg) which matches with
     * one format of the set of pdf2ps's supported input/output formats. If
     * isOutput is false, it checks against the gimp input formats ArrayList,
     * otherwise it checks against the gimp output formats HashMap.
     *
     * @param formatUri Planets URI (e.g. planets:fmt/ext/jpeg)
     * @param isOutput Is the format an input or an output format
     * @return Format extension (e.g. "JPEG")
     */
    private String getFormatExt( URI formatUri, boolean isOutput  )
    {
        String fmtStr = null;
        // status variable which indicates if an input/out format has been found
        // while iterating over possible matches
        boolean fmtFound = false;
        // Extensions which correspond to the format
        // planets:fmt/ext/jpg -> { "JPEG", "JPG" }
        // or can be found in the list of supported formats
        Set<String> reqInputFormatExts = FormatRegistryFactory
                .getFormatRegistry().getExtensions(formatUri);
        Iterator<String> itrReq = reqInputFormatExts.iterator();
        // Iterate either over input formats ArrayList or over output formats
        // HasMap
        Iterator<String> itrDvips = (isOutput)?outputFormats.iterator():inputFormats.iterator();
        // Iterate over possible extensions that correspond to the request
        // planets uri.
        while(itrReq.hasNext()) {
            // Iterate over the different extensions of the planets:fmt/ext/jpg
            // format URI, note that the relation of Planets-format-URI to
            // extensions is 1 : n.
            String reqFmtExt = normalizeExt((String) itrReq.next());
            while(itrDvips.hasNext()) {
                // Iterate over the formats that pdf2ps offers either as input or
                // as output format.
                // See input formats in the this.init() method to see the
                // pdf2ps input/output formats offered by this service.
                String gimpFmtStr = (String) itrDvips.next();
                if( reqFmtExt.equalsIgnoreCase(gimpFmtStr) )
                {
                    // select the gimp supported format
                    fmtStr = gimpFmtStr;
                    fmtFound = true;
                    break;
                }
                if( fmtFound )
                    break;
            }
        }
        return fmtStr;
    }

    /**
     * Disambiguation (e.g. JPG -> JPEG) according to the formatMapping
     * datas structure defined in this class.
     *
     * @param ext
     * @return Uppercase disambiguized extension string
     */
    private String normalizeExt(String ext)
    {
        String normExt = ext.toUpperCase();
        return ((formatMapping.containsKey(normExt))?
            (String)formatMapping.get(normExt):normExt);
    }

    /**
     * @see eu.planets_project.services.migrate.Migrate#describe()
     */
    public ServiceDescription describe() {
        ServiceDescription.Builder builder = new ServiceDescription.Builder(NAME, Migrate.class.getName());

        builder.author("Sven Schlarb <shsschlarb-planets@yahoo.de>");
        builder.classname(this.getClass().getCanonicalName());
        builder.description(" PDF2PS - Convert a PDF file to PostScript. "+
                            "PDF2PS is a simple shell script that converts "+
                            "a PDF file to PostScript using GhostScript."+
                            "Usage: pdf2ps file.pdf file.ps");
        FormatRegistry format = FormatRegistryFactory.getFormatRegistry();
        MigrationPath[] mPaths = new MigrationPath []{
            new MigrationPath(format.createExtensionUri("pdf"), format.createExtensionUri("ps"),null)};
        builder.paths(mPaths);
        builder.classname(this.getClass().getCanonicalName());
        builder.version("0.1");

        ServiceDescription mds =builder.build();

        return mds;
    }
}
