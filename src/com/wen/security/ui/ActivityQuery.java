package com.wen.security.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.wen.security.R;
import com.wen.security.adapter.MainSysListAdapter;
import com.wen.security.adapter.MyPagerAdapter;
import com.wen.security.beans.SystemMessageInfo;
import com.wen.security.beans.db.SysLogDao;
import com.wen.security.serial.TaskMG;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 信息查询
 */
public class ActivityQuery extends BaseActivity {

    private static final String TAG = "ActivityQuery";
    private MyPagerAdapter pagerAdapter;
    private List<View> views;

    private View valarm, voper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        //信息查询界面只要枪管员登录就行了，不需要警员验证指纹

        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_activity_query);
        initRes();
        initEvent();
        TaskMG.getOne().taskCloseAlarm();

        logupdate();
        registerAlarm();
    }


    ListView ac_oper_list;
    ListView ac_alarm_list;
    MainSysListAdapter operAdapter, alarmAdapter;
    private RadioGroup ac_query_rg;
    private RadioButton ac_operate;
    private RadioButton ac_alarm;
    private TextView ac_query_index_txt;
    private TextView ac_query_totolnum_txt;
    private ViewPager video_vPager;
    private Button ac_query_download;

    protected void initRes() {
        super.initRes();
        ac_query_rg = (RadioGroup) findViewById(R.id.ac_query_rg);
        ac_operate = (RadioButton) findViewById(R.id.ac_operate);
        ac_alarm = (RadioButton) findViewById(R.id.ac_alarm);
        ac_query_index_txt = (TextView) findViewById(R.id.ac_query_index_txt);
        ac_query_totolnum_txt = (TextView) findViewById(R.id.ac_query_totolnum_txt);
        video_vPager = (android.support.v4.view.ViewPager) findViewById(R.id.video_vPager);
        ac_query_download = (Button) findViewById(R.id.ac_query_download);

        views = new ArrayList<View>();
        LayoutInflater inflater = getLayoutInflater();

        voper = inflater.inflate(R.layout.ac_query_oper, null);
        valarm = inflater.inflate(R.layout.ac_query_alarm, null);

        ac_oper_list = (ListView) voper.findViewById(R.id.ac_oper_list);
        ac_alarm_list = (ListView) valarm.findViewById(R.id.ac_alarm_list);

        views.add(voper);
        views.add(valarm);


        pagerAdapter = new MyPagerAdapter(this, views);
        video_vPager.setAdapter(pagerAdapter);
        video_vPager.setCurrentItem(0);
    }

    public void logupdate() {
        SysLogDao dao = new SysLogDao(this);
        dao.disposeLog();
        ArrayList<SystemMessageInfo> operlist = dao.queryAllLog(2);
        ArrayList<SystemMessageInfo> alarmlist = dao.queryAllLog(1);
        //ac_query_totolnum_txt.setText("总记录"+(operlist.size() +alarmlist.size())+"条");
        ac_query_totolnum_txt.setText("总记录" + dao.total() + "条");
        operAdapter = new MainSysListAdapter(this, operlist);
        alarmAdapter = new MainSysListAdapter(this, alarmlist);

        ac_oper_list.setAdapter(operAdapter);
        ac_alarm_list.setAdapter(alarmAdapter);

    }

    protected void initEvent() {
        super.initEvent();
        ac_query_rg.setOnCheckedChangeListener(news_onCheckedChangeListener);
        video_vPager.setOnPageChangeListener(news_OnPageChangeListener);
        ac_query_download.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 下载数据到U盘

            }
        });
    }

    //监听viewpager选中
    private ViewPager.OnPageChangeListener news_OnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            if (arg0 == 0) {
                ac_operate.setChecked(true);
            } else if (arg0 == 1) {
                ac_alarm.setChecked(true);
            }

        }
    };

    //监听RadioGroup点击
    private OnCheckedChangeListener news_onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.ac_operate) { //操作记录
                video_vPager.setCurrentItem(0, true);
            } else if (checkedId == R.id.ac_alarm) { //报警记录
                video_vPager.setCurrentItem(1, true);
            }

        }
    };

    //
    public BroadcastReceiver BtStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {

                /**
                 * 0)：断开
                 *
                 * 1)：连上USB
                 *
                 * 2)：连上了充电器
                 */
                Log.d(TAG, "" + intent.getIntExtra("plugged", 0));
                Log.d(TAG, "" + intent.getIntExtra("plugged", 1));
                Log.d(TAG, "" + intent.getIntExtra("plugged", 2));
                check();
                Toast text = Toast.makeText(context,
                        "ACTION_USB_DEVICE_ATTACHED" + intent.getIntExtra("plugged", 0),
                        Toast.LENGTH_LONG);
                text.show();
                // dataview.setText(""+intent.getIntExtra("plugged", 0));

//                final StatFs stat = new StatFs(Udisk.getPath());
//
//                final long blockSize = stat.getBlockSize();
//
//                final long totalBlocks = stat.getBlockCount();
//
//                final long availableBlocks = stat.getAvailableBlocks();
//
//                       long mTotalSize = totalBlocks * blockSize;
//
//                       long mAvailSize =availableBlocks * blockSize;
            }
        }
    };

    //检查USB连接
    void check() {
        try {
            StorageManager sm = (StorageManager) this
                    .getSystemService(Context.STORAGE_SERVICE);

            String[] paths;

            paths = (String[]) sm.getClass().getMethod("getVolumePaths", new Class[0])
                    .invoke(sm, new Object[]{});

            for (int i = 0; i < paths.length; i++) {
                Log.i(TAG, paths[i]);
                String status = (String) sm.getClass()
                        .getMethod("getVolumeState", String.class)
                        .invoke(sm, paths[i]);
                if (status.equals(android.os.Environment.MEDIA_MOUNTED)) {
                    String txt = "测试成功.txt";
                    String sPath = paths[i] + File.separator + txt;
                    File file = new File(sPath);
                    if (file.exists()) {
                        file.delete();
                    }
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    public void CheckUdisk() {
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(BtStatusReceiver, mIntentFilter);
    }
}
