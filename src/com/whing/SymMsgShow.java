package com.whing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.whing.adapter.SymMsgAdapter;
import com.whing.external.CustomDialog;
import com.whing.external.MsgShow;
import com.whing.external.PostData;
import com.whing.external.SetActData;
import com.whing.external.ShowDialog;
import com.whing.external.URLenum;
import com.whing.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ϵͳ��Ϣչʾҳ
 * @author BLYang
 *
 */
public class SymMsgShow extends Activity implements OnClickListener{
	
	Button messageNavigationLeft;
	TextView messageRead, messageUnread;
	ListView messageShowList;
	
	SymMsgAdapter adapter;
	
	List<Map<String, Object>> mDataUnread = new ArrayList<Map<String, Object>>();
	List<Map<String, Object>> mDataRead = new ArrayList<Map<String, Object>>();
	
	List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
	
	private ProgressDialog pd;
	private ShowDialog sd;
	
	private static final int LIMIT = 20;
	private static final int FIRSTOFFSET = 0;
	
	private boolean flag = false; //�������ѡ�е�ֵ��false��ʾδ����true��ʾ�Ѷ�
	
	private View footer;
	private LayoutInflater inflater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message);
		
		init();    //��ʼ���������
		
		setListener();    //��������ļ�����
		
		setData();    //��ʼ���û���������
		
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
	 * �������ʼ��
	 */
	private void init() {
		messageNavigationLeft = (Button) findViewById(R.id.message_navigation_left);
		messageShowList = (ListView) findViewById(R.id.system_message_list);
		messageRead = (TextView) findViewById(R.id.system_message_read);
		messageUnread = (TextView) findViewById(R.id.system_message_unread);
		messageUnread.setTextColor(getResources().getColor(R.color.color_blue));
		messageRead.setTextColor(getResources().getColor(R.color.color_gray));
		
		inflater = LayoutInflater.from(this);
		footer = inflater.inflate(R.layout.listview_footer, null);
		
		sd = new ShowDialog(pd);
		
		PushAgent.getInstance(this).onAppStart();
	}
	
	
	/**
	 * ���ó�ʼ�Ĳ�������
	 */
	private void setData() {
		
		sd.showDialog(this, "���ڻ�ȡ����");
		
		Hall.executorService.submit(new Runnable(){

			@Override
			public void run() {
				
				String result = getUnreadNotice(LIMIT, FIRSTOFFSET);
				Log.v("��ʼ����δ��������Ϣ", result);
				
				try {
					mDataUnread = setData(result);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				mData.addAll(mDataUnread);
				
				Hall.handler.post(new Runnable(){
					@Override
					public void run() {
						adapter = new SymMsgAdapter(SymMsgShow.this , mData);
						messageShowList.addFooterView(footer);
						messageShowList.setAdapter(adapter);
						sd.dismissDialog();
					}
				});
			}
		});
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				
				String result = getReadNotice(LIMIT, FIRSTOFFSET);
				
				try {
					mDataRead = setData(result);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * ��������ļ�����
	 */
	private void setListener() {
		
		messageNavigationLeft.setOnClickListener(this);
		messageRead.setOnClickListener(this);
		messageUnread.setOnClickListener(this);
		messageShowList.setOnItemClickListener(ItemClick);
		
		// ���ذ�ť�ĵ��Ч��
		messageNavigationLeft.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					messageNavigationLeft.setBackgroundResource(R.drawable.login_ensure_button_down);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					messageNavigationLeft.setBackgroundResource(R.drawable.navigation_back);
				}
				return false;
			}
		});
		
		footer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//�����Ѷ�
				if(flag){
					int offsetRead = mDataRead.size();
					Log.v("offsetRead", offsetRead+"");
					String result = getReadNotice(LIMIT, offsetRead);
					
					List<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();
					
					try {
						temp = setData(result);
						mDataRead.addAll(temp);
						mData.addAll(temp);
					} catch (Exception e) {
						Toast.makeText(SymMsgShow.this, "û�и�����", Toast.LENGTH_SHORT).show();
						return;
					}
					finally{
						adapter.notifyDataSetChanged();
					}
					
				}
				
				//����δ��
				else{
					int offsetUnread = mDataUnread.size();
					Log.v("offsetUnread", offsetUnread+"");
					String result = getUnreadNotice(LIMIT, offsetUnread);
					
					List<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();
					
					try {
						temp = setData(result);
						mDataUnread.addAll(temp);
						mData.addAll(temp);
					} catch (JSONException e) {
						Toast.makeText(SymMsgShow.this, "û�и�����", Toast.LENGTH_SHORT).show();
						return;
					}
					finally{
						adapter.notifyDataSetChanged();
					}
				}
				
			}
		});
		
	}
	
	
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		
		case R.id.message_navigation_left: {
			
			finish();
			break;
			
		}
		
		case R.id.system_message_read:{
			
			if(flag){
				break;
			}
			messageRead.setTextColor(getResources().getColor(R.color.color_blue));
			messageUnread.setTextColor(getResources().getColor(R.color.color_gray));
			mData.clear();
			mData.addAll(mDataRead);
			adapter.notifyDataSetChanged();
			flag = true;
			break;
			
		}
		
		case R.id.system_message_unread:{
			
			if(!flag){
				break;
			}
			
			messageUnread.setTextColor(getResources().getColor(R.color.color_blue));
			messageRead.setTextColor(getResources().getColor(R.color.color_gray));
			mData.clear();
			mData.addAll(mDataUnread);
			adapter.notifyDataSetChanged();
			flag = false;
			break;
			
		}
		
		default :{
			break;
		}
		
		}
		
	}
	
	
	private OnItemClickListener ItemClick = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			int turnType = -1;
			
			if(!flag){
				Log.v("δ��ѡ���¼�", "�¼�����ɹ���");
				turnType = (Integer) mDataUnread.get(position).get("turnType");
				turnWay(turnType, mDataUnread, position);
			}else if(flag){
				Log.v("�Ѷ�ѡ���¼�", "�¼�����ɹ���");
				turnType = (Integer) mDataRead.get(position).get("turnType");
				turnWay(turnType, mDataRead, position);
			}
			
		}
		
	};
	
	
	private void turnWay(int turnType, List<Map<String, Object>> mData, int position){
		
		//�����Ϣ���Ѷ�
		final String noticeId = mData.get(position).get("noticeId").toString().trim();
		String res = PostData.getData("&notice_id="+noticeId, URLenum.read);
		Log.v("symRead", res);
		
		String msgShow = MsgShow.msgShowMore(mData, position);
		
		switch(turnType){
		
		//0��ʾֻ��һ�仰
		case 0:{
			CustomDialog.Builder builder = new CustomDialog.Builder(this);
			builder.setMessage(msgShow);
			builder.setTitle("��ʾ");
			builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					//������Ĳ�������
				}
			});
			builder.create().show();
			break;
		}
		
		// 1��ʾ��������ť
		case 1:{
			CustomDialog.Builder builder = new CustomDialog.Builder(this);
			builder.setMessage(msgShow);
			builder.setTitle("��ʾ");
			builder.setPositiveButton("ͬ��", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					String res2 = PostData.getData("&notice_id="+noticeId.trim(), URLenum.agree);
					Log.v("tag", URLenum.agree+"&notice_id="+noticeId);
					Log.v("agree", res2);
				}
			});
			builder.setNegativeButton("�ܾ�",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							String res2 = PostData.getData("&notice_id="+noticeId.trim(), URLenum.refuse);
							Log.v("refuse", res2);
						}
					});
			builder.create().show();
			break;
		}
		
		//2��ʾ��ת��ActivityDetail
		case 2:{
			
			Intent intent = new Intent(SymMsgShow.this, ActivityDetail.class);
			String result = PostData.getData(
					"&act_id="+mData.get(position).get("activity_id").toString(),
					URLenum.info);
			Log.v("get_act_inf", result);
			
			try {
				JSONObject json = new JSONObject(result);
			
				intent.putExtra("activityId", json.getString("activity_id"));
				intent.putExtra("userImage", json.getString("publisher_photo"));
				intent.putExtra("userName", json.getString("publisher_nickname"));
				intent.putExtra("activityName", json.getString("activity_name"));
				intent.putExtra("meetPlace", json.getString("place"));
				intent.putExtra("meetTime", json.getString("start_time"));
				intent.putExtra("endTime", json.getString("end_time"));
				intent.putExtra("remark", json.getString("remark"));
				intent.putExtra("userId", json.getString("user_id"));
				intent.putExtra("userGender", json.getString("publisher_sex"));
				intent.putExtra("activityKind", json.getString("activity_kind"));
				intent.putExtra("maxNumber", json.getString("max_number"));
				intent.putExtra("presentNumber", json.getString("present_number"));
				intent.putExtra("state", json.getString("status"));
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			startActivity(intent);
			
			break;
		}
		
		default:{
			Toast.makeText(SymMsgShow.this, "������", Toast.LENGTH_SHORT).show();
			break;
		}
		
		}
	}
	
	
	private List<Map<String, Object>> setData(String result) throws JSONException {

		List<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();

		JSONObject json = new JSONObject(result);

		String[] receiver_id = SetActData.JSArrayToStringArray(json
				.getJSONArray("user_id"));
		String[] sender_id = SetActData.JSArrayToStringArray(json
				.getJSONArray("sender_id"));
		String[] type = SetActData.JSArrayToStringArray(json
				.getJSONArray("type"));
		String[] activity_id = SetActData.JSArrayToStringArray(json
				.getJSONArray("activity_id"));
		String[] activity_name = SetActData.JSArrayToStringArray(json
				.getJSONArray("activity_name"));
		String[] status = SetActData.JSArrayToStringArray(json
				.getJSONArray("status"));
		String[] send_time = SetActData.JSArrayToStringArray(json
				.getJSONArray("send_time"));
		String[] sender_nickname = SetActData.JSArrayToStringArray(json
				.getJSONArray("sender_nickname"));
		String[] userImage = SetActData.JSArrayToStringArray(json
				.getJSONArray("sender_photo"));
		String[] noticeId = SetActData.JSArrayToStringArray(json
				.getJSONArray("notice_id"));

		for (int i = 0; i < noticeId.length; i++) {

			int turnType = -1;

			// ��ת�����ͣ�0��ʾֻ��һ�仰�� 1��ʾ��������ť�� 2��ʾ��ת��ActivityDetail
			if (type[i].equals("100") || type[i].equals("101") || type[i].equals("110")
					|| type[i].equals("113") || type[i].equals("111")
					|| type[i].equals("112")) {
				
				turnType = 0;
				
			} 
			
			else if (type[i].equals("150")) {
				
				turnType = 1;
			
			} 
			
			else if (type[i].equals("200") || type[i].equals("210")
					|| type[i].equals("300")) {
				
				turnType = 2;
				
			}

			Map<String, Object> listItem = new HashMap<String, Object>();

			listItem.put("receiver_id", receiver_id[i]);
			listItem.put("sender_id", sender_id[i]);
			listItem.put("type", type[i]);
			listItem.put("activity_id", activity_id[i]);
			listItem.put("activity_name", activity_name[i]);
			listItem.put("status", status[i]);
			listItem.put("send_time", send_time[i]);
			listItem.put("sender_nickname", sender_nickname[i]);
			listItem.put("turnType", turnType);
			listItem.put("senderImage", userImage[i]);
			listItem.put("noticeId", noticeId[i]);

			Log.v("���õ���ʱ��listItem", listItem.toString());
			temp.add(listItem);
		}

		Log.v("���õ���ʱtemp", temp.toString());

		return temp;
	}
	
	
	private String getUnreadNotice(int limitnum, int offsetnum){
		String str = "&limit=" + limitnum + "&offset=" + offsetnum;
		String result = PostData.getData(str, URLenum.unread_notice);
		Log.v("notice unread", result);
		return result;
	}
	
	private String getReadNotice( int limitnum, int offsetnum){
		String str = "&limit=" + limitnum + "&offset=" + offsetnum;
		String result = PostData.getData(str, URLenum.read_notice);
		Log.v("notice read", result);
		return result;
	}
	
}
	





