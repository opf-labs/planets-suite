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

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * These should throw CertificateExceptions if certs can't be traced.
 * Here, we assume all is well.
 * 
 * (@see javax.net.ssl.X509TrustManager)
 * 
 * http://www.javaworld.com/javatips/jw-javatip115.html
 * 
 * @author AnJackson
 *
 */
public class TrustingTrustManager implements X509TrustManager {
    
  public void checkClientTrusted( X509Certificate[] cert, String authType ) {
    return;
  }
  
  public void checkServerTrusted( X509Certificate[] cert, String authType ) {
    return;
  }
  
  public X509Certificate[] getAcceptedIssuers() {
    return new X509Certificate[ 0];
  }
}
