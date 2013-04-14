package com.example.smsread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

public class SmsDetailActivity extends Activity implements OnScrollListener,OnClickListener,OnTouchListener{
	private static final String TAG = "SmsDetailActivity";
	private SimpleAdapter mSimpleAdapter;
	private ListView lv;
	private ArrayList<HashMap<String,String>> list;
	HashSet<String> setId;//ÿλ��ϵ�����ж��ŵ�id����
	private long lastTime;
	private boolean loadComplete;
	private int lastVisibleIndex;
	private String strAddress;
	SmsDetail smsDetail;
	Button btnSend;
	EditText et;
	SmsManager sms;
	private boolean sendAble;//��ǰ�������ڷ���״̬���ǻظ�״̬
	
	private static final String WX_APP_ID = "wxac5ff3d0320855de";
	private IWXAPI wxapi;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    //���س��������
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_sms_detail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_detail); 
		
		Log.d(TAG, "onCreate Title OK");
		sendAble = false;//Ĭ���ǻظ�״̬
		et = (EditText)findViewById(R.id.et_msg);
		btnSend = (Button)findViewById(R.id.bt_reply);
		//btn = (Button)findViewById(R.id.bt_send);
		//btn.setOnClickListener(this);
		Bundle bundle = getIntent().getExtras();
		
		strAddress = bundle.getString(getText(R.string.sms_address).toString());
		if(strAddress.startsWith("+"))
			strAddress = strAddress.substring(1);
		String strContact = bundle.getString(getText(R.string.sms_contact).toString());
		String strShowAddr = "";
		if(strAddress.equals(strContact)) strShowAddr = strContact;
		else strShowAddr = strContact+"(" + strAddress + ")"; 
		
		
		//������������

		TextView tv = (TextView)findViewById(R.id.sms_address);
		tv.setText(strShowAddr);
		
		Button btnBack = (Button)findViewById(R.id.bt_back);
		btnBack.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		finish();
        	}
        });
		Button btnReply = (Button)findViewById(R.id.bt_reply);
		btnReply.setOnClickListener(this);
		
		
		lastTime = 0;
		loadComplete = false;
		setId = new HashSet<String>();
		setId.clear();
		smsDetail = new SmsDetail();
		smsDetail.init();
		list = new ArrayList<HashMap<String,String>>();
		getMoreData(strAddress, 30);
		
		//ScrollView sv = (ScrollView)findViewById(R.id.sms_scrollview);
		mSimpleAdapter = new SimpleAdapter(this, list, R.layout.sms_item,  
	                new String[] { "ItemDate", "ItemText" },  
	                new int[] { R.id.item_date, R.id.item_content }); 
		//mSimpleAdapter.isEnabled(1);
		lv = (ListView)findViewById(R.id.lvSmsDetail);
		lv.setAdapter(mSimpleAdapter);  
		lv.setOnScrollListener(this); 
		//lv.setOnTouchListener(this);
		
		//ע�ᵽ΢��
		wxapi = WXAPIFactory.createWXAPI(SmsDetailActivity.this, WX_APP_ID, true);
		wxapi.registerApp(WX_APP_ID);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
			  int position, long id) {
			 ListView listView = (ListView)parent;
			 HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
			 String date = map.get("ItemDate");
			 String text = map.get("ItemText");
			 
			 //�������ݵ�΢��
			 WXTextObject textObj = new WXTextObject();
			 textObj.text = text;
			 
			 WXMediaMessage msg = new WXMediaMessage();
			 msg.mediaObject = textObj;
			 msg.description = text;
			 
			 SendMessageToWX.Req req = new SendMessageToWX.Req();
			 req.transaction = String.valueOf(System.currentTimeMillis());
			 req.message = msg;
			 
			 wxapi.sendReq(req);
			 Toast.makeText(SmsDetailActivity.this, date ,Toast.LENGTH_LONG).show();
			}
		});
	
		
	}
	public boolean onTouch(View v, MotionEvent event)
	{
		/*if(sendAble)
		{
			InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(INPUT_METHOD_SERVICE); 
	        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	        if(et.getText().length()==0)
	        {
	    		et.setVisibility(View.GONE);
	    		btn.setText("�ظ�");
	    		sendAble = false;
	        }
		}*/
		return true;
	}
	public void onScroll(AbsListView view, int firstVisibleItem, 
			 int visibleItemCount, int totalItemCount)
	{
		//Log.d(TAG, "firstVisibleItem="+firstVisibleItem+"visibleItemCount="+visibleItemCount);
		lastVisibleIndex = firstVisibleItem + visibleItemCount;
	}
	public void onScrollStateChanged(AbsListView view, int scrollState)
	{
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& lastVisibleIndex >= mSimpleAdapter.getCount()-1)
		{
			Log.d(TAG, "lastVisibleIndex="+lastVisibleIndex+" getCount="+mSimpleAdapter.getCount());
			if(loadComplete)
			{
				
				return ;
			}
			getMoreData(strAddress, 30);
			mSimpleAdapter.notifyDataSetChanged();
		}
	}
	public void getMoreData(String telNum, int getNum)
	{
		String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" };
		String selection;
		if(lastTime!=0)
		{
			selection = "address='"+telNum+"' and date<='"+lastTime+"'";
		}
		else
		{
			selection = "address='"+telNum+"'";
		}
		Log.d(TAG, "selection="+selection);
		Cursor cur = getContentResolver().query(Uri.parse("content://sms/"), 
				projection,
				selection,
				null,
				"date desc limit "+getNum);
		Log.d(TAG, "getCount="+cur.getCount());
		if(cur.getCount()<getNum)
		{
			if(lastTime!=0)
				Toast.makeText(SmsDetailActivity.this, "ȫ���������", Toast.LENGTH_LONG).show();
			loadComplete = true;
		}
		if(!cur.moveToFirst())
		{
			return ;
		}
		//int index_Address = cur.getColumnIndex("address");
		int index_id = cur.getColumnIndex("_id");
		//int index_Person = cur.getColumnIndex("person");
		int index_Body = cur.getColumnIndex("body");
		int index_Date = cur.getColumnIndex("date");
		//int index_Type = cur.getColumnIndex("type");
		do{
			//����ȥ��
			String id = cur.getString(index_id);
			if(setId.contains(id))
			{
				Log.d(TAG, "Contains id="+id);
				continue;
			}
			setId.add(id);
			//int intPerson = cur.getInt(index_Person);
			SmsClass sms = new SmsClass(cur.getString(index_Body), cur.getLong(index_Date));
			//String strBody = cur.getString(index_Body);
			//long longDate = cur.getLong(index_Date);
			if(sms.time<lastTime || lastTime==0) lastTime = sms.time;

			boolean complete = smsDetail.addSubSms(sms);
			if(complete)
			{
				HashMap<String, String> map = new HashMap<String, String>();  
				map.put("ItemDate", smsDetail.getDate());
				map.put("ItemText", smsDetail.toString());
				list.add(map);
			}
		}while(cur.moveToNext());
		if(loadComplete && !smsDetail.isEmpty())
		{
			//�����Ķ����������
			HashMap<String, String> map = new HashMap<String, String>();  
			map.put("ItemDate", smsDetail.getDate());
			map.put("ItemText", smsDetail.toString());
			list.add(map);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sms_detail, menu);
		return true;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		//Toast.makeText(v.getContext(), "Click Send", Toast.LENGTH_LONG).show();
		if(!sendAble)
		{
			sendAble = true;
			et.setVisibility(View.VISIBLE);
			et.setFocusable(true);
			et.setFocusableInTouchMode(true);
			et.requestFocus();
			InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(INPUT_METHOD_SERVICE); 
	        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			btnSend.setText("����");
		}
		else
		{
			if(et.getText().length()==0)
			{
				Toast.makeText(this, "���͵����ݲ���Ϊ��", Toast.LENGTH_LONG).show();
			}
			else
			{
				sms = SmsManager.getDefault();
				List<String> list = sms.divideMessage(et.getText().toString());
				for(String text:list)
					sms.sendTextMessage(strAddress, null, text, null, null);
				//�������֮�� ��Ҫ�������ݿ�
				ContentValues values = new ContentValues();
				values.put("date", System.currentTimeMillis());
				values.put("read", 0);
				values.put("type", 2);
				values.put("address", strAddress);
				values.put("body", et.getText().toString());
				getContentResolver().insert(Uri.parse("content://sms/sent"), values);
				Toast.makeText(this, "���ڷ���...", Toast.LENGTH_LONG).show();
				this.finish();
			}
		}
	}

}
