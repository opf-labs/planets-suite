/**
 * AccessLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl;

public class AccessLocator extends org.apache.axis.client.Service implements eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl.Access {

    public AccessLocator() {
    }


    public AccessLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public AccessLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for AccessSoap12
    private java.lang.String AccessSoap12_address = "http://localhost:1824/Access.asmx";

    public java.lang.String getAccessSoap12Address() {
        return AccessSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String AccessSoap12WSDDServiceName = "AccessSoap12";

    public java.lang.String getAccessSoap12WSDDServiceName() {
        return AccessSoap12WSDDServiceName;
    }

    public void setAccessSoap12WSDDServiceName(java.lang.String name) {
        AccessSoap12WSDDServiceName = name;
    }

    public eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl.AccessSoap getAccessSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(AccessSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getAccessSoap12(endpoint);
    }

    public eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl.AccessSoap getAccessSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
        	eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl.AccessSoap12Stub _stub = new eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl.AccessSoap12Stub(portAddress, this);
            _stub.setPortName(getAccessSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setAccessSoap12EndpointAddress(java.lang.String address) {
        AccessSoap12_address = address;
    }


    // Use to get a proxy class for AccessSoap
    private java.lang.String AccessSoap_address = "http://localhost:1824/Access.asmx";

    public java.lang.String getAccessSoapAddress() {
        return AccessSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String AccessSoapWSDDServiceName = "AccessSoap";

    public java.lang.String getAccessSoapWSDDServiceName() {
        return AccessSoapWSDDServiceName;
    }

    public void setAccessSoapWSDDServiceName(java.lang.String name) {
        AccessSoapWSDDServiceName = name;
    }

    public eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl.AccessSoap getAccessSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(AccessSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getAccessSoap(endpoint);
    }

    public eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl.AccessSoap getAccessSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
        	eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl.AccessSoapStub _stub = new eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl.AccessSoapStub(portAddress, this);
            _stub.setPortName(getAccessSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setAccessSoapEndpointAddress(java.lang.String address) {
        AccessSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     * This service has multiple ports for a given interface;
     * the proxy implementation returned may be indeterminate.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl.AccessSoap.class.isAssignableFrom(serviceEndpointInterface)) {
            	eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl.AccessSoap12Stub _stub = new eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl.AccessSoap12Stub(new java.net.URL(AccessSoap12_address), this);
                _stub.setPortName(getAccessSoap12WSDDServiceName());
                return _stub;
            }
            if (eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl.AccessSoap.class.isAssignableFrom(serviceEndpointInterface)) {
            	eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl.AccessSoapStub _stub = new eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl.AccessSoapStub(new java.net.URL(AccessSoap_address), this);
                _stub.setPortName(getAccessSoapWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("AccessSoap12".equals(inputPortName)) {
            return getAccessSoap12();
        }
        else if ("AccessSoap".equals(inputPortName)) {
            return getAccessSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://bl.uk/", "Access");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://bl.uk/", "AccessSoap12"));
            ports.add(new javax.xml.namespace.QName("http://bl.uk/", "AccessSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("AccessSoap12".equals(portName)) {
            setAccessSoap12EndpointAddress(address);
        }
        else 
if ("AccessSoap".equals(portName)) {
            setAccessSoapEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
