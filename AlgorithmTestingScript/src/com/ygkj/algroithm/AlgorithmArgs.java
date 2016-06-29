package com.ygkj.algroithm;

import com.ygkj.config.Configuration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Yang on 2015/8/4.
 */
public class AlgorithmArgs {

	private String cityDirs;
	private List<String> args;

	public String zookeeper_on;
	public String use_origin_lineId;

	public String recv_port;
	public String send_port;
	public String send_ip;

	public AlgorithmArgs(String cityDir){
		this.cityDirs=cityDir;
		this.args=new ArrayList<String>();

		Properties properties=new Properties();
		try {
			properties.load(new InputStreamReader(new FileInputStream(cityDir+ Configuration.algorithmConfigFile)));
			zookeeper_on=properties.getProperty("zookeeper_on","0");
			use_origin_lineId=properties.getProperty("use_original_lineId","0");
			recv_port=properties.getProperty("recv_port","0");
			send_port=properties.getProperty("send_port","0");
			send_ip=properties.getProperty("send_ip","0");
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(cityDir+Configuration.algorithmConfigFile)));
			while (true){
				String data=reader.readLine();
				if (data==null){
					break;
				}
				args.add(data);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public	List<String> getArgs() {
		return args;
	}
}
