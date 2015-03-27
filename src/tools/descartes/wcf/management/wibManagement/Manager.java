/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf.management.wibManagement;

import java.util.ArrayList;
import java.util.List;

import tools.descartes.wcf.management.IManager;

public class Manager implements IManager {

	private int workloadsToManage = 0;
	private List<IWorkloadIntensityBehavior> workloads = new ArrayList<IWorkloadIntensityBehavior>();
	public static Manager INSTANCE = null;
	
	private Manager(){
		Manager.INSTANCE = this;
	}
	
	public static Manager getInstance() {
		if (null == Manager.INSTANCE) {
			Manager.INSTANCE = new Manager();
		}
		return Manager.INSTANCE;
	}
	
	public int getNumberOfManagedWIBs() {
		return workloadsToManage;
	}

	public List<IWorkloadIntensityBehavior> getWorkloads() {
		return workloads;
	}
	
	public void addWIB(IWorkloadIntensityBehavior wl) {
		workloadsToManage++;
		workloads.add(wl);
	}
	
	
	public void removeWIB(int ID){
		workloads.get(ID).setActive(false);
		workloads.remove(ID);
	}
	
	@Override
	public IWorkloadIntensityBehavior updateWIB(int ID) {
		return workloads.get(ID);
	}


	@Override
	public void requestForecast(int ID) {
		workloads.get(ID).notify();
	}

	@Override
	public void requestClassification(int ID) {
		workloads.get(ID).notify();
	}
}
