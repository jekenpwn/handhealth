package com.hins.smartband.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hins.smartband.R;
import com.hins.smartband.bean.LeftRightListBean;

import java.util.ArrayList;

/**
 * 程序员： Hins on 2016/7/21.
 * 描述：
 */
public class LeftRightListAdapter extends BaseAdapter {

    protected Context mContext;
    protected ArrayList<LeftRightListBean> datas;

    public LeftRightListAdapter(Context context, ArrayList<LeftRightListBean> leftRightBean) {
        this.mContext = context;
        this.datas = leftRightBean;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public LeftRightListBean getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_left_right_list, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_me_list_title);
            viewHolder.sub = (TextView) convertView.findViewById(R.id.tv_me_list_sub);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText(datas.get(position).getLeftStr());
        viewHolder.sub.setText(datas.get(position).getRightStr());


        return convertView;
    }

    class ViewHolder {
        private TextView title;
        private TextView sub;
    }

}
