package com.hins.smartband.tools;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Hins on 2016/12/3.
 */

public class GetAimStep {
    private String sex;
    private String birth;
    private int age;
    private int height;
    private int weight;

    public GetAimStep(String sex,String birth,Integer height,Integer weight){
        this.sex=sex;
        this.birth=birth;
        this.height=height;
        this.weight=weight;
        this.age=getAge();
    }

    public static float[][] manAvenage = {
            {46.5f, 48.0f, 49.1f, 50.3f, 51.1f, 52.0f, 52.4f, 52.4f},
            {47.3f, 49.0f, 50.1f, 51.2f, 52.0f, 53.2f, 53.4f, 53.4f},
            {48.2f, 50.0f, 51.3f, 52.1f, 52.8f, 54.1f, 54.5f, 54.5f},
            {49.4f, 51.0f, 52.3f, 53.1f, 53.9f, 55.4f, 55.7f, 55.7f},
            {50.5f, 52.1f, 53.3f, 54.3f, 55.2f, 56.6f, 57.0f, 57.0f},
            {51.7f, 53.3f, 54.5f, 55.5f, 56.6f, 58.0f, 58.5f, 58.5f},
            {53.0f, 54.5f, 55.6f, 56.9f, 58.1f, 59.4f, 60.0f, 60.6f},
            {54.7f, 55.9f, 56.9f, 58.4f, 59.5f, 60.9f, 61.5f, 61.5f},
            {55.4f, 57.3f, 58.4f, 59.8f, 61.0f, 62.6f, 63.1f, 63.1f},
            {56.8f, 58.8f, 59.9f, 61.3f, 62.5f, 63.4f, 64.6f, 64.6f},
            {58.2f, 60.2f, 61.3f, 62.8f, 64.1f, 65.9f, 66.3f, 66.3f},
            {59.5f, 61.7f, 62.9f, 64.5f, 65.9f, 67.7f, 68.4f, 68.4f},
            {61.4f, 63.3f, 64.6f, 66.5f, 67.7f, 69.5f, 70.4f, 70.5f},
            {63.1f, 64.9f, 66.4f, 68.4f, 69.7f, 71.3f, 72.3f, 72.6f},
            {65.0f, 66.6f, 68.4f, 70.4f, 71.8f, 73.2f, 74.4f, 74.7f},
            {66.5f, 68.3f, 70.4f, 72.7f, 74.0f, 75.2f, 77.1f, 77.4f}
    };

    public static float[][] womanAvenage = {
            {44.0f, 45.5f, 46.6f, 47.8f, 48.6f, 49.5f, 49.9f, 49.9f},
            {44.8f, 46.5f, 47.6f, 48.7f, 49.5f, 50.7f, 50.9f, 50.9f},
            {45.7f, 47.5f, 48.8f, 49.6f, 50.3f, 51.6f, 52.0f, 52.0f},
            {46.9f, 48.5f, 49.8f, 50.6f, 51.4f, 52.9f, 53.2f, 53.2f},
            {48.0f, 49.6f, 50.8f, 51.8f, 52.7f, 54.1f, 54.5f, 54.5f},
            {49.2f, 50.8f, 52.0f, 53.0f, 54.1f, 55.5f, 56.0f, 56.0f},
            {50.5f, 52.0f, 53.1f, 54.4f, 55.6f, 56.9f, 57.5f, 57.5f},
            {51.6f, 53.4f, 54.4f, 55.9f, 57.0f, 58.4f, 59.0f, 59.0f},
            {52.9f, 54.8f, 55.9f, 57.3f, 58.5f, 60.1f, 60.6f, 60.6f},
            {54.3f, 56.3f, 57.4f, 58.8f, 60.3f, 61.6f, 62.1f, 62.1f},
            {55.7f, 56.3f, 57.4f, 58.8f, 60.0f, 61.6f, 62.1f, 62.1f},
            {57.0f, 59.2f, 60.4f, 62.0f, 63.4f, 65.2f, 65.9f, 65.9f},
            {58.9f, 60.8f, 62.1f, 64.0f, 65.2f, 67.0f, 67.9f, 68.0f},
            {60.6f, 62.4f, 63.9f, 65.9f, 67.2f, 68.8f, 69.8f, 70.1f},
            {62.5f, 64.1f, 65.9f, 67.9f, 69.3f, 70.7f, 71.9f, 72.5f},
            {64.0f, 65.8f, 67.9f, 70.2f, 71.5f, 72.7f, 74.6f, 74.9f}
    };

    public String getStep(){
        int ageIndex=getAgeIndex();
        int heightIndex=getHeightIndex();
        float aWeight=0;
        if (sex.equals("男")){
            aWeight=manAvenage[heightIndex][ageIndex];
        }else {
            aWeight=womanAvenage[heightIndex][ageIndex];
        }
        float sWeight=Math.abs((weight-aWeight)*2);
        int step= (int) ((9625*sWeight-15000)/6);
        String stepStr=String.valueOf(step);
        return stepStr;
    }

    public String getKmile(){
        int ageIndex=getAgeIndex();
        int heightIndex=getHeightIndex();
        float aWeight=0;
        if (sex.equals("男")){
            aWeight=manAvenage[heightIndex][ageIndex];
        }else {
            aWeight=womanAvenage[heightIndex][ageIndex];
        }
        float sWeight=Math.abs((weight-aWeight)*2);
        Double kmile= (((9625*sWeight-15000)*0.75)/6000);
        DecimalFormat dt = (DecimalFormat) DecimalFormat.getInstance(); //获得格式化类对象
        dt.applyPattern("0.00");//设置小数点位数(两位) 余下的会四舍五入
        String kmileStr=String.valueOf(dt.format(kmile));
        return kmileStr;
    }

    private int getAgeIndex() {
        if (age<20){
            return 0;
        }else if (20<=age&&age<25){
            return 1;
        }else if (25<=age&&age<30){
            return 2;
        }else if (30<=age&&age<35){
            return 3;
        }else if (35<=age&&age<40){
            return 4;
        }else if (40<=age&&age<45){
            return 5;
        }else if (45<=age&&age<50){
            return 6;
        }else if (50<=age){
            return 7;
        }
        return 0;
    }

    private int getHeightIndex() {
        if (height<155){
            return 0;
        }else if (155<=height&&height<157){
            return 1;
        }else if (157<=height&&height<159){
            return 2;
        }else if (161<=height&&height<163){
            return 3;
        }else if (163<=height&&height<165){
            return 4;
        }else if (165<=height&&height<167){
            return 5;
        }else if (167<=height&&height<169) {
            return 6;
        }else if (169<=height&&height<171) {
            return 7;
        }else if (171<=height&&height<173) {
            return 8;
        }else if (173<=height&&height<175) {
            return 9;
        }else if (175<=height&&height<177) {
            return 10;
        }else if (177<=height&&height<179) {
            return 11;
        }else if (179<=height&&height<181) {
            return 12;
        }else if (181<=height&&height<183) {
            return 13;
        }else if (183<=height&&height<185) {
            return 14;
        }else if (185<=height){
            return 15;
        }
        return 0;
    }

    //由出生日期获得年龄
    public int getAge() {
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
        Date birthDay= null;
        try {
            birthDay = sdf.parse(birth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int mAge = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) mAge--;
            }else{
                mAge--;
            }
        }
        return mAge;
    }
}
