package com.ebentum.ebentumapp;

import android.os.Bundle;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.support.v4.app.NavUtils;

public class SearchUsersActivity extends WebActivity {
	
	String userQuery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		handleIntent(getIntent());
		
		PageLayout = R.layout.activity_search_users;
		WebViewId = R.id.search_users_webview;		
		
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_user_settings);
		// Show the Up button in the action bar.
		setupActionBar();
		
		
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		getActionBar().setTitle("    Buscar usuarios");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_search_users_actions, menu);
	    
		// Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_users)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));	
        
        searchView.setIconifiedByDefault(false);
        
        searchView.setQuery(userQuery,false);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected void configureWebView(){
		if (userQuery != null)
			super.configureWebView();
	}
	

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
        loadPageUrl(); //recargar busqueda
    }
    
    // Manejar intent de busqueda
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
        	userQuery = intent.getStringExtra(SearchManager.QUERY);
        	
        	Log.d("search_users",userQuery);
 
    		pageUrl = "/users/search?q="+userQuery+"&commit=Buscar&native_app=true";
    		
    		
        }
 
    }
	
}
