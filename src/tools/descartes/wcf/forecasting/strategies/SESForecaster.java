/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
* 
* Generalization of MA by using weights according to the exponential function 
* to give higher weight to more recent values. 
* 1st step: estimation of parameters for weights/exp. function 
*  2nd step: calculation of weighted averages as point forecast
* @author Andre van Hoorn, Nikolas Herbst
* 
*******************************************************************************/

package tools.descartes.wcf.forecasting.strategies;

import tools.descartes.wcf.forecasting.AbstractRForecaster;
import tools.descartes.wcf.forecasting.ForecastStrategyEnum;
import tools.descartes.wcf.management.timeSeries.ITimeSeries;

public class SESForecaster extends AbstractRForecaster {
	private final static String MODEL_FUNC_NAME = "ets"; // no explicit stochastic model
	private final static String FORECAST_FUNC_NAME = "forecast";
	private final static ForecastStrategyEnum strategy = ForecastStrategyEnum.SES;
		
	public SESForecaster(final ITimeSeries<Double> historyTimeseries) {
		super(historyTimeseries, SESForecaster.MODEL_FUNC_NAME, SESForecaster.FORECAST_FUNC_NAME, SESForecaster.strategy);
	}

	public SESForecaster(final ITimeSeries<Double> historyTimeseries, final int confidenceLevel) {
		super(historyTimeseries, SESForecaster.MODEL_FUNC_NAME, SESForecaster.FORECAST_FUNC_NAME, confidenceLevel, SESForecaster.strategy);
	}
		
	@Override
	protected String[] getModelFuncParams() {
		return new String[]{"model=\"ANN\""};
	}
	

/** From R Forecast documentation:
 * Usually a three-character string identifying method 
 * using the framework terminology of Hyndman et al. (2002) and Hyndman et al. (2008). 
 * The first letter denotes the error type ("A", "M" or "Z"); 
 * the second letter denotes the trend type ("N","A","M" or "Z"); 
 * and the third letter denotes the season type ("N","A","M" or "Z"). 
 * In all cases, "N"=none, "A"=additive, "M"=multiplicative and "Z"=automatically selected. 
 * So, for example, "ANN" is simple exponential smoothing with additive errors, 
 * "MAM" is multiplicative Holt-Winters' method with multiplicative errors, and so on. 
 * It is also possible for the model to be equal to the output from a previous call to ets. 
 * In this case, the same model is fitted to y without re-estimating any parameters.
 */
	
	
	
	@Override
	protected String[] getForecastFuncParams() {
		return null; // no additional params required by this predictor
	}
}
