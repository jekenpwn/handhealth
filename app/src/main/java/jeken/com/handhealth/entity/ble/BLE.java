package jeken.com.handhealth.entity.ble;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import java.util.List;

/**
 * Created by Administrator on 2017-06-26.
 */
//值得注意的是，android6.0以上正常使用蓝牙需要开启位置服务，这是重大的区别
public interface BLE {
    // Initializes a reference to the local Bluetooth adapter.
    boolean initialize(Context context);
    //Connects to the GATT server hosted on the Bluetooth LE device.
    boolean connect(final String address);
    //Disconnects an existing connection or cancel a pending connection.
    void disconnect();
    //release the ble resource
    void close();
    //Request an  readable Characteristic
    void readCharacteristic(BluetoothGattCharacteristic characteristic);
    //Request an wirteable Characteristic
    void writeCharacteristic(BluetoothGattCharacteristic characteristic,String value);
    //Enables or disables notification on a give characteristic.
    void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,boolean enabled);
    //Retrieves a list of supported GATT services on the connected device.
    List<BluetoothGattService> getSupportedGattServices();
}
