package com.hins.smartband.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
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
import cn.bmob.v3.listener.UpdateListener;

/**
 * @ClassName: ResetPwdFragment
 * @Description: 重置密码页面
 */
public class ResetPwdFragment extends LoginOrSignBasaeFragment implements View.OnClickListener {
    private View mView;
    /**
     * 上下文
     */
    private Context mContext;

    private CustomFragmentManagerInterFace cfmi;

    private LinearLayout sCodeLinearLayout;
    private EditText usernameEdt, pwdEdt, sCodeEdt;
    private TextView getSCodeTv;
    private Button resetBtn;

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
        }

        @Override
        public void onFinish() {
            getSCodeTv.setText("获取验证码");
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
        mView = inflater.inflate(R.layout.fragment_resetpwd, viewGroup, false);
        initView(mView);
        return mView;
    }

    private void initView(View mView) {
        usernameEdt = (EditText) mView.findViewById(R.id.edt_reset_username);
        usernameEdt.setText(initUsername);
        pwdEdt = (EditText) mView.findViewById(R.id.edt_reset_pwd);
        sCodeLinearLayout = (LinearLayout) mView.findViewById(R.id.ll_reset_securityCode);
        sCodeEdt = (EditText) mView.findViewById(R.id.edt_reset_securityCode);
        getSCodeTv = (TextView) mView.findViewById(R.id.tv_reset_getSecurityCode);
        getSCodeTv.setOnClickListener(this);
        resetBtn = (Button) mView.findViewById(R.id.btn_reset);
        resetBtn.setOnClickListener(this);
    }

    public static ResetPwdFragment newInstance(String username) {
        /*
        *获取前一个Fragment的username并传递到Argment，返回实例
        * */
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_USERNAME, username);

        ResetPwdFragment fragment = new ResetPwdFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onClick(View v) {
        String username = usernameEdt.getText().toString();
        int id = v.getId();
        switch (id) {
            case R.id.tv_reset_getSecurityCode:
                requestSCode(username);
                break;
            case R.id.btn_reset:
                String pwd = pwdEdt.getText().toString();
                String sCode = sCodeEdt.getText().toString();
                reset(username, pwd, sCode);
                break;
        }
    }

    public void reset(final String username, String pwd, String sCode) {
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getActivity(), "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isNumeric(username)) {
            if (!isMobileNO(username)) {
                Toast.makeText(getActivity(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                return;
            } else {
                pwdEdt.setVisibility(View.VISIBLE);
                sCodeLinearLayout.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "请输入新密码和验证码", Toast.LENGTH_SHORT).show();
                return;
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
            if (isMobileNO(username)) {
                Toast.makeText(getActivity(), "密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pwd.length() < 6 || pwd.length() > 12) {
                Toast.makeText(getActivity(), "密码不能少于6位或多于12位", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        UserInfoBean uf = new UserInfoBean();
        uf.setUsername(username);
        if (isMobileNO(username)) {
            uf.resetPasswordBySMSCode(sCode, pwd, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Toast.makeText(getActivity(), "重置密码成功", Toast.LENGTH_SHORT).show();
                        //跳转到登录Fragment
                        cfmi.setDefaultFragment();
                    } else {
                        Toast.makeText(getActivity(), "重置密码失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            uf.resetPasswordByEmail(username, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Toast.makeText(getActivity(), "重置密码请求成功，请到" + username + "邮箱进行密码重置操作", Toast.LENGTH_SHORT).show();
                        //跳转到登录Fragment
                        cfmi.setDefaultFragment();
                    } else {
                        Toast.makeText(getActivity(), "重置密码失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }

    public void requestSCode(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(getActivity(), "请先输入手机号", Toast.LENGTH_SHORT).show();
        } else {
            /*
            *
            *
            * */
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
