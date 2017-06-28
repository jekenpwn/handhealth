package jeken.com.handhealth.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.xutils.x;

import java.util.LinkedList;

/**
 * Created by Administrator on 2017-06-26.
 */

public abstract class BaseActivity extends AppCompatActivity {
    //放置所有activity
    public static LinkedList<AppCompatActivity> activities = new LinkedList<AppCompatActivity>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        if (getContentView() instanceof Integer){
            setContentView((Integer) getContentView());
        }else if (getContentView() instanceof View){
            setContentView((View) getContentView());
        }else {
            View view = new View(this);
            super.setContentView(view);
        }
        activities.add(this);
        x.view().inject(this);
        init();
    }

    public abstract Object getContentView();
    public abstract void init();
    public abstract String describeActivity();

}
