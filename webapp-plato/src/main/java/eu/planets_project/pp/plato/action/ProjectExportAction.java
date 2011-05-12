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

package eu.planets_project.pp.plato.action;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.XMLWriter;
import org.jboss.annotation.ejb.cache.Cache;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import sun.misc.BASE64Encoder;
import eu.planets_project.pp.plato.model.ByteStream;
import eu.planets_project.pp.plato.model.DigitalObject;
import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.model.PlanProperties;
import eu.planets_project.pp.plato.util.FileUtils;
import eu.planets_project.pp.plato.util.OS;
import eu.planets_project.pp.plato.util.PlatoLogger;
import eu.planets_project.pp.plato.xml.ProjectExporter;

/**
 * This class inserts test data into the persistence layer, including import of
 * objective trees from case studies.
 *
 * @author Christoph Becker
 */
@Stateful
@Scope(ScopeType.EVENT)
@Name("projectExport")
@Cache(org.jboss.ejb3.cache.NoPassivationCache.class)
public class ProjectExportAction implements Serializable, IProjectExport {

    private static final long serialVersionUID = 2155152208617526555L;

    private static final Log log = PlatoLogger.getLogger(ProjectExportAction.class);

    @PersistenceContext
    EntityManager em;

    @Remove
    @Destroy
    public void destroy() {

    }

    /**
     * Exports all projects into separate xml files and adds them to a zip archive.  
     * @return null Always returns null, so user stays on same screen after action performed
     */
    public String exportAllProjectsToZip(){
        List<PlanProperties> ppList = em.createQuery("select p from PlanProperties p").getResultList();

        if (!ppList.isEmpty()){
            log.debug("number of plans to export: "+ppList.size());
            String filename = "allprojects.zip";

            String exportPath = OS.getTmpPath() + "export" + System.currentTimeMillis()+"/";
            new File(exportPath).mkdirs();
            
            String binarydataTempPath = exportPath + "binarydata/";
            File binarydataTempDir = new File(binarydataTempPath);
            binarydataTempDir.mkdirs();
            
            try {
                OutputStream out = new BufferedOutputStream(new FileOutputStream(exportPath + filename));
                ZipOutputStream zipOut = new ZipOutputStream(out);
                
                for (PlanProperties pp: ppList) {
                    log.debug("EXPORTING: " + pp.getName());
                    ZipEntry zipAdd = new ZipEntry(String.format("%1$03d", pp.getId())+"-"+ FileUtils.makeFilename(pp.getName())+".xml");
                    zipOut.putNextEntry(zipAdd);
                    // export the complete project, including binary data
                    exportComplete(pp.getId(), zipOut, binarydataTempPath);
                    zipOut.closeEntry();
                }
                zipOut.close();
                out.close();
                new File(exportPath + "finished.info").createNewFile();
                
                FacesMessages.instance().add(FacesMessage.SEVERITY_INFO, "Export was written to: " + exportPath);
                log.info("Export was written to: " + exportPath);
            } catch (IOException e) {
                FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "An error occured while generating the export file.");
                log.error("An error occured while generating the export file.", e);
                File errorInfo = new File(exportPath + "error.info");
                try {
                    Writer w = new FileWriter(errorInfo);
                    w.write("An error occured while generating the export file:");
                    w.write(e.getMessage());
                    w.close();
                } catch (IOException e1) {
                    log.error("Could not write error file.");
                }
                
            } finally {
                // remove all binary temp files
                OS.deleteDirectory(binarydataTempDir);
            }
            
        } else {
            FacesMessages.instance().add("No Projects found!");
        }
        return null;
    }
    
    /**
     * Exports the project identified by PlanProperties.Id ppid and writes the document
     * to the given OutputStream - including all binary data.
     * (currently required by {@link #exportAllProjectsToZip()} )
     * - Does NOT clean up temp files written to baseTempPath
     * 
     * @param ppid
     * @param out
     * @param baseTempPath used to write temp files for binary data, 
     *        must not be used by other exports at the same time
     */
    public void exportComplete(int ppid, OutputStream out, String baseTempPath) {
        BASE64Encoder encoder = new BASE64Encoder();
        ProjectExporter exporter = new ProjectExporter();
        Document doc = exporter.createProjectDoc();

//        int i = 0;
        List<Plan> list = null;
        try {
            list = em.createQuery(
                    "select p from Plan p where p.planProperties.id = "
                            + ppid).getResultList();
        } catch (Exception e1) {
            list = new ArrayList<Plan>();
            FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "An error occured while generating the export file.");
            log.error("Could not load planProperties: ", e1);
        }
        try {
            if (list.size() != 1) {
                FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR,
                        "Skipping the export of the plan with properties"+ppid+": Couldnt load.");
            } else {
                //log.debug("adding project "+p.getplanProperties().getName()+" to XML...");
                String tempPath = baseTempPath;
                File tempDir = new File(tempPath);
                tempDir.mkdirs();

                List<Integer> uploadIDs = new ArrayList<Integer>();
                List<Integer> recordIDs = new ArrayList<Integer>();
                try {
                    exporter.addProject(list.get(0), doc, uploadIDs, recordIDs);
                    
                    writeBinaryObjects(recordIDs, uploadIDs, tempPath, encoder);
                    // perform XSLT transformation to get the DATA into the PLANS
                    XMLWriter writer = new XMLWriter(new FileOutputStream("/tmp/testout"+System.currentTimeMillis()+".xml"));
                    writer.write(doc);
                    writer.close();
                    addBinaryData(doc, out, tempPath);
                } catch (IOException e) {
                    FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "An error occured while generating the export file.");
                    log.error("Could not open response-outputstream: ", e);
                } catch (TransformerException e) {
                    FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, "An error occured while generating the export file.");
                    log.error(e);
                }
            }
        } finally {
            /* clean up */
            list.clear();
            list = null;

            em.clear();
            System.gc();
        }

    }

    /**
     *  Performs XSLT transformation to get the DATA into the PLANS
     */
    private void addBinaryData(Document doc, OutputStream out, String aTempDir) throws TransformerException {
        URL xslPath = Thread.currentThread().getContextClassLoader().getResource("data/xslt/bytestreams.xsl");
        InputStream xsl = Thread.currentThread().getContextClassLoader().getResourceAsStream("data/xslt/bytestreams.xsl");
        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer(new StreamSource(xsl));
        transformer.setParameter("tempDir", aTempDir);

        Source xmlSource = new DocumentSource(doc);
    
        Result outputTarget = new StreamResult(out); //new FileWriter(outFile));
        
        log.debug("starting bytestream transformation ...");
        transformer.transform(xmlSource, outputTarget);
        log.debug("FINISHED bytestream transformation!");
    }
    
    /**
     * Adds all enlisted plans to an XML document, but does NOT write binary data.
     * Instead the Id's of all referenced uploads and sample records are added to the provided lists,
     * this way they can be added later.
     *   
     * @param ppids
     * @param uploadIDs
     * @param recordIDs
     * @return
     */
    public Document exportToXml(List<Integer> ppids, List<Integer> uploadIDs, List<Integer> recordIDs) {
        ProjectExporter exporter = new ProjectExporter();
        Document doc = exporter.createProjectDoc();

        int i = 0;
        for (Integer id: ppids) {
            // load one plan after the other:
            List<Plan> list = em.createQuery(
                    "select p from Plan p where p.planProperties.id = "
                            + id).getResultList();
            if (list.size() != 1) {
                FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR,
                        "Skipping the export of the plan with properties"+id+": Couldnt load.");
            } else {
                //log.debug("adding project "+p.getplanProperties().getName()+" to XML...");
                exporter.addProject(list.get(0), doc, uploadIDs, recordIDs);
            }
            list.clear();
            list = null;
            
            log.info("XMLExport: addString destinationed project ppid="+id);
            i++;
            if ((i%10==0)) {
                em.clear();
                System.gc();
            }
        }
        return doc;
    }

    /**
     * Loads all binary data for the given samplerecord- and upload Ids and dumps it to XML files,  located in tempDir
     *       
     * @param recordIDs
     * @param uploadIDs
     * @param tempDir
     * @param encoder
     * @throws IOException
     */
    private void writeBinaryObjects(List<Integer> recordIDs, List<Integer> uploadIDs, String aTempDir, BASE64Encoder encoder) throws IOException {
        int counter = 0;
        int skip = 0;
        List<Integer> allIDs = new ArrayList<Integer>();
        allIDs.addAll(recordIDs);
        allIDs.addAll(uploadIDs);
        log.info("writing XMLs for bytestreams of digital objects. Size = "+allIDs.size());
        for (Integer id : allIDs) {
            if (counter > 200*1024*1024) { // 200 MB unused stuff lying around
                System.gc();
                counter = 0;
            }
            DigitalObject object = em.find(DigitalObject.class, id);
            if (object.isDataExistent()) {
                counter += object.getData().getSize();
                File f = new File(aTempDir+object.getId()+".xml");
                writeBinaryData(id, object.getData(), f, encoder);
           } else {
               skip++;
           }
           object = null;
        }
        em.clear();
        System.gc();
        log.info("finished writing bytestreams of digital objects. skipped empty objects: "+skip);
    }
    
    /**
     * Dumps binary data to provided file
     * It results in an XML file with a single element: data, 
     * @param id
     * @param data
     * @param f
     * @param encoder
     * @throws IOException
     */
    private static void writeBinaryData(int id, ByteStream data, File f, BASE64Encoder encoder) throws IOException {
        Document streamDoc = DocumentHelper.createDocument();
        Element d = streamDoc.addElement("data");
        d.addAttribute("id", ""+id);
        d.setText(encoder.encode(data.getData()));
        XMLWriter writer = new XMLWriter(new BufferedWriter(new FileWriter(f)), ProjectExporter.prettyFormat);
        writer.write(streamDoc);
        writer.flush();
        writer.close();
    }

    
}
