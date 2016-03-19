package com.whing;

/**
 * �������ҳ�棬ÿ�δ򿪳������ȿ�ʼ�򿪴�ҳ
 * ��ҳ����
 * 1�������¼
 * 2���Զ���¼�������ѱ����û��������������£�
 * 3��ע�����
 * 3��Jaccount��¼���
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
	
	//���ȫ�ֱ������趨
	EditText loginUserName;  //�û�������
	EditText loginPassword;  //�û���������
	Button loginSignin;   //��¼ȷ�����
	TextView loginRegister, loginJaccount;   //ע����ں�Jaccount��¼���
	
	public static PushAgent mPushAgent;  //�������ͷ���
	
	/**
	 * ����ȫ�ֹ��ñ���
	 */
	public static String userName;  //�û��˻���
	public static String nickName;    //�û��ǳ�
	public static String password;  //�û���¼����
	public static Drawable userImageDrawable;  //�û�ͷ��
	
	public static File prfPassword;
	public static File prfToken;
	public static File prfUserId;
	
	public static String imageFilePath;  //�û�ͷ��洢·��
	public static String token;  //�û���¼��token
	public static String user_id;  //�û�id
	
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
		
		//�ж��Ƿ��п��õ����磬���û�У�����ʾ
		if ( ! isNetworkAvailable(Login.this) ){
            Toast.makeText(this, "��ǰ���粻����", Toast.LENGTH_LONG).show();
        }
		
		response();    //�����߻ظ��û���������Activity�У��ڲ����õ���umeng��sdk
		
		userName = LocalInf.getLocalInf(this, USERNAME);
		password = LocalInf.getLocalInf(this, PASSWORD);
		
		UmengUpdateAgent.update(this);    //�汾�Զ�������
		
		mPushAgent = PushAgent.getInstance(this);    //�������ͷ���
		mPushAgent.onAppStart();
		mPushAgent.enable();
		
		init();    //��ʼ���������
		
		preNet();    //������׼��
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		autoLogin();    //�Զ���½���趨
		
		setCommonVar();    //���ü���ȫ�ֱ���
		
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
	 * �����߻ظ��û���������ʾ
	 */
	private void response(){
		FeedbackAgent agent = new FeedbackAgent(this);
		agent.sync();
	}
	
	
	/**
	 * ��ȡ��ǰ�豸����Ϣ
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
	 * ���ø�����ļ���
	 */
	private void setListener(){
		
		loginRegister.setOnClickListener(this);
		loginJaccount.setOnClickListener(this);
		loginSignin.setOnClickListener(this);

		//��¼�����ť�ĵ��Ч��
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
	 * ��ʼ���������
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
	
		PushAgent.getInstance(this).onAppStart();    //���ͷ��������ͳ�ƺ���
		
		sd = new ShowDialog(pd);
		
	}
	
	/**
	 * ��������ĵ����������
	 * ʹ�������Id����ѡ���о���ʹ��v.getId()��ɲ���
	 * @param v ���
	 */
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		
		//ע�����ӣ��������ע��ҳ�棬��ǰҳ��ر�
		case R.id.register_link:{
			Intent intent = new Intent(Login.this, BindJaccount.class);
			startActivity(intent);
			finish();
			break;
		}
			
		//jaccount��¼���ӣ��������jaccount��¼ҳ����ǰҳ��ر�
		case R.id.signin_jaccount:{
			Intent intent2 = new Intent(Login.this, LoginJaccount.class);
			startActivity(intent2);
			finish();
			break;
		}
			
		//��¼���������������֤����¼�ȳ������
		case R.id.signin_button:{
			
			new Thread(new Runnable(){
				@Override
				public void run() {
					Looper.prepare();
					sd.showDialog(Login.this, "������֤");
					Looper.loop();
				}
			}).start();
			
			
			//��¼���û��������벻Ϊ��
			//���ؽ��û��������뱣������
			//����������ˣ��ͽ�����һ���������޲���
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
				//���û����������ύ����ȡ��������������ֵ
				String result = loginPro(URLenum.login,
						loginUserName.getText().toString().trim(),
						loginPassword.getText().toString().trim()
						);
				
				//���ӷ�������ȡ��������JSON��ʽ���������ȡ���е�token���û�id������
				try {
					JSONObject jsonReturn = new JSONObject(result);
					if(!jsonReturn.getString("token").isEmpty())
					{
						//���淵�������е�tokenֵ
						LocalInf.saveData(this,
								jsonReturn.getString("token"), 
								TOKEN);
						
						//���淵�������е�user_idֵ
						LocalInf.saveData(this,
								jsonReturn.getString("user_id"),
								USERID);
						
						
						//��alias���Զ�����Ϣ����
						final String userID = jsonReturn.getString("user_id");
						bindAlias(userID);
						
						setCommonVar();
						
						sd.dismissDialog();
						
						//��ת������ҳ�棬�رյ�ǰҳ��
						Intent intent3=new Intent(Login.this, Hall.class);  
						startActivity(intent3);
						
		                finish();  
					}
					else{
						sd.dismissDialog();
						Toast.makeText(this, "��¼����", Toast.LENGTH_SHORT).show();
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
	 * ��֤�û����������Ƿ���Ϲ淶
	 * @return true / false 
	 */
	private boolean isValidate(){
		String userNameString = loginUserName.getText().toString().trim();
		String passwordString = loginPassword.getText().toString().trim();
		
		if(userNameString.equals("")){
			Toast.makeText(this, "�û�������Ϊ��", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(passwordString.equals("")){
			Toast.makeText(this, "���벻��Ϊ��", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	/**
	 * ���û����û����������ύ��ָ���ķ�������ַ������֤
	 * @param url Ŀ��������ĵ�ַ
	 * @param accountString �û����ַ���
	 * @param passwordString �û������ַ���
	 * @return ���������ص��ַ�����
	 */
	public static String loginPro(URL url, String accountString, String passwordString){
		
		Map<String, String> map = new HashMap<String, String>();
		
		//��ȡ��¼���͵�¼����
		String account = accountString.trim();
		String password = passwordString.trim();
		
		//���������MD5����
		String passwordDigest = MD5.md5(password);
		
		map.put("account", account);
		map.put("password", passwordDigest);
		
		String result = PostData.postData(map, url);
		
		return result;
	}

	/**
	 * ������׼��������
	 * 1��ǿ��������������
	 * 2������Ŀ���������URL
	 */
	private void preNet(){
		StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}
	
	
	/**
	 * ���û����������ڱ��ش洢��ʱ���Զ���¼
	 */
	private void autoLogin(){
		
		//userName��password�Ѿ��ڱ��ػ�ȡ������ȡ��ֵ��Ϊ��ʱ���Զ���½
		if ( ! (userName.equals("") || password.equals("")) ) {
			
			bindAlias(user_id);    //��alias
			
			try {
				
				String result = Login.loginPro(URLenum.login, userName, password).trim();
				
				try {
					JSONObject jsonReturn = new JSONObject(result);
					
					//�ύ�û����������������֮��ķ���ֵJSON����
					//���token��Ϊ�գ��ͽ�token���û�id��������
					//����ɹ�����ת������ҳ�棬���رյ�ǰҳ��
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
	 * ���õ�static���͵ļ��������ĸ�ֵ��
	 * ���ļ�����ȡ��ͼƬ�����Ǵ�apkԴ����ȡ��ͼƬ��Ϊ�û�ͷ��
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
		
		
		//�ж�ͷ���ļ��Ƿ���ڣ�������ʱ��ֱ�ӽ�ͷ���ļ���ֵ��Login����userImageDrawable
		//��������ʱ���¿�һ���̣߳���ͷ���ļ��ӷ���������������Ȼ��������ֵ
		
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
	 * ��Alias���û���Ϣ����
	 * @param uid �û���id
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
     * ��鵱ǰ�����Ƿ����
     * 
     * @param context
     * @return
     */
    
	/**
	 * �ж����������Ƿ����
	 * @param context
	 * @return
	 */
    public boolean isNetworkAvailable(Context context)
    {
        // ��ȡ�ֻ��������ӹ�����󣨰�����wi-fi,net�����ӵĹ���
        ConnectivityManager connectivityManager = 
        		(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (connectivityManager == null)
        {
            return false;
        }
        else
        {
            // ��ȡNetworkInfo����
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
