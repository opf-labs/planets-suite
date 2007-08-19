package eu.planets_project.tb.unittest;

import eu.planets_project.tb.impl.CommentManager;
import eu.planets_project.tb.impl.model.Comment;

public class CommentManagerTest {
	
	public long commID1;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CommentManager manager = CommentManager.getInstance();
		Comment com1 = (Comment)manager.getNewRootComment(1, "setup");
		Long lExpID = com1.getExperimentID();
		String sPhaseID = com1.getExperimentPhaseID();
		System.out.println("ID: "+lExpID+" Phase: "+sPhaseID);
		
	}

}
