package com.ygkj.chart;

import javafx.util.Pair;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Yang on 2015/3/23.
 */
public class LineChart {

	class Pair {
		double y;
		String x;

		public Pair(String x,double y) {
			this.x=x;
			this.y=y;
		}
	}

	static Comparator<Pair> pairComparator=new Comparator<Pair>() {
		@Override
		public int compare(Pair o1, Pair o2) {
			if (o1.y<o2.y) return -1;
			if (o1.y>o2.y) return 1;
			return 0;
		}
	};

	String title;
	String xName;
	String yName;
	List<Pair> data;

	public LineChart(String title,String xName,String yName) {
		data=new ArrayList<Pair>();
		this.title=title;
		this.xName=xName;
		this.yName=yName;
	}

	public void addData(String x,double y) {
		data.add(new Pair(x,y));
	}

	private DefaultCategoryDataset generatorDataSet() {
		DefaultCategoryDataset dataSet = new DefaultCategoryDataset();

		for (int i=0;i<data.size();i++) {
			dataSet.addValue(data.get(i).y,yName,data.get(i).x);
		}

		return dataSet;
	}

	public void toImage(String outFile,int best) {

		Collections.sort(data,pairComparator);

		CategoryDataset dataSet=generatorDataSet();
		JFreeChart chart= ChartFactory.createLineChart(title, xName, yName, dataSet);
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

		IntervalMarker inter = new IntervalMarker(best-15, 100);
		inter.setLabelOffsetType(LengthAdjustmentType.EXPAND);
		inter.setPaint(Color.LIGHT_GRAY);

		plot.addRangeMarker(inter, Layer.BACKGROUND);

		File out=new File(outFile);
		try {
			ChartUtilities.saveChartAsJPEG(out, chart, 800, 600);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void toImage(String outFile) {

		CategoryDataset dataSet=generatorDataSet();
		JFreeChart chart= ChartFactory.createLineChart(title, xName, yName, dataSet);
		chart.getTitle().setFont(new Font("宋体", Font.BOLD,12));
		chart.getLegend().setItemFont(new Font("宋体", Font.PLAIN, 12));
		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setRangeGridlinePaint(Color.black);
		plot.setDomainGridlinePaint(Color.BLACK);
		plot.setNoDataMessage("没有数据");
		CategoryAxis domainAxis = plot.getDomainAxis();

		domainAxis.setLabelFont(new Font("宋体", Font.PLAIN, 12));
		domainAxis.setTickLabelFont(new Font("宋体", Font.PLAIN, 8));
		domainAxis.setLowerMargin(0.01);
		domainAxis.setUpperMargin(0.06);

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setAutoRange(false);
		rangeAxis.setAutoRangeIncludesZero(true);
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
