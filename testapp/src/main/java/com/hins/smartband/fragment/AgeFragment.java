package com.hins.smartband.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.hins.smartband.R;
import com.hins.smartband.base.FinishBaseFragment;
import com.hins.smartband.bean.UserInfoBean;
import com.hins.smartband.database.AimDAO;
import com.hins.smartband.model.Tb_aim_m;
import com.hins.smartband.tools.GetAimStep;

import java.util.Calendar;

/**
 * @ClassName: AgeFragment
 * @Description: 选择年龄段页面
 */
public class AgeFragment extends FinishBaseFragment implements View.OnClickListener {

    private int year; // 年
    private int month;
    private int day;
    private String birth;

    private View mView;
    /**
     * 调用Activity方法的接口
     */
    private CustomFragmentManagerInterFace cfmi;

    private DatePicker mDatePicker;

    private TextView sure_tv;

    private UserInfoBean user;

    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        cfmi = getCFMI();//获取父类调用Activity的内部接口
        user = cfmi.getUserInfoBean();
        mView = inflater.inflate(R.layout.fragment_age, viewGroup, false);
        initView(mView);
        return mView;
    }

    private void initView(View mView) {
        sure_tv= (TextView) mView.findViewById(R.id.tv_age_sure);
        sure_tv.setOnClickListener(this);
        mDatePicker = (DatePicker) mView.findViewById(R.id.dp_age);
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR); // 获取当前年份
        month = calendar.get(Calendar.MONTH); // 获取当前月份
        day = calendar.get(Calendar.DAY_OF_MONTH); // 获取当前日
        birth = year + "-" + (month++) + "-" + day;
        // 初始化日期拾取器，并在初始化时指定监听器
        mDatePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker arg0, int year, int month,
                                      int day) {
                AgeFragment.this.birth = year + "-" + (month++) + "-" + day;// 改变day属性的值
            }
        });

    }

    @Override
    public void onClick(View v) {
        user.setBirth(birth);
        cfmi.initFragment();

    }
}
