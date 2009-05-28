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
public class QueryString extends Query {

    String query = null;

    /**
     * @param query the query
     */
    public QueryString(String query) {
        super();
        this.query = query;
    }
    

    /**
     * @param query the query to set
     */
    public void setQuery(String query) {
        this.query = query;
    }
    
    /**
     * @return the query
     */
    public String getQuery() {
        return query;
    }

}
