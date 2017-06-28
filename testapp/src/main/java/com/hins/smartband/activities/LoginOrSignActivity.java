package com.hins.smartband.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.hins.smartband.R;
import com.hins.smartband.base.BaseActivity;
import com.hins.smartband.base.LoginOrSignBasaeFragment;
import com.hins.smartband.bean.UserInfoBean;
import com.hins.smartband.database.AimDAO;
import com.hins.smartband.fragment.LoginFragment;
import com.hins.smartband.fragment.ResetPwdFragment;
import com.hins.smartband.fragment.SignupFragment;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

/**
 * 程序员： Hins on 2016/7/14.
 * 描述：登录、忘记密码、注册Fragment的容器Activity，实现BaseFragment的接口用于与三个Fragment通信
 */
public class LoginOrSignActivity extends BaseActivity implements LoginOrSignBasaeFragment.CustomFragmentManagerInterFace {

    private int REQUEST_CODE=0x111;

    private FragmentManager fm;
    private LoginFragment mLogin;
    private ResetPwdFragment mReset;
    private SignupFragment mSignup;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginorsign);

        Bmob.initialize(this, "3dc8e15b74ca1e00fb7b592ba6da4975");//初始化Bmob云

        UserInfoBean user= BmobUser.getCurrentUser(UserInfoBean.class);
        if(user != null){
            // 允许用户使用应用
            if(user.whichUserInfoUnFinish()==0){
                Intent intent=new Intent(LoginOrSignActivity.this,MainActivity.class);
                startActivity(intent);

            }else {
                Intent intent=new Intent(LoginOrSignActivity.this,FinishUserInfoActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
                startActivity(intent);
            }
        }else{
            //缓存用户对象为空时， 可打开用户注册界面…
            setDefaultFragment();

        }

        //jeken
        //startActivity(new Intent(this,MainActivity.class));
    }

    /*
    *功能：设置默认的第一个Fragment—LoginFragmnent
    *参数：
    *返回类型：
    */
    public void setDefaultFragment() {
        mLogin = new LoginFragment();
        mReset = new ResetPwdFragment();
        mSignup = new SignupFragment();
        fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.loginorsign_content, mLogin,"fragment");
        ft.commit();
    }

    @Override
    /*
    *功能：把当前的Fragment压到回退栈，显示忘记密码Fragment
    *参数：[fragment, username]
    *返回类型：void
    */
    public void setResetPwdFragment(String username) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment=getFragmentManager().findFragmentByTag("fragment");
        ft.hide(fragment);
        ft.add(R.id.loginorsign_content,mReset,"fragment");
        //ft.replace(R.id.loginorsign_content, mReset);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    /*
    *功能：把当前的Fragment压到回退栈，显示注册Fragment
    *参数：[fragment, username]
    *返回类型：void
    */
    public void setSignupFragment(String username) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment=getFragmentManager().findFragmentByTag("fragment");
        ft.hide(fragment);
        ft.add(R.id.loginorsign_content,mSignup,"fragment");
        //ft.replace(R.id.loginorsign_content, mSignup);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    /*
    *功能：把回退栈中的登录Fragment推出并显示
    *参数：[]
    *返回类型：void
    */
    public void setLoginFragment() {
        fm.popBackStackImmediate(null, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==REQUEST_CODE&&requestCode==resultCode){
            Intent intent=new Intent(LoginOrSignActivity.this,MainActivity.class);
            startActivity(intent);
            //
            AimDAO aimDAO=new AimDAO(this);
            aimDAO.close();
        }
    }
}
