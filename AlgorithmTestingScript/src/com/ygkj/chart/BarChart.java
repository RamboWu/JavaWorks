package com.ygkj.chart;

import javafx.util.Pair;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yang on 2015/3/30.
 */
public class BarChart {

	List<Pair<String,Double>> data=new ArrayList<Pair<String, Double>>();
	String title;
	String xName;
	String yName;

	public BarChart(String title,String xName,String yName) {
		this.title=title;
		this.xName=xName;
		this.yName=yName;
	}

	public void addData(String x,double y){
		data.add(new Pair<String, Double>(x,y));
	}

	private CategoryDataset createDataSet(){
		DefaultCategoryDataset dataSet = new DefaultCategoryDataset();

		for (int i=0;i<data.size();i++) {
			dataSet.addValue(data.get(i).getValue(),yName,data.get(i).getKey());
		}

		return dataSet;
	}

	public void toImage(String outFile) {

		CategoryDataset dataSet=createDataSet();
		JFreeChart chart= ChartFactory.createBarChart(
				title,
				xName,
				yName,
				dataSet,
				PlotOrientation.VERTICAL,
				false,
				false,
				false
		);
		chart.getTitle().setFont(new Font("宋体", Font.BOLD,12));
		chart.getLegend().setItemFont(new Font("宋体", Font.PLAIN, 12));
		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.black);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.BLACK);
		plot.setNoDataMessage("没有数据");
		CategoryAxis domainAxis = plot.getDomainAxis();

		domainAxis.setLabelFont(new Font("宋体", Font.PLAIN, 12));
		domainAxis.setTickLabelFont(new Font("宋体", Font.PLAIN, 8));
		domainAxis.setLowerMargin(0.01);
		domainAxis.setUpperMargin(0.06);
		domainAxis.setMaximumCategoryLabelLines(2);

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setAutoRange(false);
		rangeAxis.setAutoRangeIncludesZero(true);
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setLabelFont(new Font("宋体", Font.PLAIN, 12));
		rangeAxis.setTickLabelFont(new Font("宋体", Font.PLAIN, 12));

		File out=new File(outFile);
		try {
			ChartUtilities.saveChartAsJPEG(out, chart, 800, 600);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
