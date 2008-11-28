package eu.planets_project.ifr.core.registry.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.Queryable;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * Query by example functionality for service description instances.
 * @see CoreRegistryTests
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class Query {

    private List<ServiceDescription> descriptions = null;
    private static Log log = LogFactory.getLog(Query.class.getName());

    /**
     * @param descriptions The descriptions to query
     */
    public Query(final List<ServiceDescription> descriptions) {
        this.descriptions = descriptions;
    }

    /**
     * @param sample The sample description
     * @return Returns the service descriptions corresponding to the given
     *         sample instance
     */
    public List<ServiceDescription> byExample(final ServiceDescription sample) {
        List<ServiceDescription> result = new ArrayList<ServiceDescription>();
        for (ServiceDescription description : descriptions) {
            if (matches(description, sample)) {
                result.add(description);
            }
        }
        return result;
    }

    /**
     * Compares a candidate and a sample instance reflectively, using a marker
     * annotation. This means the ServiceDescription class can be extended by
     * new values and these values can be used for the query by example
     * functionality without changing this class.
     * @param candidate The candidate service description
     * @param sample The query-by-example sample instance
     * @return True, if the given candidate is described by the given sample,
     *         else false
     */
    private static boolean matches(final ServiceDescription candidate,
            final ServiceDescription sample) {
        /* If no sample is given any description matches: */
        if (sample == null) {
            return true;
        }
        Class<? extends ServiceDescription> clazz = sample.getClass();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            /*
             * We only compare values of methods marked with the Queryable
             * annotation:
             */
            if (method.getAnnotation(Queryable.class) != null) {
                try {
                    Object sampleValue = method.invoke(sample);
                    Object candidateValue = method.invoke(candidate);
                    String message = String
                            .format(
                                    "Comparing values for method '%s' of query example '%s' to candidate instance '%s'.",
                                    method.getName(), sampleValue,
                                    candidateValue);
                    log.debug(message);
                    /*
                     * If the sample value is null, we don't want to consider
                     * it:
                     */
                    if (sampleValue != null) {
                        if (candidateValue == null) {
                            /*
                             * The sample value is not null, but the candidate
                             * is, we have no match:
                             */
                            return false;
                        } else {
                            /*
                             * Now, none of the values are null... we now first
                             * check for Collections:
                             */
                            if (sampleValue instanceof Collection
                                    && candidateValue instanceof Collection) {
                                /*
                                 * They need not be equals, but all sample
                                 * values should be in the candidate:
                                 */
                                if (!((Collection<?>) candidateValue)
                                        .containsAll(((Collection<?>) sampleValue))) {
                                    return false;
                                }
                            } else {
                                /*
                                 * Finally, we can do the normal comparison
                                 * here: if two non-null, non-Collection values
                                 * are not equal the candidate does not match:
                                 */
                                if (!(sampleValue.equals(candidateValue))) {
                                    return false;
                                }
                            }
                        }
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}