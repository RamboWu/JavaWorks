package com.ygkj.script;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yang on 2015/4/17.
 */
public class SummaryReport {

	static class SingleReport{
		public String cityName;
		public long totalGps;
		public int discerningBusTotal=0;
		public int busTotal=0;
		public int discerningLineTotal=0;
		public int lineTotal=0;
		public long discerningSTNTotal=0;
		public long algorithmMissingSTN=0;
		public long gpsMissingSTN=0;
		public long stnTotal=0;
	}

	public List<SingleReport> reportList=new ArrayList<SingleReport>();

	public SummaryReport(){

	}

	public void addReport(AnalyseReport report){
		SingleReport singleReport=new SingleReport();
		singleReport.cityName=report.cityName;
		singleReport.totalGps=report.data_total+report.errorBus_data_total;
		singleReport.busTotal=report.busTotal;
		singleReport.discerningBusTotal=report.discerningBus;
		singleReport.discerningLineTotal=report.matchingLineTotal;
		singleReport.lineTotal=report.matchingLineTotal+report.unMatchingLineTotal;
		singleReport.stnTotal=report.stnTotal;
		singleReport.algorithmMissingSTN=report.missingStn-report.gpsMissingSTN;
		singleReport.discerningSTNTotal=report.stnTotal-report.missingStn;
		singleReport.gpsMissingSTN=report.gpsMissingSTN;

		reportList.add(singleReport);
	}

	public String toHtml() {
		StringBuilder buffer=new StringBuilder();

		buffer.append("<table border=\"1\">");
		buffer.append("<tr>")
			.append("<th rowspan=\"2\">").append("城市名").append("</th>")
			.append("<th>").append("GPS").append("</th>")
			.append("<th colspan=\"3\">").append("车辆").append("</th>")
			.append("<th colspan=\"3\">").append("线路").append("</th>")
			.append("<th  colspan=\"7\">").append("报站").append("</th>")
			.append("</tr>");
		buffer.append("<tr>")
			.append("<th>").append("总点数").append("</th>")
			.append("<th>").append("总车辆数").append("</th>")
			.append("<th>").append("确信正确识别车辆数").append("</th>")
			.append("<th>").append("占比").append("</th>")
			.append("<th>").append("总线路数").append("</th>")
			.append("<th>").append("识别车所在线路").append("</th>")
			.append("<th>").append("占比").append("</th>")
			.append("<th>").append("总报站数").append("</th>")
			.append("<th>").append("算法报站数").append("</th>")
			.append("<th>").append("占比").append("</th>")
			.append("<th>").append("GPS缺失引起丢失的报站数").append("</th>")
			.append("<th>").append("占比").append("</th>")
			.append("<th>").append("算法丢失的报站数").append("</th>")
			.append("<th>").append("占比").append("</th>")
			.append("</tr>");

		for (int i=0;i<reportList.size();i++){
			SingleReport report=reportList.get(i);
			buffer.append("<tr>")
					.append("<td>").append(report.cityName).append("</td>")
					.append("<td>").append(report.totalGps).append("</td>")
					.append("<td>").append(report.busTotal).append("</td>")
					.append("<td>").append(report.discerningBusTotal).append("</td>")
					.append("<td>").append(String.format("%.2f",(double)report.discerningBusTotal/report.busTotal)).append("</td>")
					.append("<td>").append(report.lineTotal).append("</td>")
					.append("<td>").append(report.discerningLineTotal).append("</td>")
					.append("<td>").append(String.format("%.2f",(double)report.discerningLineTotal/report.lineTotal)).append("</td>")
					.append("<td>").append(report.stnTotal).append("</td>")
					.append("<td>").append(report.discerningSTNTotal).append("</td>")
					.append("<td>").append(String.format("%.2f",(double)report.discerningSTNTotal/report.stnTotal)).append("</td>")
					.append("<td>").append(report.gpsMissingSTN).append("</td>")
					.append("<td>").append(String.format("%.2f",(double)report.gpsMissingSTN/report.stnTotal)).append("</td>")
					.append("<td>").append(report.algorithmMissingSTN).append("</td>")
					.append("<td>").append(String.format("%.2f",(double)report.algorithmMissingSTN/report.stnTotal)).append("</td>")
					.append("</tr>");
		}
		buffer.append("</table>");
		return buffer.toString();
	}
}
