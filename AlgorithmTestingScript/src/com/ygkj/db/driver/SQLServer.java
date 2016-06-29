package com.ygkj.db.driver;

class SQLServer implements Driver {

	private static final String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	public String getUrl(String ipAddress, String databaseName, String userName, String passWd, String enCoding) {
		return "jdbc:sqlserver://" + ipAddress + ";databaseName=" + databaseName + ";useUnicode=true&characterEncoding=" + enCoding;
	}

	public String getDriver() {
		return driver;
	}

}
