package jeken.com.handhealth.tools;

import jeken.com.handhealth.entity.health.HealthImpl;

/**
 * Created by Administrator on 2017-06-28.
 */

public class ProtocolTools {
    public static HealthImpl protocolToHealthImpl(byte[] bleData){
        //字节数必须是3的倍数
        if (bleData.length%3!=0) return null;
        HealthImpl healthImpl = HealthImpl.CREATOR.newArray(1)[0];
        if (bleData[0]==bleData[2]&&bleData[0]=='a'){
             healthImpl.setHeart(byteToInteger(bleData[1]));
        }
        if (bleData[3]==bleData[5]&&bleData[3]=='b'){
            healthImpl.setBlood_H(byteToInteger(bleData[4]));
        }
        if (bleData[6]==bleData[8]&&bleData[6]=='c'){
            healthImpl.setBlood_L(byteToInteger(bleData[7]));
        }
        if (bleData[9]==bleData[11]&&bleData[9]=='d'){
            healthImpl.setFootNum(byteToInteger(bleData[10]));
        }
        if (bleData[12]==bleData[14]&&bleData[12]=='e'){
            healthImpl.setTumble(byteToBoolean(bleData[13]));
        }
        return healthImpl;
    }

    public static int byteToInteger(byte src){
        int des = 0xff;
        des &= src;
        return des;
    }
    public static boolean byteToBoolean(byte src){
        if (src>0){
            return true;
        }else {
            return false;
        }
    }

}
