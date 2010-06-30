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
package eu.planets_project.tb.impl.system.batch;

import eu.planets_project.tb.api.system.batch.BatchProcessor;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.system.batch.backends.ifwee.TestbedWEEBatchProcessor;
import eu.planets_project.tb.impl.system.batch.backends.tbown.TestbedBatchProcessor;

/**
 * A joint manager for obtaining instances to the two batch execution engines 
 * we're currently supporting within the Testbed.
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 21.10.2009
 *
 */
public class TestbedBatchProcessorManager{

	private static TestbedBatchProcessorManager instance;
	private static TestbedBatchProcessor tbOldBatchProcessor;
	private static TestbedWEEBatchProcessor tbWeeBatchProcessor;
	
	public static synchronized TestbedBatchProcessorManager getInstance(){
		if (instance == null){
			instance = new TestbedBatchProcessorManager();
			tbOldBatchProcessor = (TestbedBatchProcessor)JSFUtil.getManagedObject("TestbedBatchProcessor");
			tbWeeBatchProcessor = TestbedWEEBatchProcessor.getInstance();
		}
		return instance;
	}
	
	/**
	 * Returns the BatchProcessor on basis of the system's identifier
	 * For currently supported batch processors see
	 * @see BatchProcessor
	 * @param batchSystemIdentifier
	 * @return
	 */
	public BatchProcessor getBatchProcessor(String batchSystemIdentifier) {
		if(BatchProcessor.BATCH_IDENTIFIER_TESTBED_LOCAL.equals(batchSystemIdentifier)){
			return TestbedBatchProcessorManager.tbOldBatchProcessor;
		}
		if(BatchProcessor.BATCH_QUEUE_TESTBED_WEE_LOCAL.equals(batchSystemIdentifier)){
			return TestbedBatchProcessorManager.tbWeeBatchProcessor;
		}
		return null;
	}

}
