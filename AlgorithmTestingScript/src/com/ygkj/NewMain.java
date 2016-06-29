package com.ygkj;

import com.ygkj.config.Configuration;
import com.ygkj.email.CustomEmail;
import com.ygkj.script.SummaryReport;
import gui.ava.html.image.generator.HtmlImageGenerator;
import org.apache.log4j.PropertyConfigurator;

import javax.mail.MessagingException;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * Created by Yang on 2015/4/10.
 */
public class NewMain {

	static {
		PropertyConfigurator.configure("log4j.properties");
	}

	public static void main(String[] args) throws MessagingException {

		for (int i=0;i<args.length;i++) Configuration.setProperty(args[i]);
		if (!Configuration.init()) return;

		Runner runner=new Runner();
		runner.run();
	}
}
