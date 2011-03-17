/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
/**
 * 
 */
package eu.planets_project.ifr.core.common.mail;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.SSLContext;

/**
 * This is a 'Trusting' SSL socket factory that does not check the host certificate.
 * 
 * http://www.javaworld.com/javatips/jw-javatip115.html
 * 
 * @author AnJackson
 *
 */
public class TrustingSSLSocketFactory extends SSLSocketFactory {
  private SSLSocketFactory factory;
  public TrustingSSLSocketFactory() {
    System.out.println( "TrustingSocketFactory instantiated");
    try {
      SSLContext sslcontext = SSLContext.getInstance( "TLS");
      sslcontext.init( null, // No KeyManager required
          new TrustManager[] { new TrustingTrustManager()},
          new java.security.SecureRandom());
      factory = (SSLSocketFactory) sslcontext.getSocketFactory();
    } catch( Exception ex) {
      ex.printStackTrace();
    }
  }
  public static SocketFactory getDefault() {
    return new TrustingSSLSocketFactory();
  }
  public Socket createSocket( Socket socket, String s, int i, boolean 
flag)
      throws IOException {
    return factory.createSocket( socket, s, i, flag);
  }
  public Socket createSocket( InetAddress inaddr, int i,
      InetAddress inaddr1, int j) throws IOException {
    return factory.createSocket( inaddr, i, inaddr1, j);
  }
  public Socket createSocket( InetAddress inaddr, int i) throws 
IOException {
    return factory.createSocket( inaddr, i);
  }
  public Socket createSocket( String s, int i, InetAddress inaddr, int j)
      throws IOException {
    return factory.createSocket( s, i, inaddr, j);
  }
  public Socket createSocket( String s, int i) throws IOException {
    return factory.createSocket( s, i);
  }
  public Socket createSocket() throws IOException
  {
    // ANJ: 
    //  This method is required on my system, but leads to compile-time errors on others.
    //  Is seems to be because of different underlying implementations.
    //  Any implementation should support this method, and any that does not does not conform to JSSE and should be changed.
    //  See http://forum.java.sun.com/thread.jspa?threadID=701799&messageID=10139167#10139167
    // To Fix:
    //  Try switching to the Sun JRE or adding jsse.jar (part of the Sun JRE) to your class path.
    return factory.createSocket();
  }
  public String[] getDefaultCipherSuites() {
    return factory.getSupportedCipherSuites();
  }
  public String[] getSupportedCipherSuites() {
    return factory.getSupportedCipherSuites();
  }
  
}
