package eu.planets_project.services.datatypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Metrics {
	
	public Metrics() {
		metrics = new ArrayList<Metric>();
	}

	List<Metric> metrics = null;
	
	public List<Metric> getList() {
		return this.metrics;
	}
	
	public void setMetrics(List<Metric> metricsToSet) {
		this.metrics = metricsToSet;
	}
	
	public void add(Metric metric) {
		if(this.metrics == null) {
			this.metrics = new ArrayList<Metric>();
			this.metrics.add(metric);
		}
		else {
			this.metrics.add(metric);
		}
	}
	
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
	
	public Metric getMetric(int index) {
		return this.metrics.get(index);
	}
	
	public int size() {
		if(this.metrics!=null) {
			return this.metrics.size();
		}
		else {
			return -1;
		}
	}
}
