package com.whing.imgcut;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class DownLodeImg {

	public static Bitmap downLodeImg(String string) throws IOException{
		
		URL url = new URL(string);
		
		InputStream is = url.openStream();
		Bitmap bitmap = BitmapFactory.decodeStream(is); 
				
		is.close();
		
		return bitmap;
       
	}
	
}
