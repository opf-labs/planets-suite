package eu.planets_project.tb.api.model.benchmark;

public interface BenchmarkGoal {

	public static final int WEIGHT_MINIMUM 	= 0;
	public static final int WEIGHT_MEDIUM	 =3;
	public static final int WEIGHT_MAXIMUM 	= 5;	
	
	public String getID();
	
	public void setBenchmark(Benchmark bm);
	
	//Individual information for a BenchmarGoal instance
	public Benchmark getBenchmark();
	
	public void setValue(String sValue);
	public String getValue();
	
	public void setWeight(int iWeight);
	public int getWeight();
	
	/**
	 * Validates if the provided input matches the defined type. 
	 * @see setValue();
	 * java.lang.Integer, java.lang.Long, java.lang.Float, java.lang.String, java.Boolean
	 * @param sValue
	 * @return
	 */
	public boolean checkValueValid(String sValue);
}
