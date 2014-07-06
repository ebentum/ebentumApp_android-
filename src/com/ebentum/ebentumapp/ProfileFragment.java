package com.ebentum.ebentumapp;

public class ProfileFragment extends WebFragment {
	
	public ProfileFragment() {
		
		String userID = AppNavigation.getUserId();
		
		pageUrl = "/users/" + userID; //53259d2e1e564d18b4000001";
	
		PageFragmentLayout = R.layout.fragment_profile;
		WebViewId = R.id.profile_webview;

	}
	
}
