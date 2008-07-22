package eu.planets_project.ifr.core.services.identification.jmagickconverter.impl;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Before;
import org.junit.Test;

// JAXB Generated classes reside here
import eu.planets_project.datamodel.*;
import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.common.datamodel.DataModelUtils;
import eu.planets_project.ifr.core.common.datamodel.DocumentValidator;
import eu.planets_project.ifr.core.common.datamodel.MockPreservationPlanner;
import eu.planets_project.ifr.core.common.datamodel.PlanetsDataModelException;
import eu.planets_project.ifr.core.common.datamodel.preservation.PreservationAction;
import eu.planets_project.ifr.core.common.datamodel.preservation.PreservationTool;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.services.migration.jmagickconverter.impl.L2JpgToTiffConverter;
import eu.planets_project.ifr.core.common.datamodel.PDMCreator;

/**
 * Unit test class for the PLANETS PreservationAction class.  
 * 
 * @author Geoffroy Maillol
 * @author Tessella Software Solutions
 * @modified 13th May 2008
 */
public class ImageIdentificationServiceTest {
	
	String xmlString = null;
	TypePlanetsDataModel tpdm;
	PlanetsLogger logger;
	
	@Before
	public void setUp() throws NamingException
	{
		Properties properties = new Properties();
		 properties.put("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
		 properties.put("java.naming.factory.url.pkgs","=org.jboss.naming:org.jnp.interfaces");
		 properties.put("java.naming.provider.url","localhost:1099");
		 Context context = new InitialContext(properties);
	}
	
	
	@Test
	public void basicIdentifyOneBinary() throws PlanetsDataModelException, PlanetsException, IOException {
		ImageIdentificationService identifyService = new ImageIdentificationService();
		String filePath = "C:/PLANETS/16-06-2008/WSTestCLients/WSClientProject/resultJpgToTiffConversionClient63036.jpg";
		File testFile = new File(filePath);
		byte[] binary = getByteArrayFromFile(testFile);
		URI formatURI = identifyService.basicIdentifyOneBinary(binary);
		System.out.println(formatURI.toASCIIString());
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
            //throw new IllegalArgumentException("getBytesFromFile@JpgToTiffConverter:: The file is too large (i.e. larger than 2 GB!");
        	System.out.println("Datei ist zu gross (e.g. groesser als 2GB)!");
        }
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
    
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }
	
	private boolean validate(File schemaFile, File documentFile) {
		
		DocumentValidator validator = new DocumentValidator();
		validator.setCatalog("schema-catalog.xml");
		boolean valid = validator.validate(schemaFile, documentFile);
		if (!valid) {
			for (String error : validator.getValidationErrors()) {
				System.out.println(error);
			}
		}
    	return valid;
	}
	
}
