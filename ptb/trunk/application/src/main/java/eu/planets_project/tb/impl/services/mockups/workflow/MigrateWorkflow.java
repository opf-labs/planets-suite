/**
 * 
 */
package eu.planets_project.tb.impl.services.mockups.workflow;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.techreg.formats.Format;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.ifr.core.techreg.formats.Format.UriType;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.gui.backing.exp.ExperimentStageBean;
import eu.planets_project.tb.impl.model.eval.MeasurementImpl;
import eu.planets_project.tb.impl.model.eval.mockup.TecRegMockup;
import eu.planets_project.tb.impl.model.exec.ExecutionStageRecordImpl;
import eu.planets_project.tb.impl.model.exec.MeasurementRecordImpl;
import eu.planets_project.tb.impl.services.wrappers.CharacteriseWrapper;
import eu.planets_project.tb.impl.services.wrappers.IdentifyWrapper;
import eu.planets_project.tb.impl.services.wrappers.MigrateWrapper;

/**
 * This is the class that carries the code specific to invoking an Migrate experiment.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class MigrateWorkflow implements ExperimentWorkflow {
    private static Log log = LogFactory.getLog(MigrateWorkflow.class);

    /** External property keys */
    public static final String PARAM_SERVICE = "migrate.service";
    public static final String PARAM_FROM = "migrate.from";
    public static final String PARAM_TO = "migrate.to";
    public static final String PARAM_PRE_SERVICE = "migrate.pre.service";
    public static final String PARAM_PRE_SERVICE_TYPE = "migrate.pre.service.type";
    public static final String PARAM_POST_SERVICE = "migrate.post.service";
    public static final String PARAM_POST_SERVICE_TYPE = "migrate.post.service.type";
    public static final String SERVICE_TYPE_CHARACTERISE = "Characterise";
    public static final String SERVICE_TYPE_IDENTIFY = "Identify";

    /** Internal keys for easy referral to the service+stage combinations. */
    public static final String STAGE_PRE_MIGRATE = "Characterise Before Migration";
    public static final String STAGE_MIGRATE = "Migrate";
    public static final String STAGE_POST_MIGRATE = "Characterise After Migration";
    
    private static HashMap<String,List<MeasurementImpl>> manualObservables;
    /** Statically define the automatically observable properties. */
    private static HashMap<String,List<MeasurementImpl>> observables;
    static {
        observables = new HashMap<String,List<MeasurementImpl>>();
        observables.put(STAGE_MIGRATE, new Vector<MeasurementImpl>() );
        // The service succeeded
        observables.get(STAGE_MIGRATE).add(
                TecRegMockup.getObservable(TecRegMockup.PROP_SERVICE_SUCCESS) );
        //FIXME What about the parameter: MIGRATE_SUCCESS, choosing enabled-ness...
        // The service time
        observables.get(STAGE_MIGRATE).add(
                TecRegMockup.getObservable(TecRegMockup.PROP_SERVICE_TIME) );
        /*
        observables.put( MIGRATE_SERVICE_TIME, 
                TecRegMockup.getObservable(TecRegMockup.PROP_SERVICE_TIME, STAGE_MIGRATE) );
         */
        
        manualObservables = new HashMap<String,List<MeasurementImpl>>();
        manualObservables.put(STAGE_MIGRATE, new Vector<MeasurementImpl>() );
    }

    /* ------------------------------------------------------------- */

    /** Parameters for the workflow execution etc */
    HashMap<String, String> parameters = new HashMap<String,String>();
    /** The holder for the identifier service. */
    Migrate migrator = null;
    URL migratorEndpoint = null;

    /* ------------------------------------------------------------- */
    
    Characterise dpPre = null;
    Characterise dpPost = null;
    Identify idPre = null;
    Identify idPost = null;
    
    /* ------------------------------------------------------------- */
    
    private static final FormatRegistry format = FormatRegistryFactory.getFormatRegistry();

    /* (non-Javadoc)
     * @see eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow#getStages()
     */
    public List<ExperimentStageBean> getStages() {
        List<ExperimentStageBean> stages = new Vector<ExperimentStageBean>();
        stages.add( new ExperimentStageBean(STAGE_PRE_MIGRATE, "Characterise before migration."));
        stages.add( new ExperimentStageBean(STAGE_MIGRATE, "Migrate the digital object."));
        stages.add( new ExperimentStageBean(STAGE_POST_MIGRATE, "Characterise after migration."));
        return stages;
    }
    
    /* (non-Javadoc)
     * @see eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow#getManualObservables()
     */
    public HashMap<String,List<MeasurementImpl>> getManualObservables() {
    	return manualObservables;
    }
    
    /* (non-Javadoc)
     * @see eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow#getObservables()
     */
    public HashMap<String,List<MeasurementImpl>> getObservables() {
        // Copy and augment with properties for the format type:
        HashMap<String,List<MeasurementImpl>> obs = new HashMap<String,List<MeasurementImpl>>();
        for( String stage : observables.keySet() ) {
            obs.put(stage, new Vector<MeasurementImpl>() );
            for( MeasurementImpl m : observables.get(stage) ) {
                obs.get(stage).add(m);
            }
        }

        /* --------------------------------------------------------------------- */
        
        // Pre-characterise:
        if( obs.get(STAGE_PRE_MIGRATE) == null ) 
            obs.put(STAGE_PRE_MIGRATE, new Vector<MeasurementImpl>() );
        // For Characterise:
        if( this.preIsCharacterise() ) {
            for( MeasurementImpl m : this.getMeasurementsForInFormat(this.getFromFormat()) ) {
                obs.get(STAGE_PRE_MIGRATE).add(m);
            }
        }
        // For Identify:
        if( this.preIsIdentify() ) {
            obs.get(STAGE_PRE_MIGRATE).add(IdentifyWorkflow.MEASURE_IDENTIFY_FORMAT);
            obs.get(STAGE_PRE_MIGRATE).add(IdentifyWorkflow.MEASURE_IDENTIFY_METHOD);
            obs.get(STAGE_PRE_MIGRATE).add(TecRegMockup.getObservable(TecRegMockup.PROP_DO_SIZE));
        }
        // In general:
        if( this.preIsDefined() ) {
            // Add basic properties.
            obs.get(STAGE_PRE_MIGRATE).add( 
                TecRegMockup.getObservable(TecRegMockup.PROP_SERVICE_SUCCESS) );
            obs.get(STAGE_PRE_MIGRATE).add( 
                TecRegMockup.getObservable(TecRegMockup.PROP_SERVICE_TIME) );
        }
        
        /* --------------------------------------------------------------------- */
        
        // Post-characterise:
        if( obs.get(STAGE_POST_MIGRATE) == null ) 
            obs.put(STAGE_POST_MIGRATE, new Vector<MeasurementImpl>() );
        // For Characterise:
        if( this.postIsCharacterise() ) {
            for( MeasurementImpl m : this.getMeasurementsForOutFormat(this.getToFormat()) ) {
                obs.get(STAGE_POST_MIGRATE).add(m);
            }
        }
        // For Identify:
        if( this.postIsIdentify() ) {
            obs.get(STAGE_POST_MIGRATE).add(IdentifyWorkflow.MEASURE_IDENTIFY_FORMAT);
            obs.get(STAGE_POST_MIGRATE).add(IdentifyWorkflow.MEASURE_IDENTIFY_METHOD);
            obs.get(STAGE_POST_MIGRATE).add(TecRegMockup.getObservable(TecRegMockup.PROP_DO_SIZE));
        }
        // In general:
        if( this.postIsDefined() ) {
            // Add basic properties.
            obs.get(STAGE_POST_MIGRATE).add( 
                TecRegMockup.getObservable(TecRegMockup.PROP_SERVICE_SUCCESS) );
            obs.get(STAGE_POST_MIGRATE).add( 
                TecRegMockup.getObservable(TecRegMockup.PROP_SERVICE_TIME) );
        }
        
        return obs;
    }
        
    private String cacheInFormat = "";
    private List<MeasurementImpl> cacheInProps = null;
    private String cacheOutFormat = "";
    private List<MeasurementImpl> cacheOutProps = null;
    
    private List<MeasurementImpl> getMeasurementsForInFormat(String format) {
        if( format == null ) return new Vector<MeasurementImpl>();
        if( ! format.equals(cacheInFormat) || cacheInProps == null ) {
            cacheInProps = this.getMeasurementsForFormat( format, dpPre );
            cacheInFormat = format;
        }
        return cacheInProps;
    }

    private List<MeasurementImpl> getMeasurementsForOutFormat(String format) {
        if( format == null ) return  new Vector<MeasurementImpl>();
        if( ! format.equals(cacheOutFormat) || cacheOutProps == null ) {
            cacheOutProps = this.getMeasurementsForFormat( format, dpPost );
            cacheOutFormat = format;
        }
        return cacheOutProps;
    }
    
    /**
     * Creates a list of MeasurementImpl for the requested format and Characterise service.
     * Properties are requested from the service's .listProperties(puid) method. 
     * @param format
     * @param dp
     * @return
     */
    private List<MeasurementImpl> getMeasurementsForFormat( String format, Characterise dp ) {
        List<MeasurementImpl> lm = new Vector<MeasurementImpl>();
        
        HashMap<URI,MeasurementImpl> meas = new HashMap<URI,MeasurementImpl>();
        URI formatURI;
        if( format == null ) {
            log.error("Format was set to NULL.");
            return lm;
        }
        try {
            formatURI = new URI(format);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return lm;
        }
        if( dp == null ) {
            return lm;
        }
        // Find all the PRONOM IDs for this format URI:
        for( URI puid : this.getPronomURIAliases(formatURI) ) {
            List<Property> measurableProperties = dp.listProperties(puid);
            if( measurableProperties != null ) {
                for( Property p : measurableProperties ) {
                    MeasurementImpl m = this.createMeasurementFromProperty(p);
                    if( ! meas.containsKey( m.getIdentifier() ) ) {
                        meas.put(m.getIdentifier(), m);
                    }
                }
            }
        }

        lm = new Vector<MeasurementImpl>(meas.values());
        //Collections.sort( lm );
        return lm;
    }
    
    
    /**
     * Takes a Property that's used in Planets level-one service call results
     * and converts it into the Testbed's Property model element: MeasurementImpl
     * @param p eu.planets_project.services.datatypes.Property
     * @return
     */
    private MeasurementImpl createMeasurementFromProperty( Property p ) {
        MeasurementImpl m = new MeasurementImpl();
        
        if( p == null ) return m;
        
        URI propURI = p.getUri();
        // Invent a uri if required:
        if( propURI == null ) {
            try {
                propURI = new URI( TecRegMockup.URIXCDLPropertyRoot + p.getName());
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return m;
            }
        }
        
        // Copy in:
        m.setName(p.getName());
        m.setIdentifier(propURI);
        m.setDescription(p.getDescription());
        // FIXME The TYPES have different meanings here! What should this be recorded as?
        //m.setType(p.getType());
        m.setType(MeasurementImpl.TYPE_DIGITALOBJECT);
        m.setUnit(p.getUnit());
        m.setValue(p.getValue());
        
        return m;
    }
    
    
    private List<URI> getPronomURIAliases(URI typeURI) {
        Set<URI> turis = new HashSet<URI>();
        
        Format fmt = format.getFormatForUri(typeURI);
        if( format.isUriOfType(typeURI,UriType.MIME) ) {
            Set<URI> furis = ServiceBrowser.fr.getUrisForMimeType(fmt.getMimeTypes().iterator().next());
            turis.addAll(furis);
        } else if( format.isUriOfType(typeURI, UriType.EXTENSION)) {
            Set<URI> furis = ServiceBrowser.fr.getUrisForExtension(fmt.getExtensions().iterator().next());
            turis.addAll(furis);
        } else {
            // Aliases:
            for( URI uri : fmt.getAliases() ) {
                turis.add(uri);
            }
        }
        return new ArrayList<URI>(turis);
    }
    

    /* (non-Javadoc)
     * @see eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow#setParameters(java.util.HashMap)
     */
    public void setParameters(HashMap<String, String> parameters)
    throws Exception {
        this.parameters = parameters;
        // Attempt to connect to the Migrate service.
        try {
            migratorEndpoint = new URL(this.parameters.get(PARAM_SERVICE));
            migrator = new MigrateWrapper( migratorEndpoint );
        } catch( MalformedURLException e ) {
            throw new Exception("You did not specify a valid migration service URL!");
        }

        // Also set the pre services:
        try {
            if( this.preIsCharacterise() ) {
                dpPre = new CharacteriseWrapper(new URL(this.parameters.get(PARAM_PRE_SERVICE)) );
            } else {
                dpPre = null;
            }
            if( this.preIsIdentify() ) {
                idPre = new IdentifyWrapper( new URL(this.parameters.get(PARAM_PRE_SERVICE)) );

            } else {
                idPre = null;
            }
        } catch( MalformedURLException e ) {
            throw new Exception("You did not specify a valid pre-migration service URL!");
        }

        // Also set the post services:
        try {
            if( this.postIsCharacterise() ) {
                dpPost = new CharacteriseWrapper(new URL(this.parameters.get(PARAM_POST_SERVICE)) );
            } else {
                dpPost = null;
            }
            if( this.postIsIdentify() ) {
                idPost = new IdentifyWrapper( new URL(this.parameters.get(PARAM_POST_SERVICE)) );

            } else {
                idPost = null;
            }
        } catch( MalformedURLException e ) {
            throw new Exception("You did not specify a valid post-migration service URL!");
        }

        // FIXME Also create/record a ServiceRecordImpl? 

        // MUST throw an Exception if the input and outputs are not defined!
        if( this.getFromFormat() == null || "".equals(this.getFromFormat()) ||
                this.getToFormat() == null || "".equals(this.getToFormat()) ) {
            throw new Exception("You must specify both the input and output format!");
        }
    }
    
    public HashMap<String, String> getParameters(){
    	return this.parameters;
    }
    
    private boolean preIsCharacterise() {
        if( ! this.preIsDefined() ) return false;
        return this.parameters.get(PARAM_PRE_SERVICE_TYPE).equals(SERVICE_TYPE_CHARACTERISE);
    }
    
    private boolean postIsCharacterise() {
        if( ! this.postIsDefined() ) return false;
        return this.parameters.get(PARAM_POST_SERVICE_TYPE).equals(SERVICE_TYPE_CHARACTERISE);
    }
    
    private boolean preIsIdentify() {
        if( ! this.preIsDefined() ) return false;
        return this.parameters.get(PARAM_PRE_SERVICE_TYPE).equals(SERVICE_TYPE_IDENTIFY);
    }
    
    private boolean postIsIdentify() {
        if( ! this.postIsDefined() ) return false;
        return this.parameters.get(PARAM_POST_SERVICE_TYPE).equals(SERVICE_TYPE_IDENTIFY);
    }
    
    private boolean preIsDefined() {
        if( this.parameters.get(PARAM_PRE_SERVICE_TYPE) == null ||
                "".equals( this.parameters.get(PARAM_PRE_SERVICE_TYPE) ) ) return false;
        return true;
    }
    
    private boolean postIsDefined() {
        if( this.parameters.get(PARAM_POST_SERVICE_TYPE) == null ||
                "".equals( this.parameters.get(PARAM_POST_SERVICE_TYPE) ) ) return false;
        return true;
    }
    
    private String getFromFormat() {
        log.info("getFromFormat: "+this.parameters.get(PARAM_FROM));
        return this.parameters.get(PARAM_FROM);
    }
    
    private String getToFormat() {
        log.info("getToFormat: "+this.parameters.get(PARAM_TO));
        return this.parameters.get(PARAM_TO);        
    }
    
    /* ---------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------- */

    /* (non-Javadoc)
     * @see eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow#execute(eu.planets_project.services.datatypes.DigitalObject, java.util.HashMap)
     */
    public WorkflowResult execute( DigitalObject dob ) {
        // Initialise the result:
        WorkflowResult wr = new WorkflowResult();

        // Pre-migrate characterise
        ExecutionStageRecordImpl preStage = new ExecutionStageRecordImpl(STAGE_PRE_MIGRATE);
        try {
            wr.getStages().add( preStage );
            if( this.preIsCharacterise() ) {
                executeCharacteriseStage(wr, dob, preStage, dpPre );
            }
            if( this.preIsIdentify()) {
                executeIdentifyStage(wr, dob, preStage, idPre );
            }
        } catch (Exception e ) {
            log.error("Pre-migrate stage failed! "+e);
            e.printStackTrace();
        }
         
        // Migrate Stage:
        ExecutionStageRecordImpl migrateStage = new ExecutionStageRecordImpl(STAGE_MIGRATE);
        try {
            wr.getStages().add( migrateStage );
            executeMigrateStage(wr, migrateStage, dob);
        } catch (Exception e ) {
            // Create a ServiceReport from the exception.
            // URGENT can we distinguish tool and install error here?
            ServiceReport sr = new ServiceReport(Type.ERROR, Status.TOOL_ERROR, e.toString());
            wr.setReport(sr);
            log.error("Migration failed! "+e);
            e.printStackTrace();
            return wr;
        }

        // Post-migrate characterise
        ExecutionStageRecordImpl postStage = new ExecutionStageRecordImpl(STAGE_POST_MIGRATE);
        try {
            wr.getStages().add( postStage );
            if( this.postIsCharacterise() ) {
                executeCharacteriseStage(wr, (DigitalObject)wr.getResult(), postStage, dpPost );
            }
            if( this.postIsIdentify()) {
                executeIdentifyStage(wr, (DigitalObject)wr.getResult(), postStage, idPost );
            }
        } catch (Exception e ) {
            log.error("Post-Migrate stage failed! "+e);
            e.printStackTrace();
        }
        
        return wr;
    }

    /**
     * The actual Migration stage.
     * 
     * @param wr
     * @param migrateStage 
     * @param dob
     * @throws Exception
     */
    private void executeMigrateStage( WorkflowResult wr, ExecutionStageRecordImpl migrateStage, DigitalObject dob ) throws Exception {
        // Now prepare the result:
        List<MeasurementRecordImpl> stage_m = migrateStage.getMeasurements();
        
        // Record the endpoint of the service used for this stage.
        migrateStage.setEndpoint(migratorEndpoint);
        
        // Invoke the service, timing it along the way:
        boolean success = true;
        MigrateResult migrated = null;
        long msBefore = 0, msAfter = 0;
        URI from = null, to = null;
        msBefore = System.currentTimeMillis();
        try {
            log.info("Migrating "+dob);
            from = new URI(getFromFormat());
            to = new URI(getToFormat());
            migrated = migrator.migrate(dob, from, to, null);
        } catch( Exception e ) {
            success = false;
            e.printStackTrace();
            throw new Exception ("Service Invocation Failed! : " + e );
        }
        msAfter = System.currentTimeMillis();
        
        // Compute the run time.
        stage_m.add(new MeasurementRecordImpl(TecRegMockup.PROP_SERVICE_TIME, ""+((msAfter-msBefore)/1000.0)) );

        // Now record
        if( success && migrated.getDigitalObject() != null ) {

            stage_m.add( new MeasurementRecordImpl( TecRegMockup.PROP_SERVICE_SUCCESS, "true"));

            // Take the digital object, put it in a temp file, and give it a sensible name, using the new format extension.
            File doTmp = File.createTempFile("migrateResult", ".tmp");
            doTmp.deleteOnExit();
            FileUtils.writeInputStreamToFile(migrated.getDigitalObject().getContent().read(), doTmp);
            DigitalObject.Builder newdob = new DigitalObject.Builder(migrated.getDigitalObject());
            newdob.content( Content.byReference(doTmp) );
            // FIXME The above need to be a full recursive storage operation!
            
            if( to != null ) {
                //Format f = new Format(to);
            	Set<String> extensionsTo = ServiceBrowser.fr.getExtensions(to);
                String title = dob.getTitle();
                if(extensionsTo.iterator().hasNext()){
                	title += "."+extensionsTo.iterator().next();
                }
                title = title.substring( title.lastIndexOf("/") + 1);
                newdob.title( title );
            }
            wr.setResult(newdob.build());
            wr.setResultType(WorkflowResult.RESULT_DIGITAL_OBJECT);
            wr.setReport(migrated.getReport());
            log.info("Migration succeeded.");
            return;
        }
        
        // Only get to here if there was not a valid result.
        
        // Build in a 'service failed' property, i.e. the call worked, but no result.
        stage_m.add( new MeasurementRecordImpl( TecRegMockup.PROP_SERVICE_SUCCESS, "false"));

        // FIXME Really, need to be able to ADD a report, so the full set is known.
        wr.setReport(migrated.getReport());
        
        // FIXME Should now throw an Exception, as the WF cannot proceed?
        throw new Exception("Migration failed.  No Digital Object was created. "+migrated.getReport().getMessage());
        
        // FIXME Add a 'toString' on the report that makes a text summary of the whole state?
        
    }
    
    /**
     * 
     * @param wr
     * @param dob
     * @param stage
     * @throws Exception
     */
    private void executeCharacteriseStage( WorkflowResult wr, DigitalObject dob, ExecutionStageRecordImpl  stage, Characterise dp ) throws Exception {
        // Now prepare the result:
        List<MeasurementRecordImpl> stage_m = stage.getMeasurements();
        
        // Invoke the service, timing it along the way:
        boolean success = true;
        CharacteriseResult result = null;
        long msBefore = 0, msAfter = 0;
        msBefore = System.currentTimeMillis();
        try {
            log.info("Characterising "+dob);
            //FIXME this is a hack for disabling norm data for XCDL characterisation services 
            // as parameters are currently not definable for this expType
            List<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("disableNormDataInXCDL","-n"));
            result = dp.characterise(dob, parameterList);
        } catch( Exception e ) {
            log.error("Characterisation failed with exception: "+e);
            e.printStackTrace();
            success = false;
        }
        msAfter = System.currentTimeMillis();
        if( success ) {
            log.info("Characterisation succeeded: "+result);
            if( result != null ) {
                // URGENT Formalise and refactor this logic.
                log.info("Service Report: "+result.getReport().getMessage());
                log.info("Service Report: "+result.getReport().getStatus());
                log.info("Service Report: "+result.getReport().getType());
                if( result.getReport().getStatus() == Status.INSTALLATION_ERROR 
                        || result.getReport().getStatus() == Status.TOOL_ERROR ) {
                    success = false;
                }
            }
        }
        
        // Compute the run time.
        stage_m.add(new MeasurementRecordImpl(TecRegMockup.PROP_SERVICE_TIME, ""+((msAfter-msBefore)/1000.0)) );

        // Record results:
        if( success ) {
            stage_m.add( new MeasurementRecordImpl( TecRegMockup.PROP_SERVICE_SUCCESS, "true"));
            if( result != null && result.getProperties() != null ) {
                log.info("Got "+result.getProperties().size()+" properties");
                for( Property p : result.getProperties() ) {
                    log.info("Recording measurement: "+p.getUri()+":"+p.getName()+" = "+p.getValue());
                    stage_m.add(new MeasurementRecordImpl( p.getUri(), p.getValue() ));
                }
            }
            return;
        }

        // FAILED:
        stage_m.add( new MeasurementRecordImpl( TecRegMockup.PROP_SERVICE_SUCCESS, "false"));

    }

    /**
     * FIXME Code duplication between this and the actual IdentifyWorkflow.  Can we clean up more?
     * 
     * @param wr
     * @param result
     * @param stagePostMigrate
     * @param idPost2
     */
    private void executeIdentifyStage(WorkflowResult wr, DigitalObject dob,
            ExecutionStageRecordImpl stage, Identify identify) throws Exception {
        // Now prepare the result:
        List<MeasurementRecordImpl> stage_m = stage.getMeasurements();
        
        // Invoke the service, timing it along the way:
        boolean success = true;
        IdentifyResult result = null;
        long msBefore = 0, msAfter = 0;
        msBefore = System.currentTimeMillis();
        try {
            log.info("Identifying "+dob);
            result = identify.identify(dob,null);
        } catch( Exception e ) {
            log.error("Identification failed with exception: "+e);
            e.printStackTrace();
            success = false;
        }
        msAfter = System.currentTimeMillis();
        if( success ) {
            log.info("Identification succeeded: "+result);
            if( result != null ) {
                // URGENT Formalise and refactor this logic.
                log.info("Service Report: "+result.getReport().getMessage());
                log.info("Service Report: "+result.getReport().getStatus());
                log.info("Service Report: "+result.getReport().getType());
                if( result.getReport().getStatus() == Status.INSTALLATION_ERROR 
                        || result.getReport().getStatus() == Status.TOOL_ERROR ) {
                    success = false;
                }
            }
        }
        
        // Compute the run time.
        stage_m.add(new MeasurementRecordImpl(TecRegMockup.PROP_SERVICE_TIME, ""+((msAfter-msBefore)/1000.0)) );
        
        // Record results:
        if( success ) {
            try {
                stage_m.add( new MeasurementRecordImpl( TecRegMockup.PROP_SERVICE_SUCCESS, "true"));
                log.info("Start with Measurements #"+stage_m.size());
                IdentifyWorkflow.collectIdentifyResults(stage_m, result, dob);
                log.info("Afterwards, Measurements #"+stage_m.size());
            return;
            } catch ( Exception e ) {
                log.error("Failed to record identification results: "+e);
            }
        }

        // FAILED:
        stage_m.add( new MeasurementRecordImpl( TecRegMockup.PROP_SERVICE_SUCCESS, "false"));

    }


}
