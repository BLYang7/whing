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
	 * 获取用户头像的操作
	 * @param context 当前界面context
	 * @param res 当前界面的资源池
	 * @param userId 用户的Id，也是头像文件的文件名
	 * @param userImage 用户的头像的url
	 * @return 返回drawable类型的数据
	 */
	public static Drawable setImage(final Context context,Resources res, final String userId, final String userImage){
		
		Drawable drawable = null;
		
		//如果本地有用户的头像，取出来
		if(FileUtil.imageExist(userId)){
			try {
				drawable = FileUtil.getLocalImage(userId);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//如果userImage为空，把头像设置为默认的头像
		else if(userImage.equals("") || userImage.equals("null") || userImage.equals(null)){
			
			Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.default_photo);
			drawable = new BitmapDrawable(bitmap);
			
		}
		
		//如果用户的id和本地的user_id相同，下载并设置
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
		
		//其它类，设置为默认头像
		else{
			
			Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.default_photo);
			drawable = new BitmapDrawable(bitmap);
			
			//启动新线程，下载用户头像数据
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
