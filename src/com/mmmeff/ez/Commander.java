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
	private Context context;

	public Commander(Context context){
		Initialize();
		this.context = context;
	}
	
	private void Initialize(){
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
	
	private void Kill(){
		try {
			input.writeBytes("exit\n");
			input.flush();
		} catch (IOException e) {
			Log.e(TAG, "KILLED AN ALREADY DEAD PROCESS");
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
	
	private boolean ExecMulti(String[] commands){
		try {
			for (String command : commands){
				input.writeBytes(command + "\n");
				input.flush();
			}
			input.writeBytes("exit\n");
			input.flush();
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG, e.getMessage());
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
			return false;
		}
	}
	
	public boolean FlashRecovery(String title) {
		Kill();
		Initialize();
		String path = FileMan.ASSET_LOCATION + "/";
		//due to java not supporting string switches until 1.7
		//and android requiring java 1.5 or 1.6, please forgive
		//this long and ugly if else chain :/
		String device = PreferencesSingleton.getInstance(context).prefs.getString("device", null);
		//if
		if (title.equals("CWM Touch 5.8.4.9")){
			path += "recovery_rec_CWMT5849.img";
		} else if (title.equals("CWM 6.0.1.0")){
			path += "recovery_rec_CWM6010.img";
		} else if (title.equals("TWRP 2.2.0")){
			path += "recovery_rec_twrp220.img";
		} else if (title.equals("Invisblek v2 Kernel")){
			path += "recovery_hyb_invisiblekv2.img";
		} else if (title.equals("Stock")){
			path += "recovery_rec_stock.img";
		}
		
		
		String[] commands = { "dd if=" + path + " of=/dev/block/mmcblk0p18 bs=256k"};
		return ExecMulti(commands);
	}

	public boolean FlashCustomRecovery(String pathToRecovery) {
		Kill();
		Initialize();
		String[] commands = { "dd if=" + pathToRecovery + " of=/dev/block/mmcblk0p18 bs=256k"};
		return ExecMulti(commands);
	}

	public void rebootRecovery() {
		Initialize();
		ExecSingle("reboot recovery");
	}
}
