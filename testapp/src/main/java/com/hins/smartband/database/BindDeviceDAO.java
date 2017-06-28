package com.hins.smartband.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.hins.smartband.model.Tb_bind_device_m;

/**
 * 这个类是专门保存两个蓝牙设备绑定在一起的的操作类，进行蓝牙转换连接操作
 * */
public class BindDeviceDAO {
	private DBOpenHelper helper;
	private SQLiteDatabase db;
	public BindDeviceDAO(Context context){
		helper=new DBOpenHelper(context);
	}
	public void add(Tb_bind_device_m tb_bind_device){
		try{
			if(db==null)db=helper.getWritableDatabase();
			if(db!=null)
			{
				db.execSQL("insert into tb_bind_device(nameone,addressone,addresstwo) values(?,?,?)"
						,new Object[]{tb_bind_device.getNameOne(),tb_bind_device.getAddressOne(),tb_bind_device.getAddressTwo()});
			}
		}catch(SQLiteException e){
			Log.w("SQL","add error "+e.getMessage());
		}
	}

	//查找数据
	public Tb_bind_device_m find(){
		db=helper.getWritableDatabase();
		Cursor cursor=db.rawQuery("select * from tb_bind_device", null);
		if(cursor.moveToNext()){
			return new Tb_bind_device_m(
					cursor.getString(cursor.getColumnIndex("nameone")),
					cursor.getString(cursor.getColumnIndex("addressone")),
					cursor.getString(cursor.getColumnIndex("addresstwo")));
		}
		return null;
	}

	public String get_addresstwo(String nameone){
		db=helper.getWritableDatabase();
		Cursor cursor=db.rawQuery("select nameone,addresstwo from tb_bind_device where nameone=?", new String[]{nameone});
		if(cursor.moveToNext()){
			return cursor.getString(cursor.getColumnIndex("addresstwo"));
		}
		return null;
	}
	//获得总记录数
	public long getCount(){
		db=helper.getWritableDatabase();
		Cursor cursor=db.rawQuery("select count(nameone) from tb_bind_device",null);
		if(cursor.moveToNext()){
			return cursor.getLong(0);
		}else{
			return 0;
		}
	}





}
