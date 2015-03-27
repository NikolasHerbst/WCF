/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf.forecastingTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import tools.descartes.wcf.forecasting.IForecastResult;
import tools.descartes.wcf.forecasting.strategies.ARIMA101Forecaster;
import tools.descartes.wcf.management.timeSeries.ITimeSeries;
import tools.descartes.wcf.management.timeSeries.TimeSeries;



import junit.framework.Assert;
import junit.framework.TestCase;

public class ARIMA101ForecasterTest extends TestCase {
	final long startTime = 98890787;
	final long deltaTimeMillis = 1000;
	final int confidenceLevel = 90;
	final int steps = 1;

	public void testARIMA101Predictor(){
		/*
		 * Test values resulting from this calculation:
		 * 
			var_1 <<- c(1.0,2.0,3.0,4.0)
			var_2<<-arima(var_1,c(1,0,1))
			var_3<<-predict(var_2,h=1,level=c(90))
			dprint(var_3)
			dprint(var_3$pred[1])
			dprint(var_3$pred[1] + var_3$se[1])
			dprint(var_3$pred[1] - var_3$se[1])

		 */
		final Double[] values = { 1.0, 2.0, 3.0, 4.0};
		final List<Double> expectedValues = new ArrayList<Double>(values.length);
		for (final Double curVal : values) {
			expectedValues.add(curVal);
		}

		final TimeSeries<Double> ts =
				new TimeSeries<Double>(new Date(this.startTime), this.deltaTimeMillis, TimeUnit.MILLISECONDS, 4);
		ts.appendAll(values);
		
		final ARIMA101Forecaster forecaster = new ARIMA101Forecaster(ts, this.confidenceLevel);
		final IForecastResult forecast = forecaster.forecast(this.steps);
		
		final ITimeSeries<Double> forecastSeries = forecast.getForecast();
		final double expectedForecast = 4.210429;
		this.assertEqualsWithTolerance("Unexpected forecast value", expectedForecast, 0.3, forecastSeries.getPoints().get(0).getValue());
		
		final ITimeSeries<Double> upperSeries = forecast.getUpper();
		final double expectedUpper = 4.852857;
		this.assertEqualsWithTolerance("Unexpected upper value", expectedUpper, 1, upperSeries.getPoints().get(0).getValue());
		
		final ITimeSeries<Double> lowerSeries = forecast.getLower();
		final double expectedLower =  3.568001;
		this.assertEqualsWithTolerance("Unexpected lower value", expectedLower, 1, lowerSeries.getPoints().get(0).getValue());
	}
	
	private void assertEqualsWithTolerance (final String message, final double expected, final double tolerance, final double actual) {
		if (!(expected-tolerance <= actual) || !(actual <= expected+tolerance )) {
			Assert.fail(String.format(message + ". Expected value %s with tolerance %s; found %s", expected, tolerance, actual));
		}
	}
}
