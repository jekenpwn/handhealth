package com.hins.smartband.bean;

/**
 * 程序员： Hins on 2016/7/21.
 * 描述：
 */
public class IconLeftListBean extends LeftListBean {

    private int img;

    public IconLeftListBean(int img, String leftStr) {
        super(leftStr);
        setImg(img);
    }


    public int getImg(){
        return this.img;
    }

    public void setImg(int img){
        this.img=img;
    }
}
