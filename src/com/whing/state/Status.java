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
		 * ���ڰ�ť״̬�����״̬�ļ�����Ӧ������ ����
		 * -1��ʾ�μӣ�
		 * 0��ʾ�����У�
		 * 1��ʾ�Ѳμӣ�
		 * 2��ʾ���ܾ���
		 * 3��ʾ�ѹ��ڣ�
		 * 5��ʾ������
		 * 7��ʾ�����
		 */
		
		//���ѲμӵĻ�Ĵ���
		if (mData.get(position).get("state").toString().trim().equals("1")) {

			button.setBackground(res.getDrawable(R.drawable.simple_item_already_in));

			button.setText("�Ѳμ�");

			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setPositiveButton("ȷ���˳�", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							dialog.dismiss();
							
							String str = "&act_id="	+ mData.get(position).get("activityId");
							String result = PostData.getData(str, URLenum.quit);
							Log.v("�˳���ķ���ֵ��", result);
							try {
								JSONObject json = new JSONObject(result);

								if (json.get("errcode").toString().trim().equals("292")) {
									Toast.makeText(context, "�����������˳�",Toast.LENGTH_SHORT).show();
								} 
								else if (json.get("errcode").toString().trim().equals("0")) {
									
									String result3 = PostData.getData(str, URLenum.info);

									Log.v("��ȡ�����������", result3);
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
		
		//��û�вμӸûʱ����֤�Ƿ���������
		else if (mData.get(position).get("presentNumber").equals(mData.get(position).get("maxNumber"))) {
			button.setBackground(res.getDrawable(R.drawable.simple_item_full));
			button.setText("����");
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setPositiveButton("��������", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					builder.create().show();
				}
			});
		}
		
		//��δ�μӵĻ�Ĵ���
		else if (mData.get(position).get("state").toString().trim().equals("-1")) {

			button.setBackground(res.getDrawable(R.drawable.simple_item_in));
			button.setText("�μ�");

			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setPositiveButton("ȷ�ϲμ�", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							
							Toast.makeText(context, "�ѵݽ���������", Toast.LENGTH_SHORT).show();

							String str = "&act_id="	+ mData.get(position).get("activityId");
							String result = PostData.getData(str, URLenum.join);
							Log.v("�����ķ���ֵ��", result);

							try {
								JSONObject json2 = new JSONObject(result);
								if (json2.get("errcode").toString().trim().equals("0")) {
									
									String result2 = PostData.getData(str, URLenum.info);
									Log.v("��ȡ�����������", result2);
									
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
									builder.setMessage("��ǰ��������");
									builder.setTitle("��ʾ");
									builder.setPositiveButton("ȷ��",
											new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog,	int which) {
													dialog.dismiss();
												}
											});
									builder.create().show();
								}
								
								else if (json2.get("errcode").toString().trim().equals("252")){
									CustomDialog.Builder builder = new CustomDialog.Builder(context);
									builder.setMessage("��ǰ���������ʱ���ѹ�");
									builder.setTitle("��ʾ");
									builder.setPositiveButton("ȷ��",
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

		//�����������еĻ�Ĵ���
		else if (mData.get(position).get("state").toString().trim().equals("0")) {

			button.setBackground(res.getDrawable(R.drawable.simple_item_already_in));

			button.setText("������");

			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "�������룬�����ĵȺ�", Toast.LENGTH_SHORT).show();
				}
			});
		}

		//�Ա��ܾ��Ļ�Ĵ���
		else if (mData.get(position).get("state").toString().trim().equals("2")) {

			button.setBackground(res.getDrawable(R.drawable.simple_item_full));
			button.setText("���ܾ�");

			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "���ѱ��ܾ����������ټ���", Toast.LENGTH_SHORT).show();
				}
			});
		}

		//���ѹ��ڵĻ�Ĵ���
		else if (mData.get(position).get("state").toString().trim().equals("3")) {

			button.setBackground(res.getDrawable(R.drawable.simple_item_full));
			button.setText("�ѹ���");

			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "��ѹ��ڣ��������ټ���", Toast.LENGTH_SHORT).show();
				}
			});
		}

		//�Խ����еĻ�Ĵ���
		else if (mData.get(position).get("state").toString().trim().equals("5")) {

			button.setBackground(res.getDrawable(R.drawable.simple_item_full));

			button.setText("���˳�");

			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "���˳����������ټ���", Toast.LENGTH_SHORT).show();
				}
			});
		}

		//������ɻ�Ĵ���
		else if (mData.get(position).get("state").toString().trim().equals("7")) {
			button.setBackground(res.getDrawable(R.drawable.simple_item_full));
			button.setText("�����");
			
			button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "��ǰ������", Toast.LENGTH_SHORT).show();
				}
			});
			
		}

		

	}
	

}
