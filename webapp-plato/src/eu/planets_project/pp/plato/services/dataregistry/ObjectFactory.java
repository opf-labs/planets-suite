/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/

package eu.planets_project.pp.plato.services.dataregistry;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the eu.planets_project.pp.plato.services.dataregistry package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _SOAPException_QNAME = new QName("http://planets-project.eu/ifr/core/storage/data", "SOAPException");
    private final static QName _LoginException_QNAME = new QName("http://planets-project.eu/ifr/core/storage/data", "LoginException");
    private final static QName _RepositoryException_QNAME = new QName("http://planets-project.eu/ifr/core/storage/data", "RepositoryException");
    private final static QName _URISyntaxException_QNAME = new QName("http://planets-project.eu/ifr/core/storage/data", "URISyntaxException");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: eu.planets_project.pp.plato.services.dataregistry
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RepositoryException }
     * 
     */
    public RepositoryException createRepositoryException() {
        return new RepositoryException();
    }

    /**
     * Create an instance of {@link URISyntaxException }
     * 
     */
    public URISyntaxException createURISyntaxException() {
        return new URISyntaxException();
    }

    /**
     * Create an instance of {@link LoginException }
     * 
     */
    public LoginException createLoginException() {
        return new LoginException();
    }

    /**
     * Create an instance of {@link StringArray }
     * 
     */
    public StringArray createStringArray() {
        return new StringArray();
    }

    /**
     * Create an instance of {@link SOAPException }
     * 
     */
    public SOAPException createSOAPException() {
        return new SOAPException();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SOAPException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://planets-project.eu/ifr/core/storage/data", name = "SOAPException")
    public JAXBElement<SOAPException> createSOAPException(SOAPException value) {
        return new JAXBElement<SOAPException>(_SOAPException_QNAME, SOAPException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LoginException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://planets-project.eu/ifr/core/storage/data", name = "LoginException")
    public JAXBElement<LoginException> createLoginException(LoginException value) {
        return new JAXBElement<LoginException>(_LoginException_QNAME, LoginException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RepositoryException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://planets-project.eu/ifr/core/storage/data", name = "RepositoryException")
    public JAXBElement<RepositoryException> createRepositoryException(RepositoryException value) {
        return new JAXBElement<RepositoryException>(_RepositoryException_QNAME, RepositoryException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link URISyntaxException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://planets-project.eu/ifr/core/storage/data", name = "URISyntaxException")
    public JAXBElement<URISyntaxException> createURISyntaxException(URISyntaxException value) {
        return new JAXBElement<URISyntaxException>(_URISyntaxException_QNAME, URISyntaxException.class, null, value);
    }

}
