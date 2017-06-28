package com.hins.smartband.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Message;
import android.util.Log;

import com.hins.smartband.bean.IconLeftMidRightListBean;
import com.hins.smartband.bean.UserInfoBean;
import com.hins.smartband.model.Tb_aim_m;
import com.hins.smartband.model.Tb_space_m;
import com.hins.smartband.tools.GetAimStep;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * 这个类是专门保存扫描到的设备的操作类
 * */
public class AimDAO {

	private DBOpenHelper helper;
	private SQLiteDatabase db;
	private Context context;

	public AimDAO(Context context){
		this.context=context;
		helper=new DBOpenHelper(context);
	}

	//插入数据
	public void add(Tb_aim_m tb_aim){
		try{
			if(db==null){
				db=helper.getWritableDatabase();
			}
			if(db!=null){
				db.execSQL("insert into tb_aim(objectId,uObjectId,aim_step,aim_kmile,aim_sleep,tip) values(?,?,?,?,?,?)",
						new Object[]{tb_aim.getObjectId(),tb_aim.getUObjectId(),tb_aim.getStep(),tb_aim.getKmile(),tb_aim.getSleep(),tb_aim.getTip()});
			}
		}catch(SQLiteException e){
			e.printStackTrace();
		}
	}

	//插入数据
	public void update(Tb_aim_m tb_aim){
		try{
			if(db==null){
				db=helper.getWritableDatabase();
			}
			if(db!=null){
				db.execSQL("update tb_aim set objectId=?,aim_step=?,aim_kmile=?,aim_sleep=?,tip=? where uObjectId=?",
						new Object[]{tb_aim.getObjectId(),tb_aim.getStep(),tb_aim.getKmile(),tb_aim.getSleep(),tb_aim.getTip(),tb_aim.getUObjectId()});
			}
		}catch(SQLiteException e){
			e.printStackTrace();
		}
	}

	//获得总记录数
	public long getCount(){
		db=helper.getWritableDatabase();
		Cursor cursor=db.rawQuery("select count(step) from tb_aim",null);
		if(cursor.moveToNext()){
			return cursor.getLong(0);
		}else{
			return 0;
		}
	}

	//查找数据
	public Tb_aim_m find(String deviceName){
		db=helper.getWritableDatabase();
		Cursor cursor=db.rawQuery("select * from tb_aim where uObjectId=?", new String[]{deviceName});
		if(cursor.moveToNext()){
			return new Tb_aim_m(
					cursor.getString(cursor.getColumnIndex("objectId")),
					cursor.getString(cursor.getColumnIndex("uObjectId")),
					cursor.getString(cursor.getColumnIndex("aim_step")),
					cursor.getString(cursor.getColumnIndex("aim_kmile")),
					cursor.getString(cursor.getColumnIndex("aim_sleep")),
					cursor.getString(cursor.getColumnIndex("tip")));
		}
		return null;
	}

	public Tb_aim_m find(String deviceName,UserInfoBean user){
		db=helper.getWritableDatabase();
		getAimFromBmob(user);
		Cursor cursor=db.rawQuery("select * from tb_aim where uObjectId=?", new String[]{deviceName});
		if(cursor.moveToNext()){
			return new Tb_aim_m(
					cursor.getString(cursor.getColumnIndex("objectId")),
					cursor.getString(cursor.getColumnIndex("uObjectId")),
					cursor.getString(cursor.getColumnIndex("aim_step")),
					cursor.getString(cursor.getColumnIndex("aim_kmile")),
					cursor.getString(cursor.getColumnIndex("aim_sleep")),
					cursor.getString(cursor.getColumnIndex("tip")));
		}else {
			String stepStr=new GetAimStep(user.getSex(),user.getBirth(),user.getHeight(),user.getWeight()).getStep();
			String kmileStr=new GetAimStep(user.getSex(),user.getBirth(),user.getHeight(),user.getWeight()).getKmile();
			final Tb_aim_m tb_aim_m=new Tb_aim_m(user.getObjectId(),stepStr,kmileStr,"8小时","false");
			tb_aim_m.save(new SaveListener<String>() {
				@Override
				public void done(String s, BmobException e) {
					if (e==null){
						Log.d("bmob", "上传aim数据成功");
					}else {
						Log.d("bmob", "上传aim数据失败");
					}
				}
			});
			return tb_aim_m;
		}
	}

	public void getAimFromBmob(final UserInfoBean user) {
		BmobQuery<Tb_aim_m> query = new BmobQuery<Tb_aim_m>();
		//查询playerName叫“比目”的数据
		query.addWhereEqualTo("uObjectId", user.getObjectId());
		query.setLimit(1);
		//执行查询方法
		query.findObjects(new FindListener<Tb_aim_m>() {
			@Override
			public void done(List<Tb_aim_m> object, BmobException e) {
				if (e == null) {
					Log.d("bmob", "查询bomb成功：共" + object.size() + "条数据。");
					if (object.size() > 0) {
						for (Tb_aim_m gameScore : object) {
							update(gameScore);
						}
					}else {
						Tb_aim_m tb_aim_m=new Tb_aim_m(user.getObjectId(),"7000","10.00","8小时","false");
						add(tb_aim_m);
					}
				} else {
					Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
				}
			}
		});
	}

	public void clearData(){
		db=helper.getWritableDatabase();
		db.execSQL("delete from tb_aim");
	}

	//关闭数据库
	public void close(){
		if(helper!=null){
			helper.close();
		}
	}
}
