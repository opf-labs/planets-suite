/**
 * 
 */
package eu.planets_project.tb.gui.backing.exp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.faces.model.SelectItem;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.MigrationPath;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.tb.api.data.util.DataHandler;
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
        List<ServiceDescription> sdl = new Vector<ServiceDescription>();
        for( ServiceDescription sd : this.listAllMigrationServices() )  {
            boolean addThis = false;
            for( MigrationPath path : sd.getPaths() ) {
                if( ( ! this.isInputSet() ) || this.getInputFormat().equals(path.getInputFormat().toString()) ) {
                    if( ( ! this.isOutputSet() ) || this.getOutputFormat().equals(path.getOutputFormat().toString()) ) {
                        addThis = true;
                    }
                }
            }
            if( addThis ) sdl.add(sd);
        }
        return ServiceBrowser.mapServicesToSelectList( sdl );
    }
    
    private List<ServiceDescription> listAllMigrationServices() {
        return ServiceBrowser.getListOfServices(Migrate.class.getCanonicalName());
    }


    /**
     * @return
     */
    public List<SelectItem> getPreMigrationServiceList() {
        List<SelectItem> slist = new ArrayList<SelectItem>();
        for( ServiceDescription sd : this.listAllCharacteriseServices() ) {
            slist.add( createCharServiceSelectItem(sd) );
        }
        for( ServiceDescription sd : this.listAllIdentificationServices() ) {
            slist.add( createIdentifyServiceSelectItem(sd) );
        }
        return slist;
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
        return this.getPreMigrationServiceList();
    }
 
    private List<ServiceDescription> listAllCharacteriseServices() {
        return ServiceBrowser.getListOfServices(Characterise.class.getCanonicalName());
    }

    private List<ServiceDescription> listAllIdentificationServices() {
        return ServiceBrowser.getListOfServices(Identify.class.getCanonicalName());
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
        Set<URI> formats = new HashSet<URI>();
        for( ServiceDescription sd : this.listAllMigrationServices() )  {
            for( MigrationPath path : sd.getPaths() ) {
                if( ( ! this.isServiceSet() ) || this.getMigrationService().equals(sd.getEndpoint().toString()) ) {
                    if( ( ! this.isOutputSet() ) || this.getOutputFormat().equals(path.getOutputFormat().toString()) ) {
                        formats.add(path.getInputFormat());
                    }
                }
            }
        }
        return ServiceBrowser.mapFormatURIsToSelectList(formats);
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
        Set<URI> formats = new HashSet<URI>();
        for( ServiceDescription sd : this.listAllMigrationServices() )  {
            for( MigrationPath path : sd.getPaths() ) {
                if( ( ! this.isServiceSet() ) || this.getMigrationService().equals(sd.getEndpoint().toString()) ) {
                    if( ( ! this.isInputSet() ) || this.getInputFormat().equals(path.getInputFormat().toString()) ) {
                        formats.add(path.getOutputFormat());
                    }
                }
            }
        }
        return ServiceBrowser.mapFormatURIsToSelectList(formats);
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
                DigitalObject dob;
                try {
                    dob = dh.getDigitalObject(dobRef);
                } catch (FileNotFoundException e) {
                    log.error("Could not find file. "+e);
                    return "";
                }
                summary += dob.getTitle();
                try {
                    summary += " ("+dob.getContent().read().available()+" bytes)";
                } catch (IOException e) {
                    log.error("Failed to read stream. "+e);
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
                    return dh.getDownloadURI(dobRef).toString();
                } catch (FileNotFoundException e) {
                    log.error("Failed to generate download URL. " + e);
                    return "";
                }
            }
            return "";
        }

    }
    
    public static void main(String args[]) {
        Properties p = System.getProperties();
        for( Object key : p.keySet() ) {
            System.out.println(key + " = "+p.getProperty((String)key));
        }
    }
}
