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
	 * 以GET方式从服务器获取用户信息
	 * @param user_id 需要获取的用户的id
	 * @param url 目标服务器的URL
	 * @return 返回服务器的反馈字符集
	 */
	public static String getData(final String string, final URL url){
		
		String str = url + "?" + "user_id=" + Login.user_id 
				+ "&" + "token=" + Login.token + "&" + "timestamp=" + GetTime.getBigTime();
		String sign = MD5.md5(str);
		String param = "user_id=" + Login.user_id + "&" + "timestamp=" + GetTime.getBigTime()
				+ "&" + "sign=" + sign + string;
		String result = HttpUtil.sendGet(url, param);
		Log.v("get信息的返回", result);
		return result;

	}
	

	/**
	 * 对数据进行处理之后像指定URL发送
	 * @param map K-V变量，存放需要传送的信息
	 * @param url 指定目的地的地址
	 * @return
	 */
	public static String postData(Map<String, String> map, URL url){
		//获取当前时间戳
		String currentTime = GetTime.getTime().trim();
		Log.v("时间戳：", currentTime);
		
		//对用户信息进行加密处理
		String data =encryptInf(currentTime, map.toString());
		
		//将加密之后的数据信息传递给服务器，并获取返回信息
		map.clear();
		map.put("time", currentTime);
		map.put("data", data);
		Log.v("传递给服务器的数据：", map.toString());
		
		String result = HttpUtil.sendPostMessage(map, "UTF-8", url);
		Log.v("从服务器返回的数据：", result);
		
		String str = decryptInf(result);
		
		return str;
	}
	
	/**
	 * 将数据处理之后向指定的URL发送
	 * @param map K-V变量，存放需要传送的信息
	 * @param url 目标URL
	 * @param token 本地的token值
	 * @return 返回服务器的反馈字符集
	 */
	public static String postData(Map<String, String> map, URL url, String token){
		//获取当前时间戳
		String currentTime = GetTime.getTime().trim();
		Log.v("时间戳：", currentTime);
		
		//对用户信息进行加密处理
		String data =encryptUseToken(token, map.toString());
		
		String key = MD5.md5("time=" + currentTime).substring(0, 16);
		
		//将加密之后的数据信息传递给服务器，并获取返回信息
		map.clear();
		map.put("time", currentTime);
		map.put("data", data);
		map.put("token", AES.encrypt(token, key));
		Log.v("传递给服务器的数据：", map.toString());
		
		String result = HttpUtil.sendPostMessage(map, "UTF-8", url);
		Log.v("从服务器返回的数据：", result);
		
		String str = decryptInf(result);
		
		return str;
	}
	
	
	/**
	 * 对用户信息进行AES加密并且Base64编码
	 * @param keyString 用来获取加密密钥
	 * @param data 用户提交的需要加密的数据
	 * @return 返回加密之后的数据
	 */
	public static String encryptInf(String keyString, String data) {
		
		JSONObject json = null;
		try {
			json = new JSONObject(data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// 获取当前的密钥，是字符串 "time="+currentTime 经过MD5加密之后的截取
		String key = MD5.md5("time=" + keyString).substring(0, 16);
		Log.v("密钥：", key);
		
		// 对用户名和用户密码进行AES加密，密钥为上面的key
		Log.v("字符串", json.toString());
		
		String aesEncrypt = AES.encrypt(json.toString(), key);
		Log.v("AES加密之后：", aesEncrypt);
		
		return aesEncrypt;
	}
	
	
	/**
	 * 对用户信息进行AES加密并且Base64编码
	 * @param keyString 用来获取加密密钥
	 * @param data 用户提交的需要加密的数据
	 * @return 返回加密之后的数据
	 */
	public static String encryptUseToken(String token, String data) {
		
		JSONObject json = null;
		try {
			json = new JSONObject(data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// 对用户名和用户密码进行AES加密，密钥为上面的key
		Log.v("字符串", json.toString());
		
		String aesEncrypt = AES.encrypt(json.toString(), token);
		Log.v("AES加密之后：", aesEncrypt);
		
		return aesEncrypt;
	}
	
	
	public static String decryptInf(String result){
		
		//获得返回的data数据，并Base64解密
		try {
			JSONObject json = new JSONObject(result);
			
			int errcode = (Integer) json.get("errcode");
			
			if(errcode == 0){
				
				String data = json.getString("data").trim();
				Log.v("从返回数据中取出的data数据", data);
				
				String time = json.getString("time");
				String key = MD5.md5("time=" + time).substring(0, 16);
				
				//对字节数组进行AES解密
				String str = AES.decrypt(data, key);
				Log.v("AES解码:", str);
				
				//将解码之后的数据输出
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
		//获取当前时间戳
		String currentTime = GetTime.getTime().trim();
		Log.v("时间戳：", currentTime);
		
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
		Log.v("传递的string", string);
		
		//对用户信息进行加密处理
//		String data =encryptUseToken(token, mData.toString());
		String data = AES.encrypt(string, token);
		
		String key = MD5.md5("time=" + currentTime).substring(0, 16);
		
		//将加密之后的数据信息传递给服务器，并获取返回信息
		Map<String, String> map = new HashMap<String, String>();
		map.put("time", currentTime);
		map.put("data", data);
		map.put("token", AES.encrypt(token, key));
		Log.v("传递给服务器的数据：", map.toString());
		
		String result = HttpUtil.sendPostMessage(map, "UTF-8", url);
		Log.v("从服务器返回的数据：", result);
		
		String str = decryptInf(result);
		
		return str;
	}
	
	
	
}
