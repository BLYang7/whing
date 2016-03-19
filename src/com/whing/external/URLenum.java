package com.whing.external;

import java.net.MalformedURLException;
import java.net.URL;

public class URLenum {

	static String PATHuser = "111";
	static String PATHact = "111";
	static String PATHmsg = "111";
	static String PATHreview = "111";
	static String PATHnotice = "111";
	
	public static String PATHactdetail = "111/";

	public static final URL up_token = URLmake("111");
	public static final URL img_url = URLmake("111");
	public static final URL register_jaccount = URLmake("111");
	
	public static final URL baidu = URLmake("https://www.baidu.com");
	
	public static final URL bind_jaccount_final = URLmake("111");
	public static final URL login_jaccount = URLmake("111");
	public static final URL login_jaccount_final = URLmake("111");
	public static final URL net_flow_exchange = URLmake("111");
	
	
	public static final URL login = URLuser("login");
	public static final URL register = URLuser("register");
	public static final URL check_account = URLuser("check_account");
	public static final URL get_info = URLuser("get_info");
	public static final URL update_info = URLuser("update_info");
	public static final URL update_password = URLuser("update_password");
	public static final URL update_photo = URLuser("update_photo");
	public static final URL get_follow = URLuser("get_follow");
	public static final URL follow = URLuser("follow");
	public static final URL unfollow = URLuser("unfollow");
	public static final URL get_confirm_key = URLuser("get_confirm_key");
	public static final URL relation = URLuser("relation");
	public static final URL confirm_key_login = URLuser("confirm_key_login");
	public static final URL get_account = URLuser("get_account");
	
	public static final URL release = URLact("release");
	public static final URL all = URLact("all");
	public static final URL join = URLact("join");
	public static final URL quit = URLact("quit");
	public static final URL processing = URLact("processing");
	public static final URL finished = URLact("finished");
	public static final URL kind = URLact("kind");
	public static final URL kind_num = URLact("kind_num");
	public static final URL info = URLact("info");
	public static final URL get_unesti_activity = URLact("get_unesti_activity");    //获取未评论的活动
	public static final URL ref_estimation = URLact("ref_estimation");    //拒绝评论
	public static final URL sub_estimation = URLact("sub_estimation");    //提交评论
	public static final URL participant = URLact("participant");    //获取参加活动的人
	
	public static final URL messaged_user = URLmsg("messaged_user");
	public static final URL send = URLmsg("send");
	public static final URL get_message = URLmsg("get_message");
	
	public static final URL get_review = URLreview("get_review");
	public static final URL release_review = URLreview("release");
	
	public static final URL read_notice = URLnotice("read_notice");
	public static final URL unread_notice = URLnotice("unread_notice");
	public static final URL read = URLnotice("read");
	public static final URL agree = URLnotice("agree");
	public static final URL refuse = URLnotice("refuse");
	public static final URL get_notice_num = URLnotice("get_notice_num");
	
	
	private static URL URLuser(String string) {
		
		try {
			return new URL(PATHuser + string);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	private static URL URLact(String string) {
		
		try {
			return new URL(PATHact + string);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static URL URLmsg(String string) {
		
		try {
			return new URL(PATHmsg + string);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	private static URL URLreview(String string) {
		
		try {
			return new URL(PATHreview + string);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static URL URLnotice(String string){
		
		try {
			return new URL(PATHnotice + string);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static URL URLmake(String string){
		
		try {
			return new URL(string);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
}
