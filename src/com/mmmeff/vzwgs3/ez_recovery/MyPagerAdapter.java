package com.mmmeff.vzwgs3.ez_recovery;

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
	private ArrayAdapter<CharSequence> recovery_recoveryAdapter, recovery_hybridAdapter;
	private Button browseButton, flashButton, rebootButton;
	private EditText customRecoveryPathField;
	private RadioGroup recoveryRadioGroup;
	
	public MyPagerAdapter(Context context){
		this.context = context;
		this.commander = new Commander();
	}
	
	public int getCount() {
        return 4;
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
            
            //load components
            customRecoveryPathField = (EditText) view.findViewById(R.id.pathField);
            recoveryRadioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
            
            //load radio buttons
            
            
            //load buttons
            browseButton = (Button) view.findViewById(R.id.browseButton);
            flashButton = (Button) view.findViewById(R.id.flashButton);
            rebootButton = (Button) view.findViewById(R.id.rebootButton);
            
            //set up recovery choices in spinner
            recovery_recoverySpinner = (Spinner) view.findViewById(R.id.recovery_recovery_spinner);
            recovery_recoveryAdapter = ArrayAdapter.createFromResource(
    				view.getContext(), R.array.recovery_recovery_spinner_array,
    				android.R.layout.simple_spinner_item);
    		recovery_recoveryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		recovery_recoverySpinner.setAdapter(recovery_recoveryAdapter);
    		
    		//set up hybrid recovery/kernel choices in spinner
    		recovery_hybridSpinner = (Spinner) view.findViewById(R.id.recovery_hybrid_spinner);
            recovery_hybridAdapter = ArrayAdapter.createFromResource(
    				view.getContext(), R.array.recovery_hybrid_spinner_array,
    				android.R.layout.simple_spinner_item);
    		recovery_hybridAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		recovery_hybridSpinner.setAdapter(recovery_hybridAdapter);
            
    		//configure button listeners
    		OnClickListener flashListener = new OnClickListener() {
                public void onClick(View v) {
                	boolean success;
                	int selection = recoveryRadioGroup.getCheckedRadioButtonId();
                	switch (selection){
                		case R.id.recovery_radioButtonCustom:
                			success = commander.FlashCustomRecovery(customRecoveryPathField.getText().toString());
                			break;
                		case R.id.recovery_radioButtonRecovery:
                			success = commander.FlashRecovery((String) recovery_recoverySpinner.getSelectedItem());
                			break;
                		case R.id.recovery_radioButtonHybrid:
                			success = commander.FlashRecovery((String) recovery_hybridSpinner.getSelectedItem());
                			break;
                		case R.id.recovery_radioButtonStock:
                			success = commander.FlashRecovery("recovery_rec_stock.img");
                			break;
                		default:
                			Toast toast = Toast.makeText(context, "Please make a selection.", Toast.LENGTH_SHORT);
                	    	toast.show();
                	    	success = false;
                	    	break;
                	}
                	if (!success){
                		Toast toast = Toast.makeText(context, "Recovery Flash Failed.", Toast.LENGTH_SHORT);
            	    	toast.show();
                	} else {
                		Toast toast = Toast.makeText(context, "Recovery Flash Successful!", Toast.LENGTH_SHORT);
            	    	toast.show();
                	}
                }	
            };
    		flashButton.setOnClickListener(flashListener);
    		
    		OnClickListener rebootListener = new OnClickListener() {
    			public void onClick(View v) {
    				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    				    public void onClick(DialogInterface dialog, int which) {
    				        switch (which){
    				        case DialogInterface.BUTTON_POSITIVE:
    				        	Log.d("tag", "Rebooting into recovery.");
    				            commander.rebootRecovery();
    				            break;

    				        case DialogInterface.BUTTON_NEGATIVE:
    				            //No button clicked
    				            break;
    				        }
    				    }
    				};
    				AlertDialog.Builder builder = new AlertDialog.Builder(context);
    				builder.setMessage("Are you sure you want to reboot into recovery?").setPositiveButton("Yes", dialogClickListener)
    				    .setNegativeButton("No", dialogClickListener).show();
    			}
    		};
    		rebootButton.setOnClickListener(rebootListener);
    		
            break;
        case 2:
            resId = R.layout.kexec_layout;
            view = inflater.inflate(resId, null);
            break;
        case 3:
            resId = R.layout.romcentral_layout;
            view = inflater.inflate(resId, null);
            break;
        
        }

        

        ((ViewPager) collection).addView(view, 0);

        return view;
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
        	case 0: return context.getResources().getString(R.string.title_section3);
        	case 1: return context.getResources().getString(R.string.title_section1);    
        	case 2: return context.getResources().getString(R.string.title_section2);
        	case 3: return context.getResources().getString(R.string.title_section4);
        }
        return null;
    }
}
