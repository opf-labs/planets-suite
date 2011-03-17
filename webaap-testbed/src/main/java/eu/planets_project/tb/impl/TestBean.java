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
package eu.planets_project.tb.impl;

import java.util.Hashtable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;

@Entity
public class TestBean implements java.io.Serializable{
	
	private int id;
	private String name;
	
    @Lob
    @Column(columnDefinition=ExperimentPersistencyImpl.BLOB_TYPE)
	private Hashtable<Integer,String> htable;
	
	public TestBean(){
		htable = new Hashtable<Integer,String>();
	}
	
	/*
	 * Every Persisted Value of this Entity must contain either a private or public
	 * setter and getter. 
	 */
	@Id
	public int getId(){
		return this.id;
	}
	
	public void setId(int id){
		this.id=id;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	@SuppressWarnings("unused")
	private void setHashtable(Hashtable<Integer,String> htable){
		this.htable = htable;
	}
	
	@SuppressWarnings("unused")
	private Hashtable<Integer,String> getHashtable(){
		return this.htable;
	}
	
	public void setKeyValuePairs(int htablekey, String sValue){
		boolean bcontained = htable.containsKey(htablekey);
		if (!bcontained){
			htable.put(htablekey,sValue);
		}
	}
	
	public String getKeyValueOfPair(int htableKey){
		boolean contains = htable.containsKey(htableKey);
		if (contains){
			return htable.get(htableKey);
		}
		return null;
	}
	
}
