package eu.planets_project.ifr.core.wdt.impl;

public class BPELRewriter_Impl {

	
	private static BPELRewriter_Impl instance = null;
	
	 
	/**
	 * Private constructor 
	 */
	private BPELRewriter_Impl () {
		
		
	}
	
	
	/**
	 * Returns an instance of the BPELRewriter
	 * @return
	 */
	public BPELRewriter_Impl getInstance() {
		if (this.instance == null) {
			return new BPELRewriter_Impl();
		}
		else
			return this.instance;
	}
	
	
	
	
	
}
