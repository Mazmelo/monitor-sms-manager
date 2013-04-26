package com.yaoli.smsread;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TimeUtil {
	private long longTime;
	private String yearMonth;
	public TimeUtil(long lTime)
	{
		longTime = lTime;
	}
    //这是Utf8
	public String toLabel()
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd", Locale.US);
		Date d = new Date(longTime);
		yearMonth = dateFormat.format(d);
		Date today = new Date(System.currentTimeMillis());
		if(yearMonth.equals(dateFormat.format(today)))
		{
			dateFormat = new SimpleDateFormat("hh:mm", Locale.US);
			return dateFormat.format(d);
		}
		return yearMonth; 
	}
	public String toDate()
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
		return dateFormat.format(new Date(longTime));
	}
}
