package com.whing;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.whing.date.DateTimePick;
import com.whing.debug.LOG;
import com.whing.external.PostData;
import com.whing.external.URLenum;
import com.whing.imgcut.CircleImg;
import com.whing.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class NewOne extends Activity implements OnClickListener, OnFocusChangeListener{


	Button newOneNavigationLeft, newOneEnsure;
	CircleImg newOneUserImage;
	TextView newOneUserName, newOneNeedAcceptText;
	RadioGroup newOneClassChoose;
	EditText newOneActivityName, newOneStartTime, newOneEndTime, newOneMeetPlace, newOneNumberLimit, newOneSomething;
	ToggleButton newOneNeedAccept;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_one);
		
		init();    //�����ʼ��
		
		preNet();    //����׼��
		
		setInf();    //�����û�����ͷ��
		
		setButtonCheck();    //���û����
		
		setListener();    //���ø����������
		
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
	 * �����û�ͷ����û���
	 */
	private void setInf(){
		newOneUserImage.setImageDrawable(Login.userImageDrawable);
		newOneUserName.setText(Login.nickName);
	}
	
	/**
	 * ����checkButton��Ĭ��ֵ�����Ĭ��ֵҪ��ǰ���ȡ
	 */
	private void setButtonCheck(){
		
		int buttonFlag = (Integer)getIntent().getExtras().get("ButtonFlag");
		
		switch(buttonFlag){
		
		case 1:{
			newOneClassChoose.check(R.id.new_one_class_run);
			break;
		} 
		
		case 2:{
			newOneClassChoose.check(R.id.new_one_class_ball);
			break;
		}
		
		case 3:{
			newOneClassChoose.check(R.id.new_one_class_exercise);
			break;
		}
		
		case 4:{
			newOneClassChoose.check(R.id.new_one_class_other);
			break;
		} 
				
		}
		
	}
	
	/**
	 * ��ʼ���������
	 */
	private void init(){
		newOneNavigationLeft = (Button) findViewById(R.id.new_one_navigation_left);
		newOneEnsure = (Button) findViewById(R.id.new_one_ensure);
		newOneUserImage = (CircleImg) findViewById(R.id.new_one_user_image);
		newOneUserName = (TextView) findViewById(R.id.new_one_user_name);
		newOneClassChoose = (RadioGroup) findViewById(R.id.new_one_class_choose);
		newOneActivityName = (EditText) findViewById(R.id.new_one_activity_name);
		newOneStartTime = (EditText) findViewById(R.id.new_one_start_time);
		newOneEndTime = (EditText) findViewById(R.id.new_one_end_time);
		newOneMeetPlace = (EditText) findViewById(R.id.new_one_meet_place);
		newOneNumberLimit = (EditText) findViewById(R.id.new_one_number_limit);
		newOneSomething = (EditText) findViewById(R.id.new_one_something);
		newOneNeedAccept = (ToggleButton) findViewById(R.id.new_one_need_accept);
		newOneNeedAcceptText = (TextView) findViewById(R.id.new_one_need_accept_text);
		
		PushAgent.getInstance(this).onAppStart();
	}
	
	/**
	 * ����֮ǰ��׼��
	 * <p> 1���趨Ŀ��URL��ַ </p>
	 * <p> 2��ǿ�������������� </p>
	 */
	private void preNet(){
    	StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
    	StrictMode.setThreadPolicy(policy);
	}
	
	/**
	 * ��ȡ��ǰ�ע����Ϣ
	 * @return Map����
	 */
	private Map<String, String> getData(){
		
		Map<String, String> newOneMap = new HashMap<String, String>();
		
		String audit;
		String activityKind;
		
		switch(newOneClassChoose.getCheckedRadioButtonId()){
			case R.id.new_one_class_run : activityKind = "1"; break;
			case R.id.new_one_class_ball : activityKind = "3"; break;
			case R.id.new_one_class_exercise : activityKind = "2"; break;
			case R.id.new_one_class_other : activityKind = "-1"; break;
			default: activityKind = ""; break;
		}
		
		if(newOneNeedAccept.isChecked()){
			audit = "1";
		}
		else{
			audit = "0";
		}
		
		String remark = newOneSomething.getText().toString().trim();
		
		if(remark.equals("") || remark.equals(null)){
			remark = "��";
		}
		
		newOneMap.put("activity_name", "\""+newOneActivityName.getText().toString().trim()+"\"");
		
		//��ð��  Ҫ���ַ������д�������ת���ַ�
		newOneMap.put("start_time", "\""+newOneStartTime.getText().toString().trim()+"\"");
		newOneMap.put("end_time", "\""+newOneEndTime.getText().toString().trim()+"\"");

		newOneMap.put("place", "\""+newOneMeetPlace.getText().toString().trim()+"\"");
		newOneMap.put("max_number", "\""+newOneNumberLimit.getText().toString().trim()+"\"");
		newOneMap.put("remark", "\""+remark+"\"");
		newOneMap.put("activity_kind", activityKind);
		newOneMap.put("is_agree", audit);
		
		return newOneMap;
		
	}

	/**
	 * ���������Ϣ��������
	 * @return ���������򷵻�true
	 */
	private boolean checkInf(){
		String activity_name = newOneActivityName.getText().toString().trim();
		String start_time = newOneStartTime.getText().toString().trim();
		String end_time = newOneEndTime.getText().toString().trim();
		String place = newOneMeetPlace.getText().toString().trim();
		String maxNumber = newOneNumberLimit.getText().toString().trim();
		
		if(activity_name.equals("") || activity_name.equals(null)){
			Toast.makeText(this, "�������Ϊ��", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(start_time.equals("") || start_time.equals(null)){
			Toast.makeText(this, "��ʼʱ�䲻��Ϊ��", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(end_time.equals("") || end_time.equals(null)){
			Toast.makeText(this, "����ʱ�䲻��Ϊ��", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(place.equals("") || place.equals(null)){
			Toast.makeText(this, "����ص㲻��Ϊ��", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(maxNumber.equals("") || maxNumber.equals(null)){
			Toast.makeText(this, "�������޲���Ϊ��", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if( ! (Integer.parseInt(maxNumber) % 1 == 0 ) ){
			Toast.makeText(this, "�������ޱ���������", Toast.LENGTH_SHORT).show();
			return false;
		}
		else {
			return true;
		}
	}
	
	
	/**
	 * ���ø�����ļ�����
	 */
	private void setListener(){
		newOneNavigationLeft.setOnClickListener(this);
		newOneEnsure.setOnClickListener(this);
		newOneStartTime.setOnClickListener(this);
		newOneEndTime.setOnClickListener(this);
		newOneNeedAcceptText.setOnClickListener(this);
		
		newOneEnsure.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					newOneEnsure.setBackgroundResource(R.drawable.radio_button_new_one_down);
				}
				else if(event.getAction() == MotionEvent.ACTION_UP){
					newOneEnsure.setBackgroundResource(R.drawable.radio_button_new_one_normal);
				}
				return false;
			}
		});
		
		// ���ذ�ť�ĵ��Ч��
		newOneNavigationLeft.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					newOneNavigationLeft.setBackgroundResource(R.drawable.login_ensure_button_down);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					newOneNavigationLeft.setBackgroundResource(R.drawable.navigation_back);
				}
				return false;
			}
		});
	}
	
	/**
	 * ��������������ִ��
	 */
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		
		case R.id.new_one_navigation_left : {
			finish();
			break;
		}
		
		/**
		 * ��ȷ�ϼ�ִ�л�������������ȼ�����������ԣ�Ȼ�����ݷ�װ���ݸ�������
		 * �ɷ��������ص��ַ�����ȷ���Ƿ񷢲��ɹ�
		 */
		case R.id.new_one_ensure : {
			Log.v("map��ֵ", getData().toString());
			String result = null;
			if(checkInf()){
				result = PostData.postData(getData(), URLenum.release, Login.token);
				
				if(LOG.DEBUG){
					Log.v("������Ϣ�ķ��أ�", result);
				}
				

				JSONObject json;
				try {
					json = new JSONObject(result);
					if((Integer)json.get("errcode") == 0){
						Toast.makeText(NewOne.this, "������ɹ�", Toast.LENGTH_SHORT).show();
						finish();
					}
					else{
						showErrorMsg((Integer)json.get("errcode"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			break;
		}
		
		
		case R.id.new_one_start_time:{
			DateTimePick dateTime= new DateTimePick(NewOne.this, "");
			dateTime.dateTimePicKDialog(newOneStartTime);
			break;
		}
		
		case R.id.new_one_end_time:{
			DateTimePick dateTime= new DateTimePick(NewOne.this, "");
			dateTime.dateTimePicKDialog(newOneEndTime);
			break;
		}
		
		case R.id.new_one_need_accept_text:{
			
			if(newOneNeedAccept.isChecked()){
				newOneNeedAccept.setChecked(false);
			}
			else{
				newOneNeedAccept.setChecked(true);
			}
			
			break;
		}
		
		default : {
			return;
		}
		
		}
	}

	/**
	 * ����EditText�Ľ���任����
	 */
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		
		switch(v.getId()){
		
		case R.id.new_one_start_time:{
			if(hasFocus){
				DateTimePick dateTime= new DateTimePick(NewOne.this, "");
				dateTime.dateTimePicKDialog(newOneStartTime);
			}
			break;
		}
		
		case R.id.new_one_end_time:{
			if(hasFocus){
				DateTimePick dateTime= new DateTimePick(NewOne.this, "");
				dateTime.dateTimePicKDialog(newOneEndTime);
			}
			break;
		}
		
		}
		
	}
	
	
	/**
	 * ������Ƿ��صĴ�����Ϣ����ʾ
	 * @param num
	 */
	private void showErrorMsg(int num){
		
		switch(num){
		
		case 210:{
			Toast.makeText(NewOne.this, "��ʼʱ�䲻�����ڵ�ǰʱ��", Toast.LENGTH_SHORT).show();
			break;
		}
		
		case 213:{
			Toast.makeText(NewOne.this, "����ʱ�䲻�����ڿ�ʼʱ��", Toast.LENGTH_SHORT).show();
			break;
		}
		
		case 214:{
			Toast.makeText(NewOne.this, "����ƹ���", Toast.LENGTH_SHORT).show();
			break;
		}
		
		case 215:{
			Toast.makeText(NewOne.this, "��ע��Ϣ����", Toast.LENGTH_SHORT).show();
			break;
		}
		
		default:{
			Toast.makeText(NewOne.this, "�ǺǺǣ�����ɶ?error:"+num, Toast.LENGTH_SHORT).show();
			break;
		}
		
		}
	}
	
	
}
