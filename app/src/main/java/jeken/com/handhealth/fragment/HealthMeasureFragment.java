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
import jeken.com.handhealth.uiview.BloodView;
import jeken.com.handhealth.uiview.HeartRateView;

/**
 * Created by Administrator on 2017-06-27.
 */

public class HealthMeasureFragment extends BaseFragment {
    private HeartRateView heartRateView;
    private BloodView bloodView_h,bloodView_l;

    private BroadcastReceiver broadcastReceiver;
    @Override
    public Object getContentView() {
        return R.layout.fragment_healthmeasure;
    }

    @Override
    public void init(View view) {
        heartRateView = (HeartRateView) view.findViewById(R.id.fg_health_heartRate);
        bloodView_h = (BloodView) view.findViewById(R.id.fg_health_blood_h);
        bloodView_h.setBloodFlag(false);
        bloodView_l = (BloodView) view.findViewById(R.id.fg_health_blood_l);
        bloodView_l.setBloodFlag(true);

        heartRateView.setCreditValue(70);
        bloodView_h.setCreditValue(100);
        bloodView_l.setCreditValue(70);

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
