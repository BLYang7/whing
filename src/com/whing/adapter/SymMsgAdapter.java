package com.whing.adapter;

import java.util.List;
import java.util.Map;

import com.whing.InfShow;
import com.whing.external.MsgShow;
import com.whing.imgcut.SetUserImage;
import com.whing.viewHolder.SymMsgHolder;
import com.whing.R;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SymMsgAdapter extends BaseAdapter{
	
	private LayoutInflater mInflater;
	private List<Map<String, Object>> mData;
	Context context;
	Resources res;
	
	public SymMsgAdapter(Context context) {
		this.mInflater = LayoutInflater.from(context);
	}
	
	public SymMsgAdapter(Context context, List<Map<String, Object>> mData){
		this(context);
		this.mData = mData;
		this.context = context;
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
		
		SymMsgHolder holder = null;
		
		if(convertView == null){
			holder = new SymMsgHolder();
			convertView = mInflater.inflate(R.layout.sym_msg_show, null);
			holder.symMsgShow = (TextView) convertView.findViewById(R.id.system_message_item_text);
			holder.symTimeShow = (TextView) convertView.findViewById(R.id.system_message_item_time);
			holder.symImage = (ImageView) convertView.findViewById(R.id.system_message_item_image);
			convertView.setTag(holder);	
		}
		else{
			holder = (SymMsgHolder)convertView.getTag();
		}
		
		String msgShow = MsgShow.msgShow(mData, position);
		
		holder.symMsgShow.setText(msgShow);
		holder.symTimeShow.setText(((String)mData.get(position).get("send_time")).subSequence(5, 10));
		
		final String userImage = mData.get(position).get("senderImage").toString();
		
		Drawable drawable ;
		if(userImage.equals("null")||userImage.equals("")){
			drawable = res.getDrawable(R.drawable.logo_whing);
		}
		else{
			drawable = SetUserImage.setImage(
					context,
					res,
					mData.get(position).get("sender_id").toString(), 
					userImage);
		}
		holder.symImage.setImageDrawable(drawable);
		
		holder.symImage.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				
				if(userImage.equals("null") || userImage.equals("")){
					return;
				}else{
					Intent intent = new Intent(context, InfShow.class);
					intent.putExtra("userId", mData.get(position).get("sender_id").toString());
					context.startActivity(intent);
				}
			}
		});
		
		return convertView;
	}

}
