package com.whing.adapter;

import java.util.List;
import java.util.Map;

import com.whing.InfShow;
import com.whing.imgcut.CircleImg;
import com.whing.imgcut.SetUserImage;
import com.whing.viewHolder.ReviewHolder;
import com.whing.viewHolder.UserLetterViewHolder;
import com.whing.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.sax.StartElementListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ReviewAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	List<Map<String, Object>> mData;
	Context context;
	
	public ReviewAdapter(Context context){
		this.mInflater = LayoutInflater.from(context);
		this.context = context;
	}
	
	public ReviewAdapter(Context context, List<Map<String, Object>> mData){
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
		
		ReviewHolder holder = null;
		
		if(convertView == null){
			holder = new ReviewHolder();
			convertView = mInflater.inflate(R.layout.act_review, null);
			holder.reviewName = (TextView) convertView.findViewById(R.id.review_name);
			holder.remindResponse = (TextView) convertView.findViewById(R.id.remind_response);
			holder.responseName = (TextView) convertView.findViewById(R.id.response_name);
			holder.reviewContent = (TextView) convertView.findViewById(R.id.review_content);
			holder.reviewUserImage = (CircleImg) convertView.findViewById(R.id.review_user_image);
			convertView.setTag(holder);
		}
		else{
			holder = (ReviewHolder) convertView.getTag();
		}
		
		/**
		 * �����������ĸ�����ʾ�ı�
		 */
		//�����ߵ��ǳ�
		holder.reviewName.setText((String) mData.get(position).get("userName"));
		
		Drawable drawable = SetUserImage.setImage(
				context, 
				context.getResources(),
				mData.get(position).get("userId").toString(),
				mData.get(position).get("userImage").toString());
		holder.reviewUserImage.setImageDrawable(drawable);
		
		//�鿴�Ƿ��лظ������û�У��˴���Ϊ����������һ����Ϊ�ա�����У��˴���Ϊ���ظ�������һ����ΪreplyName
		if(Integer.parseInt( mData.get(position).get("reference").toString() )  == 0 ){
			holder.remindResponse.setText(":");
			holder.responseName.setText("");
		}
		else{
			holder.remindResponse.setText("�ظ�");
			holder.responseName.setText(mData.get(position).get("replyName").toString());
		}
		holder.reviewContent.setText((String) mData.get(position).get("content"));
		
		
		holder.reviewName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(context, InfShow.class);
				intent.putExtra("userId", mData.get(position).get("userId").toString());
				context.startActivity(intent);
				
			}
		});
		
		
		holder.responseName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(context, InfShow.class);
				intent.putExtra("userId", mData.get(position).get("replyId").toString());
				context.startActivity(intent);
				
			}
		});
		
		return convertView;
	}



}





