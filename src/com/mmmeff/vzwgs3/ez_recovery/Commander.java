package com.mmmeff.vzwgs3.ez_recovery;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.util.Log;

/** serves as an interface for running console commands in the android os **/
public class Commander {
	
	/** logcat tag **/
	private static final String TAG = "ez_recovery";
	
	private Process process;
	private DataOutputStream input;
	private DataInputStream output;

	public Commander(){
		try {
			//create a process thread and ask for root permissions
			process = Runtime.getRuntime().exec("su");
			input = new DataOutputStream(process.getOutputStream());
			output = new DataInputStream(process.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}
	}
	
	/**
	 * Run a single command while disregarding any output given
	 * @param command
	 */
	public void ExecSingle(String command){
		try {
			input.writeBytes(command + "\n");
			input.flush();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}
	}
	
	/**
	 * Run a single command and return it's output
	 * @param command
	 * @return command output
	 */
	public String ExecSingleWithResults(String command){
		try {
			input.writeBytes(command + "\n");
			input.flush();
			return output.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
			return "{FAILURE}";
		}
	}
	
	public void ExecMulti(String[] commands){
		try {
			for (String command : commands){
				input.writeBytes(command + "\n");
				input.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}
	}
	
	public String[] ExecMultiWithResults(String[] commands){
		String[] res = new String[commands.length];
		try {
			for(int i = 0; i < commands.length; i++){
				input.writeBytes(commands[i] + "\n");
				input.flush();
				res[i] = output.readLine();
			}
			return res;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
			return null;
		}
	}
}
