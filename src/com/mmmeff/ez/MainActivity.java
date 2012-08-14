package com.mmmeff.ez;

import java.util.ArrayList;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	private ViewPager mViewPager;
	private EditText pathField;
	private FileMan fileman;
	private ProgressDialog progressdialog;
	final private Context context = this;

	final private boolean debug_prefs = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		if (debug_prefs)
			PreferencesSingleton.getInstance(context).prefs.edit().clear()
					.commit();
		super.onCreate(savedInstanceState);

		setTitle(getString(R.string.title_activity_main));
		setContentView(R.layout.activity_main);
		final MyPagerAdapter adapter = new MyPagerAdapter(this);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(adapter);
		mViewPager.setCurrentItem(0);

		pathField = (EditText) findViewById(R.id.pathField);

		// set up files
		fileman = new FileMan(this);

		progressdialog = ProgressDialog.show(this, "Installing files...",
				"for your health", true, false);

		new Thread() {

			public void run() {

				try {
					fileman.Initialize();
				} catch (Exception e) {
					Log.e("tag", e.getMessage());
				}
				progressdialog.dismiss();

			}
		}.start();

		// multi-device dialog
		boolean showDialog = true;
		final Dialog deviceDialog = new Dialog(this);
		deviceDialog.setContentView(R.layout.device_dialog);
		deviceDialog.setTitle(R.string.devicedialogtitle);
		deviceDialog.setCanceledOnTouchOutside(false);
		deviceDialog.setCancelable(false);
		final RadioGroup carrierRadioGroup = (RadioGroup) deviceDialog
				.findViewById(R.id.carrierRadioGroup);

		String device = PreferencesSingleton.getInstance(context).prefs
				.getString("device", null);
		if (device != null) {
			showDialog = false;
			if (device.equals("att"))
				carrierRadioGroup.check(R.id.att_carrier_radio);
			else if (device.equals("spr"))
				carrierRadioGroup.check(R.id.spr_carrier_radio);
			else if (device.equals("tmo"))
				carrierRadioGroup.check(R.id.tmo_carrier_radio);
			else if (device.equals("vzw"))
				carrierRadioGroup.check(R.id.vzw_carrier_radio);
		}

		Button dialogSaveButton = (Button) deviceDialog
				.findViewById(R.id.device_save_button);
		dialogSaveButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				switch (carrierRadioGroup.getCheckedRadioButtonId()) {
				case (R.id.att_carrier_radio):
					PreferencesSingleton.getInstance(context).prefs.edit()
							.putString("device", "att").commit();
					deviceDialog.dismiss();
					break;
				case (R.id.spr_carrier_radio):
					PreferencesSingleton.getInstance(context).prefs.edit()
							.putString("device", "spr").commit();
					deviceDialog.dismiss();
					break;
				case (R.id.tmo_carrier_radio):
					PreferencesSingleton.getInstance(context).prefs.edit()
							.putString("device", "tmo").commit();
					deviceDialog.dismiss();
					break;
				case (R.id.vzw_carrier_radio):
					PreferencesSingleton.getInstance(context).prefs.edit()
							.putString("device", "vzw").commit();
					deviceDialog.dismiss();
					break;
				default:
					Toast.makeText(context, "please make a selection",
							Toast.LENGTH_SHORT).show();
					break;
				}
				adapter.InitSpinners();
			}

		});
		if (showDialog)
			deviceDialog.show();

		ArrayList<Recovery> recoveries = fileman
				.GetRecoveries(PreferencesSingleton.getInstance(context).prefs
						.getString("device", "vzw"));

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		String extraData = data.getStringExtra("path");
		pathField.setText(extraData);
	}

}
