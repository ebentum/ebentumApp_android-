package com.ebentum.ebentumapp;

import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.SearchView;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	ProgressDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initLoadingDialog();
		setContentView(R.layout.activity_main);

		getActionBar().setTitle("");   
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		Intent intent = getIntent();
		
		if(intent!=null){
			if(intent.getAction() == Intent.ACTION_VIEW){
				AppNavigation.processWebNavigation(this,null, AppNavigation.siteUrl + intent.getData().getPath(), null) ;
				
			}
		}
		
		//AppNavigation.logginOut = false;
		
	}
	
	private void initLoadingDialog(){
		
		loadingDialog = new ProgressDialog(this);
	        
		loadingDialog.setCancelable(false);
		loadingDialog.setMessage(getString(R.string.app_loading));
		//loadingDialog.show();
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_actions, menu);
	    
		// Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_users)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));	
		
		
		return super.onCreateOptionsMenu(menu);
	}

    //flipscreen not loading again
    @Override
    public void onConfigurationChanged(Configuration newConfig){        
        super.onConfigurationChanged(newConfig);
    }  
   
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
        	case R.id.action_share_event:
        		AppNavigation.openNewEvent(MainActivity.this);
        		return true;
            case R.id.action_refresh:
            	refreshContent();
                return true;
            case R.id.action_settings:
            	AppNavigation.openUserConfig(MainActivity.this);
                return true;
            case R.id.action_close_session:
            	
            	FragmentManager fm = getSupportFragmentManager();

                Fragment fr = fm.findFragmentByTag("android:switcher:" + mViewPager.getId() + ":" + 1 ); 
            	((WebFragment) fr).closeSession();
            	AppNavigation.logOut(this);
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    	  if (requestCode == AppNavigation.OPEN_USER_SETTINGS_ACTION) {

    	     if(resultCode == RESULT_OK){      
    	         String result=data.getStringExtra("refresh");    
    	         if(result != null){
    	        	 refreshContent();
    	        	      	        	 
    	        	 mViewPager.setCurrentItem(0,true);
    	         }
    	     }
    	     if (resultCode == RESULT_CANCELED) {    
    	         //Write your code if there's no result
    	     }
    	  }
    }//onActivityResult
    
	public void refreshContent(){
		// Actualizar pesta–as
		
		mViewPager.getAdapter().notifyDataSetChanged();
	}

	public void showActivityTab(){
		mViewPager.setCurrentItem(0,true);
	}
	
	
	public void showExploreTab(){
		mViewPager.setCurrentItem(1,true);
	}
    	
	public void showUserProfileTab(){
		mViewPager.setCurrentItem(2,true);
	}
	
	
    
	@Override
	protected void onResume() {

		super.onResume();
		
		invalidateOptionsMenu(); // para que al volver de pantalla de busqueda no siga mostrando el cuadro de busqueda
		
	}

	
	public void showLoadingDialog(boolean show){
		
		if(show){
			loadingDialog.show();
		}else{
			loadingDialog.dismiss();
		}
	}


	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			switch (position) {
			case 0:
				return new ActivitiesFragment();
			case 1:
				return new ExploreFragment();
			case 2:
				return new ProfileFragment();
			}
			return null;
			
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}
		
		// Sobrecargado para hacer que se pueda refrescar el contenido
		@Override
		public int getItemPosition(Object object) {
		   return POSITION_NONE;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1);
			case 1:
				return getString(R.string.title_section2);
			case 2:
				//return getString(R.string.title_section3);
				Log.d("main_activity", "get username ...");
				return AppNavigation.getUserName();
			}
			return null;
		}
	}

}
