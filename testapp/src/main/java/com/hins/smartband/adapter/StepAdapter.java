package com.hins.smartband.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hins.smartband.R;
import com.hins.smartband.bean.IconLeftRightListBean;

import java.util.ArrayList;

/**
 * 程序员： Hins on 2016/7/21.
 * 描述：
 */
public class StepAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<IconLeftRightListBean> datas;

    public StepAdapter(Context context, ArrayList<IconLeftRightListBean> iconleftRightBean) {
        this.mContext = context;
        this.datas = iconleftRightBean;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public IconLeftRightListBean getItem(int position) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_step_left_right_list, parent, false);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.iv_me_list_ic);
            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_me_list_title);
            viewHolder.sub = (TextView) convertView.findViewById(R.id.tv_me_list_sub);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.icon.setImageResource(datas.get(position).getImg());
        viewHolder.title.setText(datas.get(position).getLeftStr());
        viewHolder.sub.setText(datas.get(position).getRightStr());
        if (datas.get(position).getRightStr().equals("心率异常")||
                datas.get(position).getRightStr().equals("摔倒")){
            viewHolder.sub.setTextColor(mContext.getResources().getColor(R.color.color_space_studytools_red));
        }else {
            viewHolder.sub.setTextColor(mContext.getResources().getColor(R.color.silver));
        }
        return convertView;
    }

    class ViewHolder {
        private ImageView icon;
        private TextView title;
        private TextView sub;
    }
}
