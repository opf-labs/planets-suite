/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
/**
 * 
 */
package eu.planets_project.tb.gui.backing;

import org.apache.myfaces.custom.tree2.TreeNodeBase;

/**
 * @author AnJackson
 *
 */
public class CommentTreeNode extends TreeNodeBase {
    static final long serialVersionUID = 981263621092194234l;
    private String title;
    private String body;
    private String author;
    private String time;
    private String expPhase;
    
    private void initComment() {
        this.setType("comment");
        this.setDescription(this.getTitle());
    }
        
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
        this.initComment();
    }
    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }
    /**
     * @param body the body to set
     */
    public void setBody(String body) {
        this.body = body;
        this.initComment();
    }
    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }
    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
        this.initComment();
    }
    /**
     * @return the time
     */
    public String getTime() {
        return time;
    }
    /**
     * @param time the time to set
     */
    public void setTime(String time) {
        this.time = time;
        this.initComment();
    }

    /**
     * @return the expPhase
     */
    public String getExpPhase() {
        return expPhase;
    }

    /**
     * @param expPhase the expPhase to set
     */
    public void setExpPhase(String expPhase) {
        this.expPhase = expPhase;
    }
    
    

}
