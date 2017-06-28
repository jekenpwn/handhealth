package com.hins.smartband.model;

import cn.bmob.v3.BmobObject;

public class Tb_aim_m extends BmobObject{

    private String uObjectId;
    private String step;
    private String sleep;
    private String kmile;
    private String tip;

    public Tb_aim_m(String uObjectId, String step, String kmile, String sleep,String tip) {
        this.uObjectId = uObjectId;
        this.step = step;
        this.sleep = sleep;
        this.kmile = kmile;
        this.tip = tip;
    }

    public Tb_aim_m(String objectId,String uObjectId, String step, String kmile, String sleep,String tip) {
        setObjectId(objectId);
        this.uObjectId = uObjectId;
        this.step = step;
        this.sleep = sleep;
        this.kmile = kmile;
        this.tip = tip;
    }

    public String getUObjectId() {
        return uObjectId;
    }

    public void setUObjectId(String uObjectId) {
        this.uObjectId = uObjectId;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getSleep() {
        return sleep;
    }

    public void setSleep(String sleep) {
        this.sleep = sleep;
    }

    public String getKmile() {
        return kmile;
    }

    public void setKmile(String kmile) {
        this.kmile = kmile;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }
}
