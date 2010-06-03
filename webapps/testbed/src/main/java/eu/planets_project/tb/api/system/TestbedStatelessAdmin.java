/**
 * Copyright (c) 2007, 2008 The Planets Project Partners.
 * 
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * GNU Lesser General Public License v3 which 
 * accompanies this distribution, and is available at 
 * http://www.gnu.org/licenses/lgpl.html
 * 
 */
package eu.planets_project.tb.api.system;

import eu.planets_project.ifr.core.storage.api.DataManagerLocal;

/**
 * @author AnJackson
 *
 */
public interface TestbedStatelessAdmin {

    /**
     * @return
     */
    public DataManagerLocal getPlanetsDataManagerAsAdmin();

}
