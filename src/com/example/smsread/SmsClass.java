package com.example.smsread;

import java.sql.Date;

public class SmsClass {
	public SmsClass(String _body, long _time)
	{
		body = _body;
		time = _time;
	}
	String body;//���ŵ�����
	long time;//���ŵ�ʱ�� ms
	Date date;//���ŵ����ڱ�ʾ
	int cur,total;//�������������е�cur�� �ܹ���total��
	
}
