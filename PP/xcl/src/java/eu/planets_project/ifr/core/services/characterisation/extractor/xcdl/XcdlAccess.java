package eu.planets_project.ifr.core.services.characterisation.extractor.xcdl;

import eu.planets_project.services.characterise.CharacteriseResult;

/**
 * Access to an XCDL document.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public interface XcdlAccess {
    /**
     * @return Possibly nested lists of properties, extracted from the XCDL file and wrapped into a CharacterizeResult
     *         (which can be used with the {@link CompareProperties} interface). Note that this only returns a
     *         restricted part of the XCDL. For complete access to the XCDL, use {@link XcdlParser}.
     */
    CharacteriseResult getCharacteriseResult();
}
