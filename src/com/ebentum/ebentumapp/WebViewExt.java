package com.ebentum.ebentumapp;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.webkit.WebView;

public class WebViewExt extends WebView {

	public WebViewExt(Context context) {
		super(context);
	}
	
	public WebViewExt(Context context, AttributeSet attrs) {
		super(context,attrs);
	}
	
	public WebViewExt(Context context, AttributeSet attrs, int defStyleAttr){
		super(context,attrs,defStyleAttr);
	}
	
//	  public boolean onTouch(View v, MotionEvent event) {
//
//		  Log.d("WebViewExt", "touch...");
//		  
//	         if (v.getId() == WebViewExt.this.getId() && event.getAction() == MotionEvent.ACTION_DOWN){
//
//	                // write your code here
//	        	 	v.playSoundEffect(SoundEffectConstants.CLICK);
//
//	            }
//
//
//	        return false;
//	    }
}
