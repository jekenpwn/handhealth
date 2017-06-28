package com.hins.smartband.activities;

import android.os.Bundle;
import android.os.Handler;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hins.smartband.R;
import com.hins.smartband.database.DeviceDAO;
import com.hins.smartband.le.DeviceControlActivity;
import com.hins.smartband.model.Tb_device_m;

public class binderDevice extends ListActivity {
	private LeDeviceListAdapter mLeDeviceListAdapter;
	private boolean mScanning;
	private BluetoothAdapter mBluetoothAdapter;
	private Handler mHandler;
	private static final int REQUEST_ENABLE_BT = 1;
	private static final long SCAN_PERIOD = 10000;// 10s后停止扫描

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		mHandler=new Handler();
		//判断是否有蓝牙功能
		if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
			Toast.makeText(this,R.string.ble_not_supported,Toast.LENGTH_SHORT).show();
			finish();
		}
		//获得蓝牙管理器对象
		final BluetoothManager bluetoothManager=(BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter=bluetoothManager.getAdapter();
		if(mBluetoothAdapter == null){
			Toast.makeText(this, R.string.error_bluetooth_not_supported,Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
	}

	@Override
	protected void onPause() {
		// TODO 自动生成的方法存根
		super.onPause();
		scanLeDevice(false);
		mLeDeviceListAdapter.clear();
	}



	@Override
	protected void onResume() {
		// TODO 自动生成的方法存根
		super.onResume();
		//判断蓝牙功能是否开启
//		if(!mBluetoothAdapter.isEnabled()){
//			Intent enableBtIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//			startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
//		}
		mLeDeviceListAdapter=new LeDeviceListAdapter();
		setListAdapter(mLeDeviceListAdapter);
		scanLeDevice(true);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//对用户的选项进行判断，如果不打开就推出软件
		// TODO 自动生成的方法存根
		if(requestCode==REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED){
			finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void init(){
		DeviceDAO deviceDao=new DeviceDAO(binderDevice.this);
		if(deviceDao.getCount()!=0){
			deviceDao.clearData();
			deviceDao.close();
		}
	}

	private void scanLeDevice(final boolean enable){
		if(enable){
			mHandler.postDelayed(new Runnable(){
				@Override
				public void run() {
					// TODO 自动生成的方法存根
					DeviceDAO deviceDao=new DeviceDAO(binderDevice.this);
					mScanning=false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					invalidateOptionsMenu();
//					if(deviceDao.find("BT05")!=null){
//						Tb_device_m tb_device=deviceDao.find("BT05");
//						final Intent intent=new Intent(binderDevice.this,DeviceControlActivity.class);
//						intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME,tb_device.getName());
//						intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS,tb_device.getAddress());
//						if(deviceDao!=null)
//						{
//							deviceDao.close();
//						}
//						startActivity(intent);
//					}
				}
			}, SCAN_PERIOD);
			mScanning=true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		}else{
			mScanning=false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
		invalidateOptionsMenu();
	}
	private BluetoothAdapter.LeScanCallback mLeScanCallback =
			new BluetoothAdapter.LeScanCallback() {
				@Override
				public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
					// TODO 自动生成的方法存根
					runOnUiThread(new Runnable(){
						@Override
						public void run() {
							DeviceDAO deviceDao=new DeviceDAO(binderDevice.this);//保存到数据库
							//判断name
							if(device!=null&&!deviceDao.isExist(device.getName())){
								Tb_device_m tb_device=new Tb_device_m(device.getName(),device.getAddress());
								deviceDao.add(tb_device);//保存到数据库
								deviceDao.close();
								Log.d("Hins","手环地址已保存到数据库");
							}
							mLeDeviceListAdapter.addDevice(device);
							mLeDeviceListAdapter.notifyDataSetChanged();
						}

					});
				}
			};

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		final BluetoothDevice device=mLeDeviceListAdapter.getDevice(position);
		DeviceDAO deviceDao=new DeviceDAO(binderDevice.this);//保存到数据库
		if (deviceDao.getCount()!=0){
			deviceDao.clearData();
		}
		Tb_device_m tb_device=new Tb_device_m(device.getName(),device.getAddress());
		deviceDao.add(tb_device);//保存到数据库
		deviceDao.close();
		Intent intent = getIntent();
		Bundle data = new Bundle();
		data.putString(MainActivity.EXTRAS_DEVICE_NAME, device.getName());
		data.putString(MainActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
		intent.putExtras(data);
		if(mScanning){
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			mScanning=false;
		}
		binderDevice.this.setResult(MainActivity.RESULT, intent);
		binderDevice.this.finish();
//		if(device == null)return ;
//		final Intent intent = new Intent(binderDevice.this, DeviceControlActivity.class);
//        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
//        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
//		if(mScanning){
//			mBluetoothAdapter.stopLeScan(mLeScanCallback);
//			mScanning=false;
//		}
//        startActivity(intent);

	}

	// Adapter for holding devices found through scanning.
	private class LeDeviceListAdapter extends BaseAdapter {
		private ArrayList<BluetoothDevice> mLeDevices;
		private LayoutInflater mInflator;

		public LeDeviceListAdapter() {
			super();
			mLeDevices = new ArrayList<BluetoothDevice>();
			mInflator = binderDevice.this.getLayoutInflater();
		}

		public void addDevice(BluetoothDevice device) {
			if(!mLeDevices.contains(device)) {
				mLeDevices.add(device);
			}
		}

		public BluetoothDevice getDevice(int position) {
			return mLeDevices.get(position);
		}

		public void clear() {
			mLeDevices.clear();
		}

		@Override
		public int getCount() {
			return mLeDevices.size();
		}

		@Override
		public Object getItem(int i) {
			return mLeDevices.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			ViewHolder viewHolder;
			// General ListView optimization code.
			if (view == null) {
				view = mInflator.inflate(R.layout.device_list, null);
				viewHolder = new ViewHolder();
				viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
				viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			BluetoothDevice device = mLeDevices.get(i);
			final String deviceName = device.getName();
			if (deviceName != null && deviceName.length() > 0)
				viewHolder.deviceName.setText(deviceName);
			else
				viewHolder.deviceName.setText(R.string.unknown_device);
			viewHolder.deviceAddress.setText(device.getAddress());

			return view;
		}
	}

	static class ViewHolder {
		TextView deviceName;
		TextView deviceAddress;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//返回Home桌面
		if(keyCode==KeyEvent.KEYCODE_BACK) {
			if(mScanning){
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
				mScanning=false;
			}
			Intent intent = getIntent();
			binderDevice.this.setResult(987, intent);
			binderDevice.this.finish();
			return false;
		}
		return false;
	}
}