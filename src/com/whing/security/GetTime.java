package com.whing.security;


public class GetTime {
	public static String getTime(){
		long millSecond = System.currentTimeMillis();
		long second = millSecond/1000;
		return second + "";
	}
	
	public static String getBigTime(){
		long millSecond = System.currentTimeMillis();
		long second = millSecond/1000 + 60;
		return second + "";
	}
	
}
