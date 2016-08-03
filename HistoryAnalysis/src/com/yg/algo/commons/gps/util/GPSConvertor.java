package com.yg.algo.commons.gps.util;

import com.yg.algo.commons.gps.bean.GPSPoint;
import com.yg84.chelaile.manager.model.YGLocation;
import com.yg84.chelaile.manager.utils.GPSConvert;

/**
 * Created by YCZhang on 6/1/15.
 */
public class GPSConvertor {

	/**
	 * 百度坐标转换成WGS坐标，有误差
	 * @param p
	 * @return WGS坐标
	 */
	public static GPSPoint bd2wgs(GPSPoint p) {
		YGLocation l = new YGLocation(p.lng_d, p.lat_d);
		l = GPSConvert.bd2wgs(l);
		return new GPSPoint(l.longitude, l.latitude);
	}

	/**
	 * WGS坐标转换成百度坐标，有误差
	 * @param p
	 * @return 百度坐标
	 */
	public static GPSPoint wgs2bd(GPSPoint p) {
		YGLocation l = new YGLocation(p.lng_d, p.lat_d);
		l = GPSConvert.wgs2bd(l);
		return new GPSPoint(l.longitude, l.latitude);
	}

	/**
	 * 百度坐标转换成GCJ坐标，有误差
	 * @param p
	 * @return GCJ坐标
	 */
	public static GPSPoint bd2gcj(GPSPoint p) {
		double[] dd = GPSConvert.bd2gcj(p.lng_d, p.lat_d);
		return new GPSPoint(dd[0], dd[1]);
	}

	/**
	 * GCJ坐标转换成百度坐标，有误差
	 * @param p
	 * @return 百度坐标
	 */
	public static GPSPoint gcj2bd(GPSPoint p) {
		double[] dd = GPSConvert.gcj2bd(p.lng_d, p.lat_d);
		return new GPSPoint(dd[0], dd[1]);
	}

	/**
	 * WGS坐标转换成GCJ坐标，有误差
	 * @param p
	 * @return GCJ坐标
	 */
	public static GPSPoint wgs2gcj(GPSPoint p) {
		double[] dd = GPSConvert.wgs2gcj(p.lng_d, p.lat_d);
		return new GPSPoint(dd[0], dd[1]);
	}

	/**
	 * GCJ坐标转换成WGS坐标，有误差
	 * @param p
	 * @return WGS坐标
	 */
	public static GPSPoint gcj2wgs(GPSPoint p) {
		return bd2wgs(gcj2bd(p));
	}

}
