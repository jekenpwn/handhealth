package com.hins.smartband.bean;

/**
 * 程序员： Hins on 2016/7/21.
 * 描述：
 */
public class IconLeftMidRightListBean  {

    private String time;
    private String rightStr,leftStr,midStr;

    public IconLeftMidRightListBean(String time,String midStr, String leftStr, String rightStr) {
        setTime(time);
        setleftStr(leftStr);
        setMidStr(midStr);
        setRightStr(rightStr);
    }

    public String getTime(){
        return this.time;
    }

    public void setTime(String rightStr){
        this.time=rightStr;
    }

    public String getLeftStr(){
        return this.leftStr;
    }

    public void setleftStr(String rightStr){
        this.leftStr=rightStr;
    }

    public String getMidStr(){
        return this.midStr;
    }

    public void setMidStr(String rightStr){
        this.midStr=rightStr;
    }

    public String getRightStr(){
        return this.rightStr;
    }

    public void setRightStr(String rightStr){
        this.rightStr=rightStr;
    }
}
