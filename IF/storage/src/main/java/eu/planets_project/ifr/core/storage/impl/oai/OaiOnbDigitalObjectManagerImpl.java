/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.planets_project.ifr.core.storage.impl.oai;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.services.datatypes.DigitalObject;
import java.net.URI;

/**
 *
 * @author onb
 */
public class OaiOnbDigitalObjectManagerImpl implements DigitalObjectManager{

    public void store(URI pdURI, DigitalObject digitalObject) throws DigitalObjectNotStoredException {
        throw new UnsupportedOperationException("Not supported via OAI.");
    }

    public DigitalObject retrieve(URI pdURI) throws DigitalObjectNotFoundException {
        return null;
    }

    public URI[] list(URI pdURI) {
        return null;
    }

}
