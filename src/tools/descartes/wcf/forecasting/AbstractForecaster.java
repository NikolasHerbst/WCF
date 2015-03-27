/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf.forecasting;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import tools.descartes.wcf.management.timeSeries.ITimeSeries;
import tools.descartes.wcf.management.timeSeries.TimeSeries;

public abstract class AbstractForecaster<T> implements IForecaster<T> {
	private final ITimeSeries<T> historyTimeseries;
	private final int confidenceLevel;
	
	/**
	 * @param historyTimeseries
	 */
	public AbstractForecaster(final ITimeSeries<T> historyTimeseries) {
		this(historyTimeseries, 0);
	}

	public AbstractForecaster(final ITimeSeries<T> historyTimeseries, final int confidenceLevel) {
		this.historyTimeseries = historyTimeseries;
		this.confidenceLevel = confidenceLevel;
	}
	
	
	/**
	 * @return the historyTimeseries
	 */
	@Override
	public ITimeSeries<T> getTsOriginal() {
		return this.historyTimeseries;
	}

	protected ITimeSeries<T> prepareForecastTS() {
		final ITimeSeries<T> history = this.getTsOriginal();

		// The starting point of the FC series is calculated by _one_ additional
		// tick...
		final long lastDistanceMillis = TimeUnit.MILLISECONDS.convert(
				history.getDeltaTime(), history.getDeltaTimeUnit());
		// ... plus the end point of the historic series
		final Date startTime = new Date(history.getEndTime().getTime()
				+ lastDistanceMillis);
		final TimeSeries<T> tsFC = new TimeSeries<T>(startTime,
				history.getDeltaTime(), history.getDeltaTimeUnit(), history.getFrequency());

		return tsFC;
	}

	@Override
	public int getConfidenceLevel() {
		return this.confidenceLevel;
	}
}
