package eu.planets_project.tb.unittest;

import eu.planets_project.tb.impl.CommentManagerImpl;
import eu.planets_project.tb.impl.model.CommentImpl;

public class CommentManagerTest {
	
	public long commID1;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CommentManagerImpl manager = CommentManagerImpl.getInstance();
		CommentImpl com1 = (CommentImpl)manager.getNewRootComment(1, "setup");
		Long lExpID = com1.getExperimentID();
		String sPhaseID = com1.getExperimentPhaseID();
		System.out.println("ID: "+lExpID+" Phase: "+sPhaseID);
		
	}

}
