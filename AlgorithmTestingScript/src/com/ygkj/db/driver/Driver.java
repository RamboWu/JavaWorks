package com.ygkj.db.driver;

/**
 * Created with IntelliJ IDEA.
 * Date: 14-7-18
 * Time: 上午11:46.
 */
interface Driver {
	abstract String getUrl(String ipAddress, String databaseName, String userName, String passWd, String enCoding);

	abstract String getDriver();
}
