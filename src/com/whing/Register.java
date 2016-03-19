package com.whing;

/**
 * 注册页面
 * 当用户绑定Jaccount完成之后，跳转到该页面
 * 该页面内填写用户的个人信息以及用户名和密码
 * 填写完成之后首先检验填写是否有误，其次检验用户帐号是否可用
 * 注册完成之后，保存用户id，保存token，保存用户名和密码，跳转到登陆页面进行自动登录过程
 */

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.whing.external.LocalInf;
import com.whing.external.PostData;
import com.whing.external.URLenum;
import com.whing.security.MD5;
import com.whing.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Activity implements OnClickListener{
	
	Button registerButtonLeft, registerEnsure;
	EditText registerUserName, registerPassword, registerPasswordAgain,
			registerUserMobile, registerUserGender;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		init();    //组件初始化
		
		preNet();    //联网之前的准备
		
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
	 * 组件初始化
	 */
	private void init(){
		registerButtonLeft = (Button) findViewById(R.id.register_navigation_left);
		registerEnsure = (Button) findViewById(R.id.register_ensure);
		registerUserName = (EditText) findViewById(R.id.register_user_account);
		registerUserMobile = (EditText) findViewById(R.id.register_user_mobile);
		registerUserGender = (EditText) findViewById(R.id.register_user_gender);
		registerPassword = (EditText) findViewById(R.id.register_user_password);
		registerPasswordAgain = (EditText) findViewById(R.id.register_user_password_again);
		
		PushAgent.getInstance(this).onAppStart();
		
		registerUserName.setText(getIntent().getExtras().getString("account"));
		registerUserName.setFocusable(false);
	}
	
	/**
	 * 联网之前的准备
	 * 强制主进程内联网 
	 */
	private void preNet(){
		StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}
	
	
	/**
	 * 设置各个组件的监听器
	 */
	private void setListener(){
		registerButtonLeft.setOnClickListener(this);
		registerEnsure.setOnClickListener(this);
		registerUserGender.setOnClickListener(this);
		
		registerEnsure.setOnTouchListener(touchListener);
		
	}
	
	/**
	 * 设置button在touchlistener时的背景响应
	 */
	private OnTouchListener touchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			if(event.getAction() == MotionEvent.ACTION_DOWN){
				registerEnsure.setBackgroundResource(R.drawable.radio_button_new_one_down);
			}
			else if(event.getAction() == MotionEvent.ACTION_UP){
				registerEnsure.setBackgroundResource(R.drawable.radio_button_new_one_normal);
			}
			
			return false;
		}
	};
	
	
	/**
	 * 各种点击事件
	 */
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		
		case R.id.register_navigation_left : {
			finish();
			break;
		}
		
		case R.id.register_ensure : {
			
			//确认注册时，首先检验输入是否符合要求
			//检验完成之后向register服务器提交数据，获取返回值
			//判断返回值的数据
			//当返回值中的数据符合条件时，保存userId和token，保存账号和密码，然后提示用户注册完成
			if(isValidate()){
				
				String result = PostData.postData(getInf(), URLenum.register);
				Log.v("result", result);
				
				try {
					JSONObject json = new JSONObject(result.trim());
					
					try{
						int errcode = json.getInt("errcode");
						if (errcode == 140) {
							Toast.makeText(this, "无效的电话号码", Toast.LENGTH_SHORT).show();
						}
					}catch(Exception e){
						
						String user_id = json.getString("user_id");
						String token = json.getString("token");

						if ( ! user_id.equals(null) ) {
							
							LocalInf.saveData(this, 
									token,
									3);
							
							LocalInf.saveData(this,
									user_id,
									4);
							
							LocalInf.saveData(this,
									registerUserName.getText().toString().trim(),
									1);
							
							LocalInf.saveData(this,
									registerPassword.getText().toString().trim(),
									2);
							
							showSimpleDialog(v);
							
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			break;
		}
		
		
		case R.id.register_user_gender:{
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			
			builder.setPositiveButton("男", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					registerUserGender.setText("男");
				}
			});
			builder.setNegativeButton("女", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					registerUserGender.setText("女");
				}
			});
			builder.create().show();
			
			break;
		}
		
		default : {
			return;
		}
		
		}
	
	}
	
	/**
	 * 检查用户输入是否有效
	 * @return 检查结果
	 */
	private boolean isValidate(){
		String account = registerUserName.getText().toString().trim();
		String password = registerPassword.getText().toString().trim();
		String passwordAgain = registerPasswordAgain.getText().toString().trim();
		String mobile = registerUserMobile.getText().toString().trim();
		String gender = registerUserGender.getText().toString().trim();
		
		//检查两次密码输入是否一致
		if(account.equals("") || account.equals(null)){
			Toast.makeText(this, "账号不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		else if(mobile.equals("") || mobile.equals(null)){
			Toast.makeText(this, "电话不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		else if(gender.equals("") || gender.equals(null)){
			Toast.makeText(this, "性别不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		else if(password.equals("") || password.equals(null)){
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		else if( ! password.equals(passwordAgain) ){
			Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		else {
			return checkAccount(this, account);
		}
		
	}
	
	/**
	 * 用户名检验，查看是否可以使用
	 * @param context 活动的Activity
	 * @param account 账号
	 * @return 是否可以使用 true / false 
	 */
	public static boolean checkAccount(Context context, String account){
		
		Map<String, String> map = new HashMap<String, String> ();
		map.put("account", account);
		
		String checkAccount = PostData.postData(map, URLenum.check_account);
		
		int errcode = 0;
		try {
			JSONObject json = new JSONObject(checkAccount);
			errcode = Integer.parseInt(json.get("errcode").toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(errcode == 124){
			Toast.makeText(context, "用户名至少六位", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		else if(errcode == 125){
			Toast.makeText(context, "用户名至多十六位", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		else if(errcode == 103){
			Toast.makeText(context, "用户名已存在", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		else if(errcode == 100){
			return true;
		}
		
		else{
			Log.v("有啥错？", errcode+"");
			return false;
		}
	}
	
	/**
	 * 将用户的信息装到map中
	 * 需要封装的信息包括用户账号、用户密码、用户电话、用户性别、时间戳
	 * @return 封装好的Map
	 */
	private Map<String, String> getInf(){
		
		String gender = registerUserGender.getText().toString().trim();
		
		Map<String, String> map = new HashMap<String, String>();
		
		//map.put("account", registerUserName.getText().toString().trim());
		map.put("password", MD5.md5(registerPassword.getText().toString().trim()));
		map.put("mobile", registerUserMobile.getText().toString().trim());
		
		if(gender.equals("男")){
			map.put("sex", "1");
		}
		else if(gender.equals("女")){
			map.put("sex", "0");
		}
		
		map.put("time", getIntent().getExtras().getString("time"));
		
		return map;
		
	}
	
	
	/**
	 * 消息提示框，弹出，需要点击确定
	 */
	public void showSimpleDialog(View view) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setPositiveButton("---注册成功---", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				//提示用户跳转
				new Thread(new Runnable(){
					@Override
					public void run() {
						Looper.prepare();
						Toast.makeText(Register.this, "正在跳转", Toast.LENGTH_LONG).show();
						Looper.loop();
					}
				}).start();
				
				Intent intent = new Intent(Register.this, Login.class);
				startActivity(intent);
				finish();
			}
		});
		
		builder.create().show();
		
    }
	
}














