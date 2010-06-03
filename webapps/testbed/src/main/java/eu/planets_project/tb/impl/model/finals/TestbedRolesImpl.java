/**
 * All the lookup is done via Java Reflection.
 * Convention that has to be followed:
 * Finals containing Testbed Roles have to start with "TESTBED_ROLE"
 * Finals containing Context have to start with "CONTEXT"
 * Finals containing Mapping_Roles_Context have to start with "MAPPING_TESTBED_ROLE"
 * 
 */
package eu.planets_project.tb.impl.model.finals;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * @author alindley
 *
 */
public class TestbedRolesImpl implements
		eu.planets_project.tb.api.model.finals.TestbedRoles {

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.finals.TestbedRoles#getAlLAvailableTestbedRoles(int)
	 */
	public List<String> getAlLAvailableRoleNames(int context) {
		Vector<String> vret = new Vector<String>();
		//get all fields of the ExperimentTypes Class via reflection
		Field[] fields = this.getClass().getFields();
		Field[] fields2 = this.getClass().getFields();
		for(int i=0; i<fields.length; i++){
			if (fields[i].getName().startsWith("TESTBED_ROLE")){
				//Now look for Mapping of the Role
				for(int j=0; j<fields2.length; j++){
					//e.g. MAPPING_TESTBED_ROLE_READER
					if (fields2[j].getName().equals("MAPPING_"+fields[i].getName())){
						//get the selected ContextType value of the mapping
						try {
							int iContextType = fields2[j].getInt(fields2[j]);
							//check if this is the type of context we're looking for
							if(iContextType == context){
								vret.addElement(fields[i].getName());
							}
							
						} catch (IllegalArgumentException e) {
							// TODO add logging statement
							//e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO add logging statement
							//e.printStackTrace();
						}
					}
				}
			}
		}
		return vret;
	}
	
	public List<String> getAllAvailableContextNames(){
		return getVariableNamesFor("CONTEXT", false);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.finals.TestbedRoles#getAllAvailableTestbedRoles()
	 */
	public List<String> getAllAvailableRoleNames() {
		return getVariableNamesFor("TESTBED_ROLE", false);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.finals.TestbedRoles#getRoleID(java.lang.String)
	 */
	public int getRoleID(String roleName) {
		// Use getVariableNamesFor with .equals and not contains (true), as we could possibly get more than one match:
		//e.g. TESTBED_ROLE_READER and TESTBED_ROLE_READER_AND_EXPERIMENTER
		Vector<Integer> vRoleName = getValuesFor(roleName, true);
		if (vRoleName.size()!=1){
			//error this may not occur, error in getValuesFor()
			return -1;
		}
		else{
			//return ID, which is stored on only position in Vector
			return vRoleName.get(0);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.finals.TestbedRoles#getRoleName(int)
	 */
	public String getRoleName(int roleID) {
		HashMap<Integer,String> hmMapping = this.getMappingVariableNamesForIDs("TESTBED_ROLE", false);
		if (hmMapping.containsKey(roleID)){
			return hmMapping.get(roleID);
		}
		else{
			//error this may not occur, error in getValuesFor()
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.finals.TestbedRoles#getAllAvailableContextIDs()
	 */
	public List<Integer> getAllAvailableContextIDs() {
		return getValuesFor("CONTEXT",false);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.finals.TestbedRoles#checkRoleIDisValid(int)
	 */
	public boolean checkRoleIDisValid(int inputRoleID) {
		Vector<Integer> vRoleIDs = getValuesFor("TESTBED_ROLE",false);
		return vRoleIDs.contains(inputRoleID);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.finals.TestbedRoles#checkRoleIDisValid(int, int)
	 */
	public boolean checkRoleIDisValid(int inputRoleID, int icontext) {
		boolean bRet= false;
		HashMap<Integer,String> hmRoles = this.getMappingVariableNamesForIDs("TESTBED_ROLE", false);
		if (!hmRoles.containsKey(inputRoleID)){
			//error this may not occur, error in getValuesFor()
			bRet = false;
		}
		else{
			//e.g. TESTBED_ROLE_USER
			String sRoleName = hmRoles.get(inputRoleID);
			HashMap<String,Integer> hmMapping = this.getMappingIDsForVariableNames("MAPPING_TESTBED_ROLE", false);
			bRet = hmMapping.containsKey("MAPPING_"+sRoleName);
		}
		return bRet;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.finals.TestbedRoles#getAllAvailableRoleIDs()
	 */
	public List<Integer> getAllAvailableRoleIDs() {
		return getValuesFor("TESTBED_ROLE",false);
	}
	
	/**
	 * This method is used to get all the corresponding values for given variables (roles) containing a certain String (sVnameStartsWith)
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
	 * This method is used to return all available variable names (e.g. roles) containing a certain String (sVnameStartsWith)
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
	 * This method is used to return all available variable names (e.g. role names) containing a certain String (sVnameStartsWith)
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
	 * This method is used to return all available variable ids (e.g. role ids) that's variable name matches a certain String (sVnameStartsWith)
	 * together with their corresponding variable name
	 * e.g. startsWith: "PLANETS_ROLE", returns all IDs of Planets Institutions together with their corresponding names.
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
