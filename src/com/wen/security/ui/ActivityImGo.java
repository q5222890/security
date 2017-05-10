package com.wen.security.ui;

import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.wen.security.DataCache;
import com.wen.security.DataFunc;
import com.wen.security.beans.OperGunInfo;
import com.wen.security.beans.request.RequestPoliceTaskInfo;
import com.wen.security.utils.DialogFactory;
import com.wen.security.utils.RTools;

/**
 * 紧急出警
 *
 * @author Administrator
 *
 */
public class ActivityImGo extends ActivityBaseGo {

    protected final String TAG = "ActivityImGo";
    protected Dialog tipDialog;

    @Override
    protected void initView() {
        DataFunc.addSysLog(this,sPMenberInfo, "进入紧急出警");
    }

    @Override
    protected void onPause() {
        super.onPause();
        DataFunc.addSysLog(this,sPMenberInfo, "退出紧急出警");
    }

    boolean isOffLine = false;

    @Override
    protected void checkData() {
        if (loadingdlg != null) {
            loadingdlg.dismiss();
            loadingdlg = null;
        }

        if (requestPoliceTaskInfo == null || requestPoliceTaskInfo.is_done == true) {
            isOffLine = true;
            tipDialog = DialogFactory.creatTipDialog(this, "无在线出警任务",
                    sPMenberInfo.User_Name + "进入离线紧急出警", new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            tipDialog.dismiss();
                            initIm();
                        }
                    });
            tipDialog.show();
            return;
        }

        if (requestPoliceTaskInfo.is_get == true) {
            tipDialog = DialogFactory.creatTipDialog(this, "已经领枪",
                    sPMenberInfo.User_Name + "已经领枪不能重复领",
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            tipDialog.dismiss();
                            // initIm();
                            ActivityImGo.this.finish();
                        }
                    });
            tipDialog.show();
            return;
        }

        if (requestPoliceTaskInfo.is_done == true) {
            isOffLine = true;
            tipDialog = DialogFactory.creatTipDialog(this, "无最新联网数据",
                    sPMenberInfo.User_Name + "进入离线模式", new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            tipDialog.dismiss();
                            initIm();
                        }
                    });
            tipDialog.show();
            return;
        }
    }

    public void initIm() {

        // 初始化离线任务
        requestPoliceTaskInfo = new RequestPoliceTaskInfo();
        requestPoliceTaskInfo.User_ID = sPMenberInfo.User_ID;
        requestPoliceTaskInfo.Task_ID = "紧急";
        requestPoliceTaskInfo.Apply_Base = 4;
        requestPoliceTaskInfo.Remark = "离线状态紧急出警";
        requestPoliceTaskInfo.Task_Begin_Time = RTools.getTimeToM();
        requestPoliceTaskInfo.Oper_Guns = DataCache.findGuns();
        requestPoliceTaskInfo.is_done = false;
        requestPoliceTaskInfo.is_get = false;;
        DataFunc.addPoliceTaskInfo(requestPoliceTaskInfo);
        // 初始化view
        initTaskView();
    }

    @Override
    protected void sendcheck() {
        Toast.makeText(this, "领枪成功", Toast.LENGTH_SHORT).show();
        requestPoliceTaskInfo.is_get = true;
        requestPoliceTaskInfo.is_done = false;
        DataFunc.addPoliceTaskInfo(requestPoliceTaskInfo);
        super.sendcheck();
    }

    @Override
    protected void addGetGunLog(OperGunInfo gun) {
        gun.Draw_Time = System.currentTimeMillis();
        DataFunc.addSysLog(this,sPMenberInfo,
                "通过" + DataCache.getOne().sMenberInfo.User_Name + "枪管员 领枪  "
                        + "枪号：" + gun.Gun_ID);
    }
}
