package com.ebentum.ebentumapp;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.Window;

public class SplashScreenActivity extends Activity {

	private long splashDelay = 3000; //3 segundos

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		//getActionBar().hide();
		    
		setContentView(R.layout.activity_splash_screen);

		initApplication();
	}
	
	private void initApplication() {

		
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				Intent mainIntent;
				
				final String userId = AppNavigation.getUserId();
				
				Log.d("splashscreen","userid -> " + userId);
				
				if(userId != null && !userId.isEmpty())
					mainIntent = new Intent().setClass(SplashScreenActivity.this, MainActivity.class);
				else
					mainIntent = new Intent().setClass(SplashScreenActivity.this, LoginActivity.class);
				startActivity(mainIntent);
				finish();//Destruimos esta activity para prevenit que el usuario retorne aqui presionando el boton Atras.
			}
		};

		Timer timer = new Timer();
		timer.schedule(task, splashDelay);//Pasado los 3 segundos dispara la tarea
	}
}
