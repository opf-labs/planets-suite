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
package eu.planets_project.tb.api.services.mockups.workflow;

import java.io.File;

import eu.planets_project.tb.api.model.eval.EvaluationExecutable;

public interface Workflow {
	
	public EvaluationExecutable execute(File f1, File f2);

}
