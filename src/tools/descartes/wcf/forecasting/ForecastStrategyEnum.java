/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf.forecasting;

public enum ForecastStrategyEnum {
	NAIVE,		
	MEAN,
	CS, 		//Cubic Smoothing Splines
	SES,		//Simple Exponential Smoothing
	CROST,		//Croston's Method 
	ETS,		//Extended Exponential Smoothing
	ARIMA101,
	ARIMA,
	TBATS,
	INACT 		//deactivated
}
