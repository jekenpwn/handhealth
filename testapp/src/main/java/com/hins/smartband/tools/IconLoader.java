package com.hins.smartband.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 程序员： Hins on 2016/8/2.
 * 描述：
 */
public class IconLoader {

    private ImageView iv;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            iv.setImageBitmap((Bitmap) msg.obj);
        }
    };

    public void showIconByThread(ImageView iv, final String url){
        this.iv=iv;

        new Thread(){
            @Override
            public void run() {
                super.run();
                Bitmap bitmap=getBitmapFromUrl(url);
                Message msg=mHandler.obtainMessage();
                msg.obj=bitmap;
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    public Bitmap getBitmapFromUrl(String urlString){
        Bitmap bitmap=null;
        InputStream is=null;
        try {
            URL url=new URL(urlString);
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            is=new BufferedInputStream(connection.getInputStream());
            bitmap= BitmapFactory.decodeStream(is);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
