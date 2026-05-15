package com.model;

public class PatternMatch {
	private int index;
	private Candle candle;
	private String followUpDirection;
	
	public PatternMatch(int index,Candle candle,String followUpDirection) {
		this.index = index;
		this.candle = candle;
		this.followUpDirection = followUpDirection;
	}
	
	public int getIndex() {
		return index;
	}
	
	public Candle getCandle() {
		return candle;
	}
	
	public String getFollowUpDirection() {
		return followUpDirection;
	}

}
