/**
 * @author Thomas Kraemer thomas.kraemer@uni-koeln.de
 */
package eu.planets_project.ifr.core.services.characterisation.fpmtool.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.BindingType;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;

import eu.planets_project.services.PlanetsException;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.compare.BasicCompareFormatProperties;
import eu.planets_project.services.utils.PlanetsLogger;
import eu.planets_project.services.utils.ProcessRunner;

@Stateless()
@Local(BasicCompareFormatProperties.class)
@Remote(BasicCompareFormatProperties.class)
@LocalBinding(jndiBinding = "planets/FPMTool")
@RemoteBinding(jndiBinding = "planets-project.eu/FPMTool")
@WebService(name = "FPMTool", serviceName = BasicCompareFormatProperties.NAME, targetNamespace = PlanetsServices.NS, endpointInterface = "eu.planets_project.services.compare.BasicCompareFormatProperties")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public class FPMTool implements BasicCompareFormatProperties, Serializable {

    private static final long serialVersionUID = 4260138383923408640L;
    private final static PlanetsLogger plogger = PlanetsLogger
            .getLogger(FPMTool.class);
    private String FPMTOOL_HOME = System.getenv("FPMTOOL_HOME")
            + File.separator;
    private String SYSTEM_TEMP = System.getProperty("java.io.tmpdir");
    private String FPMTOOL_WORK = null;
    private String FPMTOOL_OUT = "fpm.fpm";

    public FPMTool() {
        try {
            FPMTOOL_WORK = null;
            plogger.info("FPMTOOL_HOME = " + FPMTOOL_HOME);
            plogger.info("FPMTOOL_WORK= " + FPMTOOL_WORK);
            plogger.info("FPMTOOL_OUT = " + FPMTOOL_OUT);

            if (SYSTEM_TEMP.lastIndexOf(File.separator) == SYSTEM_TEMP.length() - 1) {
                FPMTOOL_WORK = SYSTEM_TEMP + "FPMTool" + File.separator;
                FPMTOOL_OUT = FPMTOOL_WORK + File.separator + FPMTOOL_OUT;
            } else if (SYSTEM_TEMP.endsWith("/tmp")) {
                FPMTOOL_WORK = SYSTEM_TEMP + File.separator + "FPMTool"
                        + File.separator;
                FPMTOOL_OUT = FPMTOOL_WORK + FPMTOOL_OUT;
            }
            plogger.info("FPMTOOL_HOME = " + FPMTOOL_HOME);
            plogger.info("FPMTOOL_WORK= " + FPMTOOL_WORK);
            plogger.info("FPMTOOL_OUT = " + FPMTOOL_OUT);
            File tmpworkdir = new File(FPMTOOL_WORK);
            if (!tmpworkdir.exists()) {
                tmpworkdir.mkdir();
            }
            // get resources to wdir, not nice, but the only workaround so far
            File resdir = new File(FPMTOOL_HOME + "res");
            File tempresdir = new File(FPMTOOL_WORK + "res");
            if (!tempresdir.exists())
                tempresdir.mkdir();
            for (File f : resdir.listFiles()) {
                File targ = new File(tempresdir, f.getName());
                if (!targ.exists())
                    targ.createNewFile();
                copy(f, targ);
            }

            File resdir2 = new File(FPMTOOL_HOME + "fpm");
            File tempresdir2 = new File(FPMTOOL_WORK + "fpm");
            if (!tempresdir2.exists()) {
                tempresdir2.mkdir();
            }
            for (File f2 : resdir2.listFiles()) {
                File targ2 = new File(tempresdir2, f2.getName());
                if (!targ2.exists())
                    targ2.createNewFile();
                copy(f2, targ2);
            }

            plogger.info("System-Temp folder is: " + SYSTEM_TEMP);
        } catch (Exception e) {
            e.printStackTrace();
            plogger.error(e.getMessage());
        }
    }

    @WebMethod(operationName = BasicCompareFormatProperties.NAME, action = PlanetsServices.NS
            + "/" + BasicCompareFormatProperties.NAME)
    @WebResult(name = BasicCompareFormatProperties.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + BasicCompareFormatProperties.NAME, partName = BasicCompareFormatProperties.NAME
            + "Result")
    public String basicCompareFormatProperties(String parameters)
            throws PlanetsException {
        byte[] tempbytearr = null;
        try {
            ProcessRunner shell = new ProcessRunner();
            plogger.info("Configuring Commandline");
            ArrayList<String> cmds = new ArrayList<String>();
            cmds.add(FPMTOOL_HOME + "fpmTool");
            cmds.add(parameters);
            shell.setCommand(cmds);
            shell.setStartingDir(new File(FPMTOOL_WORK));

            plogger.info("Setting working directory to: " + FPMTOOL_WORK);
            File outfile = new File(FPMTOOL_OUT);
            if (outfile.exists() && !outfile.isDirectory()) {
                plogger.info("Output file already exists...");
            } else {
                outfile.createTempFile("tmp", "tmp");
                plogger.info("Created new output file:"
                        + outfile.getAbsolutePath());
            }
            plogger.info("Starting Extractor...");
            shell.run();
            String processOutput = shell.getProcessOutputAsString();
            String processError = shell.getProcessErrorAsString();
            plogger.info("Process Output: " + processOutput);
            plogger.error("Process Error: " + processError);

            plogger.info("Creating byte[] to return...");
            tempbytearr = getByteArrayFromFile(outfile);
            plogger.info("Returning joint file format properties string:\n"
                    + new String(tempbytearr));
        } catch (FileNotFoundException e) {
            plogger.error("File not found: " + FPMTOOL_OUT);
            e.printStackTrace();
        } catch (IOException e) {
            plogger.error("IO Error: ");
            e.printStackTrace();
        } catch (Exception nex) {
            plogger.error("Error: " + nex.getCause().getMessage());
            return nex.getCause().getMessage();
        }
        return new String(tempbytearr);
    }

    private static byte[] getByteArrayFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // throw new
            // IllegalArgumentException("getBytesFromFile@JpgToTiffConverter::
            // The file is too large (i.e. larger than 2 GB!");
            System.out.println("Datei ist zu gross (e.g. groesser als 2GB)!");
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "
                    + file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    public void copy(File source, File target) throws IOException {
        plogger.warn("Copying from " + source.getAbsolutePath() + " to "
                + target.getAbsolutePath());
        FileChannel sourceChannel = new FileInputStream(source).getChannel();
        FileChannel targetChannel = new RandomAccessFile(target, "rwd")
                .getChannel();
        sourceChannel.transferTo(0, sourceChannel.size(), targetChannel);
        sourceChannel.close();
        targetChannel.close();
    }

}