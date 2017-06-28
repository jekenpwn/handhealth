package com.hins.smartband.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hins.smartband.R;
import com.hins.smartband.adapter.MeasureAdapter;
import com.hins.smartband.base.FinishBaseFragment;
import com.hins.smartband.base.MainBaseFragment;
import com.hins.smartband.bean.IconLeftMidRightListBean;
import com.hins.smartband.bean.UserInfoBean;
import com.hins.smartband.database.DBOpenHelper;
import com.hins.smartband.database.SpaceDAO;
import com.hins.smartband.le.BluetoothLeService;
import com.hins.smartband.model.Tb_space_m;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

import static android.os.Looper.getMainLooper;
import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 * @ClassName: HeartRateFragment
 * @Description: 测量心率页面
 */
public class HeartRateFragment extends MainBaseFragment implements View.OnClickListener {
    private View mView;
    private View mHeaderView;

    private View calculatorAnima;
    private ImageView calculatorBtn;
    private ListView mList;
    private Button testBtn;

    private Thread heartbeatThread;
    private Thread sendThread;
    private volatile boolean heartbeat;

    private MeasureAdapter measureAdapter;
    private ArrayList<IconLeftMidRightListBean> datas;

    private MainBaseFragment.CustomFragmentManagerInterFace cfmi;

    private ReceiveBroadCast receiveBroadCast;

    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_heart, viewGroup, false);
        mHeaderView = inflater.inflate(R.layout.fragment_heart_header, viewGroup, false);
        cfmi = getCFMI();
        initReceiveBroadCast();
        initView();
        initDate();
        return mView;
    }

    private void initReceiveBroadCast() {
        // 注册广播接收
        receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("Hins");    //只有持有相同的action的接受者才能接收此广播
        getActivity().registerReceiver(receiveBroadCast, filter);
    }

    private void initDate() {
        SpaceDAO spaceDAO = new SpaceDAO(getContext(),handler);
        datas = spaceDAO.getAllHeartData();
        measureAdapter = new MeasureAdapter(getContext(), datas);
        mList.setAdapter(measureAdapter);
        mList.addHeaderView(mHeaderView);
    }

    public void addDate() {
        SpaceDAO spaceDAO = new SpaceDAO(getContext());
        datas.clear();
        datas.addAll(spaceDAO.getAllHeartData());
        measureAdapter.notifyDataSetChanged();
        spaceDAO.close();
    }

    private void initView() {
        View calculatorBg = (View) mHeaderView.findViewById(R.id.study_tool_calculator_backgroud);
        initAnimationBackground(calculatorBg);
        calculatorAnima = (View) mHeaderView.findViewById(R.id.study_tool_calculator_animation);
        calculatorBtn = (ImageView) mHeaderView.findViewById(R.id.study_tool_calculator);
        calculatorBtn.setOnClickListener(this);
        testBtn = (Button) mHeaderView.findViewById(R.id.btn_test);
        testBtn.setOnClickListener(this);
        mList = (ListView) mView.findViewById(R.id.lv_heart);

    }

    @Override
    public void onClick(View v) {
        if (!cfmi.getBLEState().contains("已连接")) {
            Toast.makeText(getContext(), "请先连接您的手环", Toast.LENGTH_SHORT).show();
        } else {
            startHeartBeat();
            startSend();
            cfmi.getSpaceData();
        }
    }

    private void startSend() {
        if (sendThread == null) {
            sendThread = new SendThread();
        }
        if (!sendThread.isAlive()) {
            sendThread.start();
        }
    }

    private class SendThread extends Thread {
        public void run() {
            while (heartbeat){
                try {
                    Thread.sleep(500);
                    cfmi.getSpaceData();
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class HeatbeatThread extends Thread {

        public void run() {
            do {
                Message message = handler.obtainMessage();
                message.what=0x10;
                handler.sendMessage(message);
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (heartbeat);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==0x10){
                playHeartbeatAnimation(calculatorAnima, calculatorBtn);
            }else {
                measureAdapter.notifyDataSetChanged();
            }
        }
    };

    /**
     * 寮�濮嬪績璺�
     */
    private void startHeartBeat() {
        if (heartbeatThread == null) {
            heartbeatThread = new HeatbeatThread();
            heartbeat = true;
            testBtn.setClickable(false);
            testBtn.setVisibility(View.INVISIBLE);
        }
        if (!heartbeatThread.isAlive()) {
            heartbeatThread.start();
        }
    }

    /**
     * 鍋滄蹇冭烦
     */
    public void stopHeartBeat() {
        if (heartbeatThread != null) {
            heartbeat = false;
            testBtn.setClickable(true);
            testBtn.setVisibility(View.VISIBLE);
            heartbeatThread = null;
            System.gc();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopHeartBeat();
    }

    private void initAnimationBackground(View view) {
        view.setAlpha(0.2f);
        view.setScaleX(1.4f);
        view.setScaleY(1.4f);

    }

    private void playHeartbeatAnimation(final View heartbeatView, final View heartbeaIcon) {
        AnimationSet swellAnimationSet = new AnimationSet(true);
        swellAnimationSet.addAnimation(new ScaleAnimation(1.0f, 2.5f, 1.0f, 2.5f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f));
        swellAnimationSet.addAnimation(new AlphaAnimation(1.0f, 0.3f));

        swellAnimationSet.setDuration(500);
        swellAnimationSet.setInterpolator(new AccelerateInterpolator());
        swellAnimationSet.setFillAfter(true);

        AnimationSet shrinkAnimationIconSet = new AnimationSet(true);
        shrinkAnimationIconSet.addAnimation(new AlphaAnimation(0.3f, 1.0f));
        shrinkAnimationIconSet.setDuration(500);
        shrinkAnimationIconSet.setInterpolator(new DecelerateInterpolator());
        shrinkAnimationIconSet.setFillAfter(false);

        swellAnimationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AnimationSet shrinkAnimationSet = new AnimationSet(true);
                shrinkAnimationSet.addAnimation(new ScaleAnimation(2.0f, 1.0f, 2.0f, 1.0f, Animation.RELATIVE_TO_SELF,
                        0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
                shrinkAnimationSet.addAnimation(new AlphaAnimation(0.3f, 1.0f));
                shrinkAnimationSet.setDuration(1000);
                shrinkAnimationSet.setInterpolator(new DecelerateInterpolator());
                shrinkAnimationSet.setFillAfter(false);
                heartbeatView.startAnimation(shrinkAnimationSet);// 鍔ㄧ敾缁撴潫鏃堕噸鏂板紑濮嬶紝瀹炵幇蹇冭烦鐨刅iew
                AnimationSet shrinkAnimationIconSet = new AnimationSet(true);
                shrinkAnimationIconSet.addAnimation(new AlphaAnimation(1.0f, 0.3f));
                shrinkAnimationIconSet.setDuration(1000);
                shrinkAnimationIconSet.setInterpolator(new DecelerateInterpolator());
                shrinkAnimationIconSet.setFillAfter(false);
                heartbeaIcon.clearAnimation();
                heartbeaIcon.startAnimation(shrinkAnimationIconSet);
            }
        });

        heartbeatView.startAnimation(swellAnimationSet);
        heartbeaIcon.startAnimation(shrinkAnimationIconSet);
    }

    public class ReceiveBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //得到广播中得到的数据，并显示出来
            stopSend();
            stopHeartBeat();
            addDate();
            updateDateToBmob();
        }

    }

    public void stopSend() {
        if (sendThread != null) {
            sendThread = null;
            System.gc();
        }
    }

    public void updateDateToBmob() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SpaceDAO spaceDAO = new SpaceDAO(getContext());
                final Tb_space_m tb_space_m = spaceDAO.getLastData();
                if (tb_space_m == null) {
                    return;
                }
                UserInfoBean user = BmobUser.getCurrentUser(UserInfoBean.class);
                tb_space_m.setUObjectId(user.getObjectId());
                tb_space_m.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e != null) {
                            Toast.makeText(getActivity(), "网络不好，上传数据失败", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "已上传", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                spaceDAO.close();
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiveBroadCast);
    }

}
