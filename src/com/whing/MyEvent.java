package com.whing;

/**
 * 我的活动页面
 * 用来展示用户正在参与的活动和已经参与的活动
 * 
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.whing.adapter.MyEventAdapter;
import com.whing.external.MyListview;
import com.whing.external.SetActData;
import com.whing.external.SetListViewHeight;
import com.whing.external.ShowDialog;
import com.whing.imgcut.CircleImg;
import com.whing.R;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.TabSpec;

public class MyEvent extends TabActivity implements OnClickListener{
	
	Button myEventNavigationLeft;
	CircleImg myEventUserImage;
	TextView myEventUserId, myEventOn, myEventOff;
	MyListview myEventOnList,myEventOffList;
	
	List<Map<String, Object>> mDataOn = new ArrayList<Map<String, Object>>();
	List<Map<String, Object>> mDataOff = new ArrayList<Map<String, Object>>();
	
	TabHost tabHost;
	MyEventAdapter adapterOn;
	MyEventAdapter adapterOff;
	
	ScrollView myEventScrollView;
	private ProgressDialog pd;
	private ShowDialog sd;
	
	private View footViewOn;
	private View footViewOff;
	private LayoutInflater inflate;
	
	//设置初始化的偏移量和限度
    private static final int LIMIT = 7;
    private static final int OFFSET = 0;
    
    private static final int PROCESSING = -3;
    private static final int FINISHED = -2;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_event);
		
		tabHost = getTabHost();
		
		init();    //组件初始化
		
		setData();    //配置adapter的数据
		
		setTab();    //选项卡配置
		
		setListener();    //各个组件的监听器
		
		myEventOnList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(MyEvent.this, ActivityDetail.class);
				
				intent.putExtra("activityId", mDataOn.get(position).get("activityId").toString());
				intent.putExtra("userImage", mDataOn.get(position).get("userImage").toString());
				intent.putExtra("userName", mDataOn.get(position).get("userName").toString());
				intent.putExtra("userGender", mDataOn.get(position).get("userGender").toString());
				intent.putExtra("activityKind", mDataOn.get(position).get("activityKind").toString());
				intent.putExtra("activityName", mDataOn.get(position).get("activityName").toString());
				intent.putExtra("meetPlace", mDataOn.get(position).get("meetPlace").toString());
				intent.putExtra("meetTime", mDataOn.get(position).get("meetTime").toString());
				intent.putExtra("endTime", mDataOn.get(position).get("endTime").toString());
				intent.putExtra("maxNumber", mDataOn.get(position).get("maxNumber").toString());
				intent.putExtra("presentNumber", mDataOn.get(position).get("presentNumber").toString());
				intent.putExtra("state", mDataOn.get(position).get("state").toString());
				intent.putExtra("remark", mDataOn.get(position).get("remark").toString());
				intent.putExtra("userId", mDataOn.get(position).get("userId").toString());
				
				startActivity(intent);
			}
		});
		
		myEventOffList.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				Intent intent = new Intent(MyEvent.this, ActivityDetail.class);
				
				intent.putExtra("activityId", mDataOff.get(position).get("activityId").toString());
				intent.putExtra("userImage", mDataOff.get(position).get("userImage").toString());
				intent.putExtra("userName", mDataOff.get(position).get("userName").toString());
				intent.putExtra("userGender", mDataOff.get(position).get("userGender").toString());
				intent.putExtra("activityKind", mDataOff.get(position).get("activityKind").toString());
				intent.putExtra("activityName", mDataOff.get(position).get("activityName").toString());
				intent.putExtra("meetPlace", mDataOff.get(position).get("meetPlace").toString());
				intent.putExtra("meetTime", mDataOff.get(position).get("meetTime").toString());
				intent.putExtra("endTime", mDataOff.get(position).get("endTime").toString());
				intent.putExtra("maxNumber", mDataOff.get(position).get("maxNumber").toString());
				intent.putExtra("presentNumber", mDataOff.get(position).get("presentNumber").toString());
				intent.putExtra("state", mDataOff.get(position).get("state").toString());
				intent.putExtra("remark", mDataOff.get(position).get("remark").toString());
				intent.putExtra("userId", mDataOff.get(position).get("userId").toString());
				
				startActivity(intent);
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
		myEventNavigationLeft = (Button) findViewById(R.id.my_event_navigation_left);
		myEventUserImage = (CircleImg) findViewById(R.id.my_event_user_image);
		myEventUserId = (TextView) findViewById(R.id.my_event_user_id);
		myEventOn = (TextView) findViewById(R.id.my_event_on);
		myEventOff = (TextView) findViewById(R.id.my_event_off);
		
		myEventOnList = (MyListview) findViewById(R.id.my_event_on_list);
		myEventOffList = (MyListview) findViewById(R.id.my_event_off_list);
		
		myEventScrollView = (ScrollView) findViewById(R.id.my_event_scroll_view);
		
		myEventUserId.setText(Login.nickName);
		myEventUserImage.setImageDrawable(Login.userImageDrawable);
		
		inflate = LayoutInflater.from(this);
		footViewOn = inflate.inflate(R.layout.hall_listview_foot, null);
		footViewOff = inflate.inflate(R.layout.hall_listview_foot, null);
		
		sd = new ShowDialog(pd);
		
		PushAgent.getInstance(this).onAppStart();
	}
	
	/**
	 * 设置测试数据
	 * @throws JSONException 
	 */
	private void setData(){
		
		sd.showDialog(this, "正在获取信息");
		
		Hall.executorService.submit(new Runnable(){
			@Override
			public void run() {
				mDataOn = new ArrayList<Map<String, Object>>();
				mDataOff = new ArrayList<Map<String, Object>>();
				
				try {
					
					mDataOn = SetActData.setData(LIMIT, OFFSET, PROCESSING);
					mDataOff = SetActData.setDataOff(LIMIT, OFFSET, FINISHED);
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				Hall.handler.post(new Runnable(){
					@Override
					public void run() {
						adapterOn = new MyEventAdapter(MyEvent.this, mDataOn);
						adapterOff = new MyEventAdapter(MyEvent.this, mDataOff);
						
						myEventOnList.addFooterView(footViewOn);
						myEventOffList.addFooterView(footViewOff);
						
						myEventOnList.setAdapter(adapterOn);
						myEventOffList.setAdapter(adapterOff);
						
						SetListViewHeight.setListViewHeightBasedOnChildren(myEventOnList);
						SetListViewHeight.setListViewHeightBasedOnChildren(myEventOffList);
						
						sd.dismissDialog();
					}
				});
			}
		});
	}
	
	/**
	 * 设置选项卡
	 */
	private void setTab(){

		TabSpec tab1 = tabHost.newTabSpec("tab1").setIndicator("0").setContent(R.id.my_event_tab01);
		tabHost.addTab(tab1);
		TabSpec tab2 = tabHost.newTabSpec("tab2").setIndicator("1").setContent(R.id.my_event_tab02);
		tabHost.addTab(tab2);
		
		tabHost.setCurrentTab(0);
		myEventOn.setTextColor(getResources().getColor(R.color.color_blue));
	}

	
	/**
	 * 设置各组件的监听器
	 */
	private void setListener(){
		myEventNavigationLeft.setOnClickListener(this);
		myEventOff.setOnClickListener(this);
		myEventOn.setOnClickListener(this);
		
		myEventScrollView.smoothScrollTo(0,0);
		
		// 返回按钮的点击效果
		myEventNavigationLeft.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					myEventNavigationLeft.setBackgroundResource(R.drawable.login_ensure_button_down);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					myEventNavigationLeft.setBackgroundResource(R.drawable.navigation_back);
				}
				return false;
			}
		});
		
		footViewOn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {

				sd.showDialog(MyEvent.this, "正在加载，请稍候···");
				
				Hall.executorService.submit(new Runnable(){
					@Override
					public void run() {
						
						try {
							int offsetOn = mDataOn.size();
							Log.v("offsetOn", offsetOn+"");
							mDataOn.addAll(SetActData.setData(LIMIT, offsetOn, PROCESSING));
							
						} catch (JSONException e) {
							sd.dismissDialog();
							Looper.prepare();
							Toast.makeText(MyEvent.this, "没有更多了", Toast.LENGTH_SHORT).show();
							Looper.loop();
							return;
						}
						
						Hall.handler.post(new Runnable(){
							@Override
							public void run() {
								sd.dismissDialog();
								adapterOn.notifyDataSetChanged();
							}
						});
					}
					
				});
			}
		});
		
		footViewOff.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {

				sd.showDialog(MyEvent.this, "正在加载");
				
				Hall.executorService.submit(new Runnable(){
					@Override
					public void run() {
						
						try {
							
							int offsetOff = mDataOff.size();
							Log.v("offsetOff", offsetOff+"");
							mDataOff.addAll(SetActData.setDataOff(LIMIT, offsetOff, FINISHED));
							
						} catch (JSONException e) {
							sd.dismissDialog();
							Looper.prepare();
							Toast.makeText(MyEvent.this, "没有更多了", Toast.LENGTH_SHORT).show();
							Looper.loop();
							return;
						}
						
						Hall.handler.post(new Runnable(){
							@Override
							public void run() {
								sd.dismissDialog();
								adapterOff.notifyDataSetChanged();
							}
						});
					}
				});
			}
		});
	}
	
	
	//各个组件的监听响应
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		
		case R.id.my_event_navigation_left : {
			finish();
			break;
		}
		
		//已完成
		case R.id.my_event_off : {
			tabHost.setCurrentTab(1);
			myEventOff.setTextColor(getResources().getColor(R.color.color_blue));
			myEventOn.setTextColor(getResources().getColor(R.color.color_text_gray));
			break;
		}
		
		//进行中
		case R.id.my_event_on : {
			tabHost.setCurrentTab(0);
			myEventOn.setTextColor(getResources().getColor(R.color.color_blue));
			myEventOff.setTextColor(getResources().getColor(R.color.color_text_gray));
			break;
		}
		
		default: {
			return;
		}
		
		}
	}

}
