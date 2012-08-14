package com.mmmeff.ez;

public class Recovery {
	public String filename;
	public String name;
	
	public Recovery(String filename, String name){
		this.filename = filename;
		this.name = name;
	}
	
	public enum ImageType{
		RECOVERY, HYBRID;
	}
}
