/**
 * 
 */
package eu.planets_project.tb.gui.backing.exp;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.registry.api.Registry;
import eu.planets_project.ifr.core.registry.impl.CoreRegistry;
import eu.planets_project.ifr.core.registry.impl.PersistentRegistry;
import eu.planets_project.ifr.core.techreg.formats.Format;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.api.data.util.DigitalObjectRefBean;
import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.AdminManagerImpl;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.model.eval.MeasurementImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;
import eu.planets_project.tb.impl.services.mockups.workflow.MigrateWorkflow;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ExpTypeMigrate extends ExpTypeBackingBean {
    private PlanetsLogger log = PlanetsLogger.getLogger(ExpTypeMigrate.class, "testbed-log4j.xml");
    
    /**
     * @return the identifyService
     */
    public String getMigrationService() {
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
//        log.info("Got params: "+expBean.getExperiment().getExperimentExecutable().getParameters() );
//        log.info("Got param: "+expBean.getExperiment().getExperimentExecutable().getParameters().get(MigrateWorkflow.PARAM_SERVICE) );
        return expBean.getExperiment().getExperimentExecutable().getParameters().get(MigrateWorkflow.PARAM_SERVICE);
    }

    /**
     * @param identifyService the identifyService to set
     */
    public void setMigrationService(String identifyService) {
        // FIXME Refresh the service list at this moment?
        log.info("Setting the Migrate service to: "+identifyService);
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        expBean.getExperiment().getExperimentExecutable().getParameters().put(MigrateWorkflow.PARAM_SERVICE, identifyService);
    }
    
    private boolean isServiceSet() {
        return this.isValueSet(this.getMigrationService());
    }

    private boolean isValueSet( String value ) {
        if( value == null ) return false;
        if( "".equals(value) ) return false;
        return true;
    }

    /**
     * @return Return available Services consistent with the current Input and Output.
     */
    public List<SelectItem> getMigrationServiceList() {
        log.info("IN: getMigrationServiceList");
        ServiceBrowser sb = (ServiceBrowser)JSFUtil.getManagedObject("ServiceBrowser");
        
        String input = this.getInputFormat();
        if( ! this.isInputSet() ) input = null;
        String output = this.getOutputFormat();
        if( ! this.isOutputSet() ) output = null;
        
        List<ServiceDescription> sdl = sb.getMigrationServices( input, output );
        
        log.info("OUT: getMigrationServiceList");
        return ServiceBrowser.mapServicesToSelectList( sdl );
    }
    

    /**
     * @return
     */
    public List<SelectItem> getPreMigrationServiceList() {
        List<SelectItem> slist = new ArrayList<SelectItem>();
        for( ServiceDescription sd : this.listAllCharacteriseServices() ) {
            if( ! this.isInputSet() || this.inputsMatchFormat(sd, this.getInputFormat() )) {
                slist.add( createCharServiceSelectItem(sd) );
            }
        }
        for( ServiceDescription sd : this.listAllIdentificationServices() ) {
            if( ! this.isInputSet() || this.inputsMatchFormat(sd, this.getInputFormat() )) {
                slist.add( createIdentifyServiceSelectItem(sd) );
            }
        }
        return slist;
    }
    
    private boolean inputsMatchFormat( ServiceDescription sd, String format ) {
        URI formatUri;
        try {
            formatUri = new URI( format );
        } catch (URISyntaxException e) {
            return false;
        }

        // Accepts any input?
        if( sd.getInputFormats() == null 
                || sd.getInputFormats().size() == 0 
                || sd.getInputFormats().contains( Format.ANY ) ) return true;
        
        // Examine accepted inputs:
        for( URI sinf : sd.getInputFormats() ) {
            // Examine aliases for that format:
            for( Format alias : ServiceBrowser.fr.getFormatAliases(sinf) ) {
                if( alias.getTypeURI().equals( formatUri )) return true;
            }
        }
        return false;
    }
    
    /**
     * @param sd
     * @return
     */
    private static SelectItem createCharServiceSelectItem( ServiceDescription sd ) {
        String serviceName = "Characterise via " + sd.getName();
        serviceName += " (@"+sd.getEndpoint().getHost()+")";
        return new SelectItem( 
                encodeCharParFromOp(
                        MigrateWorkflow.SERVICE_TYPE_CHARACTERISE, 
                        sd.getEndpoint().toString() 
                        ), 
                serviceName );
    }

    /**
     * @param sd
     * @return
     */
    private static SelectItem createIdentifyServiceSelectItem( ServiceDescription sd ) {
        String serviceName = "Identify via " + sd.getName();
        serviceName += " (@"+sd.getEndpoint().getHost()+")";
        return new SelectItem( 
                encodeCharParFromOp(
                        MigrateWorkflow.SERVICE_TYPE_IDENTIFY, 
                        sd.getEndpoint().toString() 
                        ), 
                serviceName );
    }
    
    /**
     * @return
     */
    public List<SelectItem> getPostMigrationServiceList() {
        List<SelectItem> slist = new ArrayList<SelectItem>();
        for( ServiceDescription sd : this.listAllCharacteriseServices() ) {
            if( ! this.isOutputSet() || this.inputsMatchFormat(sd, this.getOutputFormat() )) {
                slist.add( createCharServiceSelectItem(sd) );
            }
        }
        for( ServiceDescription sd : this.listAllIdentificationServices() ) {
            if( ! this.isOutputSet() || this.inputsMatchFormat(sd, this.getOutputFormat() )) {
                slist.add( createIdentifyServiceSelectItem(sd) );
            }
        }
        return slist;
    }
 
    /** Name to store the look-up tables under. */
    private final static String CHAR_SD_CACHE_NAME = "CacheCharacterisationServicesCache";
    
    /**
     * @return A list of all the characterisation services (cached in request-scope).
     */
    @SuppressWarnings("unchecked")
    private List<ServiceDescription> listAllCharacteriseServices() {
        Map<String,Object> reqmap =
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
        
        // Lookup or re-build:
        List<ServiceDescription> sds = (List<ServiceDescription>) reqmap.get(CHAR_SD_CACHE_NAME);
        if( sds == null ) {
            log.info("Refreshing list of characterisation services...");
            sds = lookupServicesByType(Characterise.class.getCanonicalName());
            reqmap.put(CHAR_SD_CACHE_NAME, sds);
            log.info("Refreshed.");
        }
        return sds;
    }
    
    /** Name to store the look-up tables under. */
    private final static String IDENTIFY_SD_CACHE_NAME = "CacheIdentifcationServicesCache";
    
    /**
     * @return A list of all the identification services (cached in request-scope).
     */
    @SuppressWarnings("unchecked")
    private List<ServiceDescription> listAllIdentificationServices() {
        Map<String,Object> reqmap =
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
        
        // Lookup or re-build:
        List<ServiceDescription> sds = (List<ServiceDescription>) reqmap.get(IDENTIFY_SD_CACHE_NAME);
        if( sds == null ) {
            log.info("Refreshing list of identification services...");
            sds = lookupServicesByType(Identify.class.getCanonicalName());
            reqmap.put(IDENTIFY_SD_CACHE_NAME, sds);
            log.info("Refreshed.");
        }
        return sds;
    }

    /**
     * @param service
     */
    public void setPreMigrationService(String service) {
        log.info("Setting the Pre-Migrate service to: "+service);
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        HashMap<String, String> p = expBean.getExperiment().getExperimentExecutable().getParameters();
        p.put(MigrateWorkflow.PARAM_PRE_SERVICE_TYPE, decodeOpFromCharPar(service) );
        p.put(MigrateWorkflow.PARAM_PRE_SERVICE, decodeEndpointFromCharPar(service) );
    }

    /**
     * @return
     */
    public String getPreMigrationService() {
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        HashMap<String, String> p = expBean.getExperiment().getExperimentExecutable().getParameters();
        String config = encodeCharParFromOp(
                p.get(MigrateWorkflow.PARAM_PRE_SERVICE_TYPE), p.get(MigrateWorkflow.PARAM_PRE_SERVICE) );
        log.info("Getting the Pre-Migrate service: "+config);
        return config;

    }
    
    /**
     * @param service
     */
    public void setPostMigrationService(String service) {
        log.info("Setting the Post-Migrate service to: "+service);
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        HashMap<String, String> p = expBean.getExperiment().getExperimentExecutable().getParameters();
        p.put(MigrateWorkflow.PARAM_POST_SERVICE_TYPE, decodeOpFromCharPar(service) );
        p.put(MigrateWorkflow.PARAM_POST_SERVICE, decodeEndpointFromCharPar(service) );
    }

    /**
     * @return
     */
    public String getPostMigrationService() {
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        HashMap<String, String> p = expBean.getExperiment().getExperimentExecutable().getParameters();
        String config = encodeCharParFromOp(
                p.get( MigrateWorkflow.PARAM_POST_SERVICE_TYPE), p.get(MigrateWorkflow.PARAM_POST_SERVICE) );
        log.info("Getting the Post-Migrate service: "+config);
        return config;
    }

    private static String encodeCharParFromOp( String operation, String endpoint ) {
        return operation+":"+endpoint;
    }
    
    private static String decodeOpFromCharPar( String charparameter ) {
        if( charparameter == null || charparameter.indexOf(":") == -1 ) return "";
        return charparameter.substring(0, charparameter.indexOf(":"));
    }
    
    private static String decodeEndpointFromCharPar( String charparameter ) {
        if( charparameter == null || charparameter.indexOf(":") == -1 ) return "";
        return charparameter.substring(charparameter.indexOf(":")+1);
    }
    

    /**
     * @return
     */
    public String getInputFormat() {
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        return expBean.getExperiment().getExperimentExecutable().getParameters().get( MigrateWorkflow.PARAM_FROM );
    }
    
    /**
     * @param inputFormat
     */
    public void setInputFormat( String inputFormat) {
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        expBean.getExperiment().getExperimentExecutable().getParameters().put(MigrateWorkflow.PARAM_FROM, inputFormat );
    }
    
    private boolean isInputSet() {
        return isValueSet( this.getInputFormat() );
    }

    /**
     * @return Return available Input Formats consistent with the current Service and Output.
     */
    public List<SelectItem> getInputFormatList() {
        log.info("IN: getInputFormatList");
        ServiceBrowser sb = (ServiceBrowser)JSFUtil.getManagedObject("ServiceBrowser");
        
        String endpoint = this.getMigrationService();
        if( ! this.isServiceSet() ) endpoint = null;
        String output = this.getOutputFormat();
        if( ! this.isOutputSet() ) output = null;
        
        Set<Format> inputFormats = sb.getMigrationInputFormats(endpoint, output);

        log.info("OUT: getInputFormatList");
        return ServiceBrowser.mapFormatsToSelectList(inputFormats);
    }
    
    /**
     * @return
     */
    public String getOutputFormat() {
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        return expBean.getExperiment().getExperimentExecutable().getParameters().get( MigrateWorkflow.PARAM_TO );
    }
    
    /**
     * @param format
     */
    public void setOutputFormat( String format) {
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        expBean.getExperiment().getExperimentExecutable().getParameters().put(MigrateWorkflow.PARAM_TO, format );
    }

    private boolean isOutputSet() {
        return isValueSet( this.getOutputFormat() );
    }
    /**
     * @return Return available Output Formats consistent with the current Service and Input.
     */
    public List<SelectItem> getOutputFormatList() {
        log.info("IN: getOutputFormatList");
        ServiceBrowser sb = (ServiceBrowser)JSFUtil.getManagedObject("ServiceBrowser");
        
        String endpoint = this.getMigrationService();
        if( ! this.isServiceSet() ) endpoint = null;
        String input = this.getInputFormat();
        if( ! this.isInputSet() ) input = null;
        
        Set<Format> outputFormats = sb.getMigrationOutputFormats(endpoint, input);

        log.info("OUT: getOutputFormatList");
        return ServiceBrowser.mapFormatsToSelectList(outputFormats);
    }
    
    
    
    /* (non-Javadoc)
     * @see eu.planets_project.tb.gui.backing.exp.ExpTypeBackingBean#getStageBeans()
     */
    @Override
    public List<ExperimentStageBean> getStageBeans() {
        return getWorkflow(AdminManagerImpl.MIGRATE).getStages();
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.gui.backing.exp.ExpTypeBackingBean#getObservables()
     */
    @Override
    public HashMap<String,List<MeasurementImpl>> getObservables() {
        return getWorkflow(AdminManagerImpl.MIGRATE).getObservables();
    }
    
    HashMap<String,List<MeasurementImpl>> manualObsCache;
    long cacheExperimentID;
    /* (non-Javadoc)
     * @see eu.planets_project.tb.gui.backing.exp.ExpTypeBackingBean#getManualObservables()
     */
    @Override
    public HashMap<String,List<MeasurementImpl>> getManualObservables() {
    	ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
    	if(manualObsCache==null||(cacheExperimentID != expBean.getExperiment().getEntityID())){
    		cacheExperimentID = expBean.getExperiment().getEntityID();

        	//query for properties that have been added from the Ontology
        	HashMap<String,Vector<String>> ontoPropIDs = new HashMap<String, Vector<String>>();
        	for(ExperimentStageBean stage : expBean.getStages()){
        		ontoPropIDs.put(stage.getName(),expBean.getExperiment().getExperimentExecutable().getManualProperties(stage.getName()));
        	}
        	//this is the static list of manual properties - normally empty
        	HashMap<String,List<MeasurementImpl>> staticWFobs = 
        		getWorkflow(AdminManagerImpl.MIGRATE).getManualObservables();
        	
        	//FIXME AL: staticWFobs returns wrong items - where are they added - exclude staticWFobs for now
        	//manualObsCache = mergeManualObservables(staticWFobs, ontoPropIDs);
        	manualObsCache = mergeManualObservables(null, ontoPropIDs);

    	}
    	return manualObsCache;
    }

    /**
     * 
     * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
     */
    public class MigrateResultForDO extends ResultsForDigitalObjectBean {

        /**
         * @param input
         */
        public MigrateResultForDO(String input) {
            super(input);
        }
        
        /**
         * @return
         */
        public List<MigrationResultBean> getMigrations() {
            List<MigrationResultBean> migs = new Vector<MigrationResultBean>();
            int i = 1;
            for( ExecutionRecordImpl exerec : this.getExecutionRecords() ) {
                migs.add(new MigrationResultBean( i, exerec ) );
                i++;
            }
            return migs;
        }
        
        
        
    }
    
    
    /**
     * @return
     */
    public List<MigrateResultForDO> getMigrateResults() {
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        List<MigrateResultForDO> results = new Vector<MigrateResultForDO>();
        // Populate using the results:
        for( String file : expBean.getExperimentInputData().values() ) {
            MigrateResultForDO res = new MigrateResultForDO(file);
            results.add(res);
        }

        // Now return the results:
        return results;
        
    }

    /**
     * 
     * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
     *
     */
    public class MigrationResultBean {

        private int batchId;
        private ExecutionRecordImpl exerec;

        /**
         * @param exerec
         */
        public MigrationResultBean(int batchId, ExecutionRecordImpl exerec) {
            this.batchId = batchId;
            this.exerec = exerec;
        }
        
        /**
         * @return
         */
        public String getDigitalObjectResult() {
            String dobRef = exerec.getResult();
            String summary = "Run "+batchId+": ";
            if( dobRef != null ) {
                DataHandler dh = new DataHandlerImpl();
                DigitalObjectRefBean dobr;
                try {
                    dobr = dh.get(dobRef);
                } catch (FileNotFoundException e) {
                    log.error("Could not find file. "+e);
                    return "";
                }
                summary += dobr.getName();
                long size = dobr.getSize();
                if( size >= 0 ) {
                    summary += " ("+size+" bytes)";
                }
                return summary;
            }
            summary += "No Result.";
            return summary;
        }

        /**
         * @return
         */
        public String getDigitalObjectDownloadURL() {
            String dobRef = exerec.getResult();
            if( dobRef != null ) {
                DataHandler dh = new DataHandlerImpl();
                try {
                    DigitalObjectRefBean dobr = dh.get(dobRef);
                    return dobr.getDownloadUri().toString();
                } catch (FileNotFoundException e) {
                    log.error("Failed to generate download URL. " + e);
                    return "";
                }
            }
            return "";
        }

    }
    
    //Embed system properties in ServiceDescription property chunk?;
    //Drag-and-drop in RF moves the pane? NO.
    // FIXME Remove this.
    public static void main(String args[]) {
               
        Properties p = System.getProperties();
        
        ByteArrayOutputStream byos = new ByteArrayOutputStream();
        try {
            p.storeToXML(byos, "Automatically generated for PLANETS Service ", "UTF-8");
            String res = byos.toString("UTF-8");
            System.out.println(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // This.
        List<String> pl = new ArrayList<String>();
        for( Object key : p.keySet() ) {
            pl.add( (String)key );
        }
        Collections.sort(pl);
        
        //
        for( String key : pl ) {
            System.out.println(key + " = "+p.getProperty(key));
        }
        
        /*
         * http://java.sun.com/j2se/1.5.0/docs/api/java/lang/management/ThreadMXBean.html#getCurrentThreadCpuTime()
         * 
         * http://www.java-forums.org/new-java/5303-how-determine-cpu-usage-using-java.html
         * 
         */
        
        ThreadMXBean TMB = ManagementFactory.getThreadMXBean();
        int mscale = 1000000;
        long time = 0, time2 = 0;
        long cput = 0, cput2 = 0;
        double cpuperc = -1;

        //Begin loop.
        for( int i=0; i< 10; i++ ) {

            if( TMB.isThreadCpuTimeSupported() )
            {
                if(!TMB.isThreadCpuTimeEnabled())
                {
                    TMB.setThreadCpuTimeEnabled(true);
                }
                
//                if(new Date().getTime() * mscale - time > 1000000000) //Reset once per second
//                {
                System.out.println("Resetting...");
                time = System.currentTimeMillis() * mscale;
                cput = TMB.getCurrentThreadCpuTime();
//                cput = TMB.getCurrentThreadUserTime();
//                }

            }

            //Do cpu intensive stuff
            for( int k = 0; k < 10; k++ ) {
                for( int j = 0; j < 100000; j++ ) {
                    double a = Math.pow(i, j);
                    double b = a/j + Math.random();
                    a = b * Math.random();
                    b = a * Math.random();
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            if( TMB.isThreadCpuTimeSupported() )
            {
//                if(new Date().getTime() * mscale - time != 0) {
                cput2 = TMB.getCurrentThreadCpuTime();
                System.out.println("cpu: " + (cput2 - cput)/(1000.0*mscale) );
//                cput2 = TMB.getCurrentThreadUserTime();
                
                time2 = System.currentTimeMillis() * mscale;
                System.out.println("time: " + (time2 - time)/(1000.0*mscale) );
                
                cpuperc = 100.0 * (cput2 - cput) / (double)(time2 - time);
                System.out.println("cpu perc = " + cpuperc);
//                }
            }
            //End Loop
        }
        System.out.println("Done.");
    }
}
