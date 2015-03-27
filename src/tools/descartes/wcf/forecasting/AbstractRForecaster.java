/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf.forecasting;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import tools.descartes.wcf.management.timeSeries.ITimeSeries;
import tools.descartes.wcf.rServerBridge.RServerBridgeControl;


/**
 * Convenience class to implement an {@link IForecaster} with R.
 * 
 * @author Andre van Hoorn, Tillmann Bielefeld, Nikolas Herbst
 * 
 */
public abstract class AbstractRForecaster extends AbstractForecaster<Double> {
	/**
	 * Acquire an instance of the {@link RServerBridgeControl} once
	 */
	private final static RServerBridgeControl rBridge = RServerBridgeControl.getInstance();
	static {
		AbstractRForecaster.rBridge.e("require(forecast)");
	}

	private final String modelFunc;
	private final String forecastFunc;
	private final ForecastStrategyEnum strategy;

	public AbstractRForecaster(final ITimeSeries<Double> historyTimeseries, final String modelFunc,
			final String forecastFunc, final ForecastStrategyEnum strategy) {
		super(historyTimeseries);
		this.modelFunc = modelFunc;
		this.forecastFunc = forecastFunc;
		this.strategy = strategy;
	}

	public AbstractRForecaster(final ITimeSeries<Double> historyTimeseries, final String modelFunc,
			final String forecastFunc, final int confidenceLevel, final ForecastStrategyEnum strategy) {
		super(historyTimeseries, confidenceLevel);
		this.modelFunc = modelFunc;
		this.forecastFunc = forecastFunc;
		this.strategy = strategy;
	}
	
	@Override
	public final IForecastResult forecast(final int numForecastSteps) {
		if(rBridge == null)return null; 
		
		final ITimeSeries<Double> history = this.getTsOriginal();
		
		final String varNameValues = RServerBridgeControl.uniqueVarname();
		final String varNameModel = RServerBridgeControl.uniqueVarname();
		final String varNameForecast = RServerBridgeControl.uniqueVarname();
		
		final double[] values;
		List<Double> allHistory = new ArrayList<Double>(history.getValues());
		
		// remove NullValues only if strategy is not "Croston" (forecasting for intermitted demands)
		if(strategy == ForecastStrategyEnum.CROST){
			Double[] histValues = new Double[allHistory.size()];
			histValues = allHistory.toArray(histValues);
			values = ArrayUtils.toPrimitive(histValues);
		} else {
			Double[] histValuesNotNull = removeNullValues(allHistory); 
			values = ArrayUtils.toPrimitive(histValuesNotNull);
		}
		
		/*
		 * 0. Assign values to temporal variable
		 */
		long startFC = System.currentTimeMillis();
		AbstractRForecaster.rBridge.assign(varNameValues, values);
		
		//frequency for time series object in R --> needed for MASE calculation.
		AbstractRForecaster.rBridge.toTS(varNameValues,history.getFrequency());
		/*
		 * 1. Compute stochastic model for forecast
		 */
		if (this.modelFunc == null) {
			// In this case, the values are the model ...
			AbstractRForecaster.rBridge.assign(varNameModel,values);
			AbstractRForecaster.rBridge.toTS(varNameModel,history.getFrequency());
		} else {
			final String[] additionalModelParams = this.getModelFuncParams();
			
			StringBuffer params = new StringBuffer();
			params.append(varNameValues);
			if (null != additionalModelParams) {
				for (String param : additionalModelParams) {
					params.append(",");
					params.append(param);
				}
			}
			AbstractRForecaster.rBridge.e(String.format("%s<<-%s(%s)", varNameModel, this.modelFunc, params));
		}
		
		/*
		 * 2. Perform forecast based on stochastic model
		 */
		if(this.getConfidenceLevel()==0){
			AbstractRForecaster.rBridge.e(String.format("%s<<-%s(%s,h=%d)", varNameForecast, this.forecastFunc, varNameModel,
					numForecastSteps));	
		} else {
			AbstractRForecaster.rBridge.e(String.format("%s<<-%s(%s,h=%d,level=c(%d))", varNameForecast, this.forecastFunc, varNameModel,
				numForecastSteps, this.getConfidenceLevel()));
		}
		/*
		 * 3. Calculate Forecast Quality Metric
		 */
		double fcQuality;
		if(this.modelFunc == null || this.strategy==ForecastStrategyEnum.TBATS){
			fcQuality = AbstractRForecaster.rBridge.eDbl("accuracy("+varNameForecast+")[6]");
		} else {
			fcQuality = AbstractRForecaster.rBridge.eDbl("accuracy("+varNameModel+")[6]");
		}
		final double[] lowerValues = AbstractRForecaster.rBridge.eDblArr(lowerOperationOnResult(varNameForecast));
		final double[] forecastValues = AbstractRForecaster.rBridge.eDblArr(forecastOperationOnResult(varNameForecast));
		final double[] upperValues = AbstractRForecaster.rBridge.eDblArr(upperOperationOnResult(varNameForecast));
		
		
		// remove temporal variable:
		AbstractRForecaster.rBridge.e(String.format("rm(%s)", varNameModel));
		AbstractRForecaster.rBridge.e(String.format("rm(%s)", varNameValues));
		AbstractRForecaster.rBridge.e(String.format("rm(%s)", varNameForecast));
		
		long endFC = System.currentTimeMillis();
		
		ITimeSeries<Double> tsForecast = this.prepareForecastTS();
		ITimeSeries<Double> tsLower;
		ITimeSeries<Double> tsUpper;
		tsForecast.appendAll(ArrayUtils.toObject(forecastValues));

		if (this.getConfidenceLevel() == 0) {
			tsLower = tsForecast;
			tsUpper = tsForecast;
		} else {
			tsLower = this.prepareForecastTS();
			tsLower.appendAll(ArrayUtils.toObject(lowerValues));
			tsUpper = this.prepareForecastTS();
			tsUpper.appendAll(ArrayUtils.toObject(upperValues));
		}
		return new ForecastResult(tsForecast, this.getTsOriginal(),this.getConfidenceLevel(), fcQuality, tsLower, tsUpper, strategy, endFC-startFC);
	}

	/**
	 * @param varNameForecast
	 * @return
	 */
	protected String lowerOperationOnResult(final String varNameForecast) {
		return String.format("%s$lower", varNameForecast);
	}
	/**
	 * @param varNameForecast
	 * @return
	 */
	protected String upperOperationOnResult(final String varNameForecast) {
		return String.format("%s$upper", varNameForecast);
	}

	/**
	 * @param varNameForecast
	 * @return
	 */
	protected String forecastOperationOnResult(final String varNameForecast) {
		return String.format("%s$mean", varNameForecast);
	}

	/**
	 * Returns additional parameters to be appended to the call of the R function {@link #getModelFuncName()}.
	 * 
	 * @return the parameters or null if none
	 */
	protected abstract String[] getModelFuncParams();

	/**
	 * Returns additional parameters to be appended to the call of the R function {@link #getForecastFuncName()}.
	 * 
	 * @return the parameters or null if none
	 */
	protected abstract String[] getForecastFuncParams();
	
	private static Double[] removeNullValues(List<Double> allHistory) {
		List<Double> newList = new ArrayList<Double>();
		for (Object obj : allHistory) {
			if (null != obj && obj instanceof Double)
				newList.add((Double)obj);
		}
		return newList.toArray(new Double[] {});
	}
}
