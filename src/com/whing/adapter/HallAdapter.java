package com.whing.adapter;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
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
import com.whing.external.CustomDialog;
import com.whing.external.PostData;
import com.whing.external.SetActData;
import com.whing.external.URLenum;
import com.whing.imgcut.CircleImg;
import com.whing.imgcut.SetUserImage;
import com.whing.state.Status;
import com.whing.viewHolder.HallViewHolder;
import com.whing.R;

public class HallAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	List<Map<String, Object>> mData;
	Resources res;
	Context context;
	HallViewHolder holder = null;
	
	public HallAdapter(Context context) {
		this.mInflater = LayoutInflater.from(context);
		this.res = context.getResources();
		this.context = context;
	}
	
	public HallAdapter(Context context, List<Map<String, Object>> mData) {
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
	public View getView(final int position, View convertView,
			ViewGroup parent) {

		final String userId = mData.get(position).get("userId").toString();
		
		if (convertView == null) {
			holder = new HallViewHolder();
			convertView = mInflater.inflate(R.layout.simple_item, null);
			holder.userImage = (CircleImg) convertView.findViewById(R.id.simple_item_user_image);
			holder.userGender = (ImageView) convertView.findViewById(R.id.simple_item_user_gender);
			holder.userName = (TextView) convertView.findViewById(R.id.simple_item_user_name);
			holder.activityKind = (ImageView) convertView.findViewById(R.id.simple_item_activity_kind);
			holder.activityName = (TextView) convertView.findViewById(R.id.simple_item_activity_name);
			holder.meetPlace = (TextView) convertView.findViewById(R.id.simple_item_meet_place);
			holder.meetTime = (TextView) convertView.findViewById(R.id.simple_item_meet_time);
			holder.number = (TextView) convertView.findViewById(R.id.simple_item_number);
			holder.chooseButton = (Button) convertView.findViewById(R.id.simple_item_choose_button);
			convertView.setTag(holder);
		} else {
			holder = (HallViewHolder) convertView.getTag();
		}
		
		holder.userName.setText((String) mData.get(position).get("userName"));

		Log.v("userImage", mData.get(position).get("userImage").toString());
		
		holder.userImage.setImageDrawable(
				SetUserImage.setImage(
						context,
						res, 
						userId,
						mData.get(position).get("userImage").toString())); 
		
		holder.userImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (!Hall.slideMenu.isMainScreenShowing()) {
					Hall.slideMenu.closeMenu();
					return;
				}
				
				Intent intent = new Intent(context, InfShow.class);
				intent.putExtra("userId", userId);
				context.startActivity(intent);
				
			}
		});

		// �����Ա𣬷��ص�������1����ʾ���ԣ�������0��ʾŮ��
		if (mData.get(position).get("userGender").equals(1)) {
			holder.userGender.setImageDrawable(res.getDrawable(R.drawable.male));
		} else {
			holder.userGender.setImageDrawable(res.getDrawable(R.drawable.female));
		}

		//��������
		int presentNumber = (Integer) mData.get(position).get("presentNumber");
		int maxNumber = (Integer) mData.get(position).get("maxNumber");
		String numberShow = presentNumber + "/" + maxNumber;
		holder.number.setText(numberShow);

		// ���û����,1��ʾ�ܲ���3��ʾ���࣬2��ʾ����4��ʾ����
		if (mData.get(position).get("activityKind").equals(1)) {
			holder.activityKind.setImageDrawable(res.getDrawable(R.drawable.run_yellow));
		} else if (mData.get(position).get("activityKind").equals(2)) {
			holder.activityKind.setImageDrawable(res.getDrawable(R.drawable.exercise_yellow));
		} else if (mData.get(position).get("activityKind").equals(3)) {
			holder.activityKind.setImageDrawable(res.getDrawable(R.drawable.ball_yellow));
		} else {
			holder.activityKind.setImageDrawable(res.getDrawable(R.drawable.other_yellow));
		}

		holder.activityName.setText((String) mData.get(position).get("activityName"));
		holder.meetPlace.setText((String) mData.get(position).get("meetPlace"));
		holder.meetTime.setText((String) mData.get(position).get("meetTime"));
		
		Status.setState(holder.chooseButton, 
				mData, 
				position, 
				context, 
				HallAdapter.this);
		
		return convertView;
	}
	
}
