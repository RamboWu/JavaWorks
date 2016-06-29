package com.ygkj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by Yang on 2015/3/10.
 */
public class Tools {

	public static void copyFile(String from,String to){
		try {
			FileInputStream in=new FileInputStream(from);
			FileOutputStream out=new FileOutputStream(to);
			FileChannel inChannel=in.getChannel();
			FileChannel outChannel=out.getChannel();
			outChannel.transferFrom(inChannel, 0, inChannel.size());
			inChannel.close();
			outChannel.close();
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
