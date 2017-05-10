package com.wen.security.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wen.security.DataCache;
import com.wen.security.DataFunc;
import com.wen.security.R;
import com.wen.security.beans.MenberInfo;
import com.wen.security.beans.request.RequestMemberHandover;
import com.wen.security.finger.FingerMG;
import com.wen.security.http.TextHttpResponseHandler;
import com.wen.security.serial.TaskMG;
import com.wen.security.utils.ApiClient;
import com.wen.security.utils.MainConfig;
import com.wen.security.utils.TLog;
import com.wen.security.utils.ToastUtil;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * 指纹验证提示对话框
 *
 * @author Administrator
 *
 */
public class LoginDialog extends Dialog implements
        View.OnClickListener, FingerMG.IFingerStatus {
    /**
     * 记录当前管理员ID,如果第二登录人ID与它相同,则不允许登录,-1表示无人登录//by awenw
     */
    private static int culogonid=-1;
    protected Activity context;
    protected ImageView ac_change_dlg_finger;
    protected LinearLayout ac_change_dlg_back;

    protected ProgressBar ac_change_dlg_progressbar;

    protected TextView ac_change_dlg_msg;
    protected boolean isChange;
    protected boolean isManager = true;
    protected Class<?> toCls;

    protected void initRes(Activity context) {

        this.context = context;
        setContentView(R.layout.ac_change_dlg);
        ac_change_dlg_finger = (ImageView) findViewById(R.id.ac_change_dlg_finger);
        ac_change_dlg_back = (LinearLayout) findViewById(R.id.ac_change_dlg_back);
        ac_change_dlg_msg = (TextView) findViewById(R.id.ac_change_dlg_msg);
        ac_change_dlg_progressbar = (ProgressBar) findViewById(R.id.ac_change_dlg_progressbar);
        ac_change_dlg_back.setOnClickListener(this);
        ac_change_dlg_finger.setOnClickListener(this);
        ac_change_dlg_progressbar.setVisibility(View.GONE);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        FingerMG.getOne().OnIdentifyBtn(ac_change_dlg_finger,
                ac_change_dlg_msg, this);
        //验证指纹
        ac_change_dlg_msg.setText("请管理员按下手指");
        culogonid=-1;
    }

    protected void facklogin() {
        Intent intent = new Intent(LoginDialog.this.context, toCls);
        LoginDialog.this.context.startActivity(intent);
    }

    public LoginDialog(Activity context) {
        super(context, R.style.dialog);
        initRes(context);
        isChange = true;
    }

    /**
     * 管理员交接班
     *
     * @param context
     *              上下文
     * @param info
     *            管理员信息
     */
    public void getMemberHandover(final Context context,final MenberInfo info) {

        RequestMemberHandover request = new RequestMemberHandover();
        request.Last_Manager_ID = DataCache.getOne().sMenberInfo.User_ID;
        //request.Last_Password = Last_Password;
        request.Manager_ID = info.User_ID;
        //request.Password = Password;
        request.Room_ID =  MainConfig.Room_ID;
        request.Terminal_ID =  MainConfig.Terminal_ID;

        ApiClient.put(context, "api/member/handover/android", request,
                new TextHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        // TLog.showToast(context, "换班失败", responseString);
//                        ac_change_dlg_finger.setEnabled(true);
//                        ac_change_dlg_progressbar.setVisibility(View.INVISIBLE);
                        ac_change_dlg_msg.setText("换班失败！请重试！");
                    }

                    public void onSuccess(int statusCode, Header[] headers,
                                          String responseString) {

                        TLog.v("getMemberHandover", responseString);
                        JSONObject json;
                        try {
                            json = new JSONObject(responseString);
                            String Code = json.getString("Re_Code");
                            String Msg = json.getString("Re_Message");
                            String Token = json.getString("Token");
                            String Body = json.getString("Body");
                            if (Code.equals("0")) {
                                DataCache.getOne().sMenberInfo = info;
                                dismiss();
                            } else {
                                ac_change_dlg_msg.setText("换班失败！请重试！");
                            }
                        } catch (Exception e) {
                            ac_change_dlg_msg.setText("换班失败！请重试！");
                        }
                    }
                });
    }
    public LoginDialog(Activity context, Class<?> cls) {
        super(context, R.style.dialog);
        // TODO Auto-generated constructor stub
        this.toCls = cls;
        initRes(context);
        isChange = false;
    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub

        switch (view.getId()) {
            case R.id.ac_change_dlg_back:
                dismiss();
                break;
            case R.id.ac_change_dlg_finger:
                break;
            default:
                break;
        }
    }

    @Override
    public void dismiss() {
        // TODO Auto-generated method stub
        FingerMG.getOne().OnCancelBtn();
//        FingerPrintMG.getInstance().onCancel(true);
        super.dismiss();
    }

    @Override
    public void didVerify(int fingerid, boolean success) {
        //如果触发指纹功能,初始化触点信息,用于检查触点变化
        Log.i("-----------","finger id: "+fingerid+" result: "+success);
        int id = fingerid/10;
        Log.i("-----------","id: "+id+" result: "+success);
        if (success) { //指纹比对成功
            TaskMG.RememberStatus();//awenw
            if (isManager) { //枪弹管理员
                Log.i("-----------","is Manager");
                String user_id = null;
                try {
                    user_id = DataCache.getOne().sMenberInfo.User_ID;
                    Log.i("LoginDialog","user_id: "+user_id);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("LoginDialog","user_id is null!!!");
                    ToastUtil.showShort("user_id不存在");
                    return;
                }
                if (user_id.equals(fingerid + "")) {
                    isManager = false;
                    culogonid=id;
                    if(ac_change_dlg_msg==null){
                        Log.i("LoginDialog","ac_change_dlg_msg is NULL");
                    }else {
                        ac_change_dlg_msg.setText("验证成功,请警员按下手指");
                    }
                    Log.i("LoginDialog","验证成功,请警员按下手指");
                    ToastUtil.showShort("验证成功,请警员按下手指");
                    // FingerMG.getOne().OnIdentifyBtn();
                } else {
                    if(ac_change_dlg_msg==null){
                        Log.i("LoginDialog","ac_change_dlg_msg is NULL");
                    }else {
                        ac_change_dlg_msg.setText("验证失败，请管理员重试");
                    }
                    Log.i("LoginDialog","验证失败，请管理员重试!");
                    ToastUtil.showShort("验证失败，请管理员重试!");
                    // FingerMG.getOne().OnIdentifyBtn();
                }
            } else {
                if (!isChange) { //不是管理员交接班
                    Log.i("-----------","! isChange");
                    if (culogonid != id && DataFunc.findPolice(fingerid + "") != null) {
                        FingerMG.getOne().OnCancelBtn();
                        ac_change_dlg_msg.setText("");
                        ac_change_dlg_msg.setText("验证成功,正在进入");
                        Intent intent = new Intent(LoginDialog.this.context, toCls);
                        LoginDialog.this.context.startActivity(intent);
                        LoginDialog.this.dismiss();
                    } else {
                        ac_change_dlg_msg.setText("");
                        ac_change_dlg_msg.setText("验证失败，请警员重试");
                        // FingerMG.getOne().OnIdentifyBtn();
                    }
                }else { //管理员交接班
                    if (culogonid != id && DataFunc.changeManager(id + "") != null) {
                        FingerMG.getOne().OnCancelBtn();
                        ac_change_dlg_msg.setText("");
                        ac_change_dlg_msg.setText("验证成功,正在进入");
                        getMemberHandover(context,DataFunc.changeManager(id + ""));
                    } else {
                        ac_change_dlg_msg.setText("");
                        ac_change_dlg_msg.setText("验证失败，请重试");
                        // FingerMG.getOne().OnIdentifyBtn();
                    }
                }
            }
        } else {
            ac_change_dlg_msg.setText("验证失败，请重试");
            // FingerMG.getOne().OnIdentifyBtn();
        }
    }

}
