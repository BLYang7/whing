package com.whing.external;

import java.util.List;
import java.util.Map;

public class MsgShow {

	
	/**
	 * 在系统通知条形框中显示的字符
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
			msg = "您参加的活动 [" +activity_name + "] 即将开始";
			break;
		}
		
		case 101:{
			msg = "您参加的活动 [" + activity_name +"] 取消了";
			break;
		}
		
		case 110:{
			msg = "您参加的活动 [" + activity_name +"] 有成员退出";
			break;
		}
		
		case 111:{
			msg = "您已经被接受参加活动 [" + activity_name +"]";
			break;
		}
		
		case 112:{
			msg = "抱歉，您被拒绝参加活动 [" + activity_name +"]";
			break;
		}
		
		case 113:{
			msg = "您创建的活动 [" + activity_name +"] 有新成员加入";
			break;
		}

		case 150:{
			msg = "您有新的加入请求需要处理";
			break;
		}
		
		case 200:{
			msg = sender_nickname + " 回复了您";
			break;
		}
		
		case 210:{
			msg = sender_nickname + " 在活动 [" + activity_name + "] 中回复了您";
			break;
		}
		
		case 300:{
			msg = "您的朋友 " + sender_nickname + " 创建了新的活动 [" + activity_name +"]";
			break;
		}
		
		case 400:{
			msg = sender_nickname + "有私信给您";
			break;
		}
		
		default:{
			msg = "不知所云。。我也挺无奈的";
			break;
		}
		
		}
		
		return msg;
	}
	
	
	/**
	 * 弹出框中展示的详细的信息
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
			msg = "您参加的活动 [" +activity_name + "] 即将开始";
			break;
		}
		
		case 101:{
			msg = "您参加的活动 [" + activity_name +"] 取消了";
			break;
		}
		
		case 110:{
			msg = "您参加的活动 [" + activity_name +"] 有成员退出";
			break;
		}
		
		case 111:{
			msg = "您已经被接受参加活动 [" + activity_name +"]，请按时参加活动";
			break;
		}
		
		case 112:{
			msg = "抱歉，您被拒绝参加活动 [" + activity_name +"]， 发起者拒绝了您的请求";
			break;
		}
		
		case 113:{
			msg = "您创建的活动 [" + activity_name +"] 有新成员" + sender_nickname + "加入";
			break;
		}

		case 150:{
			msg = sender_nickname + " 申请加入您创建的活动 ["+ activity_name +"]";
			break;
		}
		
		case 200:{
			msg = sender_nickname + " 回复了您";
			break;
		}
		
		case 210:{
			msg = sender_nickname + " 在活动 [" + activity_name + "] 中回复了您";
			break;
		}
		
		case 300:{
			msg = "您的朋友 " + sender_nickname + " 创建了新的活动 [" + activity_name +"]";
			break;
		}
		
		case 400:{
			msg = sender_nickname + "有私信给您";
			break;
		}
		
		default:{
			msg = "不知所云。。我也挺无奈的";
			break;
		}
		
		}
		
		return msg;
	}
	
	
}
