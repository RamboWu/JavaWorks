package com.yg.algo.commons.gps.util;

import com.yg.algo.commons.gps.bean.GPSPoint;
import com.yg.algo.commons.gps.bean.GPSSegment;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Created by YCZhang on 4/22/15.
 */
public class GPSUtil {

	// 3 赵杨的方法

	public static final double RADIUS_m_1 = 60 * 1.1515 * 1.609344 * 1000 * 180 / Math.PI;
	public static final double RADIUS_m_2 = 6371229;
	public static final double RADIUS_m_3 = 6378137;
	public static final double RADIUS_m = RADIUS_m_3;

	/**
	 * 计算两点间的GPS距离
	 * @param p1
	 * @param p2
	 * @return 单位米
	 */
	public static double distance_m(GPSPoint p1, GPSPoint p2) {
		return distance_m_3(p1, p2);
	}

	/**
	 * 计算两点间的GPS距离，我的方法
	 * @param p1
	 * @param p2
	 * @return 单位米
	 */
	public static double distance_m_1(GPSPoint p1, GPSPoint p2) {
		return RADIUS_m * includedAngle_r(p1, p2);
	}

	/**
	 * 计算亮点简单呃GPS距离，赵杨的方法
	 * @param p1
	 * @param p2
	 * @return 单位米
	 */
	public static double distance_m_3(GPSPoint p1, GPSPoint p2) {
		double lat1 = Math.toRadians(p1.lat_d);
		double lon1 = Math.toRadians(p1.lng_d);
		double lat2 = Math.toRadians(p2.lat_d);
		double lon2 = Math.toRadians(p2.lng_d);
		double d1 = Math.abs(lat1 - lat2);
		double d2 = Math.abs(lon1 - lon2);
		double p = Math.pow(Math.sin(d1 / 2), 2) + Math.cos(lat1)
				* Math.cos(lat2) * Math.pow(Math.sin(d2 / 2), 2);
		double dis = RADIUS_m_3 * 2 * Math.asin(Math.sqrt(p));
		return dis;
	}

	/**
	 * 计算点到线段的距离
	 * @param p
	 * @param s
	 * @return 单位米
	 */
	public static double distance_m(GPSPoint p, GPSSegment s) {
		return distance_m_3(p, s);
	}

	/**
	 * 计算点到线段的距离，我的方法，球面距离，用角度算，比较慢
	 * @param p
	 * @param s
	 * @return 单位米
	 */
	public static double distance_m_1(GPSPoint p, GPSSegment s) {
		GPSPoint p0 = p;
		GPSPoint p1 = s.end1;
		GPSPoint p2 = s.end2;

		double d01 = distance_m(p0, p1);
		double d02 = distance_m(p0, p2);
		double d12 = distance_m(p1, p2);

		if (d12 < 1e-5)
			return d01;

		if (d01*d01+d12*d12<d02*d02)
			return d01;
		if (d02*d02+d12*d12<d01*d01)
			return d02;

		return RADIUS_m * includedAngle_r(p, s);
	}

	/**
	 * 计算点到线段的距离，我的方法，平面距离
	 * @param p
	 * @param s
	 * @return 单位米
	 */
	public static double distance_m_2(GPSPoint p, GPSSegment s) {
		GPSPoint p0 = p;
		GPSPoint p1 = s.end1;
		GPSPoint p2 = s.end2;

		double d01 = distance_m(p0, p1);
		double d02 = distance_m(p0, p2);
		double d12 = distance_m(p1, p2);

		if (d12 < 1e-5)
			return d01;

		if (d01*d01+d12*d12<d02*d02)
			return d01;
		if (d02*d02+d12*d12<d01*d01)
			return d02;

		return Math.sqrt((2*Math.pow(d01, 2)*Math.pow(d02, 2)+2*Math.pow(d01, 2)*Math.pow(d12, 2)+2*Math.pow(d02, 2)*Math.pow(d12, 2)-Math.pow(d01, 4)-Math.pow(d02, 4)-Math.pow(d12, 4)))/2/d12;
	}

	/**
	 * 计算点到线段的距离，赵杨的方法
	 * @param p
	 * @param s
	 * @return 单位米
	 */
	public static double distance_m_3(GPSPoint p, GPSSegment s) {
		double disPA = distance2(p, s.end1);
		double disPB = distance2(p, s.end2);
		if (disPA < 1e-10) return distance_m_3(p, s.end1);
		if (disPB < 1e-10) return distance_m_3(p, s.end2);
		double disAB = distance2(s.end1, s.end2);
		if (disAB < 1e-10) return distance_m_3(p, s.end2);
		if (disPA >= disAB + disPB) return distance_m_3(p, s.end2);
		if (disPB >= disAB + disPA) return distance_m_3(p, s.end1);

		GPSPoint vectorAB = new GPSPoint(s.end2.lng_d - s.end1.lng_d, s.end2.lat_d - s.end1.lat_d);
		GPSPoint vectorAP = new GPSPoint(p.lng_d - s.end1.lng_d, p.lat_d - s.end1.lat_d);
		if (Math.abs(cross_product(vectorAP, vectorAB)) < 1e-10) return 0;
		double sinAlpha = 0;
		if (disPA * disAB != 0) {
			sinAlpha = cross_product(vectorAP, vectorAB) / sqrt(disPA) / sqrt(disAB);
		}
		double gpsAP = distance_m_3(s.end1, p);
		return Math.abs(gpsAP * sinAlpha);
	}

	/**
	 * 计算点在线段上的投影到线段起点的距离
	 * @param p
	 * @param s
	 * @return 单位米
	 */
	public static double shadow_m(GPSPoint p, GPSSegment s) {
		return shadow_m_3(p, s);
	}

	/**
	 * 计算点在线段上的投影到线段起点的距离，赵杨的方法
	 * @param p
	 * @param s
	 * @return 单位米
	 */
	public static double shadow_m_3(GPSPoint p, GPSSegment s) {
		GPSPoint vectorAB = new GPSPoint(s.end2.lng_d - s.end1.lng_d, s.end2.lat_d - s.end1.lat_d);
		GPSPoint vectorAP = new GPSPoint(p.lng_d - s.end1.lng_d, p.lat_d - s.end1.lat_d);
		double cosAlpha = 0;
		double disPA = distance(p, s.end1);
		double disAB = distance(s.end1, s.end2);
		if (disPA * disAB != 0) {
			cosAlpha = dot_product(vectorAP, vectorAB) / distance(p, s.end1) / distance(s.end1, s.end2);
		}
		double gpsAP = distance_m_3(s.end1, p);
		return gpsAP * cosAlpha;
	}

	/**
	 * 简单判断是否在中国境内
	 * @param p
	 * @return
	 */
	public static boolean outOfChina(GPSPoint p) {
		if (p.lng_d < 72.004 || p.lng_d > 137.8347)
			return true;
		if (p.lat_d < 0.8293 || p.lat_d > 55.8271)
			return true;
		return false;
	}

	private static double distance(GPSPoint p1, GPSPoint p2) {
		return sqrt(distance2(p1, p2));
	}

	private static double distance2(GPSPoint p1, GPSPoint p2) {
		return sqr(p1.lng_d - p2.lng_d) + sqr(p1.lat_d - p2.lat_d);
	}

	private static double sqr(double d) {
		return d * d;
	}

	private static double sqrt(double d) {
		return Math.sqrt(d);
	}

	private static double cross_product(GPSPoint p1, GPSPoint p2) {
		return p1.lng_d * p2.lat_d - p1.lat_d * p2.lng_d;
	}

	private static double dot_product(GPSPoint p1, GPSPoint p2) {
		return p1.lng_d * p2.lng_d + p1.lat_d * p2.lat_d;
	}

	private static double cosIncludedAngle_r(GPSPoint p1, GPSPoint p2) {
		double lng1_r = Math.toRadians(p1.lng_d);
		double lat1_r = Math.toRadians(p1.lat_d);
		double lng2_r = Math.toRadians(p2.lng_d);
		double lat2_r = Math.toRadians(p2.lat_d);

		return Math.cos(lat1_r) * Math.cos(lat2_r) * Math.cos(lng1_r - lng2_r) + Math.sin(lat1_r) * Math.sin(lat2_r);
	}

	private static double includedAngle_r(GPSPoint p1, GPSPoint p2) {
		double lng1_r = Math.toRadians(p1.lng_d);
		double lat1_r = Math.toRadians(p1.lat_d);
		double lng2_r = Math.toRadians(p2.lng_d);
		double lat2_r = Math.toRadians(p2.lat_d);

		double tmp = Math.cos(lat1_r) * Math.cos(lat2_r) * Math.cos(lng1_r - lng2_r) + Math.sin(lat1_r) * Math.sin(lat2_r);
		tmp = Math.max(tmp, 0);
		tmp = Math.min(tmp, 1);

		return Math.acos(tmp);
	}

	private static double includedAngle_r(GPSPoint p, GPSSegment s) {
		double cosR1 = cosIncludedAngle_r(s.end1, s.end2);
		double cosR2 = cosIncludedAngle_r(p, s.end1);
		double cosR3 = cosIncludedAngle_r(p, s.end2);

		BigDecimal bc1 = new BigDecimal("1", MathContext.DECIMAL128);
		BigDecimal bc2 = new BigDecimal("2", MathContext.DECIMAL128);
		BigDecimal b1 = new BigDecimal(String.valueOf(cosR1), MathContext.DECIMAL128);
		BigDecimal b2 = new BigDecimal(String.valueOf(cosR2), MathContext.DECIMAL128);
		BigDecimal b3 = new BigDecimal(String.valueOf(cosR3), MathContext.DECIMAL128);
		double ddd = bc1.subtract(b1.multiply(b1, MathContext.DECIMAL128), MathContext.DECIMAL128).
				subtract(b2.multiply(b2, MathContext.DECIMAL128), MathContext.DECIMAL128).
				subtract(b3.multiply(b3, MathContext.DECIMAL128), MathContext.DECIMAL128).
				add(bc2.multiply(b1, MathContext.DECIMAL128).multiply(b2, MathContext.DECIMAL128).multiply(b3, MathContext.DECIMAL128), MathContext.DECIMAL128).
				divide(bc1.subtract(b1.multiply(b1, MathContext.DECIMAL128), MathContext.DECIMAL128), MathContext.DECIMAL128)
				.doubleValue();

		return Math.asin(Math.sqrt(ddd));
	}

//	public static void main(String[] args) {
//		System.out.println(Math.acos(1));
//		System.out.println(distance_m(new GPSPoint(120.044316, 30.247614000000002), new GPSPoint(120.044316, 30.247614000000002)));
//	}

}
