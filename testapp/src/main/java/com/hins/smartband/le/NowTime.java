package com.hins.smartband.le;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NowTime {
    private int year; // 年
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;
    private Calendar calendar;

    public NowTime(){
        super();
        calendar=Calendar.getInstance();
        year = calendar.get(Calendar.YEAR); // 获取当前年份
        month = calendar.get(Calendar.MONTH)+1; // 获取当前月份
        day = calendar.get(Calendar.DAY_OF_MONTH); // 获取当前日
        hour = calendar.get(Calendar.HOUR_OF_DAY); // 获取当前小时数
        minute = calendar.get(Calendar.MINUTE); // 获取当前分钟数
        second = calendar.get(Calendar.SECOND);
    }
    /**
     * 发给设备调用的方法
     * */
    public String SendnowTime(){
        if(hour<10){//小于10小时
            if(minute<10){//小于10分钟
                return "0"+hour+"0"+minute;
            }else{//大于10分钟
                return "0"+hour+""+minute;
            }
        }else{//大于10小时
            if(minute<10){//小于10分钟
                return hour+"0"+minute;
            }else{//大于10分钟
                return hour+""+minute;
            }
        }
    }
    /**
     * 保存数据调用时间
     * */
    public String nowTime(){
        String[] time={year+"",month+"",day+"",hour+"",minute+"",second+""};
//        String yearStr=year+"";
//        String monthStr=month+"";
//        String dayStr=day+"";
//        String hourStr=hour+"";
//        String minuteStr=minute+"";
//        String secondStr=second+"";
        for(int i=0;i<time.length;i++){
            if (time[i].length()<2){
                time[i]=("0"+time[i]);
            }
        }

        return time[0]+"-"+time[1]+"-"+time[2]+" "+time[3]+":"+time[4]+":"+time[5];
    }

    public int getHour(){
        return calendar.get(Calendar.HOUR_OF_DAY);// 获取当前小时数
    }


}
