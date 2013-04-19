package com.yaoli.smsread;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class SmsDetail {
	long lastTime, oldestTime;
	long lastLoadTime;//�Ѽ��ض����е����һ����ʱ�� ���ڷ�ҳ��������
	boolean complete;//�Ƿ��Ѿ��ռ���һ�������Ķ�����
	SmsClass curSms;//��ǰδ����Ķ���
	ArrayList<Queue<SmsClass>> smsList;//��Ϣ���е����� �����±��ʾ����
	ArrayList<SmsClass> fullSmsList;//�����Ķ��ż���
	Queue<SmsClass> qBody1;//������Ϣ�ĵ�һ��
	Queue<SmsClass> qBody2;//������Ϣ�ĵڶ���
	Queue<SmsClass> qBody3;//������Ϣ�ĵ�����
	String sbody;
	Context context;
	String addr;//��ǰ��ϵ�˵ĺ���
	HashSet<String> setSmsId;//��ǰ��ϵ�����ж��ŵ�id����
	boolean isLoadCompleted;//ȫ�������ꣿ
	int curNum;//��ǰ��ȡ�صĶ�������
	private static final String TAG = "SmsDetailClass";
	public SmsDetail(Context con)
	{
		curSms = null;
		lastTime = 0;
		complete = false;
		smsList = new ArrayList<Queue<SmsClass>>();
		smsList.add(new LinkedList<SmsClass>()); 
		smsList.add(new LinkedList<SmsClass>()); 
		smsList.add(new LinkedList<SmsClass>()); 
		smsList.add(new LinkedList<SmsClass>()); 
		setSmsId = new HashSet<String>();
		context  = con;
		Log.d(TAG, "Sms Detail Class construct Ok");
	}
	//ȡ��һ�����ŵ��±꣬���δ�ֶΣ��򷵻�0�����򷵻ظö����ںϲ������������е��±�
	public int getSmsIndex(SmsClass sms)
	{
		sms.cur = 0;
		Pattern pattern = Pattern.compile("^\\d*/\\d*");
		Matcher matcher = pattern.matcher(sms.body);
		if(matcher.find())
		{
			int sep = sms.body.indexOf('/');
			sms.cur = Integer.valueOf(sms.body.substring(sep-1, sep)).intValue();
			sms.total = Integer.valueOf(sms.body.substring(sep+1, sep+2)).intValue();
			Log.d(TAG, "cur="+sms.cur+" total="+sms.total+" time="+sms.time);
			sep += 2;
			if(sms.body.charAt(sep)==')') sep += 1;//���˵���
			sms.body = sms.body.substring(sep);
		}
		return sms.cur;
	}
	//�����ݿ��м��ظ���Ķ�������
	//����false��ʾ������ ����true��ʾȫ����������
	public boolean loadMoreData(int getNum)
	{
		Log.d(TAG, "loadMoreData begin getNum:"+getNum+" lastLoadTime:"+lastLoadTime);
		if(getNum<=0 || isLoadCompleted) return true;
		String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" };
		String selection;
		if(lastLoadTime!=0)
		{
			selection = "address='"+addr+"' and date<='"+lastLoadTime+"'";
		}
		else
		{
			selection = "address='"+addr+"'";
		}
		Log.d(TAG, "selection="+selection);
		Cursor cur = context.getContentResolver().query(Uri.parse("content://sms/"), 
				projection,
				selection,
				null,
				"date desc limit "+getNum);
		Log.d(TAG, "getCount="+cur.getCount());
		if(cur.getCount()<getNum)
		{
			/*if(lastLoadTime!=0)
				Toast.makeText(context, "ȫ���������", Toast.LENGTH_LONG).show();*/
			isLoadCompleted = true;
		}
		if(!cur.moveToFirst())
		{
			return isLoadCompleted;
		}
		
		int index_id = cur.getColumnIndex("_id");
		int index_body = cur.getColumnIndex("body");
		int index_date = cur.getColumnIndex("date");
		Log.d(TAG, "index_id="+index_id+" index_body="+index_body+" index_date="+index_date);
		do{
			//ȥ��
			String id = cur.getString(index_id);
			String body = cur.getString(index_body);
			long date = cur.getLong(index_date);
			if(setSmsId.contains(id))
			{
				Log.d(TAG, "Contains id="+id);
				continue;
			}
			if(lastLoadTime>date || lastLoadTime ==0) lastLoadTime = date;
			
			SmsClass sms = new SmsClass(body, date);
			int index = getSmsIndex(sms);
			smsList.get(index).add(sms);//�����Ӧ�±�Ķ��ż�����
			setSmsId.add(id);
		}while(cur.moveToNext());
		Log.d(TAG, "get Data completed="+isLoadCompleted);
		return isLoadCompleted;
	}
	public void setAddr(String address)
	{
		addr = address;
	}
	public boolean needLoadData()
	{
		Log.d(TAG, "isLoadCopleted="+isLoadCompleted+" 0="+smsList.get(0).size());
		Log.d(TAG, "1="+smsList.get(1).size()+" 2="+smsList.get(2).size()+" 3="+smsList.get(3).size());
		return !isLoadCompleted && //����������� ����false �����κ�һ�������±�Ķ���Ϊ�� ���� true
				(smsList.get(0).size() < 3) &&
				((smsList.get(1).size() < 3) || 
						(smsList.get(2).size() < 3) || 
						(smsList.get(3).size() < 3));
	}
	public long diffTime(long t1, long t2)
	{
		if(t1 > t2) return t1-t2;
		else return t2-t1;
	}
	//ȡ��һ�������Ķ���
	public SmsClass getOneSms()
	{
		Log.d(TAG, "getOneSms begin");
		if(needLoadData()) 
		{
			loadMoreData(30);
			Log.d(TAG, "loadMoreData OK");
		}
		
		SmsClass full, one, two, three;
		full = smsList.get(0).peek();
		one = smsList.get(1).peek();
		two = smsList.get(2).peek();
		three = smsList.get(3).peek();
		Log.d(TAG, "full="+full+" one="+one+" two="+two+" three="+three);
		long oneTime=0, twoTime=0, threeTime=0;
		long earlyTime = 0;
		if(one != null) 
		{
			Log.d(TAG, "one.time="+one.time);
			oneTime = one.time;
			earlyTime = one.time;
		}
		if(two != null)
		{
			Log.d(TAG,"two.time="+two.time);
			twoTime = two.time;
			if(twoTime < earlyTime)	earlyTime = two.time;
		}
		if(three != null)
		{
			Log.d(TAG, "three.time="+three.time);
			threeTime = three.time;
			if(threeTime < earlyTime) earlyTime = three.time;
		}
		
		if(full != null && full.time < earlyTime) return full;

		StringBuilder sBody = new StringBuilder();//�ϲ����ɵĶ���
		long newTime = 0;
		if(one != null)
		{
			sBody.append(one.body);
			newTime = oneTime;
			smsList.get(1).poll();
		}
		if(two != null && (sBody.toString().length() == 0 || diffTime(newTime, twoTime) < 10*1000))
		{
			//���ŵĵڶ���
			sBody.append(two.body);
			smsList.get(2).poll();
		}
		if(three != null && (sBody.toString().length() == 0 || diffTime(newTime, threeTime) < 10*1000))
		{
			sBody.append(three.body);
			smsList.get(3).poll();
		}
		if(sBody.toString().length()==0) 
		{
			Log.d(TAG, "sBody equals null");
			return null;
		}
		Log.d(TAG, "sBody="+sBody);
		return new SmsClass(sBody.toString(), earlyTime);
	}
	public void init()
	{
		qBody1 = new LinkedList<SmsClass>();
		qBody2 = new LinkedList<SmsClass>();
		qBody3 = new LinkedList<SmsClass>();
		Log.d(TAG, "Sms Detail Init OK");
	} 
	/*�жϵ�ǰ�����Ƿ�Ϊ��*/
	public boolean isEmpty()
	{
		return (qBody1.peek()==null && qBody2.peek()==null && qBody3.peek()==null && curSms==null);
	}

	public boolean addSubSms(SmsClass sms)
	{
		String body = sms.body;
		long time = sms.time;
		Pattern pattern = Pattern.compile("^\\d*/\\d*");
		Matcher matcher = pattern.matcher(body);

		if(lastTime!=0 && lastTime+10000<time)
		{
			//����10s�Ķ��� ��Ϊ����һ��
			complete = true;
			curSms = sms;
			lastTime = sms.time;
			return complete;
		}
		
		if(matcher.find())
		{
			int index = body.indexOf('/');
			sms.cur = Integer.valueOf(body.substring(index-1, index)).intValue();
			sms.total = Integer.valueOf(body.substring(index+1, index+2)).intValue();
			Log.d(TAG, "cur="+sms.cur+" total="+sms.total);
			index += 2;
			if(body.charAt(index)==')') index += 1;//���˵���
			switch(sms.cur)
			{
			case 1:
				sms.body = body.substring(index);
				qBody1.add(sms);
				break;
			case 2:
				sms.body = body.substring(index);
				qBody2.add(sms);
				break;
			case 3:
				sms.body = body.substring(index);
				qBody3.add(sms);
				break;
			default:
				complete = true;
				break;
			}
			if(qBody1.peek() != null && qBody2.peek() != null && qBody3.peek() != null)
			{
				//�������ռ�����һ�����ŵ�����
				complete = true;
			}
		} 
		else 
		{
			//qBody1.add(sms);
			curSms = sms;
			complete = true;
		}
		lastTime = sms.time;
		return complete;
	}

	public boolean isComplete()
	{
		return complete;
	}
	public String getDate()
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
		return dateFormat.format(new Date(lastTime));
	}
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		SmsClass sms;
		if(qBody1.peek()!=null)
		{
			sms = qBody1.poll();
			sb.append(sms.body);
		}
		if(qBody2.peek()!=null)
		{
			sms = qBody2.poll();
			sb.append(sms.body);
		}
		if(qBody3.peek()!=null)
		{
			sms = qBody3.poll();
			sb.append(sms.body);
		}
		if(sb.length()==0&&curSms!=null)
		{
			sb.append(curSms.body);
			curSms = null;
		}
		complete = false;
		return sb.toString();
	}
}
