package com.ygkj;

import java.io.File;

/**
 * Created by Yang on 2015/5/22.
 */
public class BusFileSort {

	public BusFileSort() {

	}

	public String getSortFileList(String dir,String input){

		File parent=new File(dir);
		File[] fileList=parent.listFiles();

		for (int i=0;i<fileList.length;i++)
			for (int j=i+1;j<fileList.length;j++){
				if (fileList[i].lastModified()>fileList[j].lastModified()){
					File temp=fileList[i];
					fileList[i]=fileList[j];
					fileList[j]=temp;
				}
			}


		StringBuilder buffer=new StringBuilder();
		int counter=0;
		for (int i=0;i<fileList.length;i++){
			if (fileList[i].getName().contains(input)){
				if (counter>0) buffer.append(";");
				buffer.append(fileList[i].getPath());
				counter++;
			}
		}

		return buffer.toString();

	}
}
