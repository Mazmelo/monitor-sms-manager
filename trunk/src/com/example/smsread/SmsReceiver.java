package com.example.smsread;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class SmsReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Toast.makeText(context, "ab", Toast.LENGTH_LONG).show();
		/*if ("android.provider.Telephony.SMS_RECEIVED"
				.equals(intent.getAction()))
		{
			StringBuilder sb = new StringBuilder();
			//������SMS������������
			Bundle bundle = intent.getExtras();
			// �ж��Ƿ�������
			if (bundle != null)
			{
				//  ͨ��pdus���Ի�ý��յ������ж�����Ϣ
				Object[] objArray = (Object[]) bundle.get("pdus");
				//�������Ŷ���array,�������յ��Ķ��󳤶�������array�Ĵ�С
				SmsMessage[] messages = new SmsMessage[objArray.length];
				for (int i = 0; i < objArray.length; i++)
				{
					messages[i] = SmsMessage
							.createFromPdu((byte[]) objArray[i]);
				}

				//�������Ķ��źϲ��Զ�����Ϣ��StringBuilder����
				for (SmsMessage currentMessage : messages)
				{
					sb.append("������Դ:");
					// ��ý��ն��ŵĵ绰����
					sb.append(currentMessage.getDisplayOriginatingAddress());
					sb.append("\n------��������------\n");
					// ��ö��ŵ�����
					sb.append(currentMessage.getDisplayMessageBody());
				}
			}
			Intent mainIntent = new Intent(context, SmsDetailActivity.class);
			mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);			
			context.startActivity(mainIntent);
			Toast.makeText(context, sb.toString(), Toast.LENGTH_LONG).show();

		}*/
	}

}
