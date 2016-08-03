package com.yg.algo.commons.gps.bean;


/**
 * Created by YCZhang on 4/22/15.
 */
public class GPSSegment implements GPSElement {

	public GPSPoint end1;
	public GPSPoint end2;

	public GPSSegment(GPSPoint end1, GPSPoint end2) {
		this.end1 = end1;
		this.end2 = end2;
	}
}
