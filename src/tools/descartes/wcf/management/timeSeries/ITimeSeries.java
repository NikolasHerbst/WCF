/*******************************************************************************
* Copyright (c) 2014 Andre van Horn, Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf.management.timeSeries;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public interface ITimeSeries<T> {
	public static final int INFINITE_CAPACITY = -1;

	/**
	 * Returns the start of the time series, i.e., the time of the first value.
	 * 
	 * @return
	 */
	public Date getStartTime();

	/**
	 * Returns the temporal distance between to time series values with respect to the configured {@link TimeUnit}
	 * {@link #getDeltaTime()}.
	 * 
	 * @return
	 */
	public long getDeltaTime();

	/**
	 * The {@link TimeUnit} used to specify the temporal distance between to values ({@link #getDeltaTime()}).
	 * 
	 * @return
	 */
	public TimeUnit getDeltaTimeUnit();

	/**
	 * Appends the given value to the time series.
	 * 
	 * @param value
	 *            the value to append
	 * @return
	 */
	public ITimeSeriesPoint<T> append(T value);

	/**
	 * Appends the given value to the time series.
	 * 
	 * @param value
	 * @return
	 */
	public List<ITimeSeriesPoint<T>> appendAll(T[] values);

	/**
	 * Returns the {@link ITimeSeriesPoint}s of this time series.
	 * 
	 * @return
	 */
	public List<ITimeSeriesPoint<T>> getPoints();

	/**
	 * Returns a list of all {@link #getPoints()#getValues()}
	 * 
	 * @return
	 */
	public List<T> getValues();

	/**
	 * Returns the maximum number of elements held in this time series.
	 * 
	 * @return the capacity; {@link #INFINITE_CAPACITY} if the capacity is infinite
	 */
	public int getCapacity();

	/**
	 * Returns the number of value contained in the time series
	 * 
	 * @return
	 */
	public int size();

	/**
	 * Returns the time corresponding to the most recent value in the time series
	 * 
	 * @return
	 */
	public Date getEndTime();
	
	/**
	 * Returns the frequency of the time series (how many time series points add up to an time unit of interest)
	 * needed to improve forecast accuracy
	 * e.g. a time series point each 15 minutes, interested in forecasting days: frequency is 96
	 * 
	 * @return frequency as the number of time series points 
	 * that add up either to the next bigger time unit and/or 
	 * to the estimated length of seasonal patterns in focus. 
	 * The value should not be too small 
	 * (still able to approximate the shape of the seasonal pattern) 
	 * and not to high (to limit the computational effort of 
	 * complex forecast strategies)
	 */
	public int getFrequency();
	
	/**
	 * a period is a timeUnit of interest in this case 
	 * returns the maximum number of periods the TS can contain - 
	 * for forecasting seasonal patterns with the maximum duration of a period, MaxPeriods should be at least 3
	 * 
	 * capacity = frequency * periods
	 * 
	 * The amount of Frequency time series points form a period. 
	 * This parameter defines the maximum number of periods that 
	 * fit into the time series. 
	 * As in a `fifo� queue the oldest values fall off 
	 * when more recent values are added. 
	 * The value of this setting should be at least 3 
	 * to enable reliable pattern detection by complex 
	 * forecast strategies and multiplied the by 
	 * Frequency value not be higher than 200 if 
	 * the computational effort of more complex 
	 * forecast strategies should stay below one minute. 
	 */
	public int getMaxPeriods();
	
	/**
	 * @return The number of time series points that have fallen 
	 * of the time series due to capacity constraints by max_periods
	 * 
	 */
	public long getSkippedValues();
	
}
