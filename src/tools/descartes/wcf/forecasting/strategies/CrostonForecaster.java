/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
* 
* An R-based time series forecaster
* 
* Intermittent Demand Forecasting
* Decomposition of the time series that contains zero values into 
* two separate sequences: a non-zero valued time series and a second 
* that contains the time intervals of zero values. Independent 
* forecast using SES and combination of the two independent forecasts. 
* No confidence intervals are computed due to no consistent underlying stochastic model.
* 
* @author Andre van Hoorn, Nikolas Herbst
*******************************************************************************/

package tools.descartes.wcf.forecasting.strategies;

import tools.descartes.wcf.forecasting.AbstractRForecaster;
import tools.descartes.wcf.forecasting.ForecastStrategyEnum;
import tools.descartes.wcf.management.timeSeries.ITimeSeries;

public class CrostonForecaster extends AbstractRForecaster {
	private final static String MODEL_FUNC_NAME = null; // no explicit stochastic model
	private final static String FORECAST_FUNC_NAME = "croston";
	private final static ForecastStrategyEnum strategy = ForecastStrategyEnum.CROST;
			
	public CrostonForecaster(final ITimeSeries<Double> historyTimeseries) {
		super(historyTimeseries, CrostonForecaster.MODEL_FUNC_NAME, CrostonForecaster.FORECAST_FUNC_NAME, CrostonForecaster.strategy);
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

