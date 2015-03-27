/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf.wibClassification;

import tools.descartes.wcf.forecasting.ForecastStrategyEnum;

public class ClassificationSetting {
		
	/**
	 * period defines how often a classification is 
	 * executed in times of checks for new time series points. 
	 * For a value of 1 a classification is requested 
	 * every new check for time series points and can be dynamically 
	 * increased by the period factors. 
	 * A classification can be executed before every 
	 * single forecast strategy call. 
	 * It is recommended to set this value equal 
	 * or smaller than 4 times the ForecastPeriod setting 
	 * (at least every forth forecast call) 
	 * to detect the time when a reclassification 
	 * is needed with an adequate precision.
	 */
	private int period = 1;
	
	/**
	 * Start Classification Strategy
	 */
	private ClassificationStrategyEnum classificationStrategy = ClassificationStrategyEnum.Initial;
	
	/**
	 * Start Forecast Strategy Tuple
	 */
	private ForecastStrategyEnum recentStrategy1 = ForecastStrategyEnum.NAIVE;
	private ForecastStrategyEnum recentStrategy2 = ForecastStrategyEnum.MEAN;

	/**
	 * 0 variance coefficient, 
	 * 1 burstiness index, 
	 * 2 relativeMonotonicity, 
	 * 3 rateOfZeroValues,
	 * 4 quartile dispersion coefficient
	 * 5 gradient of last quarter period related to median
	 */
	public double[] indices = new double[6];
	
	/**
	 * sizeThresholdInitial defines the time series size 
	 * at which the classification strategy is 
	 * switched from initial (overhead group 1) 
	 * to the fast classification strategy (overhead group 2). 
	 * The class 2 forecast strategies require at 
	 * least 4 time series values. 
	 * Using a rule of thumb this value can be set to half 
	 * the time series points of a period 
	 * (as this is in most cases the minimum size 
	 * for the plausibility check to work properly). 
	 * Proposed: 0.5x Frequency
	 */
	public int sizeThresholdInitial = 18;
	
	
	/**
	 * sizeThresholdFast defines the time series size at which 
	 * the classification strategy is switched from fast 
	 * (overhead group 2) to the complex classification 
	 * strategy (overhead groups 3 & 4). 
	 * The forecast strategies in overhead groups 3 & 4 
	 * require at least 3 periods of time series data 
	 * to detect seasonal patterns.
	 * 
	 * Proposed: 3x Frequency
	 */
	public int sizeThresholdFast = 108;
	
	/**
	 * Sliding Window Size (index calculation, smoothing)
	 * This setting defines the number of the most recent 
	 * time series points for that the analysis metrics are 
	 * calculated in the fast or complex classification 
	 * strategy. The value could be set to the Frequency 
	 * value, to capture the characteristics of the last 
	 * observed period. The value should be at least as 
	 * high as 30. As a rule of thumb from common statistics 
	 * says, the sample variance is an appropriate 
	 * estimate for the variance of the population 
	 * for more than 30 values. 
	 */
	public int lastXValues = 30; 
	
	/**
	 * Set this true if no forecast strategy switched should be allowed
	 */
	public final boolean FIXED_FORECASTSTRATEGY = false;
	
	/**
	 * PERIOD_FACTOR_COMPLEX setting is a multiplicator 
	 * for the StartHorizon and the ForecastPeriod 
	 * of the ForecastObjectives as well as for 
	 * the ClassificationPeriod 
	 * (each already multiplied by the PERIOD_FACTOR_FAST) 
	 * applied when switching from the fast to the 
	 * complex classification strategy. 
	 * 
	 * Proposed: [1;16]
	 */
	public final int PERIOD_FACTOR_COMPLEX = 8;
	
	/**
	 * Defines the overhead group limits 
	 * for a classification strategy
	 * If the overhead forecast objective is equal or smaller, 
	 * no switch to higher classification strategy allowed	
	 */
	public final int OVERHEAD_THRESHOLD_COMPLEX = 4;
	public final int OVERHEAD_THRESHOLD_FAST = 2;
	public final int OVERHEAD_THRESHOLD_INITIAL = 1;
	
	/**
	 * PERIOD_FACTOR_FAST setting is a multiplicator 
	 * for the StartHorizon and the ForecastPeriod 
	 * of the forecast objectives as well as 
	 * for the ClassificationPeriod applied 
	 * when switching from the initial to the 
	 * fast classification strategy. 
	 * As the forecast strategies in overhead group 2 
	 * only extrapolate the trend components, 
	 * the ForecastHorizon should not growth 
	 * bigger than an eighth of a period. 
	 * It may be better to apply forecast 
	 * strategies in overhead group 2 more frequently, 
	 * as their computational overheads stay below 100 ms.
	 * 
	 *  Proposed: [1;4]
	 */
	public final int PERIOD_FACTOR_FAST = 2;
	
	/**
	 * The RATE_OF_ZEROVALUES_THRESHOLD defines the sensitivity 
	 * to zero values in the time series and defines the threshold, 
	 * when the classification switches to the Croston�s 
	 * forecast strategy for intermittent demands. 
	 * For an observed rate of zero values, these values 
	 * a just skipped as the other forecast strategies are 
	 * not numerical stable if the time series contains zero 
	 * values. As it is not useful to switch the Croston�s 
	 * forecast strategy for only a few zero values, 
	 * it is recommended to set this threshold to 
	 * a value in [0.2;0.4]
	 */
	public final double RATE_OF_ZEROVALUES_THRESHOLD = 0.3;		
	
	/**
	 * BURSTINES_THRESHOLD_CS defines a necessary 
	 * precondition for the heuristic switch to the 
	 * cubic spline forecasting strategy. The observed 
	 * burstiness index value of the LastNValues need 
	 * to be higher than the value of 0.3, which can 
	 * recommend based on experiment experience.  
	 * The closer the burstiness index is to 0, 
	 * the more distinctive is the burstiness. 
	 */
	public final double BURSTINES_THRESHOLD_CS = 0.3;
	
	/**
	 * RELATIVE_GRADIENT_THRESHOLD_CS defines a necessary precondition 
	 * for the heuristic switch to the cubic spline forecasting strategy. 
	 * The observed relative gradient captures whether the discrete function 
	 * of the time series point changed during the most recent eighth of a period 
	 * for more (negative value) or less (positive values) than the median value 
	 * of the LastNValues. Based on experiment experience it can be recommended 
	 * to have this precondition fulfilled for any positive relative gradient. 
	 * 
	 *  Proposed: 0 
	 */
	public final double RELATIVE_GRADIENT_THRESHOLD_CS = 0;
	
	/**
	 * RELATIVE_MONOTONICITY_CS defines a necessary precondition for the heuristic 
	 * switch to the cubic spline forecasting strategy. 
	 * The observed relative monotonicity is the length 
	 * of the longest monotone section during the LastNValues 
	 * related to the value of LastNValues. The precondition is 
	 * fulfilled for a higher observed value. Based on experiment 
	 * experience a threshold of 0.2 is recommended.
	 */
	public final double RELATIVE_MONOTONICITY_CS = 0.2;
	
	/** 3 out of the 4 following thresholds need to be fulfilled to apply smoothing
	 * 
	 */
	
	/**
	 * BURSTINES_THRESHOLD_SMOOTHING a precondition for the heuristic 
	 * application of smoothing averages for noise reduction. 
	 * The observed burstiness index value of the LastNValues 
	 * need to be smaller than the value of 0.15, which can 
	 * be recommended based on experiment experience. 
	 * The closer the burstiness index is to 0, the more 
	 * distinctive is the burstiness.
	 */
	public final double BURSTINES_THRESHOLD_SMOOTHING = 0.15;
	
	/**
	 * QUARTILE_DISPERSION_THRESHOLD_SMOOTHING defines a precondition 
	 * for the heuristic application of smoothing averages for noise 
	 * reduction. The quartile dispersion captures is a unit-less index 
	 * capturing the variance level of the LastNValues. 
	 * If the distance between the quartiles is at least two times 
	 * bigger than the median value, the precondition would be true 
	 * for a recommended threshold value of 2. 
	 */
	public final double QUARTILE_DISPERSION_THRESHOLD_SMOOTHING = 2;
	
	/**
	 * VARIANCE_COEFFICIENT_THRESHOLD_SMOOTHING defines a precondition for the heuristic application 
	 * of smoothing averages for noise reduction. 
	 * The quartile dispersion captures is a unit-less index 
	 * capturing the variance level of the LastNValues. 
	 * If the standard deviation is as high as the arithmetic mean value, 
	 * the precondition would be true for a recommended threshold value of 1.  
	 */
	public final double VARIANCE_COEFFICIENT_THRESHOLD_SMOOTHING = 1;
	
	/** 
	 * This threshold defines a precondition for the application of 
	 * smoothing averages for noise reduction. The observed relative 
	 * monotonicity is the length of the longest monotone section during 
	 * the LastNValues related to the value of LastNValues.  
	 * The precondition is fulfilled for a lower observed value 
	 * indicating a high noise level. Based on experiment experience 
	 * a threshold of 0.15 is recommended.
	 */
	public final double RELATIVE_MONOTONICITY_SMOOTHING = 0.15;
	
	/**
	 * variable to assure that no times series interval is smoothed more than once
	 */
	public long lastSmoothedTimeSeriesPoint = 0;
	
	public ClassificationSetting(int class_period) {
		this.period = class_period;
	}
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	
	public ClassificationStrategyEnum getClassificationStrategy() {
		return classificationStrategy;
	}
	public void setClassificationStrategy(ClassificationStrategyEnum classificationStrategy) {
		this.classificationStrategy = classificationStrategy;
	}
	public ForecastStrategyEnum getRecentStrategy1() {
		return recentStrategy1;
	}
	public void setRecentStrategy1(ForecastStrategyEnum recentStrategy1) {
		this.recentStrategy1 = recentStrategy1;
	}
	public ForecastStrategyEnum getRecentStrategy2() {
		return recentStrategy2;
	}
	public void setRecentStrategy2(ForecastStrategyEnum recentStrategy2) {
		this.recentStrategy2 = recentStrategy2;
	}
}