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
	 * ��Bitmap ͼƬ���浽����·����������·��
	 * 
	 * @param c
	 * @param mType
	 *            ��Դ���ͣ����� MultimediaContentType ö�٣����ݴ����ͣ�����ʱ���Զ�����
	 * @param fileName
	 *            �ļ�����
	 * @param bitmap
	 *            ͼƬ
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
	 * ��ȡ���ش洢���ļ�
	 * @param picName �ļ���
	 * @return ����Drawable���͵��ļ�
	 * @throws IOException
	 */
	public static Drawable getLocalImage(String picName) throws IOException {
		
		picName = MD5.md5(picName).substring(START, LAST);

		String filePath = Environment.getExternalStorageDirectory() + "/Whing/"
				+ "Image" + "/";
		File file = new File(filePath, picName+".jpg");
		Bitmap bitmap = null;

		// �õ�������
		InputStream is = new FileInputStream(file);
		
		// �����õ�ͼƬ
		bitmap = BitmapFactory.decodeStream(is);

		// �ر�������
		is.close();
		
		Drawable drawable = new BitmapDrawable(bitmap);
		return drawable;
	}

	
	/**
	 * �ж��ļ��Ƿ��ڱ��ش���
	 * @param picName �ļ���
	 * @return �жϽ��
	 */
	public static boolean imageExist(String picName) {
		picName = MD5.md5(picName).substring(START, LAST);
		String filePath = Environment.getExternalStorageDirectory() + "/Whing/"
				+ "Image" + "/";
		File file = new File(filePath, picName+".jpg");
		return file.exists();
	}

}
