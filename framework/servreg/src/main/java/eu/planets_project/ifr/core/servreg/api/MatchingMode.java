package eu.planets_project.ifr.core.servreg.api;

/**
 * Matching modes as a strategy enumeration.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public enum MatchingMode {
    /** Exact string comparison, case-insensitive. */
    EXACT {
        /**
         * {@inheritDoc}
         * @see eu.planets_project.ifr.core.registry.impl.Query.MatchingMode#matches(java.lang.String,
         *      java.lang.String)
         */
        public boolean matches(final String value, final String pattern) {
            return value.equalsIgnoreCase(pattern);
        }
    },
    /**
     * Wildcard string comparison, e.g. "*Droid*" will match all "Droid",
     * "Droid2", and "A droid service" (case-insensitive).
     */
    WILDCARD {
        /**
         * {@inheritDoc}
         * @see eu.planets_project.ifr.core.registry.impl.Query.MatchingMode#matches(java.lang.String,
         *      java.lang.String)
         */
        public boolean matches(final String value, final String pattern) {
            return REGEX.matches(value, pattern.replaceAll("\\*", ".*?"));
        }
    },
    /**
     * String comparison based on regular expressions (case-insensitive).
     */
    REGEX {
        /**
         * {@inheritDoc}
         * @see eu.planets_project.ifr.core.registry.impl.Query.MatchingMode#matches(java.lang.String,
         *      java.lang.String)
         */
        public boolean matches(final String value, final String pattern) {
            return value.toLowerCase().matches(pattern.toLowerCase());
        }
    };
    /**
     * @param value The value to match the pattern against
     * @param pattern The pattern to match against
     * @return True, if the value matches the pattern
     */
    public abstract boolean matches(String value, String pattern);
}
