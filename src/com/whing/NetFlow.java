package com.whing;

import com.umeng.analytics.MobclickAgent;
import com.whing.external.ShowDialog;
import com.whing.external.URLenum;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
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

public class NetFlow extends Activity{
	
	private Button netFlowNavigationLeft;
	private WebView userNetFlowWebView;
	
	ShowDialog sd;
	ProgressDialog pd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_net_flow);
		
		init();
		
		setting();
		
		setListener();
	}

	
	private void setListener() {
		netFlowNavigationLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		netFlowNavigationLeft.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					netFlowNavigationLeft.setBackgroundResource(R.drawable.login_ensure_button_down);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					netFlowNavigationLeft.setBackgroundResource(R.drawable.navigation_back);
				}
				return false;
			}
		});
		
	}


	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	/**
	 * 初始化操作
	 */
	private void init(){
		netFlowNavigationLeft = (Button) findViewById(R.id.net_flow_navigation_left);
		userNetFlowWebView = (WebView) findViewById(R.id.user_net_flow_webview);
		
		sd = new ShowDialog(pd);
		
	}
	
	/**
	 * 嵌入的webview页面的初始化设置
	 */
	private void setting(){
		
		sd.showDialog(NetFlow.this, "正在加载");
		
		WebSettings webset = userNetFlowWebView.getSettings();
		
	    webset.setJavaScriptEnabled(true);
	    webset.setBuiltInZoomControls(true);
	    webset.supportMultipleWindows();
	    webset.setDomStorageEnabled(true);
	    webset.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
	    
	    userNetFlowWebView.clearHistory();
	    userNetFlowWebView.clearFormData();
	    userNetFlowWebView.clearCache(true);
	    
	    CookieSyncManager.createInstance(NetFlow.this);   
	    CookieSyncManager.getInstance().startSync();   
	    CookieManager.getInstance().removeSessionCookie(); 
	    
		userNetFlowWebView.setWebViewClient(new WebViewClient() {
			
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				
				view.loadUrl(url);
				return true;
				
			}

			public void onPageFinished(WebView view, String url) {
				sd.dismissDialog();
				super.onPageFinished(view, url);
			}
			
		});
		
		userNetFlowWebView.setWebChromeClient(new WebChromeClient() { 
	        @Override 
	        public void onProgressChanged(WebView view, int newProgress) { 
	            super.onProgressChanged(view, newProgress); 
	        }
	    });
		
	    userNetFlowWebView.loadUrl(URLenum.net_flow_exchange+"");
	    
	}
	
}
