package com.hins.smartband.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hins.smartband.R;
import com.hins.smartband.activities.MainActivity;
import com.hins.smartband.base.FinishBaseFragment;
import com.hins.smartband.bean.UserInfoBean;
import com.hins.smartband.database.AimDAO;
import com.hins.smartband.model.Tb_aim_m;
import com.hins.smartband.tools.GetAimStep;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @ClassName: SexFragment
 * @Description: 选择性别页面
 */
public class WeightFragment extends FinishBaseFragment implements View.OnClickListener {
    private View mView;
    /**
     * 调用Activity方法的接口
     */
    CustomFragmentManagerInterFace cfmi;

    private TextView sure_tv;

    private HorizontalScrollView ruler;
    private LinearLayout rulerlayout, all_layout;
    private TextView user_birth_value;

    private int weight = 60;

    private UserInfoBean user ;

    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        cfmi = getCFMI();//获取父类调用Activity的内部接口
        user= cfmi.getUserInfoBean();
        if (!user.getWeight().equals(new Integer("0"))){
            weight= user.getWeight();
        }
        mView = inflater.inflate(R.layout.fragment_weight, viewGroup, false);
        initView(mView);
        return mView;
    }

    private void initView(View mView) {
        sure_tv = (TextView) mView.findViewById(R.id.tv_weight_sure);
        sure_tv.setOnClickListener(this);

        user_birth_value = (TextView) mView.findViewById(R.id.user_birth_value);
        user_birth_value.setText(weight + " kg");
        ruler = (HorizontalScrollView) mView.findViewById(R.id.birthruler);
        rulerlayout = (LinearLayout) mView.findViewById(R.id.ruler_layout);
        ruler.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                user_birth_value.setText(String.valueOf((int) Math.ceil((ruler.getScrollX()) / 20)) + " kg");
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                user_birth_value.setText(String.valueOf((int) Math.ceil((ruler.getScrollX()) / 20)) + " kg");
                                weight = (int) (Math
                                        .ceil((ruler.getScrollX()) / 20));

                            }
                        }, 750);
                        break;
                }
                return false;
            }

        });

        new Handler().postDelayed((new Runnable() {
            @Override
            public void run() {
                constructRuler();
            }
        }), 300);
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scroll();
            }
        }, 400);
    }

    private void scroll() {
        ruler.smoothScrollTo(weight*20, 0);
    }

    @SuppressWarnings("deprecation")
    private void constructRuler() {
        int screenWidth = ruler.getWidth();
        View leftview = (View) LayoutInflater.from(getActivity()).inflate(
                R.layout.blankhrulerunit, null);
        leftview.setLayoutParams(new ViewGroup.LayoutParams(screenWidth / 2,
                ViewGroup.LayoutParams.MATCH_PARENT));
        rulerlayout.addView(leftview);
        for (int i = 0; i < 20; i++) {
            View view = (View) LayoutInflater.from(getActivity()).inflate(
                    R.layout.hrulerunit, null);
            view.setLayoutParams(new ViewGroup.LayoutParams(200,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            TextView tv = (TextView) view.findViewById(R.id.hrulerunit);
            tv.setText(String.valueOf(i * 10));
            rulerlayout.addView(view);
        }
        View rightview = (View) LayoutInflater.from(getActivity()).inflate(
                R.layout.blankhrulerunit, null);
        rightview.setLayoutParams(new ViewGroup.LayoutParams(screenWidth / 2,
                ViewGroup.LayoutParams.MATCH_PARENT));
        rulerlayout.addView(rightview);
    }

    @Override
    public void onClick(View v) {

        user.setWeight(weight);
        user.setBMI();
        //
        cfmi.initFragment();

    }

}
