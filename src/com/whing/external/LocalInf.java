package com.whing.external;

import com.loopj.android.http.BuildConfig;
import com.whing.debug.LOG;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class LocalInf {
	
	private static final int USERNAME = 1;
	private static final int PASSWORD = 2;
	private static final int TOKEN = 3;
	private static final int USERID = 4;
	
	/**
	 * �����û���Ϣ�����ص�sharePreference��
	 * @return �Ƿ񱣴�ɹ�
	 */
	public static boolean saveData(Context context, String string, int flag){
		SharedPreferences prf_userName = context.getSharedPreferences("file_userName", Context.MODE_PRIVATE);
		SharedPreferences prf_password = context.getSharedPreferences("file_password", Context.MODE_PRIVATE);
		SharedPreferences prf_token = context.getSharedPreferences("file_token", Context.MODE_PRIVATE);
		SharedPreferences prf_userId = context.getSharedPreferences("file_userId", Context.MODE_PRIVATE);
		
		switch(flag){
		
		case USERNAME:{
			Editor edit = prf_userName.edit();
	        edit.putString("userName", string.trim());      
	        edit.commit();
	        
	        if(LOG.DEBUG){
	        	Log.v("���ڱ��ص�userName", "------"+string+"------");
	        }
	        
			break;
		}
		
		case PASSWORD:{
			Editor edit = prf_password.edit();
	        edit.putString("password", string.trim());      
	        edit.commit();
	        
	        if(LOG.DEBUG){
	        	Log.v("���ڱ��ص�password", "------"+string+"------");
	        }
	        
			break;
		}
		
		case TOKEN:{
			Editor edit = prf_token.edit();
	        edit.putString("token", string.trim());      
	        edit.commit();
	        
	        if(LOG.DEBUG){
	        	Log.v("���ڱ��ص�token", "------"+string+"------");
	        }
	        
			break;
		}
		
		case USERID:{
			Editor edit = prf_userId.edit();
	        edit.putString("userId", string.trim());      
	        edit.commit();
	        
	        if(LOG.DEBUG){
	        	Log.v("���ڱ��ص�userId", "------"+string+"------");
	        }
			break;
		}
		
		}
		
        return true;
        
	}
	


	
	/**
	 * ��ȡ������Ϣ
	 * @param context ��ǰ��View����
	 * @param flag ��־��1��ʾ�����û����� 2��ʾ�����û����룬 3��ʾ�����û�token�� 4��ʾ�����û�user_id
	 * @return ����ķ����ַ���
	 */
	
	public static String getLocalInf(Context context, int flag){
		
		SharedPreferences prf_userName = context.getSharedPreferences("file_userName", Context.MODE_PRIVATE);
		SharedPreferences prf_password = context.getSharedPreferences("file_password", Context.MODE_PRIVATE);
		SharedPreferences prf_token = context.getSharedPreferences("file_token", Context.MODE_PRIVATE);
		SharedPreferences prf_userId = context.getSharedPreferences("file_userId", Context.MODE_PRIVATE);
		
		String str;
		
		switch(flag){
		
		case USERNAME:{
			str = prf_userName.getString("userName", "");
			
			if(LOG.DEBUG){
				Log.v("���ػ�ȡ��userName", "---" + str + "---");
			}
			
			break;
		}
		
		case PASSWORD:{
			str = prf_password.getString("password", "");
			
			if(LOG.DEBUG){
				Log.v("���ػ�ȡ��password", "---" + str + "---");
			}
			
			break;
		}
		
		case TOKEN:{
			str = prf_token.getString("token", "");
			
			if(LOG.DEBUG){
				Log.v("���ػ�ȡ��token", "---" + str + "---");
			}
			
			break;
		}
		
		case USERID:{
			str = prf_userId.getString("userId", "");
			
			if(LOG.DEBUG){
				Log.v("���ػ�ȡ��userId", "---" + str + "---");
			}
			
			break;
		}
		
		default:{
			str = "";
			break;
		}

		}
		
		return str;
	}
	
}
