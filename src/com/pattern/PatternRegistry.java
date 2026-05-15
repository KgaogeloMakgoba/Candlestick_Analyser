package com.pattern;

import java.util.List;

import com.model.Candle;

/**
 * BEARISH PATTERNS
 */

class LongUpperShadow implements CandlePatterns{
	
	@Override
	public String getName() {
		return "Long Upper Shadow";
	}
	
	@Override
	public String getBias() {
		return "Bearish";
	}
	
	@Override
	public String getExpectedFollowUp() {
		return "Bearish";
	}
	
	@Override
	public String getDescription() {
		return "Upper wick that is grater than both the body and the lower wick";
	}
	
	@Override
	public boolean detect(List<Candle> c,int i) {
		Candle x = c.get(i);
		return x.body() > 0 && x.upperWick() > x.body() && x.upperWick() > x.lowerWick();
	}
}

class BearishEngulfing implements CandlePatterns{
	
	@Override
	public String getName() {
		return "Bearish Engulfing";
	}
	
	@Override
	public String getBias() {
		return "Bearish";
	}
	
	@Override
	public String getExpectedFollowUp() {
		return "Bearish";
	}
	
	@Override
	public String getDescription() {
		return "A candle fully engulfed by a Bearish candle";
	}
	
	@Override
	public boolean detect(List<Candle> c,int i) {
		if(i < 1)
			return false;
		Candle x = c.get(i-1);
		Candle y = c.get(i);
		return (x.isBearish() || x.isBullish()) && y.isBearish()
				&& y.getHigh() > x.getHigh() && y.getLow() < x.getLow();
	}
}

class SwingHigh implements CandlePatterns{
	
	@Override
	public String getName() {
		return "Swing High";
	}
	
	@Override
	public String getBias() {
		return "Bearish";
	}
	
	@Override
	public String getExpectedFollowUp() {
		return "Bearish";
	}
	
	@Override
	public String getDescription() {
		return "A middle candle with two candles that has lower highs beside it,its a three candle formation";
	}
	
	@Override
	public boolean detect(List<Candle> c,int i) {
		if(i < 2)
			return false;
		Candle x = c.get(i-2); //left candle
		Candle y = c.get(i-1); //middle candle
		Candle z = c.get(i);   //right candle
		return (x.isBearish() || x.isBullish()) && (y.isBearish() || y.isBullish())
				&& z.isBearish() && (y.getHigh() > x.getHigh() && y.getHigh() > z.getHigh())
				&& z.getLow() < y.getLow();
	}
}

/**
 * BULLISH PATTERNS
 */

class LongLowerShadow implements CandlePatterns{
	
	@Override
	public String getName() {
		return "Long Lower Shadow";
	}
	
	@Override
	public String getBias() {
		return "Bullish";
	}
	
	@Override
	public String getExpectedFollowUp() {
		return "Bullish";
	}
	
	@Override
	public String getDescription() {
		return "Lower wick that is grater than both the body and the upper wick";
	}
	
	@Override
	public boolean detect(List<Candle> c,int i) {
		Candle x = c.get(i);
		return x.body() > 0 && x.lowerWick() > x.body() && x.lowerWick() > x.upperWick();
	}
}

class BullishEngulfing implements CandlePatterns{
	
	@Override
	public String getName() {
		return "Bullish Engulfing";
	}
	
	@Override
	public String getBias() {
		return "Bullish";
	}
	
	@Override
	public String getExpectedFollowUp() {
		return "Bullish";
	}
	
	@Override
	public String getDescription() {
		return "A candle fully engulfed by a Bullish candle";
	}
	
	@Override
	public boolean detect(List<Candle> c,int i) {
		if(i < 1)
			return false;
		Candle x = c.get(i-1);
		Candle y = c.get(i);
		return (x.isBearish() || x.isBullish()) && y.isBullish()
				&& y.getLow() < x.getLow() && y.getHigh() > x.getHigh();
	}
}


class SwingLow implements CandlePatterns{
	
	@Override
	public String getName() {
		return "Swing Low";
	}
	
	@Override
	public String getBias() {
		return "Bullish";
	}
	
	@Override
	public String getExpectedFollowUp() {
		return "Bullish";
	}
	
	@Override
	public String getDescription() {
		return "A middle candle with two candles that has higher lows beside it,its a three candle formation";
	}
	
	@Override
	public boolean detect(List<Candle> c,int i) {
		if(i < 2)
			return false;
		Candle x = c.get(i-2); //left candle
		Candle y = c.get(i-1); //middle candle
		Candle z = c.get(i);   //right candle
		return (x.isBearish() || x.isBullish()) && (y.isBearish() || y.isBullish())
				&& z.isBullish() && (y.getLow() < x.getLow() && y.getLow() < z.getLow())
				&& z.getHigh() < y.getHigh();
	}
}

public class PatternRegistry {
	
	private PatternRegistry() {
		
	}
	
	public static List<CandlePatterns> all(){
		return List.of(new LongUpperShadow(),new BearishEngulfing(),new SwingHigh(),
				       new LongLowerShadow(),new BullishEngulfing(),new SwingLow());
		
	}
	

}
