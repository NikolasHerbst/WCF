/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
* 
* The tBATS stochastic process modeling framework of innovations 
* state space approach focuses modeling of complex seasonal time series 
* (multiple/high frequency/non-integer seasonality) and uses Box-Cox transformation, 
* Fourier representations with time varying coefficients and ARMA error correction.
* Trigonometric formulation of complex seasonal time series patterns to enable 
* their identification by FFT and time series decomposition. 
* Improved computational overhead using a new method for maximum-likelihood estimations.
* @author Andre van Hoorn, Nikolas Herbst
* 
*******************************************************************************/

package tools.descartes.wcf.forecasting.strategies;

import tools.descartes.wcf.forecasting.AbstractRForecaster;
import tools.descartes.wcf.forecasting.ForecastStrategyEnum;
import tools.descartes.wcf.management.timeSeries.ITimeSeries;

public class TBATSForecaster extends AbstractRForecaster {
	private final static String MODEL_FUNC_NAME = "tbats"; // no explicit stochastic model
	private final static String FORECAST_FUNC_NAME = "forecast";
	private final static ForecastStrategyEnum strategy = ForecastStrategyEnum.TBATS;
			
	public TBATSForecaster(final ITimeSeries<Double> historyTimeseries) {
		super(historyTimeseries, TBATSForecaster.MODEL_FUNC_NAME, TBATSForecaster.FORECAST_FUNC_NAME, TBATSForecaster.strategy);
	}

	public TBATSForecaster(final ITimeSeries<Double> historyTimeseries, final int confidenceLevel) {
		super(historyTimeseries, TBATSForecaster.MODEL_FUNC_NAME, TBATSForecaster.FORECAST_FUNC_NAME, confidenceLevel, TBATSForecaster.strategy);
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
