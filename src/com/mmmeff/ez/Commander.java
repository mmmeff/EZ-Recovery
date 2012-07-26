package com.mmmeff.ez;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
	private void ExecSingle(String command){
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
	private String ExecSingleWithResults(String command){
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
	
	private boolean ExecMulti(String[] commands){
		try {
			for (String command : commands){
				input.writeBytes(command + "\n");
				input.flush();
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
			return false;
		}
	}
	
	private String[] ExecMultiWithResults(String[] commands){
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

	public boolean FlashRecovery(String title) {
		String path = "/sdcard/gs3ezrecovery/";
		//due to java not supporting string switches until 1.7
		//and android requiring java 1.5 or 1.6, please forgive
		//this long and ugly if else chain :/
		if (title.equals("CWM Touch 5.8.4.9")){
			path += "recovery_rec_CWMT5849";
		} else if (title.equals("CWM 6.0.1.0")){
			path += "recovery_rec_CWM6010";
		} else if (title.equals("TWRP 2.2.0")){
			path += "recovery_rec_twrp220";
		} else if (title.equals("Invisblek v2 Kernel")){
			path += "recovery_hyb_invisiblekv2.img";
		} else if (title.equals("Stock")){
			path += "recovery_rec_stock";
		}
		
		path += ".img";
		
		String[] commands = { "dd if=" + path + " of=/dev/block/mmcblk0p18\n", "exit\n" };
		return ExecMulti(commands);
	}

	public boolean FlashCustomRecovery(String pathToRecovery) {
		String[] commands = { "dd if=" + pathToRecovery + " of=/dev/block/mmcblk0p18\n", "exit\n" };
		return ExecMulti(commands);
	}

	public void rebootRecovery() {
		ExecSingle("reboot recovery");
	}
}
