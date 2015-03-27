/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf.forecasting;

import tools.descartes.wcf.management.timeSeries.ITimeSeries;

/**
 * 
 * @author Andre van Hoorn
 *
 * @param <T>
 */
public interface IForecaster<T> {
	
	/**
	 * Performs a time series forecast for the given number of steps in the future. 
	 * 
	 * @param numForecastSteps
	 * @return
	 */
	public IForecastResult forecast (final int numForecastSteps);
	
	/**
	 * Returns the original time series used for the forecast.
	 * 
	 * @return
	 */
	public ITimeSeries<T> getTsOriginal();
	
	/**
 	 * Returns the confidence level to be computed for the forecast.
	 * 
	 * @return
	 */
	public int getConfidenceLevel();
}
