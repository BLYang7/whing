package com.whing;

/**
 * 活动详情页
 * 展示活动的具体内容，参加者，活动评论，活动分享等等内容
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.whing.adapter.ReviewAdapter;
import com.whing.debug.LOG;
import com.whing.external.MyListview;
import com.whing.external.PopupEdit;
import com.whing.external.PopupEdit2;
import com.whing.external.PostData;
import com.whing.external.SetActData;
import com.whing.external.SetListViewHeight;
import com.whing.external.URLenum;
import com.whing.imgcut.CircleImg;
import com.whing.imgcut.SetUserImage;
import com.whing.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityDetail extends Activity implements OnClickListener{

	Button activityDetailNavigationLeft, activityDetailChooseButton;
	ImageView activityDetailUserGender, activityDetailComment, activityDetailShare, 
		activityDetailActivityKind, activityDetailMember;
	TextView activityDetailUserName, activityDetailActivityName, 
		activityDetailMeetPlace, activityDetailMeetTime, activityDetailEndTime, activityDetailNumber, activityDetailDetailShow;
	MyListview activityDetailCommentListView;
	CircleImg activityDetailUserImage;
	
	ScrollView activityDetailScrollView;
	
	private PopupEdit menuWindow;  //第一个弹出框，用户用户评论活动
	private PopupEdit2 menuWindow2;  //第二个弹出框，用于用户回复某条评论
	public static Context mContext;
	
	ReviewAdapter adapter;
	
	List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
	String activityId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		
		init();    //各组件的初始化
		
		setListener();    //设置各组件的监听器
		
		setInf();    //设置activitydetail的各个内容
		
		setData();    //设置mData的内容
		
		setActionShow();    //设置按钮的按下去的效果
		
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
	 * 设置各组件的监听器
	 */
	private void setListener(){
		activityDetailNavigationLeft.setOnClickListener(this);
		activityDetailChooseButton.setOnClickListener(this);
		activityDetailUserImage.setOnClickListener(this);
		activityDetailComment.setOnClickListener(this);
		activityDetailMember.setOnClickListener(this);
		activityDetailShare.setOnClickListener(this);
		
		activityDetailScrollView.smoothScrollTo(0,20);
		
		//当点击评论消息条目的时候，表示给当前的人做回复
		activityDetailCommentListView.setOnItemClickListener(new OnItemClickListener() {
					@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				
				ItemClick onClick = new ItemClick(position);
				
				menuWindow2 = new PopupEdit2(mContext, onClick);
				
				// 设置menuWindow的弹出位置
				menuWindow2.showAtLocation(findViewById(R.id.act_detail_layout),
								Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			}
		});
		
		
		// 返回按钮的点击效果
		activityDetailNavigationLeft.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					activityDetailNavigationLeft.setBackgroundResource(R.drawable.login_ensure_button_down);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					activityDetailNavigationLeft.setBackgroundResource(R.drawable.navigation_back);
				}
				return false;
			}
		});

	}
 
	/**
	 * 初始化各个组件
	 */
	private void init(){
		
		mContext = ActivityDetail.this;
		
		activityDetailNavigationLeft = (Button) findViewById(R.id.activity_detail_navigation_left);
		activityDetailChooseButton = (Button) findViewById(R.id.activity_detail_choose_button);
		activityDetailUserImage = (CircleImg) findViewById(R.id.activity_detail_user_image);
		activityDetailUserGender = (ImageView) findViewById(R.id.activity_detail_user_gender);
		activityDetailComment = (ImageView) findViewById(R.id.activity_detail_comment);
		activityDetailShare = (ImageView) findViewById(R.id.activity_detail_share);
		activityDetailUserName = (TextView) findViewById(R.id.activity_detail_user_name);
		activityDetailActivityName = (TextView) findViewById(R.id.activity_detail_activity_name);
		activityDetailActivityKind = (ImageView) findViewById(R.id.activity_detail_activity_kind);
		activityDetailMeetPlace = (TextView) findViewById(R.id.activity_detail_meet_place);
		activityDetailMeetTime = (TextView) findViewById(R.id.activity_detail_meet_time);
		activityDetailEndTime = (TextView) findViewById(R.id.activity_detail_end_time);
		activityDetailNumber = (TextView) findViewById(R.id.activity_detail_number);
		activityDetailDetailShow = (TextView) findViewById(R.id.activity_detail_detail_show);
		activityDetailCommentListView = (MyListview) findViewById(R.id.activity_detail_comment_listview);
		activityDetailMember = (ImageView) findViewById(R.id.activity_member);
		
		activityDetailScrollView = (ScrollView) findViewById(R.id.activity_detail_scrollview);
		
		activityId = getIntent().getExtras().get("activityId").toString().trim();
		
		PushAgent.getInstance(this).onAppStart();
	}
	
	
	private void setInf(){
		//用户头像设置  暂时还没处理
		
		Drawable drawable = SetUserImage.setImage(
				this,
				getResources(), 
				getIntent().getExtras().get("userId").toString(),
				getIntent().getExtras().get("userImage").toString());
		activityDetailUserImage.setImageDrawable(drawable);
		
		activityDetailUserName.setText(getIntent().getExtras().get("userName").toString());
		activityDetailActivityName.setText(getIntent().getExtras().get("activityName").toString());
		activityDetailMeetPlace.setText(getIntent().getExtras().get("meetPlace").toString());
		activityDetailUserName.setText(getIntent().getExtras().get("userName").toString());
		activityDetailDetailShow.setText(getIntent().getExtras().get("remark").toString());
		activityDetailMeetTime.setText(getIntent().getExtras().get("meetTime").toString());
		activityDetailEndTime.setText(getIntent().getExtras().get("endTime").toString());
		
		// 设置活动类型,1表示跑步，3表示球类，2表示健身，4表示其他
		if (getIntent().getExtras().get("activityKind").equals("1")) {
			activityDetailActivityKind.setImageDrawable(getResources().getDrawable(R.drawable.run_yellow));
		}
		else if (getIntent().getExtras().get("activityKind").equals("2")) {
			activityDetailActivityKind.setImageDrawable(getResources().getDrawable(R.drawable.exercise_yellow));
		}
		else if (getIntent().getExtras().get("activityKind").equals("3")) {
			activityDetailActivityKind.setImageDrawable(getResources().getDrawable(R.drawable.ball_yellow));
		}
		else {
			activityDetailActivityKind.setImageDrawable(getResources().getDrawable(R.drawable.other_yellow));
		}

		// 设置性别，返回的数据是1，表示男性，数据是0表示女性
		if (getIntent().getExtras().get("userGender").equals("1")) {
			activityDetailUserGender.setImageDrawable(getResources().getDrawable(R.drawable.male));
		}
		else{
			activityDetailUserGender.setImageDrawable(getResources().getDrawable(R.drawable.female));
		}
		
		//设置用户数
		{
			int presentNumber = Integer.parseInt(getIntent().getExtras().getString("presentNumber"));
			int maxNumber = Integer.parseInt(getIntent().getExtras().getString("maxNumber"));
			String numberShow = presentNumber + "/" + maxNumber;
			activityDetailNumber.setText(numberShow);
		}
		
		//设置活动选择按钮的显示状态
		setState();
		
	}
	
	
	
	/**
	 * 各监听器的响应
	 */
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		
		//回退键
		case R.id.activity_detail_navigation_left:{
			finish();
			break;
		}
			
		//点击用户头像，进入用户信息页
		case R.id.activity_detail_user_image: {
			Intent intent = new Intent(ActivityDetail.this, InfShow.class);
			intent.putExtra("userId", getIntent().getExtras().get("userId").toString());
			startActivity(intent);
			break;
		}
			
		//点击活动成员键
		case R.id.activity_member:{
			Intent intent = new Intent(ActivityDetail.this, ActMember.class);
			intent.putExtra("activityId", activityId);
			startActivity(intent);
			break;
		}
		
		//点击活动评论键
		case R.id.activity_detail_comment:{
			
			menuWindow = new PopupEdit(mContext, itemsOnClick);
			
			// 设置menuWindow的弹出位置
			menuWindow.showAtLocation(findViewById(R.id.act_detail_layout),
					Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			
			break;
		}
		
		//点击活动分析那个键
		case R.id.activity_detail_share:{
			Intent intent=new Intent(Intent.ACTION_SEND); 
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT, shareWords());
			startActivity(Intent.createChooser(intent, "请选择")); 
			break;
		}
			
		default:{
			break;
		}
		
		}
	}
	
	
	/**
	 * 设置评论数据
	 */
	private void setData(){
		
		String str = "&act_id="+activityId;
		String result = PostData.getData(str, URLenum.get_review);
		if(LOG.DEBUG){
			Log.v("review", result);
		}
		
		try {
			JSONObject json = new JSONObject(result);
		
			String[] userName = SetActData.JSArrayToStringArray(json.getJSONArray("user_nickname"));
			String[] userId = SetActData.JSArrayToStringArray(json.getJSONArray("user_id"));
			String[] replyName = SetActData.JSArrayToStringArray(json.getJSONArray("reply_nickname"));
			String[] replyId = SetActData.JSArrayToStringArray(json.getJSONArray("reply_id"));
			String[] content = SetActData.JSArrayToStringArray(json.getJSONArray("content"));
			String[] reviewId = SetActData.JSArrayToStringArray(json.getJSONArray("review_id"));
			String[] reference = SetActData.JSArrayToStringArray(json.getJSONArray("reference"));
			String[] userImage = SetActData.JSArrayToStringArray(json.getJSONArray("user_photo"));
			
			for(int i=0; i<userName.length; i++){
				
				Map<String, Object> listItem = new HashMap<String, Object>();
				listItem.put("userName", userName[i]);
				listItem.put("content", content[i]);
				listItem.put("userId", userId[i]);
				listItem.put("reference", reference[i]);
				listItem.put("replyName", replyName[i]);
				listItem.put("replyId", replyId[i]);
				listItem.put("reviewId", reviewId[i]);
				listItem.put("userImage", userImage[i]);
				
				mData.add(listItem);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		adapter = new ReviewAdapter(ActivityDetail.this, mData);
		activityDetailCommentListView.setAdapter(adapter);
		SetListViewHeight.setListViewHeightBasedOnChildren(activityDetailCommentListView);
		
	}
	
	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			
			menuWindow.dismiss();
			
			switch(v.getId()){
			
			case R.id.popup_edit_btn_send:{
				
				String msg = "\"" + menuWindow.edit.getText().toString() + "\"";
				
				Map<String, String> map = new HashMap<String,  String>();
				map.put("activity_id", activityId);
				map.put("reference",  "0");
				map.put("content", msg);
				String result = PostData.postData(map, URLenum.release_review, Login.token);
				
				try {
					JSONObject json = new JSONObject(result);
					if(json.get("errcode").equals(0)){
						
						Map<String, Object> listItem = new HashMap<String, Object>();
						listItem.put("userName", Login.nickName);
						listItem.put("content", menuWindow.edit.getText().toString());
						listItem.put("userId", Login.user_id);
						listItem.put("reference", "0");
						listItem.put("replyName", "");
						listItem.put("replyId", "");
						listItem.put("reviewId", json.get("review_id").toString());
						listItem.put("userImage", "oo");
						
						mData.add(listItem);
						
						adapter.notifyDataSetChanged();
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
			
		}
	};
		
	
	private class ItemClick implements OnClickListener{

		private int position;
		
		public ItemClick(int position){
			this.position = position;
		}
		
		@Override
		public void onClick(View v) {
			
			menuWindow2.dismiss();
			
			String msg = "\"" + menuWindow2.edit.getText().toString() + "\"";
			
			Map<String, String> map = new HashMap<String, String>();
			map.put("activity_id", activityId);
			map.put("reference", mData.get(position).get("reviewId").toString());
			map.put("content", msg);
			String result = PostData.postData(map, URLenum.release_review, Login.token);

			try {
				JSONObject json = new JSONObject(result);
				
				if (json.get("errcode").equals(0)) {
					
					Map<String, Object> listItem = new HashMap<String, Object>();
					listItem.put("userName", Login.nickName);
					listItem.put("content", menuWindow2.edit.getText().toString());
					listItem.put("userId", Login.user_id);
					
					listItem.put("reference", mData.get(position).get("reviewId").toString());
					
					listItem.put("replyName", mData.get(position).get("userName").toString());
					listItem.put("replyId", mData.get(position).get("userId").toString());
					
					listItem.put("reviewId", json.get("review_id").toString());
					listItem.put("userImage", "oo");
					
					
					mData.add(listItem);
					adapter.notifyDataSetChanged();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 分享的内容
	 * @return 分享的字符串
	 */
	private String shareWords(){
		
		String string = "互·应有个活动，我想和你一起参加！";
		return string + URLenum.PATHactdetail + activityId;
	}
	
	
	/**
	 * 设置显示按钮的状态
	 * -1表示参加，0表示申请中， 1表示已参加，2表示被拒绝，
	 */
	private void setState(){
		
		//对已参加的活动的处理
		if (getIntent().getExtras().get("state").equals("1")) {
			
			activityDetailChooseButton.setBackground(getResources().getDrawable(R.drawable.simple_item_already_in));
			activityDetailChooseButton.setText("已参加");
			
			activityDetailChooseButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDetail.this);
					builder.setPositiveButton("退出确认", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							
							String str = "&act_id=" + getIntent().getExtras().get("activityId");
							String result = PostData.getData(str, URLenum.quit);
							Log.v("加入活动的返回值：", result);
							
							try {
								JSONObject json = new JSONObject(result);
								
								if(json.get("errcode").toString().trim().equals("292") ){
									Toast.makeText(ActivityDetail.this, "不允许发布者退出", Toast.LENGTH_SHORT).show();
								}
								else if(json.get("errcode").toString().trim().equals("0")){
									
									AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDetail.this);
									builder.setPositiveButton("已退出该活动", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											finish();
											dialog.dismiss();
										}
									});
									builder.create().show();
									
								}
								else{
									Toast.makeText(mContext, "errcode:" + json.getString("errcode"), Toast.LENGTH_SHORT).show();
								}
								
							} catch (JSONException e) {
								e.printStackTrace();
							}
							
						}
					});
					builder.create().show();
					
				}
			});
		}
		
		//若不是已参加的活动，就要判断是否人数已满
		else if(getIntent().getExtras().getString("presentNumber").trim()
				.equals(getIntent().getExtras().getString("maxNumber").trim())){
			
			activityDetailChooseButton.setBackground(getResources().getDrawable(R.drawable.simple_item_full));
			activityDetailChooseButton.setText("已满");
			
			activityDetailChooseButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Toast.makeText(ActivityDetail.this, "人数已满", Toast.LENGTH_SHORT).show();
				}
			});
		}
		
		//对未参加的活动的处理
		else if (getIntent().getExtras().get("state").equals("-1")) {
			
			activityDetailChooseButton.setBackground(getResources().getDrawable(R.drawable.simple_item_in));
			activityDetailChooseButton.setText("参加");
					
			activityDetailChooseButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDetail.this);
					builder.setPositiveButton("加入确认", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							
							Toast.makeText(ActivityDetail.this, "已递交加入申请", Toast.LENGTH_SHORT).show();
							
							String str = "&act_id="	+ getIntent().getExtras().get("activityId");
							String result = PostData.getData(str, URLenum.join);

							Log.v("加入活动的返回值：", result);
							try {
								JSONObject json = new JSONObject(result);
								
								if (json.get("errcode").toString().trim().equals("0")) {
									AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDetail.this);
									builder.setPositiveButton("加入成功", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
											finish();
										}
									});
									builder.create().show();
								}
								else if(json.get("errcode").toString().trim().equals("250")){
									AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDetail.this);
									builder.setPositiveButton("当前人数已满", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
										}
									});
									builder.create().show();
								}
								else if(json.get("errcode").toString().trim().equals("252")){
									AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDetail.this);
									builder.setPositiveButton("活动报名截止时间已过", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
										}
									});
									builder.create().show();
								}
								
								
							} catch (JSONException e) {
								e.printStackTrace();
							}
							
						}
					});
					builder.create().show();
					
				}
			});
		} 
		
		//对申请中的活动的处理
		else if (getIntent().getExtras().get("state").equals("0")) {
			
			activityDetailChooseButton.setBackground(getResources().getDrawable(R.drawable.simple_item_already_in));
			activityDetailChooseButton.setText("申请中");
			
			activityDetailChooseButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Toast.makeText(mContext, "正在申请，请耐心等待", Toast.LENGTH_SHORT).show();
				}
			});
		} 
		
		//对被拒绝活动的处理
		else if(getIntent().getExtras().get("state").equals("2")){
			
			activityDetailChooseButton.setBackground(getResources().getDrawable(R.drawable.simple_item_full));
			activityDetailChooseButton.setText("被拒绝");
			
			activityDetailChooseButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Toast.makeText(mContext, "已被拒绝，不允许再加入", Toast.LENGTH_SHORT).show();
				}
			});
			
		}
		
		//对已过期的活动的处理
		else if(getIntent().getExtras().get("state").equals("3")){
			activityDetailChooseButton.setBackground(getResources().getDrawable(R.drawable.simple_item_full));
			activityDetailChooseButton.setText("已过期");
			
			activityDetailChooseButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Toast.makeText(mContext, "活动已过期", Toast.LENGTH_SHORT).show();
				}
			});
		}
		
		else if(getIntent().getExtras().get("state").toString().equals("5")){

			activityDetailChooseButton.setBackground(getResources().getDrawable(R.drawable.simple_item_full));

			activityDetailChooseButton.setText("已退出");

			activityDetailChooseButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(ActivityDetail.this, "已退出，不允许再加入", Toast.LENGTH_SHORT).show();
				}
			});
		}
		
		//对已完成活动的处理
		else if(getIntent().getExtras().get("state").toString().equals("7")){
			activityDetailChooseButton.setBackground(getResources().getDrawable(R.drawable.simple_item_full));
			activityDetailChooseButton.setText("已完成");
			
			activityDetailChooseButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Toast.makeText(mContext, "当前活动已完成", Toast.LENGTH_SHORT).show();
				}
			});
			
		}
		
	}
	
	/**
	 * 设置几个按钮的按下效果
	 */
	private void setActionShow(){
		
		activityDetailMember.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					activityDetailMember.setBackgroundResource(R.drawable.activity_member_down);
				}
				else if(event.getAction() == MotionEvent.ACTION_UP){
					activityDetailMember.setBackgroundResource(R.drawable.activity_member);
				}
				return false;
			}
		});
		
		activityDetailShare.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					activityDetailShare.setBackgroundResource(R.drawable.share_down);
				}
				else if(event.getAction() == MotionEvent.ACTION_UP){
					activityDetailShare.setBackgroundResource(R.drawable.share);
				}
				return false;
			}
		});
		
		activityDetailComment.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					activityDetailComment.setBackgroundResource(R.drawable.comment_down);
				}
				else if(event.getAction() == MotionEvent.ACTION_UP){
					activityDetailComment.setBackgroundResource(R.drawable.comment);
				}
				return false;
			}
		});
		
	}
}



