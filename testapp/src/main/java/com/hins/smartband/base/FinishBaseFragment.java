package com.hins.smartband.base;

import android.app.Activity;
import android.app.Fragment;

import com.hins.smartband.bean.UserInfoBean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FinishBaseFragment extends Fragment {
    CustomFragmentManagerInterFace cfmi;

    public CustomFragmentManagerInterFace getCFMI(){
        return cfmi;
    }
    /*
    *定义一个接口让Activity实现，用于Fragment与Activity通信
    */
    public interface CustomFragmentManagerInterFace{

        public UserInfoBean getUserInfoBean();

        public void initFragment();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // 这个方法是用来确认当前的Activity容器是否已经继承了该接口，如果没有将抛出异常
        try {
            cfmi = (CustomFragmentManagerInterFace) activity;
        } catch (ClassCastException e) {
        }
    }

}
