package com.wen.security.ui;

import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.wen.security.DataCache;
import com.wen.security.DataFunc;
import com.wen.security.beans.OperGunInfo;
import com.wen.security.utils.DialogFactory;

/**
 * 出警
 *
 * @author Administrator
 */
public class ActivityGo extends ActivityBaseGo {
    protected Dialog tipDialog;
    protected final String TAG = "ActivityGo";

    @Override
    protected void initView() {
        DataFunc.addSysLog(this, sPMenberInfo, "进入出警");
    }

    @Override
    protected void onPause() {
        super.onPause();
        DataFunc.addSysLog(this, sPMenberInfo, "退出出警");
    }

    @Override
    protected void checkData() {
        Log.i(TAG,"checkData");
        if (loadingdlg != null) {
            loadingdlg.dismiss();
            loadingdlg = null;
        }

        if (requestPoliceTaskInfo == null || requestPoliceTaskInfo.is_done == true) {

            tipDialog = DialogFactory.creatTipDialog(this, "提示",
                    sPMenberInfo.User_Name + "无出警任务", new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            tipDialog.dismiss();
                            ActivityGo.this.finish();
                        }
                    });
            tipDialog.show();

        } else if (requestPoliceTaskInfo.is_get == true) {
            tipDialog = DialogFactory.creatTipDialog(this, "已经领枪",
                    sPMenberInfo.User_Name + "已经领枪不能重复领",
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            tipDialog.dismiss();
                            // initIm();
                            ActivityGo.this.finish();
                        }
                    });
            tipDialog.show();
        }
    }

    @Override
    protected void sendcheck() {
        Toast.makeText(this, "领枪成功", Toast.LENGTH_SHORT).show();
        requestPoliceTaskInfo.is_done = false;
        requestPoliceTaskInfo.is_get = true;
        DataFunc.addPoliceTaskInfo(requestPoliceTaskInfo);
        super.sendcheck();
    }

    /**
     * 添加领枪日志
     * @param gun
     */
    @Override
    protected void addGetGunLog(OperGunInfo gun) {
        gun.Draw_Time = System.currentTimeMillis();
        DataFunc.addSysLog(this, sPMenberInfo,
                "通过" + DataCache.getOne().sMenberInfo.User_Name + "枪管员 领枪  "
                        + "枪号：" + gun.Gun_ID);
    }
}
