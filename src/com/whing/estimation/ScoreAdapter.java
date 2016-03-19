package com.whing.estimation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.whing.R;
import com.whing.imgcut.SetUserImage;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

public class ScoreAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	List<HashMap<String, Object>> mData;
	Context context;
	
	public List<HashMap<String, Object>> outData = new ArrayList<HashMap<String, Object>>();
	
	
	Holder holder = null;
	
	public ScoreAdapter(Context context, List<HashMap<String, Object>> mData){
		this.mInflater = LayoutInflater.from(context);
		this.mData = mData;
//		this.outData = mData;
		this.context = context;
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
		
		
		
		if (convertView == null) {
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.score_list_view, null);
			
			holder.name = (TextView) convertView.findViewById(R.id.score_list_view_name);
			holder.score = (RatingBar) convertView.findViewById(R.id.score_list_view_rating);
			holder.image = (ImageView) convertView.findViewById(R.id.score_list_view_image);
			
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		initOutData(holder, position);
		
		holder.name.setText((String) mData.get(position).get("userName"));

		Drawable drawable = SetUserImage.setImage(
				context,
				context.getResources(), 
				mData.get(position).get("userId").toString(),
				mData.get(position).get("photo").toString());
		
		holder.image.setImageDrawable(drawable);
		
		holder.score.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				
				HashMap<String, Object> map = new HashMap<String, Object>();
				
				map.put("estimation", rating);
				map.put("activity_id", mData.get(position).get("actId").toString());
				map.put("estimator_id", mData.get(position).get("userId").toString());
				
				try{
					outData.get(position).putAll(map);
				}catch(Exception e){
					outData.add(map);
				}
				
			}
		});

		return convertView;
	}
	
	
	private void initOutData(Holder holder, int position){
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("estimation", holder.score.getRating());
		map.put("activity_id", mData.get(position).get("actId").toString());
		map.put("estimator_id", mData.get(position).get("userId").toString());
		
		try{
			outData.get(position).putAll(map);
		}catch(Exception e){
			outData.add(map);
		}
		
	}
	
	
	private class Holder{
		TextView name;
		RatingBar score;
		ImageView image;
	}

}
