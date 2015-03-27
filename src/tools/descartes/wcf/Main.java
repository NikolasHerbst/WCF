/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf;

import java.sql.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import tools.descartes.wcf.management.wibManagement.IWorkloadIntensityBehavior;
import tools.descartes.wcf.management.wibManagement.Manager;
import tools.descartes.wcf.management.wibManagement.WorkloadIntensityBehavior;


public class Main {
	
public static void main(String[] args){
		
		Manager manager = Manager.getInstance();
		
		manager.addWIB(new WorkloadIntensityBehavior(manager, new Date(System.currentTimeMillis()), 4L, TimeUnit.SECONDS,36,4,1,1,80,18,1,1,4));
		//manager.addWIB(new WorkloadIntensityBehavior(manager, new Date(System.currentTimeMillis()), 10L, TimeUnit.SECONDS,48,4,1,1,80,12,1,1,4));
		//manager.addWIB(new WorkloadIntensityBehavior(manager, new Date(System.currentTimeMillis()), 1L, TimeUnit.SECONDS,96,3,2,1,80,48,2,1,4));
		try {
			Thread.sleep(2000); // 2 second 
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//manager.addWIB(new WorkloadIntensityBehavior(manager, new Date(System.currentTimeMillis()), 3L, TimeUnit.SECONDS,24,5,1,1,80,12,1,1,4));
		
		try {
			Thread.sleep(3600000); // 60 minutes 
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//shutdown gracefully
		Iterator<IWorkloadIntensityBehavior> iterator = manager.getWorkloads().iterator();
		while(iterator.hasNext()){
			 IWorkloadIntensityBehavior wl = iterator.next();
			 manager.removeWIB(wl.getID());
		}
	}
}
