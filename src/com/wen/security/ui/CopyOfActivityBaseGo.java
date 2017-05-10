package com.wen.security.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.wen.security.DataCache;
import com.wen.security.DataFunc;
import com.wen.security.R;
import com.wen.security.adapter.GunListAdapter;
import com.wen.security.beans.GunCabInfo;
import com.wen.security.beans.MenberInfo;
import com.wen.security.beans.OperAmmunitionInfo;
import com.wen.security.beans.OperGunInfo;
import com.wen.security.beans.StoreObjectInfo;
import com.wen.security.beans.SubCabInfo;
import com.wen.security.beans.request.RequestPoliceOperGuns;
import com.wen.security.beans.request.RequestPoliceTaskInfo;
import com.wen.security.http.TextHttpResponseHandler;
import com.wen.security.serial.TaskMG;
import com.wen.security.serial.TaskMG.IStoreObjectStatus;
import com.wen.security.serial.TaskMG.ISubStoreObjectStatus;
import com.wen.security.utils.ApiClient;
import com.wen.security.utils.DialogFactory;
import com.wen.security.utils.FileCache;
import com.wen.security.utils.RTools;
import com.wen.security.utils.TLog;

public abstract class CopyOfActivityBaseGo extends BaseActivity {
    MenberInfo sPMenberInfo;
    RequestPoliceTaskInfo requestPoliceTaskInfo;

    protected Dialog loadingdlg;
    private final String TAG = "ActivityBaseGo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        initRes();
        initEvent();

        sPMenberInfo = DataCache.getOne().sPMenberInfo;
        ac_go_id.setText(sPMenberInfo.Police_Number);
        ac_go_name.setText(sPMenberInfo.User_Name);
        ac_go_department.setText(sPMenberInfo.Department);
        ac_go_rank.setText(RTools.convertRankid(sPMenberInfo.Rankid));
        ac_go_job.setText(sPMenberInfo.Duty);
        if (sPMenberInfo.Licence_Info.Hold_Type == 1) {

            ac_go_guntype.setText("公用");
        } else if (sPMenberInfo.Licence_Info.Hold_Type == 2) {
            ac_go_guntype.setText("专用");
        }
        ac_go_gunid.setText(sPMenberInfo.Licence_Info.Gun_ID);

        initView();
        loadData();

    }

    public void loadData() {
        getPoliceTaskInfo(this, sPMenberInfo.User_ID);
        // 从本地加载
        requestPoliceTaskInfo = DataFunc.findPoliceTask(sPMenberInfo.User_ID);
        if (requestPoliceTaskInfo != null) {
            initTaskView();
        }
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
        ac_go_ok.setEnabled(false);
        ac_go_open.setEnabled(false);
        ac_go_ok.setOnClickListener(Listener);
        ac_go_open.setOnClickListener(Listener);

    }

    MyOnClickListener Listener = new MyOnClickListener();

    protected class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (v.getId() == R.id.ac_go_ok) {
                // ac_go_open.setEnabled(true);
                List<OperGunInfo> Oper_Guns = new ArrayList<OperGunInfo>();
                for (int i = 0; i < requestPoliceTaskInfo.Oper_Guns.size(); i++) {
                    if (requestPoliceTaskInfo.Oper_Guns.get(i).IsOper == true) {
                        Oper_Guns.add(requestPoliceTaskInfo.Oper_Guns.get(i));
                    }
                }
                if (Oper_Guns.size() > 0) {
                    sendPoliceOperGuns(CopyOfActivityBaseGo.this,
                            requestPoliceTaskInfo.Task_ID, Oper_Guns);
                }
            }

            if (v.getId() == R.id.ac_go_open) {
                ac_go_open.setEnabled(false);
                openlock();
            }
        }

    }

    protected abstract void exit();

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        exit();
        TaskMG.getOne().removeAlwaysTask(TAG);
        super.onDestroy();
    }

    protected void initEvent() {
        super.initEvent();

        ac_go_listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                OperGunInfo temp = (OperGunInfo) arg0.getAdapter()
                        .getItem(arg2);
            }
        });

    }

    protected abstract void checkData();

    /**
     * 获取警员出警信息
     *
     * @param context
     * @param User_ID
     *
     */
    public void getPoliceTaskInfo(final Context context, String User_ID) {
        loadingdlg = DialogFactory.creatLoadDialog(this);
        loadingdlg.show();
        final FileCache fileCache = new FileCache(context);
        ApiClient.get(context, "api/PoliceTask/GetPoliceTask/" + User_ID,
                new TextHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        checkData();
                    }

                    public void onSuccess(int statusCode, Header[] headers,
                                          String responseString) {

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
                                DataFunc.addPoliceTaskInfo(requestPoliceTaskInfo);
                                initTaskView();

                            } else {
                                checkData();
                            }
                        } catch (Exception e) {
                            checkData();
                        }

                    }
                });

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

        RequestPoliceOperGuns request = new RequestPoliceOperGuns();
        request.Oper_Guns = Oper_Guns;
        ApiClient.post(context, "/api/PoliceTask/PoliceOperGuns/" + Task_ID,
                request, null, new TextHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        Toast.makeText(context, "提交失败", Toast.LENGTH_SHORT)
                                .show();
                    }

                    public void onSuccess(int statusCode, Header[] headers,
                                          String responseString) {

                        TLog.v("sendPoliceOperGuns", responseString);
                        JSONObject json;
                        try {
                            json = new JSONObject(responseString);
                            String Code = json.getString("Re_Code");
                            String Msg = json.getString("Re_Message");
                            if (Code.equals("0")) {
                                Toast.makeText(context, "提交成功",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "提交失败",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, "提交失败", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

    protected void initTaskView() {
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
        searchCabByGunList(requestPoliceTaskInfo.Oper_Guns);
        adapter.notifyDataSetInvalidated();
        ac_go_ok.setEnabled(true);
        ac_go_open.setEnabled(true);
    }

    public void searchCabByGunList(List<OperGunInfo> Oper_Guns) {
        if (Oper_Guns != null) {
            for (int i = 0; i < Oper_Guns.size(); i++) {
                searchstoreObjectByID(Oper_Guns.get(i));
            }
        }
    }

    /**
     * 要开锁的柜子列表
     */
    protected ArrayList<GunCabInfo> find_gunCabInfolist = new ArrayList<GunCabInfo>();
    /**
     * 要开锁的子柜列表
     */
    protected ArrayList<SubCabInfo> find_Sub_Cabs = new ArrayList<SubCabInfo>();
    /**
     * 要开锁的物件列表
     */
    protected ArrayList<StoreObjectInfo> find_Store_Objects = new ArrayList<StoreObjectInfo>();

    public void searchstoreObjectByID(OperGunInfo operGunInfo) {
        ArrayList<GunCabInfo> gunCabInfolist = DataCache.getOne().gunCabInfolist;
        if (gunCabInfolist != null) {
            boolean isSubCabInfo = false;
            for (int j = 0; j < gunCabInfolist.size(); j++) {
                // 枪柜
                GunCabInfo gunCabInfo = gunCabInfolist.get(j);
                if (gunCabInfo.Sub_Cabs != null) {
                    // 子柜
                    List<SubCabInfo> Sub_Cabs = gunCabInfo.Sub_Cabs;
                    for (int i = 0; i < Sub_Cabs.size(); i++) {
                        SubCabInfo subCabInfo = Sub_Cabs.get(i);
                        if (subCabInfo.Store_Objects != null) {
                            // 储存物品
                            boolean isFindStoreObject = false;
                            List<StoreObjectInfo> Store_Objects = subCabInfo.Store_Objects;
                            for (int k = 0; k < Store_Objects.size(); k++) {
                                StoreObjectInfo storeObjectInfo = Store_Objects
                                        .get(i);
                                if (storeObjectInfo.Object_ID
                                        .equals(operGunInfo.Gun_ID)) {
                                    operGunInfo.IsOper = true;
                                    isFindStoreObject = true;
                                    find_Store_Objects.add(storeObjectInfo);
                                    break;
                                }
                            }
                            if (isFindStoreObject) {
                                isSubCabInfo = true;
                                find_Sub_Cabs.add(subCabInfo);
                                break;
                            }
                        }
                    }

                    if (isSubCabInfo) {
                        find_gunCabInfolist.add(gunCabInfo);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 开锁
     */
    public void openlock() {
        TaskMG.getOne().taskOpenCabLock(find_gunCabInfolist,
                new IStoreObjectStatus() {

                    @Override
                    public void onStatus(String id, String addr) {
                        // TODO Auto-generated method stub

                    }
                });
        //   TaskMG.getOne().taskOpenSubCabLock(find_Sub_Cabs);
        // TaskMG.getOne().taskOpenStoreObjectLock(find_Store_Objects);

        TaskMG.getOne().registerGetGun(TAG, new IStoreObjectStatus() {

            @Override
            public void onStatus(String id, String addr) {
                // TODO Auto-generated method stub
                // changeGun_List(addr);
            }
        });
        TaskMG.getOne().registerGetGunAmmo(TAG, new ISubStoreObjectStatus() {

            @Override
            public void onStatus(String id, String addr, String num) {
                // TODO Auto-generated method stub
                changeGun_Amo(addr, num);
            }
        });
    }

    /**
     * 更新枪状态
     *
     * @param addr
     *            枪的地址
     * @param num
     *            枪的实际还弹弹药数量
     */
    /*public void changeGun_List(String addr) {
        if (requestPoliceTaskInfo.Oper_Guns != null) {
            for (int i = 0; i < requestPoliceTaskInfo.Oper_Guns.size(); i++) {
                if (addr.equals(requestPoliceTaskInfo.Oper_Guns.get(i).addr)) {
                    requestPoliceTaskInfo.Oper_Guns.get(i).IsOper = true;
                    DataFunc.addSysLog(sPMenberInfo, "领枪  " + "枪号："
                            + requestPoliceTaskInfo.Oper_Guns.get(i).Gun_ID);
                    // requestPoliceTaskInfo.Oper_Guns.get(i).Oper_Ammunition.Return_Nums
                    // = num;
                }
            }
            adapter.notifyDataSetInvalidated();
        }
    }*/

    public void changeGun_Amo(String addr, String num) {
        if (requestPoliceTaskInfo.Oper_Guns != null) {
            for (int i = 0; i < requestPoliceTaskInfo.Oper_Guns.size(); i++) {
                OperAmmunitionInfo amo = requestPoliceTaskInfo.Oper_Guns.get(i).Oper_Ammunition;
                if (amo != null && addr.equals(amo.addr)) {
                    requestPoliceTaskInfo.Oper_Guns.get(i).IsOper = true;
                    amo.Return_Nums = Integer.parseInt(num);
                    DataFunc.addSysLog(this,sPMenberInfo, "领弹：" + amo.Return_Nums);
                }
            }
            adapter.notifyDataSetInvalidated();
        }
    }
}
