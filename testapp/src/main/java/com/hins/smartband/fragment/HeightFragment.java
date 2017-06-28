package com.hins.smartband.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hins.smartband.R;
import com.hins.smartband.base.FinishBaseFragment;
import com.hins.smartband.bean.UserInfoBean;
import com.hins.smartband.database.AimDAO;
import com.hins.smartband.model.Tb_aim_m;
import com.hins.smartband.tools.GetAimStep;

/**
 * @ClassName: HeightFragment
 * @Description: 选择身高段页面
 */
public class HeightFragment extends FinishBaseFragment implements View.OnClickListener {

    private View mView;
    /**
     * 调用Activity方法的接口
     */
    CustomFragmentManagerInterFace cfmi;

    private ImageView head_iv, body_iv;

    private TextView sure_tv;

    private ScrollView ruler;

    private TextView user_height_value;

    private LinearLayout rulerlayout;

    private int height = 170;

    private UserInfoBean user;

    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        cfmi = getCFMI();//获取父类调用Activity的内部接口
        user = cfmi.getUserInfoBean();
        if (!user.getHeight().equals(new Integer("0"))){
            height=user.getHeight();
        }
        mView = inflater.inflate(R.layout.fragment_height, viewGroup, false);
        initView(mView);
        return mView;
    }

    private void initView(View mView) {

        head_iv = (ImageView) mView.findViewById(R.id.userheighthead);
        body_iv = (ImageView) mView.findViewById(R.id.userheightbody);
        if (user.getSex().equals("男")){
            head_iv.setImageResource(R.drawable.userinfo_head_2);
            body_iv.setImageResource(R.drawable.userinfo_body_2);
        }

        sure_tv = (TextView) mView.findViewById(R.id.tv_height_sure);
        sure_tv.setOnClickListener(this);

        user_height_value = (TextView) mView.findViewById(R.id.user_height_value);
        user_height_value.setText(height + " CM");

        ruler = (ScrollView) mView.findViewById(R.id.vruler);
        ruler.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                user_height_value.setText((int) Math.ceil((2500 - ruler.getScrollY()) / 10) + " CM");
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            height=(int) Math.ceil((2500 - ruler.getScrollY()) / 10);
                            user_height_value.setText(height + " CM");
                        }
                    }, 750);

                }
                return false;
            }

        });

        rulerlayout = (LinearLayout) mView.findViewById(R.id.vruler_layout);
        new Handler().postDelayed((new Runnable() {
            @Override
            public void run() {
                constructRuler();
            }
        }), 300);

    }

    @Override
    public void onClick(View v) {
        user.setHeight(height);
        user.setBMI();
        cfmi.initFragment();
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
        ruler.smoothScrollTo(0, 2500 - height * 10);
    }

    private void constructRuler() {
        int rulerHeight = ruler.getHeight();
        View topview = (View) LayoutInflater.from(getActivity()).inflate(
                R.layout.item_rulervertcial_header, null);
        topview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                rulerHeight / 2));
        rulerlayout.addView(topview);
        for (int i = 25; i > 0; i--) {
            View view = (View) LayoutInflater.from(getActivity()).inflate(
                    R.layout.item_ruler_vertical, null);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    100));
            TextView tv = (TextView) view.findViewById(R.id.vrulerunit);
            tv.setText(String.valueOf(i * 10));
            rulerlayout.addView(view);
        }
        View bottomview = (View) LayoutInflater.from(getActivity()).inflate(
                R.layout.item_rulervertcial_header, null);
        bottomview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                rulerHeight / 2));
        rulerlayout.addView(bottomview);
    }
}
