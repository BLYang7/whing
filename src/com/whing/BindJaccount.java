package com.whing;

/**
 * 绑定Jaccount账号的操作
 * 载入学校的绑定Jaccount账号的页面
 * 当完成绑定之后，跳转到注册页面，当前页面结束
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
	
	private static boolean flag = true;  //标记值，用户跳转唯一性,放置finishpage中跳转的多次调用
	
	private String timeStamp = "";  //时间戳字符串，用户注册信息唯一性
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.register_jaccount);
		
		init();    //初始化设置
		
		setting();    //用户的webview设置以及URL载入
		
		setListener();
		
	}
	
	//resume方法
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	//onpause方法
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
		
		
		// 返回按钮的点击效果
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
	
	
	//初始化操作
	private void init(){
		
		registerJaccountButtonLeft = (Button) findViewById(R.id.register_jaccount_navigation_left);
		jaccountWeb = (WebView) findViewById(R.id.webview_jaccount);
		
		sd = new ShowDialog(pd);
		
		//时间戳的赋值，用于传递给后台，保证注册的唯一性
		//实际上这样做是不准确的,可能会带来错误
		timeStamp = GetTime.getTime();
		
		PushAgent.getInstance(this).onAppStart();
	}
	
	/**
	 * webview的设置，包括初始载入的URL， 对后续URL跳转的处理等
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
	    
	    Toast.makeText(this, "请先绑定Jaccount账号", Toast.LENGTH_SHORT).show();
	    
	    sd.showDialog(BindJaccount.this, "正在加载");
	    
		jaccountWeb.setWebViewClient(new WebViewClient() {
			
			//URL跳转的时候，在当前webview中实现跳转，防止转到浏览器中实现
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			//页面载入完毕的一些操作
			public void onPageFinished(WebView view, String url) {
				
				sd.dismissDialog();

				//当结束页面的URL与我们指定的页面相匹配时，作相应的处理
				if (url.trim().equals( (URLenum.bind_jaccount_final+"?time="+timeStamp).trim() ) && flag ) {

					flag = false;
					
//					Toast.makeText(BindJaccount.this, "绑定完成，请继续完成注册", Toast.LENGTH_LONG).show();
					
					AlertDialog.Builder builder = new AlertDialog.Builder(BindJaccount.this);
					builder.setPositiveButton("绑定成功，请继续完成注册", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							Map<String, String> map = new HashMap<String, String>();
							map.put("time", timeStamp);
							String result = PostData.postData(map, URLenum.get_account);
							
							try {
								JSONObject json = new JSONObject(result);
								
								//跳转到注册页面，并结束当前页面
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
//						//跳转到注册页面，并结束当前页面
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
	    
		//初始载入的URL
	    jaccountWeb.loadUrl(URLenum.register_jaccount.toString()+"[w]time="+timeStamp);
	    
	}
	
}
