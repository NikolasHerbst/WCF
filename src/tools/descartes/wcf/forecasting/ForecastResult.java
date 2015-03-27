/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/


package tools.descartes.wcf.forecasting;

import java.util.List;

import tools.descartes.wcf.management.timeSeries.ITimeSeries;
import tools.descartes.wcf.management.timeSeries.TimeSeries;
import tools.descartes.wcf.wibClassification.ClassificationUtility;

/**
 * Result of a time series forecast, e.g., computed by {@link IForecaster}. If additional fields are required,
 * {@link IForecaster}s should extend this class.
 * 
 * @author Andre van Hoorn, Nikolas Herbst
 * 
 */
public class ForecastResult implements IForecastResult {

	private final ITimeSeries<Double>  tsForecast;
	private final ITimeSeries<Double>  tsOriginal;
	private final ITimeSeries<Double>  tsUpper;
	private final ITimeSeries<Double>  tsLower;

	private final int confidenceLevel;
	private final double meanAbsoluteScaledError; 
	private final ForecastStrategyEnum fcStrategy;
	
	private final long duration; 

	public ForecastResult(final ITimeSeries<Double> tsForecast, final ITimeSeries<Double> tsOriginal, final int confidenceLevel, final double meanAbsoluteScaledError,  final ITimeSeries<Double> tsLower,
			final ITimeSeries<Double> tsUpper,final ForecastStrategyEnum fcStrategy, final long duration) {
		this.tsForecast = tsForecast;
		ITimeSeries<Double> original = new TimeSeries<Double>(tsOriginal.getStartTime(),tsOriginal.getDeltaTime(),tsOriginal.getDeltaTimeUnit(),tsOriginal.getFrequency(),tsOriginal.getMaxPeriods(),tsOriginal.getSkippedValues());
		Double[] array = new Double[tsOriginal.size()];
		original.appendAll(tsOriginal.getValues().toArray(array));
		this.tsOriginal = (ITimeSeries<Double>) original;
		this.tsUpper = tsUpper;
		this.tsLower = tsLower;
		
		this.confidenceLevel = confidenceLevel;
		this.meanAbsoluteScaledError = meanAbsoluteScaledError;
		this.duration = duration;
		this.fcStrategy = fcStrategy;
	}

	/**
	 * Constructs a {@link ForecastResult} with confidence level <code>0</code>, where the time series returned
	 * {@link #getLower()} by {@link #getUpper()} are the forecast series.
	 * 
	 * @param tsForecast
	 */
	public ForecastResult(final ITimeSeries<Double> tsForecast, final ITimeSeries<Double> tsOriginal, final double meanAbsoluteScaledError,final ForecastStrategyEnum fcStrategy, final long duration) {
		this(tsForecast, tsOriginal, 0, meanAbsoluteScaledError, tsForecast, tsForecast, fcStrategy, duration); // tsForecast also lower/upper
	}

	@Override
	public ITimeSeries<Double> getForecast() {
		return this.tsForecast;
	}

	@Override
	public int getConfidenceLevel() {
		return this.confidenceLevel;
	}

	@Override
	public ITimeSeries<Double> getUpper() {
		return this.tsUpper;
	}

	@Override
	public ITimeSeries<Double> getLower() {
		return this.tsLower;
	}

	@Override
	public ITimeSeries<Double> getOriginal() {
		return this.tsOriginal;
	}
	public double getMeanAbsoluteScaledError() {
		return meanAbsoluteScaledError;
	}
	
	public ForecastStrategyEnum getFcStrategy() {
		return fcStrategy;
	}

	public long getDuration() {
		return duration;
	}

	@Override
	public boolean isPlausible() {
		if(this.meanAbsoluteScaledError == 0 || this.meanAbsoluteScaledError == Double.NaN) return false;
		double maximumObserved = ClassificationUtility.calcMaximum(this.tsOriginal);
		List<Double> values = this.tsForecast.getValues();
		for( Double value :values){
			if(value>maximumObserved*2 || value<0) return false;
		}
		return true;
	}
}
