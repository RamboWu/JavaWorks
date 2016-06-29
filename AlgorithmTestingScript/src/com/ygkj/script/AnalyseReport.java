package com.ygkj.script;

import com.ygkj.Tools;
import com.ygkj.chart.PieChart;
import com.ygkj.config.Configuration;
import com.ygkj.config.ResultConfig;

import java.io.*;
import java.util.*;

/**
 * Created by Yang on 2015/3/30.
 */
public class AnalyseReport {

	String cityName;

	long data_total = 0;
	long matching_data_total = 0;
	long time_error_data_total = 0;
	long gps_error_data_total= 0;
	long un_matching_data_total = 0;
	long errorBus_data_total = 0;

	long discerning_data_total = 0;
	long discerning_matching_data_total = 0;
	long unMatching_data_total_of_discerning=0;
	long unMatching_caused_by_gpsError_of_discerning= 0;
	long unMatching_caused_by_TimeError_of_discerning = 0;

	int busTotal=0;
	int discerningBus=0;
	int gpsErrorBus=0;
	int matchingLineTotal=0;
	int unMatchingLineTotal=0;
	int matchingCircleLineTotal=0;
	int unMovingBus=0;

	long stnTotal=0;
	long missingStn=0;
	long gpsMissingSTN =0;

	public AnalyseReport(String cityName,String file) {
		this.cityName=cityName;
		load(file);
	}

	private void load(String file){
		Properties properties=new Properties();
		try {
			properties.load(new FileInputStream(file));
		} catch (IOException e) {
			e.printStackTrace();
		}

		data_total=Long.parseLong(properties.getProperty("DataTotal","0"));
		matching_data_total=Long.parseLong(properties.getProperty("MatchingDataTotal","0"));
		un_matching_data_total=Long.parseLong(properties.getProperty("UnMatchingDataTotal","0"));
		gps_error_data_total=Long.parseLong(properties.getProperty("GPSErrorDataTotal","0"));
		time_error_data_total=Long.parseLong(properties.getProperty("TimeErrorDataTotal","0"));
		errorBus_data_total=Long.parseLong(properties.getProperty("ErrorBusDataTotal","0"));

		discerning_data_total=Long.parseLong(properties.getProperty("BusDataTotalOfDiscerning","0"));
		discerning_matching_data_total=Long.parseLong(properties.getProperty("MatchingDataTotalOfDiscerning","0"));
		unMatching_data_total_of_discerning=Long.parseLong(properties.getProperty("UnMatchingDataTotalOfDiscerning","0"));
		unMatching_caused_by_gpsError_of_discerning=Long.parseLong(properties.getProperty("UnMatchingCausedByGPSErrorOfDiscerning","0"));
		unMatching_caused_by_TimeError_of_discerning=Long.parseLong(properties.getProperty("UnMatchingCausedByTimeErrorOfDiscerning","0"));

		busTotal=Integer.parseInt(properties.getProperty("BusTotal","0"));
		discerningBus=Integer.parseInt(properties.getProperty("DiscerningBus","0"));
		gpsErrorBus=Integer.parseInt(properties.getProperty("GPSErrorBus","0"));
		unMovingBus=Integer.parseInt(properties.getProperty("UnMovingBus","0"));
		matchingLineTotal=Integer.parseInt(properties.getProperty("MatchingLineTotal","0"));
		matchingCircleLineTotal=Integer.parseInt(properties.getProperty("MatchingCircleLineTotal","0"));
		unMatchingLineTotal=Integer.parseInt(properties.getProperty("UnMatchingLineTotal","0"));
		stnTotal=Long.parseLong(properties.getProperty("StnTotal","0"));
		missingStn=Long.parseLong(properties.getProperty("MissingStn","0"));
		gpsMissingSTN =Long.parseLong(properties.getProperty("GPSMissing","0"));
	}

	public void genImage(){
		PieChart pieChart=new PieChart("车辆识别分布图");
		pieChart.addData("确信识别车辆",discerningBus);
		pieChart.addData("GPS坐标错误车辆",gpsErrorBus);
		pieChart.addData("未移动车辆",unMovingBus);
		pieChart.addData("无法识别车辆",busTotal-discerningBus-unMovingBus-gpsErrorBus);
		pieChart.toImage(Configuration.tempDir+"BusDistribution.jpg");

		pieChart=new PieChart("数据质量分布图");
		pieChart.addData("判断数据点数",matching_data_total);
		pieChart.addData("未判断数据点数",un_matching_data_total-gps_error_data_total-time_error_data_total);
		pieChart.addData("GPS错误点数",gps_error_data_total);
		pieChart.addData("时间错误点数",time_error_data_total);
		pieChart.addData("异常车数据点数",errorBus_data_total);
		pieChart.toImage(Configuration.tempDir+"GPSDataDistribution.jpg");

		pieChart=new PieChart("确信车GPS点判断分布图");
		pieChart.addData("做出正确判断点数",discerning_matching_data_total);
		pieChart.addData("时间错误点数",unMatching_caused_by_TimeError_of_discerning);
		pieChart.addData("GPS错误点数",unMatching_caused_by_gpsError_of_discerning);
		pieChart.addData("剩余未判断点数",unMatching_data_total_of_discerning-unMatching_caused_by_gpsError_of_discerning-unMatching_caused_by_TimeError_of_discerning);
		pieChart.toImage(Configuration.tempDir+"GPSDataDistributionThatDetermining.jpg");

		pieChart=new PieChart("线路识别分布图");
		pieChart.addData("识别的车所在的线路",matchingLineTotal);
		pieChart.addData("没有车的线路",unMatchingLineTotal);
		pieChart.toImage(Configuration.tempDir+"LineDistribution.jpg");

		pieChart=new PieChart("报站丢失分布图");
		pieChart.addData("算法报站数",stnTotal-missingStn);
		pieChart.addData("GPS丢失导致丢失的报站数", gpsMissingSTN);
		pieChart.addData("算法丢失的报站数",missingStn- gpsMissingSTN);
		pieChart.toImage(Configuration.tempDir+"StnDistribution.jpg");
	}

	public void saveInCityDir(String date) {
		File file=new File(Configuration.cityDir+date);
		if (!file.exists()) file.mkdirs();

		Tools.copyFile(Configuration.getFileInTemp(ResultConfig.errorDiscern),Configuration.cityDir+date+"/"+ResultConfig.errorDiscern);
		Tools.copyFile(Configuration.getFileInTemp(ResultConfig.singleMatchingReport),Configuration.cityDir+date+"/"+ResultConfig.singleMatchingReport);
		Tools.copyFile(Configuration.getFileInTemp(ResultConfig.multiMatchingReport),Configuration.cityDir+date+"/"+ResultConfig.multiMatchingReport);
		Tools.copyFile(Configuration.getFileInTemp(ResultConfig.maybeMatchingReport),Configuration.cityDir+date+"/"+ResultConfig.maybeMatchingReport);
		Tools.copyFile(Configuration.getFileInTemp(ResultConfig.leakageBus),Configuration.cityDir+date+"/"+ResultConfig.leakageBus);
		Tools.copyFile(Configuration.getFileInTemp(ResultConfig.unDiscernLine),Configuration.cityDir+date+"/"+ResultConfig.unDiscernLine);
		Tools.copyFile(Configuration.getFileInTemp(ResultConfig.dirCounter),Configuration.cityDir+date+"/"+ResultConfig.dirCounter);
		Tools.copyFile(Configuration.getFileInTemp(ResultConfig.testingReport),Configuration.cityDir+date+"/"+ResultConfig.testingReport);
		Tools.copyFile(Configuration.getFileInTemp(ResultConfig.analyseReport),Configuration.cityDir+date+"/"+ResultConfig.analyseReport);
		Tools.copyFile(Configuration.getFileInTemp(ResultConfig.suspiciousMatchingReport),Configuration.cityDir+date+"/"+ResultConfig.suspiciousMatchingReport);
		Tools.copyFile(Configuration.getFileInTemp(ResultConfig.originalMatchingReport),Configuration.cityDir+date+"/"+ResultConfig.originalMatchingReport);
		Tools.copyFile(Configuration.getFileInTemp(ResultConfig.timeAnalyseReport),Configuration.cityDir+date+"/"+ResultConfig.timeAnalyseReport);
		Tools.copyFile(Configuration.getFileInTemp(ResultConfig.lineAnalyseReport),Configuration.cityDir+date+"/"+ResultConfig.lineAnalyseReport);
		Tools.copyFile(Configuration.getFileInTemp(ResultConfig.busAnalyseReport),Configuration.cityDir+date+"/"+ResultConfig.busAnalyseReport);
//		Tools.copyFile(Configuration.getFileInTemp(ResultConfig.routesErrorPages),Configuration.cityDir+date+"/"+ResultConfig.routesErrorPages);
		Tools.copyFile(Configuration.getFileInTemp(ResultConfig.SimilarLineReport),Configuration.cityDir+date+"/"+ResultConfig.SimilarLineReport);
//		Tools.copyFile(Configuration.getFileInTemp(ResultConfig.routesCheckerResult),Configuration.cityDir+date+"/"+ResultConfig.routesCheckerResult);
		Tools.copyFile(Configuration.getFileInTemp(ResultConfig.busMatchingFile),Configuration.cityDir+date+"/"+ResultConfig.busMatchingFile);
		Tools.copyFile(Configuration.getFileInTemp(ResultConfig.answerFile),Configuration.cityDir+date+"/"+ResultConfig.answerFile);
//		Tools.copyFile(Configuration.getFileInTemp(ResultConfig.stopsReport), Configuration.cityDir+date+"/"+ResultConfig.stopsReport);
		Tools.copyFile(Configuration.lineDataFile,Configuration.cityDir+date+"/s_json.csv");
	}

}
