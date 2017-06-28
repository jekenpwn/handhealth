package com.hins.smartband.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hins.smartband.R;
import com.hins.smartband.adapter.LeftListAdapter;
import com.hins.smartband.base.BaseActivity;
import com.hins.smartband.bean.LeftListBean;
import com.hins.smartband.bean.UserInfoBean;
import com.hins.smartband.database.AimDAO;

import java.io.File;
import java.util.ArrayList;

import cn.bmob.v3.BmobUser;

/**
 * @ClassName: OptionFragment
 * @Description: 设置页面
 */
public class OptionFragment extends Fragment implements  AdapterView.OnItemClickListener {
    private View mView;

    private ListView option_lv,logout_lv;

    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_option, viewGroup, false);
        initView(mView);
        initListView();
        return mView;
    }

    private void initListView() {
        ArrayList<LeftListBean> datas=new ArrayList<LeftListBean>();
        datas.add(new LeftListBean("关于我们"));
        datas.add(new LeftListBean("常见问题"));

        option_lv.setAdapter(new LeftListAdapter(getActivity(),datas,R.layout.item_left_list));
        option_lv.setOnItemClickListener(this);



        ArrayList<LeftListBean> datass=new ArrayList<LeftListBean>();
        datass.add(new LeftListBean("注销登录"));
        datass.add(new LeftListBean("退出"));

        logout_lv.setAdapter(new LeftListAdapter(getActivity(),datass,R.layout.item_left_list_footer));
        logout_lv.setOnItemClickListener(this);

    }

    private void initView(View mView) {
        option_lv= (ListView) mView.findViewById(R.id.lv_option);
        logout_lv= (ListView) mView.findViewById(R.id.lv_logout);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String listview=parent.toString().substring(parent.toString().lastIndexOf("/")+1,parent.toString().length()-1);
        if(listview.equals("lv_option")){
            if(position==0){

            }else {

            }
        }
        else {
            if(position==0){
                deleteUserFileOnLocal();
                deleteUserAimDate();
                BmobUser.logOut();
                BaseActivity.logout(getActivity());
            }else {
                BaseActivity.finishAll();
            }
        }
    }

    private void deleteUserAimDate() {
        AimDAO aimDao=new AimDAO(getActivity());
        aimDao.clearData();
        aimDao.close();
    }

    private void deleteUserFileOnLocal() {
        UserInfoBean user=BmobUser.getCurrentUser(UserInfoBean.class);
        String userFileUrl=getActivity().getFilesDir()+"/"+user.getUsername();
        File userFile=new File(userFileUrl);
        if(userFile.exists()){
            File[] childFiles = userFile.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                userFile.delete();
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                childFiles[i].delete();
            }
            userFile.delete();
        }
    }
}
