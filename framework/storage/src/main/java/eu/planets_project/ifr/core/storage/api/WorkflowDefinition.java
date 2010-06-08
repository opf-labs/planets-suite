/*
 * WorkflowDefinition.java The class encapsulates a workflow definition
 */

package eu.planets_project.ifr.core.storage.api;

import java.util.Date;

import org.w3c.dom.Document;

/**
 * @author CFwilson
 * @author Rainer Schmidt
 */
public class WorkflowDefinition {

    private String id = null;
    private String owner = null;
    private String version = null;
    private String description = null;
    // latest change
    private Date date = null;
    // BPEL document, or XML Preservation Plan
    Document document = null;

    /**
	 * 
	 */
    public WorkflowDefinition() {
        date = new Date();
    }

    /**
     * @param id
     * @param owner
     * @param document
     */
    public WorkflowDefinition(String id, String owner, Document document) {
        this.id = id;
        this.owner = owner;
        this.document = document;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * @param owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * @return
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @param version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param dsc
     */
    public void setDescription(String dsc) {
        this.description = dsc;
    }

    /**
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param date
     */
    public void setDate(Date date) {
        this.date = new Date(date.getTime());
    }

    /**
     * @return
     */
    public Date getDate() {
        return new Date(date.getTime());
    }

    /**
     * @param doc
     */
    public void setDocument(Document doc) {
        this.document = doc;
    }

    /**
     * @return
     */
    public Document getDocument() {
        return document;
    }
}
