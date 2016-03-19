package com.whing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.whing.adapter.ReviewAdapter;
import com.whing.adapter.UserLetterAdapter;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class UserLetter extends Activity implements OnClickListener{
	
	Button userLetterNavigationLeft;
	ListView userLetterList;
	private List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
	
	UserLetterAdapter adapter ;
	
	private ProgressDialog pd;
	ShowDialog sd;
	
	private View footer;
	private LayoutInflater inflater;
	
	private static final int LIMIT = 10;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_letter);
		
		init();    //各组件的初始化
		
		setData();    //设置测试数据
		
		setListener();    //设置组件的监听器
		
		userLetterList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(UserLetter.this, LetterChat.class);
				intent.putExtra("user_id", mData.get(position).get("userId").toString());
				intent.putExtra("userImage", mData.get(position).get("userImage").toString());
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
		userLetterNavigationLeft = (Button) findViewById(R.id.user_letter_navigation_left);
		userLetterList = (ListView) findViewById(R.id.user_letter_list);
		
		PushAgent.getInstance(this).onAppStart();
		
		inflater = LayoutInflater.from(this);
		footer = inflater.inflate(R.layout.listview_footer, null);
		
		sd = new ShowDialog(pd);
	}
	
	/**
	 * 设置测试数据
	 */
	private void setData() {

		sd.showDialog(this, "正在获取数据");

		Hall.executorService.submit(new Runnable() {

			@Override
			public void run() {

				
				String result = getMessage(LIMIT, 0);

				Log.v("tags", result);

				List<Map<String, Object>> temp = getMData(result);
				mData.addAll(temp);

				Hall.handler.post(new Runnable() {
					@Override
					public void run() {

						adapter = new UserLetterAdapter(UserLetter.this, mData);
						userLetterList.addFooterView(footer);
						userLetterList.setAdapter(adapter);
						sd.dismissDialog();

					}
				});

			}
		});
	}
	

	/**
	 * 设置各组件的监听器
	 */
	private void setListener() {
		userLetterNavigationLeft.setOnClickListener(this);
		
		// 返回按钮的点击效果
		userLetterNavigationLeft.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					userLetterNavigationLeft.setBackgroundResource(R.drawable.login_ensure_button_down);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					userLetterNavigationLeft.setBackgroundResource(R.drawable.navigation_back);
				}
				return false;
			}
		});
		
		footer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				int offset = mData.size();
				Log.v("offset", offset+"");
				
				String result = getMessage(LIMIT, offset);
				List<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();
				
				temp = getMData(result);
				
				mData.addAll(temp);
				
				adapter.notifyDataSetChanged();
			}
		});
	}
	
	/**
	 * 各组件的按键响应
	 */
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.user_letter_navigation_left : {
			finish();
			break;
		}
		
		default : {
			return;
		}
		
		}
	}
	
	
	/**
	 * 将服务器返回的值作转变
	 * @param result 服务器返回值
	 * @return 转变后的List<Map<String, Object>>数据
	 */
	private List<Map<String, Object>> getMData(String result) {

		List<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();

		JSONObject json = null;

		try {
			json = new JSONObject(result);
			String[] userName = SetActData.JSArrayToStringArray(json.getJSONArray("nickname"));
			String[] userId = SetActData.JSArrayToStringArray(json.getJSONArray("user_id"));
			String[] msg = SetActData.JSArrayToStringArray(json.getJSONArray("message"));
			String[] time = SetActData.JSArrayToStringArray(json.getJSONArray("send_time"));
			String[] userImage = SetActData.JSArrayToStringArray(json.getJSONArray("photo"));

			for (int i = 0; i < userName.length; i++) {

				Map<String, Object> listItem = new HashMap<String, Object>();
				listItem.put("userName", userName[i]);
				listItem.put("time", time[i]);
				listItem.put("userImage", userImage[i]);
				listItem.put("msg", msg[i]);
				listItem.put("userId", userId[i]);
				temp.add(listItem);

			}
		} catch (JSONException e) {
			new Thread(new Runnable(){
				@Override
				public void run() {
					Looper.prepare();
					Toast.makeText(UserLetter.this, "没有私信了", Toast.LENGTH_SHORT).show();
					Looper.loop();
				}
			}).start();
		}
		
		return temp;

	}
	
	
	/**
	 * 从服务器获取私信数据
	 * @param limit 限度
	 * @param offset 偏移量
	 * @return 从服务器返回的字符串
	 */
	private String getMessage(int limit, int offset){
		String str = "&limit=" + limit + "&offset=" + offset;
		String result = PostData.getData(str, URLenum.messaged_user);
		return result;
	}
	
	
}
