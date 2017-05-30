package org.teste.avs.monitor;

public class MonitoringRule {

	private String match;
	private RuleType type = RuleType.EQUAL_TO;
	
	public MonitoringRule(String match, RuleType type) {
		this.match = match;
		this.type = type;
	}
	
	public MonitoringRule() {
	}
	
	public String getMatch() {
		return match;
	}
	
	public void setMatch(String match) {
		this.match = match;
	}
	
	public RuleType getType() {
		return type;
	}
	
	public void setType(RuleType type) {
		this.type = type;
	}
	
	public boolean isMatchedTo(String fileName){
		return type.match(fileName, match);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof MonitoringRule)){
			return false;
		}
		MonitoringRule  other = (MonitoringRule)obj;
		return this.match.equals(other.match) && this.type.equals(other.type);
	}
	
	public static enum RuleType{
		EQUAL_TO(){
			@Override
			public boolean match(String fileName, String match) {
				return fileName.equals(match);
			}
		},
		STARTS_WITH(){
			@Override
			public boolean match(String fileName, String match) {
				return fileName.startsWith(match);
			}
		},
		ENDS_WITH(){
			@Override
			public boolean match(String fileName, String match) {
				return fileName.endsWith(match);
			}
		}
		;
		
		public abstract boolean match(String fileName, String match);
	}

}
