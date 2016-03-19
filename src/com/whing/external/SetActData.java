package com.whing.external;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.whing.R;

public class SetActData {

	/**
	 * 设置活动的返回数据
	 * @param limit 返回的活动数目
	 * @param offset 偏移量
	 * @param url 具体的目标ＵＲＬ
	 * @param kind 表示活动类型，-3 表示processing的活动，-2表示finished的活动，-1表示other，
	 *  0表示all，1表示run，2表示exercise，3表示ball，
	 * @return　返回处理好的List<Map<String, String>>值
	 * @throws JSONException
	 */
	public static List<Map<String, Object>> setData(int limit, int offset, int kind) throws JSONException{
		
		String result;
		
		if(kind == -3){
			String str = "&limit="+limit+"&offset="+offset;
			result = PostData.getData(str, URLenum.processing);
		}
		else if(kind == -2){
			String str = "&limit="+limit+"&offset="+offset;
			result = PostData.getData(str, URLenum.finished);
		}
		else if(kind == -1){
			String str = "&kind="+ kind + "&limit=" + limit + "&offset=" + offset;
			result = PostData.getData(str, URLenum.kind);
		}
		else if(kind == 0){
			String str = "&limit="+limit+"&offset="+offset;
			result = PostData.getData(str, URLenum.all);
		}
		else {
			String str = "&kind="+ kind + "&limit=" + limit + "&offset=" + offset;
			result = PostData.getData(str, URLenum.kind);
		}
		
		List<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();;
		Log.v("获取活动信息的返回值", result);
		
		JSONObject json = null;
		try {
			json = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		String[] activityId = JSArrayToStringArray(json.getJSONArray("activity_id"));
		String[] userId = JSArrayToStringArray(json.getJSONArray("user_id"));
		String[] userName = JSArrayToStringArray(json.getJSONArray("publisher_nickname"));
		String[] activityName = JSArrayToStringArray(json.getJSONArray("activity_name"));
		String[] meetPlace = JSArrayToStringArray(json.getJSONArray("place"));
		String[] meetTime = JSArrayToStringArray(json.getJSONArray("start_time"));
		String[] endTime = JSArrayToStringArray(json.getJSONArray("end_time"));
		String[] remark = JSArrayToStringArray(json.getJSONArray("remark"));
		String[] userImage = JSArrayToStringArray(json.getJSONArray("publisher_photo"));
		
		int[] userGender = JSArrayToIntArray(json.getJSONArray("publisher_sex"));
		int[] activityKind = JSArrayToIntArray(json.getJSONArray("activity_kind"));
		int[] state = JSArrayToIntArray(json.getJSONArray("status"));
		int[] maxNumber = JSArrayToIntArray(json.getJSONArray("max_number"));
		int[] presentNumber = JSArrayToIntArray(json.getJSONArray("present_number"));

		
		for (int i = 0; i < userName.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("userImage", userImage[i]);
			listItem.put("userName", userName[i]);
			listItem.put("userGender", userGender[i]);
			listItem.put("activityKind", activityKind[i]);
			listItem.put("activityName", activityName[i]);
			listItem.put("meetPlace", meetPlace[i]);
			listItem.put("meetTime", meetTime[i]);
			listItem.put("maxNumber", maxNumber[i]);
			listItem.put("presentNumber", presentNumber[i]);
			listItem.put("state", state[i]);
			listItem.put("activityId", activityId[i]);
			listItem.put("userId", userId[i]);
			listItem.put("endTime",endTime[i]);
			listItem.put("remark", remark[i]);
			temp.add(listItem);
		}
		
		return temp;
	}
	
	
	/**
	 * 将JSONarray类型转换成String[]类型
	 * @param jsonArr 目标JSONarray
	 * @return 返回String字符数组
	 * @throws JSONException
	 */
	public static String[] JSArrayToStringArray(JSONArray jsonArr)
			throws JSONException {
		
		String[] array = new String[jsonArr.length()];
		
		for(int i=0; i<jsonArr.length(); i++){
			array[i] = jsonArr.getString(i);
		}
		
		return array;
		
	}
	
	/**
	 * 将JSONArray类型转换成int[]类型
	 * @param jsonArr 目标JSONArray
	 * @return 返回int类型数组
	 * @throws JSONException
	 */
	private static int[] JSArrayToIntArray(JSONArray jsonArr)
			throws JSONException {
		
		int[] array = new int[jsonArr.length()];
		for (int i = 0; i < jsonArr.length(); i++) {
			array[i] = Integer.parseInt(jsonArr.getString(i));
		}
		return array;
	}

	
	/**
	 * 获取不同类型活动的数目
	 * @param kind -1表示other， 1表示run，2表示exercise， 3表示ball
	 * @return
	 */
	public static int actNumber(int kind){
		String str = "&kind=" + kind;
		String actNumber = PostData.getData(str, URLenum.kind_num);
		
		int ActNumber = 0;
		try {
			
			JSONObject json = new JSONObject(actNumber);
			ActNumber = Integer.parseInt(json.getString("number"));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		Log.v("return number", ActNumber+"");
		return ActNumber;
	}


	public static List<Map<String, Object>> changeHallAdapterData(
			List<Map<String, Object>> mData, int position) {
		
		List<Map<String, Object>> temp = null ;
		
		temp = mData;
		
		String str = "&act_id=" + mData.get(position).get("activityId").toString();
		String result = PostData.getData(str, URLenum.info);
		
		Log.v("获取单个活动的详情", result);
		try {
			JSONObject json = new JSONObject(result);
			
			((Map)temp.get(position)).put("userImage", json.getString("publisher_photo"));
			((Map)temp.get(position)).put("userName", json.getString("publisher_nickname"));
			((Map)temp.get(position)).put("userGender", Integer.parseInt(json.getString("publisher_sex")));
			((Map)temp.get(position)).put("activityKind", Integer.parseInt(json.getString("activity_kind")));
			((Map)temp.get(position)).put("activityName", json.getString("activity_name"));
			((Map)temp.get(position)).put("meetPlace", json.getString("place"));
			((Map)temp.get(position)).put("meetTime", json.getString("start_time"));
			((Map)temp.get(position)).put("maxNumber", Integer.parseInt(json.getString("max_number")));
			((Map)temp.get(position)).put("presentNumber", Integer.parseInt(json.getString("present_number")));
			((Map)temp.get(position)).put("activityId", json.getString("activity_id"));
			((Map)temp.get(position)).put("userId", json.getString("user_id"));
			((Map)temp.get(position)).put("endTime", json.getString("end_time"));
			((Map)temp.get(position)).put("remark", json.getString("remark"));
			((Map)temp.get(position)).put("state", Integer.parseInt(json.getString("status")));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return temp;
	}
	
	/**
	 * 设置用户评分弹框的数据
	 * @param result 从服务器得到的某个活动的参加者数据
	 * @return 处理好的mData
	 */
	public static List<HashMap<String, Object>> setRemaindData(String result, String act_id){
		List<HashMap<String, Object>> mData = new ArrayList<HashMap<String, Object>>();

		JSONObject json;
		try {
			json = new JSONObject(result);
			String[] userName = JSArrayToStringArray(json.getJSONArray("nickname"));
			String[] photo = JSArrayToStringArray(json.getJSONArray("photo"));
			String[] userId = JSArrayToStringArray(json.getJSONArray("user_id"));
			
			for(int i=0; i<photo.length; i++){
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("userName", userName[i]);
				map.put("photo", photo[i]);
				map.put("userId", userId[i]);
				map.put("actId", act_id);
				
				mData.add(map);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return mData;
		
	}
	
	
	
	/**
	 * 设置活动的返回数据
	 * @param limit 返回的活动数目
	 * @param offset 偏移量
	 * @param url 具体的目标ＵＲＬ
	 * @param kind 表示活动类型，-3 表示processing的活动，-2表示finished的活动，-1表示other，
	 *  0表示all，1表示run，2表示exercise，3表示ball，
	 * @return　返回处理好的List<Map<String, String>>值
	 * @throws JSONException
	 */
	public static List<Map<String, Object>> setDataOff(int limit, int offset, int kind) throws JSONException{
		
		String result;
		
		if(kind == -3){
			String str = "&limit="+limit+"&offset="+offset;
			result = PostData.getData(str, URLenum.processing);
		}
		else if(kind == -2){
			String str = "&limit="+limit+"&offset="+offset;
			result = PostData.getData(str, URLenum.finished);
		}
		else if(kind == -1){
			String str = "&kind="+ kind + "&limit=" + limit + "&offset=" + offset;
			result = PostData.getData(str, URLenum.kind);
		}
		else if(kind == 0){
			String str = "&limit="+limit+"&offset="+offset;
			result = PostData.getData(str, URLenum.all);
		}
		else {
			String str = "&kind="+ kind + "&limit=" + limit + "&offset=" + offset;
			result = PostData.getData(str, URLenum.kind);
		}
		
		List<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();;
		Log.v("获取活动信息的返回值", result);
		
		JSONObject json = null;
		try {
			json = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		String[] activityId = JSArrayToStringArray(json.getJSONArray("activity_id"));
		String[] userId = JSArrayToStringArray(json.getJSONArray("user_id"));
		String[] userName = JSArrayToStringArray(json.getJSONArray("publisher_nickname"));
		String[] activityName = JSArrayToStringArray(json.getJSONArray("activity_name"));
		String[] meetPlace = JSArrayToStringArray(json.getJSONArray("place"));
		String[] meetTime = JSArrayToStringArray(json.getJSONArray("start_time"));
		String[] endTime = JSArrayToStringArray(json.getJSONArray("end_time"));
		String[] remark = JSArrayToStringArray(json.getJSONArray("remark"));
		String[] userImage = JSArrayToStringArray(json.getJSONArray("publisher_photo"));
		
		int[] userGender = JSArrayToIntArray(json.getJSONArray("publisher_sex"));
		int[] activityKind = JSArrayToIntArray(json.getJSONArray("activity_kind"));
		int[] state = JSArrayToIntArray(json.getJSONArray("status"));
		int[] maxNumber = JSArrayToIntArray(json.getJSONArray("max_number"));
		int[] presentNumber = JSArrayToIntArray(json.getJSONArray("present_number"));

		
		for (int i = 0; i < userName.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("userImage", userImage[i]);
			listItem.put("userName", userName[i]);
			listItem.put("userGender", userGender[i]);
			listItem.put("activityKind", activityKind[i]);
			listItem.put("activityName", activityName[i]);
			listItem.put("meetPlace", meetPlace[i]);
			listItem.put("meetTime", meetTime[i]);
			listItem.put("maxNumber", maxNumber[i]);
			listItem.put("presentNumber", presentNumber[i]);
			listItem.put("state", 7);
			listItem.put("activityId", activityId[i]);
			listItem.put("userId", userId[i]);
			listItem.put("endTime",endTime[i]);
			listItem.put("remark", remark[i]);
			temp.add(listItem);
		}
		
		return temp;
	}
	
}
