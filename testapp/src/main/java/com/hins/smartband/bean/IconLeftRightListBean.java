package com.hins.smartband.bean;

/**
 * 程序员： Hins on 2016/7/21.
 * 描述：
 */
public class IconLeftRightListBean extends IconLeftListBean {

    private String rightStr;

    public IconLeftRightListBean(int img, String leftStr, String rightStr) {
        super(img, leftStr);
        setRightStr(rightStr);
    }

    public String getRightStr(){
        return this.rightStr;
    }

    public void setRightStr(String rightStr){
        this.rightStr=rightStr;
    }
}
