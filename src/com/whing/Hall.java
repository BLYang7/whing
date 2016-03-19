package com.whing;

/**
 * ����ҳ��
 * �ӵ�½ҳ��ת������
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
	
	//�����̳߳غ�handler
	public static Handler handler = new Handler();
    public static ExecutorService executorService = Executors.newFixedThreadPool(7);
    
    //���ó�ʼ����ƫ�������޶�
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
		
		init();    //��ʼ���������
		
		preNet();    //����֮ǰ��׼��
		
		setData();    //���ó�ʼ��չʾ����
		
		setInf();    //�����û�����ͷ��
		
		setListener();    //���ø�������ļ�����
		
		//����ۣ��ڽ������ʱ����
		new Thread(new Runnable(){
			@Override
			public void run() {
				Looper.prepare();
				estimation();
				Looper.loop();
			}
		}).start();
		
		//���ô�����listView����Ŀ�������
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
	 * �����û�ͷ����û���
	 */
	private void setInf(){
		hallUserImageNavigationRight.setImageDrawable(Login.userImageDrawable);
		menuLeftUserLogo.setImageDrawable(Login.userImageDrawable);
		
		if(!Login.userName.equals("")){
			menuLeftUserName.setText(Login.nickName);
		}
		
	}
	
	/**
	 * ��ʼ�������
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
	 * Activity �ص�Ҫִ�еĲ��������Activity�����Ĺ���
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		
		setInf();
		remaind();  //ϵͳδ��֪ͨ������
		
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	
	/**
	 * ��ʼ����ʱ������mData�����ݣ����ұ�֤�����سɹ�֮������adapter��ֵ��
	 * �������ݣ� ��Ȼ��������������ڵ�½���֮����ܵ���
	 */
	private void setData() {
		
		sd.showDialog(this,"���ڻ�ȡ����");
		
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				
				try {
					
					mData = SetActData.setData(LIMIT, OFFSET, ALL);
					
				} catch (JSONException e) {
					e.printStackTrace();
					return;
				}

				// �������������֮�󣬸���adapter��ֵ
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
	 * �˳�����ʾ����
	 * @param view ��Ӧ�˳�����ͼ
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
	 * ������׼��������
	 * 1.ǿ��������������
	 * 2.����Ŀ���������URL
	 */
	private void preNet(){
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}

	
	/**
	 * ���ø�����ļ�����
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
		
		// ���ظ�������
		footView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (!slideMenu.isMainScreenShowing()) {
					slideMenu.closeMenu();
					return;
				}

				sd.showDialog(Hall.this, "���ڼ���");

				//���̳߳صݽ����ݣ��������̣߳����������������
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
							Toast.makeText(Hall.this, "û�и�����", Toast.LENGTH_SHORT).show();
							Looper.loop();
							return;
						}
						
						// �������������֮�󣬸���adapter��ֵ
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
	 * ��������ļ������Ĵ�����
	 * @param v �����view
	 */
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		
		//�������Ҫ������
		case R.id.btn_newOne:{
			
			if (!slideMenu.isMainScreenShowing()) {
				slideMenu.closeMenu();
				break;
			}
			
			if(Login.userName.equals("")){
				Toast.makeText(this, "���ȵ�¼/ע��", Toast.LENGTH_SHORT).show();
			}
			else{
				Intent intent = new Intent(Hall.this, NewOne.class);
				intent.putExtra("ButtonFlag", 1);
				intent.putExtra("userName", Login.nickName);
				startActivity(intent);
			}

			break;
		}
		
		//����ܲ�����
		case R.id.run:{
			
			if (!slideMenu.isMainScreenShowing()) {
				slideMenu.closeMenu();
				break;
			}
			
			mDataUpdate(RUN);
			break;
		}
		
		//�������
		case R.id.ball:{
			
			if (!slideMenu.isMainScreenShowing()) {
				slideMenu.closeMenu();
				break;
			}
			
			mDataUpdate(BALL);
			break;
		}
		
		//�������
		case R.id.exercise:{
			
			if (!slideMenu.isMainScreenShowing()) {
				slideMenu.closeMenu();
				break;
			}
			
			mDataUpdate(EXERCISE);
			break;
		}
		
		//�������
		case R.id.other:{
			
			if (!slideMenu.isMainScreenShowing()) {
				slideMenu.closeMenu();
				break;
			}
			
			mDataUpdate(OTHER);
			break;
		}
			
		//����������ұߵ��û�ͷ��
		case R.id.hall_user_image_navigation_right:{
			
			if (slideMenu.isMainScreenShowing()) {
				slideMenu.openMenu();
			} else {
				slideMenu.closeMenu();
			}
			
			break;
		}
		
		
		//�����������ߵ�logo
		case R.id.btn_left:{
			
			if (!slideMenu.isMainScreenShowing()) {
				slideMenu.closeMenu();
				break;
			}
			
			break;
		}
		
		//���
		case R.id.hall_main:{
			
			if ( ! slideMenu.isMainScreenShowing() ) {
				slideMenu.closeMenu();
				break;
			}
			
			break;
		}
		
		//����հ׵ط��Ѳ��������
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
		
		//�����߲໬���û���Ϣ
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
		
		//����໬��ϵͳ֪ͨ���֪ͨ��
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
		
		//����໬���ҵĹ�ע
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
		
		//����໬���ҵĻ
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
		
		//����໬���û�����
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
		
		//����໬���û�����
		case R.id.menu_left_user_feedback:{
			agent = new FeedbackAgent(this);
			agent.startFeedbackActivity();
			agent.removeWelcomeInfo();
			break;
		}
		
		//���������ȡ����ҳ��
		case R.id.menu_left_net_flow:{
			
			Intent intent = new Intent(Hall.this, NetFlow.class);
			startActivity(intent);
			break;
		}
		
		
		//����໬���˳���¼
		case R.id.menu_left_user_exit:{
			
			//�˳���¼ȷ����ɾ�����ش洢���û���Ϣ��ɱ�����̣��˳�APP
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setPositiveButton("ȷ���˳�", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					//�������Ͱ󶨵ĳ����������߳������
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
	 * �ı�mData��ֵ
	 * @param kind ��ʾ���1�����ܲ���2������3�������࣬-1����������
	 */
	private void mDataUpdate(final int kind){
		
		if(kind == RUN){
			sd.showDialog(Hall.this, "���ڼ����ܲ����Ϣ");
		}
		else if(kind == BALL){
			sd.showDialog(Hall.this, "���ڼ���������Ϣ");
		}
		else if(kind == OTHER){
			sd.showDialog(Hall.this, "���ڼ�����������Ϣ");
		}
		else if(kind == EXERCISE){
			sd.showDialog(Hall.this, "���ڼ��ؽ�����Ϣ");
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
						Toast.makeText(Hall.this, "û��������Ļ", Toast.LENGTH_SHORT).show();
					}
					else if(kind == RUN){
						Toast.makeText(Hall.this, "û���ܲ���Ļ", Toast.LENGTH_SHORT).show();
					}
					else if(kind == EXERCISE){
						Toast.makeText(Hall.this, "û�н�����Ļ", Toast.LENGTH_SHORT).show();
					}
					else if(kind == BALL){
						Toast.makeText(Hall.this, "û������Ļ", Toast.LENGTH_SHORT).show();
					}
					Looper.loop();
					
				}
			}
		});
	}
	
	//ϵͳ������һЩ���ѣ���ʱֻ��δ����Ϣ������
	private void remaind() {
		executorService.submit(new Runnable(){

			@Override
			public void run() {
				
				String result = PostData.getData("", URLenum.get_notice_num);
				Log.v("δ��֪ͨ��Ŀ", result);
				
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
	 * ��ȡ�û�δ���۵Ļ��������
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
	 * ���ֲ���
	 * @param mDataRemaind
	 * @param act_id
	 */
	private void showDialog(List<HashMap<String, Object>> mDataRemaind, String act_id) {

		final String activity_id = act_id;
		
		EstimationCustomDialog.Builder builder = 
				new EstimationCustomDialog.Builder(Hall.this);
		builder.setTitle("������Ա����");
		builder.setAdapter(Hall.this, mDataRemaind);

		final ScoreAdapter scoreAdapter = builder.getAdapter();

		builder.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
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

		builder.setNegativeButton("ȡ��",
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

