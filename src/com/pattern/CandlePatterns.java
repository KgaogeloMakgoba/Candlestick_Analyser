package com.pattern;

import java.util.List;

import com.model.Candle;

/**
 * Every candlestick pattern implements this interface
 */
public interface CandlePatterns {
	
	//the name of the Candlestick pattern
	String getName();
	
	//description of the pattern
	String getDescription();
	
	String getBias();
	
	//the expected follow up direction
	//helps calculate the confirmation rate
	String getExpectedFollowUp();
	
	//return true if the pattern is formed at index i
	boolean detect(List<Candle> candles,int i);
	

}
