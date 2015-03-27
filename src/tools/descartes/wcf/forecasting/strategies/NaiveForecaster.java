/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
* 
* The naive forecast considers only the 
* value of the most recent observation assuming that this 
* value has the highest probability for the next forecast point.
* 
* Horizon: very short term forecast (1-2 points)
* Overhead: nearly none O(1)
* @author Andre van Hoorn, Nikolas Herbst
* 
*******************************************************************************/

package tools.descartes.wcf.forecasting.strategies;

import tools.descartes.wcf.forecasting.AbstractRForecaster;
import tools.descartes.wcf.forecasting.ForecastStrategyEnum;
import tools.descartes.wcf.management.timeSeries.ITimeSeries;

public class NaiveForecaster extends AbstractRForecaster {
	private final static String MODEL_FUNC_NAME = null; // no explicit stochastic model
	private final static String FORECAST_FUNC_NAME = "rwf";
	private final static ForecastStrategyEnum strategy = ForecastStrategyEnum.NAIVE;
	
	public NaiveForecaster(final ITimeSeries<Double> historyTimeseries) {
		super(historyTimeseries, NaiveForecaster.MODEL_FUNC_NAME, NaiveForecaster.FORECAST_FUNC_NAME, NaiveForecaster.strategy);
	}

	public NaiveForecaster(final ITimeSeries<Double> historyTimeseries, final int confidenceLevel) {
		super(historyTimeseries, NaiveForecaster.MODEL_FUNC_NAME, NaiveForecaster.FORECAST_FUNC_NAME, confidenceLevel, NaiveForecaster.strategy);
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
