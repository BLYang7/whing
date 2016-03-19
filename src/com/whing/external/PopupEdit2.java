package com.whing.external;

import com.whing.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

/**
 * All rights Reserved, Designed By blyang
 * @Title: 	PopupEdit.java 
 * @Package com.whing.external
 * @Description:从底部弹出编辑框
 * @author:	blyang 
 * @date:	2015年8月13日  09:42:00 
 * @version	V1.0
 */
public class PopupEdit2 extends PopupWindow {

	public EditText edit;
	private Button btnSend;
	private View mMenuView;

	@SuppressLint("InflateParams")
	public PopupEdit2(Context context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.layout_popup_edit2, null);
		
		edit = (EditText) mMenuView.findViewById(R.id.popup_edit_text2);
		btnSend = (Button) mMenuView.findViewById(R.id.popup_edit_btn_send2);
		// 设置按钮监听
		btnSend.setOnClickListener(itemsOnClick);
		
		// 设置PopupEdit的View
		this.setContentView(mMenuView);
		// 设置PopupEdit弹出窗体的宽
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置PopupEdit弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置PopupEdit弹出窗体可点击
		this.setFocusable(true);
		// 设置PopupEdit弹出窗体动画效果
		this.setAnimationStyle(R.style.PopupAnimation);
		
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0x00000000);
		// 设置PopupEdit弹出窗体的背景
		this.setBackgroundDrawable(dw);
		
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mMenuView.setOnTouchListener(new OnTouchListener() {

			@Override
			@SuppressLint("ClickableViewAccessibility")
			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.popup_edit2).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});
	}
}

