package com.hins.smartband.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.hins.smartband.adapter.MeasureAdapter;
import com.hins.smartband.bean.IconLeftMidRightListBean;
import com.hins.smartband.bean.IconLeftRightListBean;
import com.hins.smartband.bean.UserInfoBean;
import com.hins.smartband.model.Tb_space_m;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SpaceDAO {
    private DBOpenHelper helper;
    private SQLiteDatabase db;
    private Context context;
    private Handler handler;

    public SpaceDAO(Context context) {
        this.context=context;
        helper = new DBOpenHelper(context);
    }

    public SpaceDAO(Context context, Handler handler) {
        this.context=context;
        helper = new DBOpenHelper(context);
        this.handler=handler;
    }

    public void add(Tb_space_m tb_space_m) {
        try {
            if (db == null) db = helper.getWritableDatabase();
            if (db != null) {
                db.execSQL("insert into tb_space(temperature,heart,blood,state,running,sleep,makeup,danger,nowtime) values(?,?,?,?,?,?,?,?,?)"
                        , new Object[]{tb_space_m.getTemperature(), tb_space_m.getHeart(), tb_space_m.getBlood()
                                , tb_space_m.getState(), tb_space_m.getRunning(), tb_space_m.getSleep(), tb_space_m.getMakeup(), tb_space_m.getDanger(), tb_space_m.getNowtime()});
            }
            Log.d("Hins", "已保存心率数据");
        } catch (SQLiteException e) {
            Log.w("SQL", "add error " + e.getMessage());
        }
    }

    public Tb_space_m getLastData() {
        db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from tb_space order by datetime(nowtime) desc", null);
        if (cursor.moveToNext()) {
            return new Tb_space_m(
                    cursor.getString(cursor.getColumnIndex("temperature")),
                    cursor.getString(cursor.getColumnIndex("heart")),
                    cursor.getString(cursor.getColumnIndex("blood")),
                    cursor.getString(cursor.getColumnIndex("state")),
                    cursor.getString(cursor.getColumnIndex("running")),
                    cursor.getString(cursor.getColumnIndex("sleep")),
                    cursor.getString(cursor.getColumnIndex("makeup")),
                    cursor.getString(cursor.getColumnIndex("danger")),
                    cursor.getString(cursor.getColumnIndex("nowtime"))
            );
        }
        return null;
    }

    public ArrayList<Tb_space_m> getAllData() {
        db = helper.getWritableDatabase();
        ArrayList<Tb_space_m> datas = new ArrayList<Tb_space_m>();
        Cursor cursor = db.rawQuery("select * from tb_space", null);
        while (cursor.moveToNext()) {
            datas.add(
                    new Tb_space_m(cursor.getString(cursor.getColumnIndex("temperature")),
                            cursor.getString(cursor.getColumnIndex("heart")),
                            cursor.getString(cursor.getColumnIndex("blood")),
                            cursor.getString(cursor.getColumnIndex("state")),
                            cursor.getString(cursor.getColumnIndex("running")),
                            cursor.getString(cursor.getColumnIndex("sleep")),
                            cursor.getString(cursor.getColumnIndex("makeup")),
                            cursor.getString(cursor.getColumnIndex("danger")),
                            cursor.getString(cursor.getColumnIndex("nowtime"))
                    )
            );
        }
        return datas;
    }

    public ArrayList<IconLeftMidRightListBean> getAllHeartData() {
        db = helper.getWritableDatabase();
        ArrayList<IconLeftMidRightListBean> datas = new ArrayList<IconLeftMidRightListBean>();
        Cursor cursor = db.rawQuery("select * from tb_space order by datetime(nowtime) desc", null);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                datas.add(
                        new IconLeftMidRightListBean(
                                cursor.getString(cursor.getColumnIndex("nowtime")),
                                cursor.getString(cursor.getColumnIndex("heart")) + "次/分钟",
                                cursor.getString(cursor.getColumnIndex("blood")) + "%",
                                cursor.getString(cursor.getColumnIndex("temperature")) + "°C"
                        )
                );
            }
        } else {
            getDateFromBmob(datas);
        }
        return datas;
    }

    public IconLeftMidRightListBean getLastHeartData() {
        db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from tb_space order by datetime(nowtime) desc", null);
        if (cursor.getCount() != 0) {
            if (cursor.moveToNext()) {
                return
                        new IconLeftMidRightListBean(
                                cursor.getString(cursor.getColumnIndex("nowtime")),
                                cursor.getString(cursor.getColumnIndex("heart")) + "次/分钟",
                                cursor.getString(cursor.getColumnIndex("blood")) + "%",
                                cursor.getString(cursor.getColumnIndex("temperature")) + "°C"
                        )
                ;
            }
        }
        return null;
    }

    public void getDateFromBmob(final ArrayList<IconLeftMidRightListBean> datas) {
        BmobQuery<Tb_space_m> query = new BmobQuery<Tb_space_m>();
        UserInfoBean user = BmobUser.getCurrentUser(UserInfoBean.class);
        //查询playerName叫“比目”的数据
        query.addWhereEqualTo("uObjectId", user.getObjectId());
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(30);
        //执行查询方法
        query.order("-nowtime");
        query.findObjects(new FindListener<Tb_space_m>() {
            @Override
            public void done(List<Tb_space_m> object, BmobException e) {
                if (e == null) {
                    Log.d("bmob", "查询bomb成功：共" + object.size() + "条数据。");
                    if (object.size() > 0) {
                        for (Tb_space_m gameScore : object) {
                            add(gameScore);
                            datas.add(
                                    new IconLeftMidRightListBean(
                                            gameScore.getNowtime(),
                                            gameScore.getHeart() + "次/分钟",
                                            gameScore.getBlood() + "%",
                                            gameScore.getTemperature() + "°C")
                            );
                        }
                        Message msg=handler.obtainMessage();
                        msg.what=0x11;
                        handler.sendMessage(msg);
                    }
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    public void close() {
        db.close();
    }

    public Tb_space_m getLastStepDate() {
        db = helper.getWritableDatabase();
        Tb_space_m datas = new Tb_space_m();
        Cursor cursor = db.rawQuery("select * from tb_space order by datetime(nowtime) desc limit 0,1", null);
        if (cursor.moveToNext()) {
            return new Tb_space_m(cursor.getString(cursor.getColumnIndex("temperature")),
                    cursor.getString(cursor.getColumnIndex("heart")),
                    cursor.getString(cursor.getColumnIndex("blood")),
                    cursor.getString(cursor.getColumnIndex("state")),
                    cursor.getString(cursor.getColumnIndex("running")),
                    cursor.getString(cursor.getColumnIndex("sleep")),
                    cursor.getString(cursor.getColumnIndex("makeup")),
                    cursor.getString(cursor.getColumnIndex("danger")),
                    cursor.getString(cursor.getColumnIndex("nowtime"))
            );
        }
        return new Tb_space_m("0",
                "0",
                "0",
                "0",
                "0",
                "12:00",
                "08:00",
                "0",
                "0"
        );
    }

    public String get_nowtime() {
        return get_only_data("nowtime");
    }

    public String get_temperature() {
        return get_only_data("temperature");
    }

    public String get_heart() {
        return get_only_data("heart");
    }

    public String get_blood() {
        return get_only_data("blood");
    }

    public String get_state() {
        return get_only_data("state");
    }

    public String get_running() {
        return get_only_data("running");
    }

    public String get_sleep() {
        return get_only_data("sleep");
    }

    public String get_makeup() {
        return get_only_data("makeup");
    }

    public String get_danger() {
        return get_only_data("danger");
    }

    public String get_only_data(String dataname) {
        db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from tb_space", null);
        if (dataname == "nowtime") {
            return cursor.getString(cursor.getColumnIndex("nowtime"));
        } else if (dataname == "temperature") {
            return cursor.getString(cursor.getColumnIndex("temperature"));
        } else if (dataname == "heart") {
            return cursor.getString(cursor.getColumnIndex("heart"));
        } else if (dataname == "blood") {
            return cursor.getString(cursor.getColumnIndex("blood"));
        } else if (dataname == "state") {
            return cursor.getString(cursor.getColumnIndex("state"));
        } else if (dataname == "running") {
            return cursor.getString(cursor.getColumnIndex("running"));
        } else if (dataname == "sleep") {
            return cursor.getString(cursor.getColumnIndex("sleep"));
        } else if (dataname == "makeup") {
            return cursor.getString(cursor.getColumnIndex("makeup"));
        } else if (dataname == "danger") {
            return cursor.getString(cursor.getColumnIndex("danger"));
        }
        return null;
    }

    public long getCount() {
        db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select count(nowtime) from tb_space", null);
        if (cursor.moveToNext()) {
            return cursor.getLong(0);
        }
        return 0;
    }

}
