package com.whing.external;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.whing.Hall;
import com.whing.Login;
import com.whing.security.AES;
import com.whing.security.GetTime;
import com.whing.security.MD5;

public class PostData {
	
	/**
	 * ��GET��ʽ�ӷ�������ȡ�û���Ϣ
	 * @param user_id ��Ҫ��ȡ���û���id
	 * @param url Ŀ���������URL
	 * @return ���ط������ķ����ַ���
	 */
	public static String getData(final String string, final URL url){
		
		String str = url + "?" + "user_id=" + Login.user_id 
				+ "&" + "token=" + Login.token + "&" + "timestamp=" + GetTime.getBigTime();
		String sign = MD5.md5(str);
		String param = "user_id=" + Login.user_id + "&" + "timestamp=" + GetTime.getBigTime()
				+ "&" + "sign=" + sign + string;
		String result = HttpUtil.sendGet(url, param);
		Log.v("get��Ϣ�ķ���", result);
		return result;

	}
	

	/**
	 * �����ݽ��д���֮����ָ��URL����
	 * @param map K-V�����������Ҫ���͵���Ϣ
	 * @param url ָ��Ŀ�ĵصĵ�ַ
	 * @return
	 */
	public static String postData(Map<String, String> map, URL url){
		//��ȡ��ǰʱ���
		String currentTime = GetTime.getTime().trim();
		Log.v("ʱ�����", currentTime);
		
		//���û���Ϣ���м��ܴ���
		String data =encryptInf(currentTime, map.toString());
		
		//������֮���������Ϣ���ݸ�������������ȡ������Ϣ
		map.clear();
		map.put("time", currentTime);
		map.put("data", data);
		Log.v("���ݸ������������ݣ�", map.toString());
		
		String result = HttpUtil.sendPostMessage(map, "UTF-8", url);
		Log.v("�ӷ��������ص����ݣ�", result);
		
		String str = decryptInf(result);
		
		return str;
	}
	
	/**
	 * �����ݴ���֮����ָ����URL����
	 * @param map K-V�����������Ҫ���͵���Ϣ
	 * @param url Ŀ��URL
	 * @param token ���ص�tokenֵ
	 * @return ���ط������ķ����ַ���
	 */
	public static String postData(Map<String, String> map, URL url, String token){
		//��ȡ��ǰʱ���
		String currentTime = GetTime.getTime().trim();
		Log.v("ʱ�����", currentTime);
		
		//���û���Ϣ���м��ܴ���
		String data =encryptUseToken(token, map.toString());
		
		String key = MD5.md5("time=" + currentTime).substring(0, 16);
		
		//������֮���������Ϣ���ݸ�������������ȡ������Ϣ
		map.clear();
		map.put("time", currentTime);
		map.put("data", data);
		map.put("token", AES.encrypt(token, key));
		Log.v("���ݸ������������ݣ�", map.toString());
		
		String result = HttpUtil.sendPostMessage(map, "UTF-8", url);
		Log.v("�ӷ��������ص����ݣ�", result);
		
		String str = decryptInf(result);
		
		return str;
	}
	
	
	/**
	 * ���û���Ϣ����AES���ܲ���Base64����
	 * @param keyString ������ȡ������Կ
	 * @param data �û��ύ����Ҫ���ܵ�����
	 * @return ���ؼ���֮�������
	 */
	public static String encryptInf(String keyString, String data) {
		
		JSONObject json = null;
		try {
			json = new JSONObject(data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// ��ȡ��ǰ����Կ�����ַ��� "time="+currentTime ����MD5����֮��Ľ�ȡ
		String key = MD5.md5("time=" + keyString).substring(0, 16);
		Log.v("��Կ��", key);
		
		// ���û������û��������AES���ܣ���ԿΪ�����key
		Log.v("�ַ���", json.toString());
		
		String aesEncrypt = AES.encrypt(json.toString(), key);
		Log.v("AES����֮��", aesEncrypt);
		
		return aesEncrypt;
	}
	
	
	/**
	 * ���û���Ϣ����AES���ܲ���Base64����
	 * @param keyString ������ȡ������Կ
	 * @param data �û��ύ����Ҫ���ܵ�����
	 * @return ���ؼ���֮�������
	 */
	public static String encryptUseToken(String token, String data) {
		
		JSONObject json = null;
		try {
			json = new JSONObject(data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// ���û������û��������AES���ܣ���ԿΪ�����key
		Log.v("�ַ���", json.toString());
		
		String aesEncrypt = AES.encrypt(json.toString(), token);
		Log.v("AES����֮��", aesEncrypt);
		
		return aesEncrypt;
	}
	
	
	public static String decryptInf(String result){
		
		//��÷��ص�data���ݣ���Base64����
		try {
			JSONObject json = new JSONObject(result);
			
			int errcode = (Integer) json.get("errcode");
			
			if(errcode == 0){
				
				String data = json.getString("data").trim();
				Log.v("�ӷ���������ȡ����data����", data);
				
				String time = json.getString("time");
				String key = MD5.md5("time=" + time).substring(0, 16);
				
				//���ֽ��������AES����
				String str = AES.decrypt(data, key);
				Log.v("AES����:", str);
				
				//������֮����������
				return str;
			}
			else{
				return result;
			}
			
			
		} catch (JSONException e) {
			return result;
		}
	}
	
	
	
	public static String postListData(List<HashMap<String, Object>> mData, URL url, String token){
		//��ȡ��ǰʱ���
		String currentTime = GetTime.getTime().trim();
		Log.v("ʱ�����", currentTime);
		
		List<String> list = new ArrayList<String> ();
		
		for(int i=0; i<mData.size(); i++){
			HashMap<String, Object> map = new HashMap<String, Object>();
			map = mData.get(i);
			
			try {
				JSONObject json = new JSONObject(map.toString());
				list.add(json.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		String string = list.toString();
		Log.v("���ݵ�string", string);
		
		//���û���Ϣ���м��ܴ���
//		String data =encryptUseToken(token, mData.toString());
		String data = AES.encrypt(string, token);
		
		String key = MD5.md5("time=" + currentTime).substring(0, 16);
		
		//������֮���������Ϣ���ݸ�������������ȡ������Ϣ
		Map<String, String> map = new HashMap<String, String>();
		map.put("time", currentTime);
		map.put("data", data);
		map.put("token", AES.encrypt(token, key));
		Log.v("���ݸ������������ݣ�", map.toString());
		
		String result = HttpUtil.sendPostMessage(map, "UTF-8", url);
		Log.v("�ӷ��������ص����ݣ�", result);
		
		String str = decryptInf(result);
		
		return str;
	}
	
	
	
}
