package com.ygkj.export;

import com.ygkj.config.Configuration;
import com.ygkj.config.ResultConfig;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.*;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.regex.Pattern;

public class DBExporter {
	private static final Logger logger = Logger.getLogger("System");

	static {
		try	{
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void export(String cityEn) {
		Connection cn = null;
		CallableStatement cs = null;
		try	{
			if (Configuration.isDebug == 0)
				cn = DriverManager.getConnection("jdbc:mysql://rdsm2aan2yqriie.mysql.rds.aliyuncs.com:3306/work", "work", "work_chelaile");
			else
				cn = DriverManager.getConnection("jdbc:mysql://10.117.33.149:3306/test", "root", "chelaile");

			cs = cn.prepareCall("{call sp_insert_task_basedata_check(?,?,?,?,?,?,?)}");

			_export(cityEn, 1, new File(Configuration.getFileInTemp(ResultConfig.FormatErrorReport)), cs);
			_export(cityEn, 2, new File(Configuration.getFileInTemp(ResultConfig.InvertingStationReport)), cs);
			_export(cityEn, 3, new File(Configuration.getFileInTemp(ResultConfig.FarAwayStationReport)), cs);
			_export(cityEn, 4, new File(Configuration.getFileInTemp(ResultConfig.SimilarLineReport)), cs);
			_export(cityEn, 5, new File(Configuration.getFileInTemp(ResultConfig.unCheckerLine)), cs);
			_export(cityEn, 6, new File(Configuration.getFileInTemp(ResultConfig.routesReport)), cs);
		}
		catch (Exception e) {
			logger.error("export check report to db error", e);
		}
		finally {
			if (cs != null) try {cs.close();} catch (Exception e) {}
			if (cn != null) try	{cn.close();} catch (Exception e) {}
		}
	}

	private static final String SPLIT = Pattern.quote(",");

	private static void _export(String cityEn, int errorType, File inFile, CallableStatement cs) {
		if (!inFile.exists())
			return;

		BufferedReader reader = null;
		try	{
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), "UTF-8"));
			while (true) {
				String line = reader.readLine();
				if (line == null)
					break;

				String[] fields = line.split(SPLIT);

				boolean flag = false;

				String lineNo = null;
				String lineName = null;
				int direction = -1;
				String errorDesc = null;

				switch (errorType) {
					case 1:
						if (fields.length >= 2) {
							lineNo = fields[0];
							lineName = fields[1];
							direction = -1;
							errorDesc = "格式错误";

							flag = true;
						}
						break;
					case 2:
						if (fields.length >= 2) {
							lineNo = fields[0];
							lineName = fields[1];
							direction = -1;
							errorDesc = "站点顺序错误";

							flag = true;
						}
						break;
					case 3:
						if (fields.length >= 4) {
							lineNo = fields[0];
							lineName = fields[1];
							direction = Integer.parseInt(fields[2]);
							errorDesc = String.format("站点%s距离脊线太远", fields[3]);

							flag = true;
						}
						break;
					case 4:
						if (fields.length >= 2) {
							lineNo = fields[0];
							lineName = fields[1];
							for (int i = 2; i + 1 < fields.length; i += 2) {
								lineNo += ";" + fields[i];
								lineName += ";" + fields[i + 1];
							}
							direction = -1;
							errorDesc = "相似线路";

							flag = true;
						}
						break;
					case 5:
						if (fields.length >= 4) {
							lineNo = fields[0];
							lineName = fields[1];
							direction = -1;
							errorDesc = fields[3];

							flag = true;
						}
						break;
					case 6:
						if (fields.length >= 4) {
							lineNo = fields[0];
							lineName = fields[1];
							direction = Integer.parseInt(fields[2]);
							errorDesc = fields[3];

							flag = true;
						}
						break;
					default:
						break;
				}

				if (flag)
					insertToDB(cs, cityEn, Configuration.date, errorType, lineNo, lineName, direction, errorDesc);
			}
		}
		catch (IOException e) {
			logger.error(String.format("read from file %s error, city: %s", inFile.getPath(), cityEn), e);
		}
		finally	{
			if (reader != null) try	{reader.close();} catch (Exception e) {}
		}
	}

	private static void insertToDB(CallableStatement cs, String cityEn, String date, int errorType, String lineNo, String lineName, int direction, String errorDesc) {
		try {
			cs.setString(1, cityEn);
			cs.setString(2, date);
			cs.setInt(3, errorType);
			cs.setString(4, lineNo);
			cs.setString(5, lineName);
			cs.setInt(6, direction);
			cs.setString(7, errorDesc);

			cs.execute();
		}
		catch (Exception e) {
			logger.warn(String.format("insert data {cityEn: %s, date: %s, errorType: %s, lineNo: %s, lineName: %s, direction: %s, errorDesc: %s} error", cityEn, date, errorType, lineNo, lineName, direction, errorDesc), e);
		}
	}

	public static void main(String... args) {
		PropertyConfigurator.configure("log4j.properties");
		Configuration.date = "2015-07-21";
		Configuration.tempDir = "./2015-07-21/";
		export("FoShan");
	}
}