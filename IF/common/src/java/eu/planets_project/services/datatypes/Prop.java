/**
 * 
 */
package eu.planets_project.services.datatypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * A suggestion for a universal property class. This class differs from
 * {@link Property} mainly in two aspects:
 *<ul>
 *<li>Multiple values are possible</li>
 *<li>Can contain sub-properties</li>
 *</ul>
 * This allows to create properties like this (a sample input config property
 * for the XCDL comparator):
 * 
 * <pre>
 * {@code
Prop.name("imageHeight").type("101").unit("pixel").props(
        Prop.name("metric").type("200").description("equal").build(),
        Prop.name("metric").type("201").description("intDiff").build(),
        Prop.name("metric").type("210").description("percDev").build())
        .build();
   }
 * </pre>
 *<p/>
 * With these extensions we can model many things that are currently done by
 * many different classes, which are all very similar and could perhaps be
 * replaced by a class like this. These are:
 * <ul>
 *<li>{@link Property} (which then could be the new name for this class)</li>
 *<li>{@link Properties}</li>
 *<li>{@link FileFormatProperty}</li>
 *<li>{@link FileFormatProperties}</li>
 *<li>{@link Parameter}</li>
 *<li>{@link Parameters}</li>
 *<li>{@link Metric}</li>
 *<li>{@link Metrics}</li>
 *</ul>
 * @see {@link PropTests}
 * @author Fabian Steeg
 */
@XmlAccessorType(XmlAccessType.FIELD)
public final class Prop {

    private String name, unit, description, type;

    private List<String> values = new ArrayList<String>();

    private List<Prop> properties = new ArrayList<Prop>();

    /**
     * Entry point for creating a {@link Prop} instance.
     * @param name The property name
     * @return The builder, for cascaded calls. After setting all desired
     *         attributes, call the build() method on the returned builder to
     *         create the {@link Prop} instance.
     */
    public static Prop.Builder name(final String name) {
        return new Prop.Builder(name);
    }

    /**
     * Builder for universal properties.
     */
    public static final class Builder {
        private List<String> values = new ArrayList<String>();
        private String name;
        private String unit = "";
        private String description = "";
        private String type = "";
        private List<Prop> sub = new ArrayList<Prop>();

        /** @return The instance created using this builder. */
        public Prop build() {
            return new Prop(this);
        }

        /**
         * @param name The property name
         */
        private Builder(final String name) {
            this.name = name;
        }

        /** No-arg constructor for JAXB. */
        private Builder() {}

        /**
         * @param unit The unit
         * @return The builder, for cascaded calls
         */
        public Builder unit(final String unit) {
            this.unit = unit;
            return this;
        }

        /**
         * @param type The type
         * @return The builder, for cascaded calls
         */
        public Builder type(final String type) {
            this.type = type;
            return this;
        }

        /**
         * @param description The description
         * @return The builder, for cascaded calls
         */
        public Builder description(final String description) {
            this.description = description;
            return this;
        }

        /**
         * @param sub The sub properties
         * @return The builder, for cascaded calls
         */
        public Builder props(final Prop... sub) {
            this.sub = Arrays.asList(sub);
            return this;
        }

        /**
         * @param values The value(s)
         * @return The builder, for cascaded calls
         */
        public Builder values(final String... values) {
            this.values = Arrays.asList(values);
            return this;
        }

    }

    /**
     * For JAXB.
     */
    private Prop() {}

    /**
     * @param name The name
     * @param type The type
     * @param unit The unit
     * @param description A description
     * @param values The values
     */
    private Prop(final String name, final String type, final String unit,
            final String description, final List<String> values) {
        this.name = name;
        this.values = values;
        this.type = type;
        this.unit = unit;
        this.description = description;
    }

    /**
     * @param builder The builder
     */
    private Prop(final Builder builder) {
        this.description = builder.description;
        this.name = builder.name;
        this.type = builder.type;
        this.unit = builder.unit;
        this.values = builder.values;
        this.properties = builder.sub;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the values, as an unmodifiable view
     */
    public List<String> getValues() {
        return Collections.unmodifiableList(values);
    }

    /**
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the sub-properties, as an unmodifiable view
     */
    public List<Prop> getProps() {
        return Collections.unmodifiableList(properties);
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String format = String.format("%s '%s' = '%s'", this.getClass()
                .getSimpleName(), name, values);
        if (properties.size() > 0) {
            format += ", sub: " + properties;
        }
        return format;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Prop)) {
            return false;
        }
        Prop that = (Prop) obj;
        return this.name.equals(that.name) && this.values.equals(that.values)
                && this.unit.equals(that.unit)
                && this.description.equals(that.description)
                && this.type.equals(that.type)
                && this.properties.equals(that.properties);
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = 17;
        int oddPrime = 31;
        result = oddPrime * result + name.hashCode();
        result = oddPrime * result + values.hashCode();
        result = oddPrime * result + unit.hashCode();
        result = oddPrime * result + description.hashCode();
        result = oddPrime * result + type.hashCode();
        result = oddPrime * result + properties.hashCode();
        return result;
    }
}
