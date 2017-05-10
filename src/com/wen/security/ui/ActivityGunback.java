package com.wen.security.ui;

import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.wen.security.DataCache;
import com.wen.security.DataFunc;
import com.wen.security.beans.OperGunInfo;
import com.wen.security.serial.TaskMG;
import com.wen.security.utils.DialogFactory;

/**
 * 枪弹归还
 *
 * @author Administrator
 *
 */
public class ActivityGunback extends ActivityBaseGo {
    protected final String TAG = "ActivityGunback";

    protected Dialog tipDialog;

    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        TaskMG.getOne().taskCloseAlarm();
        ac_go_ok.setText("确认还枪");
        DataFunc.addSysLog(this,sPMenberInfo, "进入还枪");

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        DataFunc.addSysLog(this,sPMenberInfo, "退出还枪");
    }



    @Override
    protected void checkData() {
        // 没有借出枪支，不能还枪()没有出警任务
        if (loadingdlg != null) {
            loadingdlg.dismiss();
            loadingdlg = null;
        }
        // TODO Auto-generated method stub

        //没有建任务不能还   建了任务已经还了不能再还
        if (requestPoliceTaskInfo == null || requestPoliceTaskInfo.is_done == true) {

            tipDialog = DialogFactory.creatTipDialog(this, "提示",
                    sPMenberInfo.User_Name + "无借出枪支，不能还枪",
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            tipDialog.dismiss();
                            ActivityGunback.this.finish();
                        }
                    });
            tipDialog.show();

        }
    }

    @Override
    protected void sendcheck() {
        // TODO Auto-generated method stub
        Toast.makeText(this, "还枪成功", Toast.LENGTH_SHORT).show();
        requestPoliceTaskInfo.is_done = true;
        requestPoliceTaskInfo.is_get = false;
        DataFunc.addSysLog(this,sPMenberInfo, "通过"+DataCache.getOne().sMenberInfo.User_Name+"枪管员 还枪成功 " );
        DataFunc.removePoliceTaskInfo(requestPoliceTaskInfo);

        if (loadingdlg != null) {
            loadingdlg.dismiss();
            loadingdlg = null;
        }
        back();
        //super.sendcheck();
    }

    @Override
    protected void addGetGunLog(OperGunInfo gun) {
        // TODO Auto-generated method stub
        gun.Return_Time = System.currentTimeMillis();
        DataFunc.addSysLog(this,sPMenberInfo, "通过"+DataCache.getOne().sMenberInfo.User_Name+"枪管员 还枪  " + "枪号："
                + gun.Gun_ID);
    }

}
