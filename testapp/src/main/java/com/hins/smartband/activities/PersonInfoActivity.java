package com.hins.smartband.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hins.smartband.R;
import com.hins.smartband.base.BaseActivity;
import com.hins.smartband.bean.UserInfoBean;
import com.hins.smartband.fragment.AimFragment;
import com.hins.smartband.fragment.OptionFragment;
import com.hins.smartband.fragment.PersonInfoFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.bmob.v3.BmobUser;

/**
 * 程序员： Hins on 2016/7/18.
 * 描述：
 */
public class PersonInfoActivity extends BaseActivity implements  View.OnClickListener {

    private FragmentManager fm;
    private PersonInfoFragment mPerson;
    private OptionFragment mOption;
    private AimFragment mAim;

    private TextView actionbar_title_tv;
    private ImageView back_iv;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personinfo);
        initView();
        String target=getIntent().getStringExtra("target");
        switch (target){
            case "personInfoFragment":
                setPersonInfoFragment();
                break;
            case "myaimFragment":
                setMyAimFragment();
                break;
            case "optionFragment":
                setOptionFragment();
                break;
        }
    }

    private void initView() {
        actionbar_title_tv= (TextView) findViewById(R.id.tv_personinfo_actionbar_title);
        back_iv= (ImageView) findViewById(R.id.iv_personinfo_back);
        back_iv.setOnClickListener(this);
    }

    public void setPersonInfoFragment() {
        fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        mPerson=new PersonInfoFragment();
        ft.replace(R.id.fl_personinfo_content, mPerson);
        ft.commit();
        actionbar_title_tv.setText("个人信息");
    }

    public void setMyAimFragment() {
        fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        mAim=new AimFragment();
        ft.replace(R.id.fl_personinfo_content, mAim);
        ft.commit();
        actionbar_title_tv.setText("我的目标");
    }

    public void setOptionFragment() {
        fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        mOption=new OptionFragment();
        ft.replace(R.id.fl_personinfo_content, mOption);
        ft.commit();
        actionbar_title_tv.setText("设置");
    }

    @Override
    public void onClick(View v) {
        finish();
    }

}
