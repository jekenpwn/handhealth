package com.hins.smartband.le;

import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hins.smartband.R;
import com.hins.smartband.activities.binderDevice;
import com.hins.smartband.database.BindDeviceDAO;
import com.hins.smartband.database.SpaceDAO;
import com.hins.smartband.model.Tb_bind_device_m;
import com.hins.smartband.model.Tb_space_m;

/**
 * 对于一个BLE设备，该activity向用户提供设备连接，显示数据，显示GATT服务和设备的字符串支持等界面，
 * 另外这个activity还与BluetoothLeService通讯，反过来与Bluetooth LE API进行通讯
 */
public class DeviceControlActivity extends AppCompatActivity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    //连接状态
    private EditText mDataField;
    private String mDeviceName=null;
    private String mDeviceAddress=null;
    private Button button_send_value ; // 发送按钮
    private EditText edittext_input_value ; // 数据在这里输入
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;//对menu进行控制的
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private ArrayList<String> showData=new ArrayList<String>();//缓存数据
    //写数据
    private BluetoothGattCharacteristic characteristic;//写如数据的characteristic
    private BluetoothGattService mnotyGattService;;
    //读数据
    private BluetoothGattCharacteristic readCharacteristic;
    private BluetoothGattService readMnotyGattService;
    byte[] WriteBytes = new byte[20];

    //=======================//新增内容==========================================
    //新增内容
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private Button Btn_binder,Btn_dealdata;
    public static final int REQUEST=0;
    public static final int RESULT=0;
    private boolean firstconn = false;
    //===================================================================================

    //===================================================================================
    /**
     * 刷新时发送m给蓝牙设备
     * */
    //SendData_ReceData("m");
    //===================================================================================

    // Handles various events fired by the Service.处理服务所激发的各种事件
    // ACTION_GATT_CONNECTED: connected to a GATT server.连接一个GATT服务
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.从GATT服务中断开连接
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.查找GATT服务
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.从服务中接受数据
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);
        //=======================//新增内容============================================================
        //判断是否有蓝牙功能
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            Toast.makeText(this,R.string.ble_not_supported,Toast.LENGTH_SHORT).show();
            finish();
        }
        //获得蓝牙管理器对象
        final BluetoothManager bluetoothManager=(BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter=bluetoothManager.getAdapter();

        Btn_binder=(Button)findViewById(R.id.bind);//显示有没有绑定;
        Btn_binder.setOnClickListener(new View.OnClickListener() {//进行绑定蓝牙操作
            @Override
            public void onClick(View v) {
                if(Btn_binder.getText()=="已绑定"){

                }else if(Btn_binder.getText()=="未绑定"){
                    Intent intent=new Intent(DeviceControlActivity.this,binderDevice.class);
                    startActivityForResult(intent,REQUEST);
                }
            }
        });
//===================================================================================

        // Sets up UI references.


        mDataField =  (EditText) findViewById(R.id.data_value);//接受的数据显示

        button_send_value = (Button) findViewById(R.id.button_send_value);//发送数据按钮

        edittext_input_value = (EditText) findViewById(R.id.edittext_input_value);//输入要发送的数据

        Btn_dealdata=(Button) findViewById(R.id.dealdata);
        init();//对数据初始化

        button_send_value.setOnClickListener(new View.OnClickListener() {//发送数据操作
            @Override
            public void onClick(View v) {
//				SendData_ReceData();
                SendData_ReceData();
                edittext_input_value.setText("");
            }
        });
        Btn_dealdata.setOnClickListener(new View.OnClickListener() {//发送数据操作
            @Override
            public void onClick(View v) {
                SpaceDAO SpaceDao=new SpaceDAO(DeviceControlActivity.this);
                Tb_space_m tb_space=SpaceDao.getLastData();
                mDataField.setText("现在时间:"+tb_space.getNowtime()+"温度:"+tb_space.getTemperature());
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bind();
    }

    /*
	 * **************************************************************
	 * *****************************读函数*****************************
	 */
    private void read() {
        //readCharacteristic的数据发生变化，发出通知
        mBluetoothLeService.setCharacteristicNotification(readCharacteristic, true);//必须保留
    }


    @Override
    protected void onResume() {//在运行的过程中
        super.onResume();

//===================================================================================
        //判断蓝牙功能是否开启
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
        }
//===================================================================================

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());//广播服务注册
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }

    }

    //===================================================================================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //对用户的选项进行判断，如果不打开就推出软件
        // TODO 自动生成的方法存根
        if(requestCode==REQUEST&&resultCode==RESULT){
            Bundle resultdata=data.getExtras();
            mDeviceName=resultdata.getString(EXTRAS_DEVICE_NAME);
            mDeviceAddress=resultdata.getString(EXTRAS_DEVICE_ADDRESS);
            bind();//有了地址就直接调用这个
            BindDeviceDAO bindDeviceDao=new BindDeviceDAO(DeviceControlActivity.this);
            bindDeviceDao.add(new Tb_bind_device_m(mDeviceName,mDeviceAddress,"00:15:83:00:88:A4"));
            mDataField.setText(mDeviceAddress);
//    		if(bindDeviceDao.getCount()!=0){
            Btn_binder.setText("已绑定");
//    		}
        }

        if(requestCode==REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED){
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    //===================================================================================
    @Override
    protected void onPause() {//在暂停时的时候
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {//在关闭的时候
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);//必须留有
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();////必须留有
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //初始化
    public void init(){
//        final Intent intent = getIntent();
//        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
//        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        //===================================================================================
        BindDeviceDAO bindDeviceDao=new BindDeviceDAO(DeviceControlActivity.this);
        if(bindDeviceDao.getCount()==0){
            Btn_binder.setText("未绑定");
        }else if(bindDeviceDao.find().getNameOne()!=null){
            Btn_binder.setText("已绑定");
            Tb_bind_device_m tb_bind_device_m=bindDeviceDao.find();
            mDeviceName=tb_bind_device_m.getNameOne();
            mDeviceAddress=tb_bind_device_m.getAddressOne();
            bind();
        }
        //===================================================================================

    }

    //数据发送跟读取
    public void SendData_ReceData(){
       /// read();//发送读取通知
        final int charaProp = characteristic.getProperties();//对这个
        //如果该char可写
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {//这个特性是可读的
            // If there is an active notification on a characteristic, clear
            // it first so it doesn't update the data field on the user interface.
            if (mNotifyCharacteristic != null) {
                mBluetoothLeService.setCharacteristicNotification( mNotifyCharacteristic, false);
                mNotifyCharacteristic = null;
            }
            //读取数据，数据将在回调函数中
            //mBluetoothLeService.readCharacteristic(characteristic);
            byte[] value = new byte[20];
            value[0] = (byte) 0x00;
            if(edittext_input_value.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(), "请输入！", Toast.LENGTH_SHORT).show();
                return;
            }else{
                WriteBytes = edittext_input_value.getText().toString().getBytes();
                characteristic.setValue(value[0],BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                characteristic.setValue(WriteBytes);
                mBluetoothLeService.writeCharacteristic(characteristic);//写入数据成功
            }
        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            mNotifyCharacteristic = characteristic;
            mBluetoothLeService.setCharacteristicNotification(characteristic, true);
        }
    }

    //数据发送跟读取
    public void SendData_ReceData(String str){
       // read();//发送读取通知
        final int charaProp = characteristic.getProperties();//对这个
        //如果该char可写e
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {//这个特性是可读的
            // If there is an active notification on a characteristic, clear
            // it first so it doesn't update the data field on the user interface.
            if (mNotifyCharacteristic != null) {
                mBluetoothLeService.setCharacteristicNotification( mNotifyCharacteristic, false);
                mNotifyCharacteristic = null;
            }
            //读取数据，数据将在回调函数中
            //mBluetoothLeService.readCharacteristic(characteristic);
            byte[] value = new byte[20];
            value[0] = (byte) 0x00;
            WriteBytes = str.getBytes();
            characteristic.setValue(value[0],BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            characteristic.setValue(WriteBytes);
            mBluetoothLeService.writeCharacteristic(characteristic);//写入数据成功

        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            mNotifyCharacteristic = characteristic;
            mBluetoothLeService.setCharacteristicNotification(characteristic, true);
        }
    }
    //绑定服务
    public void bind(){
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
//        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }
    //处理运行得到后的数据
    public void displayData(String data) {
        if(data.contains("!")){
            new dealData(mDeviceName,mDeviceAddress,data,DeviceControlActivity.this);//绑定两个蓝牙
            mDataField.setText(data);
        }else {
            if(!showData.contains(data)){
                showData.add(data);
            }
            if(showData.size()==2){
                mDataField.setText(showData.get(0)+showData.get(1));
                dealData dealdata=new dealData(showData.get(0)+showData.get(1),DeviceControlActivity.this);
                showData.clear();
                dealdata.deal();
            }
        }
    }

    // 管理服务的生命周期
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);

        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    //广播处理器
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {//说明连接成功。。进行连接成功后的操作。
                mConnected = true;
                Btn_binder.setText("已连接");
//                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
//                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
            }
            //发现有可支持的服务
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                //写数据的服务和characteristic、、//写如数据的characteristic
                mnotyGattService = mBluetoothLeService.getSupportedGattServices(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
                characteristic = mnotyGattService.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
                //读数据的服务和characteristic
                readMnotyGattService = mBluetoothLeService.getSupportedGattServices(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
                readCharacteristic = readMnotyGattService.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
            }
            //显示数据
            else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //将数据显示在mDataField上
                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                System.out.println("data----" + data);
                displayData(data);
            }
        }
    };
    //广播注册
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}