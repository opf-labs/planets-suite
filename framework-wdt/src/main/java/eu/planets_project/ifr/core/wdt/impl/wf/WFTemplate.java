package eu.planets_project.ifr.core.wdt.impl.wf;

 /**
	* This class represents a workflow template configuration 
	* @author Rainer Schmidt
	*/
	public class WFTemplate {
		
		//display name
		private String name = null;
		//associated view
		private String view = null;
		//associated managed bean instance
		private String beanInstance = null;
		
		public WFTemplate() {
		}
		
		/**
		* @param template name
		* @param view page, typically xhtml
		* @param the template's backing bean
		*/
		public WFTemplate(String name, String view, String bean) {
			this.name = name;
			this.view = view;
			this.beanInstance = bean;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public void setView(String view) {
			this.view = view;
		}
		
		public String getView() {
			return view;
		}
		
		public void setBeanInstance(String bean) {
			this.beanInstance = bean;
		}
		
		public String getBeanInstance() {
			return beanInstance;
		}
		
		public String toString() {
			return "wf:"+name;
		}		
	}