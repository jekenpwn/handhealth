package jeken.com.handhealth.entity.health;

/**
 * Created by Administrator on 2017-06-28.
 */

public class Tb_CountFoot {
    private long footnum = 0;
    private int increase = 0;

    public Tb_CountFoot(){}
    public Tb_CountFoot(long footnum, int increase) {
        this.footnum = footnum;
        this.increase = increase;
    }

    public long getFootnum() {
        return footnum;
    }

    public void setFootnum(long footnum) {
        this.footnum = footnum;
    }

    public int getIncrease() {
        return increase;
    }

    public void setIncrease(int increase) {
        this.increase = increase;
    }

    public void add(int increase){
        this.increase+=increase;
        footnum+=increase;
    }
    public void clearIncrease(){
        increase=0;
    }
    public void clearNum(){
        this.footnum = 0;
    }
}
