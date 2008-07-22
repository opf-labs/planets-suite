package eu.planets_project.ifr.core.services.migration.generic.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.BindingType;

import org.jboss.annotation.ejb.RemoteBinding;

import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.datatypes.Parameter;
import eu.planets_project.ifr.core.common.services.datatypes.Property;
import eu.planets_project.ifr.core.common.services.migrate.MigrateOneBinary;
import eu.planets_project.ifr.core.common.services.migrate.MigrateOneBinaryResult;
import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.common.cli.ProcessRunner;
import eu.planets_project.ifr.core.services.migration.generic.common.MultiProperties;

@WebService(name = GenericMigration.NAME, serviceName = MigrateOneBinary.NAME, targetNamespace = PlanetsServices.NS)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE,
             style = SOAPBinding.Style.RPC)
@Stateless
@Remote(MigrateOneBinary.class)
@RemoteBinding(jndiBinding="planets-project.eu/GenericMigrationServiceRemote")
@BindingType(value="http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public class GenericMigration implements MigrateOneBinary, Serializable
{
	private static final long serialVersionUID = -2186431821310098736L;

	public static final String NAME = "GenericMigration";

	@WebMethod(operationName = MigrateOneBinary.NAME,
	           action = PlanetsServices.NS + "/" + MigrateOneBinary.NAME)
	@WebResult(name = MigrateOneBinary.NAME + "Result",
	           targetNamespace = PlanetsServices.NS + "/" + MigrateOneBinary.NAME,
	           partName = MigrateOneBinary.NAME + "Result")
	public MigrateOneBinaryResult migrateOneBinary(
	        @WebParam(name = "binary",
	                  targetNamespace = PlanetsServices.NS + "/" + MigrateOneBinary.NAME, partName = "binary")
	        byte[] binary,
	        @WebParam(name = "parameters",
	                  targetNamespace = PlanetsServices.NS + "/" + MigrateOneBinary.NAME, partName = "parameters")
	        Parameter[] parameters) throws PlanetsException
	{
		// yes yes, this probably ought to be a bunch of methods
		Properties p = new Properties();
		try
		{
			p.loadFromXML(new FileInputStream(new File("commands.xml")));
		}
		catch(InvalidPropertiesFormatException e)
		{
			throw new PlanetsException(e);
		}
		catch(FileNotFoundException e)
		{
			throw new PlanetsException(e);
		}
		catch(IOException e)
		{
			throw new PlanetsException(e);
		}
		MultiProperties mp = MultiProperties.load(p);
		Map<String, String> params = new HashMap<String, String>();
		for(Parameter param : parameters)
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
			
			MigrateOneBinaryResult result = new MigrateOneBinaryResult();
			try
			{
				result.binary = readDestination(outputFile);
			}
			catch(FileNotFoundException fnfe)
			{
			}
			result.log.error_state = pr.getReturnCode();
			result.log.error = pr.getProcessErrorAsString();
			result.log.info = pr.getProcessOutputAsString();
			result.log.warn = pr.getProcessOutputAsString();
			result.log.properties = new Property[] { new Property() };
			result.log.properties[0].name = "name";
			result.log.properties[0].value = "value";
			return result;
		}
		catch(IOException e)
		{
			throw new PlanetsException(e);
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
