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
package eu.planets_project.tb.impl.model.finals;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.List;

/**
 * @author alindley
 *
 */
public class PlanetsInstitutionsImpl implements
		eu.planets_project.tb.api.model.finals.PlanetsInstitutions {

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.finals.PlanetsInstitutions#getAllAvailableInstitutions()
	 */
	public List<Integer> getAllAvailableInstitutionIDs() {
		return getValuesFor("PLANETS_INSTITUTION",false);
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.finals.PlanetsInstitutions#getAllAvailableInstitutionNames()
	 */
	public List<String> getAllAvailableInstitutionNames(){
		return getVariableNamesFor("PLANETS_INSTITUTION", false);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.finals.PlanetsInstitutions#getAllAvailablePartnerTypes()
	 */
	public List<String> getAllAvailablePartnerTypes() {
		return getVariableNamesFor("PLANETS_TYPE", false);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.finals.PlanetsInstitutions#getInstitutionsID(java.lang.String)
	 */
	public int getInstitutionsID(String name) {
		// Use getVariableNamesFor with .equals and not contains (true), as we could possibly get more than one match:
		//e.g. PLANETS_INSTITUTION_BL and e.g. PLANETS_INSTITUTION_BLN
		Vector<Integer> vRoleIDs = getValuesFor(name, true);
		if (vRoleIDs.size()!=1){
			//error this may not occur, error in getValuesFor()
			return -1;
		}
		else{
			//return ID, which is stored on only position in Vector
			return vRoleIDs.get(0);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.finals.PlanetsInstitutions#getInstitutionsName(int)
	 */
	public String getInstitutionsName(int institutionID) {
		// Use getVariableNamesFor with .equals and not contains (true), as we could possibly get more than one match:
		//e.g. PLANETS_TYPE_ARCHIVE and PLANETS_TYPE_ARCHIVE_STORAGE
		HashMap<Integer,String> list = this.getMappingVariableNamesForIDs("PLANETS_INSTITUTION", false);
		if (list.containsKey(institutionID)){
			return list.get(institutionID);
		}
		else{
			//error this may not occur, error in getValuesFor()
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.finals.PlanetsInstitutions#getInstitutionsParnterType(int)
	 */
	public int getInstitutionsTypeID(int institutionID) {
		HashMap<Integer,String> instIDNames = this.getMappingVariableNamesForIDs("PLANETS_INSTITUTION", false);
		String sInstitutionName = instIDNames.get(institutionID);
		Vector<Integer> typeID = this.getValuesFor("MAPPING_"+sInstitutionName, true);
		if (typeID.size()==1){
			return typeID.get(0);
		}
		else{
			return -1;
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.finals.PlanetsInstitutions#getInstitutionsPartnerTypeDescription(int)
	 */
	public String getInstitutionsTypeName(int institutionID) {
		HashMap<Integer,String> instIDNames = this.getMappingVariableNamesForIDs("PLANETS_INSTITUTION", false);
		String sInstitutionName = instIDNames.get(institutionID);
		Vector<String> typeName = this.getVariableNamesFor("MAPPING_"+sInstitutionName, true);
		if (typeName!=null&&typeName.size()==1){
			return typeName.get(0);
		}
		else{
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.finals.PlanetsInstitutions#getTypeID(java.lang.String)
	 */
	public int getTypeID(String name) {
		//name e.g. PLANETS_TYPE_LIBRARY
		Vector<Integer> typeID = this.getValuesFor(name, true);
		if (typeID.size()==1){
			return typeID.get(0);
		}
		else{
			return -1;
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.finals.PlanetsInstitutions#getTypeNames(int)
	 */
	public String getTypeName(int partnerTypeID) {
		HashMap<Integer,String> vMapping = this.getMappingVariableNamesForIDs("PLANETS_TYPE", false);
		if (vMapping.containsKey(partnerTypeID)){
			return vMapping.get(partnerTypeID);
		}
		else{
			//error this may not occur, error in getValuesFor()
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.finals.PlanetsInstitutions#checkInstitutionIDisValid(int)
	 */
	public boolean checkInstitutionIDisValid(int inputRoleID) {
		Vector<Integer> vRoleIDs = getValuesFor("PLANETS_INSTITUTION",false);
		return vRoleIDs.contains(inputRoleID);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.finals.PlanetsInstitutions#checkInstitutionTypeIsValid(int)
	 */
	public boolean checkInstitutionTypeIsValid(int inputInstTypeID) {
		Vector<Integer> vRoleIDs = getValuesFor("PLANETS_TYPE",false);
		return vRoleIDs.contains(inputInstTypeID);
	}
	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.finals.PlanetsInstitutions#getAllAvailableInstitutionNames(int)
	 */
	public List<String> getAllAvailableInstitutionNames(int instType) {
		Vector<String> vRet = new Vector<String>();
		HashMap<String, Integer> hmMappings = this.getMappingIDsForVariableNames("MAPPING_PLANETS_INSTITUTION", false);

		boolean bContains = hmMappings.containsValue(instType);
		if (!bContains){
			return null;
		}
		else{
			Iterator<String> itMappingNames =  hmMappings.keySet().iterator();
			while(itMappingNames.hasNext()){
				//e.g. MAPPING_PLANETS_INSTITUTION_ARC
				String sMappingName = itMappingNames.next();
				StringTokenizer tokens = new StringTokenizer(sMappingName,"_",true);
				String sInstName = "";
				
				if (tokens.nextToken().equals("MAPPING")){
					tokens.nextToken();
					String sToken = tokens.nextToken();
					if(sToken.equals("PLANETS")){
						String sToken2 = tokens.nextToken();
						//expecting token2 to be '_'
						String sToken3 = tokens.nextToken();
						if(sToken3.equals("INSTITUTION")){
							sInstName = sToken+sToken2+sToken3;
							while(tokens.hasMoreTokens()){
								sInstName+=tokens.nextToken();
							}
						}
					}
					//Name should now look like: PLANETS_INSTITUTION_ARC
					vRet.addElement(sInstName);
				}
			}
		}//END else
		
		return vRet;
	}
	
	
	/**
	 * This method is used to get all the corresponding values for given variables (institution names, types) containing a certain String (sVnameStartsWith)
	 * The boolean flag indicates whether "equals" (true) or "startsWith" (false) should be used to retrieve VariableName via Java reflection.
	 * 
	 * @param sVariableStartsWith
	 * @param exactMatch This flag indicates the use of "equals" instead of "startsWith"
	 * @return
	 */
	private Vector<Integer> getValuesFor(String sVariableStartsWith, boolean exactMatch){
		Vector<Integer> vret = new Vector<Integer>();
		//get all fields of the ExperimentTypes Class via reflection
		Field[] fields = this.getClass().getFields();
		for(int i=0; i<fields.length; i++){
			//use startsWith
			if(!exactMatch){
				if (fields[i].getName().startsWith(sVariableStartsWith)){
					int iValue;
					try {
						iValue = fields[i].getInt(fields[i]);
						vret.addElement(iValue);
						
					} catch (IllegalArgumentException e) {
						// TODO ADD Logging Statement
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO ADD Logging Statement
						e.printStackTrace();
					}
				}
			}
			//use equals
			else{
				if (fields[i].getName().equals(sVariableStartsWith)){
					int iValue;
					try {
						iValue = fields[i].getInt(fields[i]);
						vret.addElement(iValue);
						
					} catch (IllegalArgumentException e) {
						// TODO ADD Logging Statement
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO ADD Logging Statement
						e.printStackTrace();
					}
				}
			}
		}
		return vret;
	}
	
	/**
	 * This method is used to return all available variable names (e.g. institution names, types) containing a certain String (sVnameStartsWith)
	 * The boolean flag indicates whether "equals" (true) or "startsWith" (false) should be used to retrieve VariableName via Java reflection.
	 * @param sVnameStartsWith
	 * @param exactMatch This flag indicates the use of "equals" instead of "startsWith"
	 * @return
	 */
	private Vector<String> getVariableNamesFor(String sVnameStartsWith, boolean exactMatch){
		Vector<String> vret = new Vector<String>();
		//get all fields of the ExperimentTypes Class via reflection
		Field[] fields = this.getClass().getFields();
		for(int i=0; i<fields.length; i++){
			//use startsWith
			if (!exactMatch){
				if (fields[i].getName().startsWith(sVnameStartsWith)){
					vret.addElement(fields[i].getName());
				}	
			}
			//use equals
			else{
				if (fields[i].getName().equals(sVnameStartsWith)){
					vret.addElement(fields[i].getName());
				}	
			}
		}
		return vret;
	}
	
	/**
	 * This method is used to return all available variable names (e.g. institution names, types) containing a certain String (sVnameStartsWith)
	 * together with their corresponding ID.
	 * The boolean flag indicates whether "equals" (true) or "startsWith" (false) should be used to retrieve VariableName via Java reflection.
	 * @param sVNameStartsWith
	 * @param exactMatch This flag indicates the use of "equals" instead of "startsWith"
	 * @return
	 */
	private HashMap<String,Integer> getMappingIDsForVariableNames(String sVNameStartsWith, boolean exactMatch){
		HashMap<String,Integer> hmMapping = new HashMap<String,Integer>();
		//get all fields of the ExperimentTypes Class via reflection
		Field[] fields = this.getClass().getFields();
		for(int i=0; i<fields.length; i++){
			//use startsWith
			if (!exactMatch){
				if (fields[i].getName().startsWith(sVNameStartsWith)){
					//add VariableName as key and its ID as value
					int value = this.getValuesFor(fields[i].getName(), true).get(0);
					hmMapping.put(fields[i].getName(), value);
				}
			}
			//use equals
			else{
				if (fields[i].getName().equals(sVNameStartsWith)){
					//add VariableName as key and its ID as value
					int value = this.getValuesFor(fields[i].getName(), true).get(0);
					hmMapping.put(fields[i].getName(), value);
				}
			}
		}
		return hmMapping;
	}
	
	/**
	 * This method is used to return all available variable ids (e.g. institution ids) that's variable name matches a certain String (sVnameStartsWith)
	 * together with their corresponding variable name
	 * e.g. startsWith: "PLANETS_INSTITUTION", returns all IDs of Planets Institutions together with their corresponding names.
	 * The boolean flag indicates whether "equals" (true) or "startsWith" (false) should be used to retrieve VariableName via Java reflection.
	 * @param sVNameStartsWith
	 * @param exactMatch This flag indicates the use of "equals" (returns exact one value) instead of "startsWith"
	 * @return
	 */
	private HashMap<Integer,String> getMappingVariableNamesForIDs(String sVNameStartsWith, boolean exactMatch){
		HashMap<Integer,String> hmMapping = new HashMap<Integer,String>();
		//get all fields of the ExperimentTypes Class via reflection
		Field[] fields = this.getClass().getFields();
		for(int i=0; i<fields.length; i++){
			//use startsWith
			if (!exactMatch){
				if (fields[i].getName().startsWith(sVNameStartsWith)){
					//add IDs as key and its VariableName as value
					int value = this.getValuesFor(fields[i].getName(), true).get(0);
					hmMapping.put(value, fields[i].getName());
				}
			}
			//use equals: will only return exact one value
			else{
				if (fields[i].getName().equals(sVNameStartsWith)){
					//add VariableName as key and its ID as value
					int value = this.getValuesFor(fields[i].getName(), true).get(0);
					hmMapping.put(value, fields[i].getName());
				}
			}
		}
		return hmMapping;
	}

}
