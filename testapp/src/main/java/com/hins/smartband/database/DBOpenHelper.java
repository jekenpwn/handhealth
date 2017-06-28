package com.hins.smartband.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper{
	private static final int VERSION=1;
	private static final String DBname="space.db";
	public DBOpenHelper(Context context){
		super(context,DBname,null,VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table tb_space ("
				+ "temperature varchar(30)"//温度
				+ ",heart varchar(30)"//心率数据
				+ ",blood varchar(30)"//血氧
				+ ",state varchar(30)"//身体状态
				+ ",running varchar(30)"//步数
				+ ",sleep varchar(30)"//睡觉时间
				+ ",makeup varchar(30)"//起床
				+ ",danger varchar(30)"//危险标志
				+ ",nowtime varchar(30))");//时间
		db.execSQL("create table tb_device (name varchar(20) ,address varchar(20))");//保存扫描到的设备，扫描前要清空一次
		db.execSQL("create table tb_bind_device(nameone varchar(20),addressone varchar(20),addresstwo varchar(20))");//将两个对应的蓝牙绑定

		//Hins
		db.execSQL("create table tb_aim ("
				+ "objectId varchar(20)"
				+ ",uObjectId varchar(20)"
				+ ",aim_step varchar(20)"
				+ ",aim_kmile varchar(20)"
				+ ",aim_sleep varchar(30)"
				+ ",tip varchar(10))");//时间

	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 自动生成的方法存根

	}



}
