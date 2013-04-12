package com.example.smsread;

import java.util.ArrayList;
import java.util.HashMap;

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
		gridViewList = getSmsList();
		Log.d(TAG, "getSmsList OK");
		gridViewAdapter = getSmsView();
		Log.d(TAG, "getSmsView OK");
		gridView.setAdapter(gridViewAdapter);
		gridView.setOnItemClickListener(gridViewClickListener);

	}
	protected ArrayList<HashMap<String, Object>> getSmsList() {
		gridViewList=new ArrayList<HashMap<String,Object>>();
		HashMap<String, Object>  map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.blank_img);
		map.put("ItemText", "abc1");
		gridViewList.add(map);
		map.put("ItemImage", R.drawable.blank_img);
		map.put("ItemText", "abc2");
		gridViewList.add(map);
		map.put("ItemImage", R.drawable.blank_img);
		map.put("ItemText", "abc3");
		gridViewList.add(map);
		map.put("ItemImage", R.drawable.blank_img);
		map.put("ItemText", "abc4");
		gridViewList.add(map);
		return gridViewList;
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
