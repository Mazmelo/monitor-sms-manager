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
	long lastLoadTime;//已加载短信中的最后一条的时间 用于分页加载数据
	boolean complete;//是否已经收集齐一条完整的短信了
	SmsClass curSms;//当前未处理的短信
	ArrayList<Queue<SmsClass>> smsList;//消息队列的数组 数组下标表示段数
	ArrayList<SmsClass> fullSmsList;//完整的短信集合
	Queue<SmsClass> qBody1;//保存消息的第一段
	Queue<SmsClass> qBody2;//保存消息的第二段
	Queue<SmsClass> qBody3;//保存消息的第三段
	String sbody;
	Context context;
	String addr;//当前联系人的号码
	boolean hasAddr;
	HashSet<String> setSmsId;//当前联系人所有短信的id集合
	boolean isLoadCompleted;//全部加载完？
	int curNum;//当前已取回的短信条数
	private static final String TAG = "SmsDetailClass";
	private static final int maxSegNum=10;
	public SmsDetail(Context con)
	{
		curSms = null;
		lastTime = 0;
		complete = false;
		smsList = new ArrayList<Queue<SmsClass>>();
		for(int i=0;i<maxSegNum;i++)
		{
			smsList.add(new LinkedList<SmsClass>()); 
		}
		setSmsId = new HashSet<String>();
		context  = con;
		addr = "";
		hasAddr = false;
		Log.d(TAG, "Sms Detail Class construct Ok");
	}
	//取得一条短信的下标，如果未分段，则返回0，否则返回该短信在合并后完整短信中的下标
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
			Log.d(TAG, "cur="+sms.cur+" total="+sms.total+" time="+sms.time+" body="+sms.body);
			sep += 2;
			if(sms.body.charAt(sep)==')') sep += 1;//过滤掉）
			sms.body = sms.body.substring(sep);
		}
		return sms.cur;
	}
    static public int getUnReadCount(Context context, String addr)
    {
        String[] projection = new String[] { "_id", "read", "type"};
        String selection = "read=0 and type=1 and address='"+addr +"'";
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(Uri.parse("content://sms/"),
                    projection,
                    selection,
                    null,
                    null);
        } catch(Exception e){
            e.printStackTrace();
        } finally {
           cursor.close();
        }
        return cursor.getCount();
    }
	//从数据库中加载更多的短信数据
	//返回false表示有数据 返回true表示全部加载完了
	public boolean loadMoreData(int getNum)
	{
		Log.d(TAG, "loadMoreData begin getNum:"+getNum+" lastLoadTime:"+lastLoadTime);
		if(getNum<=0 || isLoadCompleted) return true;
		String[] projection = new String[] { "_id", /*"address", "person", */"body", "date", "read", "type" };
		String selection="";
		if(hasAddr)
		{
			selection = "address='" + addr +"' ";
			if(lastLoadTime != 0)
			{
				selection += " and date <='" + lastLoadTime + "'";
			}
		}
		else
		{
			if(lastLoadTime!=0)
			{
				selection += "date <= '"+lastLoadTime+"'";
			}
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
				Toast.makeText(context, "全部加载完毕", Toast.LENGTH_LONG).show();*/
			isLoadCompleted = true;
		}
		if(!cur.moveToFirst())
		{
			return isLoadCompleted;
		}
		
		int index_id = cur.getColumnIndex("_id");
		int index_body = cur.getColumnIndex("body");
		int index_date = cur.getColumnIndex("date");
        int index_read = cur.getColumnIndex("read");
        int index_type = cur.getColumnIndex("type");
		Log.d(TAG, "index_id="+index_id+" index_body="+index_body+" index_date="+index_date);
		do{
			//去重
			String id = cur.getString(index_id);
			String body = cur.getString(index_body);
			long date = cur.getLong(index_date);
            int read = cur.getInt(index_read);//0:not read 1:read default is 0
			if(setSmsId.contains(id))
			{
				Log.d(TAG, "Contains id="+id);
				continue;
			}
			if(lastLoadTime>date || lastLoadTime ==0) lastLoadTime = date;
			
			SmsClass sms = new SmsClass(body, date, read==1);
			int index = getSmsIndex(sms);
			smsList.get(index).add(sms);//加入对应下标的短信集合中
			setSmsId.add(id);
		}while(cur.moveToNext());
		Log.d(TAG, "get Data completed="+isLoadCompleted);
		return isLoadCompleted;
	}
	public void setAddr(String address)
	{
		addr = address;
		hasAddr = true;
	}
	public boolean needLoadData()
	{
		Log.d(TAG, "isLoadCopleted="+isLoadCompleted+" 0="+smsList.get(0).size());
		Log.d(TAG, "1="+smsList.get(1).size()+" 2="+smsList.get(2).size()+" 3="+smsList.get(3).size());
		int segNum = 0;
		for(int i=1;i<maxSegNum;i++)
		{
			segNum += smsList.get(i).size();
		}
		return !isLoadCompleted && //如果加载完了 返回false 否则任何一个短信下标的队列为空 返回 true
				(smsList.get(0).size() < 3) &&
				(segNum < 10);
	}
	public long diffTime(long t1, long t2)
	{
		if(t1 > t2) return t1-t2;
		else return t2-t1;
	}
	//取得一条完整的短信
	public SmsClass getOneSms()
	{
		if(needLoadData()) 
		{
			loadMoreData(30);
			Log.d(TAG, "loadMoreData OK");
		}
		ArrayList<SmsClass> smsSeg = new ArrayList<SmsClass>();
		long earlyTime = 0;
        boolean isRead = true;//默认为已读
		for(int i=0;i<maxSegNum;i++)
		{
			SmsClass seg =smsList.get(i).peek(); 
			if(seg != null && seg.time > earlyTime)
			{
				//优先返回时间值大的
				earlyTime = seg.time;
			}
			smsSeg.add(seg);
		}
		//返回一条完整的短信
		if(smsSeg.get(0) != null && smsSeg.get(0).time == earlyTime)
		{
			smsList.get(0).poll();
			return smsSeg.get(0);
		}
		StringBuilder sBody = new StringBuilder();//合并而成的短信
		//开始拼接
		for(int i=1;i<maxSegNum;i++)
		{
			SmsClass sms = smsSeg.get(i);
			if(smsSeg.get(i) != null)
			{
				sBody.append(sms.body);
				Log.d(TAG, "i="+i+" time="+sms.time+" "+sms.cur+"/"+sms.total+" "+sms.body);
				smsList.get(i).poll();
                if(sms.read==false) isRead = false;//只要有一段短信是未读的 就认为整条是未读的
				if(i == sms.total) break;//拼接已完成
			}
		}
		if(sBody.toString().length()==0) 
		{
			Log.d(TAG, "sBody equals null");
			return null;
		}
		//Log.d(TAG, "sBody="+sBody);
		return new SmsClass(sBody.toString(), earlyTime, isRead);
	}
	public void init()
	{
		qBody1 = new LinkedList<SmsClass>();
		qBody2 = new LinkedList<SmsClass>();
		qBody3 = new LinkedList<SmsClass>();
		Log.d(TAG, "Sms Detail Init OK");
	} 
	/*判断当前短信是否为空*/
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
			//隔了10s的短信 认为是另一条
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
			if(body.charAt(index)==')') index += 1;//过滤掉）
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
				//表明已收集齐了一条短信的三段
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
