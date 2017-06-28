package com.hins.smartband.bean;

import android.text.TextUtils;

import java.text.DecimalFormat;

import cn.bmob.v3.BmobUser;

/**
 * 程序员： Hins on 2016/7/7.
 * 描述：用户信息的数据容器bean，继承BmobUser api
 */
public class UserInfoBean extends BmobUser {

    private String nickname;
    private String sex;
    private String birth;
    private Integer height;
    private Integer weight;
    private Float BMI;//身体质量指数
    private String iconUrl;

    public UserInfoBean() {
        nickname = new String("");
        sex = new String("");
        birth = new String("");
        height = new Integer(0);
        weight = new Integer(0);
        BMI = new Float(0.0);
        iconUrl = new String("");
    }

    /*
    *功能：用于反馈UserBean中的成员属性状态
    *参数：
    *返回类型：
    */
    public int whichUserInfoUnFinish() {
        if (TextUtils.isEmpty(sex)) {
            return 1;
        }
        if (TextUtils.isEmpty(birth)) {
            return 2;
        }
        if (height.equals(new Integer("0"))) {
            return 3;
        }
        if (weight.equals(new Integer("0"))) {
            return 4;
        }
        if (BMI.equals(new Float("0.0"))) {
            return 5;
        }
        return 0;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirth() {
        return this.birth;
    }

    public void setBirth(String age) {
        this.birth = new String(age);
    }

    public Integer getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = new Integer(height);
    }

    public Integer getWeight() {
        return this.weight;
    }

    public void setWeight(int weight) {
        this.weight = new Integer(weight);
    }

    public Float getBMI() {
        return this.BMI;
    }

    public void setBMI() {
        DecimalFormat dt = (DecimalFormat) DecimalFormat.getInstance(); //获得格式化类对象
        dt.applyPattern("0.00");//设置小数点位数(两位) 余下的会四舍五入
        //体质指数（BMI）=体重（kg）÷身高^2（m）
        //               =体重（kg）* 10000 ÷身高^2（cm）
        this.BMI = new Float(dt.format((getWeight() * 10000) / (getHeight() * getHeight())));
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

}
