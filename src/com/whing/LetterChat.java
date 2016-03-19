package com.whing;

/**
 * 私信聊天的聊天内容展示页
 * 仿微信聊天界面
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.whing.adapter.LetterChatAdapter;
import com.whing.entity.ChatMsgEntity;
import com.whing.external.PostData;
import com.whing.external.SetActData;
import com.whing.external.URLenum;
import com.whing.imgcut.SetUserImage;
import com.whing.R;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class LetterChat extends Activity implements OnClickListener{
	
	Button letterChatNavigationLeft;
	ListView letterChatListView;
	EditText letterChatEditText;
	Button letterChatMsgSend;
	
	private LetterChatAdapter letterChatAdapter;
	
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();// 消息对象数组
	
	private String user_id;
	
	private Drawable drawable ;
	
	private int firstItem;
	private int size = 20;
	private int totalNum = 0;
	private final static int LIMIT = 20;
	private int offset = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.letter_chat);
		
		init();
		
		setListener();
		
		setData();
		
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
	 * 各个组件的初始化
	 */
	private void init(){
		
		user_id = this.getIntent().getExtras().getString("user_id").trim();
		
		letterChatNavigationLeft = (Button) findViewById(R.id.letter_chat_navigation_left);
		letterChatListView = (ListView) findViewById(R.id.letter_chat_listview);
		letterChatEditText = (EditText) findViewById(R.id.letter_chat_message);
		letterChatMsgSend = (Button) findViewById(R.id.letter_chat_btn_send);
		
		drawable = SetUserImage.setImage(
				this, 
				getResources(), 
				getIntent().getExtras().get("user_id").toString(),
				getIntent().getExtras().get("userImage").toString());
		
		PushAgent.getInstance(this).onAppStart();
		
	}
	
	private void setListener(){
		letterChatNavigationLeft.setOnClickListener(this);
		letterChatMsgSend.setOnClickListener(this);
		
		// 返回按钮的点击效果
		letterChatNavigationLeft.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					letterChatNavigationLeft.setBackgroundResource(R.drawable.login_ensure_button_down);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					letterChatNavigationLeft.setBackgroundResource(R.drawable.navigation_back);
				}
				return false;
			}
		});
		
		
		letterChatListView.setOnScrollListener(new OnScrollListener(){

			boolean flag = true;
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
				Log.v("letterChatAdapterNumber", letterChatAdapter.getCount()+"");
				
				if(size<LIMIT && flag){
					Toast.makeText(LetterChat.this, "没有更多了", Toast.LENGTH_LONG).show();
					flag = false;
				}
				
				else if ( firstItem == 0) {
					
					String result = getMessage(LIMIT, offset);
					offset = offset + LIMIT;
					
					Log.v("偏移量", offset+"");
					
					List<ChatMsgEntity> temp = new ArrayList<ChatMsgEntity>();
					temp.addAll(mDataArrays);
					
					List<ChatMsgEntity> resultDataArray = new ArrayList<ChatMsgEntity>();
					resultDataArray = setDataArray(result);
					mDataArrays.clear();
					
					size = resultDataArray.size();
					Log.v("size", size+"");
					
					mDataArrays.addAll(resultDataArray);
					mDataArrays.addAll(temp);
					
                    letterChatAdapter.notifyDataSetChanged(); 
                    letterChatListView.setSelection(letterChatAdapter.getCount() - totalNum);
                 }
				 
				 Log.v("onScrollStateChanged", "执行了");
				 
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
				firstItem = firstVisibleItem;
				totalNum = totalItemCount;
				
				Log.v("totalItem", totalItemCount+"");
				Log.v("lastItem", firstItem+"");
				Log.v("onScroll", "执行了");
				
			}
			
		});
		
	}

	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		
		case R.id.letter_chat_btn_send : {
			if( ! letterChatEditText.getText().toString().equals("") ){
				
				Log.v("user_id", user_id);
				
				Map<String, String> map = new HashMap<String, String> ();
				map.put("user_id", user_id);
				map.put("content", "\"" + letterChatEditText.getText().toString() + "\"");
				long currentTimeMills = System.currentTimeMillis();
				String result = PostData.postData(map, URLenum.send, Login.token);
				
				SimpleDateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date(currentTimeMills);
				String currentTime = formatTime.format(date);
				
				try {
					JSONObject json = new JSONObject(result);
					if(json.get("errcode").equals(0)){
						
						ChatMsgEntity entity = new ChatMsgEntity();
						entity.setDate(currentTime);
						entity.setUserImage(Login.userImageDrawable);
						entity.setMsgType(false);
						entity.setMessage(letterChatEditText.getText().toString());
						mDataArrays.add(entity);
						
						letterChatEditText.setText("");
						
						letterChatAdapter.notifyDataSetChanged();
						
						letterChatListView.setSelection(letterChatListView.getBottom());
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			else{
				Toast.makeText(this, "请输入信息之后再发送", Toast.LENGTH_SHORT).show();
			}
			
			break;
		}
		
		case R.id.letter_chat_navigation_left:{
			finish();
			break;
		}
		
		default:{
			break;
		}
		    
		}
		
	}
	
	
	private void setData(){
		
		Hall.executorService.submit(new Runnable(){
			@Override
			public void run() {
				
				String result = getMessage(LIMIT, offset);
				offset = offset + LIMIT;
				
				mDataArrays.addAll(setDataArray(result));
				
				Hall.handler.post(new Runnable(){
					@Override
					public void run() {
						letterChatAdapter = new LetterChatAdapter(LetterChat.this, mDataArrays);
						letterChatListView.setAdapter(letterChatAdapter);
						letterChatListView.setDividerHeight(0);
						letterChatListView.setSelection(letterChatAdapter.getCount() - 1);
					}
				});
			}
		});
	}
	
	/**
	 * 从服务器端获取信息
	 * @param limit 限制量
	 * @param offset 偏移量
	 * @return 服务器返回的字符串
	 */
	private String getMessage(int limit, int offset){
		String str = "&msg_id=" + user_id + "&limit=" + limit + "&offset=" + offset;
		String result = PostData.getData(str, URLenum.get_message);
		return result;
	}
	
	
	/**
	 * 设置消息对象数组中的内容
	 * @param result  从服务器端取得的数据
	 * @return 返回要设置好的内容
	 */
	private List<ChatMsgEntity> setDataArray(String result){
		
		ChatMsgEntity entity = null;
		List<ChatMsgEntity> mDataArray = new ArrayList<ChatMsgEntity>();
		
		JSONObject json;
		try {
			json = new JSONObject(result);
			String[] msgArray = SetActData.JSArrayToStringArray(json.getJSONArray("content"));
			String[] dataArray = SetActData.JSArrayToStringArray(json.getJSONArray("send_time"));
			String[] receiver_id = SetActData.JSArrayToStringArray(json.getJSONArray("user_id"));
			
			Drawable[] userImageDrawable = new Drawable[] { drawable, Login.userImageDrawable };
			
			for (int i = msgArray.length-1 ; i >=0 ; i--) {
				entity = new ChatMsgEntity();
				entity.setDate(dataArray[i]);
				
				if ( ! receiver_id[i].trim().equals(user_id)) {
					entity.setUserImage(userImageDrawable[0]);
					entity.setMsgType(true);// 收到的消息
				} else {
					entity.setUserImage(userImageDrawable[1]);
					entity.setMsgType(false);// 自己发送的消息
				}
				
				entity.setMessage(msgArray[i]);
				mDataArray.add(entity);
				
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return mDataArray;
	}
	
}
