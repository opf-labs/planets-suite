package eu.planets_project.ifr.core.services.identification.droid.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jws.WebService;
import javax.xml.ws.soap.MTOM;

import uk.gov.nationalarchives.droid.core.BinarySignatureIdentifier;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationMethod;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationRequest;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResult;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResultCollection;
import uk.gov.nationalarchives.droid.core.interfaces.RequestIdentifier;
import uk.gov.nationalarchives.droid.core.interfaces.resource.FileSystemIdentificationRequest;
import uk.gov.nationalarchives.droid.core.interfaces.resource.RequestMetaData;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.ifr.core.common.conf.Configuration;
import eu.planets_project.ifr.core.common.conf.ConfigurationException;
import eu.planets_project.ifr.core.common.conf.ServiceConfig;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.datatypes.Tool;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.utils.DigitalObjectUtils;
import eu.planets_project.services.utils.ServiceUtils;

/**
 * Droid identification service.
 * 
 * @author Fabian Steeg
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a> <a
 *         href="http://sourceforge.net/users/carlwilson-bl"
 *         >carlwilson-bl@SourceForge</a> <a
 *         href="https://github.com/carlwilson-bl">carlwilson-bl@github</a>
 */
@WebService(name = Droid.NAME, serviceName = Identify.NAME, targetNamespace = PlanetsServices.NS, endpointInterface = "eu.planets_project.services.identify.Identify")
@MTOM
@StreamingAttachment(parseEagerly = true, memoryThreshold = ServiceUtils.JAXWS_SIZE_THRESHOLD)
public final class Droid implements Identify, Serializable {
	private static Logger LOG = Logger.getLogger(Droid.class.getName());

	/** The ID for serialization */
	private static final long serialVersionUID = -7116493742376868770L;
	/** The name of the service */
	static final String NAME = "Droid";
	/** The version of the service */
	static final String VERSION = "2.0";

	// The configuration for the service
	private static Configuration CONF;
	static {
		try {
			// Use the standard config method to load a config
			// from the usual areas
			ServiceConfig.getConfiguration("Droid");
		} catch (ConfigurationException e) {
			// If no config loaded then try our local resource
			CONF = ServiceConfig
					.getConfiguration(Droid.class
							.getResourceAsStream("/org/opf_labs/services/identification/droid/Droid.properties"));
		}
	}

	private static BinarySignatureIdentifier DROID = new BinarySignatureIdentifier();
	static {
		File file = new File(CONF.getString("droid.sigfile.location")
				+ CONF.getString("droid.sigfile.name"));
		DROID.setSignatureFile(file.getAbsolutePath());
		DROID.init();
	}

	private IdentificationMethod method;

	/**
	 * {@inheritDoc}
	 * 
	 * @see eu.planets_project.services.identify.Identify#identify(eu.planets_project.services.datatypes.DigitalObject,
	 *      java.util.List)
	 */
	public IdentifyResult identify(final DigitalObject digitalObject,
			final List<Parameter> parameters) {
		File file = DigitalObjectUtils.toFile(digitalObject);
		List<URI> types = identifyOneBinary(file);
		ServiceReport report = null;
		if (types == null || types.size() == 0) {
			report = new ServiceReport(Type.ERROR, Status.TOOL_ERROR,
					"No identification result for: " + file);
		} else {
			report = new ServiceReport(Type.INFO, Status.SUCCESS, "");
		}
		IdentifyResult.Method method = null;
		if (IdentificationMethod.BINARY_SIGNATURE.equals(this.method)) {
			method = IdentifyResult.Method.MAGIC;
		} else if (IdentificationMethod.EXTENSION.equals(this.method)) {
			method = IdentifyResult.Method.EXTENSION;
		}
		IdentifyResult result = new IdentifyResult(types, method, report);
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see eu.planets_project.services.identify.Identify#describe()
	 */
	public ServiceDescription describe() {
		ServiceDescription.Builder sd = new ServiceDescription.Builder(
				"DROID Identification Service",
				Identify.class.getCanonicalName());
		sd.version(VERSION);
		sd.classname(this.getClass().getCanonicalName());
		sd.description("Identification service based on Droid (DROID 3.0, Signature File 16).");
		sd.author("Carl Wilson, Fabian Steeg");
		sd.tool(Tool.create(null, "DROID", "6.0", null,
				"http://droid.sourceforge.net/"));
		sd.furtherInfo(URI.create("http://droid.sourceforge.net/"));
		// Taking this out as logo is no longer hosted there, and this is bad
		// practice anyway - should be hosted locally.
		// sd.logo(
		// URI.create("http://droid.sourceforge.net/wiki/skins/snaphouston/droidlogo.gif"));
		sd.serviceProvider("The Planets Consortium.");
		return sd.build();
	}

	/**
	 * Identify a file represented as a byte array using Droid.
	 * 
	 * @param tempFile
	 *            The file to identify using Droid
	 * @return Returns the Pronom IDs found for the file as URIs in a Types
	 *         object
	 */
	private List<URI> identifyOneBinary(final File tempFile) {
		// Set up the identification request
		RequestMetaData metadata = new RequestMetaData(tempFile.length(),
				tempFile.lastModified(), tempFile.getName());
		RequestIdentifier identifier = new RequestIdentifier(tempFile.toURI());
		identifier.setParentId(1L);
		IdentificationRequest request = new FileSystemIdentificationRequest(
				metadata, identifier);
		try {
			request.open(new FileInputStream(tempFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Get the results collection
		IdentificationResultCollection resultSet = DROID
				.matchBinarySignatures(request);
		List<IdentificationResult> results = resultSet.getResults();

		List<URI> formatHits = new ArrayList<URI>(results.size());
		// Now iterate through the collection and create the format URIs
		for (IdentificationResult result : results) {
			formatHits.add(URI.create("info:pronom/" + result.getPuid()));
			this.method = result.getMethod();
		}
		return formatHits;
	}

	private static void getCurrentSigFileVersion() {

	}
}
