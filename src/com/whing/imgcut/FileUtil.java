package com.whing.imgcut;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.whing.security.MD5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;

public class FileUtil {

	/**
	 * 将Bitmap 图片保存到本地路径，并返回路径
	 * 
	 * @param c
	 * @param mType
	 *            资源类型，参照 MultimediaContentType 枚举，根据此类型，保存时可自动归类
	 * @param fileName
	 *            文件名称
	 * @param bitmap
	 *            图片
	 * @return
	 */
	
	private final static int START = 0;
	private final static int LAST = 8;
	
	public static String saveFile(Context c, String fileName, Bitmap bitmap) {
		return saveFile(c, "", fileName, bitmap);
	}

	public static String saveFile(Context c, String filePath, String fileName,
			Bitmap bitmap) {
		byte[] bytes = bitmapToBytes(bitmap);
		return saveFile(c, filePath, fileName, bytes);
	}

	public static byte[] bitmapToBytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}

	public static String saveFile(Context c, String filePath, String fileName,
			byte[] bytes) {
		
		fileName = MD5.md5(fileName).substring(START, LAST)+".jpg";
		
		String fileFullName = "";
		FileOutputStream fos = null;

		try {
			String suffix = "";
			
			if (filePath == null || filePath.trim().length() == 0) {
				filePath = Environment.getExternalStorageDirectory()
						+ "/Whing/" + "Image" + "/";
			}
			
			File file = new File(filePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			
			File fullFile = new File(filePath, fileName + suffix);
			fileFullName = fullFile.getPath();
			fos = new FileOutputStream(new File(filePath, fileName + suffix));
			fos.write(bytes);
			
		} catch (Exception e) {
			fileFullName = "";
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					fileFullName = "";
				}
			}
		}
		return fileFullName;
	}

	
	/**
	 * 获取本地存储的文件
	 * @param picName 文件名
	 * @return 返回Drawable类型的文件
	 * @throws IOException
	 */
	public static Drawable getLocalImage(String picName) throws IOException {
		
		picName = MD5.md5(picName).substring(START, LAST);

		String filePath = Environment.getExternalStorageDirectory() + "/Whing/"
				+ "Image" + "/";
		File file = new File(filePath, picName+".jpg");
		Bitmap bitmap = null;

		// 得到数据流
		InputStream is = new FileInputStream(file);
		
		// 解析得到图片
		bitmap = BitmapFactory.decodeStream(is);

		// 关闭数据流
		is.close();
		
		Drawable drawable = new BitmapDrawable(bitmap);
		return drawable;
	}

	
	/**
	 * 判断文件是否在本地存在
	 * @param picName 文件名
	 * @return 判断结果
	 */
	public static boolean imageExist(String picName) {
		picName = MD5.md5(picName).substring(START, LAST);
		String filePath = Environment.getExternalStorageDirectory() + "/Whing/"
				+ "Image" + "/";
		File file = new File(filePath, picName+".jpg");
		return file.exists();
	}

}
