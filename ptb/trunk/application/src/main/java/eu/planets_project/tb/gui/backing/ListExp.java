package eu.planets_project.tb.gui.backing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIData;

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.gui.UserBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.gui.util.SortableList;
import java.util.Collection;


public class ListExp extends SortableList {

	private List<Experiment> exps = new ArrayList<Experiment>();
	private String column = "name";
	private boolean ascending = true;
	//private UIData data = null;
	
	public ListExp()
	{
		super("name");
                exps = this.getExperimentsOfUser();

	}
	
	  public List getExperimentsOfUser()
	  {
	  // Get all experiments created/owned by the current user  
	  // get user id from MB facility
	  // get UserBean - grab userid   
	  List<Experiment> usersExpList = new ArrayList<Experiment>();
	  UserBean managedUserBean = (UserBean)JSFUtil.getManagedObject("UserBean");  
	     
	  String userid = managedUserBean.getUserid();
	     
	  TestbedManager testbedMan = (TestbedManager)JSFUtil.getManagedObject("TestbedManager");
	  
	  Iterator<Experiment> iter = testbedMan.getAllExperiments().iterator();
	  
	  while (iter.hasNext()) {
		  Experiment exp = iter.next();
		  if (userid.equals(exp.getExperimentSetup().getBasicProperties().getExperimenter()))
			  usersExpList.add(exp);
	  }
	  exps = usersExpList;	
	  sort(getSort(), isAscending());
	  return exps;
	  }
          
          public int getNumExperimentsOfUser()
          {
              int num = exps.size();
              
              return num; 
          }
          
	  public Collection getAllExperiments()
	  {
	  // Get all experiments 
	  Collection<Experiment> allExps = new ArrayList<Experiment>();
	     
	  TestbedManager testbedMan = (TestbedManager)JSFUtil.getManagedObject("TestbedManager");
	  
	  allExps = testbedMan.getAllExperiments();
	  
	  return allExps;
	  }
          
          public int getNumAllExperiments()
          {
              Collection<Experiment> allExps = this.getAllExperiments();
              
              int num = allExps.size();
              
              return num; 
          }
	  
		protected void sort(final String column, final boolean ascending)
		{
			this.column = column;
			this.ascending = ascending;
			Comparator<Object> comparator = new MyComparator();
			Collections.sort(exps, comparator);
		}

		protected boolean isDefaultAscending(String sortColumn)
		{
			return true;
		}
		
		class MyComparator implements Comparator<Object> {

			public int compare(Object o1, Object o2)
			{
				Experiment c1 = (Experiment) o1;
				Experiment c2 = (Experiment) o2;
				if (column == null)
				{
					return 0;
				}
				if (column.equals("name"))
				{
					return ascending ? c1.getExperimentSetup().getBasicProperties().getExperimentName().compareTo(c2.getExperimentSetup().getBasicProperties().getExperimentName()) : c2.getExperimentSetup().getBasicProperties().getExperimentName()
									.compareTo(c1.getExperimentSetup().getBasicProperties().getExperimentName());
				}					
				else
					return 0;
			}			
		}
		
//		 Property getters - setters

/*		  public void setData(UIData data)
		  {
		    this.data = data;
		  }


		  public UIData getData()
		  {
		    return data;
		  }
*/
}
