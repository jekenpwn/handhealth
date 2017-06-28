package jeken.com.handhealth.entity.health;

/**
 * Created by Administrator on 2017-06-26.
 */

public interface Health {

    void setHeart(int Heart);
    int getHeart();
    void setBlood_H(int Blood_H);
    int getBlood_H();
    void setBlood_L(int Blood_L);
    int getBlood_L();
    void setFootNum(long FootNum);
    long getFootNum();
    void setTumble(boolean tumble);
    boolean isTumble();

}
