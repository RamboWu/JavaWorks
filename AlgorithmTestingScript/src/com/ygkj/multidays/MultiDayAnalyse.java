package com.ygkj.multidays;

import com.ygkj.config.Configuration;
import javafx.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Yang on 2015/3/31.
 */
public class MultiDayAnalyse {

	List<String> days;

	List<Pair<String,HashMap<String,Integer>>> errorBus;

	public MultiDayAnalyse(String[] days) {
		this.days= Arrays.asList(days);
		errorBus=new ArrayList<Pair<String, HashMap<String, Integer>>>();
	}

	public void calculate() {

		for (int i=0;i<days.size();i++) {
			File file=new File(Configuration.cityDir+days.get(i));
			if (file.exists()) {

			}
		}

	}
}
