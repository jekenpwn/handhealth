package jeken.com.handhealth.entity.ble;

import java.util.HashMap;

/**
 * Created by Administrator on 2017-06-26.
 */

public class HandGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    static {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");

        attributes.put("0003cdd0-0000-1000-8000-00805f9b0131", "USR-SERVICE");
        attributes.put("0003cdd1-0000-1000-8000-00805f9b0131", "Notify");
        attributes.put("0003cdd2-0000-1000-8000-00805f9b0131", "Write");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
