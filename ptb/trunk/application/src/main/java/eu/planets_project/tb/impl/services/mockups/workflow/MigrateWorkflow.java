/**
 * 
 */
package eu.planets_project.tb.impl.services.mockups.workflow;

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

import eu.planets_project.ifr.core.techreg.api.formats.Format;
import eu.planets_project.services.characterise.DetermineProperties;
import eu.planets_project.services.characterise.DeterminePropertiesResult;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Properties;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.gui.backing.exp.ExperimentStageBean;
import eu.planets_project.tb.impl.model.eval.MeasurementImpl;
import eu.planets_project.tb.impl.model.eval.mockup.TecRegMockup;
import eu.planets_project.tb.impl.model.exec.MeasurementRecordImpl;
import eu.planets_project.tb.impl.model.exec.ServiceRecordImpl;
import eu.planets_project.tb.impl.services.wrappers.MigrateWrapper;
import eu.planets_project.tb.utils.XCDLService;

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
    private static final String STAGE_MIGRATE = "Migrate";
    private static final String STAGE_PRE_MIGRATE = "Pre-Migrate";
    private static final String STAGE_POST_MIGRATE = "Post-Migrate";
    
    private static final String MIGRATE_SUCCESS = STAGE_MIGRATE+".service.success";
    private static final String MIGRATE_SERVICE_TIME = STAGE_MIGRATE+".service.time";

    /** Statically define the observable properties. */
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
    }

    /* ------------------------------------------------------------- */

    /** Parameters for the workflow execution etc */
    HashMap<String, String> parameters = new HashMap<String,String>();
    /** The holder for the identifier service. */
    Migrate migrator = null;

    /* ------------------------------------------------------------- */
    
    DetermineProperties dpPre = null;
    DetermineProperties dpPost = null;
    
    /* ------------------------------------------------------------- */

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
        if( format == null ) return null;
        if( ! format.equals(cacheInFormat) || cacheInProps == null ) {
            cacheInProps = this.getMeasurementsForFormat( format, dpPre );
            cacheInFormat = format;
        }
        return cacheInProps;
    }

    private List<MeasurementImpl> getMeasurementsForOutFormat(String format) {
        if( format == null ) return null;
        if( ! format.equals(cacheOutFormat) || cacheOutProps == null ) {
            cacheOutProps = this.getMeasurementsForFormat( format, dpPost );
            cacheOutFormat = format;
        }
        return cacheOutProps;
    }
    
    private List<MeasurementImpl> getMeasurementsForFormat( String format, DetermineProperties dp ) {
        
        HashMap<URI,MeasurementImpl> meas = new HashMap<URI,MeasurementImpl>();
        URI formatURI;
        if( format == null ) {
            log.error("Format was set to NULL.");
            return null;
        }
        try {
            formatURI = new URI(format);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
        // Find all the PRONOM IDs for this format URI:
        for( URI puid : this.getPronomURIAliases(formatURI) ) {
            Properties measurableProperties = dp.getMeasurableProperties(puid);
            if( measurableProperties != null ) {
                for( Property p : measurableProperties.getProperties()) {
                    MeasurementImpl m = this.createMeasurementFromProperty(p);
                    if( ! meas.containsKey( m.getIdentifier() ) ) {
                        meas.put(m.getIdentifier(), m);
                    }
                }
            }
        }

        List<MeasurementImpl> lm = new Vector<MeasurementImpl>(meas.values());
        //Collections.sort( lm );
        return lm;
    }
    
    private MeasurementImpl createMeasurementFromProperty( Property p ) {
        MeasurementImpl m = new MeasurementImpl();
        
        if( p == null ) return m;
        
        URI propURI;
        try {
            propURI = new URI( TecRegMockup.URIXCDLPropertyRoot + p.getName());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return m;
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
        
        if( Format.isThisAMimeURI(typeURI) ) {
            Format mime = new Format(typeURI);
            Set<URI> furis = ServiceBrowser.fr.getURIsForMimeType(mime.getMimeTypes().iterator().next());
            turis.addAll(furis);
        } else if( Format.isThisAnExtensionURI(typeURI)) {
            Format ext = new Format(typeURI);
            Set<URI> furis = ServiceBrowser.fr.getURIsForExtension(ext.getExtensions().iterator().next());
            turis.addAll(furis);
        } else {
            // This is a known format, ID, so add it, any aliases, and the ext and mime forms:
            Format f = ServiceBrowser.fr.getFormatForURI(typeURI);
            // Aliases:
            for( URI uri : f.getAliases() ) {
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
        migrator = new MigrateWrapper( new URL(this.parameters.get(PARAM_SERVICE)) );
        // FIXME Also set the pre services:
        if( this.preIsCharacterise() ) {
            dpPre = new XCDLService(new URL(this.parameters.get(PARAM_PRE_SERVICE)) );
        } else {
            dpPre = null;
        }
        // FIXME Also set the post services:
        if( this.postIsCharacterise() ) {
            dpPost = new XCDLService(new URL(this.parameters.get(PARAM_POST_SERVICE)) );
        } else {
            dpPost = null;
        }
        
        // FIXME Also create/record a ServiceRecordImpl? 
        
    }
    
    private boolean preIsCharacterise() {
        return this.parameters.get(PARAM_PRE_SERVICE_TYPE).equals(SERVICE_TYPE_CHARACTERISE);
    }
    
    private boolean postIsCharacterise() {
        return this.parameters.get(PARAM_POST_SERVICE_TYPE).equals(SERVICE_TYPE_CHARACTERISE);
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
        return this.parameters.get(PARAM_FROM);
    }
    
    private String getToFormat() {
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

        // Attempt to ru each stage:
        try {
            // Pre-migrate characterise
            // FIXME This could also be an ID instead.
            if( this.preIsCharacterise() ) {
                executeCharacteriseStage(wr, dob, STAGE_PRE_MIGRATE, dpPre );
            }
            
            // Migrate Stage:
            executeMigrateStage(wr, dob);
            
            // Post-migrate characterise
            // FIXME This could also be an ID instead.
            if( this.postIsCharacterise() ) {
                executeCharacteriseStage(wr, (DigitalObject)wr.getResult(), STAGE_POST_MIGRATE, dpPost );
            }
            
        } catch (Exception e ) {
            // Create a ServiceReport from the exception.
            ServiceReport sr = new ServiceReport();
            sr.setErrorState(ServiceReport.ERROR);
            sr.setError(e.toString());
            wr.setReport(sr);
        }
        return wr;
    }

    /**
     * The actual Migration stage.
     * 
     * @param wr
     * @param dob
     * @throws Exception
     */
    private void executeMigrateStage( WorkflowResult wr, DigitalObject dob ) throws Exception {
        // Now prepare the result:
        List<MeasurementRecordImpl> stage_m = wr.getStage(STAGE_MIGRATE).getMeasurements();
        
        // Create a ServiceRecord, use a factory or pass down, and fill out based on Service Registry.
        // FIXME Can this be done more automatically/sensibly?
        wr.getStage(STAGE_MIGRATE).setServiceRecord(
                ServiceBrowser.createServiceRecordFromEndpoint(this.parameters.get(PARAM_SERVICE)) );
        
        // Invoke the service, timing it along the way:
        boolean success = true;
        MigrateResult migrated = null;
        long msBefore = 0, msAfter = 0;
        URI from = null, to = null;
        msBefore = System.currentTimeMillis();
        try {
            from = new URI(getFromFormat());
            to = new URI(getToFormat());
            migrated = migrator.migrate(dob, from, to, null);
        } catch( Exception e ) {
            success = false;
            throw new Exception ("Service Invocation Failed! : " + e );
        }
        msAfter = System.currentTimeMillis();
        
        // Compute the run time.
        stage_m.add(new MeasurementRecordImpl(TecRegMockup.PROP_SERVICE_TIME, ""+((msAfter-msBefore)/1000.0)) );

        // Now record
        if( success && migrated.getDigitalObject() != null ) {

            stage_m.add( new MeasurementRecordImpl( TecRegMockup.PROP_SERVICE_SUCCESS, "true"));

            // Take the digital object, and give it a sensible name, using the new format extension.
            DigitalObject.Builder newdob = new DigitalObject.Builder(migrated.getDigitalObject());
            if( to != null ) {
                Format f = ServiceBrowser.fr.getFormatForURI(to);
                newdob.title( dob.getTitle()+"."+f.getExtensions().iterator().next());
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
        throw new Exception("Migration failed.  No Digital Object was created. "+migrated.getReport().getError());
        
        // FIXME Add a 'toString' on the report that makes a text summary of the whole state?
        
    }
    
    /**
     * 
     * @param wr
     * @param dob
     * @param stage
     * @throws Exception
     */
    private void executeCharacteriseStage( WorkflowResult wr, DigitalObject dob, String stage, DetermineProperties dp ) throws Exception {
        // Now prepare the result:
        List<MeasurementRecordImpl> stage_m = wr.getStage(stage).getMeasurements();
        
        // Invoke the service, timing it along the way:
        boolean success = true;
        DeterminePropertiesResult result = null;
        long msBefore = 0, msAfter = 0;
        msBefore = System.currentTimeMillis();
        try {
            result = dp.measure(dob, null, null);
        } catch( Exception e ) {
            success = false;
        }
        msAfter = System.currentTimeMillis();
        
        // Compute the run time.
        stage_m.add(new MeasurementRecordImpl(TecRegMockup.PROP_SERVICE_TIME, ""+((msAfter-msBefore)/1000.0)) );

        // Record results:
        if( success ) {
            stage_m.add( new MeasurementRecordImpl( TecRegMockup.PROP_SERVICE_SUCCESS, "true"));
            if( result != null ) {
                for( Property p : result.getProperties().getProperties() ) {
                    stage_m.add(new MeasurementRecordImpl( p.getName(), p.getValue() ));
                }
            }
            return;
        }

        // FAILED:
        stage_m.add( new MeasurementRecordImpl( TecRegMockup.PROP_SERVICE_SUCCESS, "false"));

    }

}
