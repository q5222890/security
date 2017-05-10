package com.wen.security.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.wen.security.DataCache;
import com.wen.security.R;
import com.wen.security.adapter.KeepGunListAdapter;
import com.wen.security.beans.GunCabInfo;
import com.wen.security.beans.MenberInfo;
import com.wen.security.beans.StoreObjectInfo;
import com.wen.security.beans.SubCabInfo;
import com.wen.security.serial.TaskMG;
import com.wen.security.serial.TaskMG.IDoorStatus;
import com.wen.security.serial.TaskMG.IGunStatus;
import com.wen.security.serial.TaskMG.IStoreObjectStatus;
import com.wen.security.utils.DialogFactory;
import com.wen.security.utils.RTools;

import java.util.ArrayList;
import java.util.List;

/**
 * 枪弹保养
 */
public class ActivityKeep extends BaseActivity {
    protected final String TAG = "ActivityKeep";
    private ListView ac_keep_listview;
    private Button ac_keep_ok;
    private Button ac_keep_open;
    private Boolean isOpen = false;
    private KeepGunListAdapter adapter;
    private List<StoreObjectInfo> list;
    private Dialog dlg;
    MenberInfo sPMenberInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_activity_keep);
        loadData();
        initRes();
        initEvent();
        registerAlarm();

    }

    public ArrayList<GunCabInfo> gunCabInfolist;

    /**
     * 加载枪弹数据
     */
    private void loadData() {
        sPMenberInfo = DataCache.getOne().sPMenberInfo; //获取警员信息
        gunCabInfolist = DataCache.getOne().gunCabInfolist; //获取枪柜列表
        list = new ArrayList<StoreObjectInfo>(); //保存物件信息list
        if (gunCabInfolist != null) {
            for (int i = 0; i < gunCabInfolist.size(); i++) {
                List<SubCabInfo> Sub_Cabs = gunCabInfolist.get(i).Sub_Cabs;
                if (Sub_Cabs != null) {
                    for (int j = 0; j < Sub_Cabs.size(); j++) {
                        List<StoreObjectInfo> temp_Store_Objects = Sub_Cabs.get(j).Store_Objects;
                        if (temp_Store_Objects != null) {
                            list.addAll(temp_Store_Objects);
                        }
                    }
                }
            }
        }

    }


    /**
     * 退出当前activity
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

    protected void initRes() {
        super.initRes();
        ac_keep_listview = (ListView) findViewById(R.id.ac_keep_listview);
        adapter = new KeepGunListAdapter(this, list);
        ac_keep_listview.setAdapter(adapter);
        ac_keep_ok = (Button) findViewById(R.id.ac_keep_ok);
        ac_keep_open = (Button) findViewById(R.id.ac_keep_open);
        ac_keep_ok.setOnClickListener(Listener);
        ac_keep_open.setOnClickListener(Listener);
//        ac_keep_ok.setVisibility(View.GONE);
    }

    MyOnClickListener Listener = new MyOnClickListener();

    protected class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (v.getId() == R.id.ac_keep_ok) {
                Log.i(TAG,"确认");
                back();
            }
            if (v.getId() == R.id.ac_keep_open) {
                Log.i(TAG,"打开所有枪柜");
                openlock();
                registerGunStatus(); //更新枪锁状态
                TaskMG.getOne().registerIsDoorOpen(TAG, new IDoorStatus() {

                    @Override
                    public void onStatus(String id, Boolean isOpen) {
                        // TODO Auto-generated method stub
                        ActivityKeep.this.isOpen = isOpen;
                    }
                });
            }
        }
    }

    protected void initEvent() {
        super.initEvent();
    }

    static String CabOpenFlag = "1";

    /**
     * 开锁
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
        for (int i = 0; i < list.size(); i++) {
            StoreObjectInfo gun = list.get(i);

            byte[] s = {(byte) gun.Sub_Sequence};
            TaskMG.getOne().taskOpenLock(gun.Own_Cab_ID, gun.Own_Sub_ID, s,
                    new IStoreObjectStatus() {
                        @Override
                        public void onStatus(String id, String addr) {
                            // TODO Auto-generated method stub

                        }
                    });
        }

    }

    /**
     * 更新枪柜状态
     */
    public void registerGunStatus() {

        TaskMG.getOne().registerGetGunsChange(TAG, new IGunStatus() {

            @Override
            public void onStatus(String id, String cabAddr, String subCabAddr,
                                 ArrayList<Integer> nolist) {
                // TODO Auto-generated method stub
                for (int i = 0; i < list.size(); i++) {
                    StoreObjectInfo gun = list.get(i);
                    if (gun.Own_Cab_ID.equals(cabAddr)
                            && gun.Own_Sub_ID.equals(subCabAddr)) {
                        if (RTools.contains(gun.Sub_Sequence, nolist)) {
                            ac_keep_ok.setVisibility(View.VISIBLE);
                            if (!gun.IsOper) {
                                gun.IsOper = true;
                                //addGetGunLog(gun);
                            }
                        }
                    }
                }
                adapter.notifyDataSetInvalidated();
            }
        });

    }
}
