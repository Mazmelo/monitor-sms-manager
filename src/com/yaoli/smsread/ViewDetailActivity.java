package com.yaoli.smsread;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ViewDetailActivity extends Activity {
	Button btnBack;
	ListView listView;
	ArrayList<HashMap<String, Object>> listViewData;
	SmsDetail smsDetail;
	Handler handler;
	SimpleAdapter listViewAdapter;
	String addr, viewName;
	private static final String TAG="ViewDetailActivity"; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_sms_view);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_view); 
		
		Log.d(TAG, "setTitle OK");
		//取得回击Item带来的数据
		Bundle bundle = getIntent().getExtras();
		addr = bundle.getString("ViewDetailAddr");
		viewName = bundle.getString("ViewName");
		Log.d(TAG, "addr="+addr+" viewName="+viewName);
		
		listView = (ListView)findViewById(R.id.lvView);
		listViewData =new ArrayList<HashMap<String,Object>>();
		smsDetail = new SmsDetail(ViewDetailActivity.this);
		//smsDetail.setAddr(addr);
		//getSmsList();
		
		
		Log.d(TAG, "getSmsList OK");
		listViewAdapter = new SimpleAdapter(this, listViewData, R.layout.sms_item,
				new String[]{"ItemDate", "ItemText"},
				new int[]{R.id.item_date, R.id.item_content});
		Log.d(TAG, "getSmsView OK");
		listView.setAdapter(listViewAdapter);
		
		handler = new Handler();
		//handler.postDelayed(add,1);//放入队列并延迟执行
		handler.post(add);//放入队列
		
		btnBack = (Button)findViewById(R.id.bt_view_back);
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	public boolean hasViewName(String body, String name)
	{
		return  body.indexOf(name) != -1; 
	}
	public int getViewDetailList()
	{
		SmsClass sms = null;
		for(int i=0;i<10;i++)
		{
			sms = smsDetail.getOneSms();
			if(sms == null) return 1;
			if(!hasViewName(sms.body, viewName)) continue;
			HashMap<String, Object>  map = new HashMap<String, Object>();
			TimeUtil t = new TimeUtil(sms.time);
			map.put("ItemDate", t.toDate());
			map.put("ItemText", sms.body);
			listViewData.add(map);
		}
		return 0;
	}
	Runnable add=new Runnable(){
	    	public void run() {
	    		if(getViewDetailList()==0)
	    		{
	    			handler.postDelayed(add, 10);
	    		}
	    		listViewAdapter.notifyDataSetChanged();
	    	}
	    };
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_detail, menu);
		return true;
	}

} 
