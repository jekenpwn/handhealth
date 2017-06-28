package jeken.com.handhealth.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;

import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import jeken.com.handhealth.R;
import jeken.com.handhealth.fragment.FootCountFragment;
import jeken.com.handhealth.fragment.HealthMeasureFragment;
import jeken.com.handhealth.uiview.Main_TabBottom;

/**
 * Created by Administrator on 2017-06-27.
 */

public class HomeActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    @ViewInject(R.id.main_viewPager)
    private ViewPager main_viewPager;
    @ViewInject(R.id.tb_main_tab_measure)
    private Main_TabBottom measure_tb;
    @ViewInject(R.id.tb_main_tab_step)
    private Main_TabBottom step_tb;
    @ViewInject(R.id.tb_main_tab_me)
    private Main_TabBottom me_tb;

    private List<Main_TabBottom> mainTabBottom = new ArrayList<Main_TabBottom>();//保存底部按钮
    private List<Fragment> mainTab = new ArrayList<Fragment>();//保存Fragment
    private FragmentPagerAdapter pagerAdapter;
    private HealthMeasureFragment healthMeasureFragment;
    private FootCountFragment footCountFragment;
    @Override
    public Object getContentView() {
        return R.layout.activity_home;
    }
    @Override
    public void init() {

//        if (SysTools.isServiceWorking("jeken.com.handhealth.service.CoreService",this)){
//            startService(new Intent(this, CoreService.class));
//        }

        measure_tb.setOnClickListener(this);
        step_tb.setOnClickListener(this);
        me_tb.setOnClickListener(this);
        mainTabBottom.add(measure_tb);
        mainTabBottom.add(step_tb);
        mainTabBottom.add(me_tb);
        measure_tb.setIconAlpha(1.0f);//设置第一个Icon为有色

        initFragment();
    }

    private void initFragment(){
        healthMeasureFragment = new HealthMeasureFragment();
        mainTab.add(healthMeasureFragment);
        footCountFragment = new FootCountFragment();
        mainTab.add(footCountFragment);
        //创建fragment适配器.
        pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mainTab.get(position);
            }

            @Override
            public int getCount() {
                return mainTab.size();
            }
        };

        //使用适配器填充
        main_viewPager.setAdapter(pagerAdapter);
        main_viewPager.setOnPageChangeListener(HomeActivity.this);
        main_viewPager.setOffscreenPageLimit(2);

    }

    @Override
    public String describeActivity() {
        return "HomeActivity";
    }

    @Override
    public void onClick(View v) {
        resetOtherTabBottomAlpha();
        switch (v.getId()) {
            case R.id.tb_main_tab_measure:
                measure_tb.setIconAlpha(1.0f);
                main_viewPager.setCurrentItem(0, false);
                setStatuBarColor(R.color.colorRedPrimaryDark);
                break;
            case R.id.tb_main_tab_step:
                step_tb.setIconAlpha(1.0f);
                main_viewPager.setCurrentItem(1, false);
                setStatuBarColor(R.color.colorGreenPrimaryDark);
                break;
            case R.id.tb_main_tab_me:
                me_tb.setIconAlpha(1.0f);
                main_viewPager.setCurrentItem(0, false);
                setStatuBarColor(R.color.colorPrimaryDark);
                break;
        }
    }

    //重置其他TabBottom的透明度
    public void resetOtherTabBottomAlpha() {
        for (int i = 0; i < mainTabBottom.size(); i++) {
            mainTabBottom.get(i).setIconAlpha(0.0f);
        }
    }
    //change the color of sate bar
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatuBarColor(int color){
        int sysVersion = Integer.parseInt(Build.VERSION.SDK);
        if (sysVersion<Build.VERSION_CODES.LOLLIPOP){
            return;
        }
        Window window=getWindow();
        window.setStatusBarColor(getResources().getColor(color));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        if (positionOffset > 0) {
            Main_TabBottom left = mainTabBottom.get(position);
            Main_TabBottom right = mainTabBottom.get(position + 1);
            left.setIconAlpha(1 - positionOffset);
            right.setIconAlpha(positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                setStatuBarColor(R.color.colorRedPrimaryDark);
                break;
            case 1:
                setStatuBarColor(R.color.colorGreenPrimaryDark);
                break;
            case 2:
                setStatuBarColor(R.color.colorPrimaryDark);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
