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

package eu.planets_project.pp.plato.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.jboss.annotation.ejb.cache.Cache;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Synchronized;

import eu.planets_project.pp.plato.action.interfaces.IMessages;

/**
 * Class holding different kinds of messages:
 * <ul>
 *   <li>Error messages. Unexpected errors that occurred during a session. Administrator can
 *   look at them on admin utils site.</li>
 *
 *   <li>News messages. Messages entered by the Administrator intended to the users.</li>
 * <ul>
 * @author Hannes Kulovits
 *
 */
@Scope(ScopeType.APPLICATION)
@Name("allmessages")
@Stateful
@Synchronized
public class Messages implements IMessages, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1279717210536246508L;

    /**
     * List holding error messages.
     */
    private List<ErrorClass> errors = new ArrayList<ErrorClass>();

    /**
     * List holding news messages.
     */
    private List<NewsClass> news = new ArrayList<NewsClass>();

    /**
     * Removes all error messages from list.
     */
    public void clearErrors() {
        this.errors.clear();
    }

    /**
     * Removes all news messages from list.
     */
    public void clearNews() {
        this.news.clear();
    }

    @Destroy
    @Remove
    public void destroy() {
    }

    /**
     * Adds error message to list.
     *
     * @param errorType type of error. In case of an exception this is the exception class.
     * @param errorMessage error message.
     * @param id session id
     * @see eu.planets_project.pp.plato.application.ErrorClass#ErrorClass(String, String, String)
     */
    public void addErrorMessage(ErrorClass error) {
        errors.add(0, error);
    }

    /**
     * Adds news message to list.
     *
     * @param news message text
     * @param importance importance level of the message. can be chosen arbitrarily.
     * @param author author of the news message
     */
    public void addNewsMessage(NewsClass news) {
        this.news.add(0, news);
    }

    public List<ErrorClass> getErrors() {
        return errors;
    }

    public List<NewsClass> getNews() {
        return news;
    }
}
