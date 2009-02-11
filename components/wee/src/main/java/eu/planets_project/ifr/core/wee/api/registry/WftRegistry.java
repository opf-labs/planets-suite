package eu.planets_project.ifr.core.wee.api.registry;

import java.io.IOException;
import java.util.List;

import javax.ejb.Remote;

import eu.planets_project.ifr.core.wee.api.wsinterface.WftRegistryService;

@Remote
public interface WftRegistry extends WftRegistryService{
	
	//public byte[] getWFTemplateClass(String fullQName) throws IOException;

	//TODO check if any additional registry functionality required that's not specified
	//within the web-service's interface and should be implemented by: WftRegistryImpl 
	//otherwise DELETE
	
}

