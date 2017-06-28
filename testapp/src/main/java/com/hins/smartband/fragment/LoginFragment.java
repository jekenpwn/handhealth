package com.hins.smartband.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hins.smartband.R;
import com.hins.smartband.activities.FinishUserInfoActivity;
import com.hins.smartband.activities.MainActivity;
import com.hins.smartband.base.LoginOrSignBasaeFragment;
import com.hins.smartband.bean.UserInfoBean;
import com.hins.smartband.database.AimDAO;
import com.hins.smartband.model.Tb_aim_m;
import com.hins.smartband.tools.GetAimStep;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.LogInListener;

/**
 * @ClassName: LoginFragment
 * @Description: 登录页面
 */
public class LoginFragment extends LoginOrSignBasaeFragment implements View.OnClickListener {
    private View mView;
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 调用Activity方法的接口
     */
    CustomFragmentManagerInterFace cfmi;

    private EditText usernameEdt, pwdEdt;
    private TextView forgetTv, signupTv;
    private Button loginBtn;
    private ProgressDialog loginDialog;

    private String initUsername;
    private static final String BUNDLE_USERNAME = "username";

    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            initUsername = bundle.getString(BUNDLE_USERNAME);
        }
        if (initUsername == null) {
            initUsername = "";
        }
        cfmi = getCFMI();//获取父类调用Activity的内部接口
        mView = inflater.inflate(R.layout.fragment_login, viewGroup, false);
        initView(mView);

        return mView;
    }

    private void initView(View mView) {
        usernameEdt = (EditText) mView.findViewById(R.id.edt_login_username);
        usernameEdt.setText(initUsername);
        pwdEdt = (EditText) mView.findViewById(R.id.edt_login_pwd);
        forgetTv = (TextView) mView.findViewById(R.id.tv_login_forgetPwd);
        forgetTv.setOnClickListener(this);
        signupTv = (TextView) mView.findViewById(R.id.tv_login_signup);
        signupTv.setOnClickListener(this);
        loginBtn = (Button) mView.findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(this);
    }

    public static LoginFragment newInstance(String username) {
        /*
        *获取前一个Fragment的username并传递到Argment，返回实例
        * */
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_USERNAME, username);

        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onClick(View v) {
        String username = usernameEdt.getText().toString();
        int id = v.getId();
        switch (id) {
            case R.id.tv_login_forgetPwd:
                getCFMI().setResetPwdFragment(username);
                break;
            case R.id.tv_login_signup:
                cfmi.setSignupFragment(username);
                break;
            case R.id.btn_login:
                String pwd = pwdEdt.getText().toString();
                login(username, pwd);
                break;
        }

    }

    public void login(String username, String pwd) {
        loginDialog=new ProgressDialog(getActivity());
        loginDialog.setMessage("正在登陆...");
        loginDialog.setCancelable(false);
        loginDialog.show();

        if (TextUtils.isEmpty(username)) {
            loginDialog.dismiss();
            Toast.makeText(getActivity(), "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isNumeric(username)) {
            if (!isMobileNO(username)) {
                loginDialog.dismiss();
                Toast.makeText(getActivity(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            if (!isEmail(username)) {
                loginDialog.dismiss();
                Toast.makeText(getActivity(), "请输入正确的电子邮箱", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (TextUtils.isEmpty(pwd)) {
            loginDialog.dismiss();
            Toast.makeText(getActivity(), "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobUser.loginByAccount(username, pwd, new LogInListener<UserInfoBean>() {
            @Override
            public void done(UserInfoBean user, BmobException e) {
                if (user != null) {
                    loginDialog.dismiss();
                    Toast.makeText(getActivity(), "登录成功", Toast.LENGTH_SHORT).show();
                    Log.d("Hins","登录成功");

                    setUpUserFileOnLocal(user.getUsername());

                    if (!user.getIconUrl().equals("")||user.getIconUrl().length()<1){
                        getUserHeadFromBmob(user.getUsername(),user.getObjectId(),user.getIconUrl());
                    }

                    // 允许用户使用应用
                    if(user.whichUserInfoUnFinish()==0){
                        addUserAimDate(user);
                        Intent intent=new Intent(getActivity(),MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }else {
                        Intent intent=new Intent(getActivity(),FinishUserInfoActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                } else {
                    loginDialog.dismiss();
                    Toast.makeText(getActivity(), "登录失败：code=" + e.getErrorCode() + "，错误描述：" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("Hins","登录失败：code=" + e.getErrorCode() + "，错误描述：" + e.getLocalizedMessage());
                }
            }
        });

    }

    private void getUserHeadFromBmob(final String username, final String objectId, final String iconUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String localUrl=getActivity().getFilesDir()+"/"+username+"/"+objectId+"_icon_head.png";
                File destDir=new File(localUrl);
                BmobFile bmobfile=new BmobFile(objectId+"_icon_head.png","",iconUrl);
                bmobfile.download(destDir, new DownloadFileListener() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e!=null) {
                            Toast.makeText(getActivity(), "下载头像失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onProgress(Integer integer, long l) {

                    }
                });
            }
        }).start();
    }

    private void addUserAimDate(UserInfoBean user) {
        AimDAO aimDao=new AimDAO(getActivity());
        Tb_aim_m tb_aim_m=aimDao.find(user.getObjectId(),user);
        aimDao.add(tb_aim_m);
    }

    private void setUpUserFileOnLocal(String username) {
        String localUrl=getActivity().getFilesDir()+"/"+username;
        File destDir=new File(localUrl);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

    }

}
