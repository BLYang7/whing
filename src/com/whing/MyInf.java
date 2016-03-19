package com.whing;

/**
 * 个人信息页面
 * 用来展示个人信息，修改个人信息，修改个人密码，修改个人头像
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

	private static final int REQUESTCODE_PICK = 0; // 相册选图标记
	private static final int REQUESTCODE_TAKE = 1; // 相机拍照标记
	private static final int REQUESTCODE_CUTTING = 2; // 图片裁切标记
	
	private static final String IMAGE_FILE_NAME = MD5.md5(Login.user_id).substring(0, 8);// 头像文件名称
	
	private String imagePath;			// 图片本地路径
	
	private CircleImg userImage;    // 头像图片
	
	private ProgressDialog pd;  // 等待进度圈
	private ShowDialog sd;
	
	private String resultStr = "";	// 服务端返回结果集
	
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
		
		init();    //各组件的初始化
		
		setListener();    //设置各组件的监听器
		
		setInf();    //设置用户名和用户头像
		
		tabSet();    //tabHost的设置
		
		getInf_SetInf();    //从服务器获取用户的基本信息并展示
		
		
		

	}
	
	
	/**
	 * onResume() 和 onPause()方法中添加的MobclickAgent是用来实现umeng数据统计的
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
	
	
	
	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			
			menuWindow.dismiss();
			
			switch (v.getId()) {
			
			// 拍照
			case R.id.takePhotoBtn:{
				Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				
				// 下面这句指定调用相机拍照后的照片存储的路径
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
				
			// 相册选择图片
			case R.id.pickPhotoBtn:{
				Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
				// 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
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
		
		// 直接从相册获取
		case REQUESTCODE_PICK:{
			try {
				startPhotoZoom(data.getData());
			} catch (NullPointerException e) {
				e.printStackTrace();// 用户点击取消操作
			}
			break;
		}
		
		// 调用相机拍照
		case REQUESTCODE_TAKE:{
			File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME +".jpg");
			startPhotoZoom(Uri.fromFile(temp));
			break;
		}
			
		// 取得裁剪后的图片
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
	 * 裁剪图片方法实现
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, REQUESTCODE_CUTTING);
	}
		
	/**
	 * 保存裁剪之后的图片数据
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		
		if (extras != null) {
			
			// 取得SDCard图片路径做显示
			Bitmap photo = extras.getParcelable("data");
			
			Drawable drawable = new BitmapDrawable(null, photo);
			
			//保存图片到本地，并且得到路径名
			imagePath = FileUtil.saveFile(mContext, Login.user_id, photo);
			
			userImage.setImageDrawable(drawable);
			
			Login.userImageDrawable = drawable;
			
			// 新线程后台上传服务端
			sd.showDialog(mContext, "正在上传图片，请稍候...");
			
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
			
			// 重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
			UploadManager uploadManager = new UploadManager();
			
			uploadManager.put(file, null, token,new UpCompletionHandler() {
				
			    @Override
			    public void complete(String key, ResponseInfo info, JSONObject response) {
			    	resultStr = response+"";
			        handler.sendEmptyMessage(0);// 执行耗时的方法之后发送消给handler
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
						Toast.makeText(MyInf.this, "上传成功", Toast.LENGTH_SHORT).show();
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
	 * 获取用户编辑之后的信息，用作修改
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
	 * 设置用户头像和用户名
	 */
	private void setInf(){
		myInfUserFollowImageName.setText(Login.nickName);
		myInfUserImage.setImageDrawable(Login.userImageDrawable);
		userImage.setImageDrawable(Login.userImageDrawable);
		
	}

	/**
	 * 设置各个组件的监听器
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
		
		// 返回按钮的点击效果
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
	 * 从服务器获取用户信息并展示
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
									myInfUserGender.setText("男");
								}
								else{
									myInfUserGender.setText("女");
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
	 * tabHost内的选项的添加以及初始设置
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
	 * 初始化各组件
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
	 * 各组件触发操作
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
					 * 修改个人信息
					 */
					JSONObject jsonPostInf = null;
					int errcode = 0;
					
					/**
					 * 检查用户名是否有变化，如果有变化，要对用户名进行数据库重复性检查，
					 * 检查通过之后可以执行下面update数据的操作。如果没有变化，则直接进行update的操作
					 */
					String result = PostData.postData(getInfEdit(), URLenum.update_info, Login.token);
					Log.v("修改信息的返回值", result);
					
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
								Toast.makeText(MyInf.this, "修改成功", Toast.LENGTH_SHORT).show();
								
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
			 * 修改用户密码
			 */
			sd.showDialog(mContext, "正在上传，请稍候...");
			
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
										Toast.makeText(MyInf.this, "更换密码成功", Toast.LENGTH_SHORT).show();
										
										//将修改后的密码保存到本地
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
								Toast.makeText(MyInf.this, "原密码错误", Toast.LENGTH_SHORT).show();
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
			
			// 设置menuWindow的弹出位置
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
			Toast.makeText(this, "新密码两次输入不一致", Toast.LENGTH_SHORT).show();
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
