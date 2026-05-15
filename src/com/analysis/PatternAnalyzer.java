package com.analysis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.model.Candle;
import com.model.PatternMatch;
import com.model.PatternResult;
import com.pattern.CandlePatterns;
import com.pattern.PatternRegistry;

public class PatternAnalyzer {
	
	private List<CandlePatterns> patterns;
	
	public PatternAnalyzer() {
		this.patterns = PatternRegistry.all();
	}
	
    /**
     * Scans all candles and detects every registered pattern,
	 * then record the follow up candle's direction for each occurrence
     * @param candles
     * @return
     */
	
	public List<PatternResult> analyzer(List<Candle> candles){
		//where results will be held
		Map<String,PatternResult> resultMap = new LinkedHashMap<>();
		
		for(CandlePatterns p : patterns) {
			resultMap.put(p.getName(),new PatternResult(p.getName(),p.getBias(),p.getDescription(),p.getExpectedFollowUp()));	
		}
		
		//scanning every candle
		for(int i = 0;i < candles.size();i++) {
			for(CandlePatterns p : patterns) {
				if(p.detect(candles, i)) {
					String followUp = resolveFollowUp(candles,i);
					resultMap.get(p.getName()).addMatch(new PatternMatch(i,candles.get(i),followUp));
				}
			}
		}
		
		return new ArrayList<>(resultMap.values());
			
	}
	
    /**
     * Returns the  direction of the next candle
     * @param candles
     * @param i
     * @return
     */
	private String resolveFollowUp(List<Candle> candles,int i) {
		if(i + 1 >= candles.size())
			return "N/A";
		Candle next = candles.get(i + 1);
		if(next.getClose() > next.getOpen())
			return "Bullish";
		if(next.getClose() < next.getOpen())
			return "Bearish";
		return "Neutral";
		
	}
	

}
