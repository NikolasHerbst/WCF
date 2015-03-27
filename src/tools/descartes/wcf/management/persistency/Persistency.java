/*******************************************************************************
* Copyright (c) 2014 Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf.management.persistency;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import tools.descartes.wcf.forecasting.IForecastResult;
import tools.descartes.wcf.management.timeSeries.ITimeSeries;
import tools.descartes.wcf.management.timeSeries.ITimeSeriesPoint;
import tools.descartes.wcf.management.timeSeries.TimeSeries;
import tools.descartes.wcf.management.wibManagement.IWorkloadIntensityBehavior;

public class Persistency implements IPersistency {
	
	@Override
	public void initWIB(IWorkloadIntensityBehavior wl) {
		String path = "data/wl-"+wl.getID()+".csv"; 
		// before open the file check to see if it already exists
		
		if(!new File(path).exists()){
		
			try {
			CsvWriter writer = new CsvWriter(new FileWriter(path, true),';');
			writer.setRecordDelimiter('\n');
				
			String[] record = {	"workload_ID",
							 	"ts_starttime",
							 	"ts_deltatime",
							 	"ts_deltatimeunit",
							 	"ts_endtime",
							 	"ts_size",
							 	"ts_frequency",
							 	"ts_maxPeriods",
							 	"ts_skippedValues",
							 	"classification_period",
							 	"classification_strategy",
							 	"selected_forecast_strategy1",
							 	"observed_fc_quality1",
							 	"selected_forecast_strategy2",
							 	"observed_fc_quality2",
							 	"recent_forecast_horizon",
							 	"max_overhead",
								"forecast_period"};
			writer.writeRecord(record);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
	}
	
	@Override
	public boolean writeWIB(IWorkloadIntensityBehavior wl) {
		String path = "data/wl-"+wl.getID()+".csv"; 
		if(new File(path).exists()){

			String[] values = new String[18];			
			values[0] = Integer.toString(wl.getID());
		
			values[1] = Long.toString(wl.getTimeSeries().getStartTime().getTime());
			values[2] = Long.toString(wl.getTimeSeries().getDeltaTime());
			values[3] = wl.getTimeSeries().getDeltaTimeUnit().name();
			values[4] = Long.toString(wl.getTimeSeries().getEndTime().getTime());
			values[5] = Integer.toString(wl.getTimeSeries().size());
		
			values[6] = Integer.toString(wl.getTimeSeries().getFrequency());
			values[7] = Integer.toString(wl.getTimeSeries().getMaxPeriods());
			values[8] = Long.toString(wl.getTimeSeries().getSkippedValues());
			
			values[9] = Integer.toString(wl.getClassSetting().getPeriod());
			values[10] = wl.getClassSetting().getClassificationStrategy().name();
			
			DecimalFormat g = new DecimalFormat("#0.0000");
			values[11] = wl.getClassSetting().getRecentStrategy1().name();
			if (wl.getMASEMetric()!=null && wl.getMASEMetric()[0]!=Integer.MAX_VALUE){values[12] = g.format(wl.getMASEMetric()[0]);}
			values[13] = wl.getClassSetting().getRecentStrategy2().name();
			if (wl.getMASEMetric()!=null && wl.getMASEMetric()[1]!=Integer.MAX_VALUE){values[14] = g.format(wl.getMASEMetric()[1]);}
			
			values[15] 	= Integer.toString(wl.getForecastObjectives().getRecentHorizon());
			values[16] 	= Integer.toString(wl.getForecastObjectives().getOverhead());
			values[17] 	= Integer.toString(wl.getForecastObjectives().getPeriod());
		
			try {			
				CsvWriter writer = new CsvWriter(new FileWriter(path, true),';');
				writer.setRecordDelimiter('\n');
				writer.writeRecord(values,true);
				writer.flush();
				writer.close();
				return true;
			} catch (IOException e) {
				return false;
			}
		} else {
			initWIB(wl);
			return writeWIB(wl);
		}
	}
	
	@Override
	public boolean writeForecastResult(IForecastResult fr, IWorkloadIntensityBehavior wl, int count) {
		String path = "data/fc-"+wl.getID()+".csv"; 		
		boolean alreadyExists = new File(path).exists();
		try {
			CsvWriter writer = new CsvWriter(new FileWriter(path, true),';');
			writer.setRecordDelimiter('\n');
			
			if(!alreadyExists){
					String[] values = {	"#forecast",
										"fc_strategy",
										"time",
										"forecast_step",
										"forecasted_value",
										"mean_absolute_scaled_error",
										"lower_confidence",
										"upper_confidence",
										"forecast_duration"};
					writer.writeRecord(values);
			}

		String[] values = new String[9];
		
		List<ITimeSeriesPoint<Double>> tsListFC = fr.getForecast().getPoints();
		List<ITimeSeriesPoint<Double>> tsListLower = fr.getLower().getPoints();
		List<ITimeSeriesPoint<Double>> tsListUpper = fr.getUpper().getPoints();
		
		ListIterator<ITimeSeriesPoint<Double>> iteratorFC = tsListFC.listIterator();
		ListIterator<ITimeSeriesPoint<Double>> iteratorLower = tsListLower.listIterator();
		ListIterator<ITimeSeriesPoint<Double>> iteratorUpper = tsListUpper.listIterator();
		DecimalFormat f = new DecimalFormat("#0.00");
		DecimalFormat g = new DecimalFormat("#0.0000");
		ITimeSeriesPoint<Double> tsp; 
		int i = 1;
		while(iteratorFC.hasNext() & iteratorLower.hasNext() & iteratorUpper.hasNext()){
			tsp = iteratorFC.next();
			values[0] = Integer.toString(count);
			values[1] = fr.getFcStrategy().name();
			values[2] =	Long.toString(tsp.getTime().getTime());
			values[3] = Integer.toString(i);
			values[4] = f.format(tsp.getValue());
			values[5] = g.format(fr.getMeanAbsoluteScaledError());
			values[6] = f.format(iteratorLower.next().getValue());
			values[7] = f.format(iteratorUpper.next().getValue());
			values[8] = Long.toString(fr.getDuration());
			i++;
			writer.writeRecord(values);
		}
			writer.flush();
			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;			
		}
	}

	@Override
	public ITimeSeries<Double> readNewTimeSeriesValues(IWorkloadIntensityBehavior wl) {
		
		/* If file does not exist return original TS
		 */
		String path = "data/ts-"+wl.getID()+".csv"; 
		if(!new File(path).exists()){
			System.out.println("No TS values for workload " +wl.getID());
			return wl.getTimeSeries();
		}
		/* Calculate number of records to read from start_time to now
		 * 
		 */
		long numberOfRecords = calculateRecords(wl);
		/* get recent ts_size - amount of already read in values to skip this time
		 */
		long tsSize = wl.getTimeSeries().size()+ wl.getTimeSeries().getSkippedValues();	
		/* Don't to anything if there are no new values available yet  
		 */
		if(numberOfRecords<=tsSize){
			return wl.getTimeSeries();
		}
		/* Only append values, that are not already in the TS 
		 */		
		ITimeSeries<Double> ts = wl.getTimeSeries();
		try {
			CsvReader reader = new CsvReader(path);
			reader.setDelimiter(';');
			reader.setRecordDelimiter('\n');
			while (reader.readRecord() && numberOfRecords>0){
				numberOfRecords--; 
				tsSize--;
				/* Skip the first #tsSize values that are already read in
			 */
				if(tsSize<0){
					ts.append(Double.parseDouble(reader.get(0)));
				}
			}
			reader.close();
			return ts;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return ts;
		} catch (IOException e) {
			e.printStackTrace();
			return ts;
		}
	}

	private long calculateRecords(IWorkloadIntensityBehavior wl){
		String path = "data/tsinfo-"+wl.getID()+".csv"; 
		if(!new File(path).exists()){
			System.out.println("No WL File for this ID");
			return 0;
		} 
		try {	
	
			CsvReader reader = new CsvReader(path);
			reader.setDelimiter(';');
			reader.setRecordDelimiter('\n');				
			reader.readHeaders();
			long records = 0;
			String[] set = null;
			while(reader.readRecord()){		//read up to the last line
				set = reader.getValues();
			}
			if(set!=null){
				long start_time = Long.parseLong(set[0]); 						//start_time
				long delta_time = Long.parseLong(set[1]); 						//delta_time
				TimeUnit delta_time_unit = TimeUnit.valueOf(set[2]); 			//delta_time_unit
				long now = System.currentTimeMillis();							//now
				long delta_time_ms = delta_time_unit.toMillis(delta_time);
			
				long time_diff = now-start_time;
				records = (long)Math.floor(time_diff/delta_time_ms);
			}	
			reader.close();
			records += wl.getTimeSeries().getSkippedValues();
			return records;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public ITimeSeries<Double> readTimeSeriesConf(IWorkloadIntensityBehavior wl){
		if(wl.getTimeSeries()==null){
			String path = "data/tsinfo-"+wl.getID()+".csv"; 
			if(!new File(path).exists()){
				System.out.println("No WL File for this ID");
				return null;
			} 
			try {	
				CsvReader reader = new CsvReader(path);
				reader.setDelimiter(';');
				reader.setRecordDelimiter('\n');				
				reader.readHeaders();
				ITimeSeries<Double> ts = null;
				String[] set = null;
				while(reader.readRecord()){
					set = reader.getValues();
				}
				if(set!=null){
					Date start_time = new Date(Long.parseLong(set[0])); 	//start_time
					long delta_time = Long.parseLong(set[1]); 				//delta_time
					TimeUnit delta_time_unit = TimeUnit.valueOf(set[2]); 	//delta_time_unit
					int frequency = Integer.parseInt(set[5]);
					int maxPeriods = Integer.parseInt(set[6]);
					long skippedValues = Long.parseLong(set[7]);
					ts = new TimeSeries<Double>(start_time,delta_time,delta_time_unit,frequency,maxPeriods,skippedValues);
				}
				reader.close();
				return ts;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return wl.getTimeSeries();
		}
	}
	
	public void writeTimeSeriesConf(IWorkloadIntensityBehavior wl){
		ITimeSeries<Double> ts = wl.getTimeSeries();
		String path = "data/tsinfo-"+wl.getID()+".csv"; 			
		boolean alreadyExists = new File(path).exists();
		try {			
			
			CsvWriter writer = new CsvWriter(new FileWriter(path, true),';');
			writer.setRecordDelimiter('\n');
			if(!alreadyExists){
				String[] record = {	"ts_starttime",
									"ts_deltatime",
									"ts_deltatimeunit",
									"ts_endtime",
									"ts_size",
									"ts_frequency",
									"ts_maxPeriods",
									"ts_skippedValues"};
				writer.writeRecord(record);
			}
			
			String[] record2 = {	Long.toString(ts.getStartTime().getTime()),
									Long.toString(ts.getDeltaTime()),
									ts.getDeltaTimeUnit().name(),
									Long.toString(ts.getEndTime().getTime()),
									Integer.toString(ts.size()),
									Integer.toString(ts.getFrequency()),
									Integer.toString(ts.getMaxPeriods()),
									Long.toString(ts.getSkippedValues())};
			writer.writeRecord(record2);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
