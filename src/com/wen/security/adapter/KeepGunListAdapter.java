package com.wen.security.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wen.security.R;
import com.wen.security.beans.OperGunInfo;
import com.wen.security.beans.StoreObjectInfo;

public class KeepGunListAdapter extends BaseAdapter {

    private Context context;
    private List<StoreObjectInfo> objs;

    public KeepGunListAdapter(Context mContext, List<StoreObjectInfo> mObjs) {

        context = mContext;
        objs = mObjs;
        if (objs == null) {
            objs = new ArrayList<StoreObjectInfo>();
        }

    }

    @Override
    public int getCount() {
        return objs.size();
    }

    @Override
    public Object getItem(int position) {

        return objs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(context);
            convertView = layoutInflator.inflate(R.layout.ac_keep_gun_item, null);
            holder = new ViewHolder();
            holder.ac_keep_gun_no = (TextView) convertView
                    .findViewById(R.id.ac_keep_gun_no);
            holder.ac_keep_gun_type = (TextView) convertView
                    .findViewById(R.id.ac_keep_gun_type);
            holder.ac_keep_guncab_no = (TextView) convertView
                    .findViewById(R.id.ac_keep_guncab_no);
            holder.ac_keep_guncab_pos = (TextView) convertView
                    .findViewById(R.id.ac_keep_guncab_pos);
            holder.ac_keep_gun_reason = (TextView) convertView
                    .findViewById(R.id.ac_keep_gun_reason);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();

        holder.ac_keep_gun_no.setText(objs.get(position).Object_ID);
        /**
         *  1、  枪
         *  2、  子弹弹药
         *  3、  弹夹
         */
        holder.ac_keep_gun_type.setText(objs.get(position).Object_Type);

        holder.ac_keep_guncab_no.setText(objs.get(position).Own_Sub_ID);
        holder.ac_keep_guncab_pos.setText(objs.get(position).Sub_Sequence+"");
        holder.ac_keep_gun_reason.setText(objs.get(position).Remark);


        if (objs.get(position).IsOper) {
            holder.ac_keep_gun_no.setTextColor(context.getResources()
                    .getColor(R.color.deep_red_imgbtn));
            holder.ac_keep_gun_type.setTextColor(context.getResources()
                    .getColor(R.color.deep_red_imgbtn));
            holder.ac_keep_guncab_no.setTextColor(context.getResources()
                    .getColor(R.color.deep_red_imgbtn));
            holder.ac_keep_guncab_pos.setTextColor(context.getResources().getColor(
                    R.color.deep_red_imgbtn));
            holder.ac_keep_gun_reason.setTextColor(context.getResources()
                    .getColor(R.color.deep_red_imgbtn));
        } else {
            holder.ac_keep_gun_no.setTextColor(context.getResources()
                    .getColor(R.color.deep_blue_imgbtn));
            holder.ac_keep_gun_type.setTextColor(context.getResources()
                    .getColor(R.color.deep_blue_imgbtn));
            holder.ac_keep_guncab_no.setTextColor(context.getResources()
                    .getColor(R.color.deep_blue_imgbtn));
            holder.ac_keep_guncab_pos.setTextColor(context.getResources().getColor(
                    R.color.deep_blue_imgbtn));
            holder.ac_keep_gun_reason.setTextColor(context.getResources()
                    .getColor(R.color.deep_blue_imgbtn));
        }
        return convertView;
    }



    static class ViewHolder {
        TextView ac_keep_gun_no;
        TextView ac_keep_gun_type;
        TextView ac_keep_guncab_no;
        TextView ac_keep_guncab_pos;
        TextView ac_keep_gun_reason;
    }
}
