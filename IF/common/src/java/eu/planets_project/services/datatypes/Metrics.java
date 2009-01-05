package eu.planets_project.services.datatypes;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of individual @see Metric objects
 */
public class Metrics {
	
	/**
	 * default no arg constructor, just creates a new list
	 */
	public Metrics() {
		metrics = new ArrayList<Metric>();
	}

	List<Metric> metrics = null;
	
	/**
	 * @return the list of metric objects
	 */
	public List<Metric> getList() {
		return this.metrics;
	}
	
	/**
	 * @param metricsToSet
	 */
	public void setMetrics(List<Metric> metricsToSet) {
		this.metrics = metricsToSet;
	}
	
	/**
	 * @return the list of metric objects
	 */
	public List<Metric> getMetrics() {
		return this.metrics;
	}

	/**
	 * Add the passed metric to the list
	 * @param metric
	 */
	public void add(Metric metric) {
		if(this.metrics == null) {
			this.metrics = new ArrayList<Metric>();
			this.metrics.add(metric);
		}
		else {
			this.metrics.add(metric);
		}
	}
	
	/**
	 * Add a new metric created from passed params to the list
	 * @param mName
	 * @param mId
	 * @param mDescription
	 */
	public void add(String mName, String mId, String mDescription) {
		if(this.metrics==null) {
			metrics = new ArrayList<Metric> ();
			
			Metric metric = new Metric();
			metric.setName(mName);
			metric.setId(mId);
			metric.setDescription(mDescription);
			
			this.metrics.add(metric);
		}
	}
	
	/**
	 * Add a new metric with name only to the list
	 * @param mName
	 */
	public void add(String mName) {
		if(this.metrics==null) {
			metrics = new ArrayList<Metric> ();
			
			Metric metric = new Metric();
			metric.setName(mName);
			metric.setId("");
			metric.setDescription("");
			
			this.metrics.add(metric);
		}
	}
	
	/**
	 * @param index
	 * @return the metric at position [index]
	 */
	public Metric getMetric(int index) {
		return this.metrics.get(index);
	}
	
	/**
	 * @return the size() of the internal list
	 */
	public int size() {
		if(this.metrics!=null) {
			return this.metrics.size();
		}
		else {
			return -1;
		}
	}
}
