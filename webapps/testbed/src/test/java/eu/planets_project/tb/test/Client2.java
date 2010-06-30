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
package eu.planets_project.tb.test;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import eu.planets_project.tb.impl.TestBean;

public class Client2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.out.println("Reads Via Remote Interface");
			Context jndiContext = getInitialContext();
			//Object ref = jndiContext.lookup("java:eu/planets_project/tb/TestWizzard/local");
			TesterRemote dao_r = (TesterRemote) PortableRemoteObject.narrow(jndiContext.lookup("TestWizzard/remote"), TesterRemote.class);
			
			System.out.println("Before finding ID1");
			TestBean test_find1 = dao_r.findTestEntry(1);
			System.out.println("found ID "+test_find1.getId());
			System.out.println("found Name "+test_find1.getName());
			
			System.out.println("Before finding ID2");
			TestBean test_find2 = dao_r.findTestEntry(2);
			System.out.println("found ID "+test_find2.getId());
			System.out.println("found Name "+test_find2.getName());
			
			/*System.out.println("Via Local Interface");
			TesterLocal dao_l = (TesterLocal) jndiContext.lookup("TestWizzard/local");
			jndiContext.lookup("TestWizzard/local");
			
			Test test_find3 = dao_l.findTestEntry(3);
			System.out.println("ID "+test_find3.getId());
			System.out.println("Name "+test_find3.getName());
			*/
			
		} catch (NamingException e) {
			e.printStackTrace();
		}

	}
	
	public static Context getInitialContext() throws javax.naming.NamingException
	{
		return new javax.naming.InitialContext();
	}

}
