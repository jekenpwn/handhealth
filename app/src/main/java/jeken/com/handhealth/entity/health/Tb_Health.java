package jeken.com.handhealth.entity.health;

/**
 * Created by Administrator on 2017-06-28.
 */

public class Tb_Health {
    private int heart;
    private int blood_h;
    private int blood_l;

    public Tb_Health(){}
    public Tb_Health(int heart, int blood_h, int blood_l) {
        this.heart = heart;
        this.blood_h = blood_h;
        this.blood_l = blood_l;
    }

    public int getHeart() {
        return heart;
    }

    public void setHeart(int heart) {
        this.heart = heart;
    }

    public int getBlood_h() {
        return blood_h;
    }

    public void setBlood_h(int blood_h) {
        this.blood_h = blood_h;
    }

    public int getBlood_l() {
        return blood_l;
    }

    public void setBlood_l(int blood_l) {
        this.blood_l = blood_l;
    }
}
