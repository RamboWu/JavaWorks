package com.ygkj.chart;

import javafx.util.Pair;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yang on 2015/4/1.
 */
public class PieChart {

	String title;
	List<Pair<String,Long>> data;

	public PieChart(String title){
		this.title=title;
		data=new ArrayList<Pair<String, Long>>();
	}

	public void addData(String name,long data){

		this.data.add(new Pair<String, Long>(name,data));
	}

	private DefaultPieDataset generatorDataset(){
		DefaultPieDataset dataset = new DefaultPieDataset();
		for (int i=0;i<data.size();i++){
			dataset.setValue(data.get(i).getKey(),data.get(i).getValue());
		}
		return dataset;
	}

	public void toImage(String file)  {

		JFreeChart chart= ChartFactory.createPieChart(title, generatorDataset(), true, true, false);

		chart.getTitle().setFont(new Font("宋体", Font.BOLD,20));
		chart.getLegend().setItemFont(new Font("宋体", Font.PLAIN, 12));
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setLabelFont(new Font("宋体", 0, 12));
		plot.setNoDataMessage("没有数据");
		plot.setCircular(true);

		StandardPieSectionLabelGenerator standarPieIG = new StandardPieSectionLabelGenerator("{0}\n数量:{1}\n占比:{2}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance());
		plot.setLabelGenerator(standarPieIG);
		plot.setLabelGap(0.02D);
		File out=new File(file);
		try {
			ChartUtilities.saveChartAsJPEG(out, chart, 800, 600);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
