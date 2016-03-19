package com.whing;

/**
 * 个人信息的展示
 * 包含信息页以及是否关注
 */

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.whing.external.PostData;
import com.whing.external.SetActData;
import com.whing.external.ShowDialog;
import com.whing.external.URLenum;
import com.whing.imgcut.CircleImg;
import com.whing.imgcut.DownLodeImg;
import com.whing.imgcut.FileUtil;
import com.whing.imgcut.SetUserImage;
import com.whing.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class InfShow extends Activity implements OnClickListener{
	
	Button infShowNavigationLeft, infShowUserFocus;
	ImageView infShowUserGenderImage;
	TextView infShowTitle, infShowUserLetter;
	TextView infShowUserRealName, infShowUserAccountTop, infShowUserAccount, infShowUserNickName, 
			infShowUserSchool, infShowUserMobile, infShowUserQQ, infShowUserInterest, 
			infShowUserCollege, infShowUserEstimation, infShowUserGender;
	
	CircleImg infShowUserImage;
	
	String userImage; //做私信时传递给下一个Activity的图片数据
	
	ProgressDialog pd;
	ShowDialog sd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inf_show);
		
		init();  //初始化各个组件
		
		setListener();  //设置各个组件的监听器
		
		setInf();  //get并且设置用户信息
		
	}
	
	/**
	 * 设置各组件的监听器
	 */
	private void setListener(){
		infShowNavigationLeft.setOnClickListener(this);
		infShowUserLetter.setOnClickListener(this);
		infShowUserFocus.setOnClickListener(this);
		
		//关注按钮的点击效果显示
		infShowUserFocus.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					infShowUserFocus.setBackgroundResource(R.drawable.radio_button_new_one_down);
				}
				else if(event.getAction() == MotionEvent.ACTION_UP){
					infShowUserFocus.setBackgroundResource(R.drawable.radio_button_new_one_normal);
				}
				return false;
			}
		});
		
		
		// 返回按钮的点击效果
		infShowNavigationLeft.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					infShowNavigationLeft.setBackgroundResource(R.drawable.login_ensure_button_down);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					infShowNavigationLeft.setBackgroundResource(R.drawable.navigation_back);
				}
				return false;
			}
		});
		
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
	 * 各组件的初始化
	 */
	private void init(){
		infShowNavigationLeft = (Button) findViewById(R.id.inf_show_navigation_left);
		infShowUserImage = (CircleImg) findViewById(R.id.inf_show_user_image);
		infShowUserGenderImage = (ImageView) findViewById(R.id.inf_show_user_gender_image);
		infShowTitle = (TextView) findViewById(R.id.inf_show_title);
		infShowUserAccountTop = (TextView) findViewById(R.id.inf_show_user_account_top);
		infShowUserLetter = (TextView) findViewById(R.id.inf_show_user_letter);
		infShowUserRealName = (TextView) findViewById(R.id.inf_show_user_real_name);
		infShowUserSchool = (TextView) findViewById(R.id.inf_show_user_school);
		infShowUserMobile = (TextView) findViewById(R.id.inf_show_user_mobile);
		infShowUserInterest = (TextView) findViewById(R.id.inf_show_user_interest);
		infShowUserAccount = (TextView) findViewById(R.id.inf_show_user_account);
		infShowUserNickName = (TextView) findViewById(R.id.inf_show_user_nickname);
		infShowUserCollege = (TextView) findViewById(R.id.inf_show_user_college);
		infShowUserQQ = (TextView) findViewById(R.id.inf_show_user_qq);
		infShowUserGender = (TextView) findViewById(R.id.inf_show_user_gender);
		infShowUserEstimation = (TextView) findViewById(R.id.inf_show_user_estimation);
		infShowUserFocus = (Button) findViewById(R.id.inf_show_user_focus);
		
		sd = new ShowDialog(pd);
		
		PushAgent.getInstance(this).onAppStart();
	}

	/**
	 * 设置展示的信息
	 */
	private void setInf(){
		
		sd.showDialog(this, "正在获取信息");
		
		//使用线程池，调用其中一个线程来处理联网加载信息
		Hall.executorService.submit(new Runnable(){

			@Override
			public void run() {
				String userId = (String) getIntent().getExtras().get("userId");
				final String str = "&"+ "uid=" + userId;
				final String result = PostData.getData(str, URLenum.get_info);
				
				//使用handler.post来更新信息
				Hall.handler.post(new Runnable(){

					@Override
					public void run() {
						
						sd.dismissDialog();
						
						JSONObject json;
						try {
							json = new JSONObject(result);
							
							infShowUserAccountTop.setText(json.getString("nickname"));
							infShowTitle.setText(json.getString("nickname"));
							infShowUserRealName.setText(json.getString("realname"));
							infShowUserNickName.setText(json.getString("nickname"));
							infShowUserAccount.setText(json.getString("account"));
							infShowUserSchool.setText(json.getString("university"));
							infShowUserCollege.setText(json.getString("college"));
							infShowUserMobile.setText(json.getString("mobile"));
							infShowUserQQ.setText(json.getString("qq"));
							infShowUserEstimation.setText(json.getString("estimation"));
							
							String[] interest = SetActData.JSArrayToStringArray(json.getJSONArray("interest"));
							String interestStr = interestString(interest);
							infShowUserInterest.setText(interestStr);
							
							userImage = json.getString("photo");
							
							Drawable drawable = SetUserImage.setImage(
									InfShow.this,
									InfShow.this.getResources(), 
									json.getString("user_id").trim(),
									userImage.trim());
							
							infShowUserImage.setImageDrawable(drawable);
							
							//性别设计  数据1表示男   数据0表示女
							if(json.getString("sex").trim().equals("0")){
								infShowUserGenderImage.setImageDrawable(InfShow.this.getResources().getDrawable(R.drawable.female));
								infShowUserGender.setText("女");
							}
							else{
								infShowUserGenderImage.setImageDrawable(InfShow.this.getResources().getDrawable(R.drawable.male));
								infShowUserGender.setText("男");
							}
							
							//用户之间的关系
							if(json.get("relation").toString().equals("2") 
									|| 
									json.get("relation").toString().equals("3")){
								
								infShowUserFocus.setText("已关注");
							}
							else{
								infShowUserFocus.setText("关注");
							}
							
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
	
	
	/**
	 * 各种监听器的响应
	 */
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		
		case R.id.inf_show_navigation_left:{
			finish();
			break;
		}
		
		//私信
		case R.id.inf_show_user_letter:{
			if(Login.user_id.equals(getIntent().getExtras().get("userId"))){
				break;
			}
			
			Intent intent = new Intent(InfShow.this, LetterChat.class);
			intent.putExtra("user_id", (String) getIntent().getExtras().get("userId"));
			intent.putExtra("userImage",userImage);
			startActivity(intent);
			break;
		}
		
		//关注
		case R.id.inf_show_user_focus:{
			
			if(infShowUserFocus.getText().toString().trim().equals("关注")){
				String result = PostData.getData("&follower_id="+((String)getIntent()
						.getExtras().get("userId")).trim(), URLenum.follow);
				
				Log.v("关注用户", result);
				try {
					JSONObject json = new JSONObject(result);
					if(Integer.parseInt(json.get("errcode").toString()) == 0){
						infShowUserFocus.setText("已关注");
						Toast.makeText(this, "关注成功", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			else if(infShowUserFocus.getText().toString().trim().equals("已关注")){
				String result = PostData.getData(
						"&follower_id="+((String)getIntent().getExtras().get("userId")).trim(), 
						URLenum.unfollow);
				Log.v("unfollow", result);
				
				try {
					JSONObject json = new JSONObject(result);
					String errcode = json.get("errcode").toString();
					
					if(errcode.equals("0")){
						infShowUserFocus.setText("关注");
						Toast.makeText(this, "已取消关注", Toast.LENGTH_SHORT).show();
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			break;
		}
		
		}
		
	}

	
	/**
	 * 显示用户的兴趣
	 * @param strings
	 * @return
	 */
	private String interestString(String[] strings) {

		String string = "";

		for (int i = 0; i < strings.length; i++) {

			int num = Integer.parseInt(strings[i]);

			switch (num) {

			case 1: {
				string = string + getResources().getString(R.string.interest_1)
						+ " ";
				break;
			}

			case 2: {
				string = string + getResources().getString(R.string.interest_2)
						+ " ";
				break;
			}

			case 3: {
				string = string + getResources().getString(R.string.interest_3)
						+ " ";
				break;
			}

			case 4: {
				string = string + getResources().getString(R.string.interest_4)
						+ " ";
				break;
			}

			case 5: {
				string = string + getResources().getString(R.string.interest_5)
						+ " ";
				break;
			}

			case 6: {
				string = string + getResources().getString(R.string.interest_6)
						+ " ";
				break;
			}

			case 7: {
				string = string + getResources().getString(R.string.interest_7)
						+ " ";
				break;
			}

			case 8: {
				string = string + getResources().getString(R.string.interest_8)
						+ " ";
				break;
			}

			case 9: {
				string = string + getResources().getString(R.string.interest_9)
						+ " ";
				break;
			}

			case 10: {
				string = string
						+ getResources().getString(R.string.interest_10) + " ";
				break;
			}

			case 11: {
				string = string
						+ getResources().getString(R.string.interest_11) + " ";
				break;
			}

			case 12: {
				string = string
						+ getResources().getString(R.string.interest_12) + " ";
				break;
			}

			case 13: {
				string = string
						+ getResources().getString(R.string.interest_13) + " ";
				break;
			}

			case 14: {
				string = string
						+ getResources().getString(R.string.interest_14) + " ";
				break;
			}

			case 15: {
				string = string
						+ getResources().getString(R.string.interest_15) + " ";
				break;
			}

			default: {
				break;
			}

			}

		}

		return string;
	}

	/**
	 * 当前页面被销毁时，启动新线程，下载用户的头像图片
	 * 实现用户头像的更新
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		new Thread(new Runnable(){

			@Override
			public void run() {
				
				Bitmap bitmap;
				
				try {
					bitmap = DownLodeImg.downLodeImg(userImage);
					FileUtil.saveFile(InfShow.this, (String) getIntent().getExtras().get("userId"), bitmap);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
		}).start();
		
	}
	
	
	
}
