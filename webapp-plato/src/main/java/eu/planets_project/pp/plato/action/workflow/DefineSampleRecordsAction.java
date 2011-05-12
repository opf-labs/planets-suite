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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import eu.planets_project.pp.plato.action.interfaces.IDefineSampleRecords;
import eu.planets_project.pp.plato.action.interfaces.IIdentifyRequirements;
import eu.planets_project.pp.plato.action.interfaces.IWorkflowStep;
import eu.planets_project.pp.plato.bean.PrepareChangesForPersist;
import eu.planets_project.pp.plato.bean.UploadBean;
import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.ByteStream;
import eu.planets_project.pp.plato.model.DigitalObject;
import eu.planets_project.pp.plato.model.PlanState;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.model.User;
import eu.planets_project.pp.plato.model.Values;
import eu.planets_project.pp.plato.model.XcdlDescription;
import eu.planets_project.pp.plato.model.tree.Leaf;
import eu.planets_project.pp.plato.model.tree.ObjectiveTree;
import eu.planets_project.pp.plato.services.PlatoServiceException;
import eu.planets_project.pp.plato.services.characterisation.DROIDIntegration;
import eu.planets_project.pp.plato.services.characterisation.FormatHit;
import eu.planets_project.pp.plato.services.characterisation.FormatIdentification;
import eu.planets_project.pp.plato.services.characterisation.FormatIdentification.FormatIdentificationResult;
import eu.planets_project.pp.plato.services.characterisation.fits.FitsIntegration;
import eu.planets_project.pp.plato.services.characterisation.jhove.JHoveAdaptor;
import eu.planets_project.pp.plato.services.characterisation.jhove.tree.JHoveTree;
import eu.planets_project.pp.plato.services.characterisation.xcl.XcdlExtractor;
import eu.planets_project.pp.plato.util.Downloader;
import eu.planets_project.pp.plato.util.FileUtils;
import eu.planets_project.pp.plato.util.OS;
import eu.planets_project.pp.plato.util.PlatoLogger;
import eu.planets_project.pp.plato.xml.plato.StringCapsule;

/**
 * Implements actions for workflow step 'Define Sample Records'
 *
 * We use DROID to identify the record the user has uploaded. ({@link DROIDIntegration} is
 * doing the job. {@link FormatIdentification} is the information we receive from DROID.  
 *
 * @author Hannes Kulovits
 */
@Stateful
@Scope(ScopeType.SESSION)
@Name("defineSampleRecords")
@Cache(org.jboss.ejb3.cache.NoPassivationCache.class)
public class DefineSampleRecordsAction extends AbstractWorkflowStep
implements   IDefineSampleRecords {
   
    /**
     * 
     */
    private static final long serialVersionUID = -6430869564062683806L;

    @Override
    protected void doClearEm() {
        super.doClearEm();
        records = selectedPlan.getSampleRecordsDefinition().getRecords();
        
        OS.deleteDirectory(tempDir);
        tempDigitalObjects.clear();        
    }
    
    protected boolean needsClearEm() {
        return true;
    }
    private static final Log log = PlatoLogger.getLogger(DefineSampleRecordsAction.class);

    /**
     * This is our successor step. Needed for {@link #getSuccessor()}
     */
    @In(create = true)
    IIdentifyRequirements identifyRequirements;

    /**
     * SampleObject to be uploaded and added to the list of sample objects.
     */
    @Out(required=false)
    private DigitalObject sampleRecordToUpload = new DigitalObject();

    /**
     * Logged in user.
     */
    @In (required=false)
    private User user;

    @Out
    private UploadBean uploadBean = new UploadBean();
    
    /**
     * Used to remove unused records when saving. We need this list because we aske the user
     * if he/she really wants to remove the record.
     */
    private List<SampleObject> recordsToRemove = new ArrayList<SampleObject>();

    /**
     * Still needed, despite it is loaded elsewhere, because seam needs that.
     * To suppress warning (... is never read locally) we annotate
     */
    @SuppressWarnings("unused")
    @DataModel
    private List<SampleObject> records;

    /**
     * DataModelSelection for {@link #records}
     */
    @DataModelSelection
    private SampleObject record;


    @Out(required = false)
    private JHoveTree jhoveTree;
    
    private JHoveAdaptor jHoveAdaptor;
    private FitsIntegration fits;
    
    /**
     * this determines the behaviour of the remove-buttons on the pag (see
     * there) - to remove sample records from the list
     */
    @Out
    private int allowRemove = -1;

    @Out(required = false)
    private String[] possibleFormatsString;

    @Out(required = false)
    private SampleObject identifiedRecord;

    @Out(required = false)
    private Map<String, FormatHit> possibleFormats = new HashMap<String, FormatHit>();

    @Out(required = false)
    private StringCapsule selectedFormat = new StringCapsule();

    @Out(required = false)
    private String formatMessage = null;

    public DefineSampleRecordsAction() {
        requiredPlanState = new Integer(PlanState.BASIS_DEFINED);
    }

    /**
     * @see AbstractWorkflowStep#getSuccessor()
     */
    protected IWorkflowStep getSuccessor() {
        return identifyRequirements;
    }

    /**
     * Ensures that at least one record is defined befor continuing.
     *
     * @see AbstractWorkflowStep#validate(boolean)
     */
    public boolean validate(boolean showValidationErrors) {
        if (selectedPlan.getSampleRecordsDefinition().getRecords().size() == 0) {
            if (showValidationErrors) {
                FacesMessages
                        .instance()
                        .add(FacesMessage.SEVERITY_ERROR,
                                "At least one record must be added to proceed with the workflow.");
            }
            return false;
        }
       
        List<String> names = new ArrayList<String>();
        for (SampleObject record: selectedPlan.getSampleRecordsDefinition().getRecords()) {
            if (names.contains(record.getShortName())) {
                if (showValidationErrors) {
                    FacesMessages
                            .instance()
                            .add(FacesMessage.SEVERITY_ERROR,
                                    "There are two records with the same short name '"+record.getShortName()+"'. Please provide unique names.");
                }
                return false;
            }
            names.add(record.getShortName());
        }
        return true;
    }

    /**
     * saves the sample records and then calls super.save to save the rest. each
     * record with an id equal to zero has not been persited before.
     *
     * @see AbstractWorkflowStep#save()
     */
    @Override
    public String save() {
        /*
         * We need to persist the AlternativesDefinition here first, because
         * every SampleObject is used as a key for the Uploads Hashmap in the
         * Experiment of every Alternative. Therefore if one SampleObject is removed,
         * but still used as a key for the HashMap Hibernate will throw error,
         * because of foreign Key Relationship.
         *
         * So when SampleObject is removed from the System we remove it from the
         * Hashmap too, then Save all Alternatives with the Experiments and DigitalObject
         * Hashmaps -> the Sample Recordarg0 to remove is referenced nowhere in the project
         * and the SampleRecordDefinition can be saved....?
         * Or wait! One thing before that... all the Values objects have changed in #removeRecord()
         * and we have to persist these as well before deleting the sampleobject.
         * THEN the SampleRecordDefinition can be saved.
         */
        /** dont forget to prepare changed entities e.g. set current user */
        PrepareChangesForPersist prep = new PrepareChangesForPersist(user.getUsername());

        em.persist(em.merge(selectedPlan.getAlternativesDefinition()));
        for (SampleObject record : selectedPlan.getSampleRecordsDefinition()
                .getRecords()) {
            
            prep.prepare(record);
            
            if (record.getId() == 0) { // the record has not yet been persisted                                
                String filename = tempDigitalObjects.get((DigitalObject)record);
                
                try {
                    if (filename != null && filename != "") {
                        File file = new File(filename);
                        byte[] data = FileUtils.getBytesFromFile(file);
                        record.getData().setData(data);
                    }
                } catch(IOException e) {
                    log.error(e);
                }

                em.persist(record);
            } else {
                if (!recordsToRemove.contains(record)) {
                    em.persist(em.merge(record));
                }
            }
        }

        // remove temporary files from hard disk
        for (String filename : tempDigitalObjects.values()) {
            File file = new File(filename);
            file.delete();           
        }
        tempDigitalObjects.clear();
        
        // If we removed samples, persist all the Values objects of all leaves in the tree
        // - that leads to the orphan VALUE objects to be deleted from the database.
        if (recordsToRemove.size() > 0) {
            for (Leaf l: selectedPlan.getTree().getRoot().getAllLeaves()) {
                for (Alternative a: selectedPlan.getAlternativesDefinition().getConsideredAlternatives()) {
                    Values v = l.getValues(a.getName());
                    if (v != null) {
                        em.persist(em.merge(v));
                    } else {
                        log.error("values is NULL: "+l.getName()+", "+a.getName());
                    }
                }
            }
            em.flush();
        }
        
        super.save(selectedPlan.getSampleRecordsDefinition());
        
        /* these are removed by the parent entity because of DELETE_ORPHANS!
        for (SampleObject record : recordsToRemove) {
            if (record.getId() != 0) {
                // this record is already persisted - remove it
                em.remove(em.merge(record));
            }
        }
        */
        recordsToRemove.clear();
        changed = "";
        
        return null;
    }

    /**
     * Adds a new record to the list of sample records in the project. This is a sample record
     * without data.
     */
    public String newRecord() {
        SampleObject newRecord = new SampleObject();

        selectedPlan.getSampleRecordsDefinition().addRecord(newRecord);
        // this SampleRecordsDefinition has been changed
        selectedPlan.getSampleRecordsDefinition().touch();

        return null;
    }

    /**
     * Removes a record from the list of samplerecords in the project AND also
     * removes all associated:
     * <ul>
     *    <li>evaluation values contained in the tree</li>
     *    <li>experiment results and their xcdl-files</li>
     * </ul>
     * - if there are any.
     */
    public String removeRecord() {
        log.info("Removing SampleObject from Plan: " + record.getFullname());
        recordsToRemove.add(record);
        selectedPlan.removeSampleObject(record);
        
        if (record == identifiedRecord) {
            identifiedRecord = null;
            possibleFormats.clear();
            possibleFormatsString = null;
        }

        allowRemove = -1;
        return null;
    }
    
    private int addAllRecords(File file) {
        log.debug("adding record for file "+file.toString());
        if (file.isFile()) {
            try {
                sampleRecordToUpload.getData().setData(FileUtils.getBytesFromFile(file));
                sampleRecordToUpload.setFullname(file.getName());
                upload();
            } catch (IOException e) {
                log.error(e);
                FacesMessages.instance().add(FacesMessage.SEVERITY_WARN,
                   "Could not read: "+file.getName()+": "+e.getMessage());
            }
            return 1;
        } else {
            int number = 0;
            for (File f: file.listFiles()) {
                number += addAllRecords(f.getAbsoluteFile());
            }
            return number;
        }
    }

    public String addAllRecords() {
        if (uploadBean.getPath() == null || "".equals(uploadBean.getPath())) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_INFO,
                "You have to provide a directory pointing to a local path up here at the server before adding these files.");
            return null;
        }
        File directory = new File(uploadBean.getPath());
        if (!directory.isDirectory()) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_INFO,
            "You have to provide a directory pointing to a local path up here at the server before adding these files.");
            return null;
        }
        int number = addAllRecords(directory.getAbsoluteFile());
        FacesMessages.instance().add(FacesMessage.SEVERITY_INFO,
            "Added "+number+" records to your project.");
        return null;
    }
    
    private Map<DigitalObject, String> tempDigitalObjects = new HashMap<DigitalObject, String>();
    
    private File tempDir = null;
    
    /**
     * Uploads a file into a newly created sample record and adds this sample
     * record to the list in the project.
     *
     * @return always returns null
     */
    public String upload() {
        if (!sampleRecordToUpload.isDataExistent()) {

            log.debug("No file for upload selected.");
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR,
                    "You have to select a file before starting upload.");

            return null;
        }

        changed = "true";
        SampleObject record = new SampleObject();
        String fullName = new File(sampleRecordToUpload.getFullname()).getName();
        record.setFullname(fullName);
        record.setShortName(record.getFullname().substring(0,Math.min(20,record.getFullname().length())));
        record.setContentType(sampleRecordToUpload.getContentType());
        selectedPlan.getSampleRecordsDefinition().addRecord(record);
        
        writeTempFile(sampleRecordToUpload, record);

        // identify format of newly uploaded records
        if (shouldCharacterise(record)) {
            identifyFormat(record);
//              describeInXcdl(record);
              characteriseFits(record);
        }
        
        // need to initialize jhove tree by upload of a new sample record
        if (record.getJhoveXMLString() == null || "".equals(record.getJhoveXMLString())) {
           record.setJhoveXMLString(jHoveAdaptor.describe(tempDigitalObjects.get(record)));
        }
        
        log.debug("Content-Type: " + sampleRecordToUpload.getContentType());
        log.debug("Size of Records Array: "
                + selectedPlan.getSampleRecordsDefinition().getRecords()
                        .size());
        log.debug("FileName: " + sampleRecordToUpload.getFullname());
        log.debug("Length of File: " + sampleRecordToUpload.getData().getSize());
        log.debug("added SampleObject: " + record.getFullname());
        log.debug("JHove initialized: " + (record.getJhoveXMLString() != null));
        
        sampleRecordToUpload.setData(new ByteStream());
        
        System.gc();
        
        return null;
    }
    
    /**
     * For some objects (such as raw camera files), calling characterisation tools is useless and
     * needs resources. This function tells us if we should attempt characterisation.
     * @param record SampleObject to be checked
     * @return true if object should be characterised, false if it's better not to do that
     */
    private boolean shouldCharacterise(SampleObject record) {
        String fullName = record.getFullname();
        if (fullName.toUpperCase().endsWith(".CR2") ||
            fullName.toUpperCase().endsWith(".NEF") ||
            fullName.toUpperCase().endsWith(".CRW")) {
            return false;
        }
        return true;
    }

    /**
     * 
     * @param dataObject is currently holding the byte stream
     * @param sampleObject the things that is going to be stored in the database
     * @return
     */
    private void writeTempFile(DigitalObject dataObject, SampleObject sampleObject) {

        String filename = dataObject.getFullname();
        String fileExtension = "";
        int bodyEnd = filename.lastIndexOf(".");
        if (bodyEnd >= 0) {
            fileExtension = filename.substring(bodyEnd);
        }
        
        String tempFileName = tempDir.getAbsolutePath()+System.nanoTime() + fileExtension;
        
        OutputStream fileStream;
        try {
            fileStream = new  BufferedOutputStream (new FileOutputStream(tempFileName));
            byte[] data = dataObject.getData().getData();
            fileStream.write(data);
            fileStream.close();
            
            // put the temp file in the map
            tempDigitalObjects.put(sampleObject, tempFileName);
            sampleObject.getData().setSize(data.length);
            
        } catch (FileNotFoundException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
        }
    }

    /**
     * @see AbstractWorkflowStep#discard()
     */
    @Override
    @RaiseEvent("reload")
    public String discard() {
        String result = super.discard();
        recordsToRemove.clear();

        // delete all temporary files from hard disk
        for (DigitalObject o: tempDigitalObjects.keySet()) {
            File file = new File(tempDigitalObjects.get(o));
            file.delete();
        }
        tempDigitalObjects.clear();        
        
        // prepare the records
        init();
        return result;
    }

    /**
     * @see AbstractWorkflowStep#init()
     */
    @Override
    public void init() {
        if (tempDir != null) {
            OS.deleteDirectory(tempDir);
        }
        tempDir = new File(OS.getTmpPath() + "sampleobjects" + System.nanoTime()+File.separator);
        tempDir.mkdir();
        tempDir.deleteOnExit();
        tempDigitalObjects.clear();
        
        allowRemove = -1;
        jhoveTree = new JHoveTree();
        jHoveAdaptor=new JHoveAdaptor();
        
        try {
            fits = new FitsIntegration();
        } catch (Throwable e) {
            fits = null;
            log.error("Could not instantiate FITS, it is not configured properly.", e);
            FacesMessages.instance().add(FacesMessage.SEVERITY_WARN, "Could not instantiate FITS, it is not configured properly.");
        }
     
        records = selectedPlan.getSampleRecordsDefinition().getRecords();
        boolean updated = false;
        for (SampleObject record : records) {
            if (record.getJhoveXMLString() == null || "".equals(record.getJhoveXMLString())) {
                record.setJhoveXMLString(new JHoveAdaptor().describe(em.merge(record)));
                updated = true;
            }
        }
        if (updated) {
            em.persist(em.merge(selectedPlan.getSampleRecordsDefinition()));
        }
        
        identifiedRecord = null;
        possibleFormats.clear();
        possibleFormatsString = null;
    }

    /**
     * checks if the record contains evaluation values. If yes, the user should
     * be asked for confirmation before removing it. If not, the record is
     * removed. *
     *
     * @see ObjectiveTree#hasValues(int[],Alternative)
     *
     * @return always returns null
     */
    public String askRemoveRecord() {
        if (record == null
                || selectedPlan.getSampleRecordsDefinition().getRecords()
                        .size() == 0) {
            allowRemove = -1;
            return null;
        }

        int rec[] = { selectedPlan.getSampleRecordsDefinition().getRecords()
                .indexOf(record) };
        
        // we need to construct the list of all altenative names because the tree doesnt know it
        Set<String> alternatives = new HashSet<String>();
        for (Alternative a: selectedPlan.getAlternativesDefinition().getConsideredAlternatives()) {
            alternatives.add(a.getName());
        }
        
        if (selectedPlan.getTree().hasValues(rec, alternatives)) {
            allowRemove = record.getId();
        } else {
            removeRecord();
        }
        return null;
    }

    public int getAllowRemove() {
        return allowRemove;
    }

    /**
     * @see AbstractWorkflowStep#destroy()
     */
    @Destroy
    @Remove
    public void destroy() {
    }

    /**
     * @see AbstractWorkflowStep#getWorkflowstepName()
     */
    protected String getWorkflowstepName() {
        return "defineSampleRecords";
    }

    /**
     * Downloads currently selected sample record. Uses {@link eu.planets_project.pp.plato.util.Downloader}
     * to perform the download.
     */
    public void download(Object object) {
        if (object instanceof DigitalObject) {
            DigitalObject dObject = (DigitalObject) object;
            if (tempDigitalObjects.containsKey(dObject)) {
                Downloader.instance().download(dObject,tempDigitalObjects.get(object));
            } else {
                DigitalObject merged = em.merge(dObject);
                Downloader.instance().download(merged);
            }
        }
    }

    /**
     * Sets the format of SampleObject {@link #identifiedRecord} to the currently selected format
     * of all {@link #possibleFormats}.
     */
    public void changeFormat(){
        if ((selectedFormat.getValue() != null) && (identifiedRecord != null)) {
            FormatHit hit = possibleFormats.get(selectedFormat.getValue());
            if (hit != null) {
                identifiedRecord.getFormatInfo().assignValues(hit.getFormat());
                identifiedRecord.touch();
//                FacesContext context = FacesContext.getCurrentInstance();
//                context.renderResponse();
                log.warn("format changed to: "+selectedFormat.getValue());
            }
        }
        
      }

    /**
     * Selects The last selected format already set by {@link #changeFormat(Object)}.
     */
    public void selectFormat(){
        identifiedRecord = null;
        possibleFormatsString = null;
        formatMessage = null;
    }

    /**
     * Identifies the format of the uploaded sample record <param>object</param> with the help of DROID.
     * <ul>
     *    <li>If there is an exact match, the new format is set.</li>
     *    <li>If there there are only tentative hits, the list {@link #possibleFormats} is populated and the user has to choose.</li>
     *    <li>If format identification fails {@link #formatMessage} is set.</li>
     * </ul>
     *
     * @param object SampleObject to be identified. Must be of type SampleObject at the moment.
     *
     */
    public void identifyFormat(Object object) {
        
        formatMessage = null;
        
        if (! (object instanceof SampleObject))
            return;
        try {
            
            SampleObject rec = (SampleObject)object;
            identifiedRecord = rec;            
            possibleFormats.clear();
            possibleFormatsString = null;
            
            /**
             * DROID is used for file identification.
             */
            FormatIdentification ident = null;
            
            String filename = tempDigitalObjects.get(rec);
            
            if (filename == null || "".equals(filename)) {
                SampleObject rec2 = em.merge(rec);
                ident = DROIDIntegration.getInstance().identifyFormat(rec2.getData().getData(), rec2.getFullname());    
            } else {
                ident = DROIDIntegration.getInstance().identify(filename);
            }
            
            if (ident.getResult() == FormatIdentificationResult.ERROR) {
                /*
                 * DROID could not identify this file.
                 */
                log.error(ident.getInfo());
                formatMessage = "DROID could not identify the format of the file." + ident.getInfo();
            } else if (ident.getResult() == FormatIdentificationResult.NOHIT) {
                /*
                 * DROID could not identify this file.
                 */
                log.info(ident.getInfo());
                formatMessage = "DROID could not identify the format of the file." + ident.getInfo();
            } else if ((ident.getResult() == FormatIdentificationResult.POSITIVE) &&
                    (ident.getFormatHits().size() == 1)){
                /*
                 *  exact match, format identification successful
                 */
                rec.getFormatInfo().assignValues(ident.getFormatHits().get(0).getFormat());
                rec.touch();
                formatMessage = ident.getInfo();
            } else {
                /*
                 * Here, we either have more than one POSITIVE hit or any number of TENTATIVE hits.
                 * DROID provides only some guesses as it cannot identify it, the user has to decide.
                 */
                formatMessage = "Droid identified several potential formats of this file. " + ident.getInfo() + " Please choose the most adequate one:";

                for (FormatHit hit : ident.getFormatHits()){
                    String key = hit.getFormat().getName();
                    if (hit.getFormat().getVersion() != null)
                        key = key + ' ' + hit.getFormat().getVersion();

                    possibleFormats.put(key, hit);
                }
                
                possibleFormatsString = possibleFormats.keySet().toArray(new String[]{});
                // set format info to first entry in the list - as the drop down box suggests
                selectedFormat.setValue(possibleFormatsString[0]);
                rec.getFormatInfo().assignValues(possibleFormats.get(selectedFormat.getValue()).getFormat());
                rec.touch();

                FacesMessages.instance().add(FacesMessage.SEVERITY_INFO, ident.getInfo());
            }
        } catch (Exception e) {
            /*
             * An error occured, maybe it would help to try it again.
             */
            log.error("Failed to identify fileformat." + e);
            formatMessage = "Could not identify the format of the file.";

        }
    }

    public DigitalObject getSampleRecordToUpload() {
        return sampleRecordToUpload;
    }

    public void setSampleRecordToUpload(DigitalObject sampleRecord) {
        this.sampleRecordToUpload = sampleRecord;
    }
    
    public String characteriseXcdl(Object object) {
        if (object instanceof DigitalObject) {
            
            describeInXcdl((DigitalObject)object);
        }
        
        return "";
    }

    private boolean describeInXcdl(DigitalObject record) {
        XcdlExtractor extractor = new XcdlExtractor();
        XcdlDescription xcdl = null;
        if (record != null && record.isDataExistent()) {
            try {
                String filepath =  tempDigitalObjects.get(record);
                if ((filepath != null) && (!"".equals(filepath))) {
                    xcdl = extractor.extractProperties(record.getFullname(), filepath);
                } else {
                    DigitalObject rec2 = em.merge(record);
                    xcdl = extractor.extractProperties(rec2);
                }
                
                //xcdl = extractor.extractProperties(record.getFullname(), record);
            } catch (PlatoServiceException e) {
                log.error("XCDL characterisation failed: "+e.getMessage(),e);
                return false;
            }
        }
        if (xcdl != null) { 
            // extraction succeeded
            record.setXcdlDescription(xcdl);
            return true;
        } else {
            // property extraction failed, remove old xcdl info
            record.setXcdlDescription(null);
            return false;
        }
    }
    
    /**
     * Extracts object properties of all sample records. 
     */
    public void extractObjectProperties(){
        List<SampleObject> records = selectedPlan.getSampleRecordsDefinition().getRecords();
        ArrayList<String> failed = new ArrayList<String>();
        
        for (SampleObject record : records) {
            if (!describeInXcdl(record)) {
                failed.add(record.getFullname() + ": The description service returned an invalid result.");  
            }
        }
        if (failed.size() == 0) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_INFO, "Successfully described all sample records.");
        } else {
            StringBuffer msg = new StringBuffer();
            msg.append("Description failed for following sample records:<br/><br/>");
            msg.append("<ul>");
            for (String f : failed) {
                msg.append("<li>").append(f).append("</li>");
            }
            msg.append("</ul>");
            
            FacesMessages.instance().add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Some sample records could not be decribed successfully.", msg.toString()));
        }
        
    }
    /**
     * Creates from the SampleObject object, retrieved by injection, a Tree with
     * all the characteristics extracted from JhoveXMLString present in the record
     * 
     */
    public String characteriseJHoveTree(Object object) {
        if(object instanceof SampleObject){
            SampleObject tmpRec = (SampleObject)object;
            if (tmpRec != null) {
                jhoveTree=jHoveAdaptor.digestString(tmpRec.getFullname(), tmpRec.getJhoveXMLString());
            }
        }
       
        return null;
    }

    public String characteriseFits(Object object) {
        if (fits == null) {
            log.debug("FITS is not available and needs to be reconfigured.");
            return null;
        }
        if(object instanceof SampleObject){
            SampleObject sample = (SampleObject)object;
            if (sample != null && sample.isDataExistent()) {
                try {
                    String fitsXML = null;
                    String filepath =  tempDigitalObjects.get(sample);
                    if ((filepath != null) && (!"".equals(filepath))) {
                        fitsXML = fits.characterise(new File(filepath));
                    } else {
                        SampleObject mergedObj = em.merge(sample);
                        writeTempFile(mergedObj, sample);
                        filepath =  tempDigitalObjects.get(sample);
                        fitsXML = fits.characterise(new File(filepath));
                    }
                    sample.setFitsXMLString(fitsXML);
                    identifiedRecord = sample;
                } catch (PlatoServiceException e) {
                    log.error("characterisation with FITS failed.",e);
                    return null;
                }
            }
        }
       
        return null;
    }
}
