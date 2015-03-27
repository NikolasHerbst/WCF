/*******************************************************************************
* Copyright (c) 2014 Andre van Horn, Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf.management.timeSeries;

import java.util.Date;

public class TimeSeriesPoint<T> implements ITimeSeriesPoint<T> {

	private final Date time;
	private final T value;
	
	/**
	 * @param time
	 * @param value
	 */
	public TimeSeriesPoint(final Date time, final T value) {
		this.time = time;
		this.value = value;
	}

	@Override
	public Date getTime() {
		return this.time;
	}

	@Override
	public T getValue() {
		return this.value;
	}

}
