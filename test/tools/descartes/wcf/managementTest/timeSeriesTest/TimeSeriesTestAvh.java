/*******************************************************************************
* Copyright (c) 2014 Andre van Hoorn, Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/


package tools.descartes.wcf.managementTest.timeSeriesTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

import tools.descartes.wcf.management.timeSeries.TimeSeries;



/**
 * Some tests in addition to {@link TimeSeriesTest}. Since I'm following a slightly different style of writing tests
 * (refuse state in tests), I've decided to add the tests to a separate class. Might be consolidated later.
 * 
 */

public class TimeSeriesTestAvh extends TestCase {
	final long startTime = 98890787;
	final long deltaTimeMillis = 1000;

	@Test
	public void testAppendAll() {
		final Double[] values = { 600.9, 400.2, 223.9};
		final List<Double> expectedValues = new ArrayList<Double>(values.length);
		for (final Double curVal : values) {
			expectedValues.add(curVal);
		}

		final TimeSeries<Double> ts =
				new TimeSeries<Double>(new Date(this.startTime), this.deltaTimeMillis, TimeUnit.MILLISECONDS,3);
		ts.appendAll(values);
		
		final List<Double> tsValues = ts.getValues(); 
		
		Assert.assertEquals("Unexpected size of time series", values.length, ts.size());
		Assert.assertEquals("Unexpected size of values list", values.length, tsValues.size());
		
		Assert.assertEquals("values not equal", expectedValues, tsValues);
	}
}
