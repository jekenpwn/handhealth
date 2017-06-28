package com.hins.smartband.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hins.smartband.R;
import com.hins.smartband.bean.IconLeftMidRightListBean;

import java.util.ArrayList;

/**
 * Created by Hins on 2016/10/13.
 */

public class MeasureAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<IconLeftMidRightListBean> datas;

    public MeasureAdapter(Context context,ArrayList<IconLeftMidRightListBean> datas) {
        this.mContext=context;
        this.datas=datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return datas.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (convertView==null){
            viewHolder=new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_icon_left_mid_right_list, parent, false);
            viewHolder.leftIcon= (ImageView) convertView.findViewById(R.id.iv_left_list_ic);
            viewHolder.midIcon= (ImageView) convertView.findViewById(R.id.iv_mid_list_ic);
            viewHolder.rightIcon= (ImageView) convertView.findViewById(R.id.iv_right_list_ic);
            viewHolder.leftTitle= (TextView) convertView.findViewById(R.id.tv_left_list_title);
            viewHolder.midTitle= (TextView) convertView.findViewById(R.id.tv_mid_list_title);
            viewHolder.rightTitle= (TextView) convertView.findViewById(R.id.tv_right_list_title);
            viewHolder.time= (TextView) convertView.findViewById(R.id.tv_time_list);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.leftIcon.setImageResource(R.drawable.icon_heart);
        viewHolder.midIcon.setImageResource(R.drawable.icon_oxy);
        viewHolder.rightIcon.setImageResource(R.drawable.icon_tem);
        viewHolder.leftTitle.setText(datas.get(position).getLeftStr());
        viewHolder.midTitle.setText(datas.get(position).getMidStr());
        viewHolder.rightTitle.setText(datas.get(position).getRightStr());
        viewHolder.time.setText(datas.get(position).getTime());
        return convertView;
    }

    class ViewHolder {
        private ImageView leftIcon;
        private ImageView midIcon;
        private ImageView rightIcon;
        private TextView leftTitle;
        private TextView midTitle;
        private TextView rightTitle;
        private TextView time;
    }
}
