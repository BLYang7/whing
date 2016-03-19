package com.whing.external;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;

public class ShowDialog {
	
	static ProgressDialog pd;
	
	public ShowDialog(ProgressDialog pd){
		super();
		this.pd = pd;
	}
	
	public void showDialog(Context context, String message){
		
//		if(pd == null){
			pd = ProgressDialog.show(context, null, message);
			pd.setCancelable(true);
//		}
//		else{
//			pd.show();
//		}
//		
		pd.setOnKeyListener(new OnKeyListener(){

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
					dismissDialog();
				}
				
				return false;
			}
			
		});
		
	}
	
	
	public void dismissDialog(){
		
		if(null != pd && pd.isShowing()){
			pd.dismiss();
		}
		
	}
	
	
}
