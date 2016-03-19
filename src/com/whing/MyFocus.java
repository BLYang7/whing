package com.whing;

/**
 * 我的关注页面，用来展示用户关注的朋友
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.whing.adapter.MyFocusAdapter;
import com.whing.external.PostData;
import com.whing.external.SetActData;
import com.whing.external.ShowDialog;
import com.whing.external.URLenum;
import com.whing.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MyFocus extends Activity{

	Button myFocusNavigationLeft;
	ListView myFocusListView;
	
	private ProgressDialog pd;
	private ShowDialog sd;
	
	private List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_focus);
		
		init();    //各组件的初始化
		
		setData();    //设置页面展示的数据
		
		setListener();   //设置各个组件的监听器
		
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
	 * 初始化各个组件
	 */
	private void init(){
		myFocusNavigationLeft = (Button) findViewById(R.id.my_focus_navigation_left);
		myFocusListView = (ListView) findViewById(R.id.my_focus_listview);
		
		sd = new ShowDialog(pd);
		
		PushAgent.getInstance(this).onAppStart();
	}
	
	/**
	 * 设置listView的各种数据
	 */
	private void setData(){
		
		sd.showDialog(this, "正在加载，请稍候···");
		
		Hall.executorService.submit(new Runnable(){

			@Override
			public void run() {
				
				String result = PostData.getData("", URLenum.get_follow);
				
				try {
					JSONObject json = new JSONObject(result);
					
					String[] userId = SetActData.JSArrayToStringArray(json.getJSONArray("user_id"));
					String[] nickName = SetActData.JSArrayToStringArray(json.getJSONArray("nickname"));
					String[] photo = SetActData.JSArrayToStringArray(json.getJSONArray("photo"));
					
					if(userId.length == 0){
						sd.dismissDialog();
						Looper.prepare();
						Toast.makeText(MyFocus.this, "还没有关注的小伙伴哦", Toast.LENGTH_SHORT).show();
						Looper.loop();
					}
					else{
						for(int i=0; i<userId.length; i++){
							
							Map<String, Object> listItem = new HashMap<String, Object>();
							listItem.put("userId", userId[i]);
							listItem.put("userName", nickName[i]);
							listItem.put("userImage", photo[i]);
							
							mData.add(listItem);
						}
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				Hall.handler.post(new Runnable(){
					@Override
					public void run() {
						sd.dismissDialog();
						MyFocusAdapter adapter = new MyFocusAdapter(MyFocus.this, mData);
						myFocusListView.setAdapter(adapter);
					}
				});
			}
		});
	}
	
	/**
	 * 设置各个组件的响应
	 */
	private void setListener(){
		myFocusNavigationLeft.setOnClickListener(navigationLeftClick);
		myFocusListView.setOnItemClickListener(ItemClick);
		
		// 返回按钮的点击效果
		myFocusNavigationLeft.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					myFocusNavigationLeft.setBackgroundResource(R.drawable.login_ensure_button_down);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					myFocusNavigationLeft.setBackgroundResource(R.drawable.navigation_back);
				}
				return false;
			}
		});
	}
	
	
	/**
	 * 左上角按钮点击的触发响应，关闭当前的Activity
	 */
	private OnClickListener navigationLeftClick = new OnClickListener(){
		@Override
		public void onClick(View v) {
			finish();
		}
	};

	/**
	 * 设置listview的点击事件
	 */
	private OnItemClickListener ItemClick = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			Intent intent = new Intent(MyFocus.this, InfShow.class);
			intent.putExtra("userId", mData.get(position).get("userId").toString());
			startActivity(intent);
		}
	};
	
}







