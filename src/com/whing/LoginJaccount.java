package com.whing;

/**
 * Jaccount��¼����
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
	
	private boolean flag = true;  //ҳ����ת���ƣ���ֹ�����ת
	
	String time = ""; //ʱ���������Jaccount��֤ҳ��
	
	private static final int TOKEN = 3;
	private static final int USERID = 4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_jaccount);
		
		PushAgent.getInstance(this).onAppStart();
		
		init();    //��ʼ������
		
		setting();    //webviewҳ������
		
		setListener();
		
	}
	
	/**
	 * ��������ĳ�ʼ��
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
		
		// ���ذ�ť�ĵ��Ч��
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
	 * webview����
	 * URL���룬URL��ת�����õ�
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
	    
	    sd.showDialog(LoginJaccount.this, "���ڼ���");
	    
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
									//ͨ��HttpGet��ʽ����ȡ���������ص����ݣ������û�token���û�id����Ϣ
									//����ȡ�������֮�󣬱����ڱ��أ�������ϵͳȫ�ֱ���
									HttpGet httpGet = new HttpGet(URLenum.confirm_key_login+"?time="+time);
									
									HttpResponse httpResponse;
									
										try {
											httpResponse = new DefaultHttpClient().execute(httpGet);
											if (httpResponse.getStatusLine().getStatusCode() == 200) {
												
												//result��ʾ�Ӻ�̨���ص�����
												String result = EntityUtils.toString(httpResponse.getEntity());
//												Toast.makeText(LoginJaccount.this, result, Toast.LENGTH_SHORT).show();
												
												try {
													JSONObject jsonSecret = new JSONObject(result);
													
													if(jsonSecret.getInt("errcode") == 0){
														//result2��ʾ��result���н��������
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
																setCommonVar();  //����Login�еĹ�����������
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
														Toast.makeText(LoginJaccount.this, "ѧУ��֤ʧ��", Toast.LENGTH_SHORT).show();
													}
													
													
												} catch (JSONException e1) {
													AlertDialog.Builder builder = new AlertDialog.Builder(LoginJaccount.this);
													builder.setPositiveButton("---����ע��---", new DialogInterface.OnClickListener() {
														
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
	 * ���õ�static���͵ļ��������ĸ�ֵ
	 * ���ļ�����ȡ��ͼƬ�����Ǵ�apkԴ����ȡ��ͼƬ��Ϊ�û�ͷ��
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
		
		//�ж�ͷ���ļ��Ƿ���ڣ�������ʱ��ֱ�ӽ�ͷ���ļ���ֵ��Login����userImageDrawable
		//��������ʱ���¿�һ���̣߳���ͷ���ļ��ӷ���������������Ȼ��������ֵ
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
