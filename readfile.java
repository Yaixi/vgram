package com.company;

import java.io.*;
import java.util.ArrayList;

public class readfile {
	static String fileName;
	
	public readfile(String fileName){
		this.fileName =fileName;
	}
	
	public  ArrayList readtxt(){
	//	StringBuilder result = new StringBuilder();
		ArrayList result = new ArrayList();;
		try{
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String s = null;
			while((s = br.readLine())!=null){

				result.add(s);
			}
			br.close();    
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

	
}
