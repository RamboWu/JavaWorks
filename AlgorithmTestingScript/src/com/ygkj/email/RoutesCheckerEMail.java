package com.ygkj.email;

import com.ygkj.config.Configuration;
import com.ygkj.config.ResultConfig;
import org.apache.log4j.Logger;

import javax.mail.MessagingException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.TreeSet;

/**
 * Created by Yang on 2015/6/3.
 */
public class RoutesCheckerEMail {

	private String cityName;
	private CustomEmail email;
	private Logger logger=Logger.getLogger("System");


	public RoutesCheckerEMail(String cityName){
		this.cityName=cityName;
	}

	public void genEmail(String title, String[] address,String[] cc) throws MessagingException {

		email=new CustomEmail(title,address,cc);
		genResultCsv();
		logger.info("RoutesChecker Report Generated");
//		email.addTable("统计汇总", Configuration.getFileInTemp(Configuration.statReport), "=");
		addStat();
		email.addTable("错误汇总", Configuration.getFileInTemp(ResultConfig.routesCheckerResult), ",");
//		email.addEmailAttach(Configuration.getFileInTemp(ResultConfig.routesErrorPages));
		email.addEmailAttach(Configuration.getFileInTemp(ResultConfig.routesCheckerResult));
		email.addEmailAttach(Configuration.getFileInTemp(ResultConfig.stopsReport));
		logger.info("RoutesChecker EMail Generated");
	}

	private void addStat() {
		int warnLines = -1;
		int totLines = -1;
		double warnMiles = -1;
		double totMiles = -1;

		TreeSet<String> lines = new TreeSet<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(Configuration.getFileInTemp(ResultConfig.routesCheckerResult)), "utf-8"));

			while (true) {
				String line = reader.readLine();
				if (line == null)
					break;

				String[] fields = line.split(",");
				if (fields.length != 4)
					continue;

				String[] ls = fields[0].split(";");
				for (String l : ls) {
					if (l != null && l.trim().length() > 0)
						lines.add(l);
				}
			}

			email.addValue("预警线路(不区分上下行)", String.valueOf(lines.size() - 1)); // 第一行是title
			warnLines = lines.size() - 1;
		} catch (Exception e) {
			logger.error("add warn line stat to email error", e);
		} finally {
			if (reader != null) try { reader.close(); } catch (Exception e) {}
		}


		reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(Configuration.getFileInTemp(ResultConfig.statReport)), "utf-8"));

			while (true) {
				String line = reader.readLine();
				if (line == null)
					break;

				int i = line.indexOf('=');
				if (i < 0)
					continue;

				String key = line.substring(0, i);
				String val = line.substring(i + 1);

				if (key.equals("预警里程数(米)")) {
					email.addValue(key, val);
					warnMiles = Double.parseDouble(val);
				}
				else if (key.equals("基础数据线路总数(不区分上下行)")) {
					email.addValue(key, val);
					totLines = Integer.parseInt(val);
				}
				else if (key.equals("总里程数(米)")) {
					email.addValue(key, val);
					totMiles = Double.parseDouble(val);
				}

			}
		} catch (Exception e) {
			logger.error("add warn mileage stat to email error", e);
		} finally {
			if (reader != null) try { reader.close(); } catch (Exception e) {}
		}

		if (warnLines >= 0) sendStat("jc_001", String.valueOf(warnLines));
		if (totLines >= 0) sendStat("jc_002", String.valueOf(totLines));
		if (warnLines >= 0 && totLines > 0) sendStat("jc_003", String.format("%.4f", (warnLines + 0.0) / totLines));
		if (warnMiles >= 0) sendStat("jc_005", String.format("%.2f", warnMiles));
		if (totMiles >= 0) sendStat("jc_006", String.format("%.2f", totMiles));
		if (warnMiles >= 0 && totMiles > 0) sendStat("jc_007", String.format("%.4f", warnMiles / totMiles));
	}

	private void sendStat(String flag, String val) {
//		if (Configuration.isDebug != 0)
//			return;

		logger.info("");

		try {
			URL url = new URL(String.format("http://dbo.yg84.com/outman/quality/edit/update_hist?city=%s&flag=%s&val=%s&day=%s", URLEncoder.encode(cityName, "utf-8"), flag, val, Configuration.date));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Accept-Charset", "utf-8");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			int code = conn.getResponseCode();
			if (code != 200)
				throw new IOException("response code is " + code);
		} catch (Exception e) {
			logger.error(String.format("send stat city=%s&flag=%s&val=%s&day=%s error", cityName, flag, val, Configuration.date), e);
		}
	}

	private void genResultCsv(){

		try {
			PrintWriter writer=new PrintWriter(new OutputStreamWriter(new FileOutputStream(Configuration.getFileInTemp(ResultConfig.routesCheckerResult)),"UTF-8"));
			writer.println("线路编号,线路名称,方向,错误类型");
			BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(Configuration.getFileInTemp(ResultConfig.FormatErrorReport)),"UTF-8"));
			while (true){
				String data=reader.readLine();
				if (data==null) break;
				String[] cols=data.split(",");
				writer.println(String.format("%s,%s,,%s",cols[0],cols[1],"格式错误"));
			}
			reader.close();

			reader=new BufferedReader(new InputStreamReader(new FileInputStream(Configuration.getFileInTemp(ResultConfig.InvertingStationReport)),"UTF-8"));
			while (true){
				String data=reader.readLine();
				if (data==null) break;
				String[] cols=data.split(",");
				writer.println(String.format("%s,%s,,%s",cols[0],cols[1],"站点顺序错误"));
			}
			reader.close();

			reader=new BufferedReader(new InputStreamReader(new FileInputStream(Configuration.getFileInTemp(ResultConfig.FarAwayStationReport)),"UTF-8"));
			while (true){
				String data=reader.readLine();
				if (data==null) break;
				String[] cols=data.split(",");
				writer.println(String.format("%s,%s,%s,%s",cols[0],cols[1],cols[2],"站点"+cols[3]+"距离脊线太远"));
			}
			reader.close();

			reader=new BufferedReader(new InputStreamReader(new FileInputStream(Configuration.getFileInTemp(ResultConfig.SimilarLineReport)),"UTF-8"));
			while (true){
				String data=reader.readLine();
				if (data==null) break;
				String[] cols=data.split(",");
				String lineIds=cols[0];
				String lineNames=cols[1];
				for (int i=2;i<cols.length;i+=2){
					lineIds=lineIds+";"+cols[i];
					lineNames=lineNames+";"+cols[i+1];
				}
				writer.println(String.format("%s,%s,,%s",lineIds,lineNames,"相似线路"));
			}
			reader.close();

			reader=new BufferedReader(new InputStreamReader(new FileInputStream(Configuration.getFileInTemp(ResultConfig.unCheckerLine)),"UTF-8"));
			while (true){
				String data=reader.readLine();
				if (data==null) break;
				String[] cols=data.split(",");
				writer.println(String.format("%s,%s,%s,%s",cols[0],cols[1],cols[2],cols[3]));
			}
			reader.close();

			reader=new BufferedReader(new InputStreamReader(new FileInputStream(Configuration.getFileInTemp(ResultConfig.routesReport)),"UTF-8"));
			while (true){
				String data=reader.readLine();
				if (data==null) break;
				String[] cols=data.split(",");
				writer.println(String.format("%s,%s,%s,%s",cols[0],cols[1],cols[2],cols[3]));
			}
			reader.close();

			writer.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send() throws MessagingException {
		email.send();
		logger.info("RoutesChecker EMail Sending");
	}

}
