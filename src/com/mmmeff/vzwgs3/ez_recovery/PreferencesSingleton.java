package com.mmmeff.vzwgs3.ez_recovery;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesSingleton {
	
   private static PreferencesSingleton instance = null;
   private Context context;
   
   /**preferences**/
   public SharedPreferences prefs;
   
   protected PreferencesSingleton(Context context) {
	   
	   this.context = context;
	   
	   this.prefs = context.getSharedPreferences(
			      "com.mmmeff.vzwgs3.ez_recovery", Context.MODE_PRIVATE);
   }
   
   public static PreferencesSingleton getInstance(Context context) {
      if(instance == null) {
         instance = new PreferencesSingleton(context);
      }
      return instance;
   }
}