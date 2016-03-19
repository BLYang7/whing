package com.whing;

/**
 * ע��ҳ��
 * ���û���Jaccount���֮����ת����ҳ��
 * ��ҳ������д�û��ĸ�����Ϣ�Լ��û���������
 * ��д���֮�����ȼ�����д�Ƿ�������μ����û��ʺ��Ƿ����
 * ע�����֮�󣬱����û�id������token�������û��������룬��ת����½ҳ������Զ���¼����
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
		
		init();    //�����ʼ��
		
		preNet();    //����֮ǰ��׼��
		
		setListener();    //���ø�����ļ�����
		
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
	 * �����ʼ��
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
	 * ����֮ǰ��׼��
	 * ǿ�������������� 
	 */
	private void preNet(){
		StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}
	
	
	/**
	 * ���ø�������ļ�����
	 */
	private void setListener(){
		registerButtonLeft.setOnClickListener(this);
		registerEnsure.setOnClickListener(this);
		registerUserGender.setOnClickListener(this);
		
		registerEnsure.setOnTouchListener(touchListener);
		
	}
	
	/**
	 * ����button��touchlistenerʱ�ı�����Ӧ
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
	 * ���ֵ���¼�
	 */
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		
		case R.id.register_navigation_left : {
			finish();
			break;
		}
		
		case R.id.register_ensure : {
			
			//ȷ��ע��ʱ�����ȼ��������Ƿ����Ҫ��
			//�������֮����register�������ύ���ݣ���ȡ����ֵ
			//�жϷ���ֵ������
			//������ֵ�е����ݷ�������ʱ������userId��token�������˺ź����룬Ȼ����ʾ�û�ע�����
			if(isValidate()){
				
				String result = PostData.postData(getInf(), URLenum.register);
				Log.v("result", result);
				
				try {
					JSONObject json = new JSONObject(result.trim());
					
					try{
						int errcode = json.getInt("errcode");
						if (errcode == 140) {
							Toast.makeText(this, "��Ч�ĵ绰����", Toast.LENGTH_SHORT).show();
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
			
			builder.setPositiveButton("��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					registerUserGender.setText("��");
				}
			});
			builder.setNegativeButton("Ů", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					registerUserGender.setText("Ů");
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
	 * ����û������Ƿ���Ч
	 * @return �����
	 */
	private boolean isValidate(){
		String account = registerUserName.getText().toString().trim();
		String password = registerPassword.getText().toString().trim();
		String passwordAgain = registerPasswordAgain.getText().toString().trim();
		String mobile = registerUserMobile.getText().toString().trim();
		String gender = registerUserGender.getText().toString().trim();
		
		//����������������Ƿ�һ��
		if(account.equals("") || account.equals(null)){
			Toast.makeText(this, "�˺Ų���Ϊ��", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		else if(mobile.equals("") || mobile.equals(null)){
			Toast.makeText(this, "�绰����Ϊ��", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		else if(gender.equals("") || gender.equals(null)){
			Toast.makeText(this, "�Ա���Ϊ��", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		else if(password.equals("") || password.equals(null)){
			Toast.makeText(this, "���벻��Ϊ��", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		else if( ! password.equals(passwordAgain) ){
			Toast.makeText(this, "�������벻һ��", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		else {
			return checkAccount(this, account);
		}
		
	}
	
	/**
	 * �û������飬�鿴�Ƿ����ʹ��
	 * @param context ���Activity
	 * @param account �˺�
	 * @return �Ƿ����ʹ�� true / false 
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
			Toast.makeText(context, "�û���������λ", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		else if(errcode == 125){
			Toast.makeText(context, "�û�������ʮ��λ", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		else if(errcode == 103){
			Toast.makeText(context, "�û����Ѵ���", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		else if(errcode == 100){
			return true;
		}
		
		else{
			Log.v("��ɶ��", errcode+"");
			return false;
		}
	}
	
	/**
	 * ���û�����Ϣװ��map��
	 * ��Ҫ��װ����Ϣ�����û��˺š��û����롢�û��绰���û��Ա�ʱ���
	 * @return ��װ�õ�Map
	 */
	private Map<String, String> getInf(){
		
		String gender = registerUserGender.getText().toString().trim();
		
		Map<String, String> map = new HashMap<String, String>();
		
		//map.put("account", registerUserName.getText().toString().trim());
		map.put("password", MD5.md5(registerPassword.getText().toString().trim()));
		map.put("mobile", registerUserMobile.getText().toString().trim());
		
		if(gender.equals("��")){
			map.put("sex", "1");
		}
		else if(gender.equals("Ů")){
			map.put("sex", "0");
		}
		
		map.put("time", getIntent().getExtras().getString("time"));
		
		return map;
		
	}
	
	
	/**
	 * ��Ϣ��ʾ�򣬵�������Ҫ���ȷ��
	 */
	public void showSimpleDialog(View view) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setPositiveButton("---ע��ɹ�---", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				//��ʾ�û���ת
				new Thread(new Runnable(){
					@Override
					public void run() {
						Looper.prepare();
						Toast.makeText(Register.this, "������ת", Toast.LENGTH_LONG).show();
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














