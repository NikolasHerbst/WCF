/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
* 
* An R-based time series forecaster, auto-arima model selection
* @author Andre van Hoorn + Nikolas Herbst
* 
* The automated ARIMA model selection process of the R forecasting package starts 
* with a complex estimation of an appropriate ARIMA(p, d, q)(P, D, Q)m model by using 
* unit-root tests and an information criterions (like the AIC) in combination with 
* a step-wise procedure for traversing a relevant model space. 
* The selected ARIMA model is then fitted to the data to provide point forecasts 
* and confidence intervals.
*******************************************************************************/

package tools.descartes.wcf.forecasting.strategies;

import tools.descartes.wcf.forecasting.AbstractRForecaster;
import tools.descartes.wcf.forecasting.ForecastStrategyEnum;
import tools.descartes.wcf.management.timeSeries.ITimeSeries;

public class ARIMAForecaster extends AbstractRForecaster {
	private final static String MODEL_FUNC_NAME = "auto.arima"; // no explicit stochastic model
	private final static String FORECAST_FUNC_NAME = "forecast";
	private final static ForecastStrategyEnum strategy = ForecastStrategyEnum.ARIMA;
	
	public ARIMAForecaster(final ITimeSeries<Double> historyTimeseries) {
		super(historyTimeseries, ARIMAForecaster.MODEL_FUNC_NAME, ARIMAForecaster.FORECAST_FUNC_NAME, ARIMAForecaster.strategy);
	}

	public ARIMAForecaster(final ITimeSeries<Double> historyTimeseries, final int confidenceLevel) {
		super(historyTimeseries, ARIMAForecaster.MODEL_FUNC_NAME, ARIMAForecaster.FORECAST_FUNC_NAME, confidenceLevel, ARIMAForecaster.strategy);
	}
	
	@Override
	protected String[] getModelFuncParams() {
		return new String[]{"parallel=\"true\""};
	}

	@Override
	protected String[] getForecastFuncParams() {
		return null; // no additional params required by this predictor
	}
}
