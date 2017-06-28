package com.hins.smartband.model;

import cn.bmob.v3.BmobObject;

public class Tb_space_m extends BmobObject {
    private String uObjectId;
    private String nowtime;
    private String temperature;
    private String heart;
    private String blood;
    private String state;
    private String running;
    private String sleep;
    private String makeup;
    private String danger;

    public Tb_space_m() {
        super();
    }

    public Tb_space_m(String temperature, String heart, String blood, String state,
                      String running, String sleep, String makeup, String danger, String nowtime) {
        super();
        this.nowtime = nowtime;
        this.temperature = temperature;
        this.heart = heart;
        this.blood = blood;
        this.state = state;
        this.running = running;
        this.sleep = sleep;
        this.makeup = makeup;
        this.danger = danger;
    }

    public String getNowtime() {
        return nowtime;
    }

    public void setNowtime(String nowtime) {
        this.nowtime = nowtime;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHeart() {
        return heart;
    }

    public void setHeart(String heart) {
        this.heart = heart;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRunning() {
        return running;
    }

    public void setRunning(String running) {
        this.running = running;
    }

    public String getSleep() {
        return sleep;
    }

    public void setSleep(String sleep) {
        this.sleep = sleep;
    }

    public String getMakeup() {
        return makeup;
    }

    public void setMakeup(String makeup) {
        this.makeup = makeup;
    }

    public String getDanger() {
        return danger;
    }

    public void setDanger(String danger) {
        this.danger = danger;
    }

    public String getUObjectId() {
        return uObjectId;
    }

    public void setUObjectId(String uObjectId) {
        this.uObjectId = uObjectId;
    }

    public boolean compare(Tb_space_m tb_space_m) {
        if (!tb_space_m.getTemperature().equals(this.getTemperature())) {
            return false;
        }
        if (!tb_space_m.getNowtime().equals(this.getNowtime())) {
            return false;
        }
        if (!tb_space_m.getHeart().equals(this.getHeart())) {
            return false;
        }
        if (!tb_space_m.getBlood().equals(this.getBlood())) {
            return false;
        }
        if (!tb_space_m.getSleep().equals(this.getSleep())) {
            return false;
        }
        if (!tb_space_m.getState().equals(this.getSleep())) {
            return false;
        }
        if (!tb_space_m.getRunning().equals(this.getRunning())) {
            return false;
        }
        if (!tb_space_m.getMakeup().equals(this.getMakeup())) {
            return false;
        }
        if (!tb_space_m.getDanger().equals(this.getDanger())) {
            return false;
        }
        return true;
    }
}
