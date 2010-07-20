package eu.planets_project.services.modification.floppyImageModify.api;

import java.net.URI;
import java.util.List;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.modify.ModifyResult;



/**
 * @author Peter Melms, peter.melms@uni-koeln.de (UzK)
 */
public interface FloppyImageModify {
	
	public abstract ServiceDescription describe();
	

	public abstract ModifyResult modify(DigitalObject digitalObject,
			URI inputFormat, List<Parameter> parameters);

}