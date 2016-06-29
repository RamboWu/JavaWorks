package com.ygkj;

import com.ygkj.algroithm.AlgorithmArgs;
import com.ygkj.algroithm.AlgorithmDump;
import com.ygkj.algroithm.AlgorithmVersion;
import com.ygkj.city.CityMeta;
import com.ygkj.city.CityScan;
import com.ygkj.config.Configuration;
import com.ygkj.config.ResultConfig;
import com.ygkj.db.DBConnectionPool;
import com.ygkj.email.CustomEmail;
import com.ygkj.email.RoutesCheckerEMail;
import com.ygkj.export.RedisExporter;
import com.ygkj.script.SummaryReport;
import gui.ava.html.image.generator.HtmlImageGenerator;
import org.apache.log4j.Logger;

import javax.mail.MessagingException;
import java.io.*;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * Created by Yang on 2015/5/27.
 */
public class Runner {

	private Logger logger=Logger.getLogger("System");
	private DBConnectionPool dbConnectionPool;

	public Runner(){
		try {
			dbConnectionPool=DBConnectionPool.getInstance();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void modifyAlgorithmInfo(CityMeta cityMeta) {
		AlgorithmDump algorithmDump=new AlgorithmDump(cityMeta.cityDir);
		AlgorithmArgs algorithmArgs=new AlgorithmArgs(cityMeta.cityDir);
		AlgorithmVersion algorithmVersion=new AlgorithmVersion(cityMeta.cityDir);

		try {
			Connection conn=dbConnectionPool.getConnection();
			CallableStatement cst=conn.prepareCall("{call UpdateAlgorithmInfo(?,?,?,?,?,?,?,?,?,?,?)}");
			cst.setString("_cityLabel",cityMeta.cityLabel);
			cst.setString("_cityName",cityMeta.cityName);
			cst.setString("_exeLastModity",algorithmVersion.getLastUpdateTime());
			cst.setString("_version",algorithmVersion.getVersion());
			cst.setString("_args",algorithmArgs.getArgs().toString());
			cst.setString("_recv_port",algorithmArgs.recv_port);
			cst.setString("_send_address",algorithmArgs.send_ip+":"+algorithmArgs.send_port);
			cst.setString("_zookeeper_on", algorithmArgs.zookeeper_on);
			cst.setString("_use_origin_lineId",algorithmArgs.use_origin_lineId);
			cst.setInt("_dumpCounter",algorithmDump.getDumpCounter());
			cst.registerOutParameter("_res", Types.INTEGER);
			cst.execute();
			cst.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void processRun(String date) throws MessagingException {

		SummaryReport summaryReport=new SummaryReport();
		long startTime=System.currentTimeMillis();

		logger.info("Date:"+Configuration.date);

		String[] cityLabels=Configuration.cityLabels.split(Pattern.quote(";"));
		String[] cityNames=Configuration.cityNames.split(Pattern.quote(";"));
		String[] input=Configuration.inputFile.split(Pattern.quote(";"));

		logger.info("city labels: "+Arrays.toString(cityLabels));
		logger.info("city names: "+Arrays.toString(cityNames));
		logger.info("input dirs: "+Arrays.toString(input));

		if (cityLabels.length!=cityNames.length) {
			logger.error("cityLables.length != cityNames.length");
			return;
		}

		CityScan scan=new CityScan();

		for (int i=0;i<input.length;i++){
			scan.addFile(input[i]+"output\\","matching.log."+Configuration.date,input[i],cityLabels[i],cityNames[i]);
		}

		while (scan.hasFile()) {
			CityMeta cityMeta = scan.getExistFile();
			if (cityMeta != null) {

				logger.info(cityMeta.cityName + " START");

				if (Configuration.isDebug==0) modifyAlgorithmInfo(cityMeta);

				Configuration.cityDir = Configuration.citySymbol + "/" + cityMeta.cityLabel + "/";
				File file = new File(Configuration.cityDir);
				if (!file.exists()) file.mkdirs();

				Configuration.mkDirs();

				Configuration.lineDataFile = cityMeta.cityDir + "zookeeper\\zk_s_json.csv";
				Configuration.stationMileageFile = cityMeta.cityDir + "temp\\station_dis.csv";

				Runtime runtime = Runtime.getRuntime();
				try {
					logger.info(cityMeta.cityName + " AlgorithmAnalyse START");
					String[] cmdArg = new String[5];
					cmdArg[0] = "AlgorithmAnalyse.exe";
					cmdArg[1] = "-i=" + cityMeta.fileString;
					cmdArg[2] = "-t=" + Configuration.tempDir;
					cmdArg[3] = "-l=" + Configuration.lineDataFile;
					cmdArg[4] = "-s=" + Configuration.stationMileageFile;
					for (int t = 0; t < cmdArg.length; t++) {
						System.out.print(cmdArg[t] + " ");
					}
					System.out.println();
					Process process = runtime.exec(cmdArg);
					process.waitFor();
					logger.info(cityMeta.cityName + " AlgorithmAnalyse END");

					logger.info(cityMeta.cityName + " BusLineRelation START");
					cmdArg = new String[5];
					cmdArg[0] = "BusLineRelation.exe";
					cmdArg[1] = "-l=" + Configuration.lineDataFile;
					cmdArg[2] = "-i=" + Configuration.getFileInTemp(ResultConfig.mergerResult);
					cmdArg[3] = "-a=" + Configuration.getFileInTemp(ResultConfig.answerFile);
					cmdArg[4] = "-o=" + Configuration.tempDir;
					for (int t = 0; t < cmdArg.length; t++) {
						System.out.print(cmdArg[t] + " ");
					}
					System.out.println();
					process = runtime.exec(cmdArg);
					process.waitFor();
					logger.info(cityMeta.cityName + " BusLineRelation END");

					logger.info(cityMeta.cityName + " BusMatchingResultGenerator START");
					cmdArg = new String[5];
					cmdArg[0] = "BusMatchingResultGenerator.exe";
					cmdArg[1] = "-l=" + Configuration.lineDataFile;
					cmdArg[2] = "-i=" + Configuration.getFileInTemp(ResultConfig.mergerResult);
					cmdArg[3] = "-b=" + Configuration.getFileInTemp(ResultConfig.singleMatchingReport);
					cmdArg[4] = "-o=" + Configuration.getFileInTemp(ResultConfig.answerFile);
					for (int t = 0; t < cmdArg.length; t++) {
						System.out.print(cmdArg[t] + " ");
					}
					System.out.println();
					process = runtime.exec(cmdArg);
					process.waitFor();
					logger.info(cityMeta.cityName + " BusMatchingResultGenerator END");

//					logger.info(cityMeta.cityName + " line_err_segment_warn START");
//					cmdArg = new String[7];
//					cmdArg[0] = "java";
//					cmdArg[1] = "-jar";
//					cmdArg[2] = "line_err_segment_warn.jar";
//					cmdArg[3] = "Path.Record=" + Configuration.getFileInTemp(ResultConfig.answerFile);
//					cmdArg[4] = "Path.Base=" + Configuration.lineDataFile;
//					cmdArg[5] = "Path.BusLine=" + Configuration.getFileInTemp(ResultConfig.singleMatchingReport);
//					cmdArg[6] = "Path.ReportDir=" + Configuration.tempDir;
//					process = runtime.exec(cmdArg);
//					process.waitFor();
//					logger.info(cityMeta.cityName + " line_err_segment_warn END");

//					logger.info(cityMeta.cityName + " line_match_inspect START");
//					cmdArg = new String[7];
//					cmdArg[0] = "java";
//					cmdArg[1] = "-jar";
//					cmdArg[2] = "line_match_inspect.jar";
//					cmdArg[3] = "Path.Match=" + Configuration.getFileInTemp(ResultConfig.busMatchingFile);
//					cmdArg[4] = "Path.Single=" + Configuration.getFileInTemp(ResultConfig.singleMatchingReport);
//					cmdArg[5] = "Path.Report.Dir=" + Configuration.tempDir;
//					cmdArg[6] = "Path.Base=" + Configuration.lineDataFile;
//					process = runtime.exec(cmdArg);
//					process.waitFor();
//					logger.info(cityMeta.cityName + " line_match_inspect END");

//					logger.info(cityMeta.cityName + " line_speed START");
//					cmdArg = new String[8];
//					cmdArg[0] = "java";
//					cmdArg[1] = "-jar";
//					cmdArg[2] = "line_speed.jar";
//					cmdArg[3] = "Path.Record=" + Configuration.getFileInTemp(ResultConfig.answerFile);
//					cmdArg[4] = "Path.Base=" + Configuration.lineDataFile;
//					cmdArg[5] = "Path.Single=" + Configuration.getFileInTemp(ResultConfig.singleMatchingReport);
//					cmdArg[6] = "City.Pinyin=" + cityMeta.cityLabel.toLowerCase();
//					cmdArg[7] = "Date=" + Configuration.date;
//					process = runtime.exec(cmdArg);
//					process.waitFor();
//					logger.info(cityMeta.cityName + " line_speed END");

//					logger.info(cityMeta.cityName + " stop_err_warn START");
//					cmdArg = new String[7];
//					cmdArg[0] = "java";
//					cmdArg[1] = "-jar";
//					cmdArg[2] = "stop_err_warn_leijp.jar";
//					cmdArg[3] = "Path.Record=" + Configuration.getFileInTemp(ResultConfig.answerFile);
//					cmdArg[4] = "Path.Base=" + Configuration.lineDataFile;
//					cmdArg[5] = "Path.Single=" + Configuration.getFileInTemp(ResultConfig.singleMatchingReport);
//					cmdArg[6] = "Path.ReportDir=" + Configuration.tempDir;
//					process = runtime.exec(cmdArg);
//					process.waitFor();
//					logger.info(cityMeta.cityName + " stop_err_warn END");

				} catch (Exception e) {
					e.printStackTrace();
				}
				ResultControl resultControl = new ResultControl(summaryReport, Configuration.date);
				resultControl.run(cityMeta.cityName, cityMeta.cityLabel);

				if (Configuration.isClear == 0) Configuration.clearDirs();
			} else {
				logger.warn("there is no input file, wait for 1 min, then retry");

				if (System.currentTimeMillis() - startTime > 12 * 60 * 60 * 1000) break;
				try {
					Thread.sleep(60 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}


		HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
		imageGenerator.loadHtml(summaryReport.toHtml());
		imageGenerator.saveAsImage(Configuration.tempDir+"summaryReport.png");

//		String[] addressTo=null;
//		String[] addressCc=null;
//		if (Configuration.eMailTo!=null) addressTo=Configuration.eMailTo.split(Pattern.quote(";"));
//		if (Configuration.summaryEMailCc !=null) addressCc=Configuration.summaryEMailCc.split(Pattern.quote(";"));
//		CustomEmail email=new CustomEmail("汇总报告"+Configuration.date,addressTo,addressCc);
//		email.addImage("汇总表格",Configuration.tempDir+"summaryReport.png");
//		email.send();

		long endTime=System.currentTimeMillis();
		System.out.println("Use Time:"+(endTime-startTime)/1000);
	}

	public void run() throws MessagingException {

		if (Configuration.date==null){
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
			Calendar date=Calendar.getInstance();
			date.add(Calendar.DATE,-1);
			Configuration.date=dateFormat.format(date.getTime());
		}

		processRun(Configuration.date);

	}
}
