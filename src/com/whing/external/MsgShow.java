package com.whing.external;

import java.util.List;
import java.util.Map;

public class MsgShow {

	
	/**
	 * ��ϵͳ֪ͨ���ο�����ʾ���ַ�
	 * @param mData
	 * @param position
	 * @return
	 */
	public static String msgShow(List<Map<String, Object>> mData, int position){
		
		String msg = "";
		
		String type = (String) mData.get(position).get("type");
		int typeNum = Integer.parseInt(type.trim());
		
		String activity_name = (String) mData.get(position).get("activity_name");
		String sender_nickname = (String) mData.get(position).get("sender_nickname");
		
		switch(typeNum){
		
		case 100:{
			msg = "���μӵĻ [" +activity_name + "] ������ʼ";
			break;
		}
		
		case 101:{
			msg = "���μӵĻ [" + activity_name +"] ȡ����";
			break;
		}
		
		case 110:{
			msg = "���μӵĻ [" + activity_name +"] �г�Ա�˳�";
			break;
		}
		
		case 111:{
			msg = "���Ѿ������ܲμӻ [" + activity_name +"]";
			break;
		}
		
		case 112:{
			msg = "��Ǹ�������ܾ��μӻ [" + activity_name +"]";
			break;
		}
		
		case 113:{
			msg = "�������Ļ [" + activity_name +"] ���³�Ա����";
			break;
		}

		case 150:{
			msg = "�����µļ���������Ҫ����";
			break;
		}
		
		case 200:{
			msg = sender_nickname + " �ظ�����";
			break;
		}
		
		case 210:{
			msg = sender_nickname + " �ڻ [" + activity_name + "] �лظ�����";
			break;
		}
		
		case 300:{
			msg = "�������� " + sender_nickname + " �������µĻ [" + activity_name +"]";
			break;
		}
		
		case 400:{
			msg = sender_nickname + "��˽�Ÿ���";
			break;
		}
		
		default:{
			msg = "��֪���ơ�����Ҳͦ���ε�";
			break;
		}
		
		}
		
		return msg;
	}
	
	
	/**
	 * ��������չʾ����ϸ����Ϣ
	 * @param mData
	 * @param position
	 * @return
	 */
	public static String msgShowMore(List<Map<String, Object>> mData, int position){
		
		String msg = "";
		
		String type = (String) mData.get(position).get("type");
		int typeNum = Integer.parseInt(type.trim());
		
		String activity_name = (String) mData.get(position).get("activity_name");
		String sender_nickname = (String) mData.get(position).get("sender_nickname");
		
		switch(typeNum){
		
		case 100:{
			msg = "���μӵĻ [" +activity_name + "] ������ʼ";
			break;
		}
		
		case 101:{
			msg = "���μӵĻ [" + activity_name +"] ȡ����";
			break;
		}
		
		case 110:{
			msg = "���μӵĻ [" + activity_name +"] �г�Ա�˳�";
			break;
		}
		
		case 111:{
			msg = "���Ѿ������ܲμӻ [" + activity_name +"]���밴ʱ�μӻ";
			break;
		}
		
		case 112:{
			msg = "��Ǹ�������ܾ��μӻ [" + activity_name +"]�� �����߾ܾ�����������";
			break;
		}
		
		case 113:{
			msg = "�������Ļ [" + activity_name +"] ���³�Ա" + sender_nickname + "����";
			break;
		}

		case 150:{
			msg = sender_nickname + " ��������������Ļ ["+ activity_name +"]";
			break;
		}
		
		case 200:{
			msg = sender_nickname + " �ظ�����";
			break;
		}
		
		case 210:{
			msg = sender_nickname + " �ڻ [" + activity_name + "] �лظ�����";
			break;
		}
		
		case 300:{
			msg = "�������� " + sender_nickname + " �������µĻ [" + activity_name +"]";
			break;
		}
		
		case 400:{
			msg = sender_nickname + "��˽�Ÿ���";
			break;
		}
		
		default:{
			msg = "��֪���ơ�����Ҳͦ���ε�";
			break;
		}
		
		}
		
		return msg;
	}
	
	
}
