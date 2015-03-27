/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
* 
* An R-based time series forecaster
* 
* ARIMA 101 is an ARIMA stochastic process model instance 
* parameterized with p = 1 as order of AR(p) process, 
* d = 0 as order of integration, q = 1 as order of MA(q) process. 
* In this case a stationary stochastic process is assumed (no integration) 
* and no seasonality considered.  
* 
* @author Andre van Hoorn, Nikolas Herbst
*******************************************************************************/

package tools.descartes.wcf.forecasting.strategies;

import tools.descartes.wcf.forecasting.AbstractRForecaster;
import tools.descartes.wcf.forecasting.ForecastStrategyEnum;
import tools.descartes.wcf.management.timeSeries.ITimeSeries;

public class ARIMA101Forecaster extends AbstractRForecaster {
	private final static String MODEL_FUNC_NAME = "arima"; // no explicit stochastic model
	private final static String FORECAST_FUNC_NAME = "forecast";
	private final static ForecastStrategyEnum strategy = ForecastStrategyEnum.ARIMA101;
	
	public ARIMA101Forecaster(final ITimeSeries<Double> historyTimeseries) {
		super(historyTimeseries, ARIMA101Forecaster.MODEL_FUNC_NAME, ARIMA101Forecaster.FORECAST_FUNC_NAME, ARIMA101Forecaster.strategy);
	}

	public ARIMA101Forecaster(final ITimeSeries<Double> historyTimeseries, final int confidenceLevel) {
		super(historyTimeseries, ARIMA101Forecaster.MODEL_FUNC_NAME, ARIMA101Forecaster.FORECAST_FUNC_NAME, confidenceLevel, ARIMA101Forecaster.strategy);
	}
	
	@Override
	protected String[] getModelFuncParams() {
		return new String[]{"c(1,0,1)", "method=\"CSS\""};
	}

	@Override
	protected String[] getForecastFuncParams() {
		return null; // no additional params required by this predictor
	}
}
