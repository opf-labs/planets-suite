/**
 * ExperimentTypes functions as getAllExperimentTypes(), are casted by the variable's starting with "EXPERIMENT_TYPE"
 */
package eu.planets_project.tb.impl.model.finals;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Vector;

/**
 * @author alindley
 *
 */
public class ExperimentTypesImpl implements
		eu.planets_project.tb.api.model.finals.ExperimentTypes {

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.finals.ExperimentTypes#getAlLAvailableExperimentTypes()
	 */
	public Vector<String> getAlLAvailableExperimentTypesNames() {
		Vector<String> vret = new Vector<String>();
		//get all fields of the ExperimentTypes Class via reflection
		Field[] fields = this.getClass().getFields();
		for(int i=0; i<fields.length; i++){
			if (fields[i].getName().startsWith("EXPERIMENT_TYPE")){
				vret.addElement(fields[i].getName());
			}
		}
		return vret;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.finals.ExperimentTypes#getExperimentTypeID(java.lang.String)
	 */
	public int getExperimentTypeID(String sTypeName) {
		int iRet = -1;
		
		try {
			//e.g. find EXPERIMENT_TYPE_SIMPLEMIGRATION
			Field fields = this.getClass().getField(sTypeName);
			
			try {
				//get the selected value of the final
				iRet = fields.getInt(fields);
			} catch (IllegalArgumentException e) {
				// TODO Add logging statement
				//e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Add logging statement
				//e.printStackTrace();
			}
			
		} catch (SecurityException e1) {
			// TODO Add logging statement
			//e1.printStackTrace();
		} catch (NoSuchFieldException e1) {
			// TODO Add logging statement
			//e1.printStackTrace();
		}
		return iRet;
		
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.finals.ExperimentTypes#getExperimentTypeName(int)
	 */
	public String getExperimentTypeName(int typeID) {
		String sRet = null;
		//get all fields of the ExperimentTypes Class via reflection
		Field[] fields = this.getClass().getFields();
		for(int i=0; i<fields.length; i++){
			if (fields[i].getName().startsWith("EXPERIMENT_TYPE")){
				int iValue;
				try {
					iValue = fields[i].getInt(fields[i]);
					
					//check if this value is the typeID whe're looking for
					if(iValue==typeID)
						sRet = fields[i].getName();
					
				} catch (IllegalArgumentException e) {
					// TODO ADD Logging Statement
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO ADD Logging Statement
					e.printStackTrace();
				}

			}
		}
		return sRet;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.finals.ExperimentTypes#getAlLAvailableExperimentTypeIDs()
	 */
	public Vector<Integer> getAlLAvailableExperimentTypeIDs() {
		Vector<Integer> vret = new Vector<Integer>();
		//get all fields of the ExperimentTypes Class via reflection
		Field[] fields = this.getClass().getFields();
		for(int i=0; i<fields.length; i++){
			if (fields[i].getName().startsWith("EXPERIMENT_TYPE")){
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
		return vret;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.finals.ExperimentTypes#checkExperimentTypeValid(int)
	 */
	public boolean checkExperimentTypeIDisValid(int typeID) {
		Vector<Integer> itGivenTypeIDs = this.getAlLAvailableExperimentTypeIDs();
		return itGivenTypeIDs.contains(typeID);
	}

}
