package eu.planets_project.ifr.core.services.characterisation.metadata.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;

import nz.govt.natlib.meta.FileHarvestSource;
import nz.govt.natlib.meta.config.Config;
import nz.govt.natlib.meta.config.Configuration;
import nz.govt.natlib.meta.config.ConfigurationException;
import nz.govt.natlib.meta.ui.PropsManager;
import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.common.services.ByteArrayHelper;
import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.characterise.BasicCharacteriseOneBinary;

/**
 * Service wrapping the Metadata Extraction Tool from the National Archive of
 * New Zealand (http://meta-extractor.sourceforge.net/)
 * 
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@WebService(name = MetadataExtractor.NAME, serviceName = BasicCharacteriseOneBinary.NAME, targetNamespace = PlanetsServices.NS)
@Local(BasicCharacteriseOneBinary.class)
@Remote(BasicCharacteriseOneBinary.class)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@Stateless()
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
public class MetadataExtractor implements BasicCharacteriseOneBinary,
		Serializable {
	public static final String NAME = "MetadataExtractor";
	private static final long serialVersionUID = -1622020084969585711L;
	public static final QName QNAME = new QName(PlanetsServices.NS,
			BasicCharacteriseOneBinary.NAME);
	public static final String RESOURCES = "PC/metadata/src/resources/";

	/**
	 * @see eu.planets_project.ifr.core.common.services.characterise.BasicCharacteriseOneBinary#basicCharacteriseOneBinary(byte[])
	 */
	@WebMethod(operationName = BasicCharacteriseOneBinary.NAME, action = PlanetsServices.NS
			+ "/" + BasicCharacteriseOneBinary.NAME)
	@WebResult(name = BasicCharacteriseOneBinary.NAME + "Result", targetNamespace = PlanetsServices.NS
			+ "/" + BasicCharacteriseOneBinary.NAME, partName = BasicCharacteriseOneBinary.NAME
			+ "Result")
	public String basicCharacteriseOneBinary(
			@WebParam(name = "binary", targetNamespace = PlanetsServices.NS
					+ "/" + BasicCharacteriseOneBinary.NAME, partName = "binary")
			byte[] binary) throws PlanetsException {
		File file = ByteArrayHelper.write(binary);
		/* Create a HarvestSource of the object we want to harvest */
		FileHarvestSource source = new FileHarvestSource(file);
		try {
			/* Get the native Configuration: */
			Configuration c = Config.getInstance().getConfiguration(
					"Extract in Native form");
			String tempFolder = file.getParent();
			c.setOutputDirectory(tempFolder);
			/* Harvest the file: */
			c.getHarvester().harvest(c, source, new PropsManager());
			/* The resulting file is the original file plus ".xml": */
			File result = new File(c.getOutputDirectory() + File.separator
					+ file.getName() + ".xml");
			result.deleteOnExit();
			return read(result.getAbsolutePath());
		} catch (ConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param location The location of the text file to read
	 * @return Return the content of the file at the specified location
	 */
	public static String read(String location) {
		StringBuilder builder = new StringBuilder();
		Scanner s;
		try {
			s = new Scanner(new File(location));
			while (s.hasNextLine()) {
				builder.append(s.nextLine()).append("\n");
			}
			return builder.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
