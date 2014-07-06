package com.ebentum.ebentumapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;

public class WebActivity extends Activity {
	
	String pageUrl;
	
	int PageLayout;
	int WebViewId;
	
	WebView pageWebview;
	
	boolean isOffline;
	boolean selectingImage;
	
	String currentFailingUrl;
	
	ProgressDialog loadingDialog;
	
	protected ValueCallback<Uri> mUploadMessage;
	
	private final static int FILECHOOSER_RESULTCODE=1;  
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	
    	initLoadingDialog();
    	
    	setContentView(PageLayout);
        
    	configureWebView();
       
	}
	
    private void initLoadingDialog(){
		
		loadingDialog = new ProgressDialog(this);
	        
		loadingDialog.setCancelable(false);
		loadingDialog.setMessage(getString(R.string.app_loading));
		loadingDialog.show();
		
	}
    
	public void showLoadingDialog(boolean show){
		
		if(show){
			loadingDialog.show();
		}else{
			loadingDialog.dismiss();
		}
	}
	
    protected void configureWebView(){
		 pageWebview = (WebView) findViewById(WebViewId);
	     
	     pageWebview.setBackgroundColor(Color.parseColor("#dfdedc"));
	
	     configureWebViewSettings();
	
	     configureChromeClient();
	     
	     pageWebview.setWebViewClient(new customWebViewClient());
	
	     loadPageUrl();
    }
    
    protected void loadPageUrl(){
    	
    	pageWebview.loadUrl(AppNavigation.siteUrl + pageUrl + "?native_app=true");
    }
    
    @SuppressLint({ "JavascriptInterface", "NewApi" })
	private void configureWebViewSettings(){
        
    	WebSettings webSettings = pageWebview.getSettings();
        
        final EbentumJavascriptInterface ebentumJSIface = new EbentumJavascriptInterface(this);
        pageWebview.addJavascriptInterface(ebentumJSIface, "EbentumAppNative");
    	
        webSettings.setJavaScriptEnabled(true); //Enables Javascript. Remove this line if your site doesn't require javascript
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setPluginState(PluginState.ON); //Enables plugins like Adobe flash. Remove if not required
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        if (Build.VERSION.SDK_INT <= 18) {
        	webSettings.setSavePassword(false);
        } else {
            // do nothing. because as google mentioned in the documentation -
            // "Saving passwords in WebView will not be supported in future versions"
        }
    }
    
    private void configureChromeClient(){
    	pageWebview.setWebChromeClient(new WebChromeClient() {
        	 @Override
			 public void onGeolocationPermissionsShowPrompt(String origin, android.webkit.GeolocationPermissions.Callback callback) {
        	    callback.invoke(origin, true, false);
        	 }
        	 
             //The undocumented magic method override  
             //Eclipse will swear at you if you try to put @Override here  
        	 // For Android 3.0+
        	 public void openFileChooser(ValueCallback<Uri> uploadMsg) {  
        		  selectingImage = true;
        		  mUploadMessage = uploadMsg;  
	              Intent i = new Intent(Intent.ACTION_GET_CONTENT);  
	              i.addCategory(Intent.CATEGORY_OPENABLE);  
	              i.setType("image/*");  
	              WebActivity.this.startActivityForResult(Intent.createChooser(i,"File Chooser"), FILECHOOSER_RESULTCODE);  

             }

        	 // For Android 3.0+
             public void openFileChooser( ValueCallback uploadMsg, String acceptType ) {
            	 selectingImage = true;
	             mUploadMessage = uploadMsg;
	             Intent i = new Intent(Intent.ACTION_GET_CONTENT);
	             i.addCategory(Intent.CATEGORY_OPENABLE);
	             i.setType("*/*");
	             WebActivity.this.startActivityForResult(
											             Intent.createChooser(i, "File Browser"),
											             FILECHOOSER_RESULTCODE);
             }

             //For Android 4.1
             public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
            	 selectingImage = true;
                 mUploadMessage = uploadMsg;  
                 Intent i = new Intent(Intent.ACTION_GET_CONTENT);  
                 i.addCategory(Intent.CATEGORY_OPENABLE);  
                 i.setType("image/*");  
                 WebActivity.this.startActivityForResult( Intent.createChooser( i, "File Chooser" ), FILECHOOSER_RESULTCODE );

             }
             

        });
    }
    
    @Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {  

		  if (requestCode == FILECHOOSER_RESULTCODE) {

		        if (mUploadMessage == null)
		            return;

		        Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
		        
		        if (result!=null){

		            String filePath = null;

		            if ("content".equals(result.getScheme())) {

		                Cursor cursor = this.getContentResolver().query(result, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
		                    cursor.moveToFirst();   
		                    filePath = cursor.getString(0);
		                    cursor.close();

		            } else {

		                filePath = result.getPath();

		            }
		           		            
		            if (filePath != null){
		            	Log.d("onactiviyresult","uno");
		            	Uri myUri = Uri.parse(filePath);
		            	mUploadMessage.onReceiveValue(myUri);
		            }else{
		            	Log.d("onactiviyresult","dos");
		            	mUploadMessage.onReceiveValue(result);
		            }

		        } else {

		        	mUploadMessage.onReceiveValue(result);
		        	
		        }
		        
		        mUploadMessage = null;
		    }
	
	}      
    @Override
    public void onBackPressed(){

        if (pageWebview.canGoBack()) {
        	pageWebview.goBack();
            return;
        }
        super.onBackPressed();      
    }
    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(keyCode)
            {
            case KeyEvent.KEYCODE_BACK:
                if(pageWebview.canGoBack()){
                	initLoadingDialog();
                	pageWebview.goBack();
                }
                else{
                	return super.onKeyDown(keyCode, event);
                }
            }

        }
        return super.onKeyDown(keyCode, event);
    }    
    
    @Override
    public boolean onKeyDown_(int keyCode, KeyEvent event) {
 
            if (keyCode == KeyEvent.KEYCODE_BACK){
                if(myWebView.canGoBack()){
                    myWebView.goBack();
                    return true;
                }
            }
 
            return super.onKeyDown(keyCode, event);
    } 
    */
    
    //flipscreen not loading again
    @Override
    public void onConfigurationChanged(Configuration newConfig){        
        super.onConfigurationChanged(newConfig);
    }  
    
    @Override
    protected void onResume() {
        super.onResume();
        
        loadFailingUrlAgain();
        
        selectingImage = false;

    }
    
    public void loadFailingUrlAgain(){
    	if(isOffline){
    		pageWebview.loadUrl(currentFailingUrl);
        }
    }
    
    private class customWebViewClient extends WebViewClient {
    	@Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
        {
            // Handle the error
    		pageWebview.loadUrl("file:///android_asset/offline.html");
    		isOffline = true;
    		WebActivity.this.showLoadingDialog(false);
    		currentFailingUrl = failingUrl;
        }
    
    	
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
 
            //view.loadUrl(url + "?native_app=true");
        	
            isOffline = false;
            return AppNavigation.processWebNavigation(WebActivity.this, view, url, "");
        }
        
        public void onPageFinished(WebView view, String url) {
        	pageWebview.loadUrl("javascript:ebentumApp.setNativeCookie()");
        	
        	// si a los 15 segs no se ha ocultado el dialogo de cargar es que algo puede haber ido mal
        	final Handler handler = new Handler();
        	handler.postDelayed(new Runnable() {
        	    @Override
        	    public void run() {
        	    	WebActivity.this.showLoadingDialog(false);
        	    }
        	}, 15000);        	
        }
 
    } 
    
    private class EbentumJavascriptInterface {
  	  Context mContext;
  	  	
  	     EbentumJavascriptInterface(Context c) {
  	         mContext = c;
  	     }
  	     @JavascriptInterface
  	     public void loadFailingUrl(){
  	    	 WebActivity.this.loadPageUrl();
  	    	 
  	     }
  	     
  	     @JavascriptInterface
  	     public void showLoadingDialog(){
  	    	WebActivity.this.showLoadingDialog(true);
  	    	 
  	     }
  	     
  	     @JavascriptInterface
  	     public void hideLoadingDialog(){
  	    	WebActivity.this.showLoadingDialog(false);
  	    	 
  	     }
    }
}
