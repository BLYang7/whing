package com.whing.adapter;

import java.util.List;

import com.whing.entity.ChatMsgEntity;
import com.whing.imgcut.CircleImg;
import com.whing.viewHolder.LetterChatHolder;
import com.whing.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LetterChatAdapter extends BaseAdapter{

	public static interface IMsgViewType {
		int IMVT_COM_MSG = 0;// �յ��Է�����Ϣ
		int IMVT_TO_MSG = 1;// �Լ����ͳ�ȥ����Ϣ
	}
	
	private static final int ITEMCOUNT = 2;// ��Ϣ���͵�����
	private List<ChatMsgEntity> coll;// ��Ϣ��������
	private LayoutInflater mInflater;
	private Context context;
	
	
	public LetterChatAdapter(Context context, List<ChatMsgEntity> coll) {
		this.coll = coll;
		mInflater = LayoutInflater.from(context);
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return coll.size();
	}

	@Override
	public Object getItem(int position) {
		return coll.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * �õ�Item�����ͣ��ǶԷ�����������Ϣ�������Լ����ͳ�ȥ��
	 */
	public int getItemViewType(int position) {
		ChatMsgEntity entity = coll.get(position);

		if (entity.getMsgType()) {//�յ�����Ϣ
			return IMsgViewType.IMVT_COM_MSG;
		} else {//�Լ����͵���Ϣ
			return IMsgViewType.IMVT_TO_MSG;
		}
		
	}
	
	/**
	 * Item���͵�����
	 */
	public int getViewTypeCount() {
		return ITEMCOUNT;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatMsgEntity entity = coll.get(position);
		boolean isComMsg = entity.getMsgType();

		LetterChatHolder viewHolder = null;
		if (convertView == null) {
			if (isComMsg) {
				convertView = mInflater.inflate(
						R.layout.letter_chat_msg_left, null);
			} else {
				convertView = mInflater.inflate(
						R.layout.letter_chat_msg_right, null);
			}

			viewHolder = new LetterChatHolder();
			viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
			viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
			viewHolder.userImage = (CircleImg) convertView.findViewById(R.id.iv_userhead);
			viewHolder.isComMsg = isComMsg;

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (LetterChatHolder) convertView.getTag();
		}
		
		viewHolder.tvSendTime.setText(entity.getDate());
		viewHolder.tvContent.setText(entity.getMessage());
		viewHolder.userImage.setImageDrawable(entity.getUserImage());
		
		return convertView;
	}

}
