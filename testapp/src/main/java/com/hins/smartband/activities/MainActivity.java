package com.hins.smartband.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.hins.smartband.R;
import com.hins.smartband.base.BaseActivity;
import com.hins.smartband.base.MainBaseFragment;
import com.hins.smartband.database.BindDeviceDAO;
import com.hins.smartband.fragment.MeFragment;
import com.hins.smartband.fragment.HeartRateFragment;
import com.hins.smartband.fragment.StepFragment;
import com.hins.smartband.le.BluetoothLeService;
import com.hins.smartband.le.DeviceControlActivity;
import com.hins.smartband.le.NowTime;
import com.hins.smartband.le.dealData;
import com.hins.smartband.model.Tb_bind_device_m;
import com.hins.smartband.ui.Main_TabBottom;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 程序员： Hins on 2016/4/30.
 * 描述：主界面使用ViewPager
 */
public class MainActivity extends BaseActivity implements MainBaseFragment.CustomFragmentManagerInterFace, View.OnClickListener, ViewPager.OnPageChangeListener {

    private final static String TAG = DeviceControlActivity.class.getSimpleName();
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private BluetoothLeService mBluetoothLeService;
    public String mBLEState = "未绑定";
    public String mDeviceName = null;
    public String mDeviceAddress = null;

    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private ArrayList<String> showData = new ArrayList<String>();//缓存数据
    //写数据
    private BluetoothGattCharacteristic characteristic;//写如数据的characteristic
    private BluetoothGattService mnotyGattService;
    //读数据
    private BluetoothGattCharacteristic readCharacteristic;
    private BluetoothGattService readMnotyGattService;
    byte[] WriteBytes = new byte[20];
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    public static final int REQUEST = 0;
    public static final int RESULT = 0;
    public boolean firstconn = false;

    //=================================增强版2.2==================================================
    private boolean mScanning;
    private Handler mHandler = new Handler();
    // 10s后停止扫描
    private static final long SCAN_PERIOD = 10000;
    //===================================================================================

    //=================================转接版2.3==================================================
    private boolean isDanger = false;//有危险就换蓝牙
    private boolean isAddressOne = false;//存在第一设备
    private boolean isAddressTwo = false;//存在第二设备

    //===================================================================================
    //=================================转接板2.4=============
    private boolean isCALLPhone = false;//是否需要打电话
    private boolean isLeService = false;
    private boolean ChangeDevice = false;

    private ViewPager viewPager;
    private List<Fragment> mainTab = new ArrayList<Fragment>();//保存Fragment
    private FragmentPagerAdapter pagerAdapter;
    private Main_TabBottom measure_tb, step_tb, me_tb;
    private List<Main_TabBottom> mainTabBottom = new ArrayList<Main_TabBottom>();//保存底部按钮
    private HeartRateFragment heartRateFragment;
    private StepFragment stepFragment;
    private MeFragment meFragment;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBLE();
        initView();
        initFragment();
        initMain_TabBottom();
    }

    private void initBLE() {
        //判断是否有蓝牙功能
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
        }
        //获得蓝牙管理器对象
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        BindDeviceDAO bindDeviceDao = new BindDeviceDAO(MainActivity.this);
        if (bindDeviceDao.getCount() == 0) {
            mBLEState = "未绑定";
            firstconn = true;
        } else if (bindDeviceDao.find().getNameOne() != null) {
            mBLEState = "未连接";
            Tb_bind_device_m tb_bind_device_m = bindDeviceDao.find();
            mDeviceName = tb_bind_device_m.getNameOne();
            mDeviceAddress = tb_bind_device_m.getAddressOne();
            //====================================增强版2.2可以用===============================================
            mScanning = true;
            scanLeDevice(true);
            //====================================增强版2.2可以用===============================================
        }
    }

    //初始化组件
    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.main_viewPager);
        measure_tb = (Main_TabBottom) findViewById(R.id.tb_main_tab_measure);
        step_tb = (Main_TabBottom) findViewById(R.id.tb_main_tab_step);
        me_tb = (Main_TabBottom) findViewById(R.id.tb_main_tab_me);
    }

    //创建方法用于初始化bottom
    private void initMain_TabBottom() {
        measure_tb.setOnClickListener(this);
        step_tb.setOnClickListener(this);
        me_tb.setOnClickListener(this);
        mainTabBottom.add(measure_tb);
        mainTabBottom.add(step_tb);
        mainTabBottom.add(me_tb);
        measure_tb.setIconAlpha(1.0f);//设置第一个Icon为有色
    }

    //创建方法用于初始化fragment
    private void initFragment() {
        heartRateFragment = new HeartRateFragment();
        mainTab.add(heartRateFragment);
        stepFragment = new StepFragment();
        mainTab.add(stepFragment);
        meFragment = new MeFragment();
        mainTab.add(meFragment);

        //创建fragment适配器.
        pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mainTab.get(position);
            }

            @Override
            public int getCount() {
                return mainTab.size();
            }
        };

        //使用适配器填充
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(MainActivity.this);
        viewPager.setOffscreenPageLimit(3);

    }

    //实现Tab_Bottom监听器跳转到对应的viewPager
    @Override
    public void onClick(View v) {
        resetOtherTabBottomAlpha();
        switch (v.getId()) {
            case R.id.tb_main_tab_measure:
                measure_tb.setIconAlpha(1.0f);
                viewPager.setCurrentItem(0, false);
                setStatuBarColor(R.color.colorRedPrimaryDark);
                break;
            case R.id.tb_main_tab_step:
                step_tb.setIconAlpha(1.0f);
                viewPager.setCurrentItem(1, false);
                setStatuBarColor(R.color.colorGreenPrimaryDark);
                break;
            case R.id.tb_main_tab_me:
                me_tb.setIconAlpha(1.0f);
                viewPager.setCurrentItem(2, false);
                setStatuBarColor(R.color.colorPrimaryDark);
                break;
        }
    }

    //重置其他TabBottom的透明度
    public void resetOtherTabBottomAlpha() {
        for (int i = 0; i < mainTabBottom.size(); i++) {
            mainTabBottom.get(i).setIconAlpha(0.0f);
        }
    }

    @Override
    //通过动态设置icon的透明度实现
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset > 0) {
            Main_TabBottom left = mainTabBottom.get(position);
            Main_TabBottom right = mainTabBottom.get(position + 1);
            left.setIconAlpha(1 - positionOffset);
            right.setIconAlpha(positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                setStatuBarColor(R.color.colorRedPrimaryDark);
                break;
            case 1:
                setStatuBarColor(R.color.colorGreenPrimaryDark);
                break;
            case 2:
                setStatuBarColor(R.color.colorPrimaryDark);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //返回Home桌面
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
            return false;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //判断蓝牙功能是否开启
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
//===================================================================================

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());//广播服务注册
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }


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
        //=================================转接版2.4==================================================
        isLeService = false;
        //=================================转接版2.4==================================================
    }

    /*
     * **************************************************************
	 * *****************************读函数*****************************
	 */

    public void getSpaceData() {
        SendData_ReceData("m");
    }


    //数据发送跟读取
    public void SendData_ReceData(String str) {
        //read();
        // 发送读取通知
        final int charaProp = characteristic.getProperties();//对这个
        //如果该char可写
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {//这个特性是可读的
            // If there is an active notification on a characteristic, clear
            // it first so it doesn't update the data field on the user interface.
            if (mNotifyCharacteristic != null) {
                mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);
                mNotifyCharacteristic = null;
            }
            //读取数据，数据将在回调函数中
            //mBluetoothLeService.readCharacteristic(characteristic);
            byte[] value = new byte[20];
            value[0] = (byte) 0x00;
            WriteBytes = str.getBytes();
            characteristic.setValue(value[0], BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            characteristic.setValue(WriteBytes);
            mBluetoothLeService.writeCharacteristic(characteristic);
            if (!str.equals("m")) {
                firstconn = false;
            }
            /*if (mBluetoothLeService.writeCharacteristic(characteristic)) {
                if (str.equals("m")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(250);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }else {
                    firstconn=false;
                }
            }*/

        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            mNotifyCharacteristic = characteristic;
            mBluetoothLeService.setCharacteristicNotification(characteristic, true);
        }
    }

    //绑定服务
    public void bind() {
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        if (bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE)) {
            isLeService = true;
        }
    }

    @Override
    public void setDeviceName(String d) {
        this.mDeviceName = d;
    }

    @Override
    public void setDeviceAddress(String d) {
        this.mDeviceAddress = d;
    }

    @Override
    public void setIsDanger(boolean isAddressOne) {
        this.isDanger = isAddressOne;
    }

    @Override
    public void setIsAddressOne(boolean isAddressOne) {
        this.isAddressOne = isAddressOne;
    }

    @Override
    public void setIsAddressTwo(boolean isAddressOne) {
        this.isAddressTwo = isAddressOne;
    }

    @Override
    public void setIsCallPhone(boolean isAddressOne) {
        this.isCALLPhone = isAddressOne;
    }

    @Override
    public void setIsLeService(boolean isAddressOne) {
        this.isLeService = isAddressOne;
    }

    @Override
    public void setChangeDevice(boolean isAddressOne) {
        this.ChangeDevice = isAddressOne;
    }

    @Override
    public boolean getFirstConn() {
        return firstconn;
    }

    //处理运行得到后的数据
    public void displayData(String data) {
        if (data.contains("!")) {
            //=================================转接版2.3====
            BindDeviceDAO bindDeviceDao = new BindDeviceDAO(MainActivity.this);
            Tb_bind_device_m tb_bind_device_m = bindDeviceDao.find();
            if (tb_bind_device_m == null) {
                new dealData(mDeviceName, mDeviceAddress, data, MainActivity.this);//绑定两个蓝牙
            }
            //=================================转接版2.3====

            SendData_ReceData(new NowTime().SendnowTime());

        } else {
            if (!showData.contains(data)) {
                showData.add(data);
            }
            if (showData.size() == 2) {
                dealData dealdata = new dealData(showData.get(0) + showData.get(1), MainActivity.this);
                //=================================转接版2.3==================================================
                if (showData.get(1).contains("i1i") || showData.get(1).contains("i2i")) {
                    isDanger = true;
                    isCALLPhone = true;
                    isAddressOne = false;
//                    mBluetoothLeService.disconnect();//断开当前连接
//                    BindDeviceDAO bindDeviceDao = new BindDeviceDAO(MainActivity.this);//获取设备二
//                    Tb_bind_device_m tb_bind_device_m = bindDeviceDao.find();
//                    mDeviceAddress = tb_bind_device_m.getAddressTwo();
                    Toast.makeText(MainActivity.this,showData.get(0) + showData.get(1),Toast.LENGTH_SHORT).show();
                }
                showData.clear();
                dealdata.deal();
                if (isDanger) {
                    Intent intentPhone = new Intent(Intent.ACTION_CALL, Uri.parse("tel:15622282202"));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    isDanger=false;
                    startActivity(intentPhone);

                }
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
                //已连接
                if (isAddressOne) {
                    mBLEState = "已连接设备一";
                }
//                if (isAddressTwo) {
//                    mBLEState = "已连接设备二";
//                }
//                if (isCALLPhone && isAddressOne) {
//
//                    //Toast.makeText(getApplicationContext(),"拨打电话",Toast.LENGTH_SHORT).show();
//                    //Intent intentPhone = new Intent(Intent.ACTION_CALL, Uri.parse("tel:15622282202"));
//
//                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                        // TODO: Consider calling
//                        //    ActivityCompat#requestPermissions
//                        // here to request the missing permissions, and then overriding
//                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                        //                                          int[] grantResults)
//                        // to handle the case where the user grants the permission. See the documentation
//                        // for ActivityCompat#requestPermissions for more details.
//                        return;
//                    }
//                    //startActivity(intentPhone);// 将Intent传递给Activity
//                    isCALLPhone=false;
//                }
                meFragment.refreshListView();
                Log.d("Hins", "手环已连接");
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mBLEState = "未连接";
                //=================================转接版2.4===========
                if(!isAddressOne&&!isDanger){
                    BindDeviceDAO bindDeviceDao=new BindDeviceDAO(MainActivity.this);//获取设备一
                    Tb_bind_device_m tb_bind_device_m=bindDeviceDao.find();
                    mDeviceAddress=tb_bind_device_m.getAddressOne();
                    mScanning = true;
                    scanLeDevice(true);
                }
                //=================================转接版2.4===========

                //=================================转接版2.3===========
//                if(isDanger&&isCALLPhone){
//                    mScanning = true;
//                    scanLeDevice(true);
//                }
                //=================================转接版2.3===========
                meFragment.refreshListView();
                Log.d("Hins", "手环未连接");
            }
            //发现有可支持的服务
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                //写数据的服务和characteristic、、//写如数据的characteristic
                mnotyGattService = mBluetoothLeService.getSupportedGattServices(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
                characteristic = mnotyGattService.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
                //读数据的服务和characteristic
                readMnotyGattService = mBluetoothLeService.getSupportedGattServices(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
                readCharacteristic = readMnotyGattService.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
                if (firstconn) {
                    SendData_ReceData("y");//第一次连接获取第二设备地址
                    firstconn=false;
                }
                //=================================转接版2.3===========
//                if(isAddressTwo&&isDanger){
//                       new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            while (isAddressTwo) {
//                                SendData_ReceData("AAA");//发送危险提示
//                                try {
//                                    Thread.sleep(3000);
//                                    ChangeDevice=true;
//                                } catch (InterruptedException e) {
//                                    // TODO 自动生成的 catch 块
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    }).start();
//                }
                //=================================转接版2.3===========
            }
            //显示数据
            else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //将数据显示在mDataField上
                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                displayData(data);
            }
            //=================================转接板2.4=============
//            else if (BluetoothLeService.ACTION_GATT_WRITE.equals(action)) {
//                //如果第二设备写入成功就断开连接
//                if(isAddressTwo&&isDanger&&ChangeDevice){
//                    isDanger=false;
//                    isAddressTwo=false;
//
//                    ChangeDevice=false;
//                    mBluetoothLeService.disconnect();
//                }
//            }
            //=================================转接板2.4=============
        }
    };

    //广播注册
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        //=================================转接板2.4=============
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_WRITE);
        //=================================转接板2.4=============
        return intentFilter;
    }

    public String getBLEState() {
        return this.mBLEState;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT
                && resultCode == RESULT_OK) {//蓝牙已经开启
            BindDeviceDAO bindDeviceDao=new BindDeviceDAO(MainActivity.this);
            //Toast.makeText(getApplicationContext(), "打开了蓝牙", Toast.LENGTH_SHORT).show();
            Tb_bind_device_m tb_bind_device_m=bindDeviceDao.find();
            if(tb_bind_device_m!=null){
                mDeviceAddress=tb_bind_device_m.getAddressOne();
                //Toast.makeText(getApplicationContext(), "连接第一设备", Toast.LENGTH_SHORT).show();
                isAddressOne=true;
                mScanning = true;
                scanLeDevice(true);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //=================================增强版2.2可以用==================================================
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(mDeviceAddress.equals(device.getAddress())){
                                //=================================转接板2.3=============
                                if(isDanger){
                                    isAddressTwo=true;//存在第二设备
                                }
                                //=================================转接板2.3=============
                                //扫描到存在的设备就停止
                                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                                mScanning = false;
                                //=================================转接板2.4=============
                                if(isAddressTwo){
                                    mBluetoothLeService.connect(mDeviceAddress);
                                }else{
                                    isAddressOne=true;
                                    //Toast.makeText(getApplicationContext(), ""+isLeService,Toast.LENGTH_SHORT).show();
                                    if(isLeService){
                                        if(mBluetoothLeService==null){
                                            bind();
                                        }else{
                                            mBluetoothLeService.connect(mDeviceAddress);
                                        }
                                    }else{
                                        bind();
                                    }
                                }
                                //=================================转接板2.4=============
                            }
                        }
                    });
                }
            };


    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    //====================================增强版2.2===============================================

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatuBarColor(int color){
        int sysVersion = Integer.parseInt(Build.VERSION.SDK);
        if (sysVersion<Build.VERSION_CODES.LOLLIPOP){
            return;
        }
        Window window=getWindow();
        window.setStatusBarColor(getResources().getColor(color));
    }
}
