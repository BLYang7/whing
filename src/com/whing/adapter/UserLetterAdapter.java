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

import com.whing.imgcut.CircleImg;
import com.whing.imgcut.SetUserImage;
import com.whing.viewHolder.UserLetterViewHolder;
import com.whing.R;

public class UserLetterAdapter extends BaseAdapter{
	
	private LayoutInflater mInflater;
	List<Map<String, Object>> mData;
	Context context;
	Resources res;
	
	
	public UserLetterAdapter(Context context) {
		this.mInflater = LayoutInflater.from(context);
		this.context = context;
	}
	
	public UserLetterAdapter(Context context, List<Map<String, Object>> mData){
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
		
		UserLetterViewHolder holder = null;
		
		if(convertView == null){
			holder = new UserLetterViewHolder();
			convertView = mInflater.inflate(R.layout.simple_letter_item, null);
			holder.userImage = (CircleImg) convertView.findViewById(R.id.simple_letter_user_image);
			holder.userName = (TextView) convertView.findViewById(R.id.simple_letter_item_user_name);
			holder.msg = (TextView) convertView.findViewById(R.id.simple_letter_inf_show);
			holder.time = (TextView) convertView.findViewById(R.id.simple_letter_item_time_show);
			convertView.setTag(holder);	
		}
		else{
			holder = (UserLetterViewHolder)convertView.getTag();
		}
		
		holder.userName.setText((String)mData.get(position).get("userName"));
		holder.msg.setText((String)mData.get(position).get("msg"));
		holder.time.setText((String)mData.get(position).get("time"));
		
		
//		"http://www.easyicon.net/api/resize_png_new.php?id=1101848&size=128",
//		"http://www.easyicon.net/api/resize_png_new.php?id=1065241&size=128",
//		"http://cdn-img.easyicon.net/png/5019/501991.png"
//		Bitmap image = GetImage.getHttpBitmap((String)mData.get(position).get("userImage").toString());
//		holder.userImage.setImageBitmap(image);
		
		Drawable drawable = SetUserImage.setImage(
				context, 
				res, 
				mData.get(position).get("userId").toString(), 
				mData.get(position).get("userImage").toString());
		holder.userImage.setImageDrawable(drawable);
		
		return convertView;
	}
}







