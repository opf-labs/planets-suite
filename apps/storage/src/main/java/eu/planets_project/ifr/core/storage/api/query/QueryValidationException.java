/**
 * Copyright (c) 2007, 2008 The Planets Project Partners.
 * 
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * GNU Lesser General Public License v3 which 
 * accompanies this distribution, and is available at 
 * http://www.gnu.org/licenses/lgpl.html
 * 
 */
package eu.planets_project.ifr.core.storage.api.query;

/**
 * @author AnJackson
 *
 */
public class QueryValidationException extends Exception {

    /***/
    private static final long serialVersionUID = 4956266507595623621L;

    /**
     * Create a query validation exception.
     */
    public QueryValidationException() {
        super();
    }

    /**
     * @param message the message
     * @param cause the cause
     */
    public QueryValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message the message
     */
    public QueryValidationException(String message) {
        super(message);
    }

    /**
     * @param cause the cause
     */
    public QueryValidationException(Throwable cause) {
        super(cause);
    }

}
