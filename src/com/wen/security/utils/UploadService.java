package com.wen.security.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.wen.security.beans.SystemMessageInfo;
import com.wen.security.beans.db.SysLogDao;
import com.wen.security.beans.request.RequestSystemMessage;
import com.wen.security.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class UploadService extends Service {

    private static final String TAG = "UploadService";
    Context context = this;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    boolean isrun;

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                isrun = true;
                while (isrun) {

                    try {

                        if (RTools.isNetworkAvailable(context)) { //检查网络
                            // 提交日志信息至服务器
                            sendLogList(context);
                        }

                        Message msg = mHandler.obtainMessage(10001);
                        mHandler.sendMessage(msg);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 10001) {

                //TLog.v("runing");


            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 异步提交系统信息
     *
     * @param context
     */
    public void sendLogListasy(final Context context) {
        SysLogDao dao = new SysLogDao(context);
        ArrayList<SystemMessageInfo> didLogList = dao.queryUploadAll();
        if (didLogList.size() > 0) {
            final long time = didLogList.get(0).Create_Time;
            RequestSystemMessage request = new RequestSystemMessage();
            request.System_Msgs = didLogList;
            ApiClient.post(context,
                    "/api/systemmessage/submit/" + MainConfig.Terminal_ID,
                    request, null, new TextHttpResponseHandler() {

                        @Override
                        public void onFailure(int statusCode, Header[] headers,
                                              String responseString, Throwable throwable) {

                            Log.i(TAG,"onFailure");
                        }

                        public void onSuccess(int statusCode, Header[] headers,
                                              String responseString) {
                            // TLog.v("sendSystemMessage", responseString);
                            JSONObject json;
                            try {
                                json = new JSONObject(responseString);
                                String Code = json.getString("Re_Code");
                                String Msg = json.getString("Re_Message");

                                if (Code.equals("0")) {
                                    SysLogDao dao = new SysLogDao(context);
                                    dao.updateLoad(time);
                                }
                            } catch (Exception e) {

                            }
                        }
                    });
        }

    }

    /**
     *
     * @param context
     */
    @SuppressWarnings("deprecation")
    public static void sendLogList(Context context) {
//        Log.i(TAG,"sendLogList");
        HttpClient client = null;
        try {
            // 定义一个客户端
            client = new DefaultHttpClient();

            // 定义post方法
            HttpPost post = new HttpPost(MainConfig.BASE_URL
                    + "/api/systemmessage/submit/" + MainConfig.Terminal_ID);

            SysLogDao dao = new SysLogDao(context);
            ArrayList<SystemMessageInfo> didLogList = dao.queryUploadAll();
            if (didLogList.size() <= 0) {
                return;
            }
            long time = didLogList.get(0).Create_Time;
            RequestSystemMessage request = new RequestSystemMessage();
            request.System_Msgs = didLogList;
            String jsonContent = JSON.toJSONString(request);
            Log.i("UploadService","jsonContent: "+jsonContent);
            ByteArrayEntity Entity = new ByteArrayEntity(
                    jsonContent.getBytes());

            // 设置参数.
            post.setEntity(Entity);

            // 设置请求头消息
            post.addHeader("Content-Type", "application/json;charset=UTF-8");
            // 使用客户端执行post方法
            HttpResponse response = client.execute(post); // 开始执行post请求,
            // 会返回给我们一个HttpResponse对象

            // 使用响应对象, 获得状态码, 处理内容
            int statusCode = response.getStatusLine().getStatusCode(); // 获得状态码
            if (statusCode == 200) { //请求成功 获得响应
                // 使用响应对象获得实体, 获得输入流
                InputStream is = response.getEntity().getContent();
                String responseString = RTools.getStringFromInputStream(is);
                JSONObject json;
                try {
                    json = new JSONObject(responseString);
                    String Code = json.getString("Re_Code");
                    String Msg = json.getString("Re_Message");

                    Log.i("UploadService","Code: "+Code+";msg: "+Msg);
                    if (Code.equals("0")) {
                        dao.updateLoad(time);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            } else {
                TLog.v("请求失败: " + statusCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.getConnectionManager().shutdown(); // 关闭连接和释放资源
            }
        }
        return;
    }


}
