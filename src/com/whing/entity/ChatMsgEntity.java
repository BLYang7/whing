package com.whing.entity;

import android.graphics.drawable.Drawable;

/**
 * 一个消息的JavaBean
 * 
 * @author way
 * 
 */
public class ChatMsgEntity {
	private String date;//消息日期
	private String message;//消息内容
	private boolean isComMeg = true;// 是否为收到的消息
	private Drawable userImage;

	public Drawable getUserImage() {
		return userImage;
	}

	public void setUserImage(Drawable userImage) {
		this.userImage = userImage;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean getMsgType() {
		return isComMeg;
	}

	public void setMsgType(boolean isComMsg) {
		isComMeg = isComMsg;
	}

	public ChatMsgEntity() {
	}

	public ChatMsgEntity(String date, String text, Drawable userImage, boolean isComMsg) {
		super();
		this.userImage = userImage;
		this.date = date;
		this.message = text;
		this.isComMeg = isComMsg;
	}

}
