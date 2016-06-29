package com.ygkj.script;

import com.ygkj.config.Configuration;
import com.ygkj.config.ResultConfig;

import java.io.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Yang on 2015/5/28.
 */
public class RoutesAnalyseReport {

	private List<String> formatError;
	private List<String> invertingError;
	private List<String> stationError;
	private List<String> notOpen;
	private List<List<String>> similar;

	public RoutesAnalyseReport() {

	}

	private void loadLineAnalyse(){

		try {
			BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(Configuration.getFileInTemp(ResultConfig.lineAnalyseReport)),"UTF-8"));

			String formatStr= reader.readLine();
			String[] cols=formatStr.split(Pattern.quote(":"));

			reader.close();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void load(){

	}
}
