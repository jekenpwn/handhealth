package com.hins.smartband.tools;

import android.util.Log;
import android.widget.Toast;

/**
 * Created by Hins on 2016/12/3.
 */

public class DealTime {

    public static int toSecond(String time){
        int a=Integer.parseInt(time.substring(0,time.indexOf("小")));
        if (time.contains("分钟")){
            int b=Integer.parseInt(time.substring(time.indexOf("时")+1,time.indexOf("分")));
            return (a-6)*60+b;
        }else {
            return (a-6)*60;
        }
    }

    public static String toTime(int time){
        int a=(time/60)+6;
        int b=time%60;
        StringBuilder str=new StringBuilder();
        str.append(a);
        str.append("小时");
        if (b!=0){
            str.append(b);
            str.append("分钟");
        }
        return str.toString();
    }
}
