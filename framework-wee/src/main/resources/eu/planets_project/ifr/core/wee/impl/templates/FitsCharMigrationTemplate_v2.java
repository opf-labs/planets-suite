package eu.planets_project.ifr.core.wee.impl.templates;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.planets_project.ifr.core.storage.api.DataRegistry;
import eu.planets_project.ifr.core.storage.api.DataRegistryFactory;
import eu.planets_project.ifr.core.storage.impl.jcr.JcrDigitalObjectManagerImpl;
import eu.planets_project.ifr.core.storage.impl.util.PDURI;
import eu.planets_project.ifr.core.wee.api.ReportingLog.Message;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResultItem;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplateHelper;
import eu.planets_project.ifr.core.wee.api.workflow.jobwrappers.LogReferenceCreatorWrapper;
import eu.planets_project.ifr.core.wee.api.workflow.jobwrappers.MigrationWFWrapper;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.datatypes.Agent;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Event;
import eu.planets_project.services.datatypes.Metadata;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.migrate.Migrate;

import java.io.*;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import com.sun.org.apache.xml.internal.serialize.*;

import java.util.Calendar;
import java.text.SimpleDateFormat;


/**
 * @author <a href="mailto:roman.graf@ait.ac.at">Roman Graf</a>
 * @since 21.04.2010
 */
public class FitsCharMigrationTemplate_v2 extends
		WorkflowTemplateHelper implements WorkflowTemplate {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** URI to use for digital object repository creation. */
	private static final URI PERMANENT_URI_PATH = URI.create("/ait/data/pdf");
	
	private static final String CHARACTERISATION_METADATA = "Characterisation metadata";
	private static final String CHARACTERISATION_EVENT = "planets://repository/event/characterisation";
	private static final String IDENTIFICATION_EVENT = "planets://repository/event/identification";
	private static final String MIGRATION_EVENT = "planets://repository/event/migration";
	private static final String SIP_CREATION_EVENT = "planets://repository/event/sip_creation";
	private static final int DURATION = 0;

	private static String DATE_FORMAT = "dd-MM-yyyy";
	private static String TIME_FORMAT = "hh-mm-ss-SS";
	private static String METADATA_XML = "/SIP_toc.xml";
	private static String SIP_NAME = "user1-test0001-OriginalEpublication-";
	private static String SIP_FORMAT = ".zip";
	
    /**
     * Characterization service to execute
     */
	private Characterise characterise;
	
    /**
     * Identify service to execute
     */
    private Identify identify;

	/**
	 * The migration service to execute
	 */
	private Migrate migrate;

	private DigitalObject processingDigo;

	private String URI_SEPARATOR = "/";
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#describe()
	 */
	public String describe() {
		return "This template performs the characterisation, identification and migration steps " +
				"of the Testbed's experiment. It implements insert in JCR, update in JCR and sending" +
				"back to the repository.";
	}


	public WorkflowResult initializeExecution() {
		this.getWFResult().setStartTime(System.currentTimeMillis());
		return this.getWFResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#execute()
	 */
	@SuppressWarnings("finally")
	public WorkflowResult execute(DigitalObject dgoA) {

		// document all general actions for this digital object
		WorkflowResultItem wfResultItem = new WorkflowResultItem(
				dgoA.getPermanentUri(),
				WorkflowResultItem.GENERAL_WORKFLOW_ACTION, 
				System.currentTimeMillis(),
				this.getWorkflowReportingLogger());
		this.addWFResultItem(wfResultItem);
		wfResultItem.addLogInfo("working on workflow template: " + this.getClass().getName());
		wfResultItem.addLogInfo("workflow-instance id: " + this.getWorklowInstanceID());

		// start executing on digital ObjectA
		this.processingDigo = dgoA;

		try {
			// Characterisation service
            wfResultItem.addLogInfo("Start characterization. dgoA: " + dgoA);
            List<Property> properties = runCharacterization(dgoA);
            wfResultItem.addLogInfo("Completed characterization.");
			Event eCharacterisation = buildEvent(properties);
			dgoA = addEvent(dgoA, eCharacterisation, null);
            wfResultItem.addLogInfo("Characterisatin properties added to the digital object.");
                        
            // Identification service for data enrichment (e.g. mime type of output object)
            String[] types = runIdentification(dgoA);
            wfResultItem.addLogInfo("Completed identification. result" + Arrays.asList(types).toString());

			for (WorkflowResultItem wri : getWFResult().getWorkflowResultItems()) {
				wfResultItem.addLogInfo("for Identification parameter: " + Arrays.asList(wri.getServiceParameters()).toString());
				wfResultItem.addLogInfo("for endpoint: " + wri.getServiceEndpoint());
			}

			String initialFileName = dgoA.getPermanentUri().toString().substring( 
					dgoA.getPermanentUri().toString().lastIndexOf(URI_SEPARATOR) + 1 );
              wfResultItem.addLogInfo(new Message("Characterisation", new Parameter("File", initialFileName), new Parameter(
                    "Result", "Properties")));
              wfResultItem.addLogInfo(new Message("Identification", new Parameter("File", initialFileName), new Parameter(
                    "Result", Arrays.asList(types).toString())));

            // Extract metadata - will otherwise get lost between steps!
            String metadata = "";
            List<Metadata> mList = dgoA.getMetadata();
            if ((mList != null) && (mList.size() > 0)) {
                metadata = mList.get(0).getContent();
            }

            if (metadata == null) {
            	wfResultItem.addLogInfo("No metadata contained in DigitalObject!");
            } else {
            	wfResultItem.addLogInfo("Extracted metadata: " + metadata);
            }            

			// Migration service
        	wfResultItem.addLogInfo("STEP 3: Starting migration");
			URI dgoBRef = runMigration(migrate, dgoA.getPermanentUri(), true);
			wfResultItem.addLogInfo("Completed migration. URI: " + dgoBRef);
			
			String migrationEndpoint = "";
			for (WorkflowResultItem wri : getWFResult().getWorkflowResultItems()) {
				wfResultItem.addLogInfo("for Migration parameter: " + Arrays.asList(wri.getServiceParameters()).toString());
				wfResultItem.addLogInfo("for endpoint: " + wri.getServiceEndpoint());
				if (wri.getServiceParameters().size() > 0 
						&& Arrays.asList(wri.getServiceParameters()).toString().contains("migration")) {
					wfResultItem.addLogInfo(new Message("Migration", new Parameter("Input", Arrays.asList(types).toString())
					, new Parameter("Result", Arrays.asList(wri.getServiceParameters()).toString())));
					migrationEndpoint = wri.getServiceEndpoint();
				}
			}

			// Evaluate migrated file data
			String migratedFileName = "";
			String migratedFileSize = "";
			
			if (dgoBRef != null) {
				try {
				    DataRegistry dataRegistry = DataRegistryFactory.getDataRegistry();
					URI baseUri = new PDURI(dgoBRef.normalize()).formDataRegistryRootURI();
					wfResultItem.addLogInfo("base URI " + baseUri);
			
					DigitalObject obj = dataRegistry.getDigitalObjectManager(baseUri).retrieve(dgoBRef);		
					wfResultItem.addLogInfo("obj: " + obj.toString());

					InputStream contentStream = obj.getContent().getInputStream();	        	  
		        	BufferedReader br = new BufferedReader(new InputStreamReader(contentStream));
		        	StringBuilder sb = new StringBuilder();
		        	String line = null;
		
		        	while ((line = br.readLine()) != null) {
		        	  sb.append(line + "\n");
		        	}
		
		        	br.close();
	    			migratedFileName = dgoA.getPermanentUri().toString().substring( 
	    					dgoA.getPermanentUri().toString().lastIndexOf(URI_SEPARATOR) + 1 );
	    			migratedFileSize = Integer.toString(sb.toString().length());
				} catch (Exception e) {
					wfResultItem.addLogInfo("migration error: " + e.getMessage());
				}
			}
			
			// Insert in JCR repository
            wfResultItem.addLogInfo("STEP 4: Insert in JCR repository. initial digital object: " + dgoA.toString());
      	    // Manage the Digital Object Data Registry:
            wfResultItem.addLogInfo("Initialize JCR repository instance.");
            JcrDigitalObjectManagerImpl dodm = 
            	 (JcrDigitalObjectManagerImpl) JcrDigitalObjectManagerImpl.getInstance();
      	    DigitalObject dgoB = dodm.store(PERMANENT_URI_PATH, dgoA, true);
         	wfResultItem.addLogInfo("Completed storing in JCR repository: " + dgoB.toString());
         	
        	wfResultItem.addLogInfo(new Message("JCRinsert", new Parameter("Digital Object", dgoB.getTitle()), new Parameter(
                    "Result", dgoB.getPermanentUri().toString())));

         	// Enrich digital object with format information from identification service
         	if (types != null) {
         		wfResultItem.addLogInfo("Identified formats count: " + types.length);
				for (int i=0; i<types.length; i++) {
					wfResultItem.addLogInfo("type[" + i + "]: " + types[i]);
				}			

				if (types[0] != null) {
	    			List<Property> pList = new ArrayList<Property>();
	    			Property pIdentificationContent = new Property.Builder(URI.create("Identify"))
	    	        	.name("content by reference")
	    	        	.value(types[0].toString())
	    	        	.description("This is a format for initial document identified by identification service")
	    	        	.unit("URI")
	    	        	.type("digital object format")
	    	        	.build();
	    			pList.add(pIdentificationContent);
	    			Event eIdentifyFormat = new Event(
	    					IDENTIFICATION_EVENT, System.currentTimeMillis() + "", new Double(DURATION), 
	    					new Agent("http://testbed-dev.planets-project.ait.ac.at:80/pserv-pc-droid/Droid?wsdl"
	    							, identify.NAME, identify.QNAME.toString()), 
	    					pList);
					dgoB = addEvent(dgoB, eIdentifyFormat, URI.create(types[0]));
					
					List<Property> pMigrationList = new ArrayList<Property>();
					Property pMigrationContent = new Property.Builder(URI.create("Migrate"))
			        	.name("content by reference")
			        	.value(types[0].toString())
			        	.description("This is a migration event")
			        	.unit("URI")
			        	.type("digital object format")
			        	.build();
					pMigrationList.add(pMigrationContent);
	    			Event eMigration = new Event(
	    					MIGRATION_EVENT, System.currentTimeMillis() + "", new Double(DURATION), 
	    					new Agent(migrationEndpoint, migrate.NAME, migrate.QNAME.toString()), 
	    					pMigrationList);
					dgoB = addEvent(dgoB, eMigration, null);
					
		         	// add create SIP event
					List<Property> pSipList = new ArrayList<Property>();
					Property pSipContent = new Property.Builder(URI.create("CreateSIP"))
			        	.name("SIP message")
			        	.value(types[0].toString())
			        	.description("This is a SIP message creation")
			        	.unit("file")
			        	.type("ZIP format")
			        	.build();
					pSipList.add(pSipContent);
					Event eSip = new Event(
							SIP_CREATION_EVENT, System.currentTimeMillis() + "", new Double(DURATION), 
							new Agent("ZIP file", "The SIP message creation", "ZIP"), 
							pSipList);
					dgoB = addEvent(dgoB, eSip, null);					
	         	}
         	}
         	
			// Update digital object in JCR repository
            wfResultItem.addLogInfo("STEP 5: Update digital object in JCR repository. initial digital object: " + 
            		dgoB.toString());
         	dgoB = dodm.updateDigitalObject(dgoB, false);
         	wfResultItem.addLogInfo("Completed update in JCR repository. result digital object: " + dgoB.toString());
            
            // Create SIP message (ZIP archive) and send it back to the initial repository
            wfResultItem.addLogInfo(
            		"STEP 6: Send enriched digital object back to the initial repository as SIP. Create ZIP archive.");            
    		List<String> filenames = new ArrayList<String>(); 
			String parentDir = "OriginalEpublication";
			String strHeaderDirectory = "";
			String strContentDirectory = parentDir + "/content";
			createDirectories(strHeaderDirectory);
			createDirectories(strContentDirectory);
         	wfResultItem.addLogInfo("before resHeader");
         	String resHeader = createMetadataXml(strHeaderDirectory, types[0]
         	                                     , migratedFileName, migratedFileSize);
         	if (resHeader != null) {
             	wfResultItem.addLogInfo("resHeader != null: " + resHeader);
         		filenames.add(resHeader);
         	}
         	wfResultItem.addLogInfo("after resHeader");
         	
         	// add file to SIP content
			try {
				String resContent = addFileToZipContent(strContentDirectory, dgoB.getContent().getInputStream());
	         	wfResultItem.addLogInfo("add file to SIP content");
	         	if (resContent != null) {
	         		filenames.add(resContent);
	         	}
			} catch (Exception e) {
				wfResultItem.addLogInfo("read file content error: " + e.getMessage());
			}
         	
         	wfResultItem.addLogInfo("before create SIP archive");
			String sipFileName = createZipArchive(filenames);
         	wfResultItem.addLogInfo("After create SIP archive sipFileName: " + sipFileName);
			String zipFileRes = sipFileName.substring(sipFileName.lastIndexOf(URI_SEPARATOR) + 1);
         	wfResultItem.addLogInfo("After create SIP archive zipFileRes: " + zipFileRes);
			
			wfResultItem.addLogInfo("host auth: "  + this.getHostAuthority());
	    	URI zipOutFolder = new URI("http",this.getHostAuthority(),"/wee-gen/id-"+this.getWorkflowReportingLogger().getResultsId(),null,null);
	    	URL zipFileURL = new URL(zipOutFolder+"/" + zipFileRes);
			wfResultItem.addLogInfo("zipFileURL: "  + zipFileURL);

        	wfResultItem.addLogInfo(new Message("SIP", new Parameter("Files", Arrays.asList(filenames).toString()), new Parameter(
                    "Result", zipFileURL.toString())));
			List<URL> uris = new ArrayList<URL>();
			uris.add(zipFileURL);
			wfResultItem.addLogInfo(this.link(uris));

			deleteTmpDir(new File(parentDir));
         	wfResultItem.addLogInfo("Completed sending of enriched digital object to the initial repository.");
            
            wfResultItem.setEndTime(System.currentTimeMillis());

			wfResultItem
				.addLogInfo("Successfully completed workflow for digitalObject with permanent uri:"
						+ processingDigo);
			wfResultItem.setEndTime(System.currentTimeMillis());

		} catch (Exception e) {
			String err = "workflow execution error for digitalObject with permanent uri: " + processingDigo;
			wfResultItem.addLogInfo(err + " " + e);
			wfResultItem.setEndTime(System.currentTimeMillis());
		}
		
		return this.getWFResult();
	}
	
	
	/**
	 * Create a characterisation event.
	 * @return The created event
	 */
	public Event buildEvent(List<Property> pList) {
		Event eCharacterisation = new Event(
				CHARACTERISATION_EVENT, System.currentTimeMillis() + "", new Double(DURATION), 
				new Agent("http://centos.planets-project.ait.ac.at/pserv-pc-fits/FitsCharacterise?wsdl", 
						characterise.NAME, characterise.QNAME.toString()), 
				pList);
		return eCharacterisation;
	}

	
	/**
	 * This method adds new event to the digital object. 
	 * 
	 * @param digitalObject
	 *        This is a digital object to be updated
	 * @param newEvent
	 *        This is a new event
	 * @param identifiedFormat
	 *        This is a format identified by identification service
	 * @return changed digital object with new event value
	 */
	public static DigitalObject addEvent(DigitalObject digitalObject, Event newEvent, URI identifiedFormat)
    {
		DigitalObject res = null;
		
    	if (digitalObject != null && newEvent != null)
    	{
	    	DigitalObject.Builder b = new DigitalObject.Builder(digitalObject.getContent());
		    if (digitalObject.getTitle() != null) b.title(digitalObject.getTitle());
		    if (digitalObject.getPermanentUri() != null) b.permanentUri(digitalObject.getPermanentUri());
		    if (identifiedFormat != null) {
		    	b.format(identifiedFormat);
		    } else {
		    	if (digitalObject.getFormat() != null) b.format(digitalObject.getFormat());
		    }
		    if (digitalObject.getManifestationOf() != null) 
		    	b.manifestationOf(digitalObject.getManifestationOf());
		    if (digitalObject.getMetadata() != null) 
		    	b.metadata((Metadata[]) digitalObject.getMetadata().toArray(new Metadata[0]));
		    if (digitalObject.getEvents() != null)
		    {
				List<Event> eventList = digitalObject.getEvents();
				eventList.add(newEvent);
		    	b.events((Event[]) eventList.toArray(new Event[0]));
		    }
            res = b.build();
    	}
		return res;
	}
	

	private String addFileToZipContent(String path, InputStream inputStream) {
		String res = null;
        //an object used to document the results of a service call for the WorkflowResult
        //document the service type and start-time
        WorkflowResultItem wfResultItem = new WorkflowResultItem(
        		WorkflowResultItem.SERVICE_ACTION_IDENTIFICATION,
        		System.currentTimeMillis());
    	wfResultItem.addLogInfo("addFileToZipContent");
	    try {
	    	String fileName = path + URI_SEPARATOR + processingDigo.getPermanentUri().toString().substring(
	    			processingDigo.getPermanentUri().toString().lastIndexOf(URI_SEPARATOR) + 1);
	    	wfResultItem.addLogInfo("path: " + path + "fileName: " + fileName);
		    File f = new File(fileName);
		    OutputStream out = new FileOutputStream(f);
		    byte buf[] = new byte[1024];
		    int len;
		    while((len=inputStream.read(buf))>0)
		    out.write(buf,0,len);
		    out.close();
		    inputStream.close();
		    res = fileName;
		    wfResultItem.addLogInfo("\nFile is created........ res: " + res);
	    }
	    catch (Exception e){
	    	wfResultItem.addLogInfo("Content file creation error: " + e.getMessage());
	    }
	    
	    return res;
	}


	/**
	 * This method recursively deletes all files and directories under passed dir. 
	 * It returns true if deletions were successful false otherwise.
	 */ 
	private boolean deleteTmpDir(File dir) { 
        WorkflowResultItem wfResultItem = new WorkflowResultItem(
        		WorkflowResultItem.SERVICE_ACTION_IDENTIFICATION,
        		System.currentTimeMillis());
    	wfResultItem.addLogInfo("deleteTmpDir");
		if (dir.isDirectory()) { 
			String[] children = dir.list(); 
			for (int i=0; i<children.length; i++) { 
				boolean success = deleteTmpDir(new File(dir, children[i])); 
				if (!success) { 
					wfResultItem.addLogInfo("deleteTmpDir error.");
					return false; 
				} 
			} 
		} 
		
		// The directory is now empty so delete it 
		return dir.delete(); 
	} 	

	
	private String createZipArchive(List<String> filenames) {
		String outFilename = "defaultName";
        WorkflowResultItem wfResultItem = new WorkflowResultItem(
        		WorkflowResultItem.SERVICE_ACTION_IDENTIFICATION,
        		System.currentTimeMillis());
    	wfResultItem.addLogInfo("createZipArchive");
		// These are the files to include in the ZIP file 
		// Create a buffer for reading the files 
		byte[] buf = new byte[1024]; 
		try { 
			// Create the ZIP file 
			outFilename = this.getWorkflowReportingLogger().getOutputFolder().getAbsolutePath() +
            	URI_SEPARATOR + SIP_NAME + now(DATE_FORMAT) + "-" + now(TIME_FORMAT) + SIP_FORMAT; 
			wfResultItem.addLogInfo("outFilename: " + outFilename);
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename)); 
			// Compress the files 
			if (filenames != null) {
				for (int idx = 0; idx < filenames.size(); idx++) {
					wfResultItem.addLogInfo("zip i: " + idx + ", filename: " + filenames.get(idx));
					if (filenames.get(idx) != null && filenames.get(idx).length() > 0) {
						FileInputStream in = new FileInputStream(filenames.get(idx)); 
						// Add ZIP entry to output stream. 
						out.putNextEntry(new ZipEntry(filenames.get(idx))); 
						// Transfer bytes from the file to the ZIP file 
						int len; 
						while ((len = in.read(buf)) > 0) { 
//							wfResultItem.addLogInfo("zip while len: " + len);
							out.write(buf, 0, len); 
							} 
						// Complete the entry 
						out.closeEntry(); 
						in.close(); 
						wfResultItem.addLogInfo("zip close entry out, close in.");
					} 
				}
			}
			// Complete the ZIP file 
			out.close(); 
			wfResultItem.addLogInfo("zip close out.");
		} catch (Exception e) {
			wfResultItem.addLogInfo("zip error: " + e.getMessage());
		} 	
		return outFilename;
    }
	
	
	private String now(String dateFormat) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(cal.getTime());
	}

	
	private void createDirectories(String path) {
        WorkflowResultItem wfResultItem = new WorkflowResultItem(
        		WorkflowResultItem.SERVICE_ACTION_IDENTIFICATION,
        		System.currentTimeMillis());
    	wfResultItem.addLogInfo("createDirectories");
		try {	
		    // Create multiple directories
		    boolean success = (new File(path)).mkdirs();
		    if (success) {
		    	wfResultItem.addLogInfo("Directories: " + path + " created");
		    }
	    } catch (Exception e) {
	    	wfResultItem.addLogInfo("createDirectories() Error: " + e.getMessage());
	    }
	}


	/**
	 * This method creates metadata XML file for SIP message.
	 * @param metadataList The list of metadata
	 * @param path The path to place the metadata XML file
	 * @param file type
	 * @return The resulting metadata XML file path
	 */
	private String createMetadataXml(String path, String fileType
			                        , String fileName, String fileSize) {
		String res = path + METADATA_XML;
        WorkflowResultItem wfResultItem = new WorkflowResultItem(
        		WorkflowResultItem.SERVICE_ACTION_IDENTIFICATION,
        		System.currentTimeMillis());
    	wfResultItem.addLogInfo("createMetadataXml");
		try {
	    	wfResultItem.addLogInfo("createMetadataXml" + 
	    			" path: " + path + ", fileType: " + fileType +
	    			", fileName: " + fileName + ", fileSize: " + fileSize);
			FileOutputStream fos = new FileOutputStream(res);
			OutputFormat of = new OutputFormat("XML","UTF-8",true);
			of.setDoctype(null,"http://banks/dtd/KB.DNEP.SIP.dtd");
			XMLSerializer serializer = new XMLSerializer(fos,of);
			// SAX2.0 ContentHandler.
			ContentHandler hd = serializer.asContentHandler();
			hd.startDocument();
			// Metadata attributes.
			AttributesImpl atts = new AttributesImpl();
			// Metadata list tag.
			hd.startElement("","","SIP",atts);
			hd.startElement("","","Asset",atts);
			hd.startElement("","","Epublication",atts);
			hd.startElement("","","OriginalEpublication",atts);
			atts.clear();
			atts.addAttribute("","","RootName","CDATA", "OriginalEpublication");
			hd.startElement("","","DirTree",atts);
			atts.clear();
			atts.addAttribute("","","DirName","CDATA", "content");
			hd.startElement("","","Directory",atts);
			
			atts.clear();
			atts.addAttribute("","","Name","CDATA", fileName);
			atts.addAttribute("","","Size","CDATA", fileSize);
			atts.addAttribute("","","Type","CDATA", fileType);
			hd.startElement("","","File",atts);
			hd.endElement("","","File");
			hd.endElement("","","Directory");
			hd.endElement("","","DirTree");
			hd.endElement("","","OriginalEpublication");
			hd.endElement("","","Epublication");
			hd.endElement("","","Asset");
			
			atts.clear();
			hd.startElement("","","MetadataBlock",atts);
			atts.clear();
			atts.addAttribute("","","supplier","CDATA", "nameOfPublisher");
			atts.addAttribute("","","starterFileName","CDATA", "content/" + fileName);
			atts.addAttribute("","","refPlatformNBN","CDATA", "");
			atts.addAttribute("","","originalNBN","CDATA", "nbn001");
			atts.addAttribute("","","dateOfCreation","CDATA", now(DATE_FORMAT));
			atts.addAttribute("","","NBN","CDATA", "nbn001");
			atts.addAttribute("","","ingestUserID","CDATA", "PLANETS1");
			atts.addAttribute("","","sourceType","CDATA", "CD-ROM");
			atts.addAttribute("","","setupFileName","CDATA", "");
			atts.addAttribute("","","sourceDescription","CDATA", "Test SIP message");
			atts.addAttribute("","","libraryFunctionName","CDATA", "Depot");
			hd.startElement("","","Metadata",atts);
			hd.endElement("","","Metadata");
			hd.endElement("","","MetadataBlock");
			hd.endElement("","","SIP");
			hd.endDocument();
			fos.close();
		} catch (Exception e) {
			wfResultItem.addLogInfo("StorageBackingBean createMetadataXml() error: " + e.getMessage());	
			res = null;
		}
		return res;
	}
	
	
	/**
	 * This method changes the metadata list value in digital object and returns changed
	 * digital object with new metadata list value. 
	 * 
	 * @param digitalObject
	 *        This is a digital object to be updated
	 * @param newMetadata
	 *        This is a new digital object metadata object
	 * @return changed digital object with new metadata list value
	 */
	public static DigitalObject addMetadata(DigitalObject digitalObject, Metadata newMetadata)
    {
		DigitalObject res = null;
		
    	if (digitalObject != null && newMetadata != null)
    	{
	    	DigitalObject.Builder b = new DigitalObject.Builder(digitalObject.getContent());
		    if (digitalObject.getTitle() != null) b.title(digitalObject.getTitle());
		    if (digitalObject.getPermanentUri() != null) b.permanentUri(digitalObject.getPermanentUri());
		    if (digitalObject.getFormat() != null) b.format(digitalObject.getFormat());
		    if (digitalObject.getManifestationOf() != null) 
		    	b.manifestationOf(digitalObject.getManifestationOf());
		    if (digitalObject.getEvents() != null) 
		    	b.events((Event[]) digitalObject.getEvents().toArray(new Event[0]));
		    if (digitalObject.getMetadata() != null)
		    {
				List<Metadata> metadataList = digitalObject.getMetadata();
				metadataList.add(newMetadata);
		    	b.metadata((Metadata[]) metadataList.toArray(new Metadata[0]));
		    }
            res = b.build();
    	}
		return res;
	}
	

	/** {@inheritDoc} */
	public WorkflowResult finalizeExecution() {
		this.getWFResult().setEndTime(System.currentTimeMillis());
		LogReferenceCreatorWrapper.createLogReferences(this);
		return this.getWFResult();
	}

	/**
	 * Runs the migration service on a given digital object reference. It uses the
	 * MigrationWFWrapper to call the service, create workflowResult logs,
	 * events and to persist the object within the specified repository
	 */
	private URI runMigration(Migrate migrationService,
			URI digORef, boolean endOfRoundtripp) throws Exception {

		MigrationWFWrapper migrWrapper = new MigrationWFWrapper(this,
				this.processingDigo.getPermanentUri(), 
				migrationService, 
				digORef, 
				DataRegistryFactory.createDataRegistryIdFromName("/experiment-files/executions/"),
				endOfRoundtripp);
		
		return migrWrapper.runMigration();

	}

    /**
     * This method runs the identification service on a given digital object and returns an
	 * Array of identified id's (for Droid e.g. PronomIDs)
	 * 
	 * @param DigitalObject
	 *            the data
	 * @return
	 * @throws Exception
     */
    private String[] runIdentification(DigitalObject digo) throws Exception {        
        //an object used to document the results of a service call for the WorkflowResult
        //document the service type and start-time
        WorkflowResultItem wfResultItem = new WorkflowResultItem(
        		WorkflowResultItem.SERVICE_ACTION_IDENTIFICATION,
        		System.currentTimeMillis());
    	wfResultItem.addLogInfo("STEP 2: Identification");
        
        //get the parameters that were passed along in the configuration
        List<Parameter> parameterList;
        if(this.getServiceCallConfigs(identify)!=null){
        	parameterList = this.getServiceCallConfigs(identify).getAllPropertiesAsParameters();
        }else{
        	parameterList = new ArrayList<Parameter>();
        }
  
        //now actually execute the identify operation of the service
        IdentifyResult results = identify.identify(digo, parameterList);
        
        //document the end-time and input digital object and the params
        wfResultItem.setEndTime(System.currentTimeMillis());
        wfResultItem.setInputDigitalObjectRef(digo.getPermanentUri());
        wfResultItem.setServiceParameters(parameterList);
        wfResultItem.setServiceEndpoint(identify.describe().getEndpoint());
        
        //have a look at the service's results
        ServiceReport report = results.getReport();
        List<URI> types = results.getTypes();

        //report service status and type
        wfResultItem.setServiceReport(report);

        if (report.getType() == Type.ERROR) {
            String s = "Service execution failed: " + report.getMessage();
            wfResultItem.addLogInfo(s);
            throw new Exception(s);
        }

        if (types.size() < 1) {
            String s = "The specified file type is currently not supported by this workflow";
            wfResultItem.addLogInfo(s);
            throw new Exception(s);
        }

        String[] strings = new String[types.size()];
        int count = 0;
        for (URI uri : types) {
            strings[count] = uri.toASCIIString();
            //document the result
            wfResultItem.addExtractedInformation(strings[count]);
            count++;
        }

        return strings;
    }

    
    /**
     * @param digital object
     */
    private List<Property> runCharacterization(DigitalObject digitalObject) {
        WorkflowResultItem wfResultItem = new WorkflowResultItem(
        		WorkflowResultItem.SERVICE_ACTION_CHARACTERISATION,
        		System.currentTimeMillis());
    	wfResultItem.addLogInfo("STEP 1: Characterisation");
        CharacteriseResult characteriseResult = characterise.characterise(
                digitalObject, null);
     	wfResultItem.addLogInfo("Characterise name: " + characterise.NAME + ", qname: " + characterise.QNAME);
        List<Property> properties = characteriseResult.getProperties();
        wfResultItem.addLogInfo("Characterised as: " + properties);
        wfResultItem.addLogInfo("Characterise endpoint: " + characterise.describe().getEndpoint());
        return properties;
    }


}
