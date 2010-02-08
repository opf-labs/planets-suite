/**
 * AccessSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl;

public interface AccessSoap extends java.rmi.Remote {
    public boolean get(java.lang.String request) throws java.rmi.RemoteException;
    public java.lang.String submitAccessRequest(java.lang.String request) throws java.rmi.RemoteException;
}
