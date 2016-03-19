package com.whing;

/**
 * ��Jaccount�˺ŵĲ���
 * ����ѧУ�İ�Jaccount�˺ŵ�ҳ��
 * ����ɰ�֮����ת��ע��ҳ�棬��ǰҳ�����
 */


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.HttpGet;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.whing.external.LocalInf;
import com.whing.external.PostData;
import com.whing.external.ShowDialog;
import com.whing.external.URLenum;
import com.whing.security.GetTime;
import com.whing.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class BindJaccount extends Activity{
	
	private Button registerJaccountButtonLeft;
	private WebView jaccountWeb;
	
	private ProgressDialog pd;
	private ShowDialog sd;
	
	private static boolean flag = true;  //���ֵ���û���תΨһ��,����finishpage����ת�Ķ�ε���
	
	private String timeStamp = "";  //ʱ����ַ������û�ע����ϢΨһ��
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.register_jaccount);
		
		init();    //��ʼ������
		
		setting();    //�û���webview�����Լ�URL����
		
		setListener();
		
	}
	
	//resume����
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	//onpause����
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	
	private void setListener(){
		
		registerJaccountButtonLeft.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		
		// ���ذ�ť�ĵ��Ч��
		registerJaccountButtonLeft.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					registerJaccountButtonLeft.setBackgroundResource(R.drawable.login_ensure_button_down);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					registerJaccountButtonLeft.setBackgroundResource(R.drawable.navigation_back);
				}
				return false;
			}
		});
	}
	
	
	//��ʼ������
	private void init(){
		
		registerJaccountButtonLeft = (Button) findViewById(R.id.register_jaccount_navigation_left);
		jaccountWeb = (WebView) findViewById(R.id.webview_jaccount);
		
		sd = new ShowDialog(pd);
		
		//ʱ����ĸ�ֵ�����ڴ��ݸ���̨����֤ע���Ψһ��
		//ʵ�����������ǲ�׼ȷ��,���ܻ��������
		timeStamp = GetTime.getTime();
		
		PushAgent.getInstance(this).onAppStart();
	}
	
	/**
	 * webview�����ã�������ʼ�����URL�� �Ժ���URL��ת�Ĵ����
	 */
	private void setting(){
		
		WebSettings webset = jaccountWeb.getSettings();  
		
	    webset.setJavaScriptEnabled(true);     
	    webset.setBuiltInZoomControls(true);
	    webset.supportMultipleWindows();
	    webset.setDomStorageEnabled(true);
	    webset.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
	    
	    jaccountWeb.clearHistory();
	    jaccountWeb.clearFormData();
	    jaccountWeb.clearCache(true);
	    
	    CookieSyncManager.createInstance(BindJaccount.this);   
	    CookieSyncManager.getInstance().startSync();   
	    CookieManager.getInstance().removeSessionCookie(); 
	    
	    Toast.makeText(this, "���Ȱ�Jaccount�˺�", Toast.LENGTH_SHORT).show();
	    
	    sd.showDialog(BindJaccount.this, "���ڼ���");
	    
		jaccountWeb.setWebViewClient(new WebViewClient() {
			
			//URL��ת��ʱ���ڵ�ǰwebview��ʵ����ת����ֹת���������ʵ��
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			//ҳ��������ϵ�һЩ����
			public void onPageFinished(WebView view, String url) {
				
				sd.dismissDialog();

				//������ҳ���URL������ָ����ҳ����ƥ��ʱ������Ӧ�Ĵ���
				if (url.trim().equals( (URLenum.bind_jaccount_final+"?time="+timeStamp).trim() ) && flag ) {

					flag = false;
					
//					Toast.makeText(BindJaccount.this, "����ɣ���������ע��", Toast.LENGTH_LONG).show();
					
					AlertDialog.Builder builder = new AlertDialog.Builder(BindJaccount.this);
					builder.setPositiveButton("�󶨳ɹ�����������ע��", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							Map<String, String> map = new HashMap<String, String>();
							map.put("time", timeStamp);
							String result = PostData.postData(map, URLenum.get_account);
							
							try {
								JSONObject json = new JSONObject(result);
								
								//��ת��ע��ҳ�棬��������ǰҳ��
								Intent intent = new Intent(BindJaccount.this,Register.class);
								intent.putExtra("time", timeStamp);
								intent.putExtra("account", json.getString("account"));
								startActivity(intent);
								finish();
								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
					});
					
					builder.create().show();
					
					
//					Map<String, String> map = new HashMap<String, String>();
//					map.put("time", timeStamp);
//					String result = PostData.postData(map, URLenum.get_account);
//					
//					try {
//						JSONObject json = new JSONObject(result);
//						
//						//��ת��ע��ҳ�棬��������ǰҳ��
//						Intent intent = new Intent(BindJaccount.this,Register.class);
//						intent.putExtra("time", timeStamp);
//						intent.putExtra("account", json.getString("account"));
//						startActivity(intent);
//						finish();
//						
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}

				}

				super.onPageFinished(view, url);

			}
			
		});
		
		jaccountWeb.setWebChromeClient(new WebChromeClient() { 
	            @Override 
	            public void onProgressChanged(WebView view, int newProgress) { 
	                super.onProgressChanged(view, newProgress); 
	            }
	        }); 
	    
		//��ʼ�����URL
	    jaccountWeb.loadUrl(URLenum.register_jaccount.toString()+"[w]time="+timeStamp);
	    
	}
	
}
