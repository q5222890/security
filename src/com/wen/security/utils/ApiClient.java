package com.wen.security.utils;

import android.content.Context;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.wen.security.beans.DepartmentInfo;
import com.wen.security.beans.GunCabInfo;
import com.wen.security.beans.MenberInfo;
import com.wen.security.beans.MenberRecordInfo;
import com.wen.security.beans.OperAmmunitionBoxInfo;
import com.wen.security.beans.OperAmmunitionInfo;
import com.wen.security.beans.OperGunInfo;
import com.wen.security.beans.StoreObjectInfo;
import com.wen.security.beans.SubCabInfo;
import com.wen.security.beans.VerifyRecordInfo;
import com.wen.security.beans.request.RequestApplyGunCab;
import com.wen.security.beans.request.RequestBase;
import com.wen.security.beans.request.RequestDeleteObject;
import com.wen.security.beans.request.RequestMemberLock;
import com.wen.security.beans.request.RequestPoliceOperAmmu;
import com.wen.security.beans.request.RequestPoliceOperBox;
import com.wen.security.beans.request.RequestPoliceOperGuns;
import com.wen.security.beans.request.RequestPoliceTaskInfo;
import com.wen.security.beans.request.RequestVerifyRecord;
import com.wen.security.http.AsyncHttpClient;
import com.wen.security.http.AsyncHttpResponseHandler;
import com.wen.security.http.ResponseHandlerInterface;
import com.wen.security.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.ByteArrayEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ApiClient {

    // 测试环境
    // public static final String FILE_URL = "http://192.168.1.211:8091";

    // private static final String BASE_URL = "http://10.201.7.116:8081/Api/";

    // 正式环境
    public static final String BASE_URL = MainConfig.BASE_URL;

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void post(Context context, String url,
                            RequestBase requestBase, SimplePropertyPreFilter filter,
                            AsyncHttpResponseHandler responseHandler) {

        String jsonContent = JSON.toJSONString(requestBase, filter);
        try {
            //TLog.v(url, jsonContent);
            ByteArrayEntity Entity = new ByteArrayEntity(jsonContent.getBytes());
            client.post(context,BASE_URL + url, Entity, "application/json;charset=UTF-8",
                    responseHandler);
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    public static void put(Context context, String url,
                           RequestBase requestBase, AsyncHttpResponseHandler responseHandler) {

        String jsonContent = JSON.toJSONString(requestBase);
        try {
            TLog.v(url, jsonContent);

            ByteArrayEntity Entity = new ByteArrayEntity(jsonContent.getBytes());
            client.put(context,BASE_URL+ url, Entity, "application/json;charset=UTF-8",
                    responseHandler);
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    public static void delete(Context context, String url,
                              RequestBase requestBase, ResponseHandlerInterface responseHandler) {

        String jsonContent = JSON.toJSONString(requestBase);
        try {
            TLog.v(url, jsonContent);

            ByteArrayEntity Entity = new ByteArrayEntity(jsonContent.getBytes());

            client.delete(context,BASE_URL + url, Entity,
                    "application/json;charset=UTF-8", responseHandler);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public static void post(Context context, String url,
                            RequestBase requestBase, AsyncHttpResponseHandler responseHandler) {
        post(context, url, requestBase, null, responseHandler);
    }

    public static void get(Context context, String url,
                           ResponseHandlerInterface responseHandler) {
        client.get(BASE_URL + url, responseHandler);
    }








    /**
     * 管理员系统锁定与解锁退出
     *
     * @param context
     * @param Police_Number
     * @param Password
     * @param Lock_Status
     *            用户所需操作的方式：1、系统锁定，2、解除锁定，3、异常报警
     */
    public static void memberLock(final Context context, String Police_Number,
                                  String Password, int Lock_Status, String Room_ID,String Terminal_ID) {
        RequestMemberLock request = new RequestMemberLock();
        request.User_ID = Police_Number;
        request.Password = Password;
        request.Lock_Status = Lock_Status;
        request.Room_ID = Room_ID;
        request.Terminal_ID = Terminal_ID;
        ApiClient.put(context, BASE_URL + "api/member/lock/android", request,
                new TextHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        Toast.makeText(context, responseString,
                                Toast.LENGTH_SHORT).show();

                    }

                    public void onSuccess(int statusCode, Header[] headers,
                                          String responseString) {

                        TLog.v("memberLock", responseString);
                        JSONObject json;
                        try {
                            json = new JSONObject(responseString);
                            String Code = json.getString("Re_Code");
                            String Msg = json.getString("Re_Message");

                            if (Code.equals("0")) {

                            } else {
                                Toast.makeText(context, Msg, Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(context, e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }





    /**
     * 获取校对服务器标准时间
     *
     * @param context
     * @param Terminal_ID
     *            终端ID
     */
    public static void getTime(final Context context, String Terminal_ID) {

        ApiClient.get(context, BASE_URL + "api/time/" + Terminal_ID,
                new TextHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        Toast.makeText(context, responseString,
                                Toast.LENGTH_SHORT).show();
                    }

                    public void onSuccess(int statusCode, Header[] headers,
                                          String responseString) {

                        TLog.v("getTime", responseString);
                        JSONObject json;
                        try {
                            json = new JSONObject(responseString);
                            String Code = json.getString("Re_Code");
                            String Msg = json.getString("Re_Message");

                            if (Code.equals("0")) {
                                String Currrnt_Time = json
                                        .getString("Currrnt_Time");
                            } else {
                                Toast.makeText(context, Msg, Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }







    /**
     * 控制终端申请添加枪柜
     *
     * @param context
     * @param Terminal_ID
     * @param Body
     *            枪柜对象数组（GunCabInfo）
     */
    public static void sendApplyGunCab(final Context context,
                                       String Terminal_ID, List<GunCabInfo> Body) {
        RequestApplyGunCab request = new RequestApplyGunCab();
        request.Body = Body;
        ApiClient.post(context, BASE_URL + "api/Terminal/ApplyGunCab/"
                + Terminal_ID, request, null, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                Toast.makeText(context, responseString, Toast.LENGTH_SHORT)
                        .show();
            }

            public void onSuccess(int statusCode, Header[] headers,
                                  String responseString) {

                TLog.v("getPoliceTaskInfo", responseString);
                JSONObject json;
                try {
                    json = new JSONObject(responseString);
                    String Code = json.getString("Re_Code");
                    String Msg = json.getString("Re_Message");
                    String Body = json.getString("Body");
                    if (Code.equals("0")) {
                        RequestPoliceTaskInfo info = JSON.parseObject(Body,
                                RequestPoliceTaskInfo.class);
                    } else {
                        Toast.makeText(context, Msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    /**
     * 获取终端控制枪柜列表
     *
     * @param context
     * @param Terminal_ID
     */
    public static void getGunCabList(final Context context, String Terminal_ID) {

        ApiClient.get(context, BASE_URL + "api/Terminal/GetGunCabList/"
                + Terminal_ID, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                Toast.makeText(context, responseString, Toast.LENGTH_SHORT)
                        .show();
            }

            public void onSuccess(int statusCode, Header[] headers,
                                  String responseString) {

                TLog.v("getGunCabList", responseString);
                JSONObject json;
                try {
                    json = new JSONObject(responseString);
                    String Code = json.getString("Re_Code");
                    String Msg = json.getString("Re_Message");
                    String Body = json.getString("Body");
                    if (Code.equals("0")) {
                        ArrayList<GunCabInfo> list = JSON.parseObject(Body,
                                new TypeReference<ArrayList<GunCabInfo>>() {});
                    } else {
                        Toast.makeText(context, Msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    /**
     * 获取枪柜子柜列表
     *
     * @param context
     * @param Terminal_ID
     *            控制终端ID
     * @param Cab_ID
     *            枪柜ID
     */
    public static void getSubCabList(final Context context, String Terminal_ID,
                                     String Cab_ID) {

        ApiClient.get(context, BASE_URL + "/api/Terminal/GetSubCabList/"
                + Terminal_ID + "/" + Cab_ID, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                Toast.makeText(context, responseString, Toast.LENGTH_SHORT)
                        .show();
            }

            public void onSuccess(int statusCode, Header[] headers,
                                  String responseString) {

                TLog.v("getSubCabList", responseString);
                JSONObject json;
                try {
                    json = new JSONObject(responseString);
                    String Code = json.getString("Re_Code");
                    String Msg = json.getString("Re_Message");
                    String Body = json.getString("Body");
                    if (Code.equals("0")) {
                        ArrayList<SubCabInfo> list = JSON.parseObject(Body,
                                new TypeReference<ArrayList<SubCabInfo>>() {});
                    } else {
                        Toast.makeText(context, Msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    /**
     * 获取子柜物件列表
     *
     * @param context
     * @param Terminal_ID
     *            控制终端ID
     * @param Cab_ID
     *            枪柜ID
     * @param Sub_Cab_ID
     *            枪柜子柜ID
     */
    public static void getObjectList(final Context context, String Terminal_ID,
                                     String Cab_ID, String Sub_Cab_ID) {

        ApiClient.get(context, BASE_URL + "/api/Terminal/GetObjectList/"
                        + Terminal_ID + "/" + Cab_ID + "/" + Sub_Cab_ID,
                new TextHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        Toast.makeText(context, responseString,
                                Toast.LENGTH_SHORT).show();
                    }

                    public void onSuccess(int statusCode, Header[] headers,
                                          String responseString) {

                        TLog.v("getSubCabList", responseString);
                        JSONObject json;
                        try {
                            json = new JSONObject(responseString);
                            String Code = json.getString("Re_Code");
                            String Msg = json.getString("Re_Message");
                            String Body = json.getString("Body");
                            if (Code.equals("0")) {
                                ArrayList<StoreObjectInfo> list = JSON
                                        .parseObject(
                                                Body,
                                                new TypeReference<ArrayList<StoreObjectInfo>>() {});
                            } else {
                                Toast.makeText(context, Msg, Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * 删除子柜物件申请
     *
     * @param context
     * @param Terminal_ID
     *            控制终端ID
     * @param Cab_ID
     *            枪柜ID
     * @param Sub_Cab_ID
     *            枪柜子柜ID
     */
    public static void sendDeleteObject(final Context context,
                                        String Terminal_ID, String Cab_ID, String Sub_Cab_ID,
                                        List<String> Object_IDs) {

        RequestDeleteObject request = new RequestDeleteObject();
        ApiClient.delete(context, BASE_URL + "/api/Terminal/DeleteObject/"
                        + Terminal_ID + "/" + Cab_ID + "/" + Sub_Cab_ID, request,
                new TextHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        Toast.makeText(context, responseString,
                                Toast.LENGTH_SHORT).show();
                    }

                    public void onSuccess(int statusCode, Header[] headers,
                                          String responseString) {

                        TLog.v("getSubCabList", responseString);
                        JSONObject json;
                        try {
                            json = new JSONObject(responseString);
                            String Code = json.getString("Re_Code");
                            String Msg = json.getString("Re_Message");

                            if (Code.equals("0")) {

                            } else {
                                Toast.makeText(context, Msg, Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    /**
     * 出警申请任务提交
     * @param context
     * @param User_ID
     * @param isBack
     * @param info
     */
    public void sendPoliceTaskInfo(final Context context, String User_ID,
                                   int isBack, RequestPoliceTaskInfo info) {


        ApiClient.post(context, "api/PoliceTask/Apply/" + User_ID, info, null,
                new TextHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        Toast.makeText(context, "提交失败", Toast.LENGTH_SHORT)
                                .show();
                    }

                    public void onSuccess(int statusCode, Header[] headers,
                                          String responseString) {

                        TLog.v("sendPoliceTaskInfo", responseString);
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
    /**
     * 警员申请领还枪
     *
     * @param context
     * @param Task_ID
     *            警员出警任务ID，紧急出警ID为NULL
     * @param Oper_Guns
     *            以JSON格式序列化的li领还枪对象数组（StoerObjectInfo）
     */
    public static void sendPoliceOperGuns(final Context context,
                                          String Task_ID, List<OperGunInfo> Oper_Guns) {

        RequestPoliceOperGuns request = new RequestPoliceOperGuns();
        request.Oper_Guns = Oper_Guns;
        ApiClient.post(context, BASE_URL + "/api/PoliceTask/PoliceOperGuns/"
                + Task_ID, request, null, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                Toast.makeText(context, responseString, Toast.LENGTH_SHORT)
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

                    } else {
                        Toast.makeText(context, Msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    /**
     * 警员申请领还弹
     *
     * @param context
     * @param Task_ID
     *            警员出警任务ID，紧急出警ID为NULL
     * @param Oper_Ammu
     *            以JSON格式序列化的领还弹对象数组（OperAmmunitionInfo）
     */
    public static void sendPoliceOperAmmu(final Context context,
                                          String Task_ID, List<OperAmmunitionInfo> Oper_Ammu) {
        RequestPoliceOperAmmu request = new RequestPoliceOperAmmu();
        request.Oper_Ammu = Oper_Ammu;
        ApiClient.post(context, BASE_URL + "/api/PoliceTask/PoliceOperAmmu/"
                + Task_ID, request, null, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                Toast.makeText(context, responseString, Toast.LENGTH_SHORT)
                        .show();
            }

            public void onSuccess(int statusCode, Header[] headers,
                                  String responseString) {

                TLog.v("sendPoliceOperAmmu", responseString);
                JSONObject json;
                try {
                    json = new JSONObject(responseString);
                    String Code = json.getString("Re_Code");
                    String Msg = json.getString("Re_Message");

                    if (Code.equals("0")) {

                    } else {
                        Toast.makeText(context, Msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    /**
     * 警员申请领还弹夹
     *
     * @param context
     * @param Task_ID
     *            警员出警任务ID，紧急出警ID为NULL
     * @param Oper_Box
     *            以JSON格式序列化的领还弹对象数组（OperAmmunitionBoxInfo
     */
    public static void sendPoliceOperBox(final Context context, String Task_ID,
                                         List<OperAmmunitionBoxInfo> Oper_Box) {
        RequestPoliceOperBox request = new RequestPoliceOperBox();
        request.Oper_Box = Oper_Box;
        ApiClient.post(context, BASE_URL + "/api/PoliceTask/PoliceOperBox/"
                + Task_ID, request, null, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                Toast.makeText(context, responseString, Toast.LENGTH_SHORT)
                        .show();
            }

            public void onSuccess(int statusCode, Header[] headers,
                                  String responseString) {

                TLog.v("sendPoliceOperBox", responseString);
                JSONObject json;
                try {
                    json = new JSONObject(responseString);
                    String Code = json.getString("Re_Code");
                    String Msg = json.getString("Re_Message");

                    if (Code.equals("0")) {

                    } else {
                        Toast.makeText(context, Msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }



    /**
     * 提交用户验证数据（指纹或虹膜）信息
     *
     * @param context
     * @param Terminal_ID
     *            终端ID
     * @param Verify_Records
     *            以JSON格式序列化的系统信息数组VerifyRecordInfo
     */
    public static void sendVerifyRecord(final Context context,
                                        String Terminal_ID, List<VerifyRecordInfo> Verify_Records) {
        RequestVerifyRecord request = new RequestVerifyRecord();
        request.Verify_Records = Verify_Records;
        ApiClient.post(context, BASE_URL + "/api/verifyrecord/submit/"
                + Terminal_ID, request, null, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                Toast.makeText(context, responseString, Toast.LENGTH_SHORT)
                        .show();
            }

            public void onSuccess(int statusCode, Header[] headers,
                                  String responseString) {

                TLog.v("sendVerifyRecord", responseString);
                JSONObject json;
                try {
                    json = new JSONObject(responseString);
                    String Code = json.getString("Re_Code");
                    String Msg = json.getString("Re_Message");

                    if (Code.equals("0")) {

                    } else {
                        Toast.makeText(context, Msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    /**
     * 根据用户ID获取指定用户信息
     *
     * @param context
     * @param UserID
     * @param Terminal_ID
     */
    public static void getMemberInfo(final Context context, String UserID,
                                     String Terminal_ID) {

        ApiClient.get(context, BASE_URL + "/api/member/id/" + UserID + "/"
                + Terminal_ID, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                Toast.makeText(context, responseString, Toast.LENGTH_SHORT)
                        .show();
            }

            public void onSuccess(int statusCode, Header[] headers,
                                  String responseString) {

                TLog.v("getMemberInfo", responseString);
                JSONObject json;
                try {
                    json = new JSONObject(responseString);
                    String Code = json.getString("Re_Code");
                    String Msg = json.getString("Re_Message");
                    String Body = json.getString("Body");
                    if (Code.equals("0")) {
                        MenberRecordInfo info = JSON.parseObject(Body,
                                MenberRecordInfo.class);
                    } else {
                        Toast.makeText(context, Msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    /**
     * 根据警员警号获取指定警员信息
     *
     * @param context
     * @param Police_Number
     * @param Terminal_ID
     */
    public static void getMemberNumber(final Context context,
                                       String Police_Number, String Terminal_ID) {

        ApiClient.get(context, BASE_URL + "/api/member/number/" + Police_Number
                + "/" + Terminal_ID, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                Toast.makeText(context, responseString, Toast.LENGTH_SHORT)
                        .show();
            }

            public void onSuccess(int statusCode, Header[] headers,
                                  String responseString) {

                TLog.v("getMemberNumber", responseString);
                JSONObject json;
                try {
                    json = new JSONObject(responseString);
                    String Code = json.getString("Re_Code");
                    String Msg = json.getString("Re_Message");
                    String Body = json.getString("Body");
                    if (Code.equals("0")) {
                        MenberRecordInfo info = JSON.parseObject(Body,
                                MenberRecordInfo.class);
                    } else {
                        Toast.makeText(context, Msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    /**
     * 根据部门编号(改成GUID)获取部门下所有警员信息子集
     * @param context
     * @param Department_ID  部门ID
     */
    public static void getMemberNumberList(final Context context,
                                           String Department_ID) {

        ApiClient.get(context, BASE_URL + "/api/member/department/"
                + Department_ID, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                Toast.makeText(context, responseString, Toast.LENGTH_SHORT)
                        .show();
            }

            public void onSuccess(int statusCode, Header[] headers,
                                  String responseString) {

                TLog.v("getMemberNumberList", responseString);
                JSONObject json;
                try {
                    json = new JSONObject(responseString);
                    String Code = json.getString("Re_Code");
                    String Msg = json.getString("Re_Message");
                    String Body = json.getString("Body");
                    if (Code.equals("0")) {
                        ArrayList<MenberInfo> list = JSON.parseObject(Body,
                                new TypeReference<ArrayList<MenberInfo>>() {});
                    } else {
                        Toast.makeText(context, Msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    /**
     * 获取部门下所有子部门列表
     * @param context
     * @param Department_ID 部门 ID
     */
    public static void getDepartmentList(final Context context,
                                         String Department_ID) {

        ApiClient.get(context, BASE_URL + "/api/department/child/"
                + Department_ID, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                Toast.makeText(context, responseString, Toast.LENGTH_SHORT)
                        .show();
            }

            public void onSuccess(int statusCode, Header[] headers,
                                  String responseString) {

                TLog.v("getMemberNumberList", responseString);
                JSONObject json;
                try {
                    json = new JSONObject(responseString);
                    String Code = json.getString("Re_Code");
                    String Msg = json.getString("Re_Message");
                    String Body = json.getString("Body");
                    if (Code.equals("0")) {
                        ArrayList<DepartmentInfo> list = JSON.parseObject(Body,
                                new TypeReference<ArrayList<DepartmentInfo>>() {});
                    } else {
                        Toast.makeText(context, Msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }
}
