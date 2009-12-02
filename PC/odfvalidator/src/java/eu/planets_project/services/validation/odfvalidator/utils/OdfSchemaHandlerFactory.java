package eu.planets_project.services.validation.odfvalidator.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class OdfSchemaHandlerFactory {
	
	private static OdfSchemaHandler schemaHandler = null;
	
	private static Logger log = Logger.getLogger(OdfSchemaHandlerFactory.class.getName());
	
	public static OdfSchemaHandler getSchemaHandlerInstance() {
		log.setLevel(Level.INFO);
		if(schemaHandler==null) {
			return new OdfSchemaHandler();
		}
		else {
			log.info("Reusing existing SchemaHandler...!");
			return schemaHandler;
		}
	}

}
