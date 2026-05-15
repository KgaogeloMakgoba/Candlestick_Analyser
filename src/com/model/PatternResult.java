package com.model;

import java.util.ArrayList;
import java.util.List;

public class PatternResult {
	private String patternName;
	private String bias;
	private String description;
	private String expectedFollowUp;
	private List<PatternMatch> matches;
	
	public PatternResult(String patternName,String bias,String description,String expectedFollowUp) {
		this.patternName = patternName;
		this.bias = bias;
		this.description = description;
		this.expectedFollowUp = expectedFollowUp;
		this.matches = new ArrayList<>();
	}
	
	//adding the PatternMatch into the List
	public void addMatch(PatternMatch m) {
		matches.add(m);
	}
	
	public String getPatternName() {
		return patternName;
	}
	
	public String getBias() {
		return bias;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getExpectedFollowUp() {
		return expectedFollowUp;
	}
	
	public List<PatternMatch> getMatch(){
		return matches;
	}
	
	//getting the count of the List contents
	public int getCount() {
		return matches.size();
	}
	
	//counting follow ups to patterns
	public long countFollowUp(String direction) {
		
		long count = 0;
		for(PatternMatch m: matches) {
			if(direction.equalsIgnoreCase(m.getFollowUpDirection())) {
				count++;
			}
		}
		return count;
	}
	
	//calculating the rate of follow ups
	public double confirmationRate() {
		if(expectedFollowUp == null || matches.isEmpty()) {
			return 0.0;
		}
		return (double) countFollowUp(expectedFollowUp)/matches.size()*100;
	}
	
	

}
