package com.ebentum.ebentumapp;

import java.net.URLDecoder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;

public class AppNavigation {
	
	//public static String siteDomain = "192.168.1.111:3000";
	public static String siteDomain = "www.ebentum.com";
	
	static final int OPEN_USER_SETTINGS_ACTION = 666;
	
	static boolean logginOut = false;
	
	// Url raiz del "backend"
	//public static String siteUrl = "http://" + siteDomain + ":3000";
	public static String siteUrl = "http://" + siteDomain;
	
	// Creo que esta clase se puede hacer una interfaz y que otras clases de actividad tengan estas funciones...
	
	public static void openMainView (Activity CurrentActivity){
		Intent myIntent = new Intent(CurrentActivity, MainActivity.class);
		//myIntent.putExtra("key", value); //Optional parameters
		CurrentActivity.startActivity(myIntent);
	}
	
	public static void openUserConfig (Activity CurrentActivity){
		Intent myIntent = new Intent(CurrentActivity, UserSettingsActivity.class);
		//myIntent.putExtra("key", value); //Optional parameters
		CurrentActivity.startActivityForResult(myIntent, OPEN_USER_SETTINGS_ACTION);
	}
	
	public static void openUserProfile (Activity CurrentActivity, String userID){
		Intent myIntent = new Intent(CurrentActivity, UserProfileActivity.class);
		myIntent.putExtra("user_id", userID); //Optional parameters
		CurrentActivity.startActivity(myIntent);
	}
	
	public static void openEventDetail (Activity CurrentActivity, String eventID){
		Intent myIntent = new Intent(CurrentActivity, EventDetailActivity.class);
		myIntent.putExtra("event_id", eventID); //Optional parameters
		CurrentActivity.startActivity(myIntent);
	}
	
	public static void openNewEvent (Activity CurrentActivity){
		Intent myIntent = new Intent(CurrentActivity, NewEventActivity.class);
		//myIntent.putExtra("key", value); //Optional parameters
		CurrentActivity.startActivity(myIntent);
	}
	
	public static void openEditEvent (Activity CurrentActivity, String eventID){
		Intent myIntent = new Intent(CurrentActivity, NewEventActivity.class);
		myIntent.putExtra("event_id", eventID);
		CurrentActivity.startActivity(myIntent);
	}	
	
	public static void logOut (Activity CurrentActivity){
		if(!logginOut){
			logginOut = true;
			Intent myIntent = new Intent(CurrentActivity, LoginActivity.class);
			myIntent.putExtra("sign_out", "true"); //Optional parameters
			CurrentActivity.startActivity(myIntent);
			CurrentActivity.finish();
		}
	}
	
	public static String getUserId (){
		String userId = AppNavigation.getCookie(AppNavigation.siteDomain,"ebentum_userid");
		return userId;
	}
	
	public static String getUserName(){
		String userName = AppNavigation.getCookie(AppNavigation.siteDomain,"ebentum_username");
		return URLDecoder.decode(userName);
	}
    
	public static String getCookie(String siteName,String CookieName){     
        String CookieValue = null;

        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);
        if(cookies != null){
	        String[] temp=cookies.split("[;]");
	        for (String ar1 : temp ){
	            if(ar1.contains(CookieName)){
	                String[] temp1=ar1.split("[=]");
	                CookieValue = temp1[1];
	            }
	        }         
        }
        return CookieValue; 
    }
    
	public static boolean processWebNavigation (Activity CurrentActivity, WebView view, String url, String fragmentName){
		
		if (url.startsWith(siteUrl)){
			
			// Es una pagina interna a la APP
				
			String pageUrl = url.replace(siteUrl + "/", "");
			String activityName = CurrentActivity.getClass().getName();
			
			Log.d("navigation", "____________");
			Log.d("navigation", "Activity -> " + activityName);
			Log.d("navigation", "Fragment -> "+ fragmentName);
			Log.d("navigation", "Url -> " + url);
			Log.d("navigation", "Page url -> " + pageUrl);
			
			// si estamos deslogeando no hacer nada. es porque saltaba la navegacion en las 
			// tres pesta–as al cerrar sesion porque ha abierto de nuevo mainactivity 
			if(AppNavigation.logginOut && !activityName.endsWith("LoginActivity"))
				return true;
			
			if (pageUrl.startsWith("users/.edit")){
				// configuracion de usuario (de momento no se navega asi)
				openUserConfig(CurrentActivity);
				
				return true;
				
			}
			if (pageUrl.startsWith("users/sign_in") || 
				pageUrl.startsWith("users/sign_out") || 
				pageUrl.startsWith("users/password") || 
				pageUrl.startsWith("users/confirmation") || 
				pageUrl.startsWith("users/sign_up") ||
				pageUrl.startsWith("users/twitter_login") ||
				pageUrl.startsWith("users/facebook_login") || 
				pageUrl.startsWith("users/auth/twitter")
				) 
			{
				
				// dejar que el webview se encargue
				
				return false;
				
			}
			if (pageUrl.startsWith("users/")){
				
				String userID = pageUrl.replace("users/", "");

				if( userID.startsWith(getUserId())) { 	
					
					
					if (activityName.endsWith("UserSettingsActivity")){
						// Volver hacia atras desde configuracion de usuario a pantalla
						// principal dejando mensaje para refrescar
						Intent returnIntent = new Intent();
						returnIntent.putExtra("refresh","true");
						CurrentActivity.setResult(CurrentActivity.RESULT_OK,returnIntent);
						CurrentActivity.finish(); // volver patras
					
						return true;
					}
					if (activityName.endsWith("MainActivity")){
						
						((MainActivity) CurrentActivity).showUserProfileTab();
						
						return true;
					}
					
					
					// el resto de casos abrir en una pantalla nueva el perfil de usuario
					openUserProfile(CurrentActivity, userID);
					
				
				}else
					openUserProfile(CurrentActivity, userID);
				
				return true;
				
			}
			if(pageUrl.startsWith("events/event_search")){
				
				((MainActivity) CurrentActivity).showExploreTab();
				
				return true;
			}
			if (pageUrl.startsWith("events/search")){
				
				//Necesario para que la pagina de busqueda cargue correctamente la –apa
				if (pageUrl.contains("?"))
					view.loadUrl(url + "&native_app=true");
				else
					view.loadUrl(url + "?native_app=true");
				
				return true;
				
			}
			
			if (pageUrl.startsWith("events/") && pageUrl.endsWith("/edit")){
				
				String eventID = pageUrl.replace("events/", "").replace("/edit", "");
				
				openEditEvent(CurrentActivity, eventID);
				
				return true;
				
			}
			
			if (pageUrl.startsWith("welcome")){
				//view.loadUrl(url + "?native_app=true");
				if (activityName.endsWith("LoginActivity")){
					// Solo la pantalla de login puede cargar esta ruta. Si no se entienede que
					// hemos llegado al welcome porque nos hemos deslogeado
					return false;
				}else{
					
					return true;	
				}
				
			}
			
			if (pageUrl != null && pageUrl.length() > 0){
				// si es "url del sitio / algo" el algo es id de evento ( pageUrl al haber quitado el siteUrl queda la id)
				openEventDetail(CurrentActivity, pageUrl);
			
			}else{
				if (activityName.endsWith("LoginActivity"))
					CurrentActivity.finish(); //cerrar login
					
				// navegar a pantalla principal
				openMainView(CurrentActivity);
				
			
			}
			
			return true;
			
		}else{
			Log.d("navigation", "____________");
			Log.d("navigation", "External -> " + url);
			
			if(url.startsWith("https://api.twitter.com") ||
				url.startsWith("https://www.facebook.com/dialog/oauth") ||
				url.startsWith("https://m.facebook.com/")
					
			){
				
				return false;
			}
			// Es una url externa abrir en el navegador
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			CurrentActivity.startActivity(i);
			
			return true;
		}
		
		
	}

}
