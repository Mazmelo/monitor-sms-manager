package com.yaoli.smsread;

import java.sql.Date;

public class SmsClass {
	public SmsClass(String _body, long _time)
	{
		body = _body;
		time = _time;
         read = false;
	}
    public SmsClass(String _body, long _time, boolean _read)
    {
        body = _body;
        time = _time;
        read = _read;
    }
	String id;//短信的id
	String addr;//短信的号码
	String body;//短信的内容
	long time;//短信的时间 ms
	Date date;//短信的日期表示
	int cur,total;//这条短信是其中的cur段 总共有total段
    boolean read;//这条短信是否已读 true表示已读 false表示未读

}
