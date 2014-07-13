package com.ebentum.ebentumapp;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.util.Log;

public class ProfileFragment extends WebFragment {
	
	public ProfileFragment() {
		
		String userID = AppNavigation.getUserId();
		String userName = AppNavigation.getUserName();
		
		Log.d("profile_fragment", "userID -> " + userID);
		Log.d("profile_fragment", "userName -> " + userName);
		
		pageUrl = "/users/" + userID; //53259d2e1e564d18b4000001";
	
		PageFragmentLayout = R.layout.fragment_profile;
		WebViewId = R.id.profile_webview;

	}
	
	@Override
    public void loadUrlContent(){
		if (AppNavigation.logginOut) {
			// La primera vez que se carga el contenido Main despues de el login retrasar un poco la carga para que tome bien los datos de cokies
			AppNavigation.logginOut = false;

			TimerTask task = new TimerTask() {
				
				@Override
				public void run() {
					(getActivity()).runOnUiThread(new Runnable(){
						
						@Override
						public void run(){
							String userID = AppNavigation.getUserId();
					
							pageUrl = "/users/" + userID;					
							pageWebview.loadUrl(AppNavigation.siteUrl + pageUrl);
						}
					});
				}
			};

			Timer timer = new Timer();
			timer.schedule(task, 2000);			
		}else{
			pageWebview.loadUrl(AppNavigation.siteUrl + pageUrl);
		}
		
		
    	
    }

}
