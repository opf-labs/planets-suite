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
 * A base class for querying a digital object manager.
 * This simple class just declares the known types of query.
 * 
 * @author AnJackson
 */
public class Query {

    /** The simplest query type is a straightforward query string. */
    public static final Class<? extends Query> STRING = QueryString.class;
    
    /** The a query type is just a data range. */
    public static final Class<? extends Query> DATE_RANGE = QueryDateRange.class;
    
}
