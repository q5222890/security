package com.wen.security.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wen.security.DataCache;
import com.wen.security.R;
import com.wen.security.beans.GunCabInfo;
import com.wen.security.beans.StoreObjectInfo;
import com.wen.security.beans.SubCabInfo;
import com.wen.security.ui.view.AlignLeftGallery;

import java.util.ArrayList;
import java.util.List;

/**
 * 枪弹状态
 */
public class ActivityStatus extends BaseActivity {

    private static final String TAG = "ActivityStatus";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_activity_status);
        initRes();
        initEvent();
    }

    private List<GunCabInfo> list;


    AlignLeftGallery gallery;
    ListView ac_as_sub_cab;
    GridView as_store;
    CabAdapter adapter;
    SubCabAdapter subadpter;
    StoreAdapter stadpter;

    protected void initRes() {
        super.initRes();
        list = DataCache.getOne().gunCabInfolist;
        gallery = (AlignLeftGallery) findViewById(R.id.as_cab_list);
        ac_as_sub_cab = (ListView) findViewById(R.id.ac_as_sub_cab);
        as_store = (GridView) findViewById(R.id.as_store);
        // 设置图片适配器
        adapter = new CabAdapter(this, list);
        gallery.setAdapter(adapter);
        if (list.size() > 0) {
            subadpter = new SubCabAdapter(ActivityStatus.this, list.get(0).Sub_Cabs);
            ac_as_sub_cab.setAdapter(subadpter);
            ac_as_sub_cab.setSelection(0);
            int size = 0;
            try {
                Log.i(TAG,"list: "+list.get(0));
                size = list.get(0).Sub_Cabs.size();
            } catch (Exception e) {
                Log.e("-----------","error: "+e.getMessage());
                e.printStackTrace();
            }
            if (size > 0) {
                stadpter = new StoreAdapter(ActivityStatus.this, list.get(0).Sub_Cabs.get(0).Store_Objects);
                as_store.setAdapter(stadpter);
                as_store.setSelection(0);
            }
        }

        // 设置监听器
        gallery.setOnItemClickListener(new AlignLeftGallery.IOnItemClickListener() {

            @Override
            public void onItemClick(int position) {
                // TODO Auto-generated method stub
                GunCabInfo cab = adapter.getItem(
                        position);
                subadpter = new SubCabAdapter(ActivityStatus.this, cab.Sub_Cabs);
                ac_as_sub_cab.setAdapter(subadpter);
                //subadpter.notifyDataSetChanged();
            }
        });

        ac_as_sub_cab.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                SubCabInfo info = (SubCabInfo) arg0.getAdapter().getItem(arg2);
                as_store.setAdapter(new StoreAdapter(ActivityStatus.this, info.Store_Objects));
            }

        });

    }

    protected void initEvent() {
        super.initEvent();

    }

    class CabAdapter extends BaseAdapter {
        // 声明Context
        private Context context;

        private List<GunCabInfo> list;

        // 声明 ImageAdapter
        public CabAdapter(Context c, List<GunCabInfo> list) {
            this.list = list;
            if (list == null) {
                list = new ArrayList<GunCabInfo>();
            }
            context = c;
        }

        @Override
        // 获取图片的个数
        public int getCount() {
            return list.size();
        }

        @Override
        // 获取图片在库中的位置
        public GunCabInfo getItem(int position) {

            return list.get(position);
        }

        @Override
        // 获取图片在库中的位置
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater layoutInflator = LayoutInflater.from(context);
                convertView = layoutInflator.inflate(R.layout.ac_as_cab_item,
                        null);
                holder = new ViewHolder();
                holder.as_cab = (TextView) convertView
                        .findViewById(R.id.as_cab);

                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();

            holder.as_cab.setText("枪柜 " + list.get(position).Cab_ID);

            return convertView;
        }

        class ViewHolder {
            TextView as_cab;

        }
    }

    class SubCabAdapter extends BaseAdapter {
        // 声明Context
        private Context context;

        private List<SubCabInfo> list;

        // 声明 ImageAdapter
        public SubCabAdapter(Context c, List<SubCabInfo> list) {
            this.list = list;
            if (this.list == null) {
                this.list = new ArrayList<SubCabInfo>();
            }
            context = c;
        }

        @Override
        // 获取图片的个数
        public int getCount() {
            return list.size();
        }

        @Override
        // 获取图片在库中的位置
        public SubCabInfo getItem(int position) {

            return list.get(position);
        }

        @Override
        // 获取图片在库中的位置
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater layoutInflator = LayoutInflater.from(this.context);
                convertView = layoutInflator.inflate(R.layout.ac_as_cab_item,
                        null);
                holder = new ViewHolder();
                holder.as_cab = (TextView) convertView
                        .findViewById(R.id.as_cab);
                holder.as_img = (ImageView) convertView
                        .findViewById(R.id.as_img);
                holder.as_img.setImageResource(R.drawable.subcab);
                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();

            holder.as_cab.setText("抽屉 " + list.get(position).Sub_Cab_ID);

            return convertView;
        }

        class ViewHolder {
            TextView as_cab;
            ImageView as_img;
        }
    }

    class StoreAdapter extends BaseAdapter {
        // 声明Context
        private Context context;

        private List<StoreObjectInfo> list;

        // 声明 ImageAdapter
        public StoreAdapter(Context c, List<StoreObjectInfo> list) {

            if (list == null) {
                this.list = new ArrayList<StoreObjectInfo>();
            } else {
                this.list = list;
            }
            context = c;
        }

        @Override
        // 获取图片的个数
        public int getCount() {
            return list.size();
        }

        @Override
        // 获取图片在库中的位置
        public StoreObjectInfo getItem(int position) {

            return list.get(position);
        }

        @Override
        // 获取图片在库中的位置
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater layoutInflator = LayoutInflater.from(this.context);
                convertView = layoutInflator.inflate(R.layout.ac_as_cab_item,
                        null);
                holder = new ViewHolder();
                holder.as_cab = (TextView) convertView
                        .findViewById(R.id.as_cab);
                holder.as_img = (ImageView) convertView
                        .findViewById(R.id.as_img);
                holder.as_cab.setVisibility(View.GONE);
                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();
            String object_type = list.get(position).Object_Type;
            if (object_type != null) {

                if (object_type.equals("枪")) {
                    holder.as_img.setImageResource(R.drawable.shortgun);
                }
                if (object_type.equals("子弹弹药")) {
                    holder.as_img.setImageResource(R.drawable.shortgun);
                }
                if (object_type.equals("弹夹")) {
                    holder.as_img.setImageResource(R.drawable.shortgun);
                }
            }
            return convertView;
        }

        class ViewHolder {
            TextView as_cab;
            ImageView as_img;
        }
    }
}
