package com.xiaoqu.bike.utils;

import java.sql.Date;

public class Date2Data {
	public static Date getSqlDate(){
		java.util.Date nowDate=new java.util.Date();
		Date datetime=new Date(nowDate.getTime());
		return datetime;
	}
}
