package jeken.com.handhealth.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import jeken.com.handhealth.R;
import jeken.com.handhealth.service.CoreService;
import jeken.com.handhealth.uiview.FootCountView;

/**
 * Created by Administrator on 2017-06-27.
 */

public class FootCountFragment extends BaseFragment {
    private FootCountView footCountView;
    private BroadcastReceiver broadcastReceiver;
    @Override
    public Object getContentView() {
        return R.layout.fragment_footcount;
    }

    @Override
    public void init(View view) {
        footCountView = (FootCountView) view.findViewById(R.id.fg_footcount);
        if (footCountView!=null)
           footCountView.setVelocity(100);
    }

    //广播接收数据
    private void broadcastReceicer(){
        if (broadcastReceiver==null)
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle bundle = intent.getBundleExtra(CoreService.EXTRA_DATA);
                    if (bundle!=null){

                    }
                }
            };
        IntentFilter action = new IntentFilter();
        action.addAction(CoreService.EXTRA_DATA);
        action.addAction(CoreService.CONN_SUCCESS);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,action);
    }
}
