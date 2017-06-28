package jeken.com.handhealth.tools;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2017-06-26.
 */
public class SysTools {
    /**
     *
     * @param className whole classname ps:com.jeken.WenService
     * @param context
     * @return if service is working {return true}
     */
    public static boolean isServiceWorking(String className,Context context){
        //get the system activitymanager ,and juge the state of service by classname
        ActivityManager  activityManager =  (ActivityManager) context.getApplicationContext().
                getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) activityManager
                .getRunningServices(50);// firstly,set the value is 50,and wait to verify right?
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(className)) {
                return true;
            }
        }
        return false;
    }

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static boolean isToday(long timemap){
        Date now = new Date(System.currentTimeMillis());
        Date last = new Date(timemap);
        if (dateFormat.format(now).equals(dateFormat.format(last))){
           return true;
        }
        return false;
    }

}
