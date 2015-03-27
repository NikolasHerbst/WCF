/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf.forecastingTest;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tools.descartes.wcf.forecasting.IForecastResult;
import tools.descartes.wcf.forecasting.strategies.MeanForecaster;
import tools.descartes.wcf.management.timeSeries.ITimeSeries;
import tools.descartes.wcf.management.timeSeries.ITimeSeriesPoint;
import tools.descartes.wcf.management.timeSeries.TimeSeries;

public class MeanForecasterTest {
	private TimeSeries<Double> ts;
	private TimeUnit timeUnit;
	private Date startTime;
	private int steps;
	private MeanForecaster forecaster;
	private IForecastResult forecast;
	private final int confidenceLevel = 95; // 95%
	private ITimeSeries<Double> forecastSeries;
	private ITimeSeries<Double> upperSeries;
	private ITimeSeries<Double> lowerSeries;
	private long deltaTime;
	private Double mean;

	@Before
	public void setUp() throws Exception {
		this.deltaTime = 1000;
		this.timeUnit = TimeUnit.MILLISECONDS;
		this.startTime = new Date(System.currentTimeMillis() - this.deltaTime * 10);

		this.initForecastWithTimeUnit(this.timeUnit);
	}

	/**
	 * @param timeUnit
	 * 
	 */
	private void initForecastWithTimeUnit(final TimeUnit timeUnit) {
		this.ts = new TimeSeries<Double>(this.startTime, this.deltaTime, timeUnit,3);

		this.steps = 1;
		this.mean = new Double(2.0);
		this.ts.append(this.mean - 1);
		this.ts.append(this.mean);
		this.ts.append(this.mean + 1);
		this.forecaster = new MeanForecaster(this.ts, this.confidenceLevel);
		this.forecast = this.forecaster.forecast(this.steps);
		this.forecastSeries = this.forecast.getForecast();
		this.upperSeries = this.forecast.getUpper();
		this.lowerSeries = this.forecast.getLower();
	}

	@Test
	public void testForecastStartingIsAccordingToLastAppend() {
		Assert.assertEquals(this.ts, this.forecaster.getTsOriginal());

		// we added three timepoints, so we must be here:
		final long expectedStartTime = this.startTime.getTime() + this.deltaTime * 4;
		Assert.assertEquals(new Date(expectedStartTime), this.forecastSeries.getStartTime());
	}

	/**
	 * Compute the starting point with a different time unit
	 */
	@Test
	public void testForecastStartingIsAccordingToLastAppendDayTU() {
		this.initForecastWithTimeUnit(TimeUnit.DAYS);

		final long expectedStartTime = this.startTime.getTime()
				+ TimeUnit.MILLISECONDS.convert(this.deltaTime * 4, TimeUnit.DAYS);
		Assert.assertEquals(new Date(expectedStartTime), this.forecastSeries.getStartTime());
	}

	@Test
	public void testMeanCalculationOneStep() {

		Assert.assertEquals(this.steps, this.forecastSeries.size());

		final ITimeSeriesPoint<Double> stepFC = this.forecast.getForecast()
				.getPoints().get(0);
		Assert.assertEquals(this.mean, stepFC.getValue());
	}

	@Test
	public void testLowerCalculationOneStep() {

		Assert.assertEquals(this.steps, this.lowerSeries.size());

		final ITimeSeriesPoint<Double> stepFC = this.forecast.getLower()
				.getPoints().get(0);
		Assert.assertTrue(this.mean > stepFC.getValue());
	}
	
	@Test
	public void testUpperCalculationOneStep() {

		Assert.assertEquals(this.steps, this.upperSeries.size());

		final ITimeSeriesPoint<Double> stepFC = this.forecast.getUpper()
				.getPoints().get(0);
		Assert.assertTrue(this.mean < stepFC.getValue());
	}
}
