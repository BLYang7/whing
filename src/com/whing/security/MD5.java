package com.whing.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	
	static MessageDigest message = null;
	
	public static String md5(String string) {

	    byte[] hash;
	    
	    try {
	    	//定义加密方式
	    	message = MessageDigest.getInstance("MD5");
	    	
	    	//获得加密之后的字节数组
	        hash = message.digest(string.getBytes("UTF-8"));

	    } catch (NoSuchAlgorithmException e) {
	        throw new RuntimeException("Huh, MD5 should be supported?", e);
	    } catch (UnsupportedEncodingException e) {
	        throw new RuntimeException("Huh, UTF-8 should be supported?", e);
	    }

	    StringBuilder hex = new StringBuilder(hash.length * 2);
	    for (byte b : hash) {
	        if ((b & 0xFF) < 0x10) hex.append("0");
	        hex.append(Integer.toHexString(b & 0xFF));
	    }
	    
	    return hex.toString();
	}
}
