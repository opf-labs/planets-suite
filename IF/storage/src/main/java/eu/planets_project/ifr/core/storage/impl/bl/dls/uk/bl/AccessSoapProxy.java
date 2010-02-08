package eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl;

public class AccessSoapProxy implements eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl.AccessSoap {
  private String _endpoint = null;
  private eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl.AccessSoap accessSoap = null;
  
  public AccessSoapProxy() {
    _initAccessSoapProxy();
  }
  
  public AccessSoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initAccessSoapProxy();
  }
  
  private void _initAccessSoapProxy() {
    try {
      accessSoap = (new eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl.AccessLocator()).getAccessSoap();
      if (accessSoap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)accessSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)accessSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (accessSoap != null)
      ((javax.xml.rpc.Stub)accessSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public eu.planets_project.ifr.core.storage.impl.bl.dls.uk.bl.AccessSoap getAccessSoap() {
    if (accessSoap == null)
      _initAccessSoapProxy();
    return accessSoap;
  }
  
  public boolean get(java.lang.String request) throws java.rmi.RemoteException{
    if (accessSoap == null)
      _initAccessSoapProxy();
    return accessSoap.get(request);
  }
  
  public java.lang.String submitAccessRequest(java.lang.String request) throws java.rmi.RemoteException{
    if (accessSoap == null)
      _initAccessSoapProxy();
    return accessSoap.submitAccessRequest(request);
  }
  
  
}