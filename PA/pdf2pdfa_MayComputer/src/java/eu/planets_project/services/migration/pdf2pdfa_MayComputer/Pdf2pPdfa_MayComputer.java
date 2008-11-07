/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author Sven Schlarb (shsschlarb-planets@yahoo.de)
 * @created 28/08/2008
 */
package eu.planets_project.services.migration.pdf2pdfa_MayComputer;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import java.lang.Runtime;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;

import eu.planets_project.services.PlanetsException;
import eu.planets_project.services.utils.PlanetsLogger;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.migrate.BasicMigrateOneBinary;
//import java.rmi.Remote;

class StreamCatcher extends Thread
{
    InputStream is;
    PlanetsLogger log = PlanetsLogger.getLogger(StreamCatcher.class);
    
    StreamCatcher(InputStream is)
    {
        this.is = is;
    }
    
    public void run()
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ( (line = br.readLine()) != null )
                log.info( "Pdf2pPdfa_MayComputer output: " + line );    
        } catch (IOException ex) {
            log.error(ex.toString());
        }
    }
}

/**
 * Convert PDF to PDFA
 *
 */
@Stateless
@Local(BasicMigrateOneBinary.class)
@LocalBinding(jndiBinding = "planets-project.eu/BasicMigrateOneBinary/Pdf2pPdfa_MayComputer")
@Remote(BasicMigrateOneBinary.class)
@RemoteBinding(jndiBinding = "planets-project.eu/BasicMigrateOneBinary/Pdf2pPdfa_MayComputer")

// Web Service Annotations, copied in from the inherited interface.
@WebService(
        name = "Pdf2pPdfa_MayComputer", 
        serviceName = BasicMigrateOneBinary.NAME,
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.ifr.core.common.services.migrate.BasicMigrateOneBinary" )
public class Pdf2pPdfa_MayComputer implements BasicMigrateOneBinary {
    
    PlanetsLogger log = PlanetsLogger.getLogger(Pdf2pPdfa_MayComputer.class);
    
    private static Logger logger = Logger.getLogger(Pdf2pPdfa_MayComputer.class.getName());
    
    public String PDFAConverter_app_name;
    
    private File fTmpInFile;
    private File fTmpOutFile;
    
   
    public String PDFAConverter_outfile_ext;
    
    public Pdf2pPdfa_MayComputer() {
        Properties props = new Properties();
        try {
            String strRsc = "/eu/planets_project/services/migration/pdf2pdfa_MayComputer/pdf2pdfa_maycomputer.properties.properties";
            props.load( this.getClass().getResourceAsStream(strRsc));
            // config vars
            this.PDFAConverter_app_name = props.getProperty("PDFAConverter.app.name");
            this.PDFAConverter_outfile_ext = props.getProperty("PDFAConverter.outfile.ext");
        } catch( IOException e ) {
            // // config vars
            this.PDFAConverter_app_name = "C:\\Programme\\PDFAConverter\\PDFAConverter.exe";
            this.PDFAConverter_outfile_ext = "tmp";
        }
        
        log.info("Using PDFAConverter application name: "+this.PDFAConverter_app_name);
        
    }

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.common.services.migrate.BasicMigrateOneBinary#basicMigrateOneBinary(byte[])
     */
    public byte[] basicMigrateOneBinary ( 
            byte[] binary ) {
        try {
            
            // write binary array to temporary file
            writeByteArrayToTmpFile( binary );
            
            // define command, example:
            // C:\Programme\PDFAConverter>PDFAConverter.exe src="C:\Data\jpeg.pdf" dst="C:\Data
            // \jpeg_pdfa.pdf" log="C:\Data\log.txt"
            String strCmd = 
                    this.PDFAConverter_app_name+" src=\""+fTmpInFile.getAbsolutePath()+"\""
                    +" dst=\""+fTmpInFile.getAbsolutePath()+"."+this.PDFAConverter_outfile_ext+"\""
                    +" log=\""+fTmpInFile.getAbsolutePath()+".log\"";
            
            try {
                // execute command
                log.info("Executing command: "+strCmd);
                Runtime rt = Runtime.getRuntime();
                Process pr = rt.exec(strCmd);
                
                // catch error messages
                StreamCatcher errorCatcher = new 
                    StreamCatcher(pr.getErrorStream());    

                // catch output messages
                StreamCatcher outputCatcher = new 
                    StreamCatcher(pr.getInputStream());
                
                // start output and error message catching
                errorCatcher.start();
                outputCatcher.start();
                
                long timeStart = System.currentTimeMillis();
                // waiting for process to end
                int iExitVal = pr.waitFor();
                long timeEnd = System.currentTimeMillis();
                long timeDiff = timeEnd - timeStart;
                SimpleDateFormat sdfTime = new SimpleDateFormat();
                sdfTime.applyPattern("mm:ss");
                log.info("PDFAConverter Execution time: " + sdfTime.format(timeDiff));

                if( iExitVal == 0 )
                    log.info("Command executed successfully: " + strCmd );
                else
                    log.error("Error executing command: " + strCmd );
                
                 
            } catch(java.lang.InterruptedException e) {
                log.error("Error executing command: " + strCmd);
            }
            
            // read byte array from temporary file
            if( fTmpInFile.isFile() && fTmpInFile.canRead() )
                binary = readByteArrayFromTmpFile(); 
            else
                log.error( "Error: Unable to read temporary file "
                        +fTmpInFile.getPath()+fTmpInFile.getName() );
            
        } catch(IOException e) {
            log.error( "IO Error:" + e.toString() );
        } finally {
            if( fTmpInFile.exists() )
                fTmpInFile.deleteOnExit();
            if( fTmpOutFile.exists() )
                fTmpOutFile.deleteOnExit();
        }
        
	return binary;
    }
    
    private synchronized void writeByteArrayToTmpFile( byte[] binary )
            throws IOException {
        try {
            this.fTmpInFile = File.createTempFile( "pdfinput",".tmp" );
            log.info("Temporary input file created: " + fTmpInFile.getAbsolutePath());
            BufferedOutputStream fos = 
                            new BufferedOutputStream(
                            new FileOutputStream(fTmpInFile));
            fos.write(binary);
            fos.close();
        } catch(IOException ex) {
            logger.log(Level.FINEST,"IO Error:" + ex.toString());
        }
    }
    
    private synchronized byte[] readByteArrayFromTmpFile() 
            throws IOException {
        byte[] binary = new byte[0];
        
        String strOutFile = fTmpInFile.getAbsolutePath()+"."+this.PDFAConverter_outfile_ext;
        fTmpOutFile = new File(strOutFile);
        try {
            if( fTmpOutFile.isFile() && fTmpOutFile.canRead())
            {
                binary = new byte[(int)fTmpOutFile.length()];
                FileInputStream fis = new FileInputStream(fTmpOutFile);
                fis.read(binary);
                log.info("Read file: " + fTmpOutFile.getAbsolutePath());
                fis.close();
            }
            else
            {
                log.info("Unable to read file: "+fTmpOutFile.getAbsolutePath());
            }
        } catch(IOException ex) {
            logger.log(Level.FINEST,"IO Error:"
                    +ex.toString());
        }
        return binary;
    }

}
