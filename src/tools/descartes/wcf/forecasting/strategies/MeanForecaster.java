/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
* 
* @author Andre van Hoorn, Nikolas Herbst
* 
*******************************************************************************/

package tools.descartes.wcf.forecasting.strategies;

import tools.descartes.wcf.forecasting.AbstractRForecaster;
import tools.descartes.wcf.forecasting.ForecastStrategyEnum;
import tools.descartes.wcf.management.timeSeries.ITimeSeries;

public class MeanForecaster extends AbstractRForecaster {
	private final static String MODEL_FUNC_NAME = null; // no explicit stochastic model
	private final static String FORECAST_FUNC_NAME = "meanf";
	private final static ForecastStrategyEnum strategy = ForecastStrategyEnum.MEAN;
	
	public MeanForecaster(final ITimeSeries<Double> historyTimeseries) {
		super(historyTimeseries, MeanForecaster.MODEL_FUNC_NAME, MeanForecaster.FORECAST_FUNC_NAME, MeanForecaster.strategy);
	}

	public MeanForecaster(final ITimeSeries<Double> historyTimeseries, final int confidenceLevel) {
		super(historyTimeseries, MeanForecaster.MODEL_FUNC_NAME, MeanForecaster.FORECAST_FUNC_NAME, confidenceLevel, MeanForecaster.strategy);
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
