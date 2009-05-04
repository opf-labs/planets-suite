package eu.planets_project.services.modification.floppyImageModify.api;

import java.net.URI;
import java.util.List;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.modify.ModifyResult;

public interface FloppyImageModify {

	/* (non-Javadoc)
	 * @see eu.planets_project.services.PlanetsService#describe()
	 */
	/* (non-Javadoc)
	 * @see eu.planets_project.services.modification.floppyImageModify.FloppyImageModifyApi#describe()
	 */
	public abstract ServiceDescription describe();

	/* (non-Javadoc)
	 * @see eu.planets_project.services.modify.Modify#modify(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.util.List)
	 */
	/* (non-Javadoc)
	 * @see eu.planets_project.services.modification.floppyImageModify.FloppyImageModifyApi#modify(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.net.URI, java.util.List)
	 */
	public abstract ModifyResult modify(DigitalObject digitalObject,
			URI inputFormat, URI actionURI, List<Parameter> parameters);

}