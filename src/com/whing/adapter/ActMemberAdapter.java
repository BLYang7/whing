package com.whing.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.whing.R;
import com.whing.imgcut.CircleImg;
import com.whing.imgcut.SetUserImage;

public class ActMemberAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	List<Map<String, Object>> mData;
	Context context;
	Resources res;
	
	public ActMemberAdapter(Context context) {
		this.mInflater = LayoutInflater.from(context);
		this.context = context;
	}
	
	public ActMemberAdapter(Context context, List<Map<String, Object>> mData){
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
		
		Holder holder = null;
		
		if(convertView == null){
			
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.act_member_list_item, null);
			holder.userImage = (CircleImg) convertView.findViewById(R.id.act_member_item_user_image);
			holder.userName = (TextView) convertView.findViewById(R.id.act_member_item_user_name);
			convertView.setTag(holder);	
			
		}else{
			holder = (Holder)convertView.getTag();
		}
		
		Drawable drawable = SetUserImage.setImage(
				context, 
				res, 
				mData.get(position).get("userId").toString(), 
				mData.get(position).get("userImage").toString() );
		
		holder.userImage.setImageDrawable(drawable);
		
		holder.userName.setText(mData.get(position).get("userName").toString());
		
		return convertView;
	}

	private class Holder{
		public CircleImg userImage;
		public TextView userName;
	}
	
}
