/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf.managementTest.persistencyTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.util.concurrent.TimeUnit;



import org.junit.Test;

import tools.descartes.wcf.management.persistency.CsvReader;
import tools.descartes.wcf.management.persistency.IPersistency;
import tools.descartes.wcf.management.persistency.Persistency;
import tools.descartes.wcf.management.wibManagement.IWorkloadIntensityBehavior;
import tools.descartes.wcf.management.wibManagement.Manager;
import tools.descartes.wcf.management.wibManagement.WorkloadIntensityBehavior;



public class PersistencyTest {
	
	private Manager manager = Manager.getInstance();
	private IWorkloadIntensityBehavior wl = new WorkloadIntensityBehavior(manager, new Date(System.currentTimeMillis()), 2L, TimeUnit.SECONDS,96,5,2,2,80,32,4,2,3);
	private IPersistency persist = new Persistency();

	@Test
	public void initWIBTest() {
		persist.initWIB(wl);
		String path = "data/wl-"+wl.getID()+".csv"; 
		if(!new File(path).exists()){
			fail("File not writen");
		}
		CsvReader reader;
		try {
			reader = new CsvReader(path);
			reader.setDelimiter(';');
			reader.setRecordDelimiter('\n');
			
			reader.readHeaders();
			
			assertEquals(reader.getHeader(0),"workload_ID");
			assertEquals(reader.getHeader(1),"ts_starttime");
			assertEquals(reader.getHeader(2),"ts_deltatime");
			assertEquals(reader.getHeader(3),"ts_deltatimeunit");
			assertEquals(reader.getHeader(4),"ts_endtime");
			assertEquals(reader.getHeader(5),"ts_size");
			assertEquals(reader.getHeader(6),"ts_frequency");
			assertEquals(reader.getHeader(7),"ts_maxPeriods");
			assertEquals(reader.getHeader(8),"ts_skippedValues");
			assertEquals(reader.getHeader(9),"classification_period");
			assertEquals(reader.getHeader(10),"classification_strategy");
			assertEquals(reader.getHeader(11),"selected_forecast_strategy1");
			assertEquals(reader.getHeader(12),"observed_fc_quality1");
			assertEquals(reader.getHeader(13),"selected_forecast_strategy2");
			assertEquals(reader.getHeader(14),"observed_fc_quality2");
			assertEquals(reader.getHeader(15),"recent_forecast_horizon");
			assertEquals(reader.getHeader(16),"max_overhead");
			assertEquals(reader.getHeader(17),"forecast_period");
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void writeWIBTest(){
		persist.writeWIB(wl);
		String path = "data/wl-"+wl.getID()+".csv"; 
		if(!new File(path).exists()){
			fail("File not writen");
		}
		CsvReader reader;
		try {
			reader = new CsvReader(path);
			reader.setDelimiter(';');
			reader.setRecordDelimiter('\n');
			
			reader.readHeaders();
			reader.readRecord();
			assert(Integer.decode(reader.get(0))==wl.getID());
			assert(Long.decode(reader.get(1))==wl.getTimeSeries().getStartTime().getTime());
			assert(Long.decode(reader.get(2))==wl.getTimeSeries().getDeltaTime());
			assertEquals(TimeUnit.valueOf(reader.get(3)),wl.getTimeSeries().getDeltaTimeUnit());
			assert(Long.decode(reader.get(4))==wl.getTimeSeries().getEndTime().getTime());
			assert(Integer.decode(reader.get(5))==wl.getTimeSeries().size());
			assert(Integer.decode(reader.get(6))==wl.getClassSetting().getPeriod());
			assert(Integer.decode(reader.get(7))==wl.getClassSetting().getClassificationStrategy().ordinal());
			assert(Integer.decode(reader.get(8))==wl.getClassSetting().getRecentStrategy1().ordinal());
			assert(Integer.decode(reader.get(9))==wl.getClassSetting().getRecentStrategy2().ordinal());
			assert(Integer.decode(reader.get(10))==wl.getForecastObjectives().getRecentHorizon());
			assert(Integer.decode(reader.get(11))==wl.getForecastObjectives().getOverhead());
			assert(Integer.decode(reader.get(12))==wl.getForecastObjectives().getPeriod());
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void readTimeSeriesTest(){
		try {
			Thread.sleep(11000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		persist.readNewTimeSeriesValues(wl);
		assert(wl.getTimeSeries().size()>0);
	}
}
