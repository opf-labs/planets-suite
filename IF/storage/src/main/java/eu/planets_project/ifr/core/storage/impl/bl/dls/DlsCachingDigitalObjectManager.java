/**
 * 
 */
package eu.planets_project.ifr.core.storage.impl.bl.dls;

import java.net.URI;
import java.util.List;

import eu.planets_project.ifr.core.common.conf.Configuration;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase;

/**
 * @author carl
 *
 */
public class DlsCachingDigitalObjectManager extends DigitalObjectManagerBase {

	protected DlsCachingDigitalObjectManager(Configuration config) {
		super(config);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#list(java.net.URI)
	 */
	@Override
	public List<URI> list(URI pdURI) {
		// TODO Auto-generated method stub
		return null;
	}

}
