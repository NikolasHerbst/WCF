/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf.forecasting;

public class ForecastObjectives {
	
	
	/** maximum number of point forecast into the future
	 * 
	 * [1,5) 	short term
	 * [5,10) 	medium term
	 * >=10 	long term
	 * 
	 * forecast period = timeUnit * horizon
	 * 
	 * The value of Max_Horizon setting defines the 
	 * maximum number of time series points 
	 * to be forecasted. 
	 * A recommendation for this setting is the value 
	 * of the Frequency setting of the time series, 
	 * as a higher horizon setting may lead to broad 
	 * confidence intervals.  
	 */
	private int max_horizon = 30;
	
	/**
	 * The recent_horizon defines the number of time series points 
	 * to be forecasted at the beginning and can be dynamically 
	 * increased by period factors in the classification setting 
	 * up to the MaximumHorizon setting. 
	 * This value should be equal or higher than the 
	 * ForecastPeriod objective 
	 * (if continuous or even overlapping forecasts are needed).
	 */
	private int recent_horizon = 4;

	/** 
	 * Forecast period objective defines how often a forecast is 
	 * executed in times of new time series points. 
	 * For a value of 1 a forecast is requested every new time series point 
	 * and can be dynamically increased by period factors in the 
	 * classification setting to reach the configured maximum horizon. 
	 * This value should be equal or smaller than the StartHorizon 
	 * objective (if continuous or even overlapping forecasts are needed)
	 */
	private int period = 2;
	
	
	/** Overhead limits the selection of forecastStrategies according to max overhead
	 * 1 = low - naive, mean:
	 * 		These two strategies are only applied if less 
	 * 		than InitialSizeThreshold values are in the 
	 * 		time series. The arithmetic mean strategy can 
	 * 		have a forecast accuracy below 1 and therefore 
	 * 		be better than a solely reactive approach 
	 * 		using implicitly the na�ve strategy. 
	 * 		This is only true in cases of nearly constant 
	 * 		base level of the arrivals rates. 
	 * 		These strategies should be executed as 
	 * 		frequently as possible every new time series point.
	 * 2 = medium - CubicSmoothingSplines, ARIMA101, SimpleExponential Smoothing, Croston�s method for intermittent demands:
	 * 		The strengths of these strategies are the 
	 * 		low computational efforts below 100ms and their 
	 * 		ability to extrapolate the trend component. 
	 * 		They differ in sensitivity to noise level or 
	 * 		seasonal components. These strategies need 
	 * 		to be executed in a high frequency with small horizons. 	
	 * 3 = high - ExtendedExponential Smoothing, tBATS
	 *		The computational effort for both strategies 
	 * 		is below 30 sec for a maximum of 200 time series points. 
	 * 		They differ in the capabilities of modeling seasonal 
	 * 		components.  
	 * 4 = very high - ARIMA, tBATS
	 * 		The computational effort for the ARIMA approach 
	 * 		can reach up to 60 sec for a maximum of 
	 * 		200 time series points and may achieve smaller 
	 * 		confidence intervals than the tBATS approach.
	 * 
	 * The overhead objective defines the highest overhead group 
	 * from which the forecast strategies will be chosen. 
	 * A value of 2 may be sufficient if the time series data 
	 * have strong trend components that are not overlaid 
	 * by seasonal patterns, as the strength of group 2 strategies 
	 * is the trend extrapolation. 
	 * For time series with seasonal patterns, 
	 * a setting of 3 for a maximum forecast 
	 * computation time of 30 seconds and 4 for forecast 
	 * computation times below 1 minute is recommended. 
	 * 
	 */
	private int overhead = 4; 
	
	/**
	 * set the confidence level in percent (value between 0 and 99) for forecasts
	 */
	private int confidenceLevel = 80;

	/**
	 * @param max_horizon
	 * @param recent_horizon 
	 * @param period
	 * @param overhead
	 * @param confidence
	 */

	public ForecastObjectives(int max_horizon,int recent_horizon, int period, int overhead, int confidence){
		this.max_horizon = max_horizon;
		this.recent_horizon = recent_horizon;
		this.period = period;
		this.overhead = overhead;
		this.confidenceLevel = confidence;
	}
	
	public int getMaxHorizon() {
		return max_horizon;
	}
	
	
	public void setMaxHorizon(int max_horizon) {
		this.max_horizon = max_horizon;
	}
	
	public int getRecentHorizon() {
		return recent_horizon;
	}
	
	
	public void setRecentHorizon(int recent_horizon) {
		this.recent_horizon = recent_horizon;
	}
	
	
	public int getPeriod() {
		return period;
	}
	
	
	public void setPeriod(int period) {
		this.period = period;
	}
	
	
	public int getOverhead() {
		return overhead;
	}
	
	
	public void setOverhead(int overhead) {
		this.overhead = overhead;
	}

	public int getConfidenceLevel() {
		return confidenceLevel;
	}

	public void setConfidenceLevel(int confidenceLevel) {
		this.confidenceLevel = confidenceLevel;
	}
}
