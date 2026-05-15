package com.model;

public class Candle {
	private String date;
	private double open;
	private double high;
	private double low;
	private double close;
	
	public Candle(String date,double open,double high,double low,double close) {
		this.date = date;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		}
	
	public String getDate() {
		return date;
	}
	
	public double getOpen() {
		return open;
	}
	
	public double getHigh() {
		return high;
	}
	
	public double getLow() {
		return low;
	}
	
	public double getClose() {
		return close;
	}
	
	//defining the body of the Candle
	public double body() {
		return Math.abs(open-close);
	}
	
	//defining the range of the candle
	public double range() {
		return high - low;
	}
	
	//defining the upper wick
	public double upperWick() {
		return high - Math.max(open,close);
	}
	
	//defining the lower wick
	public double lowerWick() {
		return Math.min(open,close) - low;
	}
	
	//defining bullish candles
	public boolean isBullish() {
		return close > open;
	}
	
	//defining bearish candles
	public boolean isBearish() {
		return close < open;
	}
	
	@Override
	public String toString() {
		return String.format("[%s O=%.5f H=%.5 L=%.5 C=%.5]",date,open,high,low,close);
	}
	

}
