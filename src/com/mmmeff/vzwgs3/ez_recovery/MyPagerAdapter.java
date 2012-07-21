package com.mmmeff.vzwgs3.ez_recovery;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MyPagerAdapter extends PagerAdapter {

	Context context;
	
	Spinner recovery_recoverySpinner;
	Spinner recovery_hybridSpinner;
	ArrayAdapter<CharSequence> recovery_recoveryAdapter;
	ArrayAdapter<CharSequence> recovery_hybridAdapter;
	
	public MyPagerAdapter(Context context){
		this.context = context;
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
            
            break;
        case 2:
            resId = R.layout.kexec_layout;
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
        }
        return null;
    }
}
