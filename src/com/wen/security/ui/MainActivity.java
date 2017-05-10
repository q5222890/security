package com.wen.security.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.wen.security.DataCache;
import com.wen.security.DataCache.IDataCacheStatus;
import com.wen.security.DataFunc;
import com.wen.security.R;
import com.wen.security.adapter.MainSysListAdapter;
import com.wen.security.beans.MenberRecordInfo;
import com.wen.security.beans.SystemMessageInfo;
import com.wen.security.beans.db.SysLogDao;
import com.wen.security.finger.FingerMG;
import com.wen.security.serial.TaskMG;
import com.wen.security.ui.view.InitDialog;
import com.wen.security.ui.view.LoginDialog;
import com.wen.security.ui.view.LoginQurreyDialog;
import com.wen.security.utils.DialogFactory;
import com.wen.security.utils.MainConfig;
import com.wen.security.utils.TLog;
import com.wen.security.utils.UploadService;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 主界面
 *
 * @author Administrator
 *
 */

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    protected Dialog tipDialog;
    private boolean isDevOpen =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ac_activity_main);
        final InitDialog loadingdlg = new InitDialog(this);
        loadingdlg.show(); //显示初始化对话框
        initRes();
        initEvent();
        FingerMG.getOne().init(this);

        //获取最新数据
        DataCache.getOne().loadData(this, new IDataCacheStatus() {

            @Override
            public void onLoading(long current, long total) { //正在载入数据的进度
                TLog.v(total + ":" + current);
                loadingdlg.setTip("正在加载：" + current + "，" + "共有: " + total);
            }

            @Override
            public void didLoading(boolean success) { //完成载入数据返回结果集
                loadingdlg.dismiss();
                if (success) { //加载数据完成
                    TaskMG.getOne().init();
                    registerAlarm();

                    initView(DataCache.getOne().sMenberInfo);

                    Intent intent = new Intent(MainActivity.this,UploadService.class);
                    startService(intent);
                } else {  //加载失败
                    tipDialog = DialogFactory.creatTipDialog(MainActivity.this,
                            "初始化失败", "请退出重试", new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    tipDialog.dismiss();
                                   // MainActivity.this.finish();
                                }
                            });
                    tipDialog.show();
                }
                try {
                    FingerMG.getOne().OnOpenDeviceBtn();//打开指纹设备
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        if (!MainConfig.isDebug) {
//            FingerMG.getOne().OnCloseDeviceBtn();
            TaskMG.getOne().close();
        }
        super.onDestroy();
    }
    /**
     * 刷新日志
     */
    protected void update() {
        SysLogDao dao = new SysLogDao(this);
        ArrayList<SystemMessageInfo> personList = dao.queryAll();
        for (int i = 0; i < personList.size(); i++) {
            SystemMessageInfo systemMessageInfo = personList.get(i);
        }
        SysLogDao.getInstance(this).deleteAll();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        update();
    }

    private ImageButton ac_main_go_btn;
    private ImageButton ac_main_emergency_go_btn;
    private ImageButton ac_main_gunshot_btn;
    private ImageButton ac_main_query_btn;
    private ImageButton ac_main_gunstatus_btn;
    private ImageButton ac_main_gunkeep_btn;
//    private ListView ac_main_sysinfo_listview;

    private MainSysListAdapter adapter;

    /**
     * 初始化资源
     */
    protected void initRes() {
        super.initRes();
        ac_top_back.setVisibility(View.GONE);
        ac_main_go_btn = (ImageButton) findViewById(R.id.ac_main_go_btn);
        ac_main_emergency_go_btn = (ImageButton) findViewById(R.id.ac_main_emergency_go_btn);
        ac_main_gunshot_btn = (ImageButton) findViewById(R.id.ac_main_gunshot_btn);
        ac_main_query_btn = (ImageButton) findViewById(R.id.ac_main_query_btn);
        ac_main_gunstatus_btn = (ImageButton) findViewById(R.id.ac_main_gunstatus_btn);
        ac_main_gunkeep_btn = (ImageButton) findViewById(R.id.ac_main_gunkeep_btn);
//        ac_main_sysinfo_listview = (ListView) findViewById(R.id.ac_main_sysinfo_listview);

    }

    protected void initEvent() {
        super.initEvent();
        ac_main_go_btn.setOnClickListener(this);
        ac_main_emergency_go_btn.setOnClickListener(this);
        ac_main_gunshot_btn.setOnClickListener(this);
        ac_main_query_btn.setOnClickListener(this);
        ac_main_gunstatus_btn.setOnClickListener(this);
        ac_main_gunkeep_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        // fack
        // DataFunc.findPolice("0");

        if (MainConfig.isDebug) {
            ArrayList<MenberRecordInfo> list = DataCache.getOne().sMenber_Datas;
            if (list != null && list.size()>0) {
                DataCache.getOne().sPMenberInfo = list.get(list.size()-1);
            }
        }
        Intent intent;
        LoginDialog dialog;
        switch (v.getId()) {
            case R.id.ac_main_go_btn:  //出警

                if (MainConfig.isDebug) {
                    // DataFunc.findPolice("11");
                    intent = new Intent(this, ActivityGo.class);
                    startActivity(intent);

                } else {
                    dialog = new LoginDialog(this, ActivityGo.class);
                    dialog.show();
                }
                break;
            case R.id.ac_main_emergency_go_btn: //紧急出警
                if (MainConfig.isDebug) {
                    //  DataFunc.findPolice("11");
                    intent = new Intent(this, ActivityImGo.class);
                    startActivity(intent);

                } else {
                    dialog = new LoginDialog(this, ActivityImGo.class);
                    dialog.show();
                }
                break;
            case R.id.ac_main_gunshot_btn:  //枪弹归还
                if (MainConfig.isDebug) {
                    //  DataFunc.findPolice("21");
                    intent = new Intent(this, ActivityGunback.class);
                    startActivity(intent);

                } else {
                    dialog = new LoginDialog(this, ActivityGunback.class);
                    dialog.show();
                }

                break;
            case R.id.ac_main_query_btn:  //信息查询
                if (MainConfig.isDebug) {
                    // DataFunc.findPolice("11");
                    intent = new Intent(this, ActivityQuery.class);
                    startActivity(intent);

                } else {
                    dialog = new LoginQurreyDialog(this, ActivityQuery.class);
                    dialog.show();
                }
                break;
            case R.id.ac_main_gunstatus_btn: //枪弹状态
                if (MainConfig.isDebug) {
                    DataFunc.findPolice("11");
                    intent = new Intent(this, ActivityStatus.class);
                    startActivity(intent);
                } else {
                    dialog = new LoginDialog(this, ActivityStatus.class);
                    dialog.show();
                }
                break;
            case R.id.ac_main_gunkeep_btn:  //枪弹保养
                if (MainConfig.isDebug) {
                    // DataFunc.findPolice("11");
                    intent = new Intent(this, ActivityKeep.class);
                    startActivity(intent);

                } else {
                    dialog = new LoginDialog(this, ActivityKeep.class);
                    dialog.show();
                }
                break;

            default:
                break;
        }
    }


}
