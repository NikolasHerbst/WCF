# WCF
Workload Classification & Forecasting

Disclaimer:
Please be aware that WCF is a research prototype not perfectly shaped to be released, yet. 
Nevertheless, I will be happy if the sources help or can partially be reused. 

DOCU:
As documentation to the code, please refer to the respective chapter 
in my diploma thesis to be found at http://descartes.tools/wcf 

SCOPE:
The WCF approach is designed to be applied to time series data 
that contain the amount of request/user/session arrivals per time unit to a system 
(exhibiting seasonal patterns/trends and are not dominated by noise). 
It’s capability to predict CPU usage or other metrics could be limited. 
Furthermore, WCF does not answers the questions, when to start or release a resource directly. 
It just predicts the amount of arriving work units. 
The later is addressed in the DML approach of our group: http://descartes.tools/dml 
and the corresponding bench http://descartes.tools/dml_bench.

SETUP:
The current implementation of WCF uses simple csv data as input and reads new values periodically from the file. 
Technically, an adapter to a monitoring data provider needs to be implemented 
if WCF is applied in a real cloud scenario (i.e. the Data Persistency Interface). 
If you plan to use WCF in an offline scenario/simulation or testing, 
the artificial waiting for new values can be deactivated in the code (WIB Manager Thread run())

The project folder ‘\data’ contains an exemplary time series of request arrivals per hour (ts-1.csv) (source Wikipedia Germany) and in the main.java a corresponding WIB object is created. 
If you want to run more WIBs in parallel, just name the second input file ts-2.csv and so on, 
as the IDs are incremented when a new WIB is created/added to the manager. 
You will find the results again in the ‘\data’ folder.

Configuration parameters (e.g. frequency of data points, forecasting and classification executions, 
units, horizon_max/min, overhead_group, confidence) are documented both in JavaDoc and in the thesis. 
Please feel free to ask when any questions arise.

You need to run an instance of R (http://cran.r-project.org/ ) with installed rServer and forecasting packages 
(rServer needs to be loaded and started), as WCF just calls the forecasters provided in R. 
The rServerBridgeControl.java contains configuration parameters IP and Port, 
currently set to localhost and DefaultPort 6311. (PW and User only if set at the rServer). 
If the rServer runs at a remote machine, it is required to allow remote request explicitly in a configuration file.
