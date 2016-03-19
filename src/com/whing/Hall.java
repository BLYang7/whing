package com.whing;

/**
 * 大厅页面
 * 从登陆页跳转进来的
 * 
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.proguard.k.e;
import com.whing.R;
import com.whing.adapter.HallAdapter;
import com.whing.estimation.EstimationCustomDialog;
import com.whing.estimation.ScoreAdapter;
import com.whing.external.MyListview;
import com.whing.external.PostData;
import com.whing.external.SetActData;
import com.whing.external.SetListViewHeight;
import com.whing.external.ShowDialog;
import com.whing.external.SlideMenu;
import com.whing.external.URLenum;
import com.whing.imgcut.CircleImg;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;

public class Hall extends Activity implements OnClickListener{

	private Button hallButtonLeft;
	private ScrollView hallScrollView;
	private ImageButton  hallButtonNewOne;
	private LinearLayout hallRun, hallBall, hallExercise, hallOther;
	private LinearLayout menuLeftUser, hallMain, hallListLayout;
	private TextView menuLeftUserActivity, menuLeftUserLetter, menuLeftUserFeedback,
			 menuLeftUserFocus, menuLeftUserExit, menuLeftUserName, menuLeftNetFlow;
	public static CircleImg menuLeftUserLogo, hallUserImageNavigationRight;
	private ImageView messageUnreadRemaind, runImage, ballImage, exerciseImage, otherImage;
	private LinearLayout menuLeftSystemMessage;
	private MyListview hallListView;
	public static SlideMenu slideMenu;
	
	private static HallAdapter adapter;
	
	private List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
	
	public static FeedbackAgent agent; 

	private View footView;
	
	private LayoutInflater inflater;
	
	private ProgressDialog pd;
	
	ShowDialog sd ;
	
	//设置线程池和handler
	public static Handler handler = new Handler();
    public static ExecutorService executorService = Executors.newFixedThreadPool(7);
    
    //设置初始化的偏移量和限度
    private static final int LIMIT = 7;
    private static final int OFFSET = 0;
    
    private static final int ALL = 0;
    private static final int OTHER = -1;
    private static final int RUN = 1;
    private static final int EXERCISE = 2;
    private static final int BALL = 3;

    public static final int REQUSET = 1;
    
    private static int KIND = ALL;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hall);
		
		init();    //初始化各个组件
		
		preNet();    //联网之前的准备
		
		setData();    //设置初始的展示数据
		
		setInf();    //设置用户名和头像
		
		setListener();    //设置各个组件的监听器
		
		//活动评论，在进入大厅时弹出
		new Thread(new Runnable(){
			@Override
			public void run() {
				Looper.prepare();
				estimation();
				Looper.loop();
			}
		}).start();
		
		//设置大厅的listView的条目点击监听
		hallListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if (!slideMenu.isMainScreenShowing()) {
					slideMenu.closeMenu();
					return;
				}
				
				Intent intent = new Intent(Hall.this, ActivityDetail.class);
				intent.putExtra("activityId", mData.get(position).get("activityId").toString());
				intent.putExtra("userImage", mData.get(position).get("userImage").toString());
				intent.putExtra("userName", mData.get(position).get("userName").toString());
				intent.putExtra("userGender", mData.get(position).get("userGender").toString());
				intent.putExtra("activityKind", mData.get(position).get("activityKind").toString());
				intent.putExtra("activityName", mData.get(position).get("activityName").toString());
				intent.putExtra("meetPlace", mData.get(position).get("meetPlace").toString());
				intent.putExtra("meetTime", mData.get(position).get("meetTime").toString());
				intent.putExtra("endTime", mData.get(position).get("endTime").toString());
				intent.putExtra("maxNumber", mData.get(position).get("maxNumber").toString());
				intent.putExtra("presentNumber", mData.get(position).get("presentNumber").toString());
				intent.putExtra("state", mData.get(position).get("state").toString());
				intent.putExtra("remark", mData.get(position).get("remark").toString());
				intent.putExtra("userId", mData.get(position).get("userId").toString());
				
				startActivity(intent);
			}
		});
	}
	
	/**
	 * 设置用户头像和用户名
	 */
	private void setInf(){
		hallUserImageNavigationRight.setImageDrawable(Login.userImageDrawable);
		menuLeftUserLogo.setImageDrawable(Login.userImageDrawable);
		
		if(!Login.userName.equals("")){
			menuLeftUserName.setText(Login.nickName);
		}
		
	}
	
	/**
	 * 初始化各组件
	 */
	private void init(){

		hallButtonLeft = (Button) findViewById(R.id.btn_left);
		hallButtonNewOne = (ImageButton) findViewById(R.id.btn_newOne);
		
		hallUserImageNavigationRight = (CircleImg) findViewById(R.id.hall_user_image_navigation_right);
		hallRun = (LinearLayout) findViewById(R.id.run);
		hallBall = (LinearLayout) findViewById(R.id.ball);
		hallExercise = (LinearLayout) findViewById(R.id.exercise);
		hallOther = (LinearLayout) findViewById(R.id.other);
		hallListView = (MyListview) findViewById(R.id.hall_listView);
		hallMain = (LinearLayout) findViewById(R.id.hall_main);
		hallListLayout = (LinearLayout) findViewById(R.id.hall_list_layout);
		
		runImage = (ImageView) findViewById(R.id.run_image);
		ballImage = (ImageView) findViewById(R.id.ball_image);
		exerciseImage = (ImageView) findViewById(R.id.exercise_image);
		otherImage = (ImageView) findViewById(R.id.other_image);

		menuLeftUser = (LinearLayout) findViewById(R.id.menu_left_user);
		menuLeftUserLogo = (CircleImg) findViewById(R.id.menu_left_user_logo);
		menuLeftUserName = (TextView) findViewById(R.id.menu_left_user_name);
		menuLeftUserActivity = (TextView) findViewById(R.id.menu_left_user_activity);
		menuLeftUserLetter = (TextView) findViewById(R.id.menu_left_user_letter);
		menuLeftSystemMessage = (LinearLayout) findViewById(R.id.menu_left_system_message);
		messageUnreadRemaind = (ImageView) findViewById(R.id.menu_left_system_message_unread_remaind);
		menuLeftUserExit = (TextView) findViewById(R.id.menu_left_user_exit);
		menuLeftUserFeedback = (TextView) findViewById(R.id.menu_left_user_feedback);
		menuLeftUserFocus = (TextView) findViewById(R.id.menu_left_user_focus);
		menuLeftNetFlow = (TextView) findViewById(R.id.menu_left_net_flow);
		
		hallScrollView = (ScrollView) findViewById(R.id.hall_scroll_view);
		
		slideMenu = (SlideMenu) findViewById(R.id.slide_menu);
		
		inflater = LayoutInflater.from(this);
		footView = inflater.inflate(R.layout.hall_listview_foot, null);
		
		sd = new ShowDialog(pd);
		
		PushAgent.getInstance(this).onAppStart();
	}
	
	
	/**
	 * Activity 回调要执行的操作，理解Activity创建的过程
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		
		setInf();
		remaind();  //系统未读通知的提醒
		
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	
	/**
	 * 开始启动时，下载mData的数据，并且保证当下载成功之后，适配adapter的值，
	 * 更新数据， 当然，这个方法必须在登陆完成之后才能调用
	 */
	private void setData() {
		
		sd.showDialog(this,"正在获取数据");
		
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				
				try {
					
					mData = SetActData.setData(LIMIT, OFFSET, ALL);
					
				} catch (JSONException e) {
					e.printStackTrace();
					return;
				}

				// 当下载数据完成之后，更新adapter的值
				handler.post(new Runnable() {
					@Override
					public void run() {
						sd.dismissDialog();
						adapter = new HallAdapter(Hall.this, mData);
						hallListView.addFooterView(footView);
						hallListView.setAdapter(adapter);
						SetListViewHeight.setListViewHeightBasedOnChildren(hallListView);
					}
				});
			}
		});

	}
	
	/**
	 * 退出的提示弹框
	 * @param view 响应退出的视图
	 */
	public void showSimpleDialog(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setMessage(getResources().getString(R.string.hall_exist));
		setPositiveButton(builder).create().show();
	}

	private AlertDialog.Builder setPositiveButton(AlertDialog.Builder builder) {
		return builder.setPositiveButton(R.string.positive_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						if (Login.prfPassword.exists()) {
							Login.prfPassword.delete();
						}
						
						if (Login.prfToken.exists()){
							Login.prfToken.delete();
						}
						
						if (Login.prfUserId.exists()){
							Login.prfUserId.delete();
						}
						
						MobclickAgent.onKillProcess(Hall.this);
						
						System.exit(0);
					}
				});
	}


	/**
	 * 联网的准备工作，
	 * 1.强制主进程内联网
	 * 2.定制目标服务器的URL
	 */
	private void preNet(){
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}

	
	/**
	 * 设置各组件的监听器
	 */
	private void setListener(){
		hallRun.setOnClickListener(this);
		hallBall.setOnClickListener(this);
		hallExercise.setOnClickListener(this);
		hallOther.setOnClickListener(this);
		hallUserImageNavigationRight.setOnClickListener(this);
		hallButtonLeft.setOnClickListener(this);
		hallButtonNewOne.setOnClickListener(this);
		hallMain.setOnClickListener(this);
		menuLeftUser.setOnClickListener(this);
		menuLeftSystemMessage.setOnClickListener(this);
		menuLeftUserActivity.setOnClickListener(this);
		menuLeftUserLetter.setOnClickListener(this);
		menuLeftUserFocus.setOnClickListener(this);
		menuLeftUserFeedback.setOnClickListener(this);
		menuLeftUserExit.setOnClickListener(this);
		menuLeftNetFlow.setOnClickListener(this);
		hallListLayout.setOnClickListener(this);
		
		hallScrollView.smoothScrollTo(0,0);
		hallScrollView.setOnClickListener(this);
		
		// 加载更多数据
		footView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (!slideMenu.isMainScreenShowing()) {
					slideMenu.closeMenu();
					return;
				}

				sd.showDialog(Hall.this, "正在加载");

				//向线程池递交数据，启动新线程，完成数据下载任务
				executorService.submit(new Runnable() {
					@Override
					public void run() {

						try {
							int offset = mData.size();
							Log.v("offset", offset + "");
							mData.addAll(SetActData.setData(LIMIT, offset, KIND));

						} catch (JSONException e) {
							sd.dismissDialog();
							Looper.prepare();
							Toast.makeText(Hall.this, "没有更多了", Toast.LENGTH_SHORT).show();
							Looper.loop();
							return;
						}
						
						// 当下载数据完成之后，更新adapter的值
						handler.post(new Runnable() {
							@Override
							public void run() {
								sd.dismissDialog();
								adapter.notifyDataSetChanged();
							}
						});
						
					}
				});

			}
			
		});
		
	}
	
	/**
	 * 各个组件的监听器的处理方案
	 * @param v 组件的view
	 */
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		
		//点击“我要发布”
		case R.id.btn_newOne:{
			
			if (!slideMenu.isMainScreenShowing()) {
				slideMenu.closeMenu();
				break;
			}
			
			if(Login.userName.equals("")){
				Toast.makeText(this, "请先登录/注册", Toast.LENGTH_SHORT).show();
			}
			else{
				Intent intent = new Intent(Hall.this, NewOne.class);
				intent.putExtra("ButtonFlag", 1);
				intent.putExtra("userName", Login.nickName);
				startActivity(intent);
			}

			break;
		}
		
		//点击跑步类型
		case R.id.run:{
			
			if (!slideMenu.isMainScreenShowing()) {
				slideMenu.closeMenu();
				break;
			}
			
			mDataUpdate(RUN);
			break;
		}
		
		//点击球类
		case R.id.ball:{
			
			if (!slideMenu.isMainScreenShowing()) {
				slideMenu.closeMenu();
				break;
			}
			
			mDataUpdate(BALL);
			break;
		}
		
		//点击健身
		case R.id.exercise:{
			
			if (!slideMenu.isMainScreenShowing()) {
				slideMenu.closeMenu();
				break;
			}
			
			mDataUpdate(EXERCISE);
			break;
		}
		
		//点击其他
		case R.id.other:{
			
			if (!slideMenu.isMainScreenShowing()) {
				slideMenu.closeMenu();
				break;
			}
			
			mDataUpdate(OTHER);
			break;
		}
			
		//点击导航栏右边的用户头像
		case R.id.hall_user_image_navigation_right:{
			
			if (slideMenu.isMainScreenShowing()) {
				slideMenu.openMenu();
			} else {
				slideMenu.closeMenu();
			}
			
			break;
		}
		
		
		//点击导航栏左边的logo
		case R.id.btn_left:{
			
			if (!slideMenu.isMainScreenShowing()) {
				slideMenu.closeMenu();
				break;
			}
			
			break;
		}
		
		//点击
		case R.id.hall_main:{
			
			if ( ! slideMenu.isMainScreenShowing() ) {
				slideMenu.closeMenu();
				break;
			}
			
			break;
		}
		
		//点击空白地方把侧边栏收起
		case R.id.hall_scroll_view:{
			
			if ( ! slideMenu.isMainScreenShowing() ) {
				slideMenu.closeMenu();
				break;
			}
			
			break;
		}
		
		
		case R.id.hall_list_layout:{
			
			if ( ! slideMenu.isMainScreenShowing() ) {
				slideMenu.closeMenu();
				break;
			}
			
			break;
		}
		
		//点击左边侧滑的用户信息
		case R.id.menu_left_user:{
			
			if (menuLeftUserName
					.getText()
					.toString()
					.equals(getResources().getString(
							R.string.menu_left_user_name_temp).toString())) {
				
				Intent intent = new Intent(Hall.this, Login.class);
				startActivityForResult(intent, REQUSET);
				
			} else {
				Intent intent = new Intent(Hall.this, MyInf.class);
				intent.putExtra("userName", menuLeftUserName.getText()
						.toString().trim());
				startActivity(intent);
			}
			
			break;
		}
		
		//点击侧滑栏系统通知（活动通知）
		case R.id.menu_left_system_message:{
			if (menuLeftUserName
					.getText()
					.toString()
					.equals(getResources().getString(
							R.string.menu_left_user_name_temp).toString())) {
				Toast.makeText(Hall.this, 
						getResources().getString(R.string.menu_left_click_notification), 
						Toast.LENGTH_SHORT)
						.show();
			} else {
				Intent intent = new Intent(Hall.this, SymMsgShow.class);
				startActivity(intent);
			}
			break;
		}
		
		//点击侧滑栏我的关注
		case R.id.menu_left_user_focus:{
			if (menuLeftUserName
					.getText()
					.toString()
					.equals(getResources().getString(
							R.string.menu_left_user_name_temp).toString())) {
				Toast.makeText(Hall.this, 
						getResources().getString(R.string.menu_left_click_notification), 
						Toast.LENGTH_SHORT)
						.show();
			} else {
				Intent intent = new Intent(Hall.this, MyFocus.class);
				startActivity(intent);
			}
			break;
		}
		
		//点击侧滑栏我的活动
		case R.id.menu_left_user_activity:{
			if (menuLeftUserName
					.getText()
					.toString()
					.equals(getResources().getString(
							R.string.menu_left_user_name_temp).toString())) {
				Toast.makeText(Hall.this, 
						getResources().getString(R.string.menu_left_click_notification), 
						Toast.LENGTH_SHORT)
						.show();
			} 
			else {
				Intent intent = new Intent(Hall.this, MyEvent.class);
				intent.putExtra("userName", menuLeftUserName.getText().toString().trim());
				startActivity(intent);
			}
			break;
		}
		
		//点击侧滑栏用户数据
		case R.id.menu_left_user_letter:{
			if (menuLeftUserName
					.getText()
					.toString()
					.equals(getResources().getString(
							R.string.menu_left_user_name_temp).toString())) {
				Toast.makeText(Hall.this, 
						getResources().getString(R.string.menu_left_click_notification), 
						Toast.LENGTH_SHORT)
						.show();
			} else {
				Intent intent = new Intent(Hall.this, UserLetter.class);
				startActivity(intent);
			}
			break;
		}
		
		//点击侧滑栏用户反馈
		case R.id.menu_left_user_feedback:{
			agent = new FeedbackAgent(this);
			agent.startFeedbackActivity();
			agent.removeWelcomeInfo();
			break;
		}
		
		//点击进入领取流量页面
		case R.id.menu_left_net_flow:{
			
			Intent intent = new Intent(Hall.this, NetFlow.class);
			startActivity(intent);
			break;
		}
		
		
		//点击侧滑栏退出登录
		case R.id.menu_left_user_exit:{
			
			//退出登录确定后，删除本地存储的用户信息，杀死进程，退出APP
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setPositiveButton("确认退出", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					//友盟推送绑定的撤销，在新线程中完成
					new Thread(new Runnable(){
						@Override
						public void run() {
							try {
								Login.mPushAgent.removeAlias(Login.user_id, "UID");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}).start();
					
					dialog.dismiss();
					
					if (Login.prfPassword.exists()) {
						Login.prfPassword.delete();
					}
					
					if (Login.prfToken.exists()){
						Login.prfToken.delete();
					}
					
					if (Login.prfUserId.exists()){
						Login.prfUserId.delete();
					}
					
					MobclickAgent.onKillProcess(Hall.this);
					
					System.exit(0);
				}
			});
			
			builder.create().show();
			break;
		}
		
		
		}
	}
	
	/**
	 * 改变mData的值
	 * @param kind 表示类别，1代表跑步，2代表健身，3代表球类，-1代表其他类
	 */
	private void mDataUpdate(final int kind){
		
		if(kind == RUN){
			sd.showDialog(Hall.this, "正在加载跑步活动信息");
		}
		else if(kind == BALL){
			sd.showDialog(Hall.this, "正在加载球类活动信息");
		}
		else if(kind == OTHER){
			sd.showDialog(Hall.this, "正在加载其他类活动信息");
		}
		else if(kind == EXERCISE){
			sd.showDialog(Hall.this, "正在加载健身活动信息");
		}
		
		executorService.submit(new Runnable(){
			@Override
			public void run() {
				
				int ActNumber = SetActData.actNumber(kind);
				
				if (ActNumber > 0) {
					
					KIND = kind;
					
					mData.clear();
					
					try {
						mData.addAll( SetActData.setData(LIMIT, OFFSET, kind) );
						sd.dismissDialog();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					handler.post(new Runnable() {
						@Override
						public void run() {
							
							adapter.notifyDataSetChanged();
							
							if(kind == RUN){
								runImage.setImageResource(R.drawable.run_blue);
								ballImage.setImageResource(R.drawable.ball_yellow);
								exerciseImage.setImageResource(R.drawable.exercise_yellow);
								otherImage.setImageResource(R.drawable.other_yellow);
							}
							else if(kind == BALL){
								runImage.setImageResource(R.drawable.run_yellow);
								ballImage.setImageResource(R.drawable.ball_blue);
								exerciseImage.setImageResource(R.drawable.exercise_yellow);
								otherImage.setImageResource(R.drawable.other_yellow);
							}
							else if(kind == OTHER){
								runImage.setImageResource(R.drawable.run_yellow);
								ballImage.setImageResource(R.drawable.ball_yellow);
								exerciseImage.setImageResource(R.drawable.exercise_yellow);
								otherImage.setImageResource(R.drawable.other_blue);
							}
							else if(kind == EXERCISE){
								runImage.setImageResource(R.drawable.run_yellow);
								ballImage.setImageResource(R.drawable.ball_yellow);
								exerciseImage.setImageResource(R.drawable.exercise_blue);
								otherImage.setImageResource(R.drawable.other_yellow);
							}
							
						}
					});

				}
				
				else{
					
					sd.dismissDialog();
					
					Looper.prepare();
					if( kind == OTHER ){
						Toast.makeText(Hall.this, "没有其他类的活动", Toast.LENGTH_SHORT).show();
					}
					else if(kind == RUN){
						Toast.makeText(Hall.this, "没有跑步类的活动", Toast.LENGTH_SHORT).show();
					}
					else if(kind == EXERCISE){
						Toast.makeText(Hall.this, "没有健身类的活动", Toast.LENGTH_SHORT).show();
					}
					else if(kind == BALL){
						Toast.makeText(Hall.this, "没有球类的活动", Toast.LENGTH_SHORT).show();
					}
					Looper.loop();
					
				}
			}
		});
	}
	
	//系统的其他一些提醒，暂时只有未读信息的提醒
	private void remaind() {
		executorService.submit(new Runnable(){

			@Override
			public void run() {
				
				String result = PostData.getData("", URLenum.get_notice_num);
				Log.v("未读通知数目", result);
				
				try {
					JSONObject json = new JSONObject(result);
					if(Integer.parseInt(json.get("number").toString()) == 0){
						handler.post(new Runnable(){
							@Override
							public void run() {
								messageUnreadRemaind.setBackgroundColor(getResources().getColor(R.color.color_hyaline));
							}
						});
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * 获取用户未评论的活动，并弹框
	 */
	private void estimation() {

		String result = PostData.getData("", URLenum.get_unesti_activity);

		JSONObject json;

		try {
			json = new JSONObject(result);
			String[] act_id = SetActData.JSArrayToStringArray(json.getJSONArray("activity_id"));

			for (int i = 0; i < act_id.length; i++) {

				String result_2 = PostData.getData("&act_id=" + act_id[i], URLenum.participant);

				List<HashMap<String, Object>> mDataRemaind = SetActData.setRemaindData(result_2, act_id[i]);
				
				showDialog(mDataRemaind, act_id[i]);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	
	/**
	 * 评分操作
	 * @param mDataRemaind
	 * @param act_id
	 */
	private void showDialog(List<HashMap<String, Object>> mDataRemaind, String act_id) {

		final String activity_id = act_id;
		
		EstimationCustomDialog.Builder builder = 
				new EstimationCustomDialog.Builder(Hall.this);
		builder.setTitle("请给活动成员评分");
		builder.setAdapter(Hall.this, mDataRemaind);

		final ScoreAdapter scoreAdapter = builder.getAdapter();

		builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				executorService.submit(new Runnable(){
					@Override
					public void run() {
						
						String result = PostData.postListData(
								scoreAdapter.outData, 
								URLenum.sub_estimation, 
								Login.token);
								
					}
							
				});
						
				dialog.dismiss();
			}
		});

		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,	int which) {
						
						String negtiveResult = PostData.getData(
								"&act_id=" + activity_id,
								URLenum.ref_estimation);
						
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

}

