/*******************************************************************************
* Copyright (c) 2014 Andre van Horn, Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf.management.timeSeries;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

public class TimeSeries<T> implements ITimeSeries<T> {
	private Date startTime;
	private Date nextTime; 
	private final long deltaTime;
	private final TimeUnit deltaTimeUnit;
	private final int frequency;
	private final int capacity;
	private final int maxPeriods;
	private final CircularFifoBuffer points;
	
	private long oneStepMillis;
	
	private long skippedValues = 0;

	/**
	 * 
	 * @param startTime: 	The timestamp (absolute time in [ms]) 
	 * 						of the first arrival rate value added to the time series is equal 
	 * 						to the start time of the time series
	 * @param deltaTime:	The constant time difference between two time series values is the Delta Time
	 * @param deltaTimeUnit: This parameter defines the time unit of the DeltaTime parameter
	 * @param frequency:	The Frequency is the number of time series points that add up either 
	 * 						to the next bigger time unit and/or to the estimated length of 
	 * 						seasonal patterns in focus. The value should not be too small 
	 * 						(still able to approximate the shape of the seasonal pattern) 
	 * 						and not to high (to limit the computational effort of 
	 * 						complex forecast strategies)
	 * @param maxPeriods	The amount of Frequency time series points form a period. 
	 * 						This parameter defines the maximum number of periods that 
	 * 						fit into the time series. As in a `fifo queue the oldest values fall 
	 * 						off when more recent values are added. The value of this setting should 
	 * 						be at least 3 to enable reliable pattern detection by complex forecast 
	 * 						strategies and multiplied the by Frequency value not be higher than 
	 * 						200 if the computational effort of more complex forecast strategies 
	 * 						should stay below one minute. 
	 * @param skippedValues: The number of time series points that have fallen 
	 * 						of the time series due to capacity constraints by max_periods
	 */
	public TimeSeries(final Date startTime, final long deltaTime,
			final TimeUnit deltaTimeUnit, final int frequency, final int maxPeriods, long skippedValues) {
		this.startTime = startTime;
		this.deltaTime = deltaTime;
		this.deltaTimeUnit = deltaTimeUnit;
		this.frequency = frequency;
		this.maxPeriods = maxPeriods;
		this.capacity = frequency*maxPeriods;
		this.skippedValues = skippedValues;
		this.oneStepMillis = TimeUnit.MILLISECONDS.convert(this.deltaTime,
				this.deltaTimeUnit);
	
		this.points = new CircularFifoBuffer(this.capacity);
		
		this.nextTime = (Date)startTime.clone();
		this.setNextTime();
	}
	
	public TimeSeries(final Date startTime, final long deltaTime,
			final TimeUnit deltaTimeUnit, final int frequency) {
		this(startTime, deltaTime, deltaTimeUnit, frequency,5,0);
	}

	/**
	 * @return the startTime
	 */
	@Override
	public Date getStartTime() {
		return this.startTime;
	}

	@Override
	public long getDeltaTime() {
		return this.deltaTime;
	}

	@Override
	public TimeUnit getDeltaTimeUnit() {
		return this.deltaTimeUnit;
	}

	@Override
	public synchronized ITimeSeriesPoint<T> append(final T value) {
		final ITimeSeriesPoint<T> point = new TimeSeriesPoint<T>(new Date(this.nextTime.getTime()),
				value);

		if(points.size()>=capacity){
			startTime = new Date(startTime.getTime()+deltaTimeUnit.toMillis(deltaTime));
			skippedValues++;
		}
		
		this.points.add(point);
		
		this.setNextTime();
		return point;
	}

	/**
	 * 
	 */
	private void setNextTime() {
		this.nextTime.setTime(this.nextTime.getTime() + this.oneStepMillis);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ITimeSeriesPoint<T>> getPoints() {
		return new ArrayList<ITimeSeriesPoint<T>>(this.points);
	}

	@Override
	public List<T> getValues() {
		final List<ITimeSeriesPoint<T>> pointsCopy = this.getPoints();
		final List<T> retVals = new ArrayList<T>(pointsCopy.size());
		for (final ITimeSeriesPoint<T> curPoint : pointsCopy) {
			retVals.add(curPoint.getValue());
		}
		
		return retVals;
	}
	
	@Override
	public int getCapacity() {
		return this.capacity;
	}

	@Override
	public int size() {
		return this.points.size();
	}

	@Override
	public Date getEndTime() {
		return new Date(this.getStartTime().getTime() + this.oneStepMillis
				* this.size());
	}

	@Override
	public List<ITimeSeriesPoint<T>> appendAll(final T[] values) {
		final List<ITimeSeriesPoint<T>> retVals = new ArrayList<ITimeSeriesPoint<T>>(values.length);
		
		for (final T value : values) {
			retVals.add(this.append(value));
		}
		
		return retVals;
	}


	@Override
	public int getFrequency() {
		return this.frequency;
	}


	@Override
	public int getMaxPeriods() {
		return this.maxPeriods;
	}
	
	public long getSkippedValues() {
		return skippedValues;
	}
	
	public void setSkippedValues(long skippedValues){
		this.skippedValues = skippedValues;
	}
	
	@SuppressWarnings("unused")
	private int calculateFrequency(final ITimeSeries<Double> history){
		int biggerUnitFactor = getFactorBiggerTimeUnit(history.getDeltaTimeUnit());
		if(history.getDeltaTime()>biggerUnitFactor){
			TimeUnit higher = getBiggerTimeUnit(history.getDeltaTimeUnit());
			biggerUnitFactor *= getFactorBiggerTimeUnit(higher);
		}
		return (int)(biggerUnitFactor/history.getDeltaTime());
	}
	
	private int getFactorBiggerTimeUnit(TimeUnit timeUnit){
		int biggerUnit;
		switch(timeUnit){
			case NANOSECONDS:
			case MICROSECONDS:
			case MILLISECONDS: biggerUnit = 1000; break;
			case SECONDS:
			case MINUTES:biggerUnit = 60; break;
			case HOURS:biggerUnit = 24; break;
			default: biggerUnit = 1;
		}
		return biggerUnit;
	}

	private TimeUnit getBiggerTimeUnit(TimeUnit timeUnit){
		TimeUnit higherUnit = null;
		switch(timeUnit){
			case NANOSECONDS: 	higherUnit = TimeUnit.MICROSECONDS; break;
			case MICROSECONDS: 	higherUnit = TimeUnit.MILLISECONDS; break;
			case MILLISECONDS: 	higherUnit = TimeUnit.SECONDS; break;
			case SECONDS:		higherUnit = TimeUnit.MINUTES; break;
			case MINUTES:		higherUnit = TimeUnit.HOURS; break;
			case HOURS:			higherUnit = TimeUnit.DAYS; break;
			default:
			break;
		}
		return higherUnit;
	}
	
}