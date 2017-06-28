package com.hins.smartband.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.hins.smartband.R;
import com.hins.smartband.activities.FinishUserInfoActivity;
import com.hins.smartband.adapter.PersonInfoAdapter;
import com.hins.smartband.bean.LeftRightListBean;
import com.hins.smartband.bean.UserInfoBean;
import com.hins.smartband.tools.FinalCode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * @ClassName: PersonInfoFragment
 * @Description: 个人信息页面
 */
public class PersonInfoFragment extends Fragment implements AdapterView.OnItemClickListener {

    private UserInfoBean user = BmobUser.getCurrentUser(UserInfoBean.class);

    private View mView;

    private ListView person_lv;

    private ArrayList<LeftRightListBean> datas;
    private PersonInfoAdapter personInfoAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_personinfo, viewGroup, false);
        initView(mView);
        initListView();

        return mView;
    }

    private void initListView() {
        datas = new ArrayList<LeftRightListBean>();
        datas.add(new LeftRightListBean("头像", user.getUsername() + "/" + user.getObjectId()));
        datas.add(new LeftRightListBean("昵称", user.getNickname()));
        datas.add(new LeftRightListBean("性别", user.getSex()));
        datas.add(new LeftRightListBean("身高", user.getHeight() + " cm"));
        datas.add(new LeftRightListBean("体重", user.getWeight() + " kg"));
        datas.add(new LeftRightListBean("生日", user.getBirth()));
        datas.add(new LeftRightListBean("手机", user.getMobilePhoneNumber()));
        datas.add(new LeftRightListBean("邮箱", user.getEmail()));

        personInfoAdapter = new PersonInfoAdapter(getActivity(), datas);
        person_lv.setAdapter(personInfoAdapter);
        person_lv.setOnItemClickListener(this);

    }

    private void initView(View mView) {
        person_lv = (ListView) mView.findViewById(R.id.lv_person);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                // 激活系统图库，选择一张图片
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
                startActivityForResult(intent, FinalCode.PHOTO_REQUEST_GALLERY);
                break;
            case 1:
                final String buffer=user.getNickname();
                final EditText et = new EditText(getActivity());
                et.setText(buffer);
                et.setTextSize(18);
                et.setSingleLine(true);
                LinearLayout layout = new LinearLayout(getActivity());
                LinearLayout.LayoutParams pm = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layout.setPadding(84,24,84,0);
                layout.addView(et, pm);
                layout.setGravity(Gravity.CENTER);
                new AlertDialog.Builder(getActivity()).setTitle("昵称")
                        .setView(layout)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int which) {
                                String input = et.getText().toString();
                                if (input.equals("") || input.length() < 1) {
                                    Toast.makeText(getActivity(), "内容不能为空！", Toast.LENGTH_LONG).show();
                                } else {
                                    user.setNickname(input);
                                    user.update(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e != null) {
                                                user.setNickname(buffer);
                                            }else {
                                                datas.clear();
                                                datas.add(new LeftRightListBean("头像", user.getUsername() + "/" + user.getObjectId()));
                                                datas.add(new LeftRightListBean("昵称", user.getNickname()));
                                                datas.add(new LeftRightListBean("性别", user.getSex()));
                                                datas.add(new LeftRightListBean("身高", user.getHeight() + " cm"));
                                                datas.add(new LeftRightListBean("体重", user.getWeight() + " kg"));
                                                datas.add(new LeftRightListBean("生日", user.getBirth()));
                                                personInfoAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case 2:
                final ImageView iv = (ImageView) view.findViewById(R.id.iv_me_list_sex);
                if (user.getSex().equals("男")) {
                    user.setSex("女");
                    user.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                iv.setImageResource(R.drawable.btn_select_woman);
                            } else {
                                user.setSex("男");
                            }
                        }
                    });
                } else {
                    user.setSex("男");
                    user.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                iv.setImageResource(R.drawable.btn_select_man);
                            } else {
                                user.setSex("女");
                            }
                        }
                    });
                }
                break;
            case 3:
                Intent intent1 = new Intent(getActivity(), FinishUserInfoActivity.class);
                intent1.putExtra("fragment", "height");
                startActivity(intent1);
                break;
            case 4:
                Intent intent2 = new Intent(getActivity(), FinishUserInfoActivity.class);
                intent2.putExtra("fragment", "weight");
                startActivity(intent2);
                break;
            case 5:
                Intent intent3 = new Intent(getActivity(), FinishUserInfoActivity.class);
                intent3.putExtra("fragment", "age");
                startActivity(intent3);
                break;
        }
        //update();
    }

    @Override
    public void onResume() {
        super.onResume();
        user = BmobUser.getCurrentUser(UserInfoBean.class);
        datas.clear();
        datas.add(new LeftRightListBean("头像", user.getUsername() + "/" + user.getObjectId()));
        datas.add(new LeftRightListBean("昵称", user.getNickname()));
        datas.add(new LeftRightListBean("性别", user.getSex()));
        datas.add(new LeftRightListBean("身高", user.getHeight() + " cm"));
        datas.add(new LeftRightListBean("体重", user.getWeight() + " kg"));
        datas.add(new LeftRightListBean("生日", user.getBirth()));
        personInfoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FinalCode.PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                crop(uri);
            }
        } else if (requestCode == FinalCode.PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据
            if (data != null) {
                Bitmap bitmap = data.getParcelableExtra("data");
                bitmap = createCircleImage(bitmap);
                saveBitmapOnLocal(bitmap);
                saveBitmapOnBmob();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveBitmapOnBmob() {
        final ProgressDialog loginDialog=new ProgressDialog(getActivity());
        loginDialog.setMessage("正在上传...");
        loginDialog.setCancelable(false);
        loginDialog.show();
        final File localPic = new File(new FinalCode(getActivity(), user.getUsername(), user.getObjectId()).getIconFileUrl());
        final BmobFile icon = new BmobFile(localPic);
        BmobFile bmobFile = new BmobFile();
        bmobFile.setUrl(user.getIconUrl());//此url是上传文件成功之后通过bmobFile.getUrl()方法获取的。
        bmobFile.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                icon.uploadblock(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            //bmobFile.getFileUrl()--返回的上传文件的完整地址
                            user.setIconUrl(icon.getFileUrl());
                            user.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        loginDialog.dismiss();
                                        Toast.makeText(getActivity(), "更新头像成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        loginDialog.dismiss();
                                        localPic.delete();
                                        Toast.makeText(getActivity(), "上传头像url失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            loginDialog.dismiss();
                            Toast.makeText(getActivity(), "上传头像失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void saveBitmapOnLocal(Bitmap bitmap) {
        String userName = user.getUsername();
        String userId = user.getObjectId();
        File file = new File(getActivity().getFilesDir() + "/" + userName + "/" + userId + "_icon_head.png");
        if (file.exists()) {
            file.delete();
        } else if (!file.isDirectory() && file.canWrite()) {
            file.delete();
            file.mkdirs();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * 剪切图片
     */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("outputFormat", "PNG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, FinalCode.PHOTO_REQUEST_CUT);
    }

    /**
     * 根据原图和变长绘制圆形图片
     *
     * @param source
     * @return
     */

    private Bitmap createCircleImage(Bitmap source) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        int min = Math.min(source.getHeight(), source.getWidth());
        Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
        /**
         * 产生一个同样大小的画布
         */
        Canvas canvas = new Canvas(target);
        /**
         * 首先绘制圆形
         */
        canvas.drawCircle(min / 2, min / 2, min / 2, paint);
        /**
         * 使用SRC_IN
         */
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        /**
         * 绘制图片
         */
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }
}
