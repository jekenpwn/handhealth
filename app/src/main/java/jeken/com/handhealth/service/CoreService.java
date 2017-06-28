package jeken.com.handhealth.service;

import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import jeken.com.handhealth.entity.ble.BLECallbackListener;
import jeken.com.handhealth.entity.ble.BLEImpl;
import jeken.com.handhealth.entity.health.Health;
import jeken.com.handhealth.entity.health.HealthImpl;
import jeken.com.handhealth.entity.health.Tb_CountFoot;
import jeken.com.handhealth.entity.health.Tb_Health;
import jeken.com.handhealth.entity.net.NetHttp;
import jeken.com.handhealth.entity.net.NetHttpImpl;
import jeken.com.handhealth.tools.ProtocolTools;
import jeken.com.handhealth.tools.SysTools;

public class CoreService extends Service {
    private String TAG = CoreService.class.getSimpleName();
    private long beginTime = 0;//记录开启服务时间
    private static Tb_CountFoot tb_countFoot;
    private static NetHttp netHttpImpl = new NetHttpImpl();
    //写死特定蓝牙
    private final String DeviceAddress = "";

    public final static String EXTRA_DATA = "com.jeken.EXTRA_DATA";
    public final static String CONN_SUCCESS = "com.jeken.CONN_SUCCESS";
    public final static String DISCONN = "com.jeken.DISCONN";

    private BLEImpl ble;

    public CoreService() {
        ble = new BLEImpl();
        beginTime = System.currentTimeMillis();
        tb_countFoot = new Tb_CountFoot();
    }

    private void sendBleBroadcast(String ACTION) {
        sendBroadcast(new Intent(ACTION));
    }

    private void sendBleBroadcast(String message, String ACTION, String NAME) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(NAME, message);
        sendBroadcast(intent);
    }

    private void  sendBleBroadcase(Health health){
        if (health==null) return;
        Intent intent = new Intent(EXTRA_DATA);
        Bundle bundle = new Bundle();
        bundle.putInt("Blood_h",health.getBlood_H());
        bundle.putInt("Blood_l",health.getBlood_L());
        bundle.putInt("Heart",health.getHeart());
        bundle.putLong("FootNum",health.getFootNum());
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        //Is the phone support BLE ?
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            if (ble.initialize(this)) {
                ble.connect(DeviceAddress);
                ble.setOnBLECallbackListener(new BLECallbackListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                    @Override
                    public void onReceive(BluetoothGattCharacteristic characteristic) {
                        //String buff = new String(characteristic.getValue());
                        //Log.i(TAG,buff);
                        //sendBleBroadcast(buff, EXTRA_DATA, "EXTRA_DATA");
                        if (characteristic==null) return;
                        HealthImpl healthImpl = ProtocolTools.
                                protocolToHealthImpl(characteristic.getValue());
                        sendBleBroadcase(healthImpl);//接收的数据广播出去
                        if (beginTime>0&&SysTools.isToday(beginTime)) {
                            tb_countFoot.clearNum();
                            tb_countFoot.clearIncrease();
                            beginTime = System.currentTimeMillis();
                        }
                        if (healthImpl.getFootNum()>0)tb_countFoot.add((int)healthImpl.getFootNum());
                        int blood_h = healthImpl.getBlood_H();
                        int blood_l = healthImpl.getBlood_L();
                        int heart = healthImpl.getHeart();
                        if ((blood_h+blood_l+heart)>=0){//数据提交服务器
                            Tb_Health tb_health = new Tb_Health(heart,blood_h,blood_l);
                            netHttpImpl.httpGetUploadHealth(tb_health);
                        }
                        if (tb_countFoot.getIncrease()>=20){//大于20步上传服务器
                            netHttpImpl.httpGetUploadCountfoot(tb_countFoot);
                        }

                    }
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                    @Override
                    public void onDiscover(BluetoothGatt gatt) {
                        Log.i(TAG,"discover device:"+gatt.getDevice().getName());
                    }
                    @Override
                    public void onConnectStateCallBack(String message) {
                        if (message.equals("conn success")) {
                            sendBleBroadcast(CONN_SUCCESS);
                        }
                    }
                });
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
