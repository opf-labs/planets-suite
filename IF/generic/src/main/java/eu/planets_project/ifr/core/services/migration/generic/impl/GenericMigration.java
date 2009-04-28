package eu.planets_project.ifr.core.services.migration.generic.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
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
import eu.planets_project.services.datatypes.ImmutableContent;
import eu.planets_project.services.datatypes.*;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.ProcessRunner;
import eu.planets_project.services.utils.ServiceUtils;

/**
 *
 */
@Stateless
@Remote(Migrate.class)

@WebService(
        name = GenericMigration.NAME, 
        serviceName = Migrate.NAME,
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.migrate.Migrate" )
        
public class GenericMigration implements Migrate, Serializable
{
	private static final long serialVersionUID = -2186431821310098736L;

	/**
	 * service name
	 */
	public static final String NAME = "GenericMigration";

    /**
     * @see eu.planets_project.services.migrate.Migrate#describe()
     */
    public ServiceDescription describe() {
        return new ServiceDescription.Builder("Generic Command Wrapper Service", Migrate.class.getCanonicalName()).build();
    }

    /**
 
     */
    public MigrateResult migrate( DigitalObject dob , URI inputFormat,
            URI outputFormat, List<Parameter> parameters) 
    {
		// yes yes, this probably ought to be a bunch of methods
		Properties p = new Properties();
		try
		{
			p.loadFromXML(new FileInputStream(new File("commands.xml")));
		}
		catch(InvalidPropertiesFormatException e)
		{
			return new MigrateResult( null, 
			        ServiceUtils.createExceptionErrorReport("Could not load commands.xml", e) );
		}
		catch(FileNotFoundException e)
		{
            return new MigrateResult( null, 
                    ServiceUtils.createExceptionErrorReport("Could not load commands.xml", e) );
		}
		catch(IOException e)
		{
            return new MigrateResult( null, 
                    ServiceUtils.createExceptionErrorReport("Could not load commands.xml", e) );
		}
		MultiProperties mp = MultiProperties.load(p);
		Map<String, String> params = new HashMap<String, String>();
		for(Parameter param : parameters )
		{
			params.put(param.name, param.value);
		}
		Map<String, String> toolParams = mp.get(params.get("tool-name"));
		try
		{
			File inputFile = FileUtils.writeInputStreamToTmpFile( dob.getContent().read(), "generic-input", "tmp");

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
			/*
			log.properties = new ArrayList<Property>();
            log.properties.add( new Property( URI.create("planets:uri"), "name", "value") );
            */
			return new MigrateResult(readDestination(outputFile), log);
		}
		catch(IOException e)
		{
            return new MigrateResult( null, 
                    ServiceUtils.createExceptionErrorReport("Could not execute command using tool "+params.get("tool-name"), e) );
		}
	}

	private DigitalObject readDestination(File outputFile) throws FileNotFoundException, IOException
	{
		FileInputStream fis = new FileInputStream(outputFile);
		byte[] output = new byte[(int)outputFile.length()];
		fis.read(output);
		fis.close();
		DigitalObject ndo = new DigitalObject.Builder(ImmutableContent.byValue(output)).build();
		return ndo;
	}

}
