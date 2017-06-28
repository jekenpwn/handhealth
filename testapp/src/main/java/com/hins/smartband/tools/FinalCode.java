package com.hins.smartband.tools;

import android.content.Context;

import com.hins.smartband.bean.UserInfoBean;

/**
 * 程序员： Hins on 2016/8/1.
 * 描述：
 */
public class FinalCode {

    private Context mContext;
    private String mUsername;
    private String mUserId;

    public FinalCode(Context context,String username,String userId){
        this.mContext = context;
        this.mUsername = username;
        this.mUserId=userId;
    }

    public static final String PERSON_ICON_FILE_NAME = "_icon_head.png";//头像文件名

    public static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择

    public static final int PHOTO_REQUEST_CUT = 3;// 裁剪相片

    public String getIconFileUrl() {
        String url = mContext.getFilesDir() + "/" + mUsername + "/" +mUserId+ PERSON_ICON_FILE_NAME;
        return url;
    }
}
