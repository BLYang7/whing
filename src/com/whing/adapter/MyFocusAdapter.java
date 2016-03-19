package com.whing.adapter;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.whing.InfShow;
import com.whing.LetterChat;
import com.whing.external.PostData;
import com.whing.external.URLenum;
import com.whing.imgcut.CircleImg;
import com.whing.imgcut.SetUserImage;
import com.whing.viewHolder.MyFocusHolder;
import com.whing.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MyFocusAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	List<Map<String, Object>> mData;
	Context context;
	Resources res;
	
	public MyFocusAdapter(Context context) {
		this.mInflater = LayoutInflater.from(context);
		this.context = context;
	}
	
	public MyFocusAdapter(Context context, List<Map<String, Object>> mData){
		this(context);
		this.mData = mData;
		this.res = context.getResources();
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
		
		MyFocusHolder holder = null;
		
		if(convertView == null){
			
			holder = new MyFocusHolder();
			convertView = mInflater.inflate(R.layout.my_focus_item, null);
			holder.focusUserImage = (CircleImg) convertView.findViewById(R.id.my_focus_item_user_image);
			holder.focusUserName = (TextView) convertView.findViewById(R.id.my_focus_item_user_name);
//			holder.focusUserOff = (Button) convertView.findViewById(R.id.my_focus_item_off_focus);
			convertView.setTag(holder);	
			
		}else{
			holder = (MyFocusHolder)convertView.getTag();
		}
		
		Drawable drawable = SetUserImage.setImage(
				context, 
				res, 
				mData.get(position).get("userId").toString(), 
				mData.get(position).get("userImage").toString() );
		
		holder.focusUserImage.setImageDrawable(drawable);
		
//		holder.focusUserImage.setOnClickListener(new OnClickListener(){
//			
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(context, InfShow.class);
//				intent.putExtra("userId", mData.get(position).get("userId").toString());
//				context.startActivity(intent);
//			}
//			
//		});
		
		holder.focusUserName.setText(mData.get(position).get("userName").toString());
		
//		holder.focusUserOff.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				
//				String result = PostData.getData(
//						"&follower_id="+mData.get(position).get("userId").toString().trim(), 
//						URLenum.unfollow);
//				Log.v("unfollow", result);
//				
//				try {
//					JSONObject json = new JSONObject(result);
//					String errcode = json.get("errcode").toString();
//					
//					if(errcode.equals("0")){
//						Toast.makeText(context, "已取消关注", Toast.LENGTH_SHORT).show();
//						((Activity) context).finish();
//					}
//					
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//			}
//		});
		
		return convertView;
	}

}
