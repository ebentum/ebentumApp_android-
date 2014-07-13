package com.ebentum.ebentumapp;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
 

public class WebFragment extends Fragment {
	
	String pageUrl;
	
	int PageFragmentLayout;
	int WebViewId;
	
	WebView pageWebview;
	
	boolean isOffline;
	
	String currentFailingUrl;
	
	private ValueCallback<Uri> mUploadMessage;
	
	private final static int FILECHOOSER_RESULTCODE=1;  
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
    	showLoadingDialog(true);
    	
        View rootView = inflater.inflate(PageFragmentLayout, container, false);
     
        pageWebview = (WebView) rootView.findViewById(WebViewId);
        
        pageWebview.setBackgroundColor(Color.parseColor("#dfdedc"));

        configureWebViewSettings();

        configureChromeClient();
        
        //configureWebViewTouch();
        
        pageWebview.setWebViewClient(new customWebViewClient());
        
        loadUrlContent();
       
        return rootView;
    }
    
    public void loadUrlContent(){
    	pageWebview.loadUrl(AppNavigation.siteUrl + pageUrl + "?native_app=true");
    }
    
    private void configureWebViewTouch(){
    	pageWebview.setOnTouchListener(new View.OnTouchListener() {
    		 public final static int FINGER_RELEASED = 0;
    	        public final static int FINGER_TOUCHED = 1;
    	        public final static int FINGER_DRAGGING = 2;
    	        public final static int FINGER_UNDEFINED = 3;

    	        private int fingerState = FINGER_RELEASED;


    	        @Override
    	        public boolean onTouch(View view, MotionEvent motionEvent) {

    	            switch (motionEvent.getAction()) {

    	                case MotionEvent.ACTION_DOWN:
    	                    if (fingerState == FINGER_RELEASED) fingerState = FINGER_TOUCHED;
    	                    else fingerState = FINGER_UNDEFINED;
    	                    break;

    	                case MotionEvent.ACTION_UP:
    	                    if(fingerState != FINGER_DRAGGING) {
    	                        fingerState = FINGER_RELEASED;

    	                        // Your onClick codes
    	                        view.playSoundEffect(SoundEffectConstants.CLICK);

    	                    }
    	                    else if (fingerState == FINGER_DRAGGING) fingerState = FINGER_RELEASED;
    	                    else fingerState = FINGER_UNDEFINED;
    	                    break;

    	                case MotionEvent.ACTION_MOVE:
    	                    if (fingerState == FINGER_TOUCHED || fingerState == FINGER_DRAGGING) fingerState = FINGER_DRAGGING;
    	                    else fingerState = FINGER_UNDEFINED;
    	                    break;

    	                default:
    	                    fingerState = FINGER_UNDEFINED;

    	            }

    	            return false;
    	        }
    	});
    
    }
    
    @SuppressLint({ "JavascriptInterface", "NewApi" })
	private void configureWebViewSettings(){
        
    	WebSettings webSettings = pageWebview.getSettings();
        
        final EbentumJavascriptInterface ebentumJSIface = new EbentumJavascriptInterface(getActivity());
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

        		  mUploadMessage = uploadMsg;  
	              Intent i = new Intent(Intent.ACTION_GET_CONTENT);  
	              i.addCategory(Intent.CATEGORY_OPENABLE);  
	              i.setType("image/*");  
	              WebFragment.this.startActivityForResult(Intent.createChooser(i,"File Chooser"), FILECHOOSER_RESULTCODE);  

             }

        	 // For Android 3.0+
             public void openFileChooser( ValueCallback uploadMsg, String acceptType ) {
	             mUploadMessage = uploadMsg;
	             Intent i = new Intent(Intent.ACTION_GET_CONTENT);
	             i.addCategory(Intent.CATEGORY_OPENABLE);
	             i.setType("*/*");
	             WebFragment.this.startActivityForResult(
											             Intent.createChooser(i, "File Browser"),
											             FILECHOOSER_RESULTCODE);
             }

             //For Android 4.1
             public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
                 mUploadMessage = uploadMsg;  
                 Intent i = new Intent(Intent.ACTION_GET_CONTENT);  
                 i.addCategory(Intent.CATEGORY_OPENABLE);  
                 i.setType("image/*");  
                 WebFragment.this.startActivityForResult( Intent.createChooser( i, "File Chooser" ), FILECHOOSER_RESULTCODE );

             }
             

        });
    }
    
    @Override
	public void onResume() {
        super.onResume();
        
        loadFailingUrlAgain();

    }
    
    public void loadFailingUrlAgain(){
    	if(isOffline){
    		pageWebview.loadUrl(currentFailingUrl);
        }
    }
    
    //flipscreen not loading again
    @Override
    public void onConfigurationChanged(Configuration newConfig){        
        super.onConfigurationChanged(newConfig);
    }  
    
    private class customWebViewClient extends WebViewClient {
    	
    	
    	
    	@Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
        {
            // Handle the error
    		pageWebview.loadUrl("file:///android_asset/offline.html");
    		isOffline = true;
    		((MainActivity) getActivity()).showLoadingDialog(false);
    		currentFailingUrl = failingUrl;
        }
    
    	
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
             
            isOffline = false;
            return AppNavigation.processWebNavigation(getActivity(), view, url, WebFragment.this.getClass().getName());
        }
 
    } 
    
	public void showLoadingDialog(boolean show){
		
		((MainActivity) getActivity()).showLoadingDialog(show);
	}
	
	public void closeSession(){
		// la unica forma de cerrar sesion para el caso de nuestra app es ejecutar un
		// javascript. por lo tanto tendra que hacerse en la pesta–a de ese momento
		Log.d("navigation", "webfragment closeSession " + this.getClass().getName());
		pageWebview.loadUrl("javascript:ebentumApp.closeSession()");
	}
    
    private class EbentumJavascriptInterface {
    	  Context mContext;
    	  	
    	     EbentumJavascriptInterface(Context c) {
    	         mContext = c;
    	     }
    	     @JavascriptInterface
    	     public void loadFailingUrl(){
    	    	 pageWebview.loadUrl(AppNavigation.siteUrl + pageUrl + "?native_app=true");
    	    	 
    	     }
    	     
    	     @JavascriptInterface
    	     public void showLoadingDialog(){
    	    	 ((MainActivity) mContext).showLoadingDialog(true);
    	     }
    	     
    	     @JavascriptInterface
    	     public void hideLoadingDialog(){
    	    	 ((MainActivity) mContext).showLoadingDialog(false);
    	    	 
    	     }
      }
    
    
}