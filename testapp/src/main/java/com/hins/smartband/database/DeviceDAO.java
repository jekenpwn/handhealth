package com.hins.smartband.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import com.hins.smartband.model.Tb_device_m;

/**
 * 这个类是专门保存扫描到的设备的操作类
 * */
public class DeviceDAO {
	private DBOpenHelper helper;
	private SQLiteDatabase db;
	private Context context;
	public DeviceDAO(Context context){
		this.context=context;
		helper=new DBOpenHelper(context);
	}
	//插入数据
	public void add(Tb_device_m tb_device){
		try{
			if(db==null){
				db=helper.getWritableDatabase();
			}
			if(db!=null){
				db.execSQL("insert into tb_device(name,address) values(?,?)",new Object[]{tb_device.getName(),tb_device.getAddress()});
			}
			Log.d("Hins","已保存设备"+tb_device.getName());
		}catch(SQLiteException e){
			Log.w("SQL","add error "+e.getMessage());
		}
	}
	//获得总记录数
	public long getCount(){
		db=helper.getWritableDatabase();
		Cursor cursor=db.rawQuery("select count(name) from tb_device",null);
		if(cursor.moveToNext()){
			return cursor.getLong(0);
		}else{
			return 0;
		}
	}
	//判断设备是否已经存在
	public boolean isExist(String deviceName){
		db=helper.getWritableDatabase();
		Cursor cursor=db.rawQuery("select name from tb_device where name=?", new String[]{deviceName});
		if(cursor.moveToNext()){
			return true;
		}
		return false;
	}
	//查找数据
	public Tb_device_m find(String deviceName){
		db=helper.getWritableDatabase();
		Cursor cursor=db.rawQuery("select name,address from tb_device where name=?", new String[]{deviceName});
		if(cursor.moveToNext()){
			return new Tb_device_m(cursor.getString(cursor.getColumnIndex("name")),
					cursor.getString(cursor.getColumnIndex("address")));
		}
		return null;
	}

	public void clearData(){
		db=helper.getWritableDatabase();
		db.execSQL("delete from tb_device");
	}
	//关闭数据库
	public void close(){
		if(helper!=null){
			helper.close();
		}
	}
}
