package jeken.com.handhealth.entity.health;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017-06-26.
 */

public class HealthImpl implements Health ,Parcelable{
    private int Heart = -1;
    private int Blood_H = -1;
    private int Blood_L = -1;
    private long FootNum = 0;
    private boolean Tumble = false;


    protected HealthImpl(Parcel in) {
        Heart = in.readInt();
        Blood_H = in.readInt();
        Blood_L = in.readInt();
        FootNum = in.readLong();
        Tumble = in.readByte() != 0;
    }

    public static final Creator<HealthImpl> CREATOR = new Creator<HealthImpl>() {
        @Override
        public HealthImpl createFromParcel(Parcel in) {
            return new HealthImpl(in);
        }

        @Override
        public HealthImpl[] newArray(int size) {
            return new HealthImpl[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Heart);
        dest.writeInt(Blood_H);
        dest.writeInt(Blood_L);
        dest.writeLong(FootNum);
        dest.writeByte((byte) (Tumble ? 1 : 0));
    }

    @Override
    public void setHeart(int Heart) {

    }

    @Override
    public int getHeart() {
        return 0;
    }

    @Override
    public void setBlood_H(int Blood_H) {

    }

    @Override
    public int getBlood_H() {
        return 0;
    }

    @Override
    public void setBlood_L(int Blood_L) {

    }

    @Override
    public int getBlood_L() {
        return 0;
    }

    @Override
    public void setFootNum(long FootNum) {

    }

    @Override
    public long getFootNum() {
        return 0;
    }

    @Override
    public void setTumble(boolean tumble) {

    }

    @Override
    public boolean isTumble() {
        return false;
    }
}
