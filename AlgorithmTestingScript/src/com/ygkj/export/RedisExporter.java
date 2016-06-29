package com.ygkj.export;

import com.ygkj.config.Configuration;
import com.ygkj.config.ResultConfig;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by YCZhang on 8/26/15.
 */
public class RedisExporter {

	private static Logger logger=Logger.getLogger("System");

//	private static final String HOST = "10.168.165.47";
	private static final String HOST = "10.165.102.184";
	private static final int PORT = 6379;
	private static final String PASSWD = "REDISchelaile";
	private static final int TIMEOUT_MS = 10 * 1000;
	private static final int DB = 8;
	private static final int EXPIRED_SEC = 24 * 60 * 60;

	public static void export(String cityEn, String date) {
		try {
			String prepareKey = String.format("prepare___%s___%s", cityEn, date);
			String basedataKey = String.format("base___%s___%s", cityEn, date);
			String singleKey = String.format("single___%s___%s", cityEn, date);
			String matchKey = String.format("match___%s___%s", cityEn, date);
//			String formatErrorReportKey = String.format("format_error_report___%s___%s", cityEn, date);
//			String invertingStationReportKey = String.format("inverting_station_report___%s___%s", cityEn, date);
//			String farawayStationReportKey = String.format("faraway_station_report___%s___%s", cityEn, date);
			String similarLineReportKey = String.format("similar_line_report___%s___%s", cityEn, date);
//			String uncheckLineReportKey = String.format("uncheck_line_report___%s___%s", cityEn, date);

			_exportFile(basedataKey, new File(Configuration.lineDataFile));
			_exportFile(singleKey, new File(Configuration.getFileInTemp(ResultConfig.singleMatchingReport)));
			_exportFile(matchKey, new File(Configuration.getFileInTemp(ResultConfig.busMatchingFile)));
//			_exportFile(formatErrorReportKey, new File(Configuration.getFileInTemp(ResultConfig.FormatErrorReport)));
//			_exportFile(invertingStationReportKey, new File(Configuration.getFileInTemp(ResultConfig.InvertingStationReport)));
//			_exportFile(farawayStationReportKey, new File(Configuration.getFileInTemp(ResultConfig.FarAwayStationReport)));
			_exportFile(similarLineReportKey, new File(Configuration.getFileInTemp(ResultConfig.SimilarLineReport)));
//			_exportFile(uncheckLineReportKey, new File(Configuration.getFileInTemp(ResultConfig.unCheckerLine)));
			_exportValue(prepareKey, "true");
		} catch (Exception e) {
			logger.error("redis sync error");
		}
	}

	private static void _exportValue(String key, String val) {
		Jedis jedis = null;
		try {
			jedis = new Jedis(HOST, PORT, TIMEOUT_MS);
			jedis.auth(PASSWD);
			jedis.select(DB);

			jedis.setex(key, EXPIRED_SEC, val);
		} catch (Exception e) {
			logger.error(String.format("write key %s to redis error", key), e);
			return;
		} finally {
			if (jedis != null) try { jedis.close(); } catch (Exception e) {}
		}
	}

	private static void _exportFile(String key, File file) {
		if (!file.exists() || file.isDirectory())
			return;

		StringBuilder content = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
			char[] chars = new char[1024];
			while (true) {
				int len = reader.read(chars, 0, 1024);
				if (len==-1)
					break;

				content.append(chars, 0, len);
			}
		} catch (Exception e) {
			logger.error(String.format("read file %s error", file.getPath()), e);
			return;
		} finally {
			if (reader != null) try { reader.close(); } catch (Exception e) {}
		}

		_exportValue(key, content.toString());
	}
}
