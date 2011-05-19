/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/

package eu.planets_project.pp.plato.action.workflow;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;

import org.apache.commons.logging.Log;
import org.jboss.annotation.ejb.cache.Cache;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;

import eu.planets_project.pp.plato.action.interfaces.IEvaluateExperiments;
import eu.planets_project.pp.plato.action.interfaces.IRunExperiments;
import eu.planets_project.pp.plato.action.interfaces.IWorkflowStep;
import eu.planets_project.pp.plato.bean.BooleanCapsule;
import eu.planets_project.pp.plato.bean.ExperimentStatus;
import eu.planets_project.pp.plato.bean.PrepareChangesForPersist;
import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.DetailedExperimentInfo;
import eu.planets_project.pp.plato.model.DigitalObject;
import eu.planets_project.pp.plato.model.Experiment;
import eu.planets_project.pp.plato.model.PlanState;
import eu.planets_project.pp.plato.model.PreservationActionDefinition;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.model.User;
import eu.planets_project.pp.plato.model.XcdlDescription;
import eu.planets_project.pp.plato.model.measurement.MeasurableProperty;
import eu.planets_project.pp.plato.model.measurement.Measurement;
import eu.planets_project.pp.plato.model.values.FreeStringValue;
import eu.planets_project.pp.plato.model.values.Value;
import eu.planets_project.pp.plato.services.PlatoServiceException;
import eu.planets_project.pp.plato.services.action.IEmulationAction;
import eu.planets_project.pp.plato.services.action.IMigrationAction;
import eu.planets_project.pp.plato.services.action.IPreservationAction;
import eu.planets_project.pp.plato.services.action.MigrationResult;
import eu.planets_project.pp.plato.services.action.PreservationActionServiceFactory;
import eu.planets_project.pp.plato.services.characterisation.FormatIdentification;
import eu.planets_project.pp.plato.services.characterisation.FormatIdentification.FormatIdentificationResult;
import eu.planets_project.pp.plato.services.characterisation.fits.FitsIntegration;
import eu.planets_project.pp.plato.services.characterisation.jhove.JHoveAdaptor;
import eu.planets_project.pp.plato.services.characterisation.xcl.XcdlExtractor;
import eu.planets_project.pp.plato.util.Downloader;
import eu.planets_project.pp.plato.util.FileUtils;
import eu.planets_project.pp.plato.util.OS;
import eu.planets_project.pp.plato.util.PlatoLogger;

/**
 * Implements actions for workflow step 'Run Experiments'
 *
 * Gives the user the opportunity to document the outcome of the experiments per alternative
 * and upload the result file. Furthermore the user can run experiments based on action services.
 *
 * @author Hannes Kulovits
 */
@Stateful
@Scope(ScopeType.SESSION)
@Name("runexperiments")
//@Cache(org.jboss.ejb3.cache.NoPassivationCache.class)
public class RunExperimentsAction extends AbstractWorkflowStep implements
        IRunExperiments {
    
    private static final long serialVersionUID = 4241837780040966155L;

    protected boolean needsClearEm() {
        return true;
    }

    private static final Log log = PlatoLogger.getLogger(RunExperimentsAction.class);

    @In(create = true)
    IEvaluateExperiments evalexperiments;

    @DataModel
    List<Alternative> consideredAlternatives;

    @DataModelSelection
    Alternative selectedAlternative;
    
    private JHoveAdaptor jHoveAdaptor;

    @Out(required = false)
    private DigitalObject up;

    /**
     * Indicates if browse field is displayed.
     */
    @Out(required = false)
    BooleanCapsule showUpload = new BooleanCapsule(false);

    @In (required=false)
    private User user;

    @Out
    private BooleanCapsule hasAutomatedExperiments = new BooleanCapsule();
    
    @Out
    private List<MeasurableProperty> measurableProperties = new ArrayList<MeasurableProperty>();
    
    @Out(required=false)
    Alternative emulationAlternative;
    
    private Map<DigitalObject, String> tempFiles = new HashMap<DigitalObject, String>();
    private File tempDir = null;   
    
    @In(required=false)
    @Out(required=false)
    private DetailedExperimentInfo selectedExperimentInfo;
    
    @In(required=false)
    @Out(required=false)
    private ExperimentStatus experimentStatus = new ExperimentStatus();

    private FitsIntegration fits;// = new FitsIntegration();

    public RunExperimentsAction() {
        requiredPlanState = new Integer(PlanState.EXPERIMENT_DEFINED);
    }

    /**
     * @see AbstractWorkflowStep#getSuccessor()
     */
    protected IWorkflowStep getSuccessor() {
        return evalexperiments;
    }

    /**
     * @see AbstractWorkflowStep#save()
     */
    public String save() {

        prepareAlternatives(); 

        
        // maybe some XCDLs have been added to the sample records, save them too 
//        for (SampleObject r : selectedPlan.getSampleRecordsDefinition().getRecords()) {
//            prep.prepare(r);
//            em.persist(em.merge(r)); 
//        }
        
        prepareTempFileSaving();
        
//        
//        // save the bytestreams into the database - 
//        // not directly through our digitalobjects, because
//        // then they would stay in memory, but rather 
//        // through an extra "channel" so that they can be loaded lazily.
//        // hence the temporary variables and the merging.
//        for (DigitalObject o: tempFiles.keySet()) {
//            try {
//                File file = new File(tempFiles.get(o));
//                byte[] data = FileUtils.getBytesFromFile(file);
//                
//                // we get a merged instance of our DigitalObject to save the bytestream into the database.
//                // we do NOT want the bytestream to be in our loaded object instance, as this costs too much
//                // memory. Lazy loading will be able to re-load that bytestream into our loaded instance... 
//                // hopefully :)
//                DigitalObject digitalObject = em.merge(o);
//                
//                digitalObject.getData().setData(data);
//                
//                em.persist(digitalObject);
//                o.setId(digitalObject.getId());
//                
//            } catch (IOException e) {
//                log.error(e);
//            }
//        }

        super.save(selectedPlan.getAlternativesDefinition());
        changed = "";
        
        selectedPlan.getTree().initValues(
                selectedPlan.getAlternativesDefinition().getConsideredAlternatives(),
                selectedPlan.getSampleRecordsDefinition().getRecords()
                        .size());
        super.save(selectedPlan.getTree());

        doClearEm();
        init();
        return null;
    }

    public void prepareTempFileSaving() {
        for (Alternative a: selectedPlan.getAlternativesDefinition().getConsideredAlternatives()) {
            for (SampleObject so: selectedPlan.getSampleRecordsDefinition().getRecords()) {
                DigitalObject result = a.getExperiment().getResults().get(so);
                if (tempFiles.containsKey(result)) {
                    try {
                        File file = new File(tempFiles.get(result));
                        byte[] data = FileUtils.getBytesFromFile(file);
                        DigitalObject storedResult = em.merge(result);
                        storedResult.getData().setData(data);
                        a.getExperiment().getResults().put(so,storedResult);
                    } catch (IOException e) {
                        log.error(e);
                    }
                }
            }
        }
    }

    public void prepareAlternatives() {
        /** dont forget to prepare changed entities e.g. set current user */
        PrepareChangesForPersist prep = new PrepareChangesForPersist(user.getUsername());
        for (Alternative alt : selectedPlan.getAlternativesDefinition()
                .getAlternatives()) {
            prep.prepare(alt);
            //em.persist(em.merge(alt));
            // alternativesdefinition is saved further down
        }
    }
    
    protected void doClearEm() {
        OS.deleteDirectory(tempDir);
        tempFiles.clear();
        super.doClearEm();
    }

    /**
     * @see AbstractWorkflowStep#init()
     */
    public void init() {
        if (tempDir != null) {
            OS.deleteDirectory(tempDir);
        }
        tempDir = new File(OS.getTmpPath() + "digitalobjects" + System.nanoTime());
        tempDir.mkdir();
        tempDir.deleteOnExit();
        tempFiles.clear();
        log.debug("using temp directory " + tempDir.getAbsolutePath());
        
        try {
            fits = new FitsIntegration();
        } catch (Throwable e) {
            fits = null;
            log.error("Could not instantiate FITS, it is not configured properly.", e);
            FacesMessages.instance().add(FacesMessage.SEVERITY_WARN, "Could not instantiate FITS, it is not configured properly.");
        }
        
        consideredAlternatives = selectedPlan.getAlternativesDefinition()
                .getConsideredAlternatives();
        jHoveAdaptor=new JHoveAdaptor();

        // are there experiments which can be run automated?
        hasAutomatedExperiments.setBool(false);
        Iterator<Alternative> iter = consideredAlternatives.iterator(); 
        while (iter.hasNext() && !hasAutomatedExperiments.isBool()) {
            Alternative a = iter.next();
            if (a.isExecutable()) {
                hasAutomatedExperiments.setBool(true);
            }
        }

        // add empty result files where missing (only for considered alternatives!)
        
        List<SampleObject> allRecords = selectedPlan.getSampleRecordsDefinition().getRecords();
        for (Alternative alternative : consideredAlternatives) {
            Experiment exp = alternative.getExperiment();

            for (SampleObject record : allRecords) {
                DigitalObject u = exp.getResults().get(record);

                if (u == null) {
                    exp.addRecord(record);  
                    u = exp.getResults().get(record);
                }
                
//                if (exp.getDetailedInfo().get(record) == null) {
//                    
//                    DetailedExperimentInfo detailedExperimentInfo = new DetailedExperimentInfo();
//                    detailedExperimentInfo.setSuccessful(true);
//                    exp.getDetailedInfo().put(record, detailedExperimentInfo);
//                }
            }
        }
        showUpload.setBool(false);
        refreshMeasurableProperties();        
    }
    private void refreshMeasurableProperties() {
        measurableProperties.clear();
        measurableProperties.addAll(selectedPlan.getMeasurableProperties());
        for (MeasurableProperty p: measurableProperties) {
            log.debug("prop:: "+p.getName());
        }
    }

    /**
     * @see AbstractWorkflowStep#discard()
     */
    @Override
    @RaiseEvent("reload")
    public String discard() {
        String result = super.discard();
        init();
        return result;
    }
    
    /**
     * @see AbstractWorkflowStep#destroy()
     */
    @Destroy
    @Remove
    public void destroy() {
    }

    /**
     * @see AbstractWorkflowStep#validate(boolean)
     */
    public boolean validate(boolean showValidationErrors) {
        return true;
    }

    /**
     * @see AbstractWorkflowStep#getWorkflowstepName()
     */
    protected String getWorkflowstepName() {
        return "runexperiments";
    }

    /**
     * Adds an error message with {@link javax.faces.application.FacesMessage#SEVERITY_ERROR}
     * to FacesMessages.
     *
     * @param message error message
     */
    private void failure(String message) {
        FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR,
                message);
        showUpload.setBool(false);
    }

    /**
     * Removes a result file from an alternative's experiment.
     *
     * @param object {@link eu.planets_project.pp.plato.model.SampleObject}
     */
    public void removeUpload(Object object) {
        log.debug("Object: " + object);
        if (object instanceof SampleObject) {
            SampleObject sample = (SampleObject) object;
            selectedAlternative.getExperiment().getResults().put(sample, new DigitalObject());
            log.debug("File in RunExperiment removed");
            showUpload.setBool(false);
        } else {
            failure("Couldn't remove upload");
        }
    }

    /**
     * Determines upload file to a specific sample record.
     *
     * @param object sample record
     */
    public void setUpload(Object object) {
        log.debug("Number: " + object);
        //log.debug("File in RunExperiment uploaded");
        if (object instanceof SampleObject) {
            SampleObject sample = (SampleObject) object;
            up = em.merge(selectedAlternative.getExperiment().getResults().get(sample));
            selectedAlternative.getExperiment().getResults().put(sample,up);
            if(up == null){
                failure("Couldn't start upload process");
            } else {
                showUpload.setBool(true);
            }
        } else {
            failure("Couldn't start upload process");
        }
    }
   
    
    public void upload() {
        saveTempFile(up, up);
        //characterise(up);
        characteriseFits(up);
        showUpload.setBool(false);
    }

    /**
     * Downloads the result file of a specific sample record.
     *
     * @param object sample record the user wants to download the result file.
     */
//    public void download(Object object) {
//        if (object instanceof SampleObject) {
//            SampleObject sample = (SampleObject) object;
//            up = selectedAlternative.getExperiment().getResults().get(sample);
//            if(up == null){
//                failure("Couldn't start download process");
//            }else{
//                if (tempFiles.containsKey(up)) {
//                    //download TEMPFILE
//                    Downloader.instance().download(up,tempFiles.get(up));
//                } else {
//                    // we have to merge it back into the session because we the data bytestream
//                    // is lazy loaded
//                    DigitalObject u = (DigitalObject)em.merge(up);
//                    Downloader.instance().download(u);
//                }
//            }
//        } else {
//            failure("Couldn't start download process");
//        }
//    }

    /**
     * Downloads the a digital object
     * @param object the object the user wants to download 
     */
    public void download(Object object) {
        if (object == null) {
           failure("Couldn't start download process");
        }else{
            DigitalObject up = (DigitalObject)object;
            if (tempFiles.containsKey(up)) {
                //download TEMPFILE
                Downloader.instance().download(up,tempFiles.get(up));
            } else {
                // we have to merge it back into the session because we the data bytestream
                // is lazy loaded
                DigitalObject u = (DigitalObject)em.merge(up);
                Downloader.instance().download(u);
            }
        }
    }

    

    private void runSingle(Alternative a) {
        if (!a.isExecutable()) {
            // this alternative has to be evaluated manually, nothing to do here
            return;
        }

        IPreservationAction action =
            PreservationActionServiceFactory.getPreservationAction(a.getAction());

        // if the action is null the service isn't accessible (anymore)
        // we have to set an error message for each sample record
        if (action == null) {

            String msg = String.format("Preservation action %s - %s is not registered or accessible and cant be executed. (Please check the registry.)",
                    a.getAction().getShortname(), a.getAction().getInfo());


            setUniformProgramOutput(a, msg, false);
        } 
        this.changed="T";
        // das sieht verdächtig aus - pad wird auch geschrieben. 
        // ABER die settings, die in pad eingetragen werden, brauchen wir nicht zum persistieren, sondern nur zum 
        // ausführen der action - stehen ja in experiment.settings. daher ist es ok, dass wir die merged instance verändern.
        PreservationActionDefinition pad = em.merge(a.getAction());
        pad.setExecute(a.getAction().isExecute());

        String settings = a.getExperiment().getSettings();
        pad.setParamByName("settings", settings);

        StringBuffer runDescription = new StringBuffer();

        if (action instanceof IMigrationAction) {
            DigitalObject migrationResultObject;
            DigitalObject experimentResultObject;
            MigrationResult migrationResult = null;
            IMigrationAction migrationAction = (IMigrationAction) action;
            //                int nextIndex = experimentStatus.getNextSampleIndex();
            SampleObject record = experimentStatus.getNextSample(); //null;
            //                if (nextIndex >= 0 && nextIndex < selectedPlan.getSampleRecordsDefinition().getRecords().size()) {
            //                    record = selectedPlan.getSampleRecordsDefinition().getRecords().get(nextIndex);
            //                }
            //experimentStatus.getNextSample();
            while (record != null) {
                if (record.isDataExistent()) {

                    // objectTomigrate is only being read, needs to be merged to lazily get the data out
                    SampleObject objectToMigrate = em.merge(record);

                    try {
                        // ACTION HAPPENS HERE:
                        migrationResult =  migrationAction.migrate(pad, objectToMigrate);
                    } catch (NullPointerException npe) {
                        log.error("Caught nullpointer exception when running a migration tool. ### WRONG CONFIGURATION? ###",npe);
                    } catch (Throwable t) {
                        log.error("Caught unchecked exception when running a migration tool: "+t.getMessage(),t);
                        //throw new PlatoServiceException("Could not run service "+a.getName()+" on object "+record.getShortName(),t);
                    }

                    if (migrationResult != null) {

                        if (migrationResult.isSuccessful() && migrationResult.getMigratedObject() != null) {
                            
                            experimentResultObject = a.getExperiment().getResults().get(record);
                            migrationResultObject = migrationResult.getMigratedObject();
                            experimentResultObject.setContentType(migrationResultObject.getContentType());
                            experimentResultObject.getFormatInfo().assignValues(migrationResultObject.getFormatInfo());

                            int size = saveTempFile(experimentResultObject,migrationResultObject);
                            experimentResultObject.getData().setSize(size);
                            experimentResultObject.setFullname(migrationResultObject.getFullname());

                            characterise(experimentResultObject);
                            characteriseFits(experimentResultObject);
                            experimentResultObject.setJhoveXMLString(jHoveAdaptor.describe(tempFiles.get(experimentResultObject)));
                        }

                        // set detailed infos depending on migration result
                        extractDetailedInfos(a.getExperiment(), record, migrationResult);

                    } else {
                        DetailedExperimentInfo info = a.getExperiment().getDetailedInfo().get(record);
                        if (info == null) {
                            info = new DetailedExperimentInfo();
                            a.getExperiment().getDetailedInfo().put(record,info);
                        }
                        info.setProgramOutput(
                                String.format("Applying action %s to sample %s failed.",
                                        a.getAction().getShortname(), 
                                        record.getFullname()));
                    }
                } 
                record = experimentStatus.getNextSample(); 
            }
        }

        refreshMeasurableProperties();        
    }
    
    public void characterise(DigitalObject dobject) {
        //characteriseFits(dobject);
        
        
        /**
         * DROID is used for file identification.
         */
        FormatIdentification ident = null;
        
        String filename = tempFiles.get(dobject);
        
//        if (filename == null || "".equals(filename)) {
//            DigitalObject o2 = em.merge(dobject);
//            try {
//                ident = DROIDIntegration.getInstance().identifyFormat(
//                        o2.getData().getData(),
//                        o2.getFullname());
//            } catch (Exception e) {
//                log.error(e.getMessage(),e);
//            }
//        } else {
//            ident = DROIDIntegration.getInstance().identify(filename);
//        }
//
        if (ident == null)  {
            return;
        }
        if (ident.getResult() == FormatIdentificationResult.ERROR) {
            /*
             * DROID could not identify this file.
             */
            log.error("DROID could not identify the format of the file." + ident.getInfo());
        } else if (ident.getResult() == FormatIdentificationResult.NOHIT) {
            /*
             * DROID could not identify this file.
             */
            log.info("DROID did not get a hit identifying the file. "+ident.getInfo());
        } else if ((ident.getResult() == FormatIdentificationResult.POSITIVE)){
            /*
             *  match, format identification successful:
             *  if it is a multiple match, we simply take the first one here.
             */
            dobject.getFormatInfo().assignValues(ident.getFormatHits().get(0).getFormat());
            dobject.touch();
        }
        }
    
    /**
     * Processes an experiment.
     *
     * @param alt alternative which in this case is a preservation action.
     */
    public void run(Object alt) {
        if (! (alt instanceof Alternative)) {
            return;
        }
        if (experimentStatus == null)  {
         experimentStatus = new ExperimentStatus();
        }
        experimentStatus.experimentSetup(Arrays.asList((Alternative)alt), selectedPlan.getSampleRecordsDefinition().getRecords());
    }
    /**
     * Runs experiments of all considered alternatives.
     *
     */
    public void runAllExperiments(){
        if (experimentStatus == null)  {
            experimentStatus = new ExperimentStatus();
           }
        experimentStatus.experimentSetup(consideredAlternatives, selectedPlan.getSampleRecordsDefinition().getRecords());
    }
    
    /**
     * runs all experiments scheduled in experimentStatus
     */
    public void startExperiments() {
        if (experimentStatus == null)  {
            experimentStatus = new ExperimentStatus();
           }
        Alternative alt = experimentStatus.getNextAlternative(); 
        while ((alt != null)&&(! experimentStatus.isCanceled())) {
            runSingle(alt);
            alt = experimentStatus.getNextAlternative();
        }
        System.gc();
    }

    /**
     * for the given alternative the program output of all experiment infos is set to <param>msg</param>.
     *  
     */
    private void setUniformProgramOutput(Alternative a, String msg, boolean successful) {
        List<SampleObject> sampleObjects = selectedPlan.getSampleRecordsDefinition().getRecords();
        for (SampleObject o : sampleObjects) {
            DetailedExperimentInfo info = a.getExperiment().getDetailedInfo().get(o);
            
            if (info == null) {
                info = new DetailedExperimentInfo();
                a.getExperiment().getDetailedInfo().put(o, info);
            }
            
            info.setProgramOutput(msg);
            info.setSuccessful(successful);
        }
    }

    /**
     * 
     * @param migratedObject the object that shall be used as KEY for storing the result bytestream
     * @param resultObject the object that contains the actual bytestream to be stored
     * @return the size of the bytestream
     */
    private int saveTempFile(DigitalObject migratedObject,
            DigitalObject resultObject) {
        String tempFileName = tempDir.getAbsolutePath()+"/"+System.nanoTime();
        OutputStream fileStream;
        try {
            fileStream = new  BufferedOutputStream (new FileOutputStream(tempFileName));
            byte[] data = resultObject.getData().getData();
            fileStream.write(data);
            fileStream.close();
            tempFiles.put(migratedObject, tempFileName);
            return data.length;
        } catch (FileNotFoundException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
        }
        return 0;
    }

    /**
     * stores {@link MigrationResult migration results} for the given sample object in {@link DetailedExperimentInfo experiment info} of experiment <param>e</param>. 
     */
    private void extractDetailedInfos(Experiment e, SampleObject rec, MigrationResult r) {
        DetailedExperimentInfo info = e.getDetailedInfo().get(rec);
        // remove previous results
        if (info != null) {
            info.getMeasurements().clear();
        } else {
            info = new DetailedExperimentInfo();
            e.getDetailedInfo().put(rec, info);
        }
        if (r == null) {
            // nothing to add
            return;
        }
        // write info of migration result to experiment's detailedInfo
        info.getMeasurements().putAll(r.getMeasurements());
        info.setSuccessful(r.isSuccessful());
        
        if (r.getReport() == null) {
            info.setProgramOutput("The tool didn't provide any output.");
        } else {
            info.setProgramOutput(r.getReport());
        }
        
        int sizeMigratedObject = (r.getMigratedObject() == null || r.getMigratedObject().getData() == null) ? 0 : r.getMigratedObject().getData().getSize();
        
        // if the executing programme claims to have migrated the object, but the result file has size 0 than something must have
        // gone wrong. so we set the migration result to 'false' and add some text to the program output.
        if (r.isSuccessful() && sizeMigratedObject == 0) {
           info.setSuccessful(false);
           String programOutput = info.getProgramOutput();
           
           programOutput += "\nSomething went wrong during migration. No result file has been generated.";
           
           info.setProgramOutput(programOutput);
        }
    }
    

    
    /**
     * Extracts object properties of all experiment results. 
     */
    public void extractObjectProperties(){
        List<SampleObject> records = selectedPlan.getSampleRecordsDefinition().getRecords();
        XcdlExtractor extractor = new XcdlExtractor();
        
        boolean missingResultfiles = false;
        ArrayList<String> failed = new ArrayList<String>();
        
        for(Alternative alt : consideredAlternatives) {
            for (SampleObject record : records) {
                // each experiment has one experiment result per sample record
                DigitalObject result = alt.getExperiment().getResults().get(record);
                
                XcdlDescription xcdl = null;
                if ((result != null)&&(result.isDataExistent())) {
                    try {
                        String filepath = tempFiles.get(result);
                        if ((filepath != null) && (!"".equals(filepath))) {
                            xcdl = extractor.extractProperties(result.getFullname(), filepath);
                        } else {
                            // we have to merge it back into the session because we the data bytestream
                            // is lazy loaded
                            DigitalObject u = (DigitalObject)em.merge(result);
                            xcdl = extractor.extractProperties(u);
                            // Should we call System.gc afterwards?
                        }
                        // there should be a file now
                        if (xcdl == null) { 
                            failed.add(alt.getName()+ ", " + record.getFullname() + ": The description service returned an empty result.");
                        }                                               
                    } catch (PlatoServiceException e) {
                        failed.add(alt.getName()+ ", " + record.getFullname() + ": " + e.getMessage()); 
                    }
                } else {
                    if (record.isDataExistent()) {
                        // The sample record has values, so there should be some result files
                        missingResultfiles = true;                        
                    }
                }
                result.setXcdlDescription(xcdl);
            }
        }
        if (missingResultfiles) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_INFO, "Some result files could not be described, because they are missing. Please upload them first.");            
        }
        if (failed.size() > 0) {
            StringBuffer msg = new StringBuffer();
            msg.append("Description failed for following result files:<br/><br/>");
            msg.append("<ul>");
            for (String f : failed) {
                msg.append("<li>").append(f).append("</li>");
            }
            msg.append("</ul>");
            
            FacesMessages.instance().add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Some result files could not be decribed successfully.", msg.toString()));
        }
        if ((!missingResultfiles)&&(failed.size() == 0)) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_INFO, "Successfully described all result files.");
        }
        
    }
    public void selectEmulationAlternative(Object alt){
        emulationAlternative = (Alternative)alt;
    }
    public void runEmulation(Object rec){
        if (rec instanceof SampleObject) {
            SampleObject sample = (SampleObject)rec;
            Alternative a = emulationAlternative;
            if (!a.isExecutable()) {
                // this alternative has to be evaluated manually, nothing to do here
                return;
            }
            IPreservationAction action =
                PreservationActionServiceFactory.getPreservationAction(a.getAction());
            /*
             * clear old run description
             */
            if (action == null) {
                
                String msg = String.format("Preservation action %s - %s is not registered or accessible and cant be executed. (Please check the registry.)",
                        a.getAction().getShortname(), a.getAction().getInfo());
                
                setUniformProgramOutput(a, msg, false);
            } 
            
            if (action instanceof IEmulationAction) {
//              GRATE does only work on files up to 2.88 MB in size
//                if (sample.getData().getSize() > (1024 * 1024 * 2.8)) {
//                    FacesMessages.instance().add(FacesMessage.SEVERITY_WARN,
//                            "Emulation actions are currently only supported on samples up to a size of 2,88 MB.");
//                    return;
//                }
                try {
                    
                    DetailedExperimentInfo info = a.getExperiment().getDetailedInfo().get(sample);
                    String sessionID = null;
                    if (info == null) {
                        info = new DetailedExperimentInfo();                            
                        a.getExperiment().getDetailedInfo().put(sample, info);
                    } else {
                        Value sid = info.getMeasurements().get("sessionid").getValue();
                        if (sid != null && (sid instanceof FreeStringValue)) {
                       //     sessionID = ((FreeStringValue)sid).getValue();
                        }
                    }
                    
                    // objectTomigrate is only being read, needs to be merged to lazily get the data out
                    SampleObject objectToView = em.merge(sample);
                    
                    byte[] b = objectToView.getData().getData();
                    
                    if (sessionID == null) {
                        sessionID = ((IEmulationAction)action).startSession(a.getAction(), objectToView);
                    }
                    a.getExperiment().getDetailedInfo().get(sample).setSuccessful(true);
                    // we cannot use SETTINGS here because settings are not PER SAMPLE OBJECT!
                    info.getMeasurements().put("sessionid", new Measurement("sessionID",sessionID));
                } catch (PlatoServiceException e) {
                    String errorMsg = "Could not start emulation service." + e.getMessage();
                    setUniformProgramOutput(a, errorMsg, false);
                    FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR,
                            "Could not start emulation service." + e.getMessage());
                    log.error("Could not start emulation service." + e.getMessage() + ": ", e.getCause());
                }
            }
        }
    }
    
    /**
     * ensures that there is a detaild experiment info per sample object
     */
    public void updateSelectedExperimentInfo(Object alternative, Object sampleObject) {
        
        Alternative a = (Alternative)alternative;
        SampleObject so = (SampleObject)sampleObject;
        
        DetailedExperimentInfo info = a.getExperiment().getDetailedInfo().get(sampleObject);
        
        if (info == null) {
            info = new DetailedExperimentInfo();
            a.getExperiment().getDetailedInfo().put(so, info);
        }
        
        this.selectedExperimentInfo = info;

        if (this.selectedExperimentInfo.getProgramOutput() == null) {
            this.selectedExperimentInfo.setProgramOutput("");
        }        
    }
   
    public void saveDetailedExperimentInfo() {
        // nothing to do here
    }

    /**
     * @param object
     * @return null
     */
    public String characteriseFits(Object object) {
        if (fits == null) {
            log.debug("FITS is not available and needs to be reconfigured.");
            return null;
        }
        if(object instanceof DigitalObject){
            DigitalObject dObject = (DigitalObject)object;
            if (dObject != null && dObject.isDataExistent()) {
                try {
                    String fitsXML = null;
                    String filepath =  tempFiles.get(dObject);
                    if ((filepath != null) && (!"".equals(filepath))) {
                        fitsXML = fits.characterise(new File(filepath));
                    } else {
                        DigitalObject mergedObj = em.merge(dObject);
                        saveTempFile(mergedObj, dObject);
                        filepath =  tempFiles.get(dObject);
                        fitsXML = fits.characterise(new File(filepath));
                    }
                    dObject.setFitsXMLString(fitsXML);
                } catch (PlatoServiceException e) {
                    log.error("characterisation with FITS failed.",e);
                    return null;
                }
            }
        }
    
        return null;
    }
    
}
