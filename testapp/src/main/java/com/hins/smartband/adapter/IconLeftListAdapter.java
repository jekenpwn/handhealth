package com.hins.smartband.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hins.smartband.R;
import com.hins.smartband.bean.IconLeftListBean;

import java.util.ArrayList;

/**
 * 程序员： Hins on 2016/7/21.
 * 描述：
 */
public class IconLeftListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<IconLeftListBean> datas;

    public IconLeftListAdapter(Context context, ArrayList<IconLeftListBean> iconleftBean) {
        this.mContext = context;
        this.datas = iconleftBean;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public IconLeftListBean getItem(int position) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_icon_left_list, parent, false);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.iv_me_list_ic);
            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_me_list_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.icon.setImageResource(datas.get(position).getImg());
        viewHolder.title.setText(datas.get(position).getLeftStr());

        return convertView;
    }

    class ViewHolder {
        private ImageView icon;
        private TextView title;
    }
}
