/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
* 
* An R-based time series forecaster
* 
* Cubic splines are fitted to the univariate time series data to obtain 
* a trend estimate and linear forecast function. 
* Prediction intervals are constructed by use of a likelihood approach for 
* estimation of smoothing parameters. The cubic splines method can be mapped to 
* an ARIMA 022 stochastic process model with a restricted parameter space.
* 
* Overhead below 100ms for less than 30 values (more values do not sig. improve accuracy)
* 
* @author Andre van Hoorn, Nikolas Herbst
*******************************************************************************/

package tools.descartes.wcf.forecasting.strategies;

import tools.descartes.wcf.forecasting.AbstractRForecaster;
import tools.descartes.wcf.forecasting.ForecastStrategyEnum;
import tools.descartes.wcf.management.timeSeries.ITimeSeries;

public class CSForecaster extends AbstractRForecaster {
	private final static String MODEL_FUNC_NAME = null; // no explicit stochastic model
	private final static String FORECAST_FUNC_NAME = "splinef";
	private final static ForecastStrategyEnum strategy = ForecastStrategyEnum.CS;
		
		public CSForecaster(final ITimeSeries<Double> historyTimeseries) {
			super(historyTimeseries, CSForecaster.MODEL_FUNC_NAME, CSForecaster.FORECAST_FUNC_NAME, CSForecaster.strategy);
		}

		public CSForecaster(final ITimeSeries<Double> historyTimeseries, final int confidenceLevel) {
			super(historyTimeseries, CSForecaster.MODEL_FUNC_NAME, CSForecaster.FORECAST_FUNC_NAME, confidenceLevel, CSForecaster.strategy);
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

