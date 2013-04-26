package com.yaoli.smsread;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//测试提交功能
public class MainActivity extends Activity implements OnItemClickListener{
	private static final String TAG = "MainActivity";
	final String SMS_URI_INBOX = "content://sms/";
	
	ListView lv;
	List<Map<String, Object>> appItems;
	//SimpleAdapter simpleAdapter;
	ListViewAdapter myAdapter;
	long SelectedThreadId;
	int CurPos;
	Button btnBar, btnNew, btnView;
	boolean leftBarToggle = false;
	PopMenu popMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏通知栏（电池、时间等）
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  
        //隐藏程序标题栏
        
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "setFeatureInt");
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_main);
		Log.d(TAG, "setFeatureInt OK");
		appItems = new ArrayList<Map<String, Object>>();
		myAdapter = new ListViewAdapter(this, appItems);
		Log.d(TAG, "myAdapter="+myAdapter);
		lv = (ListView)findViewById(R.id.lvMain);
        //getSmsInPhone();
		lv.setAdapter(myAdapter);
		
        /*Timer timer = new Timer(); //设置定时器
		timer.schedule(new TimerTask() {
		@Override
			public void run() { //弹出软键盘的代码
				getSmsInPhone();
			}
		}, 100); //设置50毫秒的时长*/
		
		Handler handler=new Handler();
		//handler.postDelayed(add,1);//放入队列并延迟3ms执行
		handler.post(add);//放入队列
		
		//弹出式菜单
		popMenu = new PopMenu(MainActivity.this);
		popMenu.addItems(new String[]{"全部告警", "视图告警", "告警集合", "其他告警"});
		//popMenu.setOnItemClickListener(this);
		
        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(new OnItemLongClickListener(){
        	@SuppressLint("UseValueOf") public boolean onItemLongClick(AdapterView<?> parent, View v,
                    int position, long id) {
        		String strContact = appItems.get(position).get("Contact").toString();
        		String SelectedAddr = appItems.get(position).get("Address").toString();
        		CurPos = position;
        		//Integer integer = new Integer((String)(appItems.get(position).get("ThreadID")));
        		SelectedThreadId = Long.valueOf(appItems.get(position).get("ThreadID").toString());
        		String strNameInTitle = strContact;
        		if(!strContact.equals(SelectedAddr))
        			strNameInTitle = strContact+"("+SelectedAddr+")";
        		Cursor cur = getContentResolver().query(Uri.parse("content://sms/"), new String[]{"_id", "address"}, "address="+"'"+SelectedAddr+"'", null, null);
        		new AlertDialog.Builder(parent.getContext()).setTitle("删除"+strNameInTitle+"的全部"+String.valueOf(cur.getCount())+"条短信?").
                setPositiveButton("确定", 
                		new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								getContentResolver().delete(Uri.parse("content://sms/conversations/"+SelectedThreadId), null, null);
								appItems.remove(CurPos);
								myAdapter.notifyDataSetChanged();
								Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_LONG).show();
							}
						}).setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int witch) {
								Toast.makeText(MainActivity.this, "取消了", Toast.LENGTH_LONG).show();
							}
						}).show();
                cur.close();
        		return false;
            }
        	});
        
        
        btnBar = (Button)findViewById(R.id.btn_bar);
        btnBar.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		/*Intent intent = new Intent(MainActivity.this, SmsViewActivity.class);
    			startActivity(intent);*/
        		//toggleLeftBar();
        		Log.d(TAG, "btn_view clicked");
        		//弹出窗口
        		popMenu.showAsDropDown(v);
        	}
        });
        
        btnNew = (Button)findViewById(R.id.btn_new);
        btnNew.setOnTouchListener(new View.OnTouchListener() {
        	public boolean onTouch(View v, MotionEvent event) {
        		Log.d(TAG, "onTouched");
        		return false;
        	}
        });
        btnNew.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		Intent intent = new Intent(MainActivity.this, SmsNewActivity.class);
    			startActivity(intent);
        	}
        });
        
        btnView = (Button)findViewById(R.id.btn_view);
        btnView.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		/*跳转到视图的Activity
        		 * Intent intent = new Intent(MainActivity.this, SmsViewActivity.class);
    			startActivity(intent);*/
        		//popMenu.setOnItemClickListener(MainActivity.this);
        	}
        });
        
    }
    private void toggleLeftBar() 
    {
    	LinearLayout leftBar = (LinearLayout)findViewById(R.id.leftBar);
		Button btnBar = (Button)findViewById(R.id.btn_bar);
		if(!leftBarToggle)
		{
			leftBar.setVisibility(View.VISIBLE);
			leftBarToggle = true;
			//btnBar.setText("<<<");
		}
		else
		{
			leftBar.setVisibility(View.GONE);
			leftBarToggle = false;
			//btnBar.setText(">>>");
		}
    }
    public String getAddressCount(String addr)
    {
    	String[] projection = new String[] {"_id"};
    	Cursor cur = getContentResolver().query(Uri.parse(SMS_URI_INBOX), projection, "address='"+addr+"'", null, null);
    	return String.valueOf(cur.getCount());
    }
    Runnable add=new Runnable(){
    	public void run() {
    		getSmsInPhone();
    		myAdapter.notifyDataSetChanged();
    	}
    };
    
    public void getSmsInPhone() {
    	
    	HashSet<String> hashSet = new HashSet<String>();  
    	try {
    		Uri uri = Uri.parse(SMS_URI_INBOX);
    		String[] projection = new String[] { "_id", "address", "person", "body", "date", "type", "thread_id"};
    		Cursor cur = getContentResolver().query(uri, projection, "1=1) group by (address", null, "date desc limit 100");
    		Log.d(TAG, "getCount:"+cur.getCount());
    		if(cur.moveToFirst()) {
    			int index_Address = cur.getColumnIndex("address");
				//int index_Person = cur.getColumnIndex("person");
				int index_Body = cur.getColumnIndex("body");
				int index_Date = cur.getColumnIndex("date");
				//int index_Type = cur.getColumnIndex("type");
				int index_thread_id = cur.getColumnIndex("thread_id");
				
				do{
					String strAddress = cur.getString(index_Address);
					//int intPerson = cur.getInt(index_Person);
					String strBody = cur.getString(index_Body);
					long longDate = cur.getLong(index_Date);

					long thread_id = cur.getLong(index_thread_id);
					Log.d(TAG, "addr="+strAddress);
					if(hashSet.contains(strAddress))
					{
						continue;
					}
					else
					{
						hashSet.add(strAddress);
					}

					/*
					SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd", Locale.US);
					Date d = new Date(longDate);
					
					String strDate = dateFormat.format(d);*/
					TimeUtil t = new TimeUtil(longDate);
					String strDate = t.toLabel();
					
					Map<String, Object> appItem = new HashMap<String, Object>();
					//appItem.put("Image", R.id.Image);
					appItem.put("Address", strAddress);
					appItem.put("Contact", getContactWithTelNum(strAddress));
					appItem.put("Date", strDate);
					appItem.put("Content", strBody);
					appItem.put("ThreadID", thread_id);
					appItems.add(appItem);
					//myAdapter.notifyDataSetChanged();
				} while (cur.moveToNext());
				if (!cur.isClosed()) {
					cur.close();
					cur = null;
				}
			} else {
				
			} // end if
		} catch (SQLiteException ex) {
			Log.d(TAG, ex.getMessage());
		}
    	for(int i=0;i<appItems.size();i++)
    	{
    		appItems.get(i).put("Count", "("+getAddressCount(appItems.get(i).get("Address").toString())+")");
    	}
    	Log.d(TAG, "get All Count OK");
		Log.d(TAG, "myAdapter="+myAdapter);
    	//myAdapter.notifyDataSetChanged();
    	Log.d(TAG, "getSmsInPhone OK");
    }
    
    /*
     * 根据电话号码取得联系人的名字
     * */
    public String getContactWithTelNum(String telNum)
    {
    	String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
    		ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
    	String select = ContactsContract.CommonDataKinds.Phone.NUMBER+"='"+telNum+"'";
    	Cursor cur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, select, null, null);
    	if(cur == null || cur.getCount()==0) return telNum;
    	cur.moveToFirst();
    	return cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
		// TODO Auto-generated method stub
		//super.onListItemClick(l, v, position, id);
		Log.d(TAG, "onItemClick position="+position+" id="+id);
		if(position>0)
		{
			Intent intent = new Intent(this, SmsDetailActivity.class);
	    	TextView tv = (TextView)v.findViewById(R.id.Contact);
	    	intent.putExtra(getText(R.string.sms_contact).toString(), tv.getText());
	    	tv = (TextView)v.findViewById(R.id.Content);
	    	intent.putExtra(getText(R.string.sms_content).toString(), tv.getText());
	    	intent.putExtra(getText(R.string.sms_address).toString(), appItems.get(position-1).get("Address").toString());
	    	startActivity(intent);
		}
		else
		{
			Intent intent = new Intent(this, SmsNewActivity.class);
			Log.d(TAG, "SmsNewActivity OK");
			startActivity(intent);
		}
		//Toast.makeText(this, "position="+position+"id="+id, Toast.LENGTH_LONG).show();
    }
}
