package com.whing.security;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;


public class AES {
	
	private static final String AESTYPE ="AES/ECB/PKCS5Padding"; 

	/**
	 * 加密
	 * @param data 待加密的字符串
	 * @param password 加密密钥
	 * @return 加密之后的字符串
	 */
	public static String encrypt(String data, String password) { 
        byte[] encrypt = null; 
        try{ 
            Key key = generateKey(password); 
            Cipher cipher = Cipher.getInstance(AESTYPE); 
            cipher.init(Cipher.ENCRYPT_MODE, key); 
            encrypt = cipher.doFinal(data.getBytes());     
        }catch(Exception e){ 
            e.printStackTrace(); 
        }
        return base64Encode(encrypt);
    } 
 
	/**
	 * 对加密之后的字符串进行解密处理
	 * @param encryptStr 加密之后的字符串
	 * @param password 解密密钥
	 * @return 解密之后的字符串
	 */
	
	public static String decrypt(String encryptData, String keyStr) {
        byte[] decrypt = null; 
        try{ 
            Key key = generateKey(keyStr); 
            Cipher cipher = Cipher.getInstance(AESTYPE); 
            cipher.init(Cipher.DECRYPT_MODE, key); 
            decrypt = cipher.doFinal(Base64.decode(encryptData, Base64.DEFAULT)); 
        }catch(Exception e){ 
            e.printStackTrace(); 
        } 
        return new String(decrypt).trim(); 
    } 
	
	
    private static Key generateKey(String key)throws Exception{ 
        try{            
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES"); 
            return keySpec; 
        }catch(Exception e){ 
            e.printStackTrace(); 
            throw e; 
        } 
    } 
    
	/**
	 * 将AES加密之后的字节数组变成字符串
	 * <p>这里的字节编码方式是Base64</p>
	 * @param encrypt 加密之后的字节数组
	 * @return 返回字符串
	 */
	public static String base64Encode(byte[] encrypt){
		return Base64.encodeToString(encrypt, Base64.DEFAULT);
	}
	
	/**
	 * 将AES加密并编码之后的字符串解码成字节数组
	 * @param encryptStr 编码之后的字符串
	 * @return Base64解码的字节数组
	 */
	public static byte[] base64Decode(String encryptStr){
		return Base64.decode(encryptStr, Base64.DEFAULT);
	}
	
	
}
