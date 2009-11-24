/*
 * ObjectReference.java Created on 02 July 2007, 08:26 To change this template,
 * choose Tools | Template Manager and open the template in the editor.
 */

package eu.planets_project.ifr.core.storage.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "objectReferenceType", namespace = "http://planets-project.eu/ifr/core/storage/data", propOrder = {
        "username", "password", "workflow", "task", "objectName" })
public class ObjectReference implements Serializable {
    /** Generated. */
    private static final long serialVersionUID = 3687203096596192633L;

    private String username;

    private char[] password;

    private String workflow; // optional

    private String task; // optional

    private String objectName;

    public ObjectReference() {}

    // TODO strip out characters that are illegal in a jackrabbit path
    private static String sanitizePathElement(String elt) {
        return elt;
    }

    public String[] getPathParts() {
        List<String> elements = new ArrayList<String>();
        if (workflow != null) {
            elements.add(workflow);
        }
        if (task != null) {
            elements.add(task);
        }
        for (int i = 0; i < elements.size(); ++i) {
            elements.set(i, sanitizePathElement(elements.get(i)));
        }
        return elements.toArray(new String[elements.size()]);
    }

    public String[] getPathElements() {
        List<String> elements = new ArrayList<String>();
        if (workflow != null) {
            elements.add(workflow);
        }
        if (task != null) {
            elements.add(task);
        }
        elements.add(objectName);
        for (int i = 0; i < elements.size(); ++i) {
            elements.set(i, sanitizePathElement(elements.get(i)));
        }
        return elements.toArray(new String[elements.size()]);
    }

    public String getRelativePath() {
        StringBuilder sb = new StringBuilder();
        String[] elts = getPathElements();
        for (int i = 0; i < elts.length; ++i) {
            if (i != 0) {
                sb.append("/");
            }
            sb.append(elts[i]);
        }
        return sb.toString();
    }

    public String getAbsolutePath() {
        StringBuilder sb = new StringBuilder("/");
        sb.append(this.getRelativePath());
        return sb.toString();
    }

    /** Use default storage area */
    public ObjectReference(String username_, char[] password_,
            String objectName_) {
        username = username_;
        password = password_.clone();
        objectName = objectName_;
    }

    /** Use workflow storage area */
    public ObjectReference(String username_, char[] password_,
            String workflow_, String objectName_) {
        this(username_, password_, objectName_);
        workflow = workflow_;
    }

    /** Use task storage area */
    public ObjectReference(String username_, char[] password_,
            String workflow_, String task_, String objectName_) {
        this(username_, password_, workflow_, objectName_);
        task = task_;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public char[] getPassword() {
        // It's primitive and one-dimensional, so we can clone:
        return (char[]) password.clone();
    }

    public void setPassword(char[] password) {
        // It's primitive and one-dimensional, so we can clone:
        this.password = (char[]) password.clone();
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getWorkflow() {
        return workflow;
    }

    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }
}
