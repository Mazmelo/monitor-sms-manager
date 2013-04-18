package com.yaoli.smsread;

import com.yaoli.smsread.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.Window;
import android.widget.TabHost;

public class MainTabActivity extends FragmentActivity {
	TabHost tabHost;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_tab);
		findTabView();
		
		TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener()
		{
			public void onTabChanged(String tabId)
			{
				FragmentManager fm = getSupportFragmentManager();
			}
		};
	}
	public void findTabView()
	{
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_tab, menu);
		return true;
	}

}
