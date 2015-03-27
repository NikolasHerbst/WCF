/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf.forecasting;

import tools.descartes.wcf.management.timeSeries.ITimeSeries;

public interface IForecastResult {
	
	/**
	 * Returns the point forecasts.
	 * 
	 * @return
	 */
	public ITimeSeries<Double> getForecast();
	
	/**
	 * Returns the confidence level for the forecast interval.
	 * 
	 * @return
	 */
	public int getConfidenceLevel();
	
	/**
	 * Returns the MeanAbsoluteScaledError of the forecast as quality metric
	 * 
	 * @return
	 */
	public double getMeanAbsoluteScaledError();
	
	/**
	 * Returns the upper limits for forecast interval with respect to the confidence level {@link #getConfidenceLevel()}. 
	 * 
	 * @return
	 */
	public ITimeSeries<Double> getUpper();
		
	/**
	 * Returns the lower limits for forecast interval with respect to the confidence level {@link #getConfidenceLevel()}. 
	 * 
	 * @return
	 */
	public ITimeSeries<Double> getLower();
	
	/**
	 * Returns the original time series that was the basis for the forecast
	 * 
	 * @return
	 */
	public ITimeSeries<Double> getOriginal();
	
	/**
	 * Returns the forecasting strategy that has been used for this forecast 
	 * 
	 * @return
	 */
	public ForecastStrategyEnum getFcStrategy();
	
	/**
	 * Returns the duration in [ms] of the executed forecast
	 * 
	 * @return
	 */
	public long getDuration();
	
	/**
	 * Returns whether the result is plausible - mean forecast bigger than 0 and smaller than 1.5*maximum
	 * 
	 * @return
	 */
	public boolean isPlausible();
	
}
