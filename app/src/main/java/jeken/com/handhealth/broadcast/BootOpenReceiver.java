package jeken.com.handhealth.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import jeken.com.handhealth.service.CoreService;
import jeken.com.handhealth.tools.SysTools;

public class BootOpenReceiver extends BroadcastReceiver {

    //Start the service when boot has been opened
    @Override
    public void onReceive(Context context, Intent intent) {
        if (SysTools.isServiceWorking("jeken.com.handhealth.service.CoreService",context))
        context.startService(new Intent(context, CoreService.class));
    }
}
