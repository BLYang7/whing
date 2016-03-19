package com.whing;

/**
 * ���Ա��չʾ
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import com.whing.adapter.ActMemberAdapter;
import com.whing.debug.LOG;
import com.whing.external.PostData;
import com.whing.external.SetActData;
import com.whing.external.URLenum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class ActMember extends Activity{
	
	Button actMemberNavigationLeft;
	ListView actMemberListView;
	
	ActMemberAdapter adapter;
	
	List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_member);
		
		init();    //��ʼ���������
		
		setData();    //����ҳ������
		
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
	 * ������ĳ�ʼ��
	 */
	private void init(){
		actMemberNavigationLeft = (Button) findViewById(R.id.act_member_navigation_left);
		actMemberListView = (ListView) findViewById(R.id.act_member_listview);
	}
	
	
	/**
	 * ���ø�������ļ�����
	 */
	private void setListener(){
		
		actMemberNavigationLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		actMemberListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Intent intent = new Intent(ActMember.this, InfShow.class);
				intent.putExtra("userId", mData.get(position).get("userId").toString());
				startActivity(intent);
				
			}
		});
		
		// ���ذ�ť�ĵ��Ч��
		actMemberNavigationLeft.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					actMemberNavigationLeft.setBackgroundResource(R.drawable.login_ensure_button_down);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					actMemberNavigationLeft.setBackgroundResource(R.drawable.navigation_back);
				}
				return false;
			}
		});
		
	}
	
	/**
	 * ��������
	 */
	private void setData(){
		
		String str = "&act_id="+getIntent().getExtras().getString("activityId");
		String result = PostData.getData(str, URLenum.participant);
		if(LOG.DEBUG){
			Log.v("review", result);
		}
		
		try {
			JSONObject json = new JSONObject(result);
		
			String[] userName = SetActData.JSArrayToStringArray(json.getJSONArray("nickname"));
			String[] userImage = SetActData.JSArrayToStringArray(json.getJSONArray("photo"));
			String[] userId = SetActData.JSArrayToStringArray(json.getJSONArray("user_id"));
			
			for(int i=0; i<userName.length; i++){
				
				Map<String, Object> listItem = new HashMap<String, Object>();
				listItem.put("userName", userName[i]);
				listItem.put("userId", userId[i]);
				listItem.put("userImage", userImage[i]);
				
				mData.add(listItem);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		adapter = new ActMemberAdapter(ActMember.this, mData);
		actMemberListView.setAdapter(adapter);
		
	}
	
}
