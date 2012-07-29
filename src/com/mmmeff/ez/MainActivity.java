package com.mmmeff.ez;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.EditText;

public class MainActivity extends FragmentActivity {

	private ViewPager mViewPager;
	private EditText pathField;
	private FileMan fileman;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		fileman = new FileMan(this);
		
		setContentView(R.layout.activity_main);
		MyPagerAdapter adapter = new MyPagerAdapter(this);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(adapter);
		mViewPager.setCurrentItem(0);

		pathField = (EditText) findViewById(R.id.pathField);

	}
	
	public void onActivityResult(int requestCode,int resultCode,Intent data)
    {
     super.onActivityResult(requestCode, resultCode, data);

     String extraData = data.getStringExtra("path");
     pathField.setText(extraData);
    }

}
