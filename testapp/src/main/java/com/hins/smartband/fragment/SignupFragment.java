package com.hins.smartband.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hins.smartband.R;
import com.hins.smartband.base.LoginOrSignBasaeFragment;
import com.hins.smartband.bean.UserInfoBean;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * @ClassName: SignupFragment
 * @Description: 注册页面
 */
public class SignupFragment extends LoginOrSignBasaeFragment implements View.OnClickListener {
    private View mView;
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 用于调用Activity方法的接口
     */
    private CustomFragmentManagerInterFace cfmi;

    private LinearLayout sCodeLinearLayout;
    private EditText usernameEdt, pwdEdt, confirmEdt, sCodeEdt;
    private TextView getSCodeTv, loginTv, resetTv;
    private Button btn_signup;

    private String initUsername;
    private static final String BUNDLE_USERNAME = "username";

    private MyCountTimer timer;

    class MyCountTimer extends CountDownTimer {

        public MyCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            getSCodeTv.setText((millisUntilFinished / 1000) + "秒后重发");
            getSCodeTv.setClickable(false);
        }

        @Override
        public void onFinish() {
            getSCodeTv.setText("获取验证码");
            getSCodeTv.setClickable(true);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            initUsername = bundle.getString(BUNDLE_USERNAME);
        }
        if (initUsername == null) {
            initUsername = "";
        }
        cfmi = getCFMI();//获取父类调用Activity的内部接口
        mView = inflater.inflate(R.layout.fragment_signup, viewGroup, false);
        initView(mView);
        return mView;
    }

    private void initView(View mView) {
        usernameEdt = (EditText) mView.findViewById(R.id.edt_signup_username);
        usernameEdt.setText(initUsername);
        pwdEdt = (EditText) mView.findViewById(R.id.edt_signup_pwd);
        confirmEdt = (EditText) mView.findViewById(R.id.edt_signup_pwd_confirm);
        sCodeLinearLayout = (LinearLayout) mView.findViewById(R.id.ll_signup_securityCode);
        sCodeEdt = (EditText) mView.findViewById(R.id.edt_signup_securityCode);
        getSCodeTv = (TextView) mView.findViewById(R.id.tv_signup_getSecurityCode);
        getSCodeTv.setOnClickListener(this);
        loginTv = (TextView) mView.findViewById(R.id.tv_signup_login);
        loginTv.setOnClickListener(this);
        resetTv = (TextView) mView.findViewById(R.id.tv_signup_reset);
        resetTv.setOnClickListener(this);
        btn_signup = (Button) mView.findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(this);
    }

    public static SignupFragment newInstance(String username) {
        /*
        *获取前一个Fragment的username并传递到Argment，返回实例
        * */
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_USERNAME, username);

        SignupFragment fragment = new SignupFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onClick(View v) {
        String username = usernameEdt.getText().toString();
        int id = v.getId();
        switch (id) {
            case R.id.tv_signup_getSecurityCode:
                requestSCode(username);
                break;
            case R.id.btn_signup:
                String pwd = pwdEdt.getText().toString();
                String confirmPwd = confirmEdt.getText().toString();
                String sCode = sCodeEdt.getText().toString();
                signup(username, pwd, confirmPwd, sCode);
                break;
            case R.id.tv_signup_login:
                cfmi.setLoginFragment();
                break;
            case R.id.tv_signup_reset:
                cfmi.setResetPwdFragment(username);
                break;
        }
    }

    public void signup(final String username, String pwd, String confirmPwd, String sCode) {
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getActivity(), "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isNumeric(username)) {
            if (!isMobileNO(username)) {
                Toast.makeText(getActivity(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                return;
            } else {
                if (sCode.equals("")){
                    sCodeLinearLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "请输入验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } else {
            if (!isEmail(username)) {
                Toast.makeText(getActivity(), "请输入正确的电子邮箱", Toast.LENGTH_SHORT).show();
                return;
            } else {
                sCodeLinearLayout.setVisibility(View.GONE);
            }
        }
        if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(getActivity(), "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pwd.length() < 6 || pwd.length() > 12) {
            Toast.makeText(getActivity(), "密码不能少于6位或多于12位", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pwd.equals(confirmPwd)) {
            Toast.makeText(getActivity(), "请输入一样的密码", Toast.LENGTH_SHORT).show();
            return;
        }
        UserInfoBean uf = new UserInfoBean();
        uf.setUsername(username);
        uf.setPassword(pwd);
        uf.setNickname(username);
        if (isMobileNO(username)) {
            uf.setMobilePhoneNumber(username);
            uf.setMobilePhoneNumberVerified(true);
            uf.signOrLogin(sCode, new SaveListener<UserInfoBean>() {
                @Override
                public void done(UserInfoBean user, BmobException e) {
                    if (e == null) {
                        Toast.makeText(getActivity(), "注册成功", Toast.LENGTH_SHORT).show();
                        //跳转到登录Fragment
                        cfmi.setLoginFragment();
                    } else {
                        Toast.makeText(getActivity(), "注册失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Hins",e.getErrorCode()+"");
                    }
                }
            });
        } else {
            uf.setEmail(username);
            uf.signUp(new SaveListener<UserInfoBean>() {
                @Override
                public void done(UserInfoBean userInfoBean, BmobException e) {
                    if (e == null) {
                        Toast.makeText(getActivity(), "注册成功,请尽快进行邮箱验证", Toast.LENGTH_SHORT).show();
                        //跳转到登录Fragment
                        cfmi.setLoginFragment();
                    } else {
                        Toast.makeText(getActivity(), "注册失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Hins",e.getErrorCode()+"");
                    }
                }
            });
        }


    }

    public void requestSCode(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(getActivity(), "请先输入手机号", Toast.LENGTH_SHORT).show();
        } else {
            timer = new MyCountTimer(60000, 1000);
            timer.start();
            BmobSMS.requestSMSCode(phoneNumber, "Hins", new QueryListener<Integer>() {
                @Override
                public void done(Integer integer, BmobException e) {
                    if (e == null) {
                        Toast.makeText(getActivity(), "验证码发送成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "请输入正确的手机号再试", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}
