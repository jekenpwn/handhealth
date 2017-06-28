package com.hins.smartband.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hins.smartband.activities.LoginOrSignActivity;

import java.util.LinkedList;

/**
 * 程序员： Hins on 2016/7/22.
 * 描述：实现完全退出和注销登录的功能
 */
public class BaseActivity extends AppCompatActivity {

    public static LinkedList<Activity> activities = new LinkedList<Activity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       activities.add(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activities.remove(this);
    }

    public static void logout(Context context){
        for (Activity activity : activities) {
            activity.finish();
        }
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void finishAll(){
        for (Activity activity : activities) {
            activity.finish();
        }
        activities.clear();
        //这个主要是用来关闭进程的, 光把所有activity finish的话，进程是不会关闭的
        System.exit(0);
//      android.os.Process.killProcess(android.os.Process.myPid());
    }

}
