/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf.wibClassification;

import tools.descartes.wcf.forecasting.ForecastStrategyEnum;
import tools.descartes.wcf.management.wibManagement.IWorkloadIntensityBehavior;

public class ComplexClassificationStrategy implements IClassificationStrategy{
	
	public ComplexClassificationStrategy(IWorkloadIntensityBehavior wl){
		ClassificationSetting setting = wl.getClassSetting();
		/**
		 * double forecast horizon and forecast period and classification period if not limited by max_horizon
		 */
		if(wl.getForecastObjectives().getRecentHorizon()*setting.PERIOD_FACTOR_COMPLEX<=wl.getForecastObjectives().getMaxHorizon()){
			wl.getForecastObjectives().setRecentHorizon(wl.getForecastObjectives().getRecentHorizon()*setting.PERIOD_FACTOR_COMPLEX);
			wl.setPeriod(wl.getPeriod()*setting.PERIOD_FACTOR_COMPLEX);
		} else {
			wl.getForecastObjectives().setRecentHorizon(wl.getForecastObjectives().getMaxHorizon());
		}
		classify(wl);
	}
	
	
	/**
	 * This classification strategy observes 
	 * the noise level and can apply the moving averages 
	 * for smoothing, heuristically selects the Croston�s method 
	 * if necessary and evaluates the result plausibility and 
	 * the forecast accuracy via the estimated and observed 
	 * MASE metrics to adjust the current classification. 
	 * Either strategies from overhead class 3 or class 4 
	 * are selected.
	 */
	@Override
	public void classify(IWorkloadIntensityBehavior wl) {
		ClassificationSetting setting = wl.getClassSetting();
		if(setting.FIXED_FORECASTSTRATEGY){
			return;
		}
		
		wl.setMASEMetric(ClassificationUtility.calcForecastQuality(wl));
		setting.indices = ClassificationUtility.calcIndices(wl,setting.lastXValues);
		
		/**	
		 * choose the Croston's method for intermitted demand forecasting  if the rate of null values is above 25
		 */
		if(setting.indices[3]>setting.RATE_OF_ZEROVALUES_THRESHOLD){
			wl.getClassSetting().setRecentStrategy1(ForecastStrategyEnum.CROST);
			wl.getClassSetting().setRecentStrategy2(ForecastStrategyEnum.INACT);
			return;
		} 
		
		/**
		 * if time series shows high quartile dispersion and burstiness(value near 0) --> smooth the ts with MA x = 3;
		 */
		int atLeastThree = 0;
		if(setting.indices[4]>setting.QUARTILE_DISPERSION_THRESHOLD_SMOOTHING) atLeastThree++;
		if(setting.indices[1]<setting.BURSTINES_THRESHOLD_SMOOTHING) atLeastThree++;
		if(setting.indices[0]>setting.VARIANCE_COEFFICIENT_THRESHOLD_SMOOTHING) atLeastThree++;
		if(setting.indices[2]<setting.RELATIVE_MONOTONICITY_SMOOTHING) atLeastThree++;	
		if(atLeastThree>=3 && setting.lastSmoothedTimeSeriesPoint+setting.lastXValues < wl.getTimeSeries().size()+wl.getTimeSeries().getSkippedValues()){
			wl.setTimeSeries(ClassificationUtility.applyMovingAverage(wl.getTimeSeries(),setting.lastXValues));
			setting.lastSmoothedTimeSeriesPoint = wl.getTimeSeries().size()+wl.getTimeSeries().getSkippedValues();
			System.out.println("workload " + wl.getID() + ": time series has been smoothed due to high variance and burstiness");
		}
		
		/**
		 * if not select the best strategy according to observed or (2nd choice) estimated MASE 
		 * deactivate the worse fc_strategy
		 * if no comparison possible, activate 2nd strategy 
		 */
		if(wl.getForecastObjectives().getOverhead()==setting.OVERHEAD_THRESHOLD_COMPLEX){
			wl.getClassSetting().setRecentStrategy1(ForecastStrategyEnum.ARIMA);	
		} else {
			wl.getClassSetting().setRecentStrategy1(ForecastStrategyEnum.ETS);
		}	
		wl.getClassSetting().setRecentStrategy2(ForecastStrategyEnum.TBATS);
		
		
		/**
		 * Deactivates the worse forecast strategy if classification period is >1	
		 */
		ClassificationUtility.deactivateWorseStrategy(wl);
		System.out.println("workload " + wl.getID() + ": has been classified by " +wl.getClassSetting().getClassificationStrategy().name());
	}
}
