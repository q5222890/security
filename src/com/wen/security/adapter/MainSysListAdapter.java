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
import com.wen.security.beans.SystemMessageInfo;
import com.wen.security.utils.RTools;

public class MainSysListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<SystemMessageInfo> objs;

    public MainSysListAdapter(Context mContext, ArrayList<SystemMessageInfo> mObjs) {

        this.mContext = mContext;
        if (mObjs!=null) {
            objs = mObjs;
        }else{
            objs = new ArrayList<SystemMessageInfo>();
        }/*else {
            objs = new ArrayList<SystemMessageInfo>();
            List<SystemMessageInfo> objs = new ArrayList<SystemMessageInfo>();
            objs.add(new SystemMessageInfo());
            objs.add(new SystemMessageInfo());
            objs.add(new SystemMessageInfo());
            objs.add(new SystemMessageInfo());
            objs.add(new SystemMessageInfo());
            objs.add(new SystemMessageInfo());
        }*/


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

    static class ViewHolder {
        TextView ac_main_sysinfo_txt;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.ac_main_sysinfo_item,
                    null);
            holder = new ViewHolder();
            holder.ac_main_sysinfo_txt = (TextView) convertView
                    .findViewById(R.id.ac_main_sysinfo_txt);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        SystemMessageInfo info= objs.get(position);
        String type ="";
        if (info.Message_Type == 1) {
            type = "报警信息";
            holder.ac_main_sysinfo_txt.setTextColor(mContext.getResources()
                    .getColor(R.color.deep_red_imgbtn));
        }
        if (info.Message_Type == 2) {
            type = "系统操作";
            holder.ac_main_sysinfo_txt.setTextColor(mContext.getResources()
                    .getColor(R.color.deep_blue_imgbtn));
        }
//        if (info.isUpload == true) {
//            
//            holder.ac_main_sysinfo_txt.setTextColor(mContext.getResources()
//                    .getColor(R.color.deep_blue_imgbtn));
//        }else{
//            holder.ac_main_sysinfo_txt.setTextColor(mContext.getResources()
//                    .getColor(R.color.deep_red_imgbtn));
//        }
        holder.ac_main_sysinfo_txt.setText(RTools.long2StrTime(info.Create_Time)+"  "+type +"  "+info.Other);

        return convertView;
    }

}
