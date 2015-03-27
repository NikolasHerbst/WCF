/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf.wibClassification;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import tools.descartes.wcf.forecasting.ForecastStrategyEnum;
import tools.descartes.wcf.forecasting.IForecastResult;
import tools.descartes.wcf.management.timeSeries.ITimeSeries;
import tools.descartes.wcf.management.timeSeries.TimeSeries;
import tools.descartes.wcf.management.wibManagement.IWorkloadIntensityBehavior;


public class ClassificationUtility{

	/**
	 * Calculates the MASE Forecast Quality metric of the last forecast 
	 * compared to observed values
	 * @param wl
	 * @return
	 */
	public static double[] calcForecastQuality(IWorkloadIntensityBehavior wl){
		
		IForecastResult fr1 = wl.getResult1(), fr2 = wl.getResult2();
		double[] mase = new double[2];
	
		if(fr1==null && fr2==null){
			mase[0] = Integer.MAX_VALUE; mase[1] = Integer.MAX_VALUE;
			return mase;
		}
		
		//get the observed values for the specific forecast
		ITimeSeries<Double> ts = wl.getTimeSeries();
		if(fr1!=null){
			if(ts.size()+ts.getSkippedValues()>=fr1.getOriginal().size()+fr1.getForecast().size()+fr1.getOriginal().getSkippedValues()){
				Double[] observations1 = new Double[fr1.getForecast().size()+1];
				observations1 = ts.getValues().subList(ts.size()-fr1.getForecast().size()-1, ts.size()).toArray(observations1);
			
				//sum of absolute differences between an observation and its previous observation (quality of naive forecast) 
				double sum1 = 0;
				for(int i = 1; i<observations1.length;i++){
					sum1 += Math.abs(observations1[0]-observations1[i]);
				}
				double naive1 = sum1/(observations1.length-1);		
				/*calculation of the sum of absolute errors between a forecasted value 
				* and the according observation divided by the value for the naive forecast (sum of q values)
				*/
				if(naive1==0){
					System.out.println("error in forecast accuracy calculation - time series may be constant");
					mase[0] = Integer.MAX_VALUE;
				}
				double qsum1 = 0;
				for(int i = 1; i<observations1.length;i++){
					qsum1 += Math.abs((fr1.getForecast().getValues().get(i-1)-observations1[i])/naive1);
				}
				
				// the MASE metric is defined as arithmetic mean value of the absolute q values
				mase[0] = qsum1/(observations1.length-1);
			} else {
				mase[0] = Integer.MAX_VALUE;
			}
		} else {
			mase[0] = Integer.MAX_VALUE;
		}
		
		if(fr2!=null){
			if (ts.size()+ts.getSkippedValues()>=fr2.getOriginal().size()+fr2.getForecast().size()+fr2.getOriginal().getSkippedValues()){
				Double[] observations2 = new Double[fr2.getForecast().size()+1];
				observations2 = ts.getValues().subList(ts.size()-fr2.getForecast().size()-1, ts.size()).toArray(observations2);
				//sum of absolute differences between an observation and its previous observation (quality of naive forecast) 
				double sum2 = 0;
				for(int i = 1;i< observations2.length;i++){
					sum2 += Math.abs(observations2[0]-observations2[i]);
				}
				double naive2 = sum2/(observations2.length-1);
				
				/*calculation of the sum of absolute errors between a forecasted value 
				* and the according observation divided by the value for the naive forecast (sum of q values)
				*/
				if(naive2==0){
					System.out.println("error in forecast accuracy calculation - time series may be constant");
					return mase;
				}
				double qsum2 = 0;
				for(int i = 1;i< observations2.length;i++){
					qsum2 += Math.abs((fr2.getForecast().getValues().get(i-1)-observations2[i])/naive2);
				}
				
				// the MASE metric is defined as arithmetic mean value of the absolute q values
				mase[1] = qsum2/(observations2.length-1);
			} else {
				mase[1] = Integer.MAX_VALUE;
			}
		} else {
			mase[1] = Integer.MAX_VALUE;
		}
		return mase;
	}
	
	/**
	 * 0 variance coefficient: 
	 * 		The VarianceCoefficient is defined as the StandardDeviation 
	 * 		divided by the mean value.  The VarianceCoefficient is a relative measure of dispersion (without a unit to enable comparison). A coefficient close to zero indicates low variance, 
	 * 		a coefficient close or bigger than 1 indicates high variance.
	 * 1 burstiness index: 
	 * 		The BurstinessIndex is defined as the maximum values divided by the median. 
	 *		A burst is a positive peak. The BurstinessIndex characterizes the level of bursts 
	 *		(not the frequency of bursts) whereas an index value of close to zero stands 
	 *		for high level of bursts � 
	 *		no bursts if the index value is 1 or close.
	 * 2 relativeMonotonicity: 
	 * 		By iteration through the time series values the number of 
	 * 		consecutive monotonic values (up or downwards) is counted. 
	 * 		The LongestMonotonicSection is the maximum 
	 * 		of these numbers and can be related to the Length of the analyzed time series span.  
	 * 		The LongestMonotonicSection in relation the length is a rate that characterized 
	 * 		the trend behavior of the time series. A rate close to zero indicates a high level 
	 * 		of noise or high frequency seasonal components. A rate close to 1 indicates the absence 
	 * 		of noise or seasonal components � in this case even na�ve forecast strategies can have results of good quality. 
	 * 3 rateOfZeroValues: 
	 * 		The RateOfZeroValues is the number of zeros in relation to the analyzed 
	 * 		length of the time series. Most forecast strategies need non-zero 
	 * 		values as input and therefore the zero values are removed before. 
	 * 		If the rate is higher than a threshold one could assume an intermittent 
	 * 		demand and apply a special forecast strategy (CROSTON)
	 * 4 quartile dispersion coefficient:
	 * 		The QuartileDispersionCoefficient is defined as the distance of the 
	 * 		Quartiles divided by the Median value. The QuartileDispersionCoefficient is 
	 * 		another relative measure of dispersion. Again, a coefficient close to 
	 * 		zero indicates low variance, a coefficient close or bigger than 1 
	 * 		indicates high variance.
	 * 5 relative absolute gradient:
	 * 		The RelativeGradient is the absolute gradient of the latest 
	 * 		quarter of a Period in relation the Median. 
	 * 		The RelativeGradient captures the steepness of the 
	 * 		latest quarter Period without a unit, as it is related to the Median � 
	 * 		a positive RelativeGradient shows that the last quarter Period changed 
	 * 		less than the Median value, a negative value indicates a steep section 
	 * 		within the time series (e.g. the limb of a seasonal component). 
	 *		In this case the choice of simple forecast strategies should be limited. 
	 * @return
	 */
	public static double[] calcIndices(IWorkloadIntensityBehavior wl, int x){
		double[] indices = new double[6];
		ITimeSeries<Double> ts = wl.getTimeSeries();
		ITimeSeries<Double> shortenedTS = getLastXofTS(ts,x);
		indices[0] = calcVarianceCoefficient(shortenedTS);
		indices[1] = calcBurstinessIndex(shortenedTS);
		indices[2] = calcRelativeMonotonicity(shortenedTS);
		indices[3] = countNumberOfZeroValues(shortenedTS);
		indices[4] = calcQuartileDispersionCoefficient(shortenedTS);
		indices[5] = calcRelativeGradient(shortenedTS);
		
		DecimalFormat g = new DecimalFormat("#0.0000");
		System.out.println(	"workload " 					+ wl.getID() + ":" 	+
							"\nvariance coefficient:	" 	+ g.format(indices[0]) + 
							"\nburstiness index:	" 		+ g.format(indices[1]) +				
							"\nmonotonicity index:	" 		+ g.format(indices[2]) +
							"\nrate of null values: 	" 	+ g.format(indices[3]) +
							"\nquartile dispersion coefficient:	" + g.format(indices[4]) +
							"\nrelative absolute gradient:	" + g.format(indices[5]));
		return indices;	
	}
	
	/**
	 * calculates the gradient in the latest quarter of a ts_period and relates it to the median
	 * @param ts
	 * @return
	 */
	public static double calcRelativeGradient(ITimeSeries<Double> ts) {
		int lastEighths = ts.getFrequency()/8;
		double last = ts.getValues().get(ts.size()-1);
		double firstOfLastQuarter = ts.getValues().get(ts.size()-lastEighths);
		double gradient = (calcMedian(ts)-Math.abs(last-firstOfLastQuarter))/(lastEighths);
		return gradient;
	}

	/**
	 * @return monotonicity of times series in relation to its length
	 */
	public static double calcRelativeMonotonicity(ITimeSeries<Double> ts){
		return (double)calcLengthOfBiggestMonotonicSection(ts)/(double)ts.size();
	}
	
	/**
	 *	@return variance coefficient (standard deviation divided by arth. mean)	 
	 */
	public static double calcVarianceCoefficient(ITimeSeries<Double> ts){
		return calcDeviation(ts)/calcArithMean(ts);
	}
	
	/**
	 * @return burstinessIndex: median div. by maximum - value of 1 is indicating no burstiness 
	 * near 0 indicating high burstiness
	 */
	public static double calcBurstinessIndex(ITimeSeries<Double> ts){
		return calcMedian(ts)/calcMaximum(ts);
	}
	
	public static double calcQuartileDispersionCoefficient(ITimeSeries<Double> ts){
		double[] quartiles = null;
		quartiles = calcQuartiles(ts.getValues());
		return (quartiles[2]-quartiles[0])/quartiles[1];
	}
	
	public static double[] calcQuartiles(List<Double> values) {
	    if (values.size() > 3){
	    	double median = calcMedian(values);
	    	List<Double> lowerHalf = getValuesLessThan(values, median, true);
	    	List<Double> upperHalf = getValuesGreaterThan(values, median, true);
	    	return new double[] {calcMedian(lowerHalf), median, calcMedian(upperHalf)};
	    } else {
	    	return new double[] {0d,values.get(0),0d};
	    }
	}
	 
	public static List<Double> getValuesGreaterThan(List<Double> values, double limit, boolean orEqualTo){
	    List<Double> modValues = new ArrayList<Double>();
	    for (double value : values)
	        if (value > limit || (value == limit && orEqualTo))
	            modValues.add(value);
	    return modValues;
	}
	 
	public static List<Double> getValuesLessThan(List<Double> values, double limit, boolean orEqualTo){
	    List<Double> modValues = new ArrayList<Double>();
	    for (double value : values)
	        if (value < limit || (value == limit && orEqualTo))
	            modValues.add(value);
	    return modValues;
	}
	
	
	/**
	 *	@return counts Number of zeros in time series	 
	 */
	public static double countNumberOfZeroValues(ITimeSeries<Double> ts){
		List<Double> values = ts.getValues();
		int i = 0;
		for(double t : values){
			if(t<=0){
				i++;
			}
		}
		return (double)i/(double)ts.size();
	}
	
	/**
	 * @return arithmetic mean of the time series
	 */
	public static double calcArithMean(ITimeSeries<Double> ts){
		List<Double> values = ts.getValues();
		double sum = 0;
		for(double t : values){
			sum+=t;
		}
		return sum/ts.size();
	}
	
	/**
	 * @return standard deviation of the time series from its arithmetic mean value 
	 */
	public static double calcDeviation(ITimeSeries<Double> ts){
		double mean = calcArithMean(ts);
		List<Double> values = ts.getValues();
		double sumSquaredDiff = 0;
		for(double t : values){
			sumSquaredDiff+=Math.pow(t-mean, 2);
		}
		double stdDev = Math.sqrt(sumSquaredDiff/(values.size()-1));
		return stdDev;
	}
	
	/**
	 * @return maximum value of the time series
	 */
	public static double calcMaximum(ITimeSeries<Double> ts){
		List<Double> values = ts.getValues();
		double max = 0;
		for(double t : values){
			if(t>max){
				max = t;
			}
		}
		return max;
	}
	
	/**
	 * @return median value of the time series
	 */
	public static double calcMedian(ITimeSeries<Double> ts){
		double median = 0;
		List<Double> values = ts.getValues();
		Collections.sort(values);
		if(values.size()%2==0){
			median = 0.5*(values.get((int)Math.floor(values.size()/2.0)) + values.get((int)Math.ceil(values.size()/2.0)));
		} else {
			median = values.get((int)(values.size()/2.0 + 0.5));
		}
		return median;
	}
	
	public static double calcMedian(List<Double> list){
		double median = 0;
		Collections.sort(list);
		if(list.size()%2==0){
			median = 0.5*(list.get((int)Math.floor(list.size()/2.0)) + list.get((int)Math.ceil(list.size()/2.0)));
		} else {
			median = list.get((int)(list.size()/2.0 + 0.5));
		}
		return median;
	}
	
	/**
	 * 	@return length of the longest monotonic section of the time series
	 */
	public static int calcLengthOfBiggestMonotonicSection(ITimeSeries<Double> ts){
		List<Double> values = ts.getValues();
		int counter = 0;
		List<Integer> lengths = new ArrayList<Integer>();
		double old = values.get(0);
		boolean asc = true;
		for(double t: values){
			if(asc){
				if(t>=old){
					counter++;
				} else {
					lengths.add(counter);
					counter = 0;
					asc = false;
				}
			} else {
				if(t<=old){
					counter++;
				} else {
					lengths.add(counter);
					counter = 0;
					asc = true;
				}
			}
			old = t;
		}
		lengths.add(counter);	
		return Collections.max(lengths);
	}
	
	/**
	 * @return time series of same length as original time series, 
	 * smoothed last x by moving average (no weights, window size 3)
	 */
	public static ITimeSeries<Double> applyMovingAverage(ITimeSeries<Double> ts, int x){
		ITimeSeries<Double> newTS = new TimeSeries<Double>(ts.getStartTime(), ts.getDeltaTime(), ts.getDeltaTimeUnit(),ts.getFrequency(),ts.getMaxPeriods(),ts.getSkippedValues());
		if(ts.size()>x){
			Double[] a = new Double[ts.size()-x];
			a = ts.getValues().subList(0, ts.size()-x).toArray(a);			
			Double[] b = new Double[x];
			b = getLastXofTS(ts,x).getValues().toArray(b);	
			b = smoothWithDegreeTwo(b);
			newTS.appendAll(a);
			newTS.appendAll(b);
		} else {
			Double[] a = new Double[ts.size()];		
			a = ts.getValues().toArray(a);
			a = smoothWithDegreeTwo(a);
			newTS.appendAll(a);
		}	
		return newTS;
	}
	
	/**
	 * WeightedMovingAverages smoothing is applied in a sliding window of a 
	 * given window size 3 and a weight vector [0.25;0.5;0.25] to calculate a weighted 
	 * average for every time series point. (E.g. for careful noise reduction: 
	 * window size = 3 and weight vector (0.25, 0.5, 0.25)).
	 * WeightedMovingAverages are a simple technique to smooth 
	 * out noise components or bursts and not losing too much information 
	 * on deterministic components as trend and seasonal patterns.
	 */
    public static Double[] smoothWithDegreeTwo(Double[] a) {
        int lengthofthevector = a.length;
        a[0]=Math.rint((2*a[0]+a[1])/3.0f);
        a[lengthofthevector-1]=Math.rint((2*a[lengthofthevector-1]+a[lengthofthevector-2])/3.0f);
        for (int i=1;i<(lengthofthevector-1);i++)
            a[i] = Math.rint((2*a[i]+a[i+1]+a[i-1])/4.0f);
        return a;
    }

	/**
	 * @return time series with half the length of the original time series 
	 * by taking the arithmetic mean of two values
	 */	
	public static ITimeSeries<Double> smoothTSByCombination(ITimeSeries<Double> ts){
		ITimeSeries<Double> newTS = new TimeSeries<Double>(ts.getStartTime(), ts.getDeltaTime()*2, ts.getDeltaTimeUnit(), ts.getFrequency(),ts.getMaxPeriods(),ts.getSkippedValues());
		Double[] a = null;
		a = ts.getValues().toArray(a);
		int i = 0;
		if(a.length%2!=0){
			i = 1;
			newTS.append(a[0]);
		}
		for(;i<a.length;i+=2){
			newTS.append(a[i]+a[i+1]/2);
		}
		return newTS;
	}
	
	/**
	 * Returns a new time series object shortened to the last x values
	 * @param ts
	 * @param x
	 * @return
	 */
	public static ITimeSeries<Double> getLastXofTS(ITimeSeries<Double> ts, int x){
		if(ts.size()>=x){
			Double[] a = new Double[ts.size()]; 
			a = ts.getValues().toArray(a);
			Double[] b = new Double[x];
			for(int i = 0; i<x; i++){
				b[i] = a[ts.size()-x+i];
			}
			long newStartTime = ts.getStartTime().getTime();
			newStartTime += (ts.size()-x)*ts.getDeltaTimeUnit().toMillis(ts.getDeltaTime());	
			TimeSeries<Double> tsLastX = new TimeSeries<Double>(new Date(newStartTime),ts.getDeltaTime(),ts.getDeltaTimeUnit(),ts.getFrequency(),ts.getMaxPeriods(),ts.getSkippedValues());
			tsLastX.appendAll(b);
			return tsLastX;
		} else {
			return ts;
		}
	}
	
	/**
	 * Deactivates the worse forecast strategy if classification period is >1	
	 */
	public static void deactivateWorseStrategy(IWorkloadIntensityBehavior wl){
		/**
		 * deactivate only if classification is not triggered every forecast execution
		 */
		if(wl.getClassSetting().getPeriod()>1){
			/**
			 * try first if observed MASE value is available
			 */	
		if(wl.getMASEMetric()!= null && wl.getMASEMetric()[0]!=Integer.MAX_VALUE && wl.getMASEMetric()[1]!=Integer.MAX_VALUE){
			if(wl.getMASEMetric()[1]<wl.getMASEMetric()[0]){
				wl.getClassSetting().setRecentStrategy1(ForecastStrategyEnum.INACT);
			} else {
				wl.getClassSetting().setRecentStrategy2(ForecastStrategyEnum.INACT);
			}	
		/**
		 * if not, decide based on historic MASE
		 */
		} else if(wl.getResult1()!=null && wl.getResult2()!=null){
			if(wl.getResult2().getMeanAbsoluteScaledError()<wl.getResult1().getMeanAbsoluteScaledError()){
				wl.getClassSetting().setRecentStrategy1(ForecastStrategyEnum.INACT);
			} else {
				wl.getClassSetting().setRecentStrategy2(ForecastStrategyEnum.INACT);
			}
		}
		}
	}
}
