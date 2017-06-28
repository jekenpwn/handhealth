package com.hins.smartband.fragment;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hins.smartband.R;
import com.hins.smartband.activities.MainActivity;
import com.hins.smartband.activities.PersonInfoActivity;
import com.hins.smartband.activities.binderDevice;
import com.hins.smartband.adapter.IconLeftListAdapter;
import com.hins.smartband.adapter.IconLeftRightListAdapter;
import com.hins.smartband.base.MainBaseFragment;
import com.hins.smartband.bean.IconLeftListBean;
import com.hins.smartband.bean.IconLeftRightListBean;
import com.hins.smartband.bean.UserInfoBean;
import com.hins.smartband.database.BindDeviceDAO;
import com.hins.smartband.le.BluetoothLeService;
import com.hins.smartband.le.DeviceControlActivity;
import com.hins.smartband.model.Tb_bind_device_m;

import java.util.ArrayList;

import cn.bmob.v3.BmobUser;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * @ClassName: MeFragment
 * @Description: 个人信息页面
 */
public class MeFragment extends MainBaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private UserInfoBean user;

    private View mView;

    private ImageView icon_iv;

    private TextView nickname_tv, height_tv, weight_tv, bmi_tv;

    private ListView smart_lv, option_lv;

    private static final int PERSONINFO_FRAGMENT = 23;
    public static final int REQUEST=0;
    public static final int RESULT=0;
    private String mDeviceName=null;
    private String mDeviceAddress=null;
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private String BLEState;

    private CustomFragmentManagerInterFace cfmi;

    private ArrayList<IconLeftRightListBean> smartDatas;
    private ArrayList<IconLeftListBean> optionDatas;

    private IconLeftRightListAdapter iconLeftRightListAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_me, viewGroup, false);
        cfmi=getCFMI();
        BLEState=cfmi.getBLEState();
        initView(mView);
        initDatas();
        initListView();
        return mView;
    }

    private void initListView() {
        smartDatas = new ArrayList<IconLeftRightListBean>();
        optionDatas = new ArrayList<IconLeftListBean>();
        smartDatas.add(new IconLeftRightListBean(R.drawable.ic_smartband, "智能手环", BLEState));
        smartDatas.add(new IconLeftRightListBean(R.drawable.ic_myaim, "我的目标", ""));
        optionDatas.add(new IconLeftListBean(R.drawable.ic_option, "设置"));
        iconLeftRightListAdapter=new IconLeftRightListAdapter(getContext(), smartDatas);
        smart_lv.setAdapter(iconLeftRightListAdapter);
        option_lv.setAdapter(new IconLeftListAdapter(getContext(), optionDatas));
        smart_lv.setOnItemClickListener(this);
        option_lv.setOnItemClickListener(this);

    }

    public void initDatas() {
        user = BmobUser.getCurrentUser(UserInfoBean.class);
        if (user != null) {
            nickname_tv.setText(user.getNickname());
            height_tv.setText(user.getHeight() + "");
            weight_tv.setText(user.getWeight() + "");
            bmi_tv.setText(user.getBMI() + "");
            Bitmap icon = BitmapFactory.decodeFile(getActivity().getFilesDir() + "/" + user.getUsername() + "/"+user.getObjectId()+"_icon_head.png");
            if (icon != null) {
                icon_iv.setImageBitmap(icon);
            }

        }

    }

    private void initView(View mView) {
        nickname_tv = (TextView) mView.findViewById(R.id.tv_user_nickname);
        icon_iv = (ImageView) mView.findViewById(R.id.iv_user_ic);
        height_tv = (TextView) mView.findViewById(R.id.tv_user_height);
        weight_tv = (TextView) mView.findViewById(R.id.tv_user_weight);
        bmi_tv = (TextView) mView.findViewById(R.id.tv_user_bmi);
        smart_lv = (ListView) mView.findViewById(R.id.lv_me_smart);
        option_lv = (ListView) mView.findViewById(R.id.lv_me_option);

        nickname_tv.setOnClickListener(this);
        icon_iv.setOnClickListener(this);
        height_tv.setOnClickListener(this);
        weight_tv.setOnClickListener(this);
        bmi_tv.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String listview = parent.toString().substring(parent.toString().lastIndexOf("/") + 1, parent.toString().length() - 1);
        if (listview.equals("lv_me_smart")) {
            if (position == 0) {
                Intent intent = new Intent(getActivity(), binderDevice.class);
                startActivityForResult(intent,REQUEST);
            } else {
                Intent intent = new Intent(getActivity(), PersonInfoActivity.class);
                intent.putExtra("target", "myaimFragment");
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(getActivity(), PersonInfoActivity.class);
            intent.putExtra("target", "optionFragment");
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), PersonInfoActivity.class);
        intent.putExtra("target", "personInfoFragment");
        startActivityForResult(intent,PERSONINFO_FRAGMENT );
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshListView();
        initDatas();
    }

    public void refreshListView() {
        BLEState=cfmi.getBLEState();
        smartDatas.clear();
        smartDatas.add(new IconLeftRightListBean(R.drawable.ic_smartband, "智能手环", BLEState));
        smartDatas.add(new IconLeftRightListBean(R.drawable.ic_myaim, "我的目标", ""));
        iconLeftRightListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode== REQUEST&&resultCode==RESULT){
            Bundle resultdata=data.getExtras();
            cfmi.setDeviceName(resultdata.getString(EXTRAS_DEVICE_NAME));
            cfmi.setDeviceAddress(resultdata.getString(EXTRAS_DEVICE_ADDRESS));
            cfmi.bind();
            BLEState="未连接";
            refreshListView();
            //=================================转接版2.3========
            cfmi.setIsAddressOne(true);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
