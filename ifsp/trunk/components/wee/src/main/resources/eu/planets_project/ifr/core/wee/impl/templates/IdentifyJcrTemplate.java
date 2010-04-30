package eu.planets_project.ifr.core.wee.impl.templates;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import eu.planets_project.ifr.core.storage.impl.jcr.JcrDigitalObjectManagerImpl;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResult;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowResultItem;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplateHelper;
import eu.planets_project.ifr.core.wee.api.workflow.jobwrappers.LogReferenceCreatorWrapper;
import eu.planets_project.services.datatypes.Agent;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Event;
import eu.planets_project.services.datatypes.Metadata;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;


/**
 * @author <a href="mailto:roman.graf@ait.ac.at">Roman Graf</a>
 */
public class IdentifyJcrTemplate extends
		WorkflowTemplateHelper implements WorkflowTemplate {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** URI to use for digital object repository creation. */
	private static final URI PERMANENT_URI_PATH = URI.create("/ait/data/exp");

	private static final String FORMAT_EVENT_TYPE = "JCRupdate";
	private static final String IDENTIFY_EVENT = "planets://repository/event/identify";
	
    /**
     * Identify service to execute
     */
    private Identify identify;

	private DigitalObject processingDigo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate#describe()
	 */
	public String describe() {
		return "This template performs the identification and storing steps " +
				"of the Testbed's experiment. It implements insert in JCR repository.";
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
            // Identification service for data enrichment (e.g. mime type of output object)
            String[] types = runIdentification(dgoA);
            wfResultItem.addLogInfo("Completed identification. result" + Arrays.asList(types).toString());

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
            	
			// Insert in JCR repository
            wfResultItem.addLogInfo("STEP 2: Insert in JCR repository. initial digital object: " + dgoA.toString());
      	    // Manage the Digital Object Data Registry:
            wfResultItem.addLogInfo("Initialize JCR repository instance.");
            JcrDigitalObjectManagerImpl dodm = 
            	 (JcrDigitalObjectManagerImpl) JcrDigitalObjectManagerImpl.getInstance();
      	    DigitalObject dgoB = dodm.store(PERMANENT_URI_PATH, dgoA, true);
         	wfResultItem.addLogInfo("Completed storing in JCR repository: " + dgoB.toString());
            
         	// Enrich digital object with format information from identification service
         	if (types != null) {
         		wfResultItem.addLogInfo("Identified formats count: " + types.length);
				for (int i=0; i<types.length; i++) {
					wfResultItem.addLogInfo("type[" + i + "]: " + types[i]);
				}			

				if (types[0] != null) {
	    			Event eIdentifyFormat = buildEvent(URI.create(types[0].toString()));
					dgoB = addEvent(dgoB, eIdentifyFormat, URI.create(types[0]));
	         	}
         	}

			// Update digital object in JCR repository
            wfResultItem.addLogInfo("STEP 3: Update digital object in JCR repository. initial digital object: " + 
            		dgoB.toString());
         	dgoB = dodm.updateDigitalObject(dgoB, false);
         	wfResultItem.addLogInfo("Completed update in JCR repository. result digital object: " + dgoB.toString());

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
	 * Create an identification event.
	 * @return The created event
	 */
	public Event buildEvent(URI format){
		List<Property> pList = new ArrayList<Property>();
		Property pIdentificationContent = new Property.Builder(URI.create(FORMAT_EVENT_TYPE))
        	.name("content by reference")
        	.value(format.toString())
        	.description("This is a format fo initial document identified by identification service")
        	.unit("URI")
        	.type("digital object format")
        	.build();
		pList.add(pIdentificationContent);
		Event eIdentifyFormat = new Event(
				IDENTIFY_EVENT, System.currentTimeMillis() + "", new Double(100), 
				new Agent("JCR Repository v1.0", "The Planets Jackrabbit Content Repository", "planets://data/repository"), 
				pList);
		return eIdentifyFormat;
	}

	
	/**
	 * This method changes the content value in digital object and returns changed
	 * digital object with new content value. 
	 * 
	 * @param digitalObject
	 *        This is a digital object to be updated
	 * @param newContent
	 *        This is a new digital object content
	 * @param identifiedFormat
	 *        This is a format identified by identification service
	 * @return changed digital object with new content value
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
	

	/** {@inheritDoc} */
	public WorkflowResult finalizeExecution() {
		this.getWFResult().setEndTime(System.currentTimeMillis());
		LogReferenceCreatorWrapper.createLogReferences(this);
		return this.getWFResult();
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
    	wfResultItem.addLogInfo("STEP 1: Identification");
        
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

    

}
