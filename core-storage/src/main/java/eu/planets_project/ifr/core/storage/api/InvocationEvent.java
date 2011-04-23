/*
 * WorkflowEvent.java Event for a Web service invocation during a workflow
 * execution
 */

package eu.planets_project.ifr.core.storage.api;

import java.net.URI;
import java.util.Date;

public class InvocationEvent implements WorkflowEvent {

    private String id = null;
    private URI service = null;
    private String operation = null;
    private URI inFile = null;
    private URI outFile = null;
    private Date start = null;
    private Date end = null;

    /**
	 * 
	 */
    public InvocationEvent() {}

    /**
     * @param service
     * @param operation
     * @param in
     * @param out
     * @param start
     * @param end
     */
    public InvocationEvent(String id, URI service, String operation, URI in,
            URI out, Date start, Date end) {
        this.id = id;
        this.service = service;
        this.operation = operation;
        this.inFile = in;
        this.outFile = out;
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
    }

    /**
     * @return
     */
    public String getId() {
        return this.id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param service
     */
    public void setService(URI service) {
        this.service = service;
    }

    /**
     * @return
     */
    public URI getService() {
        return service;
    }

    /**
     * @param op
     */
    public void setOperation(String op) {
        this.operation = op;
    }

    /**
     * @return
     */
    public String getOperation() {
        return operation;
    }

    /**
     * @param in
     */
    public void setInFile(URI in) {
        this.inFile = in;
    }

    /**
     * @return
     */
    public URI getInFile() {
        return inFile;
    }

    /**
     * @param out
     */
    public void setOutFile(URI out) {
        this.outFile = out;
    }

    /**
     * @return
     */
    public URI getOutFile() {
        return outFile;
    }

    /**
     * @param start
     */
    public void setStartDate(Date start) {
        this.start = new Date(start.getTime());
    }

    public Date getStartDate() {
        return new Date(start.getTime());
    }

    /**
     * @param end
     */
    public void setEndDate(Date end) {
        this.end = new Date(end.getTime());
    }

    /**
     * @return
     */
    public Date getEndDate() {
        return new Date(end.getTime());
    }
}