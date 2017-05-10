package com.wen.security.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wen.security.R;
import com.wen.security.beans.OperGunInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 枪支列表
 */
public class GunListAdapter extends BaseAdapter {

    private Context context;
    private List<OperGunInfo> objs;

    public GunListAdapter(Context mContext, List<OperGunInfo> mObjs) {

        context = mContext;
        objs = mObjs;
        if (objs == null) {
            objs = new ArrayList<OperGunInfo>();
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
            convertView = layoutInflator.inflate(R.layout.ac_go_gun_item, null);
            holder = new ViewHolder();
            holder.ac_go_gun_type_txt = (TextView) convertView
                    .findViewById(R.id.ac_go_gun_type_txt);
            holder.ac_go_gun_id_txt = (TextView) convertView
                    .findViewById(R.id.ac_go_gun_id_txt);
            holder.ac_go_gun_num_txt = (TextView) convertView
                    .findViewById(R.id.ac_go_gun_num_txt);
            holder.ac_go_p_id_txt = (TextView) convertView
                    .findViewById(R.id.ac_go_p_id_txt);
            holder.ac_go_pname_txt = (TextView) convertView
                    .findViewById(R.id.ac_go_pname_txt);
            holder.ac_go_gun_rnum_txt = (TextView) convertView
                    .findViewById(R.id.ac_go_gun_rnum_txt);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();

        holder.ac_go_gun_type_txt.setText(objs.get(position).GunTypeName);
        holder.ac_go_gun_id_txt.setText(objs.get(position).Gun_ID);

        holder.ac_go_p_id_txt.setText(objs.get(position).Draw_People);

        holder.ac_go_pname_txt.setText(objs.get(position).PoliceName);
        if (objs.get(position).Oper_Ammunition != null) {
            holder.ac_go_gun_num_txt
                    .setText(objs.get(position).Oper_Ammunition.Ammo_Nums+"");
            holder.ac_go_gun_rnum_txt
                    .setText(objs.get(position).Oper_Ammunition.Return_Nums+"");
        }

        if (objs.get(position).IsOper) {
            holder.ac_go_gun_type_txt.setTextColor(context.getResources()
                    .getColor(R.color.deep_red_imgbtn));
            holder.ac_go_gun_id_txt.setTextColor(context.getResources()
                    .getColor(R.color.deep_red_imgbtn));
            holder.ac_go_gun_num_txt.setTextColor(context.getResources()
                    .getColor(R.color.deep_red_imgbtn));
            holder.ac_go_p_id_txt.setTextColor(context.getResources().getColor(
                    R.color.deep_red_imgbtn));
            holder.ac_go_pname_txt.setTextColor(context.getResources()
                    .getColor(R.color.deep_red_imgbtn));
            holder.ac_go_gun_rnum_txt.setTextColor(context.getResources()
                    .getColor(R.color.deep_red_imgbtn));
        } else {
            holder.ac_go_gun_type_txt.setTextColor(context.getResources()
                    .getColor(R.color.deep_blue_imgbtn));
            holder.ac_go_gun_id_txt.setTextColor(context.getResources()
                    .getColor(R.color.deep_blue_imgbtn));
            holder.ac_go_gun_num_txt.setTextColor(context.getResources()
                    .getColor(R.color.deep_blue_imgbtn));
            holder.ac_go_p_id_txt.setTextColor(context.getResources().getColor(
                    R.color.deep_blue_imgbtn));
            holder.ac_go_pname_txt.setTextColor(context.getResources()
                    .getColor(R.color.deep_blue_imgbtn));
            holder.ac_go_gun_rnum_txt.setTextColor(context.getResources()
                    .getColor(R.color.deep_blue_imgbtn));
        }
        return convertView;
    }



    static class ViewHolder {
        TextView ac_go_gun_type_txt;
        TextView ac_go_gun_id_txt;
        TextView ac_go_gun_num_txt;
        TextView ac_go_p_id_txt;
        TextView ac_go_pname_txt;
        TextView ac_go_gun_rnum_txt;
    }
}
