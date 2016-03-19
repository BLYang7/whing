package com.whing.external;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.Key;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class HttpUtil {
//	public static String PATH = "http://whing4.sinaapp.com/api/user/"; // 服务端地址

	public HttpUtil() {
		super();
	}

	/**
	 * 发送消息体到服务端
	 * @param params
	 * @param encode
	 * @return
	 */
	public static String sendPostMessage(Map<String, String> params,String encode, URL url) {
		StringBuilder stringBuilder = new StringBuilder();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				try {
					stringBuilder
							.append(entry.getKey())
							.append("=")
							.append(URLEncoder.encode(entry.getValue(), encode))
							.append("&");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
			try {
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setConnectTimeout(3000);
				urlConnection.setRequestMethod("POST"); // 以post请求方式提交
				urlConnection.setDoInput(true); // 读取数据
				urlConnection.setDoOutput(true); // 向服务器写数据
				
				// 获取上传信息的大小和长度
				byte[] myData = stringBuilder.toString().getBytes();
				
				// 设置请求体的类型是文本类型,表示当前提交的是文本数据
				urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				urlConnection.setRequestProperty("Content-Length",String.valueOf(myData.length));
				
				// 获得输出流，向服务器输出内容
				OutputStream outputStream = urlConnection.getOutputStream();
				
				// 写入数据
				outputStream.write(myData, 0, myData.length);
				outputStream.close();
				
				// 获得服务器响应结果和状态码
				int responseCode = urlConnection.getResponseCode();
				if (responseCode == 200) {
					// 取回响应的结果
					return changeInputStream(urlConnection.getInputStream(),encode);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return "";
	}

	/**
	 * 将一个输入流转换成指定编码的字符串
	 * 
	 * @param inputStream
	 * @param encode
	 * @return
	 */
	private static String changeInputStream(InputStream inputStream,
			String encode) {

		// 内存流
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		String result = null;
		if (inputStream != null) {
			try {
				while ((len = inputStream.read(data)) != -1) {
					byteArrayOutputStream.write(data, 0, len);
				}
				result = new String(byteArrayOutputStream.toByteArray(), encode);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	
	/**
     * 向指定URL发送GET方法的请求
     * 
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(URL url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            
            // 设置通用的请求属性
            // 设置请求体的类型是文本类型,表示当前提交的是文本数据
			connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            
            // 建立实际的连接
            connection.connect();
            
//            // 获取所有响应头字段
//            Map<String, List<String>> map = connection.getHeaderFields();
//            
//            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
//                Log.v("key:" , key + "--->" + map.get(key));
//            }
            
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            
        } catch (Exception e) {
            Log.v("发送GET请求出现异常！" , e+"");
            e.printStackTrace();
        }
        
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
    
    
    public static String sendGet2(URL url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
//            URL realUrl = new URL(urlNameString);
            
            // 打开和URL之间的连接
            URLConnection connection = url.openConnection();
            
            
            // 设置通用的请求属性
            // 设置请求体的类型是文本类型,表示当前提交的是文本数据
			connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            
            // 建立实际的连接
            connection.connect();
            if(!connection.getURL().equals("") && !connection.getURL().equals(null)){
            	Log.v("连接建立", "success");
            }
            
            
            
//            // 获取所有响应头字段
//            Map<String, List<String>> map = connection.getHeaderFields();
//            
//            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
//                Log.v("key:" , key + "--->" + map.get(key));
//            }
            
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            Log.v("输入流", in.readLine() + "000");
            
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            
        } catch (Exception e) {
            Log.v("发送GET请求出现异常！" , e+"");
            e.printStackTrace();
        }
        
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        
        Log.v("结果", result + "000");
        return result;
        
        
    }
    
}
