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
//	public static String PATH = "http://whing4.sinaapp.com/api/user/"; // ����˵�ַ

	public HttpUtil() {
		super();
	}

	/**
	 * ������Ϣ�嵽�����
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
				urlConnection.setRequestMethod("POST"); // ��post����ʽ�ύ
				urlConnection.setDoInput(true); // ��ȡ����
				urlConnection.setDoOutput(true); // �������д����
				
				// ��ȡ�ϴ���Ϣ�Ĵ�С�ͳ���
				byte[] myData = stringBuilder.toString().getBytes();
				
				// ������������������ı�����,��ʾ��ǰ�ύ�����ı�����
				urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				urlConnection.setRequestProperty("Content-Length",String.valueOf(myData.length));
				
				// ������������������������
				OutputStream outputStream = urlConnection.getOutputStream();
				
				// д������
				outputStream.write(myData, 0, myData.length);
				outputStream.close();
				
				// ��÷�������Ӧ�����״̬��
				int responseCode = urlConnection.getResponseCode();
				if (responseCode == 200) {
					// ȡ����Ӧ�Ľ��
					return changeInputStream(urlConnection.getInputStream(),encode);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return "";
	}

	/**
	 * ��һ��������ת����ָ��������ַ���
	 * 
	 * @param inputStream
	 * @param encode
	 * @return
	 */
	private static String changeInputStream(InputStream inputStream,
			String encode) {

		// �ڴ���
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
     * ��ָ��URL����GET����������
     * 
     * @param url
     *            ���������URL
     * @param param
     *            ����������������Ӧ���� name1=value1&name2=value2 ����ʽ��
     * @return URL ������Զ����Դ����Ӧ���
     */
    public static String sendGet(URL url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            
            // �򿪺�URL֮�������
            URLConnection connection = realUrl.openConnection();
            
            // ����ͨ�õ���������
            // ������������������ı�����,��ʾ��ǰ�ύ�����ı�����
			connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            
            // ����ʵ�ʵ�����
            connection.connect();
            
//            // ��ȡ������Ӧͷ�ֶ�
//            Map<String, List<String>> map = connection.getHeaderFields();
//            
//            // �������е���Ӧͷ�ֶ�
//            for (String key : map.keySet()) {
//                Log.v("key:" , key + "--->" + map.get(key));
//            }
            
            // ���� BufferedReader����������ȡURL����Ӧ
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            
        } catch (Exception e) {
            Log.v("����GET��������쳣��" , e+"");
            e.printStackTrace();
        }
        
        // ʹ��finally�����ر�������
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
            
            // �򿪺�URL֮�������
            URLConnection connection = url.openConnection();
            
            
            // ����ͨ�õ���������
            // ������������������ı�����,��ʾ��ǰ�ύ�����ı�����
			connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            
            // ����ʵ�ʵ�����
            connection.connect();
            if(!connection.getURL().equals("") && !connection.getURL().equals(null)){
            	Log.v("���ӽ���", "success");
            }
            
            
            
//            // ��ȡ������Ӧͷ�ֶ�
//            Map<String, List<String>> map = connection.getHeaderFields();
//            
//            // �������е���Ӧͷ�ֶ�
//            for (String key : map.keySet()) {
//                Log.v("key:" , key + "--->" + map.get(key));
//            }
            
            // ���� BufferedReader����������ȡURL����Ӧ
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            Log.v("������", in.readLine() + "000");
            
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            
        } catch (Exception e) {
            Log.v("����GET��������쳣��" , e+"");
            e.printStackTrace();
        }
        
        // ʹ��finally�����ر�������
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        
        Log.v("���", result + "000");
        return result;
        
        
    }
    
}
