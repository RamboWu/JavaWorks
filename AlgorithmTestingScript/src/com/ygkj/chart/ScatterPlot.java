package com.ygkj.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultXYDataset;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yang on 2015/3/25.
 */
public class ScatterPlot {

	class Pair {
		double y;
		double x;

		public Pair(double x,double y) {
			this.x=x;
			this.y=y;
		}
	}

	List<Pair> data=new ArrayList<Pair>();
	String title;
	String xName;
	String yName;

	public ScatterPlot(String title,String xName,String yName) {
		this.title=title;
		this.xName=xName;
		this.yName=yName;
	}

	public void addData(double x,double y) {
		data.add(new Pair(x,y));
	}

	private DefaultXYDataset generatorDataSet() {
		DefaultXYDataset dataSet = new DefaultXYDataset ();

		double[][] datas=new double[2][data.size()];

		for (int i=0;i<data.size();i++) {
			datas[0][i]=data.get(i).x;
			datas[1][i]=data.get(i).y;
		}

		dataSet.addSeries(title, datas);
		return dataSet;
	}

	public void toImage(String outFile) {

		DefaultXYDataset dataSet=generatorDataSet();

		JFreeChart chart = ChartFactory.createScatterPlot(title,xName, yName, dataSet, PlotOrientation.VERTICAL, true, false,false);
		chart.setBackgroundPaint(Color.white);
		chart.getTitle().setFont(new Font("宋体", Font.BOLD,12));
		chart.getLegend().setItemFont(new Font("宋体", Font.PLAIN, 12));

		XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.black);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.BLACK);
		plot.setNoDataMessage("没有数据");

		ValueAxis axis=plot.getDomainAxis();
		axis.setLabelFont(new Font("宋体", Font.PLAIN, 12));
		axis.setTickLabelFont(new Font("宋体", Font.PLAIN, 12));

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
