package com.hins.smartband.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hins.smartband.R;
import com.hins.smartband.bean.LeftRightListBean;

import java.util.ArrayList;

/**
 * 程序员： Hins on 2016/7/21.
 * 描述：
 */
public class PersonInfoAdapter extends LeftRightListAdapter {

    public PersonInfoAdapter(Context context, ArrayList<LeftRightListBean> leftRightBean) {
        super(context, leftRightBean);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (position == 0) {

            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_left_right_list_header, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_me_list_title);
            viewHolder.imageSub = (ImageView) convertView.findViewById(R.id.iv_me_list_ic);
            convertView.setTag(viewHolder);


            viewHolder.title.setText(datas.get(position).getLeftStr());
            Bitmap icon = BitmapFactory.decodeFile(mContext.getFilesDir() + "/" + datas.get(position).getRightStr() + "_icon_head.png");
            if (icon != null) {
                viewHolder.imageSub.setImageBitmap(icon);
            }

        } else if (position == 2) {

            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_left_right_list_sex, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_me_list_title);
            viewHolder.imageSub = (ImageView) convertView.findViewById(R.id.iv_me_list_sex);
            convertView.setTag(viewHolder);

            if (!datas.get(position).getRightStr().equals("男")) {
                viewHolder.imageSub.setImageResource(R.drawable.btn_select_woman);
            }


            viewHolder.title.setText(datas.get(position).getLeftStr());

        } else {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_left_right_list, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_me_list_title);
            viewHolder.sub = (TextView) convertView.findViewById(R.id.tv_me_list_sub);
            convertView.setTag(viewHolder);


            viewHolder.title.setText(datas.get(position).getLeftStr());
            viewHolder.sub.setText(datas.get(position).getRightStr());
        }

        return convertView;
    }

    class ViewHolder {
        private TextView title;
        private TextView sub;
        private ImageView imageSub;
    }

}
