package com.hins.smartband.base;

import android.app.Activity;
import android.app.Fragment;

import com.hins.smartband.bean.UserInfoBean;

public class PersonInfoBaseFragment extends Fragment {

    CustomFragmentManagerInterFace cfmi;

    public CustomFragmentManagerInterFace getCFMI(){
        return cfmi;
    }
    /*
    *定义一个接口让Activity实现，用于Fragment与Activity通信
    */
    public interface CustomFragmentManagerInterFace{

        public UserInfoBean getUserInfoBean();

        public void setPersonInfoFragment();

        public void setSmartBandFragment();

        public void setMyAimFragment();

        public void setFeedBackFragment();

        public void setOptionFragment();

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
