package com.whing.state;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import com.whing.ActivityDetail;
import com.whing.Hall;
import com.whing.Login;
import com.whing.R;
import com.whing.external.CustomDialog;
import com.whing.external.PostData;
import com.whing.external.URLenum;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

public class Status {

	public static void setState(final Button button,
			final List<Map<String, Object>> mData, 
			final int position,
			final Context context, 
			final BaseAdapter adapter) {

		Resources res = context.getResources();

		
		/**
		 * 关于按钮状态及其各状态的监听响应的设置 其中
		 * -1表示参加，
		 * 0表示申请中，
		 * 1表示已参加，
		 * 2表示被拒绝，
		 * 3表示已过期，
		 * 5表示进行中
		 * 7表示已完成
		 */
		
		//对已参加的活动的处理
		if (mData.get(position).get("state").toString().trim().equals("1")) {

			button.setBackground(res.getDrawable(R.drawable.simple_item_already_in));

			button.setText("已参加");

			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setPositiveButton("确认退出", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							dialog.dismiss();
							
							String str = "&act_id="	+ mData.get(position).get("activityId");
							String result = PostData.getData(str, URLenum.quit);
							Log.v("退出活动的返回值：", result);
							try {
								JSONObject json = new JSONObject(result);

								if (json.get("errcode").toString().trim().equals("292")) {
									Toast.makeText(context, "不允许发布者退出",Toast.LENGTH_SHORT).show();
								} 
								else if (json.get("errcode").toString().trim().equals("0")) {
									
									String result3 = PostData.getData(str, URLenum.info);

									Log.v("获取单个活动的详情", result3);
									try {
										JSONObject json3 = new JSONObject(result3);

										((Map) mData.get(position)).put("userImage", json3.getString("publisher_photo"));
										((Map) mData.get(position)).put("userName", json3.getString("publisher_nickname"));
										((Map) mData.get(position)).put("activityId",json3.getString("activity_id"));
										((Map) mData.get(position)).put("userId",json3.getString("user_id"));
										((Map) mData.get(position)).put("endTime",json3.getString("end_time"));
										((Map) mData.get(position)).put("remark",json3.getString("remark"));
										((Map) mData.get(position)).put("activityName", json3.getString("activity_name"));
										((Map) mData.get(position)).put("meetPlace", json3.getString("place"));
										((Map) mData.get(position)).put("meetTime", json3.getString("start_time"));
										((Map) mData.get(position)).put("maxNumber",Integer.parseInt(json3.getString("max_number")));
										((Map) mData.get(position)).put("presentNumber", Integer.parseInt(json3.getString("present_number")));
										((Map) mData.get(position)).put("state",Integer.parseInt(json3.getString("status")));
										((Map) mData.get(position)).put("userGender", Integer.parseInt(json3.getString("publisher_sex")));
										((Map) mData.get(position)).put("activityKind", Integer.parseInt(json3.getString("activity_kind")));
										
									} catch (JSONException e) {
										e.printStackTrace();
									}
									adapter.notifyDataSetChanged();
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
							
						}
					});
					
					builder.create().show();
					
					
				}
			});
		}
		
		//当没有参加该活动时，验证是否人数已满
		else if (mData.get(position).get("presentNumber").equals(mData.get(position).get("maxNumber"))) {
			button.setBackground(res.getDrawable(R.drawable.simple_item_full));
			button.setText("已满");
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setPositiveButton("人数已满", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					builder.create().show();
				}
			});
		}
		
		//对未参加的活动的处理
		else if (mData.get(position).get("state").toString().trim().equals("-1")) {

			button.setBackground(res.getDrawable(R.drawable.simple_item_in));
			button.setText("参加");

			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setPositiveButton("确认参加", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							
							Toast.makeText(context, "已递交加入申请", Toast.LENGTH_SHORT).show();

							String str = "&act_id="	+ mData.get(position).get("activityId");
							String result = PostData.getData(str, URLenum.join);
							Log.v("加入活动的返回值：", result);

							try {
								JSONObject json2 = new JSONObject(result);
								if (json2.get("errcode").toString().trim().equals("0")) {
									
									String result2 = PostData.getData(str, URLenum.info);
									Log.v("获取单个活动的详情", result2);
									
									try {
										JSONObject json = new JSONObject(result2);

										((Map) mData.get(position)).put("userImage", json.getString("publisher_photo"));
										((Map) mData.get(position)).put("userName", json.getString("publisher_nickname"));
										((Map) mData.get(position)).put("userGender", Integer.parseInt(json.getString("publisher_sex")));
										((Map) mData.get(position)).put("activityKind", Integer.parseInt(json.getString("activity_kind")));
										((Map) mData.get(position)).put("activityName", json.getString("activity_name"));
										((Map) mData.get(position)).put("meetPlace", json.getString("place"));
										((Map) mData.get(position)).put("meetTime",	json.getString("start_time"));
										((Map) mData.get(position)).put("maxNumber",Integer.parseInt(json.getString("max_number")));
										((Map) mData.get(position)).put("presentNumber", Integer.parseInt(json.getString("present_number")));
										((Map) mData.get(position)).put("activityId",json.getString("activity_id"));
										((Map) mData.get(position)).put("userId",json.getString("user_id"));
										((Map) mData.get(position)).put("endTime",json.getString("end_time"));
										((Map) mData.get(position)).put("remark",json.getString("remark"));
										((Map) mData.get(position)).put("state",Integer.parseInt(json.getString("status")));

									} catch (JSONException e) {
										e.printStackTrace();
									}

									adapter.notifyDataSetChanged();
								}

								else if (json2.get("errcode").toString().trim().equals("250")) {
									CustomDialog.Builder builder = new CustomDialog.Builder(context);
									builder.setMessage("当前人数已满");
									builder.setTitle("提示");
									builder.setPositiveButton("确定",
											new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog,	int which) {
													dialog.dismiss();
												}
											});
									builder.create().show();
								}
								
								else if (json2.get("errcode").toString().trim().equals("252")){
									CustomDialog.Builder builder = new CustomDialog.Builder(context);
									builder.setMessage("当前活动截至报名时间已过");
									builder.setTitle("提示");
									builder.setPositiveButton("确定",
											new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog,	int which) {
													dialog.dismiss();
												}
											});
									builder.create().show();
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
							
						}
					});
					builder.create().show();
					
				}
			});

		}

		//对正在申请中的活动的处理
		else if (mData.get(position).get("state").toString().trim().equals("0")) {

			button.setBackground(res.getDrawable(R.drawable.simple_item_already_in));

			button.setText("申请中");

			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "正在申请，请耐心等候", Toast.LENGTH_SHORT).show();
				}
			});
		}

		//对被拒绝的活动的处理
		else if (mData.get(position).get("state").toString().trim().equals("2")) {

			button.setBackground(res.getDrawable(R.drawable.simple_item_full));
			button.setText("被拒绝");

			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "您已被拒绝，不允许再加入", Toast.LENGTH_SHORT).show();
				}
			});
		}

		//对已过期的活动的处理
		else if (mData.get(position).get("state").toString().trim().equals("3")) {

			button.setBackground(res.getDrawable(R.drawable.simple_item_full));
			button.setText("已过期");

			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "活动已过期，不允许再加入", Toast.LENGTH_SHORT).show();
				}
			});
		}

		//对进行中的活动的处理
		else if (mData.get(position).get("state").toString().trim().equals("5")) {

			button.setBackground(res.getDrawable(R.drawable.simple_item_full));

			button.setText("已退出");

			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "已退出，不允许再加入", Toast.LENGTH_SHORT).show();
				}
			});
		}

		//对已完成活动的处理
		else if (mData.get(position).get("state").toString().trim().equals("7")) {
			button.setBackground(res.getDrawable(R.drawable.simple_item_full));
			button.setText("已完成");
			
			button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "当前活动已完成", Toast.LENGTH_SHORT).show();
				}
			});
			
		}

		

	}
	

}
