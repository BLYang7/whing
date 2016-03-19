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
		
		init();    //组件初始化
		
		preNet();    //联网准备
		
		setInf();    //设置用户名和头像
		
		setButtonCheck();    //设置活动类型
		
		setListener();    //设置各组件监听器
		
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
	 * 设置用户头像和用户名
	 */
	private void setInf(){
		newOneUserImage.setImageDrawable(Login.userImageDrawable);
		newOneUserName.setText(Login.nickName);
	}
	
	/**
	 * 设置checkButton的默认值，这个默认值要从前面获取
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
	 * 初始化各个组件
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
	 * 联网之前的准备
	 * <p> 1、设定目标URL网址 </p>
	 * <p> 2、强制主进程内联网 </p>
	 */
	private void preNet(){
    	StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
    	StrictMode.setThreadPolicy(policy);
	}
	
	/**
	 * 获取当前活动注册信息
	 * @return Map数据
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
			remark = "无";
		}
		
		newOneMap.put("activity_name", "\""+newOneActivityName.getText().toString().trim()+"\"");
		
		//有冒号  要对字符串进行处理，加入转义字符
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
	 * 检查输入信息的完整性
	 * @return 输入完整则返回true
	 */
	private boolean checkInf(){
		String activity_name = newOneActivityName.getText().toString().trim();
		String start_time = newOneStartTime.getText().toString().trim();
		String end_time = newOneEndTime.getText().toString().trim();
		String place = newOneMeetPlace.getText().toString().trim();
		String maxNumber = newOneNumberLimit.getText().toString().trim();
		
		if(activity_name.equals("") || activity_name.equals(null)){
			Toast.makeText(this, "活动名不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(start_time.equals("") || start_time.equals(null)){
			Toast.makeText(this, "开始时间不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(end_time.equals("") || end_time.equals(null)){
			Toast.makeText(this, "结束时间不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(place.equals("") || place.equals(null)){
			Toast.makeText(this, "会面地点不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(maxNumber.equals("") || maxNumber.equals(null)){
			Toast.makeText(this, "人数上限不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if( ! (Integer.parseInt(maxNumber) % 1 == 0 ) ){
			Toast.makeText(this, "人数上限必须是整数", Toast.LENGTH_SHORT).show();
			return false;
		}
		else {
			return true;
		}
	}
	
	
	/**
	 * 设置各组件的监听器
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
		
		// 返回按钮的点击效果
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
	 * 各个监听器任务执行
	 */
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		
		case R.id.new_one_navigation_left : {
			finish();
			break;
		}
		
		/**
		 * 按确认键执行活动发布操作，首先检查数据完整性，然后将数据封装传递给服务器
		 * 由服务器返回的字符串，确认是否发布成功
		 */
		case R.id.new_one_ensure : {
			Log.v("map的值", getData().toString());
			String result = null;
			if(checkInf()){
				result = PostData.postData(getData(), URLenum.release, Login.token);
				
				if(LOG.DEBUG){
					Log.v("发布信息的返回：", result);
				}
				

				JSONObject json;
				try {
					json = new JSONObject(result);
					if((Integer)json.get("errcode") == 0){
						Toast.makeText(NewOne.this, "活动发布成功", Toast.LENGTH_SHORT).show();
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
	 * 设置EditText的焦点变换方法
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
	 * 发布活动是返回的错误信息的提示
	 * @param num
	 */
	private void showErrorMsg(int num){
		
		switch(num){
		
		case 210:{
			Toast.makeText(NewOne.this, "开始时间不能早于当前时间", Toast.LENGTH_SHORT).show();
			break;
		}
		
		case 213:{
			Toast.makeText(NewOne.this, "结束时间不能早于开始时间", Toast.LENGTH_SHORT).show();
			break;
		}
		
		case 214:{
			Toast.makeText(NewOne.this, "活动名称过长", Toast.LENGTH_SHORT).show();
			break;
		}
		
		case 215:{
			Toast.makeText(NewOne.this, "备注信息过长", Toast.LENGTH_SHORT).show();
			break;
		}
		
		default:{
			Toast.makeText(NewOne.this, "呵呵呵，这是啥?error:"+num, Toast.LENGTH_SHORT).show();
			break;
		}
		
		}
	}
	
	
}
