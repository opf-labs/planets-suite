package eu.planets_project.tb.api.model.eval;

public enum TBEvaluationTypes {
	VERYGOOD("very good"), 
	GOOD("good"),
	BAD("bad"),
	VERYBAD("very bad");
	private String screenName;
	
	TBEvaluationTypes(String screenName){
		this.screenName = screenName;
	}
	
	public String screenName(){
		return this.screenName;
	}
}
