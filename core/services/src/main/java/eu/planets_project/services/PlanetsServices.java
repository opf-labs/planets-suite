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
package eu.planets_project.services;

/**
 * @author AnJackson
 *
 */
public class PlanetsServices {


    public static final String PLANETS_NS = "http://planets-project.eu";


    /**
     * The namespace for the planets digital objects, and all the
     * contained datastructures
     * @see #PLANETS_NS
     */
    public static final String OBJECTS_NS = PLANETS_NS +"/objects";


    /**
     * The namespace for all planets services, and service interfaces
     * and datastructures like ServiceDescription and ServiceReport
     * @see #PLANETS_NS
     */
    public static final String SERVICES_NS = PLANETS_NS + "/services";

    /**
     *  A namespace for the tool elements for wrapped tools
     * @see #SERVICES_NS
     */
    public static final String TOOLS_NS = SERVICES_NS + "/tools";


    /**
     * The namespace for datatypes not belonging to objects or services
     *
     * @see #OBJECTS_NS
     * @see #SERVICES_NS
     */
    public static final String DATATYPES_NS = SERVICES_NS+"/datatypes";


    /**
     * The Dublin Core namespace, used for certain terms
     */
    public static final String TERMS_NS = "http://purl.org/dc/terms/";


    /** Define the Planets Service Action namespace, in one place so it can be
     * shared. No trailing slash.
     * Used to many places to change, but the proper name is SERVICES_NS
     * @see #SERVICES_NS
     * */
    public static final String NS =  SERVICES_NS;

}
