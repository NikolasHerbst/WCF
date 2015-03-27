/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
* 
* An R-based time series forecaster which computes a forecast based on exponential smoothing.
* 1st step: model estimation: noise, trend and season components are either additive (A), 
* or multiplicative (M) or not modeled (N) 
* 2nd step: estimation of parameters for an explicit noise, trend and seasonal components 
* 3rd step: calculation of point forecasts for level, trend and season components independently 
* using SES and combination of results
* @author Andre van Hoorn, Nikolas Herbst
* 
*******************************************************************************/

package tools.descartes.wcf.forecasting.strategies;

import tools.descartes.wcf.forecasting.AbstractRForecaster;
import tools.descartes.wcf.forecasting.ForecastStrategyEnum;
import tools.descartes.wcf.management.timeSeries.ITimeSeries;

public class ETSForecaster extends AbstractRForecaster {
	private final static String MODEL_FUNC_NAME = "ets"; // no explicit stochastic model
	private final static String FORECAST_FUNC_NAME = "forecast";
	private final static ForecastStrategyEnum strategy = ForecastStrategyEnum.ETS;
	
	public ETSForecaster(final ITimeSeries<Double> historyTimeseries) {
		super(historyTimeseries, ETSForecaster.MODEL_FUNC_NAME, ETSForecaster.FORECAST_FUNC_NAME, ETSForecaster.strategy);
	}

	public ETSForecaster(final ITimeSeries<Double> historyTimeseries, final int confidenceLevel) {
		super(historyTimeseries, ETSForecaster.MODEL_FUNC_NAME, ETSForecaster.FORECAST_FUNC_NAME, confidenceLevel, ETSForecaster.strategy);
	}
	
	@Override
	protected String[] getModelFuncParams() {
		return null; // no additional params required by this predictor
	}

	@Override
	protected String[] getForecastFuncParams() {
		return null; // no additional params required by this predictor
	}
}
