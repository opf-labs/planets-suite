package eu.planets_project.services.datatypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for methods to be queryable (e.g. when using query by
 * example in the service registry component).
 * @see eu.planets_project.ifr.core.servreg.api.Query
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Queryable {

}
