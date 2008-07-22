package eu.planets_project.ifr.core.services.migration.jmagickconverter.impl;



//import java.io.BufferedInputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXBException;
import javax.xml.soap.SOAPException;
import javax.xml.ws.BindingType;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;

import eu.planets_project.datamodel.TypeAgent;
import eu.planets_project.datamodel.TypeComponentManifestation;
import eu.planets_project.datamodel.TypeEvents;
import eu.planets_project.datamodel.TypeFile;
import eu.planets_project.datamodel.TypeFiles;
import eu.planets_project.datamodel.TypeManifestation;
import eu.planets_project.datamodel.TypeManifestationFile;
import eu.planets_project.datamodel.TypeMigrationPathwayRef;
import eu.planets_project.datamodel.TypePathwayType;
import eu.planets_project.datamodel.TypePlanetsDataModel;
import eu.planets_project.datamodel.TypePostTransformationFileSet;
import eu.planets_project.datamodel.TypeTransformationEvent;
import eu.planets_project.datamodel.TypeTransformationUnit;
import eu.planets_project.ifr.core.common.api.L2PlanetsService;
import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.common.datamodel.DataModelUtils;
import eu.planets_project.ifr.core.common.datamodel.PlanetsDataModelException;
import eu.planets_project.ifr.core.common.datamodel.XMLUtilities;
import eu.planets_project.ifr.core.common.datamodel.preservation.ComponentUpdater;
import eu.planets_project.ifr.core.common.datamodel.preservation.PreservationBase;
import eu.planets_project.ifr.core.common.datamodel.preservation.PreservationEntities;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.services.migration.jmagickconverter.impl.utils.GeneralImageConverter;
import eu.planets_project.ifr.core.services.migration.jmagickconverter.impl.utils.MigrationResults;
import eu.planets_project.ifr.core.storage.api.DataManagerLocal;


/**
 * This is a first draft of a level 2 service, which takes a XML-String representation of a Planets Datamodel instance
 * and returns an updated version of this XML-String.
 * This XML-String contains the references to the created/migrated files in the Data Registry, as well as other
 * additional Metainformation about the Agent (this service) and the Event (Preservation) and the outcomes.
 * This class implements the L2PlanetsService interface and the single method: invokeService(String xmlPDMString).  
 * 
 * @author Peter Melms, peter.melms@uni-koeln.de
 * Date: 25.06.2008
 * 
 */

@WebService(
        name = "L2JpgToTiffConverter", 
        serviceName= L2PlanetsService.NAME, 
        targetNamespace = PlanetsServices.NS )
@SOAPBinding(
        parameterStyle = SOAPBinding.ParameterStyle.BARE,
        style = SOAPBinding.Style.RPC)
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
@Stateless()
@Local(L2PlanetsService.class)
@Remote(L2PlanetsService.class)
@LocalBinding(jndiBinding = "planets/L2PlanetsService")
@RemoteBinding(jndiBinding = "planets-project.eu/L2PlanetsService")
//@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http")
public class L2JpgToTiffConverter extends PreservationBase implements Serializable, L2PlanetsService {

	private static final long serialVersionUID = -2694628997974517602L;

	   // Creating a PlanetsLogger...
    private final static String logConfigFile = "eu/planets_project/ifr/core/services/migration/jmagickconverter/logconfig/l2jpgtotiffconverter-log4j.xml";
    private PlanetsLogger plogger = PlanetsLogger.getLogger(this.getClass());
    
    // a GeneralImageConverter instance to carry out the migration.
    private GeneralImageConverter converter = new GeneralImageConverter();
    private MigrationResults migrationResults = new MigrationResults();
    private final static String TARGET_FORMAT = "TIFF";
    private final static String REQUIRED_SRC_FORMAT = "JPEG";
    private final static String OUTPUT_FOLDER = "L2JmagickConverter_OUTPUT";
    private final static String PATHWAY_TYPE = "Preservation";
    private static int MONTH;
    private static int DAY;
    private static int YEAR;
    private static int HOUR;
    private static int MINUTE;
    private static int SECOND;
    private static Calendar myCALENDAR;
    
    private TypePlanetsDataModel inputPDM; // the Planets Data Model object
    private Map<String, String> newManifestationIds = new HashMap<String, String>();
    
    /**
     * Default Constructor.
     * Creates some static fields, which are used for "timestamping" of duplicate files. 
     */
    public L2JpgToTiffConverter() {
    	// Creating a Calendar instance for the timestamp used in the storeBinaryInDataRegistry() method.
    	myCALENDAR = Calendar.getInstance();
    	DAY = myCALENDAR.get(Calendar.DAY_OF_MONTH);
    	MONTH = myCALENDAR.get(Calendar.MONTH) + 1;
    	YEAR = myCALENDAR.get(Calendar.YEAR);
    	HOUR = myCALENDAR.get(Calendar.HOUR_OF_DAY);
    	MINUTE = myCALENDAR.get(Calendar.MINUTE);
    	SECOND = myCALENDAR.get(Calendar.SECOND);
    }
    
    /**
     * The "main" method of that level 2 service. It takes a XML-String PDM instance,
     * carries out the migration of given images.
     * 
     * @param xmlPDMString a String representing a Planets DataModel instance
     * @return an updated Planets DataModel instance serialized to a XML String.
     * 
     */
    @WebMethod(
            operationName = L2PlanetsService.NAME, 
            action = PlanetsServices.NS + "/" + L2PlanetsService.NAME)
    @WebResult(
            name = L2PlanetsService.NAME+"Result", 
            targetNamespace = PlanetsServices.NS + "/" + L2PlanetsService.NAME, 
            partName = L2PlanetsService.NAME + "Result")
    public String invokeService(
    		@WebParam(
            name = "xmlPDMString", 
            targetNamespace = PlanetsServices.NS + "/" + L2PlanetsService.NAME, 
            partName = "xmlPDMString")String xmlPDMString) throws PlanetsException {
    	String updatedXmlPDM = null;
		try {
			plogger.debug("Unmarshalling the pdmString...");
			inputPDM = DataModelUtils.unmarshal(xmlPDMString);

			initialiseNewIdMap();
			inputPDM = preservePDM();
	
		} catch (PlanetsDataModelException e) {
			plogger.warn(e.getLocalizedMessage());
			e.printStackTrace();
			// Here should be returned the updated PDM as well...
			return xmlPDMString;
		}
		try {
			updatedXmlPDM = DataModelUtils.marshalToString(inputPDM);
		} catch (JAXBException e) {
			plogger.error("An error occured while trying to marshal the PDM Object to a XML String: ");
			e.printStackTrace();
		}
		return updatedXmlPDM;
	}

	
	// Get the srcFile from DataRegistry as a byte[]
	// This File-Object is returned for conversion.
	/**
	 * get the src file from the DataRegistry using the file reference contained in
	 * the XML-PDM String.
	 * The file is returned as byte[]. 
	 *
     * @param fileReference reference to the src-file in the DataRegistry 
     * @return src file as byte[] for conversion
     */
    private byte[] getBinaryFromDataRegistry(String fileReference){
		plogger.debug("Starting to get File from DataRegistry...");
		
		URI fileURI = null;
		try {
			fileURI = new URI(fileReference);
		} catch (URISyntaxException e1) {
			plogger.warn("Exception: " + e1.getLocalizedMessage());
			e1.printStackTrace();
		}

		DataManagerLocal dataRegistry = null;
		
		// Binding the DataManagerLocal-Interface to the local DataManager-Instance via JNDI.
		plogger.debug("Trying to get InitialContext for JNDI-Lookup...");
		try {
			Context ctx = new InitialContext();
			dataRegistry = (DataManagerLocal)ctx.lookup("planets-project.eu/DataManager/local");
			plogger.debug("Created dataRegistry-Object...");
		} catch (NamingException e2) {
			plogger.error("Could not lookup local DataManager!");
			e2.printStackTrace();
		}
		
		byte[] srcFileArray = null;
		try {
			plogger.debug("Retrieving file from DataRegistry: " + fileURI.toASCIIString());
			srcFileArray = dataRegistry.retrieveBinary(fileURI);
			plogger.debug("Successfully retrieved file!");
		} catch (SOAPException e) {
			plogger.error("Exception: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		return srcFileArray; 		
	}
	
	/**
	 * Private method to generate a timestamp, which is used, if a migrated file, meant to be stored
	 * in the DataRegistry already exists.
	 * In this case, the migrated file will be renamed by adding a timestamp (valid for each "conversion-session")
	 * to the filename.
	 * 
	 * @return a String containing the timestamp for this "session".
	 */
	private String getTimeStamp() {
		String day, month, year, hour, minute, second, millisecond = null;
		
		if(DAY > 9) {
			day = "" + DAY; 
		}
		else {
			day = "0" + DAY;
		}
		if(MONTH > 9) {
			month = "" + MONTH;
		}
		else {
			month = "0" + MONTH;
		}

		year = "" + YEAR;

		if(HOUR > 9) {
			hour = "" + HOUR;
		}
		else {
			hour = "0" + HOUR;
		}
		if(MINUTE > 9) {
			minute = "" + MINUTE;
		}
		else {
			minute = "0" + MINUTE;
		}
		if(SECOND > 9) {
			second = "" + SECOND;
		}
		else {
			second = "0" + SECOND;
		}
		Calendar now = Calendar.getInstance();
		millisecond = "" + now.get(Calendar.MILLISECOND) + "ms";
		
		String timestamp = day + "-" + month + "-" + year + "_" + hour + "-" + minute + "-" + second + "_" + millisecond;
		return timestamp;
	}
	
	
	/**
	 * Stores binary to the DataRegistry. Gets a byte[] containing the migrated file and the filename of the new file.
	 * 
	 * @param binary a byte[] containing the migrated file (here: a new .TIFF file).
	 * @param fileName the filename for the byte[]. Used to create the URI under which the image is stored to the DataRegistry.
	 * @return the PLANETS URI to the "new"/migrated file. Using this URI the file could be retrieved from the DataRegistry. 
	 * @throws SOAPException
	 */
	private URI storeBinaryInDataRegistry (byte[] binary, String fileName) throws SOAPException {
		plogger.debug("Starting to store File in DataRegistry...");
		DataManagerLocal dataRegistry = null;
		URI fileURI = null;
		URI registryRoot = null;
		String dataRegistryPath = null;
		
		// Binding the DataManagerLocal-Interface to the local DataManager-Instance via JNDI.
		plogger.debug("Trying to get InitialContext for JNDI-Lookup...");
		try {
			Context ctx = new InitialContext();
			dataRegistry = (DataManagerLocal)ctx.lookup("planets-project.eu/DataManager/local");
			plogger.debug("Created dataRegistry-Object...");
			try {
				// Get the root path of the DataRegistry...using an undocumented "hidden" feature of the DataManager,
				// which is to return the root path of the DR, when "null" is passed to the list() method.
				URI[] storagePaths = dataRegistry.list(null);
				registryRoot = storagePaths[0];
				dataRegistryPath = registryRoot.toASCIIString();
				plogger.debug("Registry root: " + dataRegistryPath);		
				
			} catch (SOAPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NamingException e2) {
			// TODO Auto-generated catch block
			plogger.debug("Could not lookup local DataManager!");
			e2.printStackTrace();
		}
		
		try {
			plogger.debug("Creating migratedFileURI...");
			plogger.debug("URI will be: " + dataRegistryPath + "/" + OUTPUT_FOLDER + "/" + fileName);
			
			// Create the new URI for storing the file to the DataRegistry.
			fileURI = new URI(dataRegistryPath + "/" + OUTPUT_FOLDER + "/" + fileName);
			
			plogger.debug("Created migratedFileURI: " + fileURI.toASCIIString());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			plogger.error("Malformed URI...! " + fileURI.toASCIIString());
			e.printStackTrace();
		}
		
		try {
			plogger.debug("Starting to write binary to DataRegistry...");
			// URI of the default OUTPUT_FOLDER of this Service, used as search root when testing
			// if a file already exists.
			URI outputFolderURI = new URI(dataRegistryPath + "/" + OUTPUT_FOLDER);
			plogger.debug("Outputfolder: " + outputFolderURI.toASCIIString());
			plogger.debug("Searching for duplicated files...");
			
			URI[] searchResults = dataRegistry.findFilesWithNameContaining(registryRoot, fileName);
			
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<searchResults.length;i++){
				sb = sb.append(searchResults[i].toASCIIString() + "\n");
			}
			plogger.debug("Found the following hits: " + sb.toString());
			
			// The returned URI[] searchResults is not NULL and
			if(searchResults != null) {
				// there have been some hits, e.g. files with the same filename, but maybe in a different path...
				if(searchResults.length > 0) {
					for(int i=0;i < searchResults.length;i++) {
						String currentURI = searchResults[i].toASCIIString();
						// Check if there have been hits inside the OUTPUT_FOLDER
						if(currentURI.indexOf(OUTPUT_FOLDER)!=-1) {
							// There is (at least) a file with the same name inside the OUTPUT_FOLDER so...
							plogger.debug("File already exists: " + fileName + ". File will be renamed...");
							
							// ...get a timestamp
							String timestamp = getTimeStamp();   
							
							// ...split the initial filename in a prefix and...
							String fileNamePrefix = fileName.substring(0, fileName.lastIndexOf("."));
					        plogger.debug("fileNamePrefix: " + fileNamePrefix);
					        
	//				        // ...and the postfix
					        String fileNamePostfix = fileName.substring(fileName.lastIndexOf("."));
					        plogger.debug("fileNamePostfix: " + fileNamePostfix);
					        
	//				        // and add the "_[timestamp]" to the filename
					        plogger.debug("Adding timestamp to filename: " + timestamp);
							String renamedFileName = fileNamePrefix + "_" + timestamp + fileNamePostfix;
							
						    plogger.debug("New migratedFileName: " + renamedFileName);
						    // create a new URI for the renamed file and...
						    URI renamedFileURI = new URI(outputFolderURI.toASCIIString() + "/" + renamedFileName);
							plogger.debug("New migratedFileURI: " + renamedFileURI.toASCIIString());
							plogger.debug("Storing file with new name: " + renamedFileName + " to DataRegistry...");
							// store it in the DataRegistry, using the new filename
							dataRegistry.storeBinary(renamedFileURI, binary);
							plogger.debug("Successfully stored binary to DataRegistry: " + renamedFileName);
							fileURI = renamedFileURI;
						}
						
						// There have been hits (e.g. files with the same name), but in a different folder,
						// so just store the file with its initial name to the DataRegistry
						else {
							plogger.debug("Attempting to store binary to DataRegistry: " + fileName);
							// store the file...
							dataRegistry.storeBinary(fileURI, binary);
							plogger.debug("Successfully stored binary to DataRegistry: " + fileName);
						}
					}
				}
				// There have been NO search hits, so store the file with its initial filename, too.
				else {
					plogger.debug("Attempting to store binary to DataRegistry: " + fileName);
					// store the file to the DR...
					dataRegistry.storeBinary(fileURI, binary);
					plogger.debug("Successfully stored binary to DataRegistry: " + fileName);
				}
			}
			
		} catch (LoginException e) {
			plogger.error("LoginException: " + e.getLocalizedMessage());
			e.printStackTrace();
		} catch (RepositoryException e) {
			plogger.error("RepositoryException: " + e.getLocalizedMessage());
			e.printStackTrace();
		} catch (URISyntaxException e) {
			plogger.error("URISyntaxException: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		// Last test if the URI created to store the file is a valid URI...
		if(DataModelUtils.isValidReference(fileURI.toASCIIString())) {
			// ...if yes, return it
			plogger.debug("Validating the created file URI: " + fileURI.toASCIIString());
			plogger.debug("Validataion result: OK!");
			return fileURI;
		}
		else {
			// ...if no, log out an error and return NULL.
			plogger.error("The URI of the migrated file is not valid!");
			return null;
		}
	}

	@Override
	public String getExportedFileSuffix() {
		return TARGET_FORMAT;
	}
	
	
	/**
	 * This method carries out the Migration. It retrieves binaries from the DataReistry, converts them using 
	 * ImageMagick and stores the new migrated files to the DataRegistry. 
	 *
     * @param inputFiles a TypeFiles Object, which contains the files to be migrated (obtained from the DataModel) 
     * @return a TypeFiles Object, containing the MetaData for the migrated Files
     *
	 * @see eu.planets_project.ifr.core.common.datamodel.preservation.PreservationTool#export(eu.planets_project.datamodel.TypeFiles)
	 */
	public TypeFiles export(TypeFiles inputFiles) {
		URI resultURI = null;
		String migratedFileName = null;
		TypeFiles migratedTypeFiles = new TypeFiles();
		// Get the File element from the DataModel
		List<TypeFile> fileList = inputFiles.getFile();
		// If the list of files containes at least one file
		if(fileList.size()> 0){
			// get the first (and by contract: only file in this list, as long as a service just converts ONE file at a time)
			TypeFile typeFile = fileList.get(0);
			// Get the File reference to the DataRegistry from the PDM
			String fileRef = typeFile.getFileRef();
			// get the filename from the PDM
			String fileName = typeFile.getFileName();
			// test if the filename contains a file extension
			if(fileName.indexOf(".")!=-1) {
				// ...if yes, create a new filename, by stripping the old extension and adding a new one
				// indicating the target format.
				migratedFileName = fileName.substring(0, fileName.lastIndexOf("."))+ "." + TARGET_FORMAT;
			}
			else {
				// ...if no, just add the TARGET_FORMAT (--> "TIFF")
				migratedFileName = fileName + "." + TARGET_FORMAT;
			}
			
			// retrieve the src-file from the DataRegistry 
			byte[] srcBinary = getBinaryFromDataRegistry(fileRef);
			plogger.debug("Successfully retrieved files from DataRegistry!");
			// Convert the retrieved file, using a GeneralImageConverter Object, and receive the MigrationResults object
			// from this conversion process.
			migrationResults = converter.convertImage(srcBinary, REQUIRED_SRC_FORMAT, TARGET_FORMAT, plogger);
			
			// If migration was successful, store the migrated file to the DataRegistry
			if(migrationResults.migrationWasSuccessful()) {
				try {
					plogger.debug(this.getClass().toString() + ": Trying to store file in DataRegistry...");
					// store the file to the DataRegistry and get the URI to this file back.
					resultURI = storeBinaryInDataRegistry(migrationResults.getByteArray(), migratedFileName);
				} catch (SOAPException e) {
					// TODO Auto-generated catch block
					plogger.error("File could not be stored in DataRegistry!");
					e.printStackTrace();
				}
				
				// Add details to TypeFile to be returned
				TypeFile tiffFile = new TypeFile();
				tiffFile.setFileRef(resultURI.toASCIIString());
				tiffFile.setFileName(migratedFileName);
		        tiffFile.setWorkingPath(typeFile.getWorkingPath());
		        migratedTypeFiles.getFile().add(0, tiffFile);
		        migratedTypeFiles = populateFileDetails(migratedTypeFiles, typeFile.getWorkingPath(), OUTPUT_FOLDER);
			}
			// ...otherwise give out an error message
			else {
				plogger.error("An Error has occured: " + migrationResults.getMessage());
			}
		}
		return migratedTypeFiles;
	}
	
	

	public String getDescription() {
		return "Converts images from JPEG to TIFF using ImageMagick.";
	}

	public String getName() {
		return "L2JpgToTiffConverter";
	}

	public int getNumberInputFiles() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getVersion() {
		return "1.0";
	}
	
	public String getFullClass() {
		return "L2JpgtoTiffConverter";
	}
	
	
	
	
    

	/**
	 * @param inputPDM the inputPDM to set
	 */
	public void setPDM(TypePlanetsDataModel inputPDM) {
		this.inputPDM = inputPDM;
		initialiseNewIdMap();
	}
    
	/**
	 * Creates a map of new manifestation IDs for potential new manifestation 
	 * created by this process.
	 */
	private void initialiseNewIdMap() {
		for (TypeManifestation manifestation : inputPDM.getDeliverableUnits().getManifestation()) {
			newManifestationIds.put(manifestation.getManifestationRef(), UUID.randomUUID().toString());
		}
	}
	
    /**
     * Migrate each transformation unit and update the PDM file.
     * 
     * @return TypePlanetsDataModel the new PDM after migration
     * @throws PlanetsDataModelException a general exception
     */
    public TypePlanetsDataModel preservePDM() throws PlanetsDataModelException {
    	
    	try{
    		
	        List<TypeTransformationUnit> transformationUnits = inputPDM.getTransformations().getTransformationUnit();
	        
	        for(TypeTransformationUnit transformationUnit : transformationUnits) {
	        
	        	// Check the integrity of the migration pathways.
	        	Set<TypePathwayType> pathwayTypes = new HashSet<TypePathwayType>();
        		for(TypeMigrationPathwayRef migrationPathway : transformationUnit.getMigrationPathwayRef()) {
        			pathwayTypes.add(migrationPathway.getPathwayType());
        		}
        		if(pathwayTypes.size() != 1) {
        			String errorMessage = "Multiple migration pathway types encountered: " + pathwayTypes;
        			plogger.error(errorMessage);
        			return null;
        		}
        		
        		// migrate each transformation unit
        		applyTranformationUnit(transformationUnit);
	        }
	        
	        for (TypeManifestation manifestation : inputPDM.getDeliverableUnits().getManifestation()) {
		        // Remove any file (in a given Manifestation) that doesn't keep a link to 
	        	// a ComponentManifestation. 
	        	// That link would have been broken during preservation. 
	        	DataModelUtils.removeUnusedManifestationFiles(manifestation);	        	
	        }
	        
    	} catch(PlanetsDataModelException e) {
    		plogger.error("Error aborted the migration process: " + e.getMessage(), e);
    		return null;
    	}
    	
        return inputPDM;
    }

    
    /**
     * The main part of the migration: apply a transformation unit to a ComponentManifestation.
     *
     * @param transformationUnit the current transformation unit
     * @throws PlanetsDataModelException Mainly for the case where the input PDM file was inconsistent in some way
     */
    private void applyTranformationUnit(TypeTransformationUnit transformationUnit) throws PlanetsDataModelException {

        // Check we have 1 pathway defined for this transformation
        List<TypeMigrationPathwayRef> definedPathways = transformationUnit.getMigrationPathwayRef();
        if (definedPathways.size() < 1) {
        	String errorMessage = "Preservation Plan should have at least 1 pathway defined for a " +
        			              "transformation unit: " + transformationUnit.getTransformationUnitRef();
        	plogger.error(errorMessage);
            throw new PlanetsDataModelException(errorMessage);
        }

        // Get the component for this transformation unit
        String componentManifestationRef = transformationUnit.getComponentManifestationRef();

        TypeComponentManifestation componentManifestation = 
        	DataModelUtils.getComponentManifestationByRef(componentManifestationRef, inputPDM);
        
        if (componentManifestation == null) {
        	String errorMessage = "Component manifestation not found: " + componentManifestationRef.toString();
            throw new PlanetsDataModelException(errorMessage);
        }
        
        // Retrieve the manifestation holding the component being migrated.
        TypeManifestation manifestationToMigrate = getManifestationToMigrate(transformationUnit);

        // Carry out the actual preservation on the component.
        PreservationEntities entities = preserveComponent(componentManifestation, manifestationToMigrate, transformationUnit);
        if (entities == null) {
        	String errorMessage = "An error occurred during the migration of component manifestation: " 
        		                   + componentManifestationRef.toString();
            throw new PlanetsDataModelException(errorMessage);
        }
        
        
        // Replace the copy of the old Component manifestation with the updated one.
        manifestationToMigrate.getComponentManifestation().remove(componentManifestation);
        manifestationToMigrate.getComponentManifestation().add(entities.getComponentManifestation());
        
        // Create a new PostTranformationFileSet, add its reference to the TransformationUnit
        // and add it to the PDM.
        TypePostTransformationFileSet newPostTransformationFS = new TypePostTransformationFileSet();
        newPostTransformationFS.setFileSetRef(UUID.randomUUID().toString());
        newPostTransformationFS.getTransformationUnitRef().add(transformationUnit.getTransformationUnitRef());
        
        transformationUnit.setPostTransformationFileSetRef(newPostTransformationFS.getFileSetRef());
        
        inputPDM.getFileSets().getFileSet().add(newPostTransformationFS);
        
        // Add the references of the new Transformation Event in the TransformationUnit  
        // and add it to the PDM.
        
        transformationUnit.setTransformationEventRef(entities.getTransformationEvent().getEventRef());

        if (inputPDM.getEvents() == null) {
        	inputPDM.setEvents(new TypeEvents());
        }
        inputPDM.getEvents().getEvent().add(entities.getTransformationEvent());
        
        // Finally, add the new files to the new PDM.
        for (TypeFile file : entities.getTypeFiles().getFile()) {
        	// set the file's FileSetRef to be that of the new PostTransformationSet.
        	file.setFileSetRef(newPostTransformationFS.getFileSetRef());
        	
        	// Create a new Manifestation File and add it to the manifestation.
        	TypeManifestationFile newManifestationFile = new TypeManifestationFile();
        	newManifestationFile.setFileRef(file.getFileRef());
        	newManifestationFile.setPath(file.getWorkingPath());
        	manifestationToMigrate.getManifestationFile().add(newManifestationFile);
        	
        	inputPDM.getFiles().getFile().add(file);
        }
    }
    
	/**
	 * Preserve component: retrieves each file to be migrated and apply the tool 
	 * to do the work. The relevant PDM objects are then updated or created.
	 * 
	 * @param oldCompManif the component manifestation
	 * @param manifestationToMigrate the manifestation linked to the component manifestation
	 * @param transformationUnit the transformation unit
	 * @return the preservation entities
	 * @throws Exception 
	 */
	private PreservationEntities preserveComponent(TypeComponentManifestation oldCompManif,
			                                      TypeManifestation manifestationToMigrate,
			                                      TypeTransformationUnit transformationUnit) {
        
        // Check we have 1 migration pathway
        if (transformationUnit.getMigrationPathwayRef().size() != 1) {
            plogger.error("Transformation ref " + transformationUnit.getTransformationUnitRef() 
            		     + " contains no or more than 1 migration pathway");
            return null;
        }
        
		// Select the files to be selected based on the file URI.
		TypeFiles preTransFiles = new TypeFiles();
		for (String fileRef : oldCompManif.getFileRef()) {
		    TypeFile file = DataModelUtils.getFileByRef(fileRef, inputPDM);
		    
		    // Retrieve the file's relative path from the corresponding ManifestationFile entity
		    // and set the file's workingPath to it.
		    TypeManifestationFile manifestationFile = DataModelUtils.getManifestationFileByRef(fileRef, manifestationToMigrate);
			file.setWorkingPath(manifestationFile.getPath());
		    
			//String targetFileFormatURI = migrationPathway.getOriginalFormat().getFormatInfo().getFormatURI();
			//String fileFormatURI = file.getFormatInfo().get(0).getFormatURI();
				
		    preTransFiles.getFile().add(file);	
		}
		
		// Check the input files are as expected and apply the tool.
		if (preTransFiles.getFile().size() == 0) {
		    String errorMessage = "Pre-Transformation file set is empty";
			plogger.error(errorMessage);
			return null;
        }
			
		
		TypeFiles postTransfFiles = export(preTransFiles);
		
		// Create a new Component manifestation.
		TypeComponentManifestation newCompManif = new TypeComponentManifestation();
		newCompManif.setComponentManifestationRef(UUID.randomUUID().toString());
		newCompManif.setComponentRef(oldCompManif.getComponentRef());
		
		for (TypeFile file : postTransfFiles.getFile()) {
			newCompManif.getFileRef().add(file.getFileRef());
			
			if(postTransfFiles.getFile().size() == 1) {
				newCompManif.getMasterFileRef().add(file.getFileRef());
            }
		}
		
		newCompManif.setComponentType(oldCompManif.getComponentType());
		newCompManif.setComponentManifestationType(TARGET_FORMAT);

		// Create the TranformationEvent.
        TypeTransformationEvent transformationEvent = new TypeTransformationEvent();
        transformationEvent.setEventRef(UUID.randomUUID().toString());
        transformationEvent.setEventDate(XMLUtilities.getCurrentXMLDate());
        
		TypeAgent newAgent = new TypeAgent();
		newAgent.setAgentRef("42");
		newAgent.setName(getName());
		newAgent.setType(PATHWAY_TYPE);

        transformationEvent.setAgent(newAgent);
		
        // Update linked components that could be affected 
        // by the changes made to the current component.
        updateLinkedComponents(newCompManif, preTransFiles, postTransfFiles);
        
		return new PreservationEntities(newCompManif, postTransfFiles, transformationEvent);
	}

	/**
	 * Looks for linked component in an updated component manifestation
	 * and fixes them according to the new component file names.
	 * 
	 * @param componentManifestation the component manifestation
	 * @param preTransFiles the original set of files
	 * @param postTransfFiles the migrated set of files
	 */
	private void updateLinkedComponents(TypeComponentManifestation componentManifestation,
			                            TypeFiles preTransFiles,
			                            TypeFiles postTransfFiles) {
		// Retrieve the linked component manifestations.
		List<String> componentLinks  = componentManifestation.getLinkedComponentManifestationRef();
		
		for (String linkedCompManifRef : componentLinks) {
			TypeComponentManifestation linkedCompManif = 
				DataModelUtils.getComponentManifestationByRef(linkedCompManifRef, inputPDM);
			
            String type = linkedCompManif.getComponentManifestationType();
            ComponentUpdater componentUpdater = (ComponentUpdater) createGenericToolWrapperObject(type);
            
            if(componentUpdater == null) {
            	plogger.info("No tool can update linked components of type " + type);
            } else {
            	try {
					linkedCompManif = componentUpdater.updateLinkedComponents(componentManifestation, 
							preTransFiles, postTransfFiles);
				} catch (Exception e) {
					plogger.info("Failed to update linked components of type " + type 
							    + " - Exception: " + e.getMessage());
				}
            }
		}
	}

	/**
	 * Convenience method that gets the manifestation to migrate from 
	 * the ComponentManifestationRef found in the TransformationUnit.
	 * 
	 * @param transformationUnit the current transformation unit.
	 * @return manifestationToMigrate the desired manifestation.
	 * @throws PlanetsDataModelException the exception.
	 */
	private TypeManifestation getManifestationToMigrate(TypeTransformationUnit transformationUnit) throws PlanetsDataModelException {
        
		// get the source manifestation ref of this transformation unit
        String oldManifestationRef = transformationUnit.getSourceManifestationRef();
        
        // Get the ref for the new manifestation and check if it has already been 
        // added by a previous transformation unit. If not create it.
        String newManifestationRef = newManifestationIds.get(oldManifestationRef);
        
        TypeManifestation manifestationToMigrate = DataModelUtils.getManifestationByRef(newManifestationRef, inputPDM);
        if (manifestationToMigrate == null) {
        	
        	// Retrieve the original manifestation
        	TypeManifestation initialManifestation = DataModelUtils.getManifestationByRef(oldManifestationRef, inputPDM);
        	if (initialManifestation == null) {
        		String errorMessage = "The transformation source manifestation is inconsistent";
                plogger.error(errorMessage);
                throw new PlanetsDataModelException(errorMessage);	
        	}
        	
        	// Copy the initial manifestation to a new manifestation object
        	manifestationToMigrate = DataModelUtils.copyManifestation(initialManifestation);
        	
        	initialManifestation.setActive(false);
        	
        	// Update new manifestation's ref and add it to the PDM.
        	manifestationToMigrate.setManifestationRef(newManifestationRef);
        	
        	inputPDM.getDeliverableUnits().getManifestation().add(manifestationToMigrate);
        }
		
		return manifestationToMigrate;
	}
	
	/**
	 * Creates a new instance of the object defined by the class name
	 * passed as argument.
	 * 
	 * @param className the class name of the object to be created.
	 * @return the new instance of the desired object.
	 */
    private Object createGenericToolWrapperObject(String className) {

        plogger.debug("Looking for tool name: " + className);

        // try and get the tool from the command
        Object tool = null;
        plogger.debug("The command is not in the beans file: " + className);
        try {
            tool = Class.forName(className).newInstance();
        } catch (ClassNotFoundException cEx) {
            plogger.debug("The class is not in the class path: " + className);
        } catch (InstantiationException iEx) {
            plogger.debug("The class could not be Instantiated: " + className);
        } catch (IllegalAccessException illEx) {
            plogger.debug("The class has no default constructor: " + className);
        }

        return tool;
    }

	/**
	 * @return the logger
	 */
	public PlanetsLogger getLogger() {
		return plogger;
	}

	/**
	 * @param logger the logger to set
	 */
	public void setLogger(PlanetsLogger logger) {
		this.plogger = logger;
	}
	
}
	
