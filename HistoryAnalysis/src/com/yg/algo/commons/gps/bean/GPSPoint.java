package com.yg.algo.commons.gps.bean;

/**
 * Created by YCZhang on 4/22/15.
 */
public class GPSPoint implements GPSElement {

	public double lng_d;
	public double lat_d;

	public GPSPoint(double lng_d, double lat_d) {
		this.lng_d = lng_d;
		this.lat_d = lat_d;
	}

	@Override
	public String toString() {
		return lng_d + "," + lat_d;
	}
}
