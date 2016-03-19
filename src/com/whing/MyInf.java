package com.whing;

/**
 * ������Ϣҳ��
 * ����չʾ������Ϣ���޸ĸ�����Ϣ���޸ĸ������룬�޸ĸ���ͷ��
 */

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.whing.external.LocalInf;
import com.whing.external.PostData;
import com.whing.external.SetActData;
import com.whing.external.ShowDialog;
import com.whing.external.URLenum;
import com.whing.imgcut.CircleImg;
import com.whing.imgcut.FileUtil;
import com.whing.imgcut.SelectPicPopupWindow;
import com.whing.security.MD5;
import com.whing.R;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MyInf extends TabActivity implements OnClickListener, OnTouchListener{

	Button myInfNavigationLeft, myInfChangeEnsure, myInfChangePasswordEnsure,
			myInfChangeInfEnsure;
	
	Button interest_1,  interest_2,  interest_3, interest_4, interest_5, interest_6, 
		interest_7, interest_8, interest_9, interest_10, interest_11, interest_12, 
		interest_13, interest_14, interest_15;
	
	TextView myInfUserFollowImageName, myInfBasicInf, myInfChangePassword,
			myInfChangeUserImage;
	
	TextView myInfUserRealName, myInfUserNickName, myInfUserAccount, myInfUserGender, myInfUserSchool,
			myInfUserMobile, myInfUserInterest, myInfUserCollege,myInfUserEstimation, myInfUserQQ;
	
	EditText myInfUserNickNameEdit, myInfUserMobileEdit, myInfUserQQEdit;
	
	EditText myInfOrigenalPassword, myInfNewPassword, myInfNewPasswordAgain;

	private CircleImg myInfUserImage;
	
	private SelectPicPopupWindow menuWindow;
	public static Context mContext;

	private static final int REQUESTCODE_PICK = 0; // ���ѡͼ���
	private static final int REQUESTCODE_TAKE = 1; // ������ձ��
	private static final int REQUESTCODE_CUTTING = 2; // ͼƬ���б��
	
	private static final String IMAGE_FILE_NAME = MD5.md5(Login.user_id).substring(0, 8);// ͷ���ļ�����
	
	private String imagePath;			// ͼƬ����·��
	
	private CircleImg userImage;    // ͷ��ͼƬ
	
	private ProgressDialog pd;  // �ȴ�����Ȧ
	private ShowDialog sd;
	
	private String resultStr = "";	// ����˷��ؽ����
	
	private String[] interestArr ;
	
	TabHost tabHost;
	
	private boolean flag_1= false;
	private boolean flag_2= false;
	private boolean flag_3= false;
	private boolean flag_4= false;
	private boolean flag_5= false;
	private boolean flag_6= false;
	private boolean flag_7= false;
	private boolean flag_8= false;
	private boolean flag_9= false;
	private boolean flag_10= false;
	private boolean flag_11= false;
	private boolean flag_12= false;
	private boolean flag_13= false;
	private boolean flag_14= false;
	private boolean flag_15= false;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_inf);
		
		tabHost = getTabHost();
		
		init();    //������ĳ�ʼ��
		
		setListener();    //���ø�����ļ�����
		
		setInf();    //�����û������û�ͷ��
		
		tabSet();    //tabHost������
		
		getInf_SetInf();    //�ӷ�������ȡ�û��Ļ�����Ϣ��չʾ
		
		
		

	}
	
	
	/**
	 * onResume() �� onPause()��������ӵ�MobclickAgent������ʵ��umeng����ͳ�Ƶ�
	 */
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
	
	
	
	// Ϊ��������ʵ�ּ�����
	private OnClickListener itemsOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			
			menuWindow.dismiss();
			
			switch (v.getId()) {
			
			// ����
			case R.id.takePhotoBtn:{
				Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				
				// �������ָ������������պ����Ƭ�洢��·��
				takeIntent.putExtra(
						MediaStore.EXTRA_OUTPUT, 
						Uri.fromFile(
								new File(Environment.getExternalStorageDirectory(),
								IMAGE_FILE_NAME)
						)
				);
				
				startActivityForResult(takeIntent, REQUESTCODE_TAKE);
				break;
			}
				
			// ���ѡ��ͼƬ
			case R.id.pickPhotoBtn:{
				Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
				// ���������Ҫ�����ϴ�����������ͼƬ����ʱ����ֱ��д�磺"image/jpeg �� image/png�ȵ�����"
				pickIntent.setDataAndType(
								MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
								"image/*");
				startActivityForResult(pickIntent, REQUESTCODE_PICK);
				break;
			}
				
			default:{
				break;
			}
				
			}
		}
	};
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch (requestCode) {
		
		// ֱ�Ӵ�����ȡ
		case REQUESTCODE_PICK:{
			try {
				startPhotoZoom(data.getData());
			} catch (NullPointerException e) {
				e.printStackTrace();// �û����ȡ������
			}
			break;
		}
		
		// �����������
		case REQUESTCODE_TAKE:{
			File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME +".jpg");
			startPhotoZoom(Uri.fromFile(temp));
			break;
		}
			
		// ȡ�òü����ͼƬ
		case REQUESTCODE_CUTTING:{
			if (data != null) {
				setPicToView(data);
			}
			break;
		}
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * �ü�ͼƬ����ʵ��
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop=true�������ڿ�����Intent��������ʾ��VIEW�ɲü�
		intent.putExtra("crop", "true");
		// aspectX aspectY �ǿ�ߵı���
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY �ǲü�ͼƬ���
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, REQUESTCODE_CUTTING);
	}
		
	/**
	 * ����ü�֮���ͼƬ����
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		
		if (extras != null) {
			
			// ȡ��SDCardͼƬ·������ʾ
			Bitmap photo = extras.getParcelable("data");
			
			Drawable drawable = new BitmapDrawable(null, photo);
			
			//����ͼƬ�����أ����ҵõ�·����
			imagePath = FileUtil.saveFile(mContext, Login.user_id, photo);
			
			userImage.setImageDrawable(drawable);
			
			Login.userImageDrawable = drawable;
			
			// ���̺߳�̨�ϴ������
			sd.showDialog(mContext, "�����ϴ�ͼƬ�����Ժ�...");
			
			new Thread(uploadImageRunnable).start();
			
			setInf();
		}
	}
	
	Runnable uploadImageRunnable = new Runnable() {
		@Override
		public void run() {
			
			String token = "";
			try {
				String result = PostData.getData("", URLenum.up_token);
				
				JSONObject json = new JSONObject(result);
				token = json.getString("uptoken");
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			File file = new File(imagePath);
			
			// ���� uploadManager��һ��أ�ֻ��Ҫ����һ�� uploadManager ����
			UploadManager uploadManager = new UploadManager();
			
			uploadManager.put(file, null, token,new UpCompletionHandler() {
				
			    @Override
			    public void complete(String key, ResponseInfo info, JSONObject response) {
			    	resultStr = response+"";
			        handler.sendEmptyMessage(0);// ִ�к�ʱ�ķ���֮��������handler
			    }
			}, null);
			
		}

	};
	
	Handler handler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			
			switch (msg.what) {
			
			case 0:{
				sd.dismissDialog();
				
				try {

					JSONObject jsonObject = new JSONObject(resultStr);
					String key ="\"" + URLenum.img_url + jsonObject.getString("key") + "\"";
					
					Map<String, String> map = new HashMap<String, String>();
					map.put("photo", key);

					String result = PostData.postData(map, URLenum.update_photo, Login.token);
					jsonObject = new JSONObject(result);
					
					if(jsonObject.get("errcode").equals(0)){
						Toast.makeText(MyInf.this, "�ϴ��ɹ�", Toast.LENGTH_SHORT).show();
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				break;
			}
				
			default:{
				break;
			}

			}
			
			return false;
		}
	});


	/**
	 * ��ȡ�û��༭֮�����Ϣ�������޸�
	 * @return
	 */
	private Map<String, String> getInfEdit(){
		Map<String, String> myInfMap = new HashMap<String, String>();
		
		ArrayList<Integer> interestList = new ArrayList<Integer>();
		if(flag_1) interestList.add(1);
		if(flag_2) interestList.add(2);
		if(flag_3) interestList.add(3);
		if(flag_4) interestList.add(4);
		if(flag_5) interestList.add(5);
		if(flag_6) interestList.add(6);
		if(flag_7) interestList.add(7);
		if(flag_8) interestList.add(8);
		if(flag_9) interestList.add(9);
		if(flag_10) interestList.add(10);
		if(flag_11) interestList.add(11);
		if(flag_12) interestList.add(12);
		if(flag_13) interestList.add(13);
		if(flag_14) interestList.add(14);
		if(flag_15) interestList.add(15);
		
		
		myInfMap.put("nickname", myInfUserNickNameEdit.getText().toString().trim());
		myInfMap.put("mobile", myInfUserMobileEdit.getText().toString().trim());
		myInfMap.put("interest", "\"" + interestList.toString() + "\"");
		
		if(!myInfUserQQEdit.getText().toString().trim().equals(myInfUserQQ.getText().toString().trim())){
			myInfMap.put("qq", myInfUserQQEdit.getText().toString()
					.trim());
		}
		
		return myInfMap;
	}
	
	/**
	 * �����û�ͷ����û���
	 */
	private void setInf(){
		myInfUserFollowImageName.setText(Login.nickName);
		myInfUserImage.setImageDrawable(Login.userImageDrawable);
		userImage.setImageDrawable(Login.userImageDrawable);
		
	}

	/**
	 * ���ø�������ļ�����
	 */
	private void setListener(){
		myInfNavigationLeft.setOnClickListener(this);
		myInfBasicInf.setOnClickListener(this);
		myInfChangePassword.setOnClickListener(this);
		myInfChangeUserImage.setOnClickListener(this);
		myInfChangeEnsure.setOnClickListener(this);
		myInfChangeInfEnsure.setOnClickListener(this);
		myInfChangePasswordEnsure.setOnClickListener(this);
		userImage.setOnClickListener(this);
		
		myInfChangeEnsure.setOnTouchListener(this);
		myInfChangeInfEnsure.setOnTouchListener(this);
		myInfChangePasswordEnsure.setOnTouchListener(this);
		
		interest_1.setOnClickListener(this);
		interest_2.setOnClickListener(this);
		interest_3.setOnClickListener(this);
		interest_4.setOnClickListener(this);
		interest_5.setOnClickListener(this);
		interest_6.setOnClickListener(this);
		interest_7.setOnClickListener(this);
		interest_8.setOnClickListener(this);
		interest_9.setOnClickListener(this);
		interest_10.setOnClickListener(this);
		interest_11.setOnClickListener(this);
		interest_12.setOnClickListener(this);
		interest_13.setOnClickListener(this);
		interest_14.setOnClickListener(this);
		interest_15.setOnClickListener(this);
		
		// ���ذ�ť�ĵ��Ч��
		myInfNavigationLeft.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					myInfNavigationLeft.setBackgroundResource(R.drawable.login_ensure_button_down);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					myInfNavigationLeft.setBackgroundResource(R.drawable.navigation_back);
				}
				return false;
			}
		});
		
	}

	
	/**
	 * �ӷ�������ȡ�û���Ϣ��չʾ
	 */
	private void getInf_SetInf(){
		
		Hall.executorService.submit(new Runnable(){

			@Override
			public void run() {
				
				final JSONObject jsonGetInf;
				
				try {
					String result = PostData.getData("", URLenum.get_info);
					jsonGetInf = new JSONObject(result);

					Hall.handler.post(new Runnable(){

						@Override
						public void run() {
							try {
								myInfUserRealName.setText(jsonGetInf.get("realname").toString().trim());
								
								myInfUserAccount.setText(jsonGetInf.get("account").toString().trim());
								
								myInfUserNickName.setText(jsonGetInf.get("nickname").toString().trim());
								myInfUserNickNameEdit.setText(jsonGetInf.get("nickname").toString().trim());
								
								myInfUserSchool.setText(jsonGetInf.get("university").toString()
										.trim());
								
								myInfUserCollege.setText(jsonGetInf.get("college").toString().trim());
								
								myInfUserEstimation.setText(jsonGetInf.get("estimation").toString().trim());
								
								myInfUserQQ.setText(jsonGetInf.get("qq").toString().trim());
								myInfUserQQEdit.setText(jsonGetInf.get("qq").toString().trim());
								
								myInfUserMobile.setText(jsonGetInf.get("mobile").toString()
										.trim());
								myInfUserMobileEdit.setText(jsonGetInf.get("mobile").toString()
										.trim());
								
								String sex = jsonGetInf.getString("sex");
								if(sex.equals("1")){
									myInfUserGender.setText("��");
								}
								else{
									myInfUserGender.setText("Ů");
								}
								
								interestArr = SetActData.JSArrayToStringArray(jsonGetInf.getJSONArray("interest"));
								String interest = interestString(interestArr);
								myInfUserInterest.setText(interest);
								
								sd.dismissDialog();
								
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * tabHost�ڵ�ѡ�������Լ���ʼ����
	 */
	private void tabSet(){
		TabSpec tab1 = tabHost.newTabSpec("tab1").setIndicator("0").setContent(R.id.tab01);
		tabHost.addTab(tab1);
		TabSpec tab2 = tabHost.newTabSpec("tab2").setIndicator("1").setContent(R.id.tab02);
		tabHost.addTab(tab2);
		TabSpec tab3 = tabHost.newTabSpec("tab3").setIndicator("2").setContent(R.id.tab03);
		tabHost.addTab(tab3);
		TabSpec tab4 = tabHost.newTabSpec("tab4").setIndicator("3").setContent(R.id.tab04);
		tabHost.addTab(tab4);

		tabHost.setCurrentTab(0);
		myInfBasicInf.setTextColor(getResources().getColor(R.color.color_blue));
	}
	
	/**
	 * ��ʼ�������
	 */
	private void init(){

		mContext = MyInf.this;
		sd = new ShowDialog(pd);
		
		PushAgent.getInstance(this).onAppStart();

		myInfNavigationLeft = (Button) findViewById(R.id.my_inf_navigation_left);
		myInfChangeEnsure = (Button) findViewById(R.id.my_inf_change_ensure);
		myInfUserImage = (CircleImg) findViewById(R.id.my_inf_user_image);
		myInfUserFollowImageName = (TextView) findViewById(R.id.my_inf_user_follow_image_name);
		myInfBasicInf = (TextView) findViewById(R.id.my_inf_basic_inf);
		myInfChangePassword = (TextView) findViewById(R.id.my_inf_change_password);
		myInfChangeUserImage = (TextView) findViewById(R.id.my_inf_change_user_image);
		
		
		myInfUserRealName = (TextView) findViewById(R.id.my_inf_user_realname);
		myInfUserNickName = (TextView) findViewById(R.id.my_inf_user_nickname);
		myInfUserAccount = (TextView) findViewById(R.id.my_inf_user_account);
		myInfUserGender = (TextView) findViewById(R.id.my_inf_user_gender);
		myInfUserSchool = (TextView) findViewById(R.id.my_inf_user_school);
		myInfUserMobile = (TextView) findViewById(R.id.my_inf_user_mobile);
		myInfUserInterest = (TextView) findViewById(R.id.my_inf_user_interest);
		myInfUserCollege = (TextView) findViewById(R.id.my_inf_user_college);
		myInfUserEstimation = (TextView) findViewById(R.id.my_inf_user_estimation);
		myInfUserQQ = (TextView) findViewById(R.id.my_inf_user_qq);

		myInfUserNickNameEdit = (EditText) findViewById(R.id.my_inf_user_nickname_edit);
		myInfUserMobileEdit = (EditText) findViewById(R.id.my_inf_user_mobile_edit);
		myInfUserQQEdit = (EditText) findViewById(R.id.my_inf_user_qq_edit);
		myInfChangeInfEnsure = (Button) findViewById(R.id.my_inf_change_inf_ensure);

		myInfOrigenalPassword = (EditText) findViewById(R.id.my_inf_origenal_password);
		myInfNewPassword = (EditText) findViewById(R.id.my_inf_new_password);
		myInfNewPasswordAgain = (EditText) findViewById(R.id.my_inf_new_password_again);
		myInfChangePasswordEnsure = (Button) findViewById(R.id.my_inf_change_password_ensure);

		userImage = (CircleImg) findViewById(R.id.my_inf_change_user_picture);
		
		interest_1 = (Button) findViewById(R.id.interest_1);
		interest_2 = (Button) findViewById(R.id.interest_2);
		interest_3 = (Button) findViewById(R.id.interest_3);
		interest_4 = (Button) findViewById(R.id.interest_4);
		interest_5 = (Button) findViewById(R.id.interest_5);
		interest_6 = (Button) findViewById(R.id.interest_6);
		interest_7 = (Button) findViewById(R.id.interest_7);
		interest_8 = (Button) findViewById(R.id.interest_8);
		interest_9 = (Button) findViewById(R.id.interest_9);
		interest_10 = (Button) findViewById(R.id.interest_10);
		interest_11 = (Button) findViewById(R.id.interest_11);
		interest_12 = (Button) findViewById(R.id.interest_12);
		interest_13 = (Button) findViewById(R.id.interest_13);
		interest_14 = (Button) findViewById(R.id.interest_14);
		interest_15 = (Button) findViewById(R.id.interest_15);
		
	}

	/**
	 * �������������
	 */
	@Override
	public void onClick(View v) {
		
		
		switch(v.getId()){
		
		case R.id.my_inf_navigation_left:{
			finish();
			break;
		}
		
		case R.id.my_inf_basic_inf:{
			tabHost.setCurrentTab(0);
			myInfBasicInf.setTextColor(getResources().getColor(R.color.color_blue));
			myInfChangePassword.setTextColor(getResources().getColor(R.color.color_text_gray));
			myInfChangeUserImage.setTextColor(getResources().getColor(R.color.color_text_gray));
			break;
		}
		
		case R.id.my_inf_change_password:{
			tabHost.setCurrentTab(1);
			myInfBasicInf.setTextColor(getResources().getColor(R.color.color_text_gray));
			myInfChangePassword.setTextColor(getResources().getColor(R.color.color_blue));
			myInfChangeUserImage.setTextColor(getResources().getColor(R.color.color_text_gray));
			break;
		}
		
		case R.id.my_inf_change_user_image:{
			tabHost.setCurrentTab(2);
			myInfBasicInf.setTextColor(getResources().getColor(R.color.color_text_gray));
			myInfChangePassword.setTextColor(getResources().getColor(R.color.color_text_gray));
			myInfChangeUserImage.setTextColor(getResources().getColor(R.color.color_blue));
			break;
		}
		
		case R.id.my_inf_change_ensure:
		{
			tabHost.setCurrentTab(3);
			break;
		}
		
		case R.id.my_inf_change_inf_ensure:
		{
			Hall.executorService.submit(new Runnable(){
				
				@Override
				public void run() {
					/**
					 * �޸ĸ�����Ϣ
					 */
					JSONObject jsonPostInf = null;
					int errcode = 0;
					
					/**
					 * ����û����Ƿ��б仯������б仯��Ҫ���û����������ݿ��ظ��Լ�飬
					 * ���ͨ��֮�����ִ������update���ݵĲ��������û�б仯����ֱ�ӽ���update�Ĳ���
					 */
					String result = PostData.postData(getInfEdit(), URLenum.update_info, Login.token);
					Log.v("�޸���Ϣ�ķ���ֵ", result);
					
					try {
						jsonPostInf = new JSONObject(result);
						errcode = (Integer) jsonPostInf.get("errcode");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					if (errcode == 0 ) {
						
						Hall.handler.post(new Runnable(){

							@Override
							public void run() {
								Toast.makeText(MyInf.this, "�޸ĳɹ�", Toast.LENGTH_SHORT).show();
								
								myInfUserNickName.setText(myInfUserNickNameEdit.getText().toString().trim());
								Login.nickName = myInfUserNickNameEdit.getText().toString().trim();
								myInfUserMobile.setText(myInfUserMobileEdit.getText().toString().trim());
								myInfUserQQ.setText(myInfUserQQEdit.getText().toString().trim());
								
								String interest = "";
								if(flag_1) interest = interest + getResources().getString(R.string.interest_1) + " ";
								if(flag_2) interest = interest + getResources().getString(R.string.interest_2) + " ";
								if(flag_3) interest = interest + getResources().getString(R.string.interest_3) + " ";
								if(flag_4) interest = interest + getResources().getString(R.string.interest_4) + " ";
								if(flag_5) interest = interest + getResources().getString(R.string.interest_5) + " ";
								if(flag_6) interest = interest + getResources().getString(R.string.interest_6) + " ";
								if(flag_7) interest = interest + getResources().getString(R.string.interest_7) + " ";
								if(flag_8) interest = interest + getResources().getString(R.string.interest_8) + " ";
								if(flag_9) interest = interest + getResources().getString(R.string.interest_9) + " ";
								if(flag_10) interest = interest + getResources().getString(R.string.interest_10) + " ";
								if(flag_11) interest = interest + getResources().getString(R.string.interest_11) + " ";
								if(flag_12) interest = interest + getResources().getString(R.string.interest_12) + " ";
								if(flag_13) interest = interest + getResources().getString(R.string.interest_13) + " ";
								if(flag_14) interest = interest + getResources().getString(R.string.interest_14) + " ";
								if(flag_15) interest = interest + getResources().getString(R.string.interest_15) + " ";
								
								myInfUserInterest.setText(interest);
								
								tabHost.setCurrentTab(0);
							}
						});
					}
				}
			});
			
			break;
		}
		
		
		case R.id.my_inf_change_password_ensure: {
			/**
			 * �޸��û�����
			 */
			sd.showDialog(mContext, "�����ϴ������Ժ�...");
			
			if(isValidate()){
				
				Hall.executorService.submit(new Runnable(){

					@Override
					public void run() {
						
						Map<String, String> passMap = new HashMap<String, String> ();
						passMap.put("old_pass", MD5.md5(myInfOrigenalPassword.getText().toString().trim()));
						passMap.put("new_pass", MD5.md5(myInfNewPassword.getText().toString().trim()));
						String result = PostData.postData(passMap, URLenum.update_password, Login.token);
						
						try {
							JSONObject json = new JSONObject(result);
							int errcode = (Integer) json.get("errcode");
							if(errcode == 0){
								
								Hall.handler.post(new Runnable(){

									@Override
									public void run() {
										Toast.makeText(MyInf.this, "��������ɹ�", Toast.LENGTH_SHORT).show();
										
										//���޸ĺ�����뱣�浽����
										LocalInf.saveData(MyInf.this, 
												myInfNewPassword.getText().toString().trim(),
												2);
										
										tabHost.setCurrentTab(0);
										myInfBasicInf.setTextColor(getResources().getColor(R.color.color_blue));
										myInfChangePassword.setTextColor(getResources().getColor(R.color.color_text_gray));
										myInfChangeUserImage.setTextColor(getResources().getColor(R.color.color_text_gray));
										
										myInfOrigenalPassword.setText("");
										myInfNewPassword.setText("");
										myInfNewPasswordAgain.setText("");
										
										sd.dismissDialog();
										
									}
									
								});
							}
							else if(errcode == 150){
								Toast.makeText(MyInf.this, "ԭ�������", Toast.LENGTH_SHORT).show();
							}
							else{
								Toast.makeText(MyInf.this, errcode+"", Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}
			break;
		}
		
		
		case R.id.my_inf_change_user_picture:{
			menuWindow = new SelectPicPopupWindow(mContext, itemsOnClick);
			
			// ����menuWindow�ĵ���λ��
			menuWindow.showAtLocation(findViewById(R.id.my_inf_layout),
					Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			
			break;
		}
		
		case R.id.interest_1:{
			if(flag_1){
				interest_1.setBackgroundColor(getResources().getColor(R.color.color_hyaline));
				flag_1 = false;
			}
			else{
				interest_1.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_1 = true;
			}
			break;
		}
		
		case R.id.interest_2:{
			if(flag_2){
				interest_2.setBackgroundColor(getResources().getColor(R.color.color_hyaline));
				flag_2 = false;
			}
			else{
				interest_2.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_2 = true;
			}
			break;
		}
		
		case R.id.interest_3:{
			if(flag_3){
				interest_3.setBackgroundColor(getResources().getColor(R.color.color_hyaline));
				flag_3 = false;
			}
			else{
				interest_3.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_3 = true;
			}
			break;
		}
		
		case R.id.interest_4:{
			if(flag_4){
				interest_4.setBackgroundColor(getResources().getColor(R.color.color_hyaline));
				flag_4 = false;
			}
			else{
				interest_4.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_4 = true;
			}
			break;
		}
		
		case R.id.interest_5:{
			if(flag_5){
				interest_5.setBackgroundColor(getResources().getColor(R.color.color_hyaline));
				flag_5 = false;
			}
			else{
				interest_5.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_5 = true;
			}
			break;
		}
		
		case R.id.interest_6:{
			if(flag_6){
				interest_6.setBackgroundColor(getResources().getColor(R.color.color_hyaline));
				flag_6 = false;
			}
			else{
				interest_6.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_6 = true;
			}
			break;
		}
		
		case R.id.interest_7:{
			if(flag_7){
				interest_7.setBackgroundColor(getResources().getColor(R.color.color_hyaline));
				flag_7 = false;
			}
			else{
				interest_7.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_7 = true;
			}
			break;
		}
		
		case R.id.interest_8:{
			if(flag_8){
				interest_8.setBackgroundColor(getResources().getColor(R.color.color_hyaline));
				flag_8 = false;
			}
			else{
				interest_8.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_8 = true;
			}
			break;
		}
		
		case R.id.interest_9:{
			if(flag_9){
				interest_9.setBackgroundColor(getResources().getColor(R.color.color_hyaline));
				flag_9 = false;
			}
			else{
				interest_9.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_9 = true;
			}
			break;
		}
		
		case R.id.interest_10:{
			if(flag_10){
				interest_10.setBackgroundColor(getResources().getColor(R.color.color_hyaline));
				flag_10 = false;
			}
			else{
				interest_10.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_10 = true;
			}
			break;
		}
		
		case R.id.interest_11:{
			if(flag_11){
				interest_11.setBackgroundColor(getResources().getColor(R.color.color_hyaline));
				flag_11 = false;
			}
			else{
				interest_11.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_11 = true;
			}
			break;
		}
		
		case R.id.interest_12:{
			if(flag_12){
				interest_12.setBackgroundColor(getResources().getColor(R.color.color_hyaline));
				flag_12 = false;
			}
			else{
				interest_12.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_12 = true;
			}
			break;
		}
		
		case R.id.interest_13:{
			if(flag_13){
				interest_13.setBackgroundColor(getResources().getColor(R.color.color_hyaline));
				flag_13 = false;
			}
			else{
				interest_13.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_13 = true;
			}
			break;
		}
		
		case R.id.interest_14:{
			if(flag_14){
				interest_14.setBackgroundColor(getResources().getColor(R.color.color_hyaline));
				flag_14 = false;
			}
			else{
				interest_14.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_14 = true;
			}
			break;
		}
		
		case R.id.interest_15:{
			if(flag_15){
				interest_15.setBackgroundColor(getResources().getColor(R.color.color_hyaline));
				flag_15 = false;
			}
			else{
				interest_15.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_15 = true;
			}
			break;
		}
		
		
		}
	}

	private boolean isValidate(){
		if(!myInfNewPassword.getText().toString().trim()
				.equals(myInfNewPasswordAgain.getText().toString().trim())){
			Toast.makeText(this, "�������������벻һ��", Toast.LENGTH_SHORT).show();
			return false;
		}
		else{
			return true;
		}
	}



	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		switch(v.getId()){
		
		case R.id.my_inf_change_ensure:{
			if(event.getAction() == MotionEvent.ACTION_DOWN){
				myInfChangeEnsure.setBackgroundResource(R.drawable.radio_button_new_one_down);
			}
			else if(event.getAction() == MotionEvent.ACTION_UP){
				myInfChangeEnsure.setBackgroundResource(R.drawable.radio_button_new_one_normal);
			}
			break;
		}
		
		case R.id.my_inf_change_inf_ensure:{
			if(event.getAction() == MotionEvent.ACTION_DOWN){
				myInfChangeInfEnsure.setBackgroundResource(R.drawable.radio_button_new_one_down);
			}
			else if(event.getAction() == MotionEvent.ACTION_UP){
				myInfChangeInfEnsure.setBackgroundResource(R.drawable.radio_button_new_one_normal);
			}
			break;
		}
		
		case R.id.my_inf_change_password_ensure:{
			if(event.getAction() == MotionEvent.ACTION_DOWN){
				myInfChangePasswordEnsure.setBackgroundResource(R.drawable.radio_button_new_one_down);
			}
			else if(event.getAction() == MotionEvent.ACTION_UP){
				myInfChangePasswordEnsure.setBackgroundResource(R.drawable.radio_button_new_one_normal);
			}
			break;
		}
		
		default:{
			break;
		}
		
		}
		
		return false;
	}

	
	private String interestString(String[] strings) {
		
		String string="";
		
		for(int i=0; i<strings.length; i++){
			
			int num = Integer.parseInt(strings[i]);
			
			switch(num){
			
			case 1:{
				string = string + getResources().getString(R.string.interest_1) + " ";
				interest_1.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_1 = true;
				break;
			}
			
			case 2:{
				string = string + getResources().getString(R.string.interest_2) + " ";
				interest_2.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_2 = true;
				break;
			}
			
			case 3:{
				string = string + getResources().getString(R.string.interest_3) + " ";
				interest_3.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_3 = true;
				break;
			}
			
			case 4:{
				string = string + getResources().getString(R.string.interest_4) + " ";
				interest_4.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_4 = true;
				break;
			}
			
			case 5:{
				string = string + getResources().getString(R.string.interest_5) + " ";
				interest_5.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_5 = true;
				break;
			}
			
			case 6:{
				string = string + getResources().getString(R.string.interest_6) + " ";
				interest_6.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_6 = true;
				break;
			}
			
			case 7:{
				string = string + getResources().getString(R.string.interest_7) + " ";
				interest_7.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_7 = true;
				break;
			}
			
			case 8:{
				string = string + getResources().getString(R.string.interest_8) + " ";
				interest_8.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_8 = true;
				break;
			}
			
			case 9:{
				string = string + getResources().getString(R.string.interest_9) + " ";
				interest_9.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_9 = true;
				break;
			}
			
			case 10:{
				string = string + getResources().getString(R.string.interest_10) + " ";
				interest_10.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_10 = true;
				break;
			}
			
			case 11:{
				string = string + getResources().getString(R.string.interest_11) + " ";
				interest_11.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_11 = true;
				break;
			}
			
			case 12:{
				string = string + getResources().getString(R.string.interest_12) + " ";
				interest_12.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_12 = true;
				break;
			}
			
			case 13:{
				string = string + getResources().getString(R.string.interest_13) + " ";
				interest_13.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_13 = true;
				break;
			}
			
			case 14:{
				string = string + getResources().getString(R.string.interest_14) + " ";
				interest_14.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_14 = true;
				break;
			}
			
			case 15:{
				string = string + getResources().getString(R.string.interest_15) + " ";
				interest_15.setBackgroundColor(getResources().getColor(R.color.color_blue));
				flag_15 = true;
				break;
			}
			
			}
			
		}
		
		return string;
	}
	
}
