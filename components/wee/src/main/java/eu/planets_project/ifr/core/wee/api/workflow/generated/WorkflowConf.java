//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB)
// Reference Implementation, vhudson-jaxb-ri-2.1-520
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source
// schema.
// Generated on: 2008.11.14 at 04:05:14 PM CET
//

package eu.planets_project.ifr.core.wee.api.workflow.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * <p>
 * Java class for anonymous complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
* &lt;complexType>
*   &lt;complexContent>
*     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
*       &lt;sequence>
*         &lt;element name="template">
*           &lt;complexType>
*             &lt;complexContent>
*               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
*                 &lt;sequence>
*                   &lt;element name="class" type="{http://www.w3.org/2001/XMLSchema}string"/>
*                 &lt;/sequence>
*               &lt;/restriction>
*             &lt;/complexContent>
*           &lt;/complexType>
*         &lt;/element>
*         &lt;element name="services" minOccurs="0">
*           &lt;complexType>
*             &lt;complexContent>
*               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
*                 &lt;sequence>
*                   &lt;element name="service" maxOccurs="unbounded">
*                     &lt;complexType>
*                       &lt;complexContent>
*                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
*                           &lt;sequence>
*                             &lt;element name="endpoint" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
*                             &lt;element name="parameters" minOccurs="0">
*                               &lt;complexType>
*                                 &lt;complexContent>
*                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
*                                     &lt;sequence>
*                                       &lt;element name="param" maxOccurs="unbounded">
*                                         &lt;complexType>
*                                           &lt;complexContent>
*                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
*                                               &lt;sequence>
*                                                 &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
*                                                 &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
*                                               &lt;/sequence>
*                                             &lt;/restriction>
*                                           &lt;/complexContent>
*                                         &lt;/complexType>
*                                       &lt;/element>
*                                     &lt;/sequence>
*                                   &lt;/restriction>
*                                 &lt;/complexContent>
*                               &lt;/complexType>
*                             &lt;/element>
*                           &lt;/sequence>
*                           &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
*                         &lt;/restriction>
*                       &lt;/complexContent>
*                     &lt;/complexType>
*                   &lt;/element>
*                 &lt;/sequence>
*               &lt;/restriction>
*             &lt;/complexContent>
*           &lt;/complexType>
*         &lt;/element>
*         &lt;element name="data" minOccurs="0">
*           &lt;complexType>
*             &lt;complexContent>
*               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
*                 &lt;sequence maxOccurs="unbounded">
*                   &lt;choice>
*                     &lt;element name="base64">
*                       &lt;complexType>
*                         &lt;simpleContent>
*                           &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>base64Binary">
*                             &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
*                           &lt;/extension>
*                         &lt;/simpleContent>
*                       &lt;/complexType>
*                     &lt;/element>
*                     &lt;element name="refURL">
*                       &lt;complexType>
*                         &lt;simpleContent>
*                           &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>anyURI">
*                             &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
*                           &lt;/extension>
*                         &lt;/simpleContent>
*                       &lt;/complexType>
*                     &lt;/element>
*                   &lt;/choice>
*                 &lt;/sequence>
*               &lt;/restriction>
*             &lt;/complexContent>
*           &lt;/complexType>
*         &lt;/element>
*       &lt;/sequence>
*     &lt;/restriction>
*   &lt;/complexContent>
* &lt;/complexType>
* </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "template", "services", "data" })
@XmlRootElement(name = "workflowConf")
public class WorkflowConf {

    @XmlElement(required = true)
    protected WorkflowConf.Template template;
    protected WorkflowConf.Services services;
    protected WorkflowConf.Data data;

    /**
     * Gets the value of the template property.
     * @return possible object is {@link WorkflowConf.Template }
     */
    public WorkflowConf.Template getTemplate() {
        return template;
    }

    /**
     * Sets the value of the template property.
     * @param value allowed object is {@link WorkflowConf.Template }
     */
    public void setTemplate(WorkflowConf.Template value) {
        this.template = value;
    }

    /**
     * Gets the value of the services property.
     * @return possible object is {@link WorkflowConf.Services }
     */
    public WorkflowConf.Services getServices() {
        return services;
    }

    /**
     * Sets the value of the services property.
     * @param value allowed object is {@link WorkflowConf.Services }
     */
    public void setServices(WorkflowConf.Services value) {
        this.services = value;
    }

    /**
     * Gets the value of the data property.
     * @return possible object is {@link WorkflowConf.Data }
     */
    public WorkflowConf.Data getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     * @param value allowed object is {@link WorkflowConf.Data }
     */
    public void setData(WorkflowConf.Data value) {
        this.data = value;
    }

    /**
     * <p>
     * Java class for anonymous complex type.
     * <p>
     * The following schema fragment specifies the expected content contained
     * within this class.
     * 
     * <pre>
  * &lt;complexType>
  *   &lt;complexContent>
  *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
  *       &lt;sequence maxOccurs="unbounded">
  *         &lt;choice>
  *           &lt;element name="base64">
  *             &lt;complexType>
  *               &lt;simpleContent>
  *                 &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>base64Binary">
  *                   &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
  *                 &lt;/extension>
  *               &lt;/simpleContent>
  *             &lt;/complexType>
  *           &lt;/element>
  *           &lt;element name="refURL">
  *             &lt;complexType>
  *               &lt;simpleContent>
  *                 &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>anyURI">
  *                   &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
  *                 &lt;/extension>
  *               &lt;/simpleContent>
  *             &lt;/complexType>
  *           &lt;/element>
  *         &lt;/choice>
  *       &lt;/sequence>
  *     &lt;/restriction>
  *   &lt;/complexContent>
  * &lt;/complexType>
  * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = { "base64OrRefURL" })
    public static class Data {

        @XmlElements( {
                @XmlElement(name = "refURL", type = WorkflowConf.Data.RefURL.class),
                @XmlElement(name = "base64", type = WorkflowConf.Data.Base64.class) })
        protected List<Object> base64OrRefURL;

        /**
         * Gets the value of the base64OrRefURL property.
         * <p>
         * This accessor method returns a reference to the live list, not a
         * snapshot. Therefore any modification you make to the returned list
         * will be present inside the JAXB object. This is why there is not a
         * <CODE>set</CODE> method for the base64OrRefURL property.
         * <p>
         * For example, to add a new item, do as follows:
         * 
         * <pre>
      *    getBase64OrRefURL().add(newItem);
      * </pre>
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link WorkflowConf.Data.RefURL } {@link WorkflowConf.Data.Base64 }
         */
        public List<Object> getBase64OrRefURL() {
            if (base64OrRefURL == null) {
                base64OrRefURL = new ArrayList<Object>();
            }
            return this.base64OrRefURL;
        }

        /**
         * <p>
         * Java class for anonymous complex type.
         * <p>
         * The following schema fragment specifies the expected content
         * contained within this class.
         * 
         * <pre>
      * &lt;complexType>
      *   &lt;simpleContent>
      *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>base64Binary">
      *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
      *     &lt;/extension>
      *   &lt;/simpleContent>
      * &lt;/complexType>
      * </pre>
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = { "value" })
        public static class Base64 {

            @XmlValue
            protected byte[] value;
            @XmlAttribute(required = true)
            protected String id;

            /**
             * Gets the value of the value property.
             * @return possible object is byte[]
             */
            public byte[] getValue() {
                // Primitive and shallow, so clone is OK here:
                return (byte[]) value.clone();
            }

            /**
             * Sets the value of the value property.
             * @param value allowed object is byte[]
             */
            public void setValue(byte[] value) {
                // Primitive and shallow, so clone is OK here:
                this.value = (byte[]) value.clone();
            }

            /**
             * Gets the value of the id property.
             * @return possible object is {@link String }
             */
            public String getId() {
                return id;
            }

            /**
             * Sets the value of the id property.
             * @param value allowed object is {@link String }
             */
            public void setId(String value) {
                this.id = value;
            }

        }

        /**
         * <p>
         * Java class for anonymous complex type.
         * <p>
         * The following schema fragment specifies the expected content
         * contained within this class.
         * 
         * <pre>
      * &lt;complexType>
      *   &lt;simpleContent>
      *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>anyURI">
      *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
      *     &lt;/extension>
      *   &lt;/simpleContent>
      * &lt;/complexType>
      * </pre>
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = { "value" })
        public static class RefURL {

            @XmlValue
            @XmlSchemaType(name = "anyURI")
            protected String value;
            @XmlAttribute(required = true)
            protected String id;

            /**
             * Gets the value of the value property.
             * @return possible object is {@link String }
             */
            public String getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * @param value allowed object is {@link String }
             */
            public void setValue(String value) {
                this.value = value;
            }

            /**
             * Gets the value of the id property.
             * @return possible object is {@link String }
             */
            public String getId() {
                return id;
            }

            /**
             * Sets the value of the id property.
             * @param value allowed object is {@link String }
             */
            public void setId(String value) {
                this.id = value;
            }

        }

    }

    /**
     * <p>
     * Java class for anonymous complex type.
     * <p>
     * The following schema fragment specifies the expected content contained
     * within this class.
     * 
     * <pre>
  * &lt;complexType>
  *   &lt;complexContent>
  *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
  *       &lt;sequence>
  *         &lt;element name="service" maxOccurs="unbounded">
  *           &lt;complexType>
  *             &lt;complexContent>
  *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
  *                 &lt;sequence>
  *                   &lt;element name="endpoint" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
  *                   &lt;element name="parameters" minOccurs="0">
  *                     &lt;complexType>
  *                       &lt;complexContent>
  *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
  *                           &lt;sequence>
  *                             &lt;element name="param" maxOccurs="unbounded">
  *                               &lt;complexType>
  *                                 &lt;complexContent>
  *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
  *                                     &lt;sequence>
  *                                       &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
  *                                       &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
  *                                     &lt;/sequence>
  *                                   &lt;/restriction>
  *                                 &lt;/complexContent>
  *                               &lt;/complexType>
  *                             &lt;/element>
  *                           &lt;/sequence>
  *                         &lt;/restriction>
  *                       &lt;/complexContent>
  *                     &lt;/complexType>
  *                   &lt;/element>
  *                 &lt;/sequence>
  *                 &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
  *               &lt;/restriction>
  *             &lt;/complexContent>
  *           &lt;/complexType>
  *         &lt;/element>
  *       &lt;/sequence>
  *     &lt;/restriction>
  *   &lt;/complexContent>
  * &lt;/complexType>
  * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = { "service" })
    public static class Services {

        @XmlElement(required = true)
        protected List<WorkflowConf.Services.Service> service;

        /**
         * Gets the value of the service property.
         * <p>
         * This accessor method returns a reference to the live list, not a
         * snapshot. Therefore any modification you make to the returned list
         * will be present inside the JAXB object. This is why there is not a
         * <CODE>set</CODE> method for the service property.
         * <p>
         * For example, to add a new item, do as follows:
         * 
         * <pre>
      *    getService().add(newItem);
      * </pre>
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link WorkflowConf.Services.Service }
         */
        public List<WorkflowConf.Services.Service> getService() {
            if (service == null) {
                service = new ArrayList<WorkflowConf.Services.Service>();
            }
            return this.service;
        }

        /**
         * <p>
         * Java class for anonymous complex type.
         * <p>
         * The following schema fragment specifies the expected content
         * contained within this class.
         * 
         * <pre>
      * &lt;complexType>
      *   &lt;complexContent>
      *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
      *       &lt;sequence>
      *         &lt;element name="endpoint" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
      *         &lt;element name="parameters" minOccurs="0">
      *           &lt;complexType>
      *             &lt;complexContent>
      *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
      *                 &lt;sequence>
      *                   &lt;element name="param" maxOccurs="unbounded">
      *                     &lt;complexType>
      *                       &lt;complexContent>
      *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
      *                           &lt;sequence>
      *                             &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
      *                             &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
      *                           &lt;/sequence>
      *                         &lt;/restriction>
      *                       &lt;/complexContent>
      *                     &lt;/complexType>
      *                   &lt;/element>
      *                 &lt;/sequence>
      *               &lt;/restriction>
      *             &lt;/complexContent>
      *           &lt;/complexType>
      *         &lt;/element>
      *       &lt;/sequence>
      *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
      *     &lt;/restriction>
      *   &lt;/complexContent>
      * &lt;/complexType>
      * </pre>
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = { "endpoint", "parameters" })
        public static class Service {

            @XmlElement(required = true)
            @XmlSchemaType(name = "anyURI")
            protected String endpoint;
            protected WorkflowConf.Services.Service.Parameters parameters;
            @XmlAttribute(required = true)
            protected String id;

            /**
             * Gets the value of the endpoint property.
             * @return possible object is {@link String }
             */
            public String getEndpoint() {
                return endpoint;
            }

            /**
             * Sets the value of the endpoint property.
             * @param value allowed object is {@link String }
             */
            public void setEndpoint(String value) {
                this.endpoint = value;
            }

            /**
             * Gets the value of the parameters property.
             * @return possible object is
             *         {@link WorkflowConf.Services.Service.Parameters }
             */
            public WorkflowConf.Services.Service.Parameters getParameters() {
                return parameters;
            }

            /**
             * Sets the value of the parameters property.
             * @param value allowed object is
             *        {@link WorkflowConf.Services.Service.Parameters }
             */
            public void setParameters(
                    WorkflowConf.Services.Service.Parameters value) {
                this.parameters = value;
            }

            /**
             * Gets the value of the id property.
             * @return possible object is {@link String }
             */
            public String getId() {
                return id;
            }

            /**
             * Sets the value of the id property.
             * @param value allowed object is {@link String }
             */
            public void setId(String value) {
                this.id = value;
            }

            /**
             * <p>
             * Java class for anonymous complex type.
             * <p>
             * The following schema fragment specifies the expected content
             * contained within this class.
             * 
             * <pre>
          * &lt;complexType>
          *   &lt;complexContent>
          *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
          *       &lt;sequence>
          *         &lt;element name="param" maxOccurs="unbounded">
          *           &lt;complexType>
          *             &lt;complexContent>
          *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
          *                 &lt;sequence>
          *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
          *                   &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
          *                 &lt;/sequence>
          *               &lt;/restriction>
          *             &lt;/complexContent>
          *           &lt;/complexType>
          *         &lt;/element>
          *       &lt;/sequence>
          *     &lt;/restriction>
          *   &lt;/complexContent>
          * &lt;/complexType>
          * </pre>
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = { "param" })
            public static class Parameters {

                @XmlElement(required = true)
                protected List<WorkflowConf.Services.Service.Parameters.Param> param;

                /**
                 * Gets the value of the param property.
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object. This is
                 * why there is not a <CODE>set</CODE> method for the param
                 * property.
                 * <p>
                 * For example, to add a new item, do as follows:
                 * 
                 * <pre>
              *    getParam().add(newItem);
              * </pre>
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link WorkflowConf.Services.Service.Parameters.Param }
                 */
                public List<WorkflowConf.Services.Service.Parameters.Param> getParam() {
                    if (param == null) {
                        param = new ArrayList<WorkflowConf.Services.Service.Parameters.Param>();
                    }
                    return this.param;
                }

                /**
                 * <p>
                 * Java class for anonymous complex type.
                 * <p>
                 * The following schema fragment specifies the expected content
                 * contained within this class.
                 * 
                 * <pre>
              * &lt;complexType>
              *   &lt;complexContent>
              *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
              *       &lt;sequence>
              *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
              *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
              *       &lt;/sequence>
              *     &lt;/restriction>
              *   &lt;/complexContent>
              * &lt;/complexType>
              * </pre>
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = { "name", "value" })
                public static class Param {

                    @XmlElement(required = true)
                    @XmlSchemaType(name = "anyURI")
                    protected String name;
                    @XmlElement(required = true)
                    protected String value;

                    /**
                     * Gets the value of the name property.
                     * @return possible object is {@link String }
                     */
                    public String getName() {
                        return name;
                    }

                    /**
                     * Sets the value of the name property.
                     * @param value allowed object is {@link String }
                     */
                    public void setName(String value) {
                        this.name = value;
                    }

                    /**
                     * Gets the value of the value property.
                     * @return possible object is {@link String }
                     */
                    public String getValue() {
                        return value;
                    }

                    /**
                     * Sets the value of the value property.
                     * @param value allowed object is {@link String }
                     */
                    public void setValue(String value) {
                        this.value = value;
                    }

                }

            }

        }

    }

    /**
     * <p>
     * Java class for anonymous complex type.
     * <p>
     * The following schema fragment specifies the expected content contained
     * within this class.
     * 
     * <pre>
  * &lt;complexType>
  *   &lt;complexContent>
  *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
  *       &lt;sequence>
  *         &lt;element name="class" type="{http://www.w3.org/2001/XMLSchema}string"/>
  *       &lt;/sequence>
  *     &lt;/restriction>
  *   &lt;/complexContent>
  * &lt;/complexType>
  * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = { "clazz" })
    public static class Template {

        @XmlElement(name = "class", required = true)
        protected String clazz;

        /**
         * Gets the value of the clazz property.
         * @return possible object is {@link String }
         */
        public String getClazz() {
            return clazz;
        }

        /**
         * Sets the value of the clazz property.
         * @param value allowed object is {@link String }
         */
        public void setClazz(String value) {
            this.clazz = value;
        }

    }

}
