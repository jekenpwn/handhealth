package com.hins.smartband.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hins.smartband.R;
import com.hins.smartband.bean.LeftListBean;

import java.util.ArrayList;

/**
 * 程序员： Hins on 2016/7/21.
 * 描述：
 */
public class LeftListAdapter extends BaseAdapter {

    private int layout;

    private Context mContext;
    private ArrayList<LeftListBean> datas;

    public LeftListAdapter(Context mContext, ArrayList<LeftListBean> datas, int layout) {
        this.layout = layout;
        this.mContext = mContext;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public LeftListBean getItem(int position) {
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
            viewHolder=new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(layout, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_me_list_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText(datas.get(position).getLeftStr());

        return convertView;
    }

    class ViewHolder {
        private TextView title;
    }
}
