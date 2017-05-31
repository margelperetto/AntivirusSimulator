package org.teste.avs.monitor;

import java.io.File;

public class MonitoringRule {

	private String match;
	private MatchType matchType = MatchType.EQUAL_TO;
	private RuleType ruleType = RuleType.FILE_NAME;
	
	public MonitoringRule(String match, MatchType matchType, RuleType ruleType) {
		this.match = match;
		this.matchType = matchType;
		this.ruleType = ruleType;
	}
	
	public MonitoringRule() {
	}
	
	public String getMatch() {
		return match;
	}
	
	public void setMatch(String match) {
		this.match = match;
	}
	
	public MatchType getMatchType() {
		return matchType;
	}
	
	public void setMatchType(MatchType type) {
		this.matchType = type;
	}
	
	public boolean isMatchedTo(File file){
		String fileStr = ruleType.getStringToMatch(file);
		return matchType.match(fileStr, match);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof MonitoringRule)){
			return false;
		}
		MonitoringRule  other = (MonitoringRule)obj;
		return this.match.equals(other.match) && this.matchType.equals(other.matchType);
	}
	
	public RuleType getRuleType() {
		return ruleType;
	}

	public void setRuleType(RuleType ruleType) {
		this.ruleType = ruleType;
	}

	public static enum RuleType{
		FILE_NAME() {
			@Override
			public String getStringToMatch(File file) {
				return file.getName();
			}
		}, 
		PATH() {
			@Override
			public String getStringToMatch(File file) {
				return file.getParent();
			}
		}, 
		ALL() {
			@Override
			public String getStringToMatch(File file) {
				return file.getAbsolutePath();
			}
		};

		public abstract String getStringToMatch(File file);
	}
	
	public static enum MatchType{
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
		},
		CONTAINS(){
			@Override
			public boolean match(String fileName, String match) {
				return fileName.contains(match);
			}
		}
		;
		
		public abstract boolean match(String fileName, String match);
	}

}
