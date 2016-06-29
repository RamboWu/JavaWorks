package com.ygkj.db.driver;

import com.ygkj.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * Date: 14-7-18
 * Time: 上午11:44.
 */
public class DriverManager {

	static Driver driver;

	static {
		try {
			Class<Driver> driverClass = (Class<Driver>) Class.forName("com.ygkj.db.driver." + Configuration.DB_TYPE);
			driver = driverClass.newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static String getUrl() {
		return driver.getUrl(
				Configuration.DB_HOST + ":" + Configuration.DB_PORT,
				Configuration.DB_NAME,
				Configuration.DB_USERNAME,
				Configuration.DB_PASSWORD,
				Configuration.DB_ENCODE
		);
	}

	public static String getUrl(String host,String port,String dbName,String userName,String password,String encode){
		return driver.getUrl(
				host + ":" + port,
				dbName,
				userName,
				password,
				encode
		);
	}

	public static String getDriver() {
		return driver.getDriver();
	}
}
