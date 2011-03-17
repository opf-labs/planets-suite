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
package eu.planets_project.tb.api.services.util;

import eu.planets_project.tb.api.services.TestbedServiceTemplate;

/**
 * @author alindley
 * Responsible for handling the import of a single TestbedServiceTemplate.xml file which is
 * the format the Testbed serializes a ServiceTemplate. The file is validated
 * and afterwards converted and registered within the TBServiceTemplate registry
 *
 */
public interface ServiceTemplateImporter {

	/**
	 * Tasks are create a TBServiceTemplate object from a exported and valid service template.xml;
	 * populate it with relevant information provided within this implementing parser class
	 * and finally register the template within the service template registry
	 * @return the imported TestbedServiceTemplate object
	 * @throws Exception: if any error (e.g. validation) or if Service endpoint is not reachable
	 */
	public TestbedServiceTemplate createAndRegisterTemplate() throws Exception;
	
	/**
	 * Same as createAndRegisterTemplate, but just returns the object without adding it
	 * into the service template registry
	 * @return the imported TestbedServiceTemplate object
	 * @throws Exception: if any error (e.g. validation) or if Service endpoint is not reachable
	 */
	public TestbedServiceTemplate createTemplate() throws Exception;
	
	/**
	 * Checks if the provided endpoint within the imported template is reachable i.e. properly deployed
	 * @return
	 */
	public boolean isImportedServiceEndpointReachable();
	
	/**
	 * Returns true if the imported config file validates against the ServiceTemplate schema
	 * @return
	 */
	public boolean isAValidServiceTemplate();
	
	/**
	 * Returns true if the imported config file validates against the ServiceTemplate schema
	 * @return
	 */
	public boolean isAValidEvaluationServiceTemplate();
}
