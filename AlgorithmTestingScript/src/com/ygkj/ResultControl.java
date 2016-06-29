package com.ygkj;

import com.ygkj.city.CityMeta;
import com.ygkj.config.Configuration;
import com.ygkj.config.ResultConfig;
import com.ygkj.email.RoutesCheckerEMail;
import com.ygkj.export.DBExporter;
import com.ygkj.export.RedisExporter;
import com.ygkj.script.AnalyseReport;
import com.ygkj.script.SummaryReport;
import org.apache.log4j.Logger;

import java.util.regex.Pattern;

/**
 * Created by Yang on 2015/4/10.
 */
public class ResultControl {

	private Logger logger=Logger.getLogger("System");

	String date=null;
	SummaryReport summaryReport;

	public ResultControl(SummaryReport summaryReport){
		this.summaryReport=summaryReport;
	}

	public ResultControl(SummaryReport summaryReport,String date){
		this.summaryReport=summaryReport;
		this.date=date;
	}

	public void run(String cityName, String cityLabel){
		AnalyseReport report=new AnalyseReport(cityName,Configuration.getFileInTemp(ResultConfig.analyseReport));
		report.genImage();
		summaryReport.addReport(report);

		String[] addressTo=null;
		String[] addressCc=null;
		try{
			if (Configuration.eMailTo!=null) addressTo=Configuration.eMailTo.split(Pattern.quote(";"));
			if (Configuration.cityEmailCc !=null) addressCc=Configuration.cityEmailCc.split(Pattern.quote(";"));
//			CustomEmail email=new CustomEmail(cityMeta.cityName+"检查报告"+Configuration.date,addressTo,addressCc);
//			email.addImage("数据质量分布图",Configuration.tempDir+"GPSDataDistribution.jpg");
//			email.addImage("车辆识别分布图",Configuration.tempDir+"BusDistribution.jpg");
//			email.addImage("确信车GPS点判断分布图",Configuration.tempDir+"GPSDataDistributionThatDetermining.jpg");
//			email.addImage("报站分布图",Configuration.tempDir+"StnDistribution.jpg");
//			email.addImage("线路识别分布图",Configuration.tempDir+"LineDistribution.jpg");
//			email.send();`
		}catch (Exception e){
			logger.error("send stat email error", e);
		}

//		try{
//			String[] routesCheckerCc=null;
//			if (Configuration.routesCheckerEMailCc!=null) routesCheckerCc=Configuration.routesCheckerEMailCc.split(Pattern.quote(";"));
//			RoutesCheckerEMail routesCheckerEMail=new RoutesCheckerEMail(cityName);
//			routesCheckerEMail.genEmail(cityName + "基础数据检查" + Configuration.date, addressTo, routesCheckerCc);
//			routesCheckerEMail.send();
//		}catch (Exception e){
//			logger.error("send route check email error", e);
//		}
//
//		try {
//			DBExporter.export(cityLabel);
//		} catch (Exception e) {
//			logger.error("export to db error", e);
//		}
//
		try {
			RedisExporter.export(cityLabel.toLowerCase(), Configuration.date);
		} catch (Exception e) {
			logger.error("export to redis error", e);
		}

		if (this.date!=null) {
			report.saveInCityDir(this.date);
			logger.info(cityName+" backup");
		}
	}
}
