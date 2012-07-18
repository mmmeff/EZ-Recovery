package com.mmmeff.vzwgs3.ez_recovery;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

public class MyPagerAdapter extends PagerAdapter {

	Context context;
	
	public MyPagerAdapter(Context context){
		this.context = context;
	}
	
	public int getCount() {
        return 3;
    }

    public Object instantiateItem(View collection, int position) {

        LayoutInflater inflater = (LayoutInflater) collection.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int resId = 0;
        switch (position) {
        case 0:
            resId = R.layout.recovery_layout;
            break;
        case 1:
            resId = R.layout.kexec_layout;
            break;
        case 2:
            resId = R.layout.about_layout;
            break;
        }

        View view = inflater.inflate(resId, null);

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
            case 0: return context.getResources().getString(R.string.title_section1);
            case 1: return context.getResources().getString(R.string.title_section2);
            case 2: return context.getResources().getString(R.string.title_section3);
        }
        return null;
    }
}
