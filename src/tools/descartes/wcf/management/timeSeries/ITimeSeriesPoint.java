/*******************************************************************************
* Copyright (c) 2014 Andre van Horn, Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf.management.timeSeries;

import java.util.Date;

public interface ITimeSeriesPoint<T> {
	public Date getTime();
	public T getValue();
}
