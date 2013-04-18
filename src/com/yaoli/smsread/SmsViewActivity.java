package com.yaoli.smsread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yaoli.smsread.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;

public class SmsViewActivity extends Activity{
	public static final String TAG = "SmsViewActivity";
	private GridView gridView;
	SimpleAdapter   gridViewAdapter;
	ArrayList<HashMap<String, Object>>  gridViewList;
	Button btnBack;
	SmsDetail smsDetail;
	HashSet<String> setViewName;//用于去重的视图名称
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_sms_view);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_view); 
		
		Log.d(TAG, "setTitle OK");
		btnBack = (Button)findViewById(R.id.bt_view_back);
		btnBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		gridView = (GridView)findViewById(R.id.grid_view);
		gridViewList=new ArrayList<HashMap<String,Object>>();
		setViewName = new HashSet<String>();
		getSmsList();
		Log.d(TAG, "getSmsList OK");
		gridViewAdapter = getSmsView();
		Log.d(TAG, "getSmsView OK");
		gridView.setAdapter(gridViewAdapter);
		gridView.setOnItemClickListener(gridViewClickListener);
		

	}
	public boolean isViewSms(String body)
	{
		Pattern pattern = Pattern.compile("\\{*.\\}");
		Matcher matcher = pattern.matcher(body);
		if(matcher.find())
			return true;
		else return false;
	}
	public String getViewName(String body)
	{
		if(isViewSms(body))
		{
			int start = body.indexOf('{');
			int end = body.indexOf('}');
			return body.substring(start, end+1);
		}
		return null;
	}
	protected void getSmsList() {
		Log.d(TAG, "getSmsList begin");
		if(smsDetail==null)
		{
			smsDetail = new SmsDetail(SmsViewActivity.this);
		}
		smsDetail.setAddr("10698888170002100");
		Log.d(TAG, "SetAddr OK");
		SmsClass sms = null;
		for(int i=0;i<60;i++)
		{
			sms = smsDetail.getOneSms();
			if (sms == null) return ;
			//成功取得一条完整的短信
			Log.d(TAG, "i="+i+" "+sms.body);
			String viewName = getViewName(sms.body);
			Log.d(TAG, "viewName="+viewName);
			if(setViewName.contains(viewName)) 
			{
				Log.d(TAG, "contains");
				continue;
			}
			setViewName.add(viewName);
			if(viewName != null)
			{
				Log.d(TAG, "ViewName != null");
				HashMap<String, Object>  map = new HashMap<String, Object>();
				map.put("ItemImage", R.drawable.blank_cube);
				map.put("ItemText", viewName);
				gridViewList.add(map);
				Log.d(TAG, "gridViewList add ok");
			}
		}
		Log.d(TAG, "getSmsList OK");
		/*
		map.put("ItemImage", R.drawable.blank_cube);
		map.put("ItemText", "abc1");
		gridViewList.add(map);
		map.put("ItemImage", R.drawable.blank_cube);
		map.put("ItemText", "abc2");
		gridViewList.add(map);
		map.put("ItemImage", R.drawable.blank_cube);
		map.put("ItemText", "abc3");
		gridViewList.add(map);
		map.put("ItemImage", R.drawable.blank_cube);
		map.put("ItemText", "abc4");
		gridViewList.add(map);*/
	}
	
	protected SimpleAdapter getSmsView() {
		return new SimpleAdapter(this, gridViewList, R.layout.grid_view_item,
				new String[]{"ItemImage", "ItemText"},
				new int[]{R.id.gridview_image, R.id.gridview_text});
	}
	
	private  OnItemClickListener  gridViewClickListener=new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0,//The AdapterView where the click happened  
				                View arg1,//The view within the AdapterView that was clicked 
				                int pos,//The position of the view in the adapter 
				                long arg3//The row id of the item that was clicked 
				                )
		{
			HashMap<String, Object>  item=(HashMap<String, Object>)arg0.getItemAtPosition(pos);
			setTitle((String)item.get("itemtext"));
		}
	};
}
