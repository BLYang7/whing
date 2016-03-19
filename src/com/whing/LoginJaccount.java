package com.whing;

/**
 * Jaccount登录过程
 * 
 */

import java.io.File;
import java.io.IOException;






import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.HttpGet;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.whing.R;
import com.whing.external.LocalInf;
import com.whing.external.PostData;
import com.whing.external.ShowDialog;
import com.whing.external.URLenum;
import com.whing.imgcut.DownLodeImg;
import com.whing.imgcut.FileUtil;
import com.whing.security.GetTime;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
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

public class LoginJaccount extends Activity{
	
	Button loginJaccountNavigationLeft;
	WebView loginJaccountWebView ;
	
	ShowDialog sd;
	ProgressDialog pd;
	
	private boolean flag = true;  //页面跳转控制，防止多次跳转
	
	String time = ""; //时间戳，用于Jaccount验证页面
	
	private static final int TOKEN = 3;
	private static final int USERID = 4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_jaccount);
		
		PushAgent.getInstance(this).onAppStart();
		
		init();    //初始化操作
		
		setting();    //webview页面设置
		
		setListener();
		
	}
	
	/**
	 * 各个组件的初始化
	 */
	private void init(){
		
		loginJaccountNavigationLeft = (Button) findViewById(R.id.login_jaccount_navigation_left);
		loginJaccountWebView = (WebView) findViewById(R.id.login_jaccount_webview);
		
		sd = new ShowDialog(pd);
		PushAgent.getInstance(this).onAppStart();
		
		time = GetTime.getTime();
	}
	
	private void setListener(){
		loginJaccountNavigationLeft.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		// 返回按钮的点击效果
		loginJaccountNavigationLeft.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					loginJaccountNavigationLeft.setBackgroundResource(R.drawable.login_ensure_button_down);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					loginJaccountNavigationLeft.setBackgroundResource(R.drawable.navigation_back);
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
	 * webview设置
	 * URL载入，URL跳转的设置等
	 */
	private void setting(){
		
		WebSettings webset = loginJaccountWebView.getSettings();
		
	    webset.setJavaScriptEnabled(true);
	    webset.setBuiltInZoomControls(true);
	    webset.supportMultipleWindows();
	    webset.setDomStorageEnabled(true);
	    webset.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
	    
	    loginJaccountWebView.clearHistory();
	    loginJaccountWebView.clearFormData();
	    loginJaccountWebView.clearCache(true);
	    
	    CookieSyncManager.createInstance(LoginJaccount.this);   
	    CookieSyncManager.getInstance().startSync();   
	    CookieManager.getInstance().removeSessionCookie(); 
	    
	    sd.showDialog(LoginJaccount.this, "正在加载");
	    
		loginJaccountWebView.setWebViewClient(new WebViewClient() {
			
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				
				view.loadUrl(url);
				
				return true;
			}

			public void onPageFinished(WebView view, String url) {
				
				sd.dismissDialog();

				if (url.trim().equals( (URLenum.login_jaccount_final)+"?time="+time )  && flag) {
					
					flag = false;
					
					Hall.executorService.submit(new Runnable(){

						@Override
						public void run() {
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
							Hall.handler.post(new Runnable(){

								@Override
								public void run() {
									//通过HttpGet方式来获取服务器返回的数据，包括用户token，用户id等信息
									//当获取数据完成之后，保存在本地，并设置系统全局变量
									HttpGet httpGet = new HttpGet(URLenum.confirm_key_login+"?time="+time);
									
									HttpResponse httpResponse;
									
										try {
											httpResponse = new DefaultHttpClient().execute(httpGet);
											if (httpResponse.getStatusLine().getStatusCode() == 200) {
												
												//result表示从后台返回的数据
												String result = EntityUtils.toString(httpResponse.getEntity());
//												Toast.makeText(LoginJaccount.this, result, Toast.LENGTH_SHORT).show();
												
												try {
													JSONObject jsonSecret = new JSONObject(result);
													
													if(jsonSecret.getInt("errcode") == 0){
														//result2表示对result进行解码的数据
														String result2 = PostData.decryptInf(result);
														
														JSONObject json;
														try {
															json = new JSONObject(result2);
															
															if(LocalInf.saveData(
																	LoginJaccount.this,
																	json.getString("token"), 
																	3)
																	
																	&&
																	
																	LocalInf.saveData(
																			LoginJaccount.this,
																			json.getString("user_id"),
																			4)
																	)
															{
																setCommonVar();  //设置Login中的公共变量内容
																Intent intent = new Intent(LoginJaccount.this, Hall.class);
																startActivity(intent);
															    finish();
																return;
															}
								
														} catch (JSONException e) {
															e.printStackTrace();
														}
														
													}
													
													else if(jsonSecret.getInt("errcode") == 102){
														Toast.makeText(LoginJaccount.this, "学校认证失败", Toast.LENGTH_SHORT).show();
													}
													
													
												} catch (JSONException e1) {
													AlertDialog.Builder builder = new AlertDialog.Builder(LoginJaccount.this);
													builder.setPositiveButton("---请先注册---", new DialogInterface.OnClickListener() {
														
														@Override
														public void onClick(DialogInterface dialog, int which) {
															
															Intent intent = new Intent(LoginJaccount.this, BindJaccount.class);
															startActivity(intent);
															finish();
															
														}
													});
													
													builder.create().show();
												}
												
												
												
											}
										} catch (ClientProtocolException e) {
											e.printStackTrace();
										} catch (IOException e) {
											e.printStackTrace();
										}
								}
							});
							
						}
						
					});
					
					
						
				}

				super.onPageFinished(view, url);
			}
		});
		
		loginJaccountWebView.setWebChromeClient(new WebChromeClient() { 
	        @Override 
	        public void onProgressChanged(WebView view, int newProgress) { 
	            super.onProgressChanged(view, newProgress); 
	        }
	    });
		
	    loginJaccountWebView.loadUrl(URLenum.login_jaccount+"?time="+time);
	    
	}
	
	/**
	 * 公用的static类型的几个变量的赋值
	 * 从文件夹中取出图片或者是从apk源码中取出图片作为用户头像
	 */
	private void setCommonVar(){
		
		Login.token = LocalInf.getLocalInf(this, TOKEN);
		Login.user_id = LocalInf.getLocalInf(this, USERID);
		
		final String str = "&"+ "uid=" + Login.user_id;
		final String personInf = PostData.getData(str, URLenum.get_info);
		JSONObject jsonPersonInf;
		try {
			jsonPersonInf = new JSONObject(personInf);
			Login.userName = jsonPersonInf.getString("account");
			Login.nickName = jsonPersonInf.getString("nickname");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		//判断头像文件是否存在，当存在时，直接将头像文件赋值给Login常量userImageDrawable
		//当不存在时，新开一个线程，将头像文件从服务器下载下来，然后再作赋值
		if(FileUtil.imageExist(Login.user_id)){
			
			try {
				Login.userImageDrawable = FileUtil.getLocalImage(Login.user_id);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		else{
			
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_photo);
			Login.userImageDrawable = new BitmapDrawable(bitmap);
			
			Hall.executorService.submit(new Runnable(){

				@Override
				public void run() {
					
					String result = PostData.getData("", URLenum.get_info);
					
					try {
						JSONObject json = new JSONObject(result);
						Bitmap bitmap = DownLodeImg.downLodeImg(json.get("photo").toString());
						FileUtil.saveFile(LoginJaccount.this, Login.user_id, bitmap);
						
						Hall.handler.post(new Runnable(){

							@Override
							public void run() {
								
								try {
									Login.userImageDrawable = FileUtil.getLocalImage(Login.user_id);
									Hall.hallUserImageNavigationRight.setImageDrawable(Login.userImageDrawable);
									Hall.menuLeftUserLogo.setImageDrawable(Login.userImageDrawable);
									
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		
		Login.prfPassword = new File("/data/data/" + getPackageName().toString()
				+ "/shared_prefs", "file_password.xml");;
		Login.prfToken = new File("/data/data/" + getPackageName().toString()
				+ "/shared_prefs", "file_token.xml");
		Login.prfUserId = new File("/data/data/" + getPackageName().toString()
				+ "/shared_prefs", "file_userId.xml");
		
		Login.imageFilePath = Environment.getExternalStorageDirectory() + "/Whing/"
				+ "Image" + "/";
	}
	
	
}
