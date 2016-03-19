package com.whing;

/**
 * �ҵĹ�עҳ�棬����չʾ�û���ע������
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
		
		init();    //������ĳ�ʼ��
		
		setData();    //����ҳ��չʾ������
		
		setListener();   //���ø�������ļ�����
		
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
	 * ��ʼ���������
	 */
	private void init(){
		myFocusNavigationLeft = (Button) findViewById(R.id.my_focus_navigation_left);
		myFocusListView = (ListView) findViewById(R.id.my_focus_listview);
		
		sd = new ShowDialog(pd);
		
		PushAgent.getInstance(this).onAppStart();
	}
	
	/**
	 * ����listView�ĸ�������
	 */
	private void setData(){
		
		sd.showDialog(this, "���ڼ��أ����Ժ򡤡���");
		
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
						Toast.makeText(MyFocus.this, "��û�й�ע��С���Ŷ", Toast.LENGTH_SHORT).show();
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
	 * ���ø����������Ӧ
	 */
	private void setListener(){
		myFocusNavigationLeft.setOnClickListener(navigationLeftClick);
		myFocusListView.setOnItemClickListener(ItemClick);
		
		// ���ذ�ť�ĵ��Ч��
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
	 * ���Ͻǰ�ť����Ĵ�����Ӧ���رյ�ǰ��Activity
	 */
	private OnClickListener navigationLeftClick = new OnClickListener(){
		@Override
		public void onClick(View v) {
			finish();
		}
	};

	/**
	 * ����listview�ĵ���¼�
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







