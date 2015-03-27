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

public class InitialClassificationStrategy implements IClassificationStrategy{
		
	public InitialClassificationStrategy(IWorkloadIntensityBehavior wl){
		classify(wl);
	}
	
	
	/**
	 * The initial classification strategy 
	 * checks via the estimated and observed MASE metrics 
	 * whether the arithmetic mean is a better forecast 
	 * estimate than the na�ve approach.
	 */
	@Override
	public void classify(IWorkloadIntensityBehavior wl) {
		ClassificationSetting setting = wl.getClassSetting();
		
		wl.setMASEMetric(ClassificationUtility.calcForecastQuality(wl));
		if(wl.getTimeSeries()==null){
			return;
		}
		/**
		 * checks whether higher ClassificationStrategy is suitable 
		 */
		if(wl.getTimeSeries().getFrequency()<setting.sizeThresholdInitial)setting.sizeThresholdInitial = wl.getTimeSeries().getFrequency();
		if(wl.getTimeSeries().size()>setting.sizeThresholdInitial && wl.getForecastObjectives().getOverhead()>setting.OVERHEAD_THRESHOLD_INITIAL){
			wl.getClassSetting().setClassificationStrategy(ClassificationStrategyEnum.Fast);
			/**
			 * switches to higher ClassificationStrategy (classification is started in constructor)
			 */
			wl.setClassificator(new FastClassificationStrategy(wl));
		} 
		/**
		 * if not select the best strategy according to observed or (2nd choice) estimated MASE 
		 * deactivate the worse fc_strategy
		 * if no comparison possible, activate 2nd strategy 
		 */
		else {
			if(setting.FIXED_FORECASTSTRATEGY){
				return;
			} else {
				wl.getClassSetting().setRecentStrategy1(ForecastStrategyEnum.NAIVE);
				wl.getClassSetting().setRecentStrategy2(ForecastStrategyEnum.MEAN);
				/**
				 * Deactivates the worse forecast strategy if classification period is >1	
				 */
				ClassificationUtility.deactivateWorseStrategy(wl);
			}
			System.out.println("workload " + wl.getID() + ": has been classified by " +wl.getClassSetting().getClassificationStrategy().name());
		}
	}
}
