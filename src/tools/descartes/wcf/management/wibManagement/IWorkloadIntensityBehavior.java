/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf.management.wibManagement;

import tools.descartes.wcf.forecasting.ForecastObjectives;
import tools.descartes.wcf.forecasting.IForecastResult;
import tools.descartes.wcf.management.timeSeries.ITimeSeries;
import tools.descartes.wcf.wibClassification.ClassificationSetting;
import tools.descartes.wcf.wibClassification.IClassificationStrategy;

public interface IWorkloadIntensityBehavior {
	
	/**
	 * returns the current time series of monitored values of the WIB
	 */
	public ITimeSeries<Double> getTimeSeries();
	
	/**
	 * setting the current WIBclassification strategy
	 */
	public void setClassificator(IClassificationStrategy classificator);
	
	/**
	 * returns an object that contains the Forecast Objectives
	 */
	public ForecastObjectives getForecastObjectives();
	
	/**
	 * returns the current classification configuration
	 */
	public ClassificationSetting getClassSetting();
	
	/**
	 * return the WIBs ID
	 */
	public int getID();
	
	/**
	 * 
	 */
	public void setTimeSeries(ITimeSeries<Double> ts);
	
	/**
	 * Checks whether the WIB flag is active
	 */
	public boolean isActive();
	
	/**
	 * For smooth de-activation of a WIBs thread
	 */
	public void setActive(boolean active);
	
	/**
	 * returns the forecast result of the first strategy in comparison
	 */
	public IForecastResult getResult1();
	
	/**
	 * returns the forecast result of the second strategy in comparison
	 */
	public IForecastResult getResult2();
	
	/**
	 * returns the observed MASE metric for both strategies 
	 */
	public double[] getMASEMetric();
	
	/**
	 * sets the current observed MASE metric array
	 */
	public void setMASEMetric(double[] mase);
	
	/**
	 * returns the period (times time series points) the WIB thread checks for new values
	 */
	public int getPeriod();
	
	/**
	 * sets the period (times time series points) the WIB thread checks for new values
	 */
	public void setPeriod(int period);
}
