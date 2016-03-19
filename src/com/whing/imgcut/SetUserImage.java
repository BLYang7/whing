package com.whing.imgcut;

import java.io.IOException;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.whing.Login;
import com.whing.R;

public class SetUserImage {

	/**
	 * ��ȡ�û�ͷ��Ĳ���
	 * @param context ��ǰ����context
	 * @param res ��ǰ�������Դ��
	 * @param userId �û���Id��Ҳ��ͷ���ļ����ļ���
	 * @param userImage �û���ͷ���url
	 * @return ����drawable���͵�����
	 */
	public static Drawable setImage(final Context context,Resources res, final String userId, final String userImage){
		
		Drawable drawable = null;
		
		//����������û���ͷ��ȡ����
		if(FileUtil.imageExist(userId)){
			try {
				drawable = FileUtil.getLocalImage(userId);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//���userImageΪ�գ���ͷ������ΪĬ�ϵ�ͷ��
		else if(userImage.equals("") || userImage.equals("null") || userImage.equals(null)){
			
			Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.default_photo);
			drawable = new BitmapDrawable(bitmap);
			
		}
		
		//����û���id�ͱ��ص�user_id��ͬ�����ز�����
		else if(userId.equals(Login.user_id)){
			
			Bitmap bitmap;
			try {
				bitmap = DownLodeImg.downLodeImg(userImage);
				FileUtil.saveFile(context, userId, bitmap);
				drawable = new BitmapDrawable(bitmap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//�����࣬����ΪĬ��ͷ��
		else{
			
			Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.default_photo);
			drawable = new BitmapDrawable(bitmap);
			
			//�������̣߳������û�ͷ������
			new Thread (new Runnable(){
				@Override
				public void run() {
					try {
						Bitmap bitmap = DownLodeImg.downLodeImg(userImage);
						FileUtil.saveFile(context, userId, bitmap);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
			
		}
		
		return drawable;
		
	}
	
}
