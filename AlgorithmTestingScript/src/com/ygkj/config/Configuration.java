package com.ygkj.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Created by Yang on 2015/3/10.
 */
public class Configuration {

	public static Properties properties=new Properties();

	public static String citySymbol="Citys";
	public static String cityDir="";
	public static String lineDataFile="input/s_json.csv";
	public static String stationMileageFile="temp/station_dis.csv";
	public static String inputFile="output/matching.log";
	public static String tempDir = "temp/";

	public static String algorithmConfigFile="input/config.ini";
	public static String algorithmVersionFile="version.ini";
	public static String algorithmDumpDir="dump/";
	public static String algorithmExe="BusMatching.exe";

	public static String separator = ",";

	public static int isClear=0;
	public static int isDebug=0;

	public static String cityLabels="";
	public static String cityNames="";
	public static String eMailUserName="algorithmreport@chelaile.net.cn";
	public static String eMailPassword="yuanguang2015";
	public static String smtpHost="smtp.ym.163.com";

	public static String eMailTo="yang.zhao@chelaile.net.cn";
	public static String cityEmailCc =null;

	public static String summaryEMailCc=null;

	public static String routesCheckerEMailCc =null;

	public static String date;

	public static int sortFileLimit=100000;

	public static int useDatabase=0;

	public static String DB_USERNAME;
	public static String DB_PASSWORD;
	public static String DB_HOST;
	public static String DB_PORT;
	public static String DB_NAME;
	public static String DB_ENCODE="UTF-8";
	public static String DB_TYPE="MySQL";

	public static boolean init() {
		isClear=Integer.parseInt(properties.getProperty("-c","0"));
		isDebug = Integer.parseInt(properties.getProperty("-d", "0"));

		String configFile=properties.getProperty("-cf","config.ini");
		try {
			Properties config=new Properties();
			config.load(new InputStreamReader(new FileInputStream(configFile),"UTF-8"));
			cityLabels=config.getProperty("cityLabels",cityLabels);
			cityNames=config.getProperty("cityNames");
			inputFile=config.getProperty("input");
			eMailUserName=config.getProperty("eMailUserName",eMailUserName);
			eMailPassword=config.getProperty("eMailPassword",eMailPassword);
			smtpHost=config.getProperty("smtpHost",smtpHost);
			eMailTo=config.getProperty("eMailTo",eMailTo);
			cityEmailCc =config.getProperty("cityEmailCc", cityEmailCc);
			summaryEMailCc=config.getProperty("summaryEmailCc",summaryEMailCc);
			routesCheckerEMailCc =config.getProperty("routesCheckerEMailCc", routesCheckerEMailCc);
			sortFileLimit=Integer.parseInt(config.getProperty("sortFileLimit",Integer.toString(sortFileLimit)));
			date=config.getProperty("date",null);
			useDatabase=Integer.parseInt(config.getProperty("useDatabase","0"));

			if (useDatabase==1){
				DB_USERNAME=config.getProperty("DB_USERNAME");
				DB_HOST=config.getProperty("DB_HOST");
				DB_PORT=config.getProperty("DB_PORT");
				DB_PASSWORD=config.getProperty("DB_PASSWORD");
				DB_NAME=config.getProperty("DB_NAME");
				DB_ENCODE=config.getProperty("DB_ENCODE","UTF-8");
				DB_TYPE=config.getProperty("DB_TYPE","MySQL");
			}

			if (inputFile==null || cityNames==null) return false;

			return dirInit();

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private static boolean dirInit() {
		mkDirs();
		return true;
	}

	public static String getFileInTemp(String file){
		return tempDir+file;
	}

	public static void mkDirs() {
		File file=new File(tempDir);
		if (!file.exists()) file.mkdirs();
	}

	public static void clearDirs() {
		File file=new File(tempDir);

		File[] files=file.listFiles();
		for (int i=0;i<files.length;i++){
			deleteDir(files[i]);
		}
	}

	private static void deleteDir(File dir){
		if (dir.isDirectory()){
			File[] files=dir.listFiles();
			for (int i=0;i<files.length;i++){
				deleteDir(files[i]);
			}
		}
		dir.delete();
	}

	public static void setProperty(String args) {
		String[] cols=args.split(Pattern.quote("="));
		if (cols.length!=2) return;
		properties.setProperty(cols[0],cols[1]);
	}

}
