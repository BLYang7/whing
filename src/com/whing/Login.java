package com.whing;

/**
 * 程序入口页面，每次打开程序最先开始打开此页
 * 此页包含
 * 1、点击登录
 * 2、自动登录（本地已保存用户名和密码的情况下）
 * 3、注册入口
 * 3、Jaccount登录入口
 * 
 */

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.message.ALIAS_TYPE;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;
import com.umeng.message.proguard.k.e;
import com.umeng.update.UmengUpdateAgent;
import com.whing.R;
import com.whing.external.LocalInf;
import com.whing.external.PostData;
import com.whing.external.ShowDialog;
import com.whing.external.URLenum;
import com.whing.imgcut.DownLodeImg;
import com.whing.imgcut.FileUtil;
import com.whing.imgcut.SetUserImage;
import com.whing.security.MD5;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity implements OnClickListener{
	
	//类的全局变量的设定
	EditText loginUserName;  //用户名输入
	EditText loginPassword;  //用户密码输入
	Button loginSignin;   //登录确定点击
	TextView loginRegister, loginJaccount;   //注册入口和Jaccount登录入口
	
	public static PushAgent mPushAgent;  //友盟推送服务
	
	/**
	 * 程序全局公用变量
	 */
	public static String userName;  //用户账户名
	public static String nickName;    //用户昵称
	public static String password;  //用户登录密码
	public static Drawable userImageDrawable;  //用户头像
	
	public static File prfPassword;
	public static File prfToken;
	public static File prfUserId;
	
	public static String imageFilePath;  //用户头像存储路径
	public static String token;  //用户登录的token
	public static String user_id;  //用户id
	
	ProgressDialog pd;
	ShowDialog sd;
	
	private final static int USERNAME = 1;
	private final static int PASSWORD = 2;
	private final static int TOKEN = 3;
	private final static int USERID = 4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		//判断是否有可用的网络，如果没有，则提示
		if ( ! isNetworkAvailable(Login.this) ){
            Toast.makeText(this, "当前网络不可用", Toast.LENGTH_LONG).show();
        }
		
		response();    //开发者回复用户，放在主Activity中，内部调用的是umeng的sdk
		
		userName = LocalInf.getLocalInf(this, USERNAME);
		password = LocalInf.getLocalInf(this, PASSWORD);
		
		UmengUpdateAgent.update(this);    //版本自动检查更新
		
		mPushAgent = PushAgent.getInstance(this);    //友盟推送服务
		mPushAgent.onAppStart();
		mPushAgent.enable();
		
		init();    //初始化各个组件
		
		preNet();    //联网的准备
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		autoLogin();    //自动登陆的设定
		
		setCommonVar();    //设置几个全局变量
		
		setListener();    //设置各组件的监听器
		
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	
	/**
	 * 开发者回复用户反馈的提示
	 */
	private void response(){
		FeedbackAgent agent = new FeedbackAgent(this);
		agent.sync();
	}
	
	
	/**
	 * 获取当前设备的信息
	 * @param context
	 * @return
	 */
	public static String getDeviceInfo(Context context) {
	    try{
	      org.json.JSONObject json = new org.json.JSONObject();
	      android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
	          .getSystemService(Context.TELEPHONY_SERVICE);

	      String device_id = tm.getDeviceId();

	      android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

	      String mac = wifi.getConnectionInfo().getMacAddress();
	      json.put("mac", mac);

	      if( TextUtils.isEmpty(device_id) ){
	        device_id = mac;
	      }

	      if( TextUtils.isEmpty(device_id) ){
	        device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
	      }

	      json.put("device_id", device_id);

	      return json.toString();
	      
	    }catch(Exception e){
	      e.printStackTrace();
	    }
	    
	  return null;
	}
	

	
	/**
	 * 设置各组件的监听
	 */
	private void setListener(){
		
		loginRegister.setOnClickListener(this);
		loginJaccount.setOnClickListener(this);
		loginSignin.setOnClickListener(this);

		//登录点击按钮的点击效果
		loginSignin.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					loginSignin.setBackgroundResource(R.drawable.login_ensure_button_down);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					loginSignin.setBackgroundResource(R.drawable.go);
				}
				return false;
			}
		});

	}

	/**
	 * 初始化各个组件
	 */
	private void init(){
		
		loginUserName = (EditText) findViewById(R.id.username_edit);
		loginPassword = (EditText) findViewById(R.id.password_edit);
		loginSignin = (Button) findViewById(R.id.signin_button);
		loginRegister = (TextView) findViewById(R.id.register_link);
		loginJaccount = (TextView) findViewById(R.id.signin_jaccount);
		
		loginPassword.requestFocus();
		
		String string = LocalInf.getLocalInf(this, USERNAME);
		loginUserName.setText(string);
	
		PushAgent.getInstance(this).onAppStart();    //推送服务的数据统计函数
		
		sd = new ShowDialog(pd);
		
	}
	
	/**
	 * 各个组件的点击监听器，
	 * 使用组件的Id进行选择判决，使用v.getId()完成操作
	 * @param v 组件
	 */
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		
		//注册链接，点击进入注册页面，当前页面关闭
		case R.id.register_link:{
			Intent intent = new Intent(Login.this, BindJaccount.class);
			startActivity(intent);
			finish();
			break;
		}
			
		//jaccount登录链接，点击进入jaccount登录页，当前页面关闭
		case R.id.signin_jaccount:{
			Intent intent2 = new Intent(Login.this, LoginJaccount.class);
			startActivity(intent2);
			finish();
			break;
		}
			
		//登录按键，点击进入验证、登录等程序操作
		case R.id.signin_button:{
			
			new Thread(new Runnable(){
				@Override
				public void run() {
					Looper.prepare();
					sd.showDialog(Login.this, "正在验证");
					Looper.loop();
				}
			}).start();
			
			
			//登录的用户名和密码不为空
			//本地将用户名和密码保存下来
			//如果两项都完成了，就进入下一步，否则无操作
			if(isValidate() 
					&&
					LocalInf.saveData(Login.this, 
							loginUserName.getText().toString().trim(),
							USERNAME)
					&& 
					LocalInf.saveData(Login.this, 
							loginPassword.getText().toString().trim(),
							PASSWORD)
					)
			{
				//将用户名和密码提交，获取服务器返回数据值
				String result = loginPro(URLenum.login,
						loginUserName.getText().toString().trim(),
						loginPassword.getText().toString().trim()
						);
				
				//将从服务器获取的数据以JSON格式解出来，获取其中的token和用户id并保存
				try {
					JSONObject jsonReturn = new JSONObject(result);
					if(!jsonReturn.getString("token").isEmpty())
					{
						//保存返回数据中的token值
						LocalInf.saveData(this,
								jsonReturn.getString("token"), 
								TOKEN);
						
						//保存返回数据中的user_id值
						LocalInf.saveData(this,
								jsonReturn.getString("user_id"),
								USERID);
						
						
						//绑定alias，自定义消息推送
						final String userID = jsonReturn.getString("user_id");
						bindAlias(userID);
						
						setCommonVar();
						
						sd.dismissDialog();
						
						//跳转到大厅页面，关闭当前页面
						Intent intent3=new Intent(Login.this, Hall.class);  
						startActivity(intent3);
						
		                finish();  
					}
					else{
						sd.dismissDialog();
						Toast.makeText(this, "登录出错", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					sd.dismissDialog();
					e.printStackTrace();
				}
				
			}
			else{
				sd.dismissDialog();
			}
			
			break;
		}
		
		default :{
			return;
		}
		
		}
	}
	
	/**
	 * 验证用户名和密码是否符合规范
	 * @return true / false 
	 */
	private boolean isValidate(){
		String userNameString = loginUserName.getText().toString().trim();
		String passwordString = loginPassword.getText().toString().trim();
		
		if(userNameString.equals("")){
			Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(passwordString.equals("")){
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	/**
	 * 将用户的用户名和密码提交给指定的服务器地址进行验证
	 * @param url 目标服务器的地址
	 * @param accountString 用户名字符串
	 * @param passwordString 用户密码字符串
	 * @return 服务器返回的字符串集
	 */
	public static String loginPro(URL url, String accountString, String passwordString){
		
		Map<String, String> map = new HashMap<String, String>();
		
		//获取登录名和登录密码
		String account = accountString.trim();
		String password = passwordString.trim();
		
		//对密码进行MD5加密
		String passwordDigest = MD5.md5(password);
		
		map.put("account", account);
		map.put("password", passwordDigest);
		
		String result = PostData.postData(map, url);
		
		return result;
	}

	/**
	 * 联网的准备工作，
	 * 1、强制主进程内联网
	 * 2、定制目标服务器的URL
	 */
	private void preNet(){
		StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}
	
	
	/**
	 * 当用户名和密码在本地存储的时候，自动登录
	 */
	private void autoLogin(){
		
		//userName和password已经在本地获取，当获取的值不为空时，自动登陆
		if ( ! (userName.equals("") || password.equals("")) ) {
			
			bindAlias(user_id);    //绑定alias
			
			try {
				
				String result = Login.loginPro(URLenum.login, userName, password).trim();
				
				try {
					JSONObject jsonReturn = new JSONObject(result);
					
					//提交用户名和密码给服务器之后的返回值JSON化，
					//如果token不为空，就将token和用户id保存下来
					//保存成功后，跳转到大厅页面，并关闭当前页面
					if(!jsonReturn.getString("token").isEmpty()
							&& 
							LocalInf.saveData(Login.this, 
									userName,
									USERNAME)
							&& 
							LocalInf.saveData(Login.this, 
									password,
									PASSWORD)
							&& 
							LocalInf.saveData(Login.this, 
									jsonReturn.getString("token"),
									TOKEN)
							&&
							LocalInf.saveData(Login.this, 
									jsonReturn.getString("user_id"),
									USERID)
							){
						
						setCommonVar();
						
						Intent intent = new Intent(Login.this, Hall.class);
						startActivity(intent);
						
						finish();
						
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
		}

	}
	
	
	/**
	 * 公用的static类型的几个变量的赋值，
	 * 从文件夹中取出图片或者是从apk源码中取出图片作为用户头像
	 */
	private void setCommonVar(){
		
		userName = LocalInf.getLocalInf(this, USERNAME);
		password = LocalInf.getLocalInf(this, PASSWORD);
		token = LocalInf.getLocalInf(this, TOKEN);
		user_id = LocalInf.getLocalInf(this, USERID);
		
		
		final String str = "&"+ "uid=" + user_id;
		final String personInf = PostData.getData(str, URLenum.get_info);
		JSONObject jsonPersonInf;
		try {
			jsonPersonInf = new JSONObject(personInf);
			nickName = jsonPersonInf.getString("nickname");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		
		//判断头像文件是否存在，当存在时，直接将头像文件赋值给Login常量userImageDrawable
		//当不存在时，新开一个线程，将头像文件从服务器下载下来，然后再作赋值
		
		Drawable drawable = SetUserImage.setImage(this, 
				getResources(),
				user_id, 
				"");
		
		userImageDrawable = drawable;
		
		prfPassword = new File("/data/data/" + getPackageName().toString()
				+ "/shared_prefs", "file_password.xml");
		
		prfToken = new File("/data/data/" + getPackageName().toString()
				+ "/shared_prefs", "file_token.xml");
		
		prfUserId = new File("/data/data/" + getPackageName().toString()
				+ "/shared_prefs", "file_userId.xml");
		
		imageFilePath = Environment.getExternalStorageDirectory() + "/Whing/"
				+ "Image" + "/";
	}
	
	
	/**
	 * 绑定Alias，用户消息推送
	 * @param uid 用户的id
	 */
	private void bindAlias(final String uid){
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					mPushAgent.addAlias(uid, "UID");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	
	/**
     * 检查当前网络是否可用
     * 
     * @param context
     * @return
     */
    
	/**
	 * 判断网络连接是否可用
	 * @param context
	 * @return
	 */
    public boolean isNetworkAvailable(Context context)
    {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = 
        		(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (connectivityManager == null)
        {
            return false;
        }
        else
        {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            
            if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                {
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        
        return false;
        
    }
	
}
