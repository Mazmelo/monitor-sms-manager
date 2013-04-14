package com.example.smsread;

import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SmsNewActivity extends Activity{
	public static final String TAG = "SmsNewActivity";
	public EditText etRecv, etNewText;
	private Button btnBack, btnRecv, btnSend;
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
		
		btnRecv = (Button)findViewById(R.id.bt_recv);
		btnRecv.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);  
				SmsNewActivity.this.startActivityForResult(intent, 1);
			}
		});
	
		etRecv = (EditText)findViewById(R.id.et_recv);
		etRecv.setFocusable(true);
		etRecv.setFocusableInTouchMode(true);
		etRecv.requestFocus();
		
		etNewText = (EditText)findViewById(R.id.et_new_text);
		
		btnSend = (Button)findViewById(R.id.btn_send);
		btnSend.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				String text = etNewText.getText().toString();
				String recv = etRecv.getText().toString();
				if(recv.length()==0)
				{
					Toast.makeText(SmsNewActivity.this,"δѡ����ϵ��", Toast.LENGTH_LONG).show();
					return ;
				}
				if(text.length()==0)
				{
					Toast.makeText(SmsNewActivity.this,"���ݲ���Ϊ��", Toast.LENGTH_LONG).show();
					return ;
				}

				SmsManager sms = SmsManager.getDefault();
				List<String> list = sms.divideMessage(etNewText.getText().toString());
				for(String t:list)
					sms.sendTextMessage(etRecv.getText().toString(), null, t, null, null);
				//�������֮�� ��Ҫ�������ݿ�
				ContentValues values = new ContentValues();
				values.put("date", System.currentTimeMillis());
				values.put("read", 0);
				values.put("type", 2);
				values.put("address", etRecv.getText().toString());
				values.put("body", etNewText.getText().toString());
				getContentResolver().insert(Uri.parse("content://sms/sent"), values);
				Toast.makeText(SmsNewActivity.this, "���ڷ���...", Toast.LENGTH_LONG).show();
				SmsNewActivity.this.finish();
			}
		});
		
		/*Timer timer = new Timer(); //���ö�ʱ��
		timer.schedule(new TimerTask() {
		@Override
			public void run() { //��������̵Ĵ���
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(et, InputMethodManager.RESULT_SHOWN);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
			}
		}, 300); //����300�����ʱ��*/
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode)
		{
			case (1) :
			{
			if (resultCode == Activity.RESULT_OK)
			{
			Uri contactData = data.getData();
			Cursor c = getContentResolver().query(contactData, null, null, null, null);
			c.moveToFirst();
			String phoneNum=this.getContactPhone(c);
			etRecv.setText(phoneNum);
			}
			break;
			}
		}
	}
	//��ȡ��ϵ�˵绰
	private String getContactPhone(Cursor cursor)
	{
		int phoneColumn = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);  
		int phoneNum = cursor.getInt(phoneColumn); 
		String phoneResult="";
		//System.out.print(phoneNum);
		if (phoneNum > 0)
		{
		// �����ϵ�˵�ID��
			int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
			String contactId = cursor.getString(idColumn);
				// �����ϵ�˵ĵ绰�����cursor;
				Cursor phones = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				null,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " + contactId, 
				null, null);
				//int phoneCount = phones.getCount();
				//allPhoneNum = new ArrayList<String>(phoneCount);
				if (phones.moveToFirst())
				{
						// �������еĵ绰����
						for (;!phones.isAfterLast();phones.moveToNext())
						{                                            
							int index = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
							int typeindex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
							int phone_type = phones.getInt(typeindex);
							String phoneNumber = phones.getString(index);
							switch(phone_type)
							{
								case 2:
									phoneResult=phoneNumber;
								break;
							}
							   //allPhoneNum.add(phoneNumber);
						}
						if (!phones.isClosed())
						{
							   phones.close();
						}
				}
		}
		return phoneResult;
	}
}
