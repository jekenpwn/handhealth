package com.hins.smartband.fragment;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hins.smartband.R;
import com.hins.smartband.activities.PersonInfoActivity;
import com.hins.smartband.adapter.IconLeftListAdapter;
import com.hins.smartband.adapter.IconLeftRightListAdapter;
import com.hins.smartband.adapter.StepAdapter;
import com.hins.smartband.base.MainBaseFragment;
import com.hins.smartband.bean.IconLeftListBean;
import com.hins.smartband.bean.IconLeftRightListBean;
import com.hins.smartband.bean.UserInfoBean;
import com.hins.smartband.database.AimDAO;
import com.hins.smartband.database.SpaceDAO;
import com.hins.smartband.le.DeviceControlActivity;
import com.hins.smartband.model.Tb_aim_m;
import com.hins.smartband.model.Tb_space_m;

import java.util.ArrayList;

import cn.bmob.v3.BmobUser;

/**
 * @ClassName: MeFragment
 * @Description: 个人信息页面
 */
public class StepFragment extends MainBaseFragment {

    private UserInfoBean user;

    private View mView;

    private TextView step_tv, aim_tv, gongli_tv;

    private ListView step_lv;

    private CustomFragmentManagerInterFace cfmi;

    private ReceiveBroadCast receiveBroadCast;

    private ArrayList<IconLeftRightListBean> datas;

    private StepAdapter stepAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_step, viewGroup, false);
        cfmi = getCFMI();
        user=BmobUser.getCurrentUser(UserInfoBean.class);
        initReceiveBroadCast();
        initView(mView);
        initListView();
        return mView;
    }

    private void initReceiveBroadCast() {
        // 注册广播接收
        receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("Hins");    //只有持有相同的action的接受者才能接收此广播
        getActivity().registerReceiver(receiveBroadCast, filter);
    }

    private void initListView() {
        SpaceDAO spaceDAO = new SpaceDAO(getContext());
        Tb_space_m tb_space_m = spaceDAO.getLastStepDate();
        datas = new ArrayList<IconLeftRightListBean>();
        datas.add(new IconLeftRightListBean(R.drawable.ic_sleep, "睡眠时间", tb_space_m.getSleep()));
        datas.add(new IconLeftRightListBean(R.drawable.ic_markup, "起床时间", tb_space_m.getMakeup()));
        if (tb_space_m.getDanger().equals("0")) {
            datas.add(new IconLeftRightListBean(R.drawable.ic_state, "状态", "正常"));
        } else {
            if (tb_space_m.getDanger().equals("1")){
                datas.add(new IconLeftRightListBean(R.drawable.ic_state, "状态", "心率异常"));
            }else {
                datas.add(new IconLeftRightListBean(R.drawable.ic_state, "状态", "摔倒"));
            }
        }
        stepAdapter = new StepAdapter(getContext(), datas);
        step_lv.setAdapter(stepAdapter);
        step_tv.setText(tb_space_m.getRunning());
    }

    private void initView(View mView) {
        step_tv = (TextView) mView.findViewById(R.id.tv_step_step);
        aim_tv = (TextView) mView.findViewById(R.id.tv_step_aim);
        gongli_tv = (TextView) mView.findViewById(R.id.tv_step_gongli);
        step_lv = (ListView) mView.findViewById(R.id.lv_step);

    }

    @Override
    public void onResume() {
        super.onResume();
        //
        AimDAO aimDAO=new AimDAO(getActivity());
        Tb_aim_m tb_aim_m=aimDAO.find(user.getObjectId());
        aim_tv.setText(tb_aim_m.getStep());
        gongli_tv.setText(tb_aim_m.getKmile());
        aimDAO.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiveBroadCast);
    }

    public class ReceiveBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            refreshDatas();
        }

    }

    private void refreshDatas() {
        SpaceDAO spaceDAO = new SpaceDAO(getContext());
        Tb_space_m tb_space_m = spaceDAO.getLastStepDate();
        datas.clear();
        datas.add(new IconLeftRightListBean(R.drawable.ic_sleep, "睡眠时间", tb_space_m.getSleep()));
        datas.add(new IconLeftRightListBean(R.drawable.ic_markup, "起床时间", tb_space_m.getMakeup()));
        if (tb_space_m.getDanger().equals("0")) {
            datas.add(new IconLeftRightListBean(R.drawable.ic_state, "状态", "正常"));
        } else {
            if (tb_space_m.getDanger().equals("1")){
                datas.add(new IconLeftRightListBean(R.drawable.ic_state, "状态", "心率异常"));
            }else {
                datas.add(new IconLeftRightListBean(R.drawable.ic_state, "状态", "摔倒"));
            }
        }
        stepAdapter.notifyDataSetChanged();
        step_tv.setText(tb_space_m.getRunning().trim());

    }

}
