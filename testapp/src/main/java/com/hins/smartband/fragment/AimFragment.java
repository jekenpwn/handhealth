package com.hins.smartband.fragment;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.hins.smartband.R;
import com.hins.smartband.adapter.LeftListAdapter;
import com.hins.smartband.base.BaseActivity;
import com.hins.smartband.bean.LeftListBean;
import com.hins.smartband.bean.UserInfoBean;
import com.hins.smartband.database.AimDAO;
import com.hins.smartband.database.SpaceDAO;
import com.hins.smartband.model.Tb_aim_m;
import com.hins.smartband.model.Tb_space_m;
import com.hins.smartband.tools.DealTime;

import java.io.File;
import java.util.ArrayList;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @ClassName: AimFragment
 * @Description: 目标页面
 */
public class AimFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private View mView;

    private TextView step_tv,sleep_tv;
    private SeekBar step_seekbar,sleep_seekbar;
    private Switch tip_switch;
    private Button save_btn;

    private Tb_aim_m tb_aim_m;

    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_aim, viewGroup, false);
        initView(mView);
        initDates();
        return mView;
    }

    private void initDates() {
        String uObjectId=BmobUser.getCurrentUser().getObjectId();
        AimDAO aimDao=new AimDAO(getActivity());
        tb_aim_m=aimDao.find(uObjectId);
        step_tv.setText(tb_aim_m.getStep());
        sleep_tv.setText(tb_aim_m.getSleep());
        step_seekbar.setProgress(Integer.parseInt(tb_aim_m.getStep()));
        sleep_seekbar.setProgress(DealTime.toSecond(tb_aim_m.getSleep()));
        if (tb_aim_m.getTip().equals("true")){
            tip_switch.setChecked(true);
        }else {
            tip_switch.setChecked(false);
        }
        aimDao.close();
    }


    private void initView(View mView) {
        step_tv= (TextView) mView.findViewById(R.id.tv_aim_step);
        sleep_tv= (TextView) mView.findViewById(R.id.tv_aim_sleep);
        step_seekbar= (SeekBar) mView.findViewById(R.id.seekbar_aim_step);
        step_seekbar.setOnSeekBarChangeListener(this);
        sleep_seekbar= (SeekBar) mView.findViewById(R.id.seekbar_aim_sleep);
        sleep_seekbar.setOnSeekBarChangeListener(this);
        tip_switch= (Switch) mView.findViewById(R.id.switch_aim_tip);
        tip_switch.setOnCheckedChangeListener(this);
        save_btn= (Button) mView.findViewById(R.id.btn_aim_save);
        save_btn.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked){
            tb_aim_m.setTip("true");
            //
        }else {
            tb_aim_m.setTip("false");

        }
    }

    @Override
    public void onClick(View v) {
        AimDAO aimDao=new AimDAO(getActivity());
        aimDao.update(tb_aim_m);
        aimDao.close();
        updateAimToBmob();
        Toast.makeText(getActivity(),"保存成功",Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser){
            if (seekBar.equals(step_seekbar)){
                step_tv.setText(String.valueOf(progress));
                tb_aim_m.setStep(String.valueOf(progress));
            }else {
                sleep_tv.setText(DealTime.toTime(progress));
                tb_aim_m.setSleep(DealTime.toTime(progress));
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void updateAimToBmob() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                tb_aim_m.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e==null){
                            Log.d("bmob", "上传aim数据成功");
                        }else {
                            Log.d("bmob", "上传aim数据失败");
                        }
                    }
                });
            }
        }).start();
    }
}
