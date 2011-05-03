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
package eu.planets_project.services.utils;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Suite to run all tests in the common component.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */

@RunWith( Suite.class )
@Suite.SuiteClasses( { ZipUtilsTest.class } )
public class AllUtilsTestSuite {}
