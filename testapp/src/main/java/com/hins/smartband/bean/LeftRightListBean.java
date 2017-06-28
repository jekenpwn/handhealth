package com.hins.smartband.bean;

/**
 * 程序员： Hins on 2016/7/21.
 * 描述：
 */
public class LeftRightListBean extends LeftListBean {

    String rightStr;

    public LeftRightListBean(String leftStr, String rightStr){
        super(leftStr);
        setRightStr(rightStr);
    }

    public LeftRightListBean(String leftStr){
        super(leftStr);
    }

    public String getRightStr(){
        return this.rightStr;
    }

    public void setRightStr(String rightStr){
        this.rightStr=rightStr;
    }
}
