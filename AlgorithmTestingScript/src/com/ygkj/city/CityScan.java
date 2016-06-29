package com.ygkj.city;

import com.ygkj.BusFileSort;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Yang on 2015/5/27.
 */
public class CityScan {

	List<CityMeta> citys;

	public CityScan(){
		citys =new ArrayList<CityMeta>();
	}

	public void addFile(String inputDir,String input,String cityDir,String cityLabel,String cityName){
		CityMeta meta=new CityMeta();
		meta.cityDir=cityDir;
		meta.cityLabel=cityLabel;
		meta.cityName=cityName;
		meta.input=input;
		meta.inputDir=inputDir;
		this.citys.add(meta);
	}

	public boolean hasFile() {
		if (citys.size()>0) return true;
		else return false;
	}

	public CityMeta getExistFile() {
		int index=-1;
		for (int i=0;i< citys.size();i++){
			CityMeta meta= citys.get(i);
			BusFileSort busDataSort=new BusFileSort();
			meta.fileString=busDataSort.getSortFileList(meta.inputDir,meta.input);
			if (meta.fileString.length()>0){
				String[] files=meta.fileString.split(Pattern.quote(";"));
				boolean flag=true;
				for (int k=0;k<files.length;k++){
					File temp=new File(files[k]);
					if (!temp.exists()){
						flag=false;
						break;
					}
				}
				if (flag){
					index=i;
					break;
				}
			}
		}

		if (index==-1) return null;
		CityMeta result= citys.get(index);
		citys.remove(index);
		return result;
	}
}
