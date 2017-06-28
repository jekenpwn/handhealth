package com.hins.smartband.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hins.smartband.R;
import com.hins.smartband.base.FinishBaseFragment;
import com.hins.smartband.bean.UserInfoBean;
import com.hins.smartband.database.AimDAO;
import com.hins.smartband.model.Tb_aim_m;
import com.hins.smartband.tools.GetAimStep;

/**
 * @ClassName: SexFragment
 * @Description: 选择性别页面
 */
public class SexFragment extends FinishBaseFragment implements View.OnClickListener {
    private View mView;
    /**
     * 调用Activity方法的接口
     */
    CustomFragmentManagerInterFace cfmi;

    private TextView manTv, womanTv;

    private UserInfoBean user;

    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        cfmi = getCFMI();//获取父类调用Activity的内部接口
        user = cfmi.getUserInfoBean();
        mView = inflater.inflate(R.layout.fragment_sex, viewGroup, false);
        initView(mView);
        return mView;
    }

    private void initView(View mView) {
        manTv = (TextView) mView.findViewById(R.id.tv_sex_man);
        manTv.setOnClickListener(this);
        womanTv = (TextView) mView.findViewById(R.id.tv_sex_woman);
        womanTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_sex_man:
                user.setSex("男");
                cfmi.initFragment();
                break;
            case R.id.tv_sex_woman:
                user.setSex("女");
                cfmi.initFragment();
                break;
        }

    }


}
