package com.yaoli.smsread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SmsViewActivity extends Activity{
	public static final String TAG = "SmsViewActivity";
	private ListView listView;
	SimpleAdapter   listViewAdapter;
	ArrayList<HashMap<String, Object>>  listViewData;
	Button btnBack;
	SmsDetail smsDetail;
	HashMap<String, Integer> mapViewName;//用于去重的视图名称 Integer为视图在列表中的位置
	Handler handler;
	String addr;
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
		listView = (ListView)findViewById(R.id.lvView);
		listViewData =new ArrayList<HashMap<String,Object>>();
		mapViewName = new HashMap<String, Integer>();
		smsDetail = new SmsDetail(SmsViewActivity.this);
		//getSmsList();
		handler = new Handler();
		//handler.postDelayed(add,1);//放入队列并延迟执行
		handler.post(add);//放入队列
		
		Log.d(TAG, "getSmsList OK");
		listViewAdapter = getSmsView();
		Log.d(TAG, "getSmsView OK");
		listView.setAdapter(listViewAdapter);
		listView.setOnItemClickListener(gridViewClickListener);
	}
    Runnable add=new Runnable(){
    	public void run() {
    		if(getSmsList()==0)
    		{
    			handler.postDelayed(add, 100);
    		}
    		listViewAdapter.notifyDataSetChanged();
    	}
    };
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
	protected int getSmsList() {
		Log.d(TAG, "getSmsList begin");
		addr = "10698888170002100";
		smsDetail.setAddr(addr);
		Log.d(TAG, "SetAddr OK");
		SmsClass sms = null;
		int pos = 0;
		for(int i=0;i<10;i++)
		{
			sms = smsDetail.getOneSms();
			if (sms == null) return 1;
			//成功取得一条完整的短信
			Log.d(TAG, "i="+i+" "+sms.body);
			String viewName = getViewName(sms.body);
			Log.d(TAG, "viewName="+viewName);
			if(mapViewName.containsKey(viewName)) 
			{
				Log.d(TAG, "contains");
				//对计数值加1
				Integer intpos = mapViewName.get(viewName);
				String s = listViewData.get(intpos.intValue()).get("ItemCount").toString();
				Integer integer = Integer.parseInt(s.substring(1, s.length()-1));
				integer = Integer.valueOf(integer.intValue()+1);
				s = "("+String.valueOf(integer)+")";
				Log.d(TAG, "viewName="+viewName+" pos="+intpos.intValue()+" num="+s);
				listViewData.get(intpos.intValue()).put("ItemCount", s);
				Log.d(TAG, "continue");
				continue;
			}
			mapViewName.put(viewName, pos);
			pos++;
			if(viewName != null)
			{
				Log.d(TAG, "ViewName != null");
				HashMap<String, Object>  map = new HashMap<String, Object>();
				TimeUtil t = new TimeUtil(sms.time);
				map.put("ItemView", viewName);
				map.put("ItemTime", t.toLabel());
				map.put("ItemText", sms.body);
				map.put("ItemCount", "(1)");//至少为1
				listViewData.add(map);
				Log.d(TAG, "gridViewList add ok");
			}
		}
		Log.d(TAG, "getSmsList OK");
		return 0;
	}
	
	protected SimpleAdapter getSmsView() {
		return new SimpleAdapter(this, listViewData, R.layout.grid_view_item,
				new String[]{"ItemView", "ItemCount", "ItemTime", "ItemText"},
				new int[]{R.id.view_name, R.id.view_count, R.id.view_time, R.id.view_text});
	}
	
	private  OnItemClickListener  gridViewClickListener=new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0,//The AdapterView where the click happened  
				                View arg1,//The view within the AdapterView that was clicked 
				                int pos,//The position of the view in the adapter 
				                long arg3//The row id of the item that was clicked 
				                )
		{
			HashMap<String, Object>  item=(HashMap<String, Object>)arg0.getItemAtPosition(pos);
			//Toast.makeText(SmsViewActivity.this, item.get("ItemView").toString(), Toast.LENGTH_LONG).show();
			
			Intent intent = new Intent(SmsViewActivity.this, ViewDetailActivity.class);
	    	intent.putExtra("ViewDetailAddr", addr);
	    	intent.putExtra("ViewName", item.get("ItemView").toString());
	    	startActivity(intent);
		}
	};
}
