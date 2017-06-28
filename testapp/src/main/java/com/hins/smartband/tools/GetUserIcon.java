package com.hins.smartband.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.Toast;

import com.hins.smartband.bean.UserInfoBean;

import java.io.File;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.QueryListener;

/**
 * 程序员： Hins on 2016/8/2.
 * 描述：
 */
public class GetUserIcon implements Runnable{

    private Context mContext;

    private UserInfoBean mUser;

    private String localUrl;

    private String bmobUrl;

    private File localFile;

    private Bitmap icon;

    private boolean hasGet=false;

    private ImageView icon_iv;

    private Handler mHandle=new Handler(){

        public void handleMessage(Message msg) {
            if (msg.what==0x11){
                icon_iv.setImageBitmap(icon);
            }
        }
    };

    public GetUserIcon(Context context, UserInfoBean user,ImageView icon_iv) {
        this.mContext = context;
        this.mUser = user;
        this.icon_iv=icon_iv;
    }

    public void get() {
        if (isHasOnDir()) {
           getIconOnDir();
        }
        if (isHasOnBmob()) {
            getIconOnBmob();
        }
        if (icon!=null){
            hasGet=true;
        }
    }

    private void getIconOnBmob() {
        BmobFile bmobfile =new BmobFile("icon_head.png","",bmobUrl);
        bmobfile.download(localFile, new DownloadFileListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    getIconOnDir();
                }else {
                    Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgress(Integer integer, long l) {

            }
        });
    }

    private boolean isHasOnBmob() {
        BmobQuery<UserInfoBean> query = new BmobQuery<UserInfoBean>();
        query.getObject(mUser.getObjectId(), new QueryListener<UserInfoBean>() {
            @Override
            public void done(UserInfoBean userInfoBean, BmobException e) {
                if (e == null) {
                    bmobUrl = userInfoBean.getIconUrl();
                }else {
                    Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (!bmobUrl.equals("")){
            return true;
        }
        return false;
    }

    private void getIconOnDir() {
        icon = BitmapFactory.decodeFile(localUrl);
    }

    public boolean isHasOnDir() {
        localUrl = new FinalCode(mContext, mUser.getUsername(),mUser.getObjectId()).getIconFileUrl();
        localFile = new File(localUrl);
        if (localFile.exists() && localFile.length() != 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void run() {
        while (!hasGet){
            get();
        }
        Message msg=mHandle.obtainMessage();
        msg.what=0x11;
        mHandle.sendMessage(msg);
    }


}
