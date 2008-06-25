package eu.planets_project.tb.gui.backing.admin;

/**
 * This bean implements the following logic: controller + JSF element backing
 *    - import and export service templates from/to XML from/into the application
 *
 * @author Andrew Lindley, ARC
 */

public class ImportExportTBServices {
	
	private String sEntry = "";
	
	public String command_getFirstEntry(){
		return "reload-page";
	}
	

}
