package eu.planets_project.ifr.core.services.migration.generic.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;


import eu.planets_project.ifr.core.services.migration.generic.common.MultiProperties;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Parameters;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.migrate.MigrateOneBinary;
import eu.planets_project.services.migrate.MigrateOneBinaryResult;
import eu.planets_project.services.utils.ProcessRunner;
import eu.planets_project.services.utils.ServiceUtils;

@Stateless
@Remote(MigrateOneBinary.class)
@WebService(
        name = GenericMigration.NAME, 
        serviceName = MigrateOneBinary.NAME,
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.migrate.MigrateOneBinary" )
public class GenericMigration implements MigrateOneBinary, Serializable
{
	private static final long serialVersionUID = -2186431821310098736L;

	public static final String NAME = "GenericMigration";

    /* (non-Javadoc)
     * @see eu.planets_project.services.migrate.MigrateOneBinary#describe()
     */
    public ServiceDescription describe() {
        return new ServiceDescription("Generic Command Wrapper Service", MigrateOneBinary.class.getCanonicalName());
    }

    /* (non-Javadoc)
     * @see eu.planets_project.services.migrate.MigrateOneBinary#migrate(byte[], java.net.URI, java.net.URI, eu.planets_project.services.datatypes.Parameters)
     */
    public MigrateOneBinaryResult migrate(byte[] binary, URI inputFormat,
            URI outputFormat, Parameters parameters) 
    {
		// yes yes, this probably ought to be a bunch of methods
		Properties p = new Properties();
		try
		{
			p.loadFromXML(new FileInputStream(new File("commands.xml")));
		}
		catch(InvalidPropertiesFormatException e)
		{
			return new MigrateOneBinaryResult( null, 
			        ServiceUtils.createExceptionErrorReport("Could not load commands.xml", e) );
		}
		catch(FileNotFoundException e)
		{
            return new MigrateOneBinaryResult( null, 
                    ServiceUtils.createExceptionErrorReport("Could not load commands.xml", e) );
		}
		catch(IOException e)
		{
            return new MigrateOneBinaryResult( null, 
                    ServiceUtils.createExceptionErrorReport("Could not load commands.xml", e) );
		}
		MultiProperties mp = MultiProperties.load(p);
		Map<String, String> params = new HashMap<String, String>();
		for(Parameter param : parameters.getParameters())
		{
			params.put(param.name, param.value);
		}
		Map<String, String> toolParams = mp.get(params.get("tool-name"));
		try
		{
			File inputFile = File.createTempFile("generic-input", "tmp");
			createSource(binary, inputFile);

			File outputFile = File.createTempFile("generic-output", "tmp");
			outputFile.delete();

			String commandString = toolParams.get("path") + File.separator + toolParams.get("command");
			String argsString = toolParams.get("arguments");
			argsString = argsString.replace("$IN", inputFile.getAbsolutePath());
			argsString = argsString.replace("$OUT", outputFile.getAbsolutePath());
			
			List<String> strings = new ArrayList<String>();
			strings.add(commandString);
			for(String s : argsString.split(" "))
			{
				strings.add(s);
			}
			
			ProcessRunner pr = new ProcessRunner(strings);
			pr.run();

			inputFile.delete();
			outputFile.delete();
			
			ServiceReport log = new ServiceReport();
			log.error_state = pr.getReturnCode();
			log.error = pr.getProcessErrorAsString();
			log.info = pr.getProcessOutputAsString();
			log.warn = pr.getProcessOutputAsString();
			log.properties = new ArrayList<Property>();
			log.properties.add( new Property("name", "value") );
			return new MigrateOneBinaryResult(readDestination(outputFile), log);
		}
		catch(IOException e)
		{
            return new MigrateOneBinaryResult( null, 
                    ServiceUtils.createExceptionErrorReport("Could not execute command using tool "+params.get("tool-name"), e) );
		}
	}

	private byte[] readDestination(File outputFile) throws FileNotFoundException, IOException
	{
		FileInputStream fis = new FileInputStream(outputFile);
		byte[] output = new byte[(int)outputFile.length()];
		fis.read(output);
		fis.close();
		return output;
	}

	private void createSource(byte[] binary, File inputFile) throws FileNotFoundException, IOException
	{
		FileOutputStream fos = new FileOutputStream(inputFile);
		fos.write(binary);
		fos.close();
	}

}
