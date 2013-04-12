package com.example.smsread;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class SmsNewActivity extends Activity{
	public static final String TAG = "SmsNewActivity";
	public EditText et;
	private Button btnBack;
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_sms_new);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_new); 
		
		btnBack = (Button)findViewById(R.id.bt_view_back);
		btnBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		et = (EditText)findViewById(R.id.new_sms_address);
		et.setFocusable(true);
		et.setFocusableInTouchMode(true);
		et.requestFocus();
		
		/*Timer timer = new Timer(); //设置定时器
		timer.schedule(new TimerTask() {
		@Override
			public void run() { //弹出软键盘的代码
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(et, InputMethodManager.RESULT_SHOWN);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
			}
		}, 300); //设置300毫秒的时长*/
	}
}
