package com.mmmeff.vzwgs3.ez_recovery;

import java.io.DataOutputStream;
import java.io.IOException;

import android.util.Log;

/** serves as an interface for running console commands in the android os **/
public class Commander {
	
	/** logcat tag **/
	private static final String TAG = "ez_recovery";
	
	private Process process;
	private DataOutputStream os;

	public Commander(){
		try {
			//create a process thread and ask for root permissions
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}
	}
}
