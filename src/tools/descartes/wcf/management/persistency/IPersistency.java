/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf.management.persistency;

import tools.descartes.wcf.forecasting.IForecastResult;
import tools.descartes.wcf.management.timeSeries.ITimeSeries;
import tools.descartes.wcf.management.wibManagement.IWorkloadIntensityBehavior;

/**
 * @author Nikolas
 */
public interface IPersistency {
	
	/**
	 * Persists all attributes of a WIB
	 */
	public boolean writeWIB(IWorkloadIntensityBehavior wl);
	
	/**
	 * Writes a ForecastResult (Mean, Lower and Upper Confidence Point Forecasts + MASE estimation) 
	 * including count of forecast execution into a file/table/stream of the corresponding WIB
	 */
	public boolean writeForecastResult(IForecastResult fr, IWorkloadIntensityBehavior wl, int fcCount);
	
	/**
	 * Checks whether new timeSeries values are available for a certain 
	 * WIB and if yes adds them to the in-memory time series
	 */
	public ITimeSeries<Double> readNewTimeSeriesValues(IWorkloadIntensityBehavior wl);
	
	/**
	 * Checks if a time series configuration for a WIB ID is already available 
	 * (needed after a WCF System restart) - if not, a customer provided configuration is necessary to build the in-memory time series object
	 */
	public ITimeSeries<Double> readTimeSeriesConf(IWorkloadIntensityBehavior wl);
	
	/**
	 * Writes the current time series configuration
	 */
	public void writeTimeSeriesConf(IWorkloadIntensityBehavior wl);
	
	/**
	 * Initialisation of a WIB Backup table/file
	 */
	public void initWIB(IWorkloadIntensityBehavior wl);
}
