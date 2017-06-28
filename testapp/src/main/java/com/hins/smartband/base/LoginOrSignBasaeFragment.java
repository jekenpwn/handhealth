package com.hins.smartband.base;

import android.app.Activity;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginOrSignBasaeFragment extends Fragment {
    CustomFragmentManagerInterFace cfmi;

    public CustomFragmentManagerInterFace getCFMI(){
        return cfmi;
    }
    /*
    *定义一个接口让Activity实现，用于Fragment与Activity通信
    */
    public interface CustomFragmentManagerInterFace{

        public void setResetPwdFragment(String username);

        public void setSignupFragment(String username);

        public void setLoginFragment();

        public void setDefaultFragment();
    }

    //判断手机格式是否正确
    public boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    //判断email格式是否正确
    public boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    //判断是否全是数字
    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
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
