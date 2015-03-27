/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf.management.wibManagement;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import tools.descartes.wcf.forecasting.ForecastObjectives;
import tools.descartes.wcf.forecasting.ForecasterFactory;
import tools.descartes.wcf.forecasting.IForecastResult;
import tools.descartes.wcf.forecasting.IForecaster;
import tools.descartes.wcf.management.persistency.IPersistency;
import tools.descartes.wcf.management.persistency.Persistency;
import tools.descartes.wcf.management.timeSeries.ITimeSeries;
import tools.descartes.wcf.management.timeSeries.TimeSeries;
import tools.descartes.wcf.wibClassification.ClassificationSetting;
import tools.descartes.wcf.wibClassification.IClassificationStrategy;
import tools.descartes.wcf.wibClassification.InitialClassificationStrategy;


public class WorkloadIntensityBehavior implements Runnable, IWorkloadIntensityBehavior {
	
	private final int ID;
	private Thread t = null;
	private Manager myManager;
	private IForecaster<Double> forecaster1 = null, forecaster2 = null;
	private ITimeSeries<Double> ts = null;
	private IForecastResult result1 = null, result2 = null;
	
	private boolean filterResults = true; //only one forecast is printed at a time - the forecast, that is supposed to be the better
	
	/**
	 *  defines the period in times of ts_deltatime  
	 *  the WIB tries to import new ts_values
	 *  the period for classification + the period for forecasting take this period as basis
	 */
	private int period = 2;		 
	private int fcCount = 0;
	private boolean active = true;
	private double[] mase = new double[2];
	
	private ClassificationSetting classSetting = null;

	private IClassificationStrategy classificator = new InitialClassificationStrategy(this);
	private ForecastObjectives forecastObjectives = null;
	private IPersistency persist = new Persistency();
	
	
	/**
	*	 
	* The Workload Object is self-managed - the tread routine is started by constructor
	* For maintenance tasks only, a manager is needed.
	* 
 	* @param myManager 
 	* @param startTime of the time series
 	* @param deltaTime 
 	* @param deltaTimeUnit
 	* @param frequency
 	* @param maxPeriods
 	* @param check_period how often the workload thread checks for new values (check period ~ expected new ts values)
 	* @param class_period how often the classification is called in times of check_period
 	* @param confidence
 	* @param maxHorizon 
 	* @param startHorizon
 	* @param fcperiod
 	* @param overhead
 	*/
	public WorkloadIntensityBehavior(Manager myManager, Date startTime, Long deltaTime, TimeUnit deltaTimeUnit, int frequency, int maxPeriods,
					int check_period, int class_period, int confidence, int maxHorizon, int startHorizon, int fcperiod, int overhead){
		this.myManager = myManager;
		this.ID = myManager.getNumberOfManagedWIBs();
		myManager.addWIB(this);
		persist.initWIB(this);
		this.period = check_period;
		this.classSetting = new ClassificationSetting(class_period);
		this.forecastObjectives = new ForecastObjectives(maxHorizon,startHorizon,fcperiod,overhead,confidence);
		this.ts = persist.readTimeSeriesConf(this);
		
		if(this.ts==null){
			this.ts = new TimeSeries<Double>(startTime,deltaTime,deltaTimeUnit,frequency,maxPeriods,0);
			persist.writeTimeSeriesConf(this);
		}

		ts = persist.readNewTimeSeriesValues(this);
		persist.writeWIB(this);
		
		this.t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		
		long startRoutine;
		long oldSize = 0, count = -1;
		
		while(active){
			startRoutine = System.currentTimeMillis();
			oldSize = ts.size()+ts.getSkippedValues();
			ts = persist.readNewTimeSeriesValues(this);
			
			//only classify according to period
			callClassificator(oldSize,count);
			
			//only forecast according to period
			if(oldSize<ts.size()+ts.getSkippedValues() && count%forecastObjectives.getPeriod()==0){
				callForecaster();
				writeFcResult();				
				fcCount++;
			}
			persist.writeWIB(this);
			persist.writeTimeSeriesConf(this);
			count++;
			
			sleepUntilNextPeriod(startRoutine);
		}
	}
	
	private void writeFcResult(){
		//print only the better result if filterResults
		if(!filterResults){
			if(result1!=null && result1.getForecast().size()>0){
				persist.writeForecastResult(result1, this, fcCount);	
			}
			if(result2!=null && result2.getForecast().size()>0){
				persist.writeForecastResult(result2, this, fcCount);	
			}
		} else if(result1!=null && result2!=null && result1.getForecast().size()>0 && result2.getForecast().size()>0) {
			if(result1.isPlausible() && !result2.isPlausible()){
				persist.writeForecastResult(result1, this, fcCount);
			} 
			else if(!result1.isPlausible() && result2.isPlausible()){
				persist.writeForecastResult(result2, this, fcCount);
			}
			else if(result1.getMeanAbsoluteScaledError()<result2.getMeanAbsoluteScaledError()){
				persist.writeForecastResult(result1, this, fcCount);
			} else {
				persist.writeForecastResult(result2, this, fcCount);
			}
		} else {
			if(result1!=null && result1.getForecast().size()>0){
				persist.writeForecastResult(result1, this, fcCount);	
			}
			if(result2!=null && result2.getForecast().size()>0){
				persist.writeForecastResult(result2, this, fcCount);	
			}
		}
	}
	
	private void callForecaster(){
		forecaster1 = ForecasterFactory.getForecaster(classSetting.getRecentStrategy1(), ts, forecastObjectives.getConfidenceLevel());
		forecaster2 = ForecasterFactory.getForecaster(classSetting.getRecentStrategy2(), ts, forecastObjectives.getConfidenceLevel());
		if(forecaster1!=null){
			result1 = forecaster1.forecast(forecastObjectives.getRecentHorizon());
		}
		if(forecaster2!=null){
			result2 = forecaster2.forecast(forecastObjectives.getRecentHorizon());
		}
		
		//delete old results if forecaster inactive
		if(forecaster1==null){
			result1 = null;
		}
		if(forecaster2==null){
			result2=null;
		}
	}
	
	private void callClassificator(long oldSize, long count){
		if(oldSize<ts.size()+ts.getSkippedValues() && count%classSetting.getPeriod()==0){
			classificator.classify(this);
		}	
	}
	
	private void sleepUntilNextPeriod(long startRoutine){
		long routineDuration, recent_period;
		routineDuration = System.currentTimeMillis()-startRoutine;
		recent_period = ts.getDeltaTimeUnit().toMillis(period*ts.getDeltaTime());
		if(recent_period-routineDuration>0){
			System.out.println("workload " +ID+ ": sleeping for "+ (recent_period-routineDuration) +" milliseconds" );
			System.out.println("workload " +ID+ ": time series size " +ts.size());
			try {
				Thread.sleep(recent_period-routineDuration);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("routine duration:" + routineDuration + " longer than period of " + recent_period);
		}
	}
	
	public void setClassificator(IClassificationStrategy classificator) {
		this.classificator = classificator;
	}
	
	public int getID() {
		return ID;
	}
	
	public Thread getThread() {
		return t;
	}

	public ITimeSeries<Double> getTimeSeries() {
		return ts;
	}

	public ClassificationSetting getClassSetting() {
		return classSetting;
	}
	
	public Manager getMyManager() {
		return myManager;
	}
	public ForecastObjectives getForecastObjectives() {
		return forecastObjectives;
	}
	
	public void setTimeSeries(ITimeSeries<Double> ts){
		this.ts = ts;
	}
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	public IForecastResult getResult1() {
		return result1;
	}
	
	public IForecastResult getResult2() {
		return result2;
	}

	public double[] getMASEMetric() {
		return mase;
	}

	public void setMASEMetric(double[] mase) {
		this.mase = mase;
	}
	
	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}
	
}
