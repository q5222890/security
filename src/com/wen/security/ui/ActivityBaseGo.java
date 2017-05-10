package com.wen.security.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.wen.security.DataCache;
import com.wen.security.DataFunc;
import com.wen.security.R;
import com.wen.security.adapter.GunListAdapter;
import com.wen.security.beans.MenberInfo;
import com.wen.security.beans.OperGunInfo;
import com.wen.security.beans.request.RequestPoliceOperGuns;
import com.wen.security.beans.request.RequestPoliceTaskInfo;
import com.wen.security.http.TextHttpResponseHandler;
import com.wen.security.serial.TaskMG;
import com.wen.security.serial.TaskMG.IDoorStatus;
import com.wen.security.serial.TaskMG.IGunStatus;
import com.wen.security.serial.TaskMG.IStoreObjectStatus;
import com.wen.security.utils.ApiClient;
import com.wen.security.utils.DialogFactory;
import com.wen.security.utils.FileCache;
import com.wen.security.utils.MainConfig;
import com.wen.security.utils.RTools;
import com.wen.security.utils.TLog;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public abstract class ActivityBaseGo extends BaseActivity {
    MenberInfo sPMenberInfo;    //警员信息
    RequestPoliceTaskInfo requestPoliceTaskInfo; //请求出警任务信息
    static String CabOpenFlag = "1"; //枪柜打开标志
    protected Dialog loadingdlg;
    protected final String TAG = "ActivityBaseGo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initRes();
        initEvent();

        sPMenberInfo = DataCache.getOne().sPMenberInfo; //警员信息
        if (sPMenberInfo != null) {
            ac_go_id.setText(RTools.getString(sPMenberInfo.Police_Number));
            ac_go_name.setText(sPMenberInfo.User_Name);
            ac_go_department.setText(sPMenberInfo.Department);
            ac_go_rank.setText(RTools.convertRankid(sPMenberInfo.Rankid));
            ac_go_job.setText(sPMenberInfo.Duty);
            if (sPMenberInfo.Licence_Info != null) {
                if (sPMenberInfo.Licence_Info.Hold_Type == 1) {

                    ac_go_guntype.setText("公用");
                } else if (sPMenberInfo.Licence_Info.Hold_Type == 2) {
                    ac_go_guntype.setText("专用");
                }
                ac_go_gunid.setText(sPMenberInfo.Licence_Info.Gun_ID);
            }

            initView();
            loadData();
            registerAlarm();
        }
    }

    /**
     * 加载出警数据
     */
    public void loadData() {
        getPoliceTaskInfo(this, sPMenberInfo.User_ID);
        // 从本地加载
        // requestPoliceTaskInfo =
        // DataFunc.findPoliceTask(sPMenberInfo.User_ID);
        // if (requestPoliceTaskInfo != null) {
        // for (int i = 0; i < requestPoliceTaskInfo.Oper_Guns.size(); i++) {
        // requestPoliceTaskInfo.Oper_Guns.get(i).IsOper = false;
        // }
        // initTaskView();
        // }
    }

    protected abstract void initView();

    protected EditText ac_go_time;
    protected EditText ac_go_go_time;
    protected EditText ac_go_back_time;
    protected EditText ac_go_men;
    protected ImageView ac_go_icon_img;
    protected EditText ac_go_id;
    protected EditText ac_go_name;
    protected EditText ac_go_department;
    protected EditText ac_go_rank;
    protected EditText ac_go_job;
    protected EditText ac_go_guntype;
    protected EditText ac_go_gunid;
    protected EditText ac_go_mark;
    protected TextView ac_go_reason;
    protected ListView ac_go_listview;
    protected Button ac_go_ok;
    protected Button ac_go_open;

    // protected RequestPoliceTaskInfo info;
    protected GunListAdapter adapter;

    protected void initRes() {
        setContentView(R.layout.ac_activity_go);
        super.initRes();

        ac_go_time = (EditText) findViewById(R.id.ac_go_time);
        ac_go_go_time = (EditText) findViewById(R.id.ac_go_go_time);
        ac_go_back_time = (EditText) findViewById(R.id.ac_go_back_time);
        ac_go_men = (EditText) findViewById(R.id.ac_go_men);
        ac_go_icon_img = (ImageView) findViewById(R.id.ac_go_icon_img);
        ac_go_id = (EditText) findViewById(R.id.ac_go_id);
        ac_go_name = (EditText) findViewById(R.id.ac_go_name);
        ac_go_department = (EditText) findViewById(R.id.ac_go_department);
        ac_go_rank = (EditText) findViewById(R.id.ac_go_rank);
        ac_go_job = (EditText) findViewById(R.id.ac_go_job);
        ac_go_guntype = (EditText) findViewById(R.id.ac_go_guntype);
        ac_go_gunid = (EditText) findViewById(R.id.ac_go_gunid);
        ac_go_mark = (EditText) findViewById(R.id.ac_go_mark);
        ac_go_reason = (TextView) findViewById(R.id.ac_go_reason);
        ac_go_listview = (ListView) findViewById(R.id.ac_go_listview);
        ac_go_ok = (Button) findViewById(R.id.ac_go_ok);
        ac_go_open = (Button) findViewById(R.id.ac_go_open);

        ac_go_time.setEnabled(false);
        ac_go_go_time.setEnabled(false);
        ac_go_back_time.setEnabled(false);
        ac_go_men.setEnabled(false);
        ac_go_id.setEnabled(false);
        ac_go_name.setEnabled(false);
        ac_go_department.setEnabled(false);
        ac_go_rank.setEnabled(false);
        ac_go_job.setEnabled(false);
        ac_go_guntype.setEnabled(false);
        ac_go_gunid.setEnabled(false);
        ac_go_mark.setEnabled(false);
        ac_go_ok.setVisibility(View.GONE);
        ac_go_ok.setOnClickListener(Listener);
        ac_go_open.setOnClickListener(Listener);

    }

    Dialog dlg;

    /**
     * 返回按钮监听门是否关闭
     */
    @Override
    public void back() {
        if (!isOpen) {
            finish();
        } else {
            if (dlg == null) {
                dlg = DialogFactory.creatTipDialog(this, "警告", "请关上柜门",
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                if (dlg != null) {
                                    dlg.dismiss();
                                    dlg = null;
                                }
                                back();

                            }
                        });
                dlg.show();
            }
        }
    }

    MyOnClickListener Listener = new MyOnClickListener();
    Dialog tipDialog;

    protected class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (v.getId() == R.id.ac_go_ok) { //确认领枪
                // ac_go_open.setEnabled(true);

                // if (!isOpen) {
                List<OperGunInfo> Oper_Guns = new ArrayList<OperGunInfo>();
                for (int i = 0; i < requestPoliceTaskInfo.Oper_Guns
                        .size(); i++) {
                    if (requestPoliceTaskInfo.Oper_Guns.get(i).IsOper == true) {
                        // requestPoliceTaskInfo.Oper_Guns.get(i).IsOper =
                        // false;
                        Oper_Guns.add(requestPoliceTaskInfo.Oper_Guns.get(i));
                    }
                }
                if (Oper_Guns.size() > 0) {
                    sendPoliceOperGuns(ActivityBaseGo.this,
                            requestPoliceTaskInfo.Task_ID, Oper_Guns);
                } else {
                    tipDialog = DialogFactory.creatTipDialog(
                            ActivityBaseGo.this, "没有领枪",
                            sPMenberInfo.User_Name + "没有领枪，不能确认领枪",
                            new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    tipDialog.dismiss();

                                }
                            });
                    tipDialog.show();
                }
                // }
            }

            if (v.getId() == R.id.ac_go_open) { //打开枪柜
                // ac_go_open.setEnabled(false);
                openlock();
                /*
                 * TaskMG.getOne().registerTimeOutOpen(TAG, new
                 * IStoreObjectStatus() {
                 *
                 * @Override public void onStatus(String id, String addr) { //
                 * DataFunc.addAlarmLog(sPMenberInfo.User_Name +" 超时未关门"); } });
                 */
                if (MainConfig.isDebug) { //调试程序
                    for (int i = 0; i < requestPoliceTaskInfo.Oper_Guns
                            .size(); i++) {
                        OperGunInfo gun = requestPoliceTaskInfo.Oper_Guns
                                .get(i);

                        ac_go_ok.setVisibility(View.VISIBLE); //显示确认领枪

                        gun.IsOper = true;  //设置为可领用

                        addGetGunLog(gun);  //添加领枪日志

                    }
                    adapter.notifyDataSetInvalidated();
                } else {
                    registerGunStatus();
                }

                TaskMG.getOne().registerIsDoorOpen(TAG, new IDoorStatus() {

                    @Override
                    public void onStatus(String id, Boolean isOpen) {
                        ActivityBaseGo.this.isOpen = isOpen;
                    }
                });
            }
        }
    }

    private Boolean isOpen = false;

    // protected abstract void exit();

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        // exit();
        TaskMG.getOne().removeAlwaysTask(TAG);
        super.onDestroy();
    }

    protected void initEvent() {
        super.initEvent();

        ac_go_listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 获取当前条目数据
                OperGunInfo operGunInfo = (OperGunInfo) parent.getAdapter()
                        .getItem(position);

            }
        });

    }

    protected abstract void checkData();

    /**
     * 获取警员出警信息
     *
     * @param context
     * @param User_ID
     */
    public void getPoliceTaskInfo(final Activity context, String User_ID) {
        loadingdlg = DialogFactory.creatLoadDialog(this);
        loadingdlg.show();
        final FileCache fileCache = new FileCache(context);
        //api/task/current/{policeId}
        ApiClient.get(context, "api/task/current/" + User_ID,
//        ApiClient.get(context, "api/PoliceTask/GetPoliceTask/" + User_ID,
                new TextHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        Log.i(TAG, "onFailure");
                        Log.i(TAG, "throwable msg:" + throwable.toString()); //NotFound
                        requestPoliceTaskInfo = DataFunc
                                .findPoliceTask(sPMenberInfo.User_ID);
                        if (requestPoliceTaskInfo != null) {
                            initTaskView();
                        }
                        checkData();
                    }

                    public void onSuccess(int statusCode, Header[] headers,
                                          String responseString) {
                        Log.i(TAG, "onSuccess");
                        TLog.v("getPoliceTaskInfo", responseString);
                        JSONObject json;
                        try {
                            json = new JSONObject(responseString);
                            String Code = json.getString("Re_Code");
                            String Msg = json.getString("Re_Message");
                            if (Code.equals("0")) {
                                String Body = json.getString("Body");
                                requestPoliceTaskInfo = JSON.parseObject(Body,
                                        RequestPoliceTaskInfo.class);

                                if (requestPoliceTaskInfo != null) {
                                    // 从本地加载
                                    DataFunc.addPoliceTaskInfo(
                                            requestPoliceTaskInfo);

                                }
                            }
                        } catch (Exception e) {

                        }
                        requestPoliceTaskInfo = DataFunc
                                .findPoliceTask(
                                        sPMenberInfo.User_ID);
                        if (requestPoliceTaskInfo != null) {
                            initTaskView();
                        }
                        checkData();
                    }
                });
    }

    protected void sendcheck() {
        // Toast.makeText(this, "领枪成功", Toast.LENGTH_SHORT).show();
        if (loadingdlg != null) {
            loadingdlg.dismiss();
            loadingdlg = null;
        }

        // if (requestPoliceTaskInfo != null) {
        // for (int i = 0; i < requestPoliceTaskInfo.Oper_Guns.size(); i++) {
        // DataCache.takeGun(requestPoliceTaskInfo.Oper_Guns.get(i));
        // }
        // }
        back();
    }

    /**
     * 申请领还枪
     *
     * @param context
     * @param Task_ID
     * @param Oper_Guns
     */
    public void sendPoliceOperGuns(final Context context, String Task_ID,
                                   List<OperGunInfo> Oper_Guns) {
        loadingdlg = DialogFactory.creatLoadDialog(this);
        loadingdlg.show();

        RequestPoliceOperGuns request = new RequestPoliceOperGuns();
        request.Oper_Guns = Oper_Guns;
        ApiClient.post(context, "/api/PoliceTask/PoliceOperGuns/" + Task_ID,
                request, null, new TextHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        TLog.v("sendPoliceOperGuns", responseString);
                        //  Toast.makeText(context, "领枪成功，更新至本地数据库", Toast.LENGTH_SHORT).show();
                        sendcheck();

                    }

                    public void onSuccess(int statusCode, Header[] headers,
                                          String responseString) {

                        TLog.v("sendPoliceOperGuns", responseString);
                        JSONObject json;
                        try {
                            json = new JSONObject(responseString);
                            String Code = json.getString("Re_Code");
                            String Msg = json.getString("Re_Message");
//                            if (Code.equals("0")) {
//                                Toast.makeText(context, "领枪成功，更新至服务器",
//                                        Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(context, "领枪成功，更新至本地数据库",
//                                        Toast.LENGTH_SHORT).show();
//                            }
                        } catch (Exception e) {

                        }
                        sendcheck();
                    }
                });
    }

    /**
     * 计划出警原因
     */

    protected void initTaskView() {
        Log.i(TAG, "initTaskView");
        ac_go_time.setText(requestPoliceTaskInfo.ApprovTime);
        ac_go_go_time.setText(requestPoliceTaskInfo.AllowBeginTime);
        ac_go_back_time.setText(requestPoliceTaskInfo.AllowEndTime);
        ac_go_men.setText(requestPoliceTaskInfo.ApprovPoliceName);
        /*
         * 1、 涉黄 2、 涉赌 3、 刑事案件
         */
        switch (requestPoliceTaskInfo.Apply_Base) {
            case 1:
                ac_go_reason.setText("涉黄");
                break;
            case 2:
                ac_go_reason.setText("涉赌");
                break;
            case 3:
                ac_go_reason.setText("刑事案件");
                break;
            case 4:
                ac_go_reason.setText("紧急出警");
                break;

            default:
                ac_go_reason.setText("其他");
                break;
        }

        ac_go_mark.setText(requestPoliceTaskInfo.Remark);
        adapter = new GunListAdapter(this, requestPoliceTaskInfo.Oper_Guns);
        ac_go_listview.setAdapter(adapter);
    }

    /**
     * 开枪柜门锁
     */
    public void openlock() {
        // 开柜子锁
        TaskMG.getOne().taskOpenCabLock(null, new IStoreObjectStatus() {
            @Override
            public void onStatus(String id, String addr) {
                // TODO Auto-generated method stub
                // 如果打开门成功，再打开手枪柜
                CabOpenFlag = "0";

            }
        });
        /**
         * 如果柜门已经打开，才发打开枪锁命令
         */
        // if(SerialMG.CabDoor[1][1] ==1)//如果柜门已经打开，才发打开枪锁命令

        // 开枪锁
        for (int i = 0; i < requestPoliceTaskInfo.Oper_Guns.size(); i++) {
            OperGunInfo gun = requestPoliceTaskInfo.Oper_Guns.get(i);

            byte[] s = {(byte) gun.Sub_Sequence};
            TaskMG.getOne().taskOpenLock(gun.Own_Cab_ID, gun.Own_Sub_ID, s,
                    new IStoreObjectStatus() {
                        @Override
                        public void onStatus(String id, String addr) {
                            //物件状态

                        }
                    });
        }

    }

    /**
     * 注册领枪
     */
    public void registerGunStatus() {

        TaskMG.getOne().registerGetGunsChange(TAG, new IGunStatus() {

            @Override
            public void onStatus(String id, String cabAddr, String subCabAddr,
                                 ArrayList<Integer> nolist) {
                // TODO Auto-generated method stub
                for (int i = 0; i < requestPoliceTaskInfo.Oper_Guns
                        .size(); i++) {
                    OperGunInfo gun = requestPoliceTaskInfo.Oper_Guns.get(i);
                    if (gun.Own_Cab_ID.equals(cabAddr)
                            && gun.Own_Sub_ID.equals(subCabAddr)) {
                        if (contains(gun.Sub_Sequence, nolist)) {
                            ac_go_ok.setVisibility(View.VISIBLE);
                            if (!gun.IsOper) {
                                gun.IsOper = true;
                                addGetGunLog(gun); //添加领枪日志
                            }
                        }
                    }

                }
                adapter.notifyDataSetInvalidated();
            }
        });

    }

    /**
     * 添加领枪日志
     *
     * @param gun
     */
    protected abstract void addGetGunLog(OperGunInfo gun);

    private boolean contains(Integer i, ArrayList<Integer> nolist) {
        for (int j = 0; j < nolist.size(); j++) {
            if (i == nolist.get(j)) {
                return true;
            }
        }
        return false;
    }

    /*
     * public void changeGun_Amo(String addr, String num) { if
     * (requestPoliceTaskInfo.Oper_Guns != null) { for (int i = 0; i <
     * requestPoliceTaskInfo.Oper_Guns.size(); i++) { OperAmmunitionInfo amo =
     * requestPoliceTaskInfo.Oper_Guns.get(i).Oper_Ammunition; if (amo != null
     * && addr.equals(amo.addr)) { requestPoliceTaskInfo.Oper_Guns.get(i).IsOper
     * = true; amo.Return_Nums = Integer.parseInt(num);
     * DataFunc.addSysLog(sPMenberInfo, "领弹：" + amo.Return_Nums); } }
     * adapter.notifyDataSetInvalidated(); } }
     */
}
