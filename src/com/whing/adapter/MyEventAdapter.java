package com.whing.adapter;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.whing.Hall;
import com.whing.InfShow;
import com.whing.external.PostData;
import com.whing.external.SetActData;
import com.whing.external.URLenum;
import com.whing.imgcut.SetUserImage;
import com.whing.state.Status;
import com.whing.viewHolder.MyEventViewHolder;
import com.whing.R;

public class MyEventAdapter extends BaseAdapter{
	
	private LayoutInflater mInflater;
	private List<Map<String, Object>> mData;
	Context context;
	Resources res;
	
	public MyEventAdapter(Context context) {
		this.mInflater = LayoutInflater.from(context);
		this.res = context.getResources();
		this.context = context;
	}
	
	public MyEventAdapter(Context context, List<Map<String, Object>> mData){
		this(context);
		this.mData = mData;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		MyEventViewHolder holder = null;
		
		if(convertView == null){
			holder = new MyEventViewHolder();
			convertView = mInflater.inflate(R.layout.simple_item, null);
			holder.userImage = (ImageView) convertView.findViewById(R.id.simple_item_user_image);
			holder.userGender = (ImageView) convertView.findViewById(R.id.simple_item_user_gender);
			holder.userName = (TextView) convertView.findViewById(R.id.simple_item_user_name);
			holder.activityKind = (ImageView) convertView.findViewById(R.id.simple_item_activity_kind);
			holder.activityName = (TextView) convertView.findViewById(R.id.simple_item_activity_name);
			holder.meetPlace = (TextView) convertView.findViewById(R.id.simple_item_meet_place);
			holder.meetTime = (TextView) convertView.findViewById(R.id.simple_item_meet_time);
			holder.number = (TextView) convertView.findViewById(R.id.simple_item_number);
			holder.chooseButton = (Button) convertView.findViewById(R.id.simple_item_choose_button);
			convertView.setTag(holder);	
		}
		
		else{
			holder = (MyEventViewHolder) convertView.getTag();
		}
		
		
		holder.userName.setText((String)mData.get(position).get("userName"));
		
		Drawable drawable = SetUserImage.setImage(
				context, 
				res, 
				(String)mData.get(position).get("userId"),
				mData.get(position).get("userImage").toString());
		
		holder.userImage.setImageDrawable(drawable);
		
		holder.userImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, InfShow.class);
				intent.putExtra("userId", mData.get(position).get("userId").toString());
				context.startActivity(intent);
			}
		});
		
		
		//设置性别，返回的数据是1，表示男性，数据是0表示女性
		if(mData.get(position).get("userGender").equals(1)){
			holder.userGender.setImageDrawable(res.getDrawable(R.drawable.male));
		}
		else{
			holder.userGender.setImageDrawable(res.getDrawable(R.drawable.female));
		}
		
		//设置人数
		int presentNumber = (Integer) mData.get(position).get("presentNumber");
		int maxNumber = (Integer) mData.get(position).get("maxNumber");
		String numberShow = presentNumber + "/" + maxNumber;
		holder.number.setText(numberShow);
		
		
		//设置活动类型,1表示跑步，2表示健身，3表示球类，4表示其他
		if(mData.get(position).get("activityKind").equals(1)){
			holder.activityKind.setImageDrawable(res.getDrawable(R.drawable.run_yellow));
		}
		else if(mData.get(position).get("activityKind").equals(2)){
			holder.activityKind.setImageDrawable(res.getDrawable(R.drawable.exercise_yellow));
		}
		else if(mData.get(position).get("activityKind").equals(3)){
			holder.activityKind.setImageDrawable(res.getDrawable(R.drawable.ball_yellow));
		}
		else{
			holder.activityKind.setImageDrawable(res.getDrawable(R.drawable.other_yellow));
		}
		
		holder.activityName.setText((String)mData.get(position).get("activityName"));
		holder.meetPlace.setText((String)mData.get(position).get("meetPlace"));
		holder.meetTime.setText((String)mData.get(position).get("meetTime"));
		
		Status.setState(holder.chooseButton, 
				mData, 
				position, 
				context, 
				MyEventAdapter.this);
		
//		//设置选项按钮的状态，1表示参加，2表示已参加，3表示已满
//		if (mData.get(position).get("state").equals(-1)) {
//			holder.chooseButton.setBackground(res.getDrawable(R.drawable.simple_item_in));
//			holder.chooseButton.setText("参加");
//			
//			holder.chooseButton.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					String str = "&act_id=" + mData.get(position).get("activityId");
//					String result = PostData.getData(str, URLenum.join);
//					Log.v("加入活动的返回值：", result);
//					try {
//						JSONObject json = new JSONObject(result);
//						if(json.get("errcode").toString().trim().equals("0")){
//							mData = SetActData.setData(7, 0, 0);
//							MyEventAdapter.this.notifyDataSetChanged();
//						}
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//					
//				}
//			});
//			
//		} else if (mData.get(position).get("state").equals(0)) {
//			holder.chooseButton.setBackground(res.getDrawable(
//					R.drawable.simple_item_already_in));
//			holder.chooseButton.setText("申请中");
//		} 
//		else if (mData.get(position).get("state").equals(1)) {
//			holder.chooseButton.setBackground(res.getDrawable(
//					R.drawable.simple_item_already_in));
//			holder.chooseButton.setText("已参加");
//			holder.chooseButton.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					String str = "&act_id=" + mData.get(position).get("activityId");
//					String result = PostData.getData(str, URLenum.quit);
//					Log.v("退出活动的返回值：", result);
//					try {
//						JSONObject json = new JSONObject(result);
//						if(json.get("errcode").toString().trim().equals("292") ){
//							Toast.makeText(context, "不允许发布者退出", Toast.LENGTH_SHORT).show();
//						}
//						else if(json.get("errcode").toString().trim().equals("0")){
//							mData = SetActData.setData(7, 0, 0);
//							MyEventAdapter.this.notifyDataSetChanged();
//						}
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				}
//			});
//		}
//		else if(mData.get(position).get("state").equals(2)){
//			holder.chooseButton.setBackground(res.getDrawable(
//					R.drawable.simple_item_full));
//			holder.chooseButton.setText("被拒绝");
//		}
//		else if(mData.get(position).get("state").equals(3)){
//			holder.chooseButton.setBackground(res.getDrawable(
//					R.drawable.simple_item_full));
//			holder.chooseButton.setText("已过期");
//		}
//		else if(mData.get(position).get("state").equals(5)){
//			holder.chooseButton.setBackground(res.getDrawable(
//					R.drawable.simple_item_full));
//			holder.chooseButton.setText("进行中");
//		}
//		else {
//			holder.chooseButton.setBackground(res.getDrawable(
//					R.drawable.simple_item_full));
//			holder.chooseButton.setText("已满");
//		}
		
		return convertView;
	}
}
