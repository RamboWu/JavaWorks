package com.ygkj.db;

import com.alibaba.druid.pool.DruidDataSource;
import com.ygkj.config.Configuration;
import com.ygkj.db.driver.DriverManager;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * Date: 14-7-17
 * Time: 下午10:11.
 */
public class DBConnectionPool {
	private static DBConnectionPool dbConnectionPool;
	private static DruidDataSource dataSource;

	private DBConnectionPool() throws SQLException {
		dataSource = new DruidDataSource();
		dataSource.setDriverClassName(DriverManager.getDriver());
		dataSource.setUsername(Configuration.DB_USERNAME);
		dataSource.setPassword(Configuration.DB_PASSWORD);
		dataSource.setUrl(DriverManager.getUrl());
		dataSource.setInitialSize(1);
		dataSource.setMinIdle(1);
		dataSource.setMaxActive(5);
		dataSource.setPoolPreparedStatements(false);
	}

	private DBConnectionPool(String host,String port,String dbName,String userName,String password,String encode){
		dataSource = new DruidDataSource();
		dataSource.setDriverClassName(DriverManager.getDriver());
		dataSource.setUsername(userName);
		dataSource.setPassword(password);
		dataSource.setUrl(DriverManager.getUrl(host,port,dbName,userName,password,encode));
		dataSource.setInitialSize(1);
		dataSource.setMinIdle(1);
		dataSource.setMaxActive(5);
		dataSource.setPoolPreparedStatements(false);
	}

	public static DBConnectionPool getInstance() throws SQLException {
		if (dbConnectionPool == null) {
			dbConnectionPool = new DBConnectionPool();
		}
		return dbConnectionPool;
	}

	public static DBConnectionPool getInstance(String host,String port,String dbName,String userName,String password,String encode) throws SQLException {
		if (dbConnectionPool == null) {
			dbConnectionPool = new DBConnectionPool(host,port,dbName,userName,password,encode);
		}
		return dbConnectionPool;
	}

	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
}
