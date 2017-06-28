package com.hins.smartband.base;

import android.app.Activity;
import android.support.v4.app.Fragment;


import com.hins.smartband.bean.UserInfoBean;

public class MainBaseFragment extends Fragment {
    CustomFragmentManagerInterFace cfmi;

    public CustomFragmentManagerInterFace getCFMI(){
        return cfmi;
    }
    /*
    *定义一个接口让Activity实现，用于Fragment与Activity通信
    */
    public interface CustomFragmentManagerInterFace{

        public String getBLEState();

        public void getSpaceData();

        public void bind();

        public void setDeviceName(String d);

        public void setDeviceAddress(String d);

        public void setIsDanger(boolean isAddressOne);

        public void setIsAddressOne(boolean isAddressOne);

        public void setIsAddressTwo(boolean isAddressOne);

        public void setIsCallPhone(boolean isAddressOne);

        public void setIsLeService(boolean isAddressOne);

        public void setChangeDevice(boolean isAddressOne);

        public boolean getFirstConn();
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
