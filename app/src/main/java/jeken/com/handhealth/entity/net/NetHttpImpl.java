package jeken.com.handhealth.entity.net;

import android.util.Log;
import java.net.HttpURLConnection;
import java.net.URL;
import jeken.com.handhealth.entity.health.Tb_CountFoot;
import jeken.com.handhealth.entity.health.Tb_Health;
/**
 * Created by Administrator on 2017-06-28.
 */

public class NetHttpImpl implements NetHttp {
    private String TAG = NetHttpImpl.class.getSimpleName();

    private final static String URL = "http://jekenpwn.cn:8080/smartband/smartbandInterface.php?";
    @Override
    public void httpGetUploadHealth(Tb_Health health) {
        if (health==null) return;
        int blood_h = health.getBlood_h();
        int blood_l = health.getBlood_l();
        int heart = health.getHeart();
        String url = URL+"type=0";
        if (blood_h>=0) url+=("&blood_h="+blood_h);
        if (blood_l>=0) url+=("&blood_l="+blood_l);
        if (heart>=0) url+=("&heart="+heart);
        final String finalUrl = url;//出于安全考虑
        NetPool.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                httpGet(finalUrl);
            }
        });
    }

    @Override
    public void httpGetUploadCountfoot(Tb_CountFoot countFoot) {
        if (countFoot==null) return;
        long num = countFoot.getFootnum();
        String url = URL+"type=1";
        if (num>0) url += ("&footnum="+num);
        final String finalUrl = url;//出于安全考虑
        NetPool.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                httpGet(finalUrl);
            }
        });
    }

    @Override
    public Tb_Health httpGetDownHealth() {
        return null;
    }

    @Override
    public Tb_CountFoot httpGetDownCountfoot() {
        return null;
    }

    private void httpGet(String url){
        HttpURLConnection conn = null;
        try {
            //打开URL并创建HTTP连接对象
            URL myurl = new URL(url);
            //设置参数
            conn = (HttpURLConnection) myurl.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(1000);
            conn.setConnectTimeout(3000);
            //连接
            conn.connect();
            //响应码200才正常
            if (conn.getResponseCode()!=200){
                Log.e(TAG,"httpget Upload fail,code=="+conn.getResponseCode());
            }
        }catch (Exception e){
            Log.e(TAG,"httpget Upload error!!");
        }finally {
            if (conn != null)
                conn.disconnect();
        }
    }
}
