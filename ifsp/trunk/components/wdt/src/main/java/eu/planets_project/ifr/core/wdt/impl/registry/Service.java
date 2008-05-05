package eu.planets_project.ifr.core.wdt.impl.registry;

import javax.xml.namespace.QName;

/**
	*
	* @author Rainer Schmidt
	*
	* represents a service object that can be stored in a registry
	* might be removed by a common class later
	*/
	public class Service {
		
		//unique registry identifier
		private String id = null;
		//display name
		private String name = null;
		//target namespace
		private String namespace = null;
		//associated endpoint url
		private String endpoint = null;
		//human readable description
		private String dsc = null;
		//Planets category
		private String category;
		// Qualified name
		private QName qName;
	
		
		public Service() {
		}
		
		public Service(String id, String name, String namespace, String endpoint, String category, QName qnamein) {

			this.id = id;
			this.name = name;
			this.namespace = namespace;
			this.endpoint = endpoint;
			this.category = category;
			this.qName = qnamein;
		}
		
		public void setId(String id) {
			this.id = id;
		}
		
		public String getId() {
			return id;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public void setNamespace(String namespace) {
			this.namespace = namespace;
		}
		
		public String getNamespace() {
			return namespace;
		}
		
		public void setEndpoint(String endpoint) {
			this.endpoint = endpoint;
		}
		
		public String getEndpoint() {
			return endpoint;
		}
		
		public void setDescription(String dsc) {
			this.dsc = dsc;
		}
		
		public String getDescription() {
			return dsc;
		}
		
		public void setCategory(String category) {
			this.category = category;
		}
		
		public String getCategory() {
			return category;
		}
		
		public String toString() {
			return "service [id="+id+", name="+name+", namespace="+namespace+", endpoint="+endpoint+", category="+category+"]";
		}

		public QName getQName() {
			return qName;
		}

		public void setQName(QName name) {
			qName = name;
		}	
	}