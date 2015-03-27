/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf.management;

import tools.descartes.wcf.management.wibManagement.IWorkloadIntensityBehavior;

public interface IManager {
	
	/**
	 * adds a WIB Object to the Managers queue
	 */
	public void addWIB(IWorkloadIntensityBehavior wl);
	
	/**
	 * removes a WIB Object from the Managers queue and deactivates the WIB thread gracefully
	 */
	public void removeWIB(int ID);
	
	/**
	 * returns a managed WIB object to update the attributes and referenced forecast objectives + classification settings
	 */
	public IWorkloadIntensityBehavior updateWIB(int ID);	
	
	/**
	 * notifies the WIBs thread to trigger a forecast execution if sleeping
	 */
	public void requestForecast(int ID);	
	
	/**
	 * notifies the WIBs thread to trigger a classification execution if sleeping
	 */
	public void requestClassification(int ID);
	
	/**
	 * @return number of managed WIB
	 */
	public int getNumberOfManagedWIBs();	
}
