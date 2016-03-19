package com.whing.security;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;


public class AES {
	
	private static final String AESTYPE ="AES/ECB/PKCS5Padding"; 

	/**
	 * ����
	 * @param data �����ܵ��ַ���
	 * @param password ������Կ
	 * @return ����֮����ַ���
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
	 * �Լ���֮����ַ������н��ܴ���
	 * @param encryptStr ����֮����ַ���
	 * @param password ������Կ
	 * @return ����֮����ַ���
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
	 * ��AES����֮����ֽ��������ַ���
	 * <p>������ֽڱ��뷽ʽ��Base64</p>
	 * @param encrypt ����֮����ֽ�����
	 * @return �����ַ���
	 */
	public static String base64Encode(byte[] encrypt){
		return Base64.encodeToString(encrypt, Base64.DEFAULT);
	}
	
	/**
	 * ��AES���ܲ�����֮����ַ���������ֽ�����
	 * @param encryptStr ����֮����ַ���
	 * @return Base64������ֽ�����
	 */
	public static byte[] base64Decode(String encryptStr){
		return Base64.decode(encryptStr, Base64.DEFAULT);
	}
	
	
}
