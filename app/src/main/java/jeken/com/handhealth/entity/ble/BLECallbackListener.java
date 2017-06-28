package jeken.com.handhealth.entity.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by Administrator on 2017-06-26.
 */

public interface BLECallbackListener {

    //BLE receive the message ,and callback
    void onReceive(BluetoothGattCharacteristic characteristic);

    void onDiscover(BluetoothGatt gatt);

    void onConnectStateCallBack(String message);

}
