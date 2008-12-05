/**
 * 
 */
package eu.planets_project.tb.impl.persistency;

import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public interface ExecutionRecordPersistency {

    public ExecutionRecordImpl findExecutionRecordImpl(long id);

    public long persistExecutionRecordImpl(ExecutionRecordImpl executionRecordImpl);

    public void updateExecutionRecordImpl(ExecutionRecordImpl executionRecordImpl);

}
