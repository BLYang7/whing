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
 * @Description:�ӵײ������༭��
 * @author:	blyang 
 * @date:	2015��8��13��  09:42:00 
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
		// ���ð�ť����
		btnSend.setOnClickListener(itemsOnClick);
		
		// ����PopupEdit��View
		this.setContentView(mMenuView);
		// ����PopupEdit��������Ŀ�
		this.setWidth(LayoutParams.MATCH_PARENT);
		// ����PopupEdit��������ĸ�
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// ����PopupEdit��������ɵ��
		this.setFocusable(true);
		// ����PopupEdit�������嶯��Ч��
		this.setAnimationStyle(R.style.PopupAnimation);
		
		// ʵ����һ��ColorDrawable��ɫΪ��͸��
		ColorDrawable dw = new ColorDrawable(0x00000000);
		// ����PopupEdit��������ı���
		this.setBackgroundDrawable(dw);
		
		// mMenuView���OnTouchListener�����жϻ�ȡ����λ�������ѡ������������ٵ�����
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

