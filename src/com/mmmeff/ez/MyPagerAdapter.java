package com.mmmeff.ez;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class MyPagerAdapter extends PagerAdapter {

	protected static final String TAG = "ezrecovery-MyPagerAdapter";

	private Commander commander;

	private Context context;
	private Spinner recovery_recoverySpinner, recovery_hybridSpinner;
	private ArrayAdapter<CharSequence> recovery_recoveryAdapter,
			recovery_hybridAdapter;
	private Button browseButton, flashButton, rebootButton;
	private EditText customRecoveryPathField;
	private RadioGroup recoveryRadioGroup;

	public MyPagerAdapter(Context context) {
		this.context = context;
		this.commander = new Commander();
	}

	public int getCount() {
		return 3;
	}

	public Object instantiateItem(View collection, int position) {

		View view = null;
		LayoutInflater inflater = (LayoutInflater) collection.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		int resId = 0;
		switch (position) {
		case 0:
			resId = R.layout.about_layout;
			view = inflater.inflate(resId, null);
			break;
		case 1:
			resId = R.layout.recovery_layout;
			view = inflater.inflate(resId, null);

			// load components
			customRecoveryPathField = (EditText) view
					.findViewById(R.id.pathField);
			recoveryRadioGroup = (RadioGroup) view
					.findViewById(R.id.radioGroup);

			// load radio buttons

			// load buttons
			browseButton = (Button) view.findViewById(R.id.browseButton);
			flashButton = (Button) view.findViewById(R.id.flashButton);
			rebootButton = (Button) view.findViewById(R.id.rebootButton);

			// set up recovery choices in spinners
			recovery_recoverySpinner = (Spinner) view
					.findViewById(R.id.recovery_recovery_spinner);
			recovery_hybridSpinner = (Spinner) view
					.findViewById(R.id.recovery_hybrid_spinner);
			InitSpinners(view);

			// configure button listeners
			OnClickListener flashListener = new OnClickListener() {
				public void onClick(View v) {
					boolean success;
					int selection = recoveryRadioGroup
							.getCheckedRadioButtonId();
					switch (selection) {
					case R.id.recovery_radioButtonCustom:
						success = commander
								.FlashCustomRecovery(customRecoveryPathField
										.getText().toString());
						PreferencesSingleton.getInstance(context).prefs
								.edit()
								.putInt("last_flash",
										R.id.recovery_radioButtonCustom)
								.commit();
						PreferencesSingleton.getInstance(context).prefs
								.edit()
								.putString(
										"last_custom",
										customRecoveryPathField.getText()
												.toString()).commit();
						break;
					case R.id.recovery_radioButtonRecovery:
						success = commander
								.FlashRecovery((String) recovery_recoverySpinner
										.getSelectedItem());
						PreferencesSingleton.getInstance(context).prefs
								.edit()
								.putInt("last_flash",
										R.id.recovery_radioButtonRecovery)
								.commit();
						PreferencesSingleton.getInstance(context).prefs
								.edit()
								.putInt("last_recovery",
										recovery_recoverySpinner
												.getSelectedItemPosition())
								.commit();
						break;
					case R.id.recovery_radioButtonHybrid:
						success = commander
								.FlashRecovery((String) recovery_hybridSpinner
										.getSelectedItem());
						PreferencesSingleton.getInstance(context).prefs
								.edit()
								.putInt("last_flash",
										R.id.recovery_radioButtonHybrid)
								.commit();
						PreferencesSingleton.getInstance(context).prefs
								.edit()
								.putInt("last_hybrid",
										recovery_hybridSpinner
												.getSelectedItemPosition())
								.commit();
						break;
					case R.id.recovery_radioButtonStock:
						success = commander.FlashRecovery("Stock");
						PreferencesSingleton.getInstance(context).prefs
								.edit()
								.putInt("last_flash",
										R.id.recovery_radioButtonStock)
								.commit();
						break;
					default:
						Toast toast = Toast.makeText(context,
								"Please make a selection.", Toast.LENGTH_SHORT);
						toast.show();
						success = false;
						break;
					}
					if (!success) {
						Toast toast = Toast.makeText(context,
								"Recovery Flash Failed.", Toast.LENGTH_SHORT);
						toast.show();
					} else {
						Toast toast = Toast.makeText(context,
								"Recovery Flash Successful!",
								Toast.LENGTH_SHORT);
						toast.show();
					}
				}
			};
			flashButton.setOnClickListener(flashListener);

			OnClickListener rebootListener = new OnClickListener() {
				public void onClick(View v) {
					DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case DialogInterface.BUTTON_POSITIVE:
								Log.d("tag", "Rebooting into recovery.");
								commander.rebootRecovery();
								break;

							case DialogInterface.BUTTON_NEGATIVE:
								// No button clicked
								break;
							}
						}
					};
					AlertDialog.Builder builder = new AlertDialog.Builder(
							context);
					builder.setMessage(
							"Are you sure you want to reboot into recovery?")
							.setPositiveButton("Yes", dialogClickListener)
							.setNegativeButton("No", dialogClickListener)
							.show();
				}
			};
			rebootButton.setOnClickListener(rebootListener);

			OnClickListener browseListener = new OnClickListener() {
				public void onClick(View v) {
					FileDialog fd = new FileDialog(context);
					fd.setListener(new FileDialog.ActionListener() {
						public void userAction(int action, String filePath) {
							// Test if user select a file
							if (action == FileDialog.ACTION_SELECTED_FILE) {
								customRecoveryPathField.setText(filePath);
							}

							// flashRecovery(false, filePath);
						}
					});
					fd.selectFile();
				}
			};
			browseButton.setOnClickListener(browseListener);

			// load preferences
			int last_flash = PreferencesSingleton.getInstance(context).prefs
					.getInt("last_flash", 0);
			if (last_flash != 0) {
				recoveryRadioGroup.check(last_flash);
			} else {
				recoveryRadioGroup.check(R.id.recovery_radioButtonStock);
			}

			String last_custom = PreferencesSingleton.getInstance(context).prefs
					.getString("last_custom", null);
			if (last_custom != null) {
				customRecoveryPathField.setText(last_custom);
			}

			int last_recovery = PreferencesSingleton.getInstance(context).prefs
					.getInt("last_recovery", -1);
			if (last_recovery != -1) {
				recovery_recoverySpinner.setSelection(last_recovery);
			}

			int last_hybrid = PreferencesSingleton.getInstance(context).prefs
					.getInt("last_hybrid", -1);
			if (last_hybrid != -1) {
				recovery_hybridSpinner.setSelection(last_hybrid);
			}

			PreferencesSingleton.getInstance(context).prefs.edit().commit();
			break;
		case 2:
			resId = R.layout.ezrom_layout;
			view = inflater.inflate(resId, null);
			break;

		}

		((ViewPager) collection).addView(view, 0);

		return view;
	}

	private void InitSpinners(View view) {

		String device = PreferencesSingleton.getInstance(context).prefs
				.getString("device", null);
		if (device == null){
			return;
		}
		if (device.equals("vzw")) {
			recovery_recoveryAdapter = ArrayAdapter.createFromResource(
					view.getContext(),
					R.array.recovery_vzw_recovery_spinner_array,
					android.R.layout.simple_spinner_item);
			recovery_recoveryAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			recovery_recoverySpinner.setAdapter(recovery_recoveryAdapter);

			recovery_hybridAdapter = ArrayAdapter.createFromResource(
					view.getContext(),
					R.array.recovery_vzw_hybrid_spinner_array,
					android.R.layout.simple_spinner_item);
			recovery_hybridAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			recovery_hybridSpinner.setAdapter(recovery_hybridAdapter);
		} else if (device.equals("att")) {
			recovery_recoveryAdapter = ArrayAdapter.createFromResource(
					view.getContext(),
					R.array.recovery_att_recovery_spinner_array,
					android.R.layout.simple_spinner_item);
			recovery_recoveryAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			recovery_recoverySpinner.setAdapter(recovery_recoveryAdapter);

			recovery_hybridAdapter = ArrayAdapter.createFromResource(
					view.getContext(),
					R.array.recovery_att_hybrid_spinner_array,
					android.R.layout.simple_spinner_item);
			recovery_hybridAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			recovery_hybridSpinner.setAdapter(recovery_hybridAdapter);
		} else if (device.equals("spr")) {
			recovery_recoveryAdapter = ArrayAdapter.createFromResource(
					view.getContext(),
					R.array.recovery_spr_recovery_spinner_array,
					android.R.layout.simple_spinner_item);
			recovery_recoveryAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			recovery_recoverySpinner.setAdapter(recovery_recoveryAdapter);

			recovery_hybridAdapter = ArrayAdapter.createFromResource(
					view.getContext(),
					R.array.recovery_spr_hybrid_spinner_array,
					android.R.layout.simple_spinner_item);
			recovery_hybridAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			recovery_hybridSpinner.setAdapter(recovery_hybridAdapter);
		} else if (device.equals("tmo")) {
			recovery_recoveryAdapter = ArrayAdapter.createFromResource(
					view.getContext(),
					R.array.recovery_tmo_recovery_spinner_array,
					android.R.layout.simple_spinner_item);
			recovery_recoveryAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			recovery_recoverySpinner.setAdapter(recovery_recoveryAdapter);

			recovery_hybridAdapter = ArrayAdapter.createFromResource(
					view.getContext(),
					R.array.recovery_tmo_hybrid_spinner_array,
					android.R.layout.simple_spinner_item);
			recovery_hybridAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			recovery_hybridSpinner.setAdapter(recovery_hybridAdapter);
		} else {

		}

	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView((View) arg2);

	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == ((View) arg1);

	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
		case 0:
			return context.getResources().getString(R.string.title_section0);
		case 1:
			return context.getResources().getString(R.string.title_section1);
		case 2:
			return context.getResources().getString(R.string.title_section2);
		}
		return null;
	}
}
