/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf.managementTest.timeSeriesTest;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import tools.descartes.wcf.management.timeSeries.TimeSeries;



public class TimeSeriesTest {

	private int frequency;
	private int maxPeriods;
	private TimeSeries<Double> boundedTS;
	private TimeUnit timeUnit;
	private Date startTime;

	@Before
	public void setUp() throws Exception {
		long deltaTime = 2;
		timeUnit = TimeUnit.SECONDS;
		startTime = new Date(System.currentTimeMillis());
		
		frequency = 6;
		maxPeriods = 5;
		boundedTS = new TimeSeries<Double>(startTime, deltaTime, timeUnit, frequency, maxPeriods,0);
	}

	@Test 
	public void testGettersAndAppendingValues() {
		assertEquals(startTime, boundedTS.getStartTime());
		
		long testStartTime = startTime.getTime();
		
		assertEquals(0, boundedTS.size());
		boundedTS.append(666.0);
		boundedTS.append(666.0);
		assertEquals(2, boundedTS.size());
		
		assert(testStartTime== boundedTS.getStartTime().getTime());
	}
	
	@Test 
	public void testValueSort() {
		int count = 30;
		for (int i = 0; i < count; i++) {
			boundedTS.append(new Double(i));
		}

		for (int i = 0; i < count; i++) {
			assertEquals(new Double(i), boundedTS.getPoints().get(i).getValue());
		}

		assertEquals(count, boundedTS.size());
	}
	
	@Test
	public void testCapacityRestriction() {
		assertEquals(0, boundedTS.size());
		assertEquals(frequency*maxPeriods, boundedTS.getCapacity());
		for (int i = 0; i <frequency*maxPeriods  + 1; i++) {
			boundedTS.append(new Double(10 * i));
		}
		assertEquals(frequency*maxPeriods, boundedTS.size());
	}
	
	@Test
	public void testKeepNewerValuesInCapacity() {
		assertEquals(0, boundedTS.size());
		int i;
		int lastNumber = frequency*maxPeriods*2;
		for (i = 0; i <= lastNumber; i++) {
			boundedTS.append(new Double(i));
		}
		assertEquals(new Double(lastNumber), boundedTS.getPoints().get((frequency*maxPeriods)-1).getValue());
	}

}
