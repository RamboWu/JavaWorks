package com.ygkj.algroithm;

import com.ygkj.config.Configuration;

import java.io.File;

/**
 * Created by Yang on 2015/8/4.
 */
public class AlgorithmDump {

	private String cityDir;
	private int dumpCounter=0;

	public AlgorithmDump(String cityDir) {
		this.cityDir=cityDir;

		File dir=new File(cityDir+ Configuration.algorithmDumpDir);
		dumpCounter=dir.listFiles().length;
	}

	public int getDumpCounter() {
		return dumpCounter;
	}
}
