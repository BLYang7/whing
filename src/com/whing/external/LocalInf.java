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
	 * 保存用户信息到本地的sharePreference中
	 * @return 是否保存成功
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
	        	Log.v("存在本地的userName", "------"+string+"------");
	        }
	        
			break;
		}
		
		case PASSWORD:{
			Editor edit = prf_password.edit();
	        edit.putString("password", string.trim());      
	        edit.commit();
	        
	        if(LOG.DEBUG){
	        	Log.v("存在本地的password", "------"+string+"------");
	        }
	        
			break;
		}
		
		case TOKEN:{
			Editor edit = prf_token.edit();
	        edit.putString("token", string.trim());      
	        edit.commit();
	        
	        if(LOG.DEBUG){
	        	Log.v("存在本地的token", "------"+string+"------");
	        }
	        
			break;
		}
		
		case USERID:{
			Editor edit = prf_userId.edit();
	        edit.putString("userId", string.trim());      
	        edit.commit();
	        
	        if(LOG.DEBUG){
	        	Log.v("存在本地的userId", "------"+string+"------");
	        }
			break;
		}
		
		}
		
        return true;
        
	}
	


	
	/**
	 * 获取本地信息
	 * @param context 当前的View界面
	 * @param flag 标志，1表示返回用户名， 2表示返回用户密码， 3表示返回用户token， 4表示返回用户user_id
	 * @return 具体的返回字符串
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
				Log.v("本地获取的userName", "---" + str + "---");
			}
			
			break;
		}
		
		case PASSWORD:{
			str = prf_password.getString("password", "");
			
			if(LOG.DEBUG){
				Log.v("本地获取的password", "---" + str + "---");
			}
			
			break;
		}
		
		case TOKEN:{
			str = prf_token.getString("token", "");
			
			if(LOG.DEBUG){
				Log.v("本地获取的token", "---" + str + "---");
			}
			
			break;
		}
		
		case USERID:{
			str = prf_userId.getString("userId", "");
			
			if(LOG.DEBUG){
				Log.v("本地获取的userId", "---" + str + "---");
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
