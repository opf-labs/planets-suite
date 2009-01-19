package eu.planets_project.ifr.core.services.characterisation.extractor.xcdl;

import java.util.List;

import eu.planets_project.services.datatypes.Property;

/**
 * Access to an XCDL document.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public interface XcdlAccess {
    /**
     * @return A list of properties, extracted from the XCDL file. Note that
     *         this only returns a restricted part of the XCDL. For a complete
     *         access to the XCDL, use {@link XcdlParser}.
     */
    List<Property> getProperties();
}
