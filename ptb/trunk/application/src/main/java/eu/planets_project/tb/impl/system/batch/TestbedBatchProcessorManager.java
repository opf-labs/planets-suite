package eu.planets_project.tb.impl.system.batch;

import java.util.Collection;
import java.util.List;

import eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.system.batch.BatchProcessor;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.services.mockups.workflow.ExperimentWorkflow;
import eu.planets_project.tb.impl.system.batch.backends.ifwee.TestbedWEEBatchProcessor;
import eu.planets_project.tb.impl.system.batch.backends.tbown.TestbedBatchProcessor;
import eu.planets_project.tb.impl.TestbedManagerImpl;

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
			return this.tbOldBatchProcessor;
		}
		if(BatchProcessor.BATCH_QUEUE_TESTBED_WEE_LOCAL.equals(batchSystemIdentifier)){
			return this.tbWeeBatchProcessor;
		}
		return null;
	}

}
