package com.ygkj.email;

import com.ygkj.config.Configuration;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * Created by Yang on 2015/3/30.
 */
public class CustomEmail {

	private MimeMultipart mailContent = new MimeMultipart();
	private MimeMessage message;
	private StringBuilder emailBuffer=new StringBuilder();
	private MimeBodyPart emailText = new MimeBodyPart();
	private AtomicInteger index=new AtomicInteger(0);

	public CustomEmail(String title,String[] toAddress,String[] ccAddress) throws MessagingException {

		Properties properties=new Properties();
		properties.put("mail.smtp.host", Configuration.smtpHost);
		properties.put("mail.smtp.auth", "true");
		message = new MimeMessage(Session.getInstance(properties,
				new Authenticator() {
					public PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(Configuration.eMailUserName, Configuration.eMailPassword);
					}
				}));

		message.setSubject(title);
		message.setFrom(new InternetAddress(Configuration.eMailUserName));

		InternetAddress[] adrTo=new InternetAddress[toAddress.length];
		for(int i=0;i<toAddress.length;i++){
			adrTo[i]=new InternetAddress(toAddress[i]);
		}
		message.setRecipients(Message.RecipientType.TO,adrTo);

		if (ccAddress!=null) {
			try {
				InternetAddress[] adrCc=new InternetAddress[ccAddress.length];
				for(int i=0;i<ccAddress.length;i++){
					adrCc[i]=new InternetAddress(ccAddress[i]);
				}
				message.setRecipients(Message.RecipientType.CC,adrCc);
			}catch (Exception e){
				e.printStackTrace();
			}
		}

		emailBuffer.append("<p>此邮件为系统自动发送</p>");
		mailContent.addBodyPart(emailText);
	}

	public void addValue(String describe,String value) {

		emailBuffer.append("<p><B><U><font size=\"+2\">").append(describe).append(":</font></U></B><br>")
				.append("<font color=\"red\">").append(value).append("</font></p>");
	}

	public void addImage(String describe,String file) throws MessagingException {

		int id=index.incrementAndGet();
		String imageId=Long.toString(id);
		emailBuffer.append("<p><B><U><font size=\"+1\">").append(describe).append(":</font></U></B><br>")
				.append("<img src='cid:").append(imageId).append("'></p>");

		MimeBodyPart img = new MimeBodyPart();
		DataHandler dh = new DataHandler(new FileDataSource(file));
		img.setDataHandler(dh);
		img.setContentID(imageId);

		mailContent.addBodyPart(img);
	}

	public void addEmailAttach(String file) throws MessagingException {
		File f = new File(file);
		if (!f.exists())
			return;

		MimeBodyPart bodyPart=new MimeBodyPart();
		DataSource dataSource=new FileDataSource(file);
		bodyPart.setDataHandler(new DataHandler(dataSource));
		bodyPart.setFileName(new File(file).getName());

		mailContent.addBodyPart(bodyPart);
	}

	public void addTable(String describe,String file,String sep) {
		StringBuilder buffer=new StringBuilder();
		buffer.append("<table border=\"1\">");
		try {
			BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
			while (true) {
				String data=reader.readLine();
				if (data==null) break;
				String[] cols=data.split(Pattern.quote(sep));
				buffer.append("<tr>");
				for (int i=0;i<cols.length;i++){
					buffer.append("<td>").append(cols[i]).append("</td>");
				}
				buffer.append("</tr>");
			}
			reader.close();
		}catch (IOException e) {
			e.printStackTrace();
		}

		buffer.append("</table>");

		emailBuffer.append("<p><B><U><font size=\"+2\">").append(describe).append(":</font></U></B><br>").append(buffer).append("</p>");
	}

	public void send() throws MessagingException {

		emailText.setContent(emailBuffer.toString(),"text/html;charset=utf-8");
		message.setContent(mailContent);
		message.saveChanges();
		Transport.send(message);
	}
}
