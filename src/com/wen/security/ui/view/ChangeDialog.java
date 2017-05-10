package com.wen.security.ui.view;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.wen.security.DataCache;
import com.wen.security.R;
import com.wen.security.SecurityApplication;
import com.wen.security.beans.MenberInfo;
import com.wen.security.beans.MenberRecordInfo;
import com.wen.security.beans.VerifyRecordInfo;
import com.wen.security.beans.request.RequestMemberHandover;
import com.wen.security.beans.request.RequestMemberLogin;
import com.wen.security.finger.FingerMG;
import com.wen.security.http.TextHttpResponseHandler;
import com.wen.security.utils.ApiClient;
import com.wen.security.utils.FileCache;
import com.wen.security.utils.MainConfig;
import com.wen.security.utils.TLog;

/**
 * 枪管员交接
 *
 * @author Administrator
 *
 */
public class ChangeDialog extends Dialog implements
        android.view.View.OnClickListener {
    private static final int ERROR = 1;
    private static final int SUCCESS = 2;
    private Activity context;
    private ImageView ac_change_dlg_finger;
    private LinearLayout ac_change_dlg_back;

    private ProgressBar ac_change_dlg_progressbar;

    private TextView ac_change_dlg_msg;
    private boolean isChange;
    private boolean isLoaded = false;
    private Class<?> toCls;
    protected FileCache fileCache;

    private MenberRecordInfo manager;
    private MenberRecordInfo police;

    private boolean isManager = true;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ERROR:
                    ac_change_dlg_finger.setEnabled(true);
                    ac_change_dlg_progressbar.setVisibility(View.INVISIBLE);
                    ac_change_dlg_msg.setText("验证指纹失败！请重试！");
                    break;
                case SUCCESS:

                    // 验证跳转到相应模块
                    MenberRecordInfo info = (MenberRecordInfo) msg.obj;

                    if (info != null) {
                        if (isManager) {
                            manager = info;
                            isManager = false;
                            ac_change_dlg_finger.setEnabled(true);
                            ac_change_dlg_progressbar.setVisibility(View.INVISIBLE);

                            if (isChange) {
                                ac_change_dlg_msg.setText("请接班管理员按下指纹");
                            } else {
                                ac_change_dlg_msg.setText("请警员按下指纹");
                            }

                        } else {
                            if (isChange) {
                                ac_change_dlg_msg.setText("验证指纹成功！正在交接班……");
                                getMemberHandover(context, manager.User_ID,
                                        manager.Record_Datas.get(0).Data,
                                        info.User_ID,
                                        info.Record_Datas.get(0).Data,
                                        MainConfig.Room_ID, MainConfig.Terminal_ID);
                            } else {
                                ac_change_dlg_msg.setText("验证指纹成功！正在进入……");
                                memberLogin(context, MainConfig.Room_ID,
                                        MainConfig.Terminal_ID, info.User_ID,
                                        info.Record_Datas.get(0).Data);
                            }
                        }
                    } else {
                        ac_change_dlg_msg.setText("验证指纹失败！请关闭重试！");
                        ac_change_dlg_finger.setEnabled(true);
                        ac_change_dlg_progressbar.setVisibility(View.INVISIBLE);
                    }

                    break;
            }
        }
    };

    private void initRes(Activity context) {
        fileCache = new FileCache(context);
        this.context = context;

        setContentView(R.layout.ac_change_dlg);
        ac_change_dlg_finger = (ImageView) findViewById(R.id.ac_change_dlg_finger);
        ac_change_dlg_back = (LinearLayout) findViewById(R.id.ac_change_dlg_back);
        ac_change_dlg_msg = (TextView) findViewById(R.id.ac_change_dlg_msg);
        ac_change_dlg_progressbar = (ProgressBar) findViewById(R.id.ac_change_dlg_progressbar);
        ac_change_dlg_back.setOnClickListener(this);
        ac_change_dlg_finger.setOnClickListener(this);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        //FingerMG.getOne().init(context,ac_change_dlg_finger,ac_change_dlg_msg);
    }

    public ChangeDialog(Activity context) {
        super(context, R.style.dialog);

        // TODO Auto-generated constructor stub
        initRes(context);
        isChange = true;
    }

    public ChangeDialog(Activity context, Class<?> cls) {
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
                isrun = false;
                dismiss();
                break;
            case R.id.ac_change_dlg_finger:
                ac_change_dlg_finger.setEnabled(false);
                ac_change_dlg_progressbar.setVisibility(View.VISIBLE);
                ac_change_dlg_msg.setText("正在验证指纹……");
                //loadDate();
                //getFinger();
                //  FingerMG.getOne().OnVerifyBtn(ac_change_dlg_finger, ac_change_dlg_msg);
                break;
            default:
                break;
        }
    }

    @Override
    public void dismiss() {
        // TODO Auto-generated method stub
        FingerMG.getOne().OnCloseDeviceBtn();
        super.dismiss();
    }

    private boolean isrun;

    private void getFinger() {
        // 获取指纹
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                isrun = true;
                while (isrun) {
                    try {
                        // Thread.sleep(1000);
                        // 取得指纹，验证
                        if (isLoaded == true) {
                            VerifyRecordInfo info = new VerifyRecordInfo();
                            info.Data = "123456";
                            Verification(info);
                            isrun = false;
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    // 获取服务器或本地数据
    private void loadDate() {

        /*if (DataCache.getOne().sMenber_Datas == null) {
            getMenberInfos(context, MainConfig.current_Ver);
        } else {
            isLoaded = true;
        }*/

    }

    /**
     * 验证
     *
     * @return
     */
    private void Verification(VerifyRecordInfo info) {
        /*
         * if (DataCache.getOne().sMenber_Datas != null) { for (int i = 0; i <
         * DataCache.getOne().sMenber_Datas.size(); i++) { MenberRecordInfo temp
         * = DataCache.getOne().sMenber_Datas.get(i); if (temp.Record_Datas !=
         * null) { for (int j = 0; j < temp.Record_Datas.size(); j++) {
         * VerifyRecordInfo vtemp = temp.Record_Datas.get(j); if
         * (vtemp.Data.equals(info.Data)) { Message msg =
         * mHandler.obtainMessage(); msg.obj = temp; msg.what = SUCCESS;
         * mHandler.sendMessage(msg); return; } } } } }
         * mHandler.sendEmptyMessage(ERROR); return;
         */
    }

    /**
     * 3、终端用户验证（根据在本地的指纹记录验证指纹的合法性，并通过指纹信息取出用户的相应信息进行服务器登陆）
     *
     * @param context
     * @param Police_Number
     *            登陆警员编号
     * @param Password
     */
    public void memberLogin(final Context context, String Room_ID,
                            String Terminal_ID, String User_ID, String Password) {

        RequestMemberLogin request = new RequestMemberLogin();
        request.Room_ID = Room_ID;
        request.Terminal_ID = Terminal_ID;
        request.User_ID = User_ID;
        request.Password = Password;

        ApiClient.post(context, "api/member/login/android", request,
                new TextHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        String json = fileCache.readJsonFile("sPMenberInfo");
                        if (!json.equals("")) {
                            DataCache.getOne().sPMenberInfo = JSON.parseObject(
                                    json, MenberInfo.class);
                            isLoaded = true;
                        } else {
                            Toast.makeText(context, responseString,
                                    Toast.LENGTH_SHORT).show();
                            ac_change_dlg_finger.setEnabled(true);
                            ac_change_dlg_progressbar
                                    .setVisibility(View.INVISIBLE);
                            ac_change_dlg_msg.setText("登录失败！请重试！");
                        }

                    }

                    public void onSuccess(int statusCode, Header[] headers,
                                          String responseString) {

                        TLog.v("memberLogin", responseString);
                        JSONObject json;
                        try {
                            json = new JSONObject(responseString);
                            String Code = json.getString("Re_Code");
                            String Msg = json.getString("Re_Message");
                            String token = json.getString("Token");
                            String Body = json.getString("Body");
                            if (Code.equals("0")) {
                                fileCache.writeJsonFile("sPMenberInfo", Body);
                                DataCache.getOne().sPMenberInfo = JSON
                                        .parseObject(Body, MenberInfo.class);

                                Intent intent = new Intent(context, toCls);
                                context.startActivity(intent);
                                dismiss();
                            } else {
                                TLog.showToast(context, "登录失败！请重试！", Msg);
                                ac_change_dlg_finger.setEnabled(true);
                                ac_change_dlg_progressbar
                                        .setVisibility(View.INVISIBLE);
                                ac_change_dlg_msg.setText("");
                                isManager = true;
                            }
                        } catch (JSONException e) {

                            TLog.showToast(context, "登录失败", e.getMessage());
                        }
                    }
                });
    }

    /**
     * 管理员交接班
     *
     * @param context
     * @param Last_Police_Number
     *            下班管理员警号
     * @param Last_Password
     *            下班管理员密码
     * @param Manager_Police_Number
     *            接班管理员警号
     * @param Password
     *            接班管理员密码
     * @param Room_ID
     *            监控室ID
     */
    public void getMemberHandover(final Context context,
                                  String Last_Manager_ID, String Last_Password, String Manager_ID,
                                  String Password, String Room_ID, String Terminal_ID) {

        RequestMemberHandover request = new RequestMemberHandover();
        request.Last_Manager_ID = Last_Manager_ID;
        request.Last_Password = Last_Password;
        request.Manager_ID = Manager_ID;
        request.Password = Password;
        request.Room_ID = Room_ID;
        request.Terminal_ID = Terminal_ID;

        ApiClient.put(context, "api/member/handover/android", request,
                new TextHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        // TLog.showToast(context, "换班失败", responseString);
                        ac_change_dlg_finger.setEnabled(true);
                        ac_change_dlg_progressbar.setVisibility(View.INVISIBLE);
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
                                fileCache.writeJsonFile("getMemberCurrent",
                                        Body);
                                DataCache.getOne().sMenberInfo = JSON
                                        .parseObject(Body, MenberInfo.class);
                                TLog.showToast(context, "换班成功", null);
                                dismiss();
                            } else {
                                TLog.showToast(context, "换班成功", Msg);
                                ac_change_dlg_finger.setEnabled(true);
                                ac_change_dlg_progressbar
                                        .setVisibility(View.INVISIBLE);
                                ac_change_dlg_msg.setText("换班失败！请重试！");
                                isManager = true;
                            }
                        } catch (Exception e) {
                            TLog.showToast(context, "换班失败", e.getMessage());
                        }

                    }
                });
    }
}
