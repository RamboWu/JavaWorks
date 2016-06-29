package com.ygkj.algroithm;

import com.ygkj.config.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 * Created by Yang on 2015/8/4.
 */
public class AlgorithmVersion {

	String cityDir;
	String version;
	String lastUpdateTime="";

	public AlgorithmVersion(String cityDir) {
		this.cityDir=cityDir;

		Properties properties=new Properties();
		try {
			properties.load(new InputStreamReader(new FileInputStream(cityDir+Configuration.algorithmVersionFile)));
			version=properties.getProperty("version","0");
		} catch (IOException e) {
			e.printStackTrace();
		}

		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		File file=new File(cityDir+ Configuration.algorithmExe);
		lastUpdateTime=dateFormat.format(file.lastModified());
	}

	public String getVersion() {
		return version;
	}

	public String getLastUpdateTime() {
		return lastUpdateTime;
	}
}
