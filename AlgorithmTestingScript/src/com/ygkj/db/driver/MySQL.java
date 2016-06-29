package com.ygkj.db.driver;

class MySQL implements Driver {

	private static final String driver = "com.mysql.jdbc.Driver";

	public String getUrl(String ipAddress, String databaseName, String userName, String passWd, String enCoding) {
		return "jdbc:mysql://" + ipAddress + "/" + databaseName + "?useUnicode=true&characterEncoding=" + enCoding;
	}

	public String getDriver() {
		return driver;
	}
}
