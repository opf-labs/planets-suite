/**
 * 
 */
package eu.planets_project.tb.unittest.model.finals;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import junit.framework.TestCase;
import eu.planets_project.tb.api.model.finals.ExperimentTypes;
import eu.planets_project.tb.impl.model.finals.ExperimentTypesImpl;

/**
 * @author alindley
 *
 */
public class ExperimentTypeTest extends TestCase{
	
	public void testGetExperimentTypeNames(){
		ExperimentTypes types = new ExperimentTypesImpl();
		List<String> vTypes = types.getAlLAvailableExperimentTypesNames();
		
		assertTrue(vTypes.size()>0);
		assertEquals(vTypes.size(),types.getAlLAvailableExperimentTypeIDs().size());
	}
	
	public void testGetExpeirmentTypeName(){
		ExperimentTypes types = new ExperimentTypesImpl();
		String sRetName = types.getExperimentTypeName(ExperimentTypes.EXPERIMENT_TYPE_SIMPLE_MIGRATION);
		
		Field[] fields = new ExperimentTypesImpl().getClass().getFields();
		boolean bFound = false;
		for(int i=0; i<fields.length; i++){
			if (fields[i].getName().startsWith("EXPERIMENT_TYPE")){
				String sVariableName = fields[i].getName();
				
				if (sVariableName.equals(sRetName)){
					bFound = true;
				}
			}
		}
		assertTrue(bFound);
	}
	
	
	public void testGetExpeirmentTypeNamePrettyPrint(){
		ExperimentTypes types = new ExperimentTypesImpl();
		String sRetName = types.getExpeirmentTypeName(ExperimentTypes.EXPERIMENT_TYPE_SIMPLE_MIGRATION, true);
		
		Field[] fields = new ExperimentTypesImpl().getClass().getFields();
		boolean bFound = false;
		for(int i=0; i<fields.length; i++){
			if (fields[i].getName().startsWith("EXPERIMENT_TYPE")){
				String sVariableName = fields[i].getName();
				
				if(sRetName.contains(" ")){
					StringTokenizer tokens = new StringTokenizer(sRetName," ",true);
					String sBuildVariableName = new String();
					while(tokens.hasMoreTokens()){
						String token = tokens.nextToken();
						if(token.equals(" ")){
							sBuildVariableName+="_";
						}else{
							sBuildVariableName+=token;
						}
					}
					if(sVariableName.equals("EXPERIMENT_TYPE_"+sBuildVariableName)){
						bFound = true;
					}
				}else{
					//only one name without ' ' was found
					if(sVariableName.equals("EXPERIMENT_TYPE_"+sRetName)){
						bFound = true;
					}
				}
			}
		}
	}
	
	public void testTypeNamesTokenizedCorrectly(){
		ExperimentTypes types = new ExperimentTypesImpl();
		
		Field[] fields = new ExperimentTypesImpl().getClass().getFields();
		for(int i=0; i<fields.length; i++){
			if (fields[i].getName().startsWith("EXPERIMENT_TYPE")){
				String sVariableName = fields[i].getName();
				
				//now use the method that extracts the pretty print name from the variable name
				List<String> vTypes = types.getAlLAvailableExperimentTypesNames(true);
				assertTrue(vTypes.size()>0);
				
				Iterator<String> itTypes = vTypes.iterator();
				boolean bFound = false;
				while(itTypes.hasNext()){
					String sTypeName = itTypes.next();
					
					//as I'm adding white spaces into the Name instead of '_'
					if(sTypeName.contains(" ")){
						StringTokenizer tokens = new StringTokenizer(sTypeName," ",true);
						String sBuildVariableName = new String();
						while(tokens.hasMoreTokens()){
							String token = tokens.nextToken();
							if(token.equals(" ")){
								sBuildVariableName+="_";
							}else{
								sBuildVariableName+=token;
							}
						}
						if(sVariableName.equals("EXPERIMENT_TYPE_"+sBuildVariableName)){
							bFound = true;
						}
					}else{
						//only one name without ' ' was found
						if(sVariableName.equals("EXPERIMENT_TYPE_"+sTypeName)){
							bFound = true;
						}
					}
				}
				assertTrue(bFound);
			}
		}
	}

}
