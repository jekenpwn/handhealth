package com.hins.smartband.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.WorkSource;
import android.widget.Toast;

import com.hins.smartband.R;
import com.hins.smartband.base.BaseActivity;
import com.hins.smartband.base.FinishBaseFragment;
import com.hins.smartband.bean.UserInfoBean;
import com.hins.smartband.database.AimDAO;
import com.hins.smartband.fragment.AgeFragment;
import com.hins.smartband.fragment.HeightFragment;
import com.hins.smartband.fragment.SexFragment;
import com.hins.smartband.fragment.WeightFragment;
import com.hins.smartband.model.Tb_aim_m;
import com.hins.smartband.tools.GetAimStep;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 程序员： Hins on 2016/7/18.
 * 描述：
 */
public class FinishUserInfoActivity extends BaseActivity implements FinishBaseFragment.CustomFragmentManagerInterFace {

    private UserInfoBean user;

    private FragmentManager fm;
    private SexFragment mSex;
    private AgeFragment mAge;
    private HeightFragment mHeight;
    private WeightFragment mWeight;

    private ProgressDialog progressDialog;

    boolean have_init=false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finishuserinfo);

        user= BmobUser.getCurrentUser(UserInfoBean.class);

        fm = getFragmentManager();

        if (getIntent().getStringExtra("fragment")!=null){
            have_init=true;
            switch (getIntent().getStringExtra("fragment")){
                case "sex":
                    setSexFragment();
                    break;
                case "age":
                    setAgeFragment();
                    break;
                case "height":
                    setHeightFragment();
                    break;
                case "weight":
                    setWeightFragment();
                    break;
            }
        }else {
            initFragment();
        }

    }

    public void initFragment() {
        switch (user.whichUserInfoUnFinish()){
            case 1:
                setSexFragment();
                break;
            case 2:
                setAgeFragment();
                break;
            case 3:
                setHeightFragment();
                break;
            case 4:
                setWeightFragment();
                break;
            case 0:
                finishUserInfo(user);
                break;
        }
    }

    @Override
    public UserInfoBean getUserInfoBean() {
        return this.user;
    }

    /*
    *功能：设置默认的第一个Fragment—SexFragmnent
    *参数：
    *返回类型：
    */
    public void setSexFragment() {
        FragmentTransaction ft = fm.beginTransaction();
        mSex = new SexFragment();
        ft.replace(R.id.finishuserinfo_content, mSex);
        ft.commit();
    }

    public void setAgeFragment() {
        FragmentTransaction ft = fm.beginTransaction();
        mAge = new AgeFragment();
        ft.replace(R.id.finishuserinfo_content, mAge);
        if (!have_init){
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    public void setHeightFragment() {
        FragmentTransaction ft = fm.beginTransaction();
        mHeight = new HeightFragment();
        ft.replace(R.id.finishuserinfo_content, mHeight);
        if (!have_init){
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    public void setWeightFragment() {
        FragmentTransaction ft = fm.beginTransaction();
        mWeight = new WeightFragment();
        ft.replace(R.id.finishuserinfo_content, mWeight);
        if (!have_init){
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    private void finishUserInfo(final UserInfoBean user) {
        progressDialog=new ProgressDialog(FinishUserInfoActivity.this);
        progressDialog.setMessage("正在保存...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                progressDialog.dismiss();
                if (e == null) {
                    updateUserAim();
                    Toast.makeText(FinishUserInfoActivity.this, "更新用户信息到云成功", Toast.LENGTH_SHORT).show();
                    FinishUserInfoActivity.this.setResult(0x111);
                    FinishUserInfoActivity.this.finish();
                } else {
                    Toast.makeText(FinishUserInfoActivity.this, "更新用户信息失败：" + e.getMessage() + "请检查网络后重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUserAim() {
        AimDAO aimDao=new AimDAO(this);
        Tb_aim_m tb_aim_m=aimDao.find(user.getObjectId());
        String stepStr=new GetAimStep(user.getSex(),user.getBirth(),user.getHeight(),user.getWeight()).getStep();
        String kmileStr=new GetAimStep(user.getSex(),user.getBirth(),user.getHeight(),user.getWeight()).getKmile();
        tb_aim_m.setStep(stepStr);
        tb_aim_m.setKmile(kmileStr);
        aimDao.update(tb_aim_m);
        updateAimToBmob(tb_aim_m);
        aimDao.close();
    }

    public void updateAimToBmob(final Tb_aim_m tb_aim_m) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                tb_aim_m.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {

                    }
                });
            }
        }).start();
    }
}
