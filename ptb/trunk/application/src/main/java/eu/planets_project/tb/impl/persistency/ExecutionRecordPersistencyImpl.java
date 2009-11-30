package eu.planets_project.tb.impl.persistency;

import java.util.List;



import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.rmi.PortableRemoteObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;

@Stateless
public class ExecutionRecordPersistencyImpl implements ExecutionRecordPersistency {
	
    private static Log log = LogFactory.getLog(ExecutionRecordPersistencyImpl.class.getName());
    
	@PersistenceContext(unitName="testbed", type=PersistenceContextType.TRANSACTION) 
	protected EntityManager manager;

	public ExecutionRecordImpl findExecutionRecordImpl(long id) {
		return manager.find(ExecutionRecordImpl.class, id);
	}

	public long persistExecutionRecordImpl(ExecutionRecordImpl executionRecordImpl) {
	    log.info("Persisting ExecutionRecord: "+executionRecordImpl.getDigitalObjectSource());
		manager.persist(executionRecordImpl);
		return executionRecordImpl.getId();
	}

	public void updateExecutionRecordImpl(ExecutionRecordImpl executionRecordImpl) {
		manager.merge(executionRecordImpl);
	}

	
    /**
     * A Factory method to build a reference to this interface.
     * @return
     */
	public static ExecutionRecordPersistency getInstance(){
		try{
			Context jndiContext = getInitialContext();
//            ExecutionRecordPersistency dao_r = (ExecutionRecordPersistency) PortableRemoteObject.narrow(
//                    jndiContext.lookup("testbed/ExecutionRecordPersistencyImpl/remote"), ExecutionRecordPersistency.class);
            ExecutionRecordPersistency dao_r = (ExecutionRecordPersistency) 
                    jndiContext.lookup("testbed/ExecutionRecordPersistencyImpl/local");
			return dao_r;
		}catch (NamingException e) {
			//TODO integrate message into logging mechanism
			System.out.println("Failure in getting PortableRemoteObject: "+e.toString());
			return null;
		}
	}
	
	private static Context getInitialContext() throws javax.naming.NamingException
	{
		return new javax.naming.InitialContext();
	}

    
}
