package eu.planets_project.tb.impl.model.benchmark;

import java.io.Serializable;

import eu.planets_project.tb.api.model.benchmark.Benchmark;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;

//@Entity
public class BenchmarkGoalImpl implements BenchmarkGoal, Serializable {

	private int iWeight = -1;
	private String sValue = new String();
	private Benchmark benchmark;
	
	public BenchmarkGoalImpl(Benchmark benchmark) {
		this.benchmark = benchmark;
	}
	
	public String getID(){
		if (benchmark != null)
			return benchmark.getID();
		else
			return null;
	}
	
	public Benchmark getBenchmark() {
		return this.benchmark;
	}
	
	public void setBenchmark(Benchmark bm) {
		this.benchmark = bm;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.Benchmark#getValue()
	 */
	public String getValue() {
		return this.sValue;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.Benchmark#getWeight()
	 */
	public int getWeight() {
		return this.iWeight;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.Benchmark#setValue(java.lang.String)
	 */
	public void setValue(String value) {
		if(checkValueValid(value))
			this.sValue = value;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.Benchmark#setWeight(int)
	 */
	public void setWeight(int weight) {
		if(this.WEIGHT_MINIMUM<=weight&&weight<=this.WEIGHT_MAXIMUM){
			this.iWeight = weight;
		}
	}	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.benchmark.Benchmark#checkValueValid(java.lang.String)
	 */
	public boolean checkValueValid(String sValue) {
		boolean bRet = false;
		try{
			//type e.g. "java.lang.Integer"
			Class obj1 = Class.forName(this.benchmark.getType());
			try{
				//Integer
				if(obj1.isInstance(new Integer(10))){
					//if input is no Integer this will cause an exception
					Integer.valueOf(sValue);
					bRet = true;
				}
			}catch(Exception e){}
			try{
				//Long
				if(obj1.isInstance(new Long(10))){
					//if input is no Long this will cause an exception
					Long.valueOf(sValue);
					bRet = true;
				}
			}catch(Exception e){}
			try{
				//Float
				if(obj1.isInstance(new Float(10))){
					//if input is no Float this will cause an exception
					Float.valueOf(sValue);
					bRet = true;
				}
			}catch(Exception e){}
			try{
				//String
				if(obj1.isInstance(new String())){
					bRet = true;
				}
			}catch(Exception e){}
			try{
				//Boolean
				if(obj1.isInstance(new Boolean(true))){
					if(sValue.equals(Boolean.valueOf(sValue).toString())){
						bRet = true;
					}
				}
			}catch(Exception e){}
			
		}catch(Exception e){
			bRet = false;
		}
		return bRet;
	}

	
}
