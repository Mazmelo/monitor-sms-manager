package com.yaoli.smsread;

import java.util.*;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
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

public class SmsDetailActivity extends Activity 
implements OnScrollListener, OnClickListener, OnGestureListener{
	private static final String TAG = "SmsDetailActivity";
	//private SimpleAdapter mSimpleAdapter;
    private DetailListViewAdapter mSimpleAdapter;
	private ListView lv;
	private List<Map<String,Object>> list;
	HashSet<String> setId;//每位联系人所有短信的id集合
	private long lastTime;
	private boolean loadComplete;
	private int lastVisibleIndex;
	private String strAddress;
	SmsDetail smsDetail;
	EditText et;
	SmsManager sms;
	Button btnReply;
	private boolean sendAble;//当前按键处于发送状态还是回复状态

	private static final String WX_APP_ID = "wxac5ff3d0320855de";
	private IWXAPI wxapi;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    //自定义程序标题栏
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_sms_detail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_detail); 
		
		sendAble = false;//默认是回复状态
		et = (EditText)findViewById(R.id.et_msg);
		//取得回击Item带来的数据
		Bundle bundle = getIntent().getExtras();
		strAddress = bundle.getString(getText(R.string.sms_address).toString());
		if(strAddress.startsWith("+"))
			strAddress = strAddress.substring(1);
		String strContact = bundle.getString(getText(R.string.sms_contact).toString());
		String strShowAddr = "";
		if(strAddress.equals(strContact)) strShowAddr = strContact;
		else strShowAddr = strContact+"(" + strAddress + ")"; 
		
		//标题栏的设置
		TextView tv = (TextView)findViewById(R.id.sms_address);
		tv.setText(strShowAddr);
		//返回按钮
		Button btnBack = (Button)findViewById(R.id.bt_back);
		btnBack.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		finish();
        	}
        });
		btnReply = (Button)findViewById(R.id.bt_reply);
		btnReply.setOnClickListener(this);
		
		lastTime = 0;
		loadComplete = false;
		setId = new HashSet<String>();
		setId.clear();
		smsDetail = new SmsDetail(SmsDetailActivity.this);
		//smsDetail.init();
		list = new ArrayList<Map<String,Object>>();
		getMoreData(strAddress, 30);
		
		/*mSimpleAdapter = new SimpleAdapter(this, list, R.layout.sms_item,
	                new String[] { "ItemDate", "ItemText" },  
	                new int[] { R.id.item_date, R.id.item_content }); */
        mSimpleAdapter = new DetailListViewAdapter(this, list);
		lv = (ListView)findViewById(R.id.lvSmsDetail);
		lv.setAdapter(mSimpleAdapter);  
		lv.setOnScrollListener(this); 
		
		//注册到微信
		wxapi = WXAPIFactory.createWXAPI(SmsDetailActivity.this, WX_APP_ID, true);
		wxapi.registerApp(WX_APP_ID);
		/*lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
			  int position, long id) {
			 ListView listView = (ListView)parent;
			 HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
			 String date = map.get("ItemDate");
			 String text = map.get("ItemText");
			 
			 //发送内容到微信
			 WXTextObject textObj = new WXTextObject();
			 textObj.text = text;
			 
			 WXMediaMessage msg = new WXMediaMessage();
			 msg.mediaObject = textObj;
			 msg.description = text;
			 
			 SendMessageToWX.Req req = new SendMessageToWX.Req();
			 req.transaction = String.valueOf(System.currentTimeMillis());
			 req.message = msg;
             req.scene =SendMessageToWX.Req.WXSceneSession;
			 
			 wxapi.sendReq(req);
			 Toast.makeText(SmsDetailActivity.this, date ,Toast.LENGTH_LONG).show();
			}
		}); */
	
		
	}

	public void onScroll(AbsListView view, int firstVisibleItem, 
			 int visibleItemCount, int totalItemCount)
	{
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
		smsDetail.setAddr(telNum);
		SmsClass sms = null;
		for(int i=0;i<getNum;i++)
		{
			sms = smsDetail.getOneSms();
			if(sms == null) 
			{
				loadComplete = true;
				return ;
			}
			TimeUtil t = new TimeUtil(sms.time);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ItemDate", t.toDate());
			map.put("ItemText", sms.body);
            map.put("ItemType", String.valueOf(sms.type));
            if(sms.read)
                map.put("ItemRead", sms.read);
			list.add(map);
		}
		/*String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" };
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
				Toast.makeText(SmsDetailActivity.this, "全部加载完毕", Toast.LENGTH_LONG).show();
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
			//首先去重
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
			//把最后的短信内容输出
			HashMap<String, String> map = new HashMap<String, String>();  
			map.put("ItemDate", smsDetail.getDate());
			map.put("ItemText", smsDetail.toString());
			list.add(map);
		}*/
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
			btnReply.setText("发送");
		}
		else
		{
			if(et.getText().length()==0)
			{
				Toast.makeText(this, "发送的内容不能为空", Toast.LENGTH_LONG).show();
			}
			else
			{
				sms = SmsManager.getDefault();
				List<String> list = sms.divideMessage(et.getText().toString());
				for(String text:list)
					sms.sendTextMessage(strAddress, null, text, null, null);
				//发送完成之后 需要存入数据库
				ContentValues values = new ContentValues();
				values.put("date", System.currentTimeMillis());
				values.put("read", 0);
				values.put("type", 2);
				values.put("address", strAddress);
				values.put("body", et.getText().toString());
				getContentResolver().insert(Uri.parse("content://sms/sent"), values);
				Toast.makeText(this, "正在发送...", Toast.LENGTH_LONG).show();
				this.finish();
			}
		}
	}
	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onDown");
		return false;
	}
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float x,
			float y) {
		// TODO Auto-generated method stub
		Log.d(TAG, "OnFling");
        if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH){
            return false;
        }
        if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(x) > SWIPE_THRESHOLD_VELOCITY) {
            Toast.makeText(getApplicationContext(), "Left Swipe", Toast.LENGTH_SHORT).show();
        } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(x) > SWIPE_THRESHOLD_VELOCITY) {
            Toast.makeText(getApplicationContext(), "Right Swipe", Toast.LENGTH_SHORT).show();
        }
		return true;
	}
	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onLongPress");
	}
	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onScroll");
		return false;
	}
	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onShowPress");
	}
	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onSingleTapUp");
		return false;
	}

}
