/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf.forecasting;

import tools.descartes.wcf.forecasting.strategies.ARIMA101Forecaster;
import tools.descartes.wcf.forecasting.strategies.ARIMAForecaster;
import tools.descartes.wcf.forecasting.strategies.CSForecaster;
import tools.descartes.wcf.forecasting.strategies.CrostonForecaster;
import tools.descartes.wcf.forecasting.strategies.ETSForecaster;
import tools.descartes.wcf.forecasting.strategies.MeanForecaster;
import tools.descartes.wcf.forecasting.strategies.NaiveForecaster;
import tools.descartes.wcf.forecasting.strategies.SESForecaster;
import tools.descartes.wcf.forecasting.strategies.TBATSForecaster;
import tools.descartes.wcf.management.timeSeries.ITimeSeries;
import tools.descartes.wcf.wibClassification.ClassificationUtility;

public class ForecasterFactory {
	public static IForecaster<Double> getForecaster(ForecastStrategyEnum strategy, ITimeSeries<Double> ts, int alpha){
		IForecaster<Double> forecaster = null;
		switch(strategy){
			case NAIVE: 	
				forecaster = new NaiveForecaster(ts,alpha);
				break;		
			case MEAN:
				forecaster = new MeanForecaster(ClassificationUtility.getLastXofTS(ts, 10),alpha);
				break;
			case CS:		
				forecaster = new CSForecaster(ClassificationUtility.getLastXofTS(ts, 30),alpha);
				break; 					//Cubic Smoothing Splines			
			case SES:
				forecaster = new SESForecaster(ts,alpha);
				break;					//Simple Exponential Smoothing
			case CROST:		
				forecaster = new CrostonForecaster(ts);
				break;					//Croston's Method 
			case ETS:					//Extended Exponential Smoothing
				forecaster = new ETSForecaster(ts,alpha);
				break;
			case ARIMA101:
				forecaster = new ARIMA101Forecaster(ts,alpha);
				break;
			case ARIMA:
				forecaster = new ARIMAForecaster(ts,alpha);
				break;
			case TBATS:		
				forecaster = new TBATSForecaster(ts,alpha);
				break;
			case INACT: return null;
		}
		return forecaster;
	}
}
