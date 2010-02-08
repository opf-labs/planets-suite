/**
 * Access.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl;

public interface Access extends javax.xml.rpc.Service {
    public java.lang.String getAccessSoap12Address();

    public eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl.AccessSoap getAccessSoap12() throws javax.xml.rpc.ServiceException;

    public eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl.AccessSoap getAccessSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    public java.lang.String getAccessSoapAddress();

    public eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl.AccessSoap getAccessSoap() throws javax.xml.rpc.ServiceException;

    public eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl.AccessSoap getAccessSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
