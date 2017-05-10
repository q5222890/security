package com.wen.security;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wen.security.beans.GunCabInfo;
import com.wen.security.beans.GunLicenceInfo;
import com.wen.security.beans.MenberInfo;
import com.wen.security.beans.MenberRecordInfo;
import com.wen.security.beans.OperAmmunitionBoxInfo;
import com.wen.security.beans.OperAmmunitionInfo;
import com.wen.security.beans.OperGunInfo;
import com.wen.security.beans.StoreObjectInfo;
import com.wen.security.beans.SubCabInfo;
import com.wen.security.beans.SystemMessageInfo;
import com.wen.security.beans.VerifyRecordInfo;
import com.wen.security.beans.request.RequestPoliceTaskInfo;
import com.wen.security.beans.request.RequestSystemMessage;
import com.wen.security.finger.FingerMG;
import com.wen.security.http.TextHttpResponseHandler;
import com.wen.security.utils.ApiClient;
import com.wen.security.utils.FileCache;
import com.wen.security.utils.MainConfig;
import com.wen.security.utils.RTools;
import com.wen.security.utils.TLog;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据中心
 *
 * @author cloud
 *
 */
public class DataCache {
    private static final String TAG = "DataCache";
    public int localVer = 0;
    // 缓存文件名
    public static final String F_MENBERRECORDLIST = "FMENBERRECORDLIST";  //警员信息
    public static final String F_GUNCABINFOLIST = "FGUNCABINFOLIST";       //枪弹柜信息
    public static final String F_REQUESTPOLICETASKLIST = "FREQUESTPOLICETASKLIST";  //请求出警任务表
    public static final String F_MENBERINFO = "FMENBERINFO";   //警员身份信息
    public static final String F_LOGLIST = "FLOGLIST";         //日志表

    private static DataCache instance;
    private FileCache fileCache;
    /**
     * 用户数据集
     */
    public ArrayList<MenberRecordInfo> sMenber_Datas = null;

    /**
     * 保存警员数据
     */
    public void saveMenber_Datas() {
        fileCache.writeJsonFile(F_MENBERRECORDLIST,
                JSON.toJSONString(sMenber_Datas));
    }

    /**
     * 枪柜列表
     */
    public ArrayList<GunCabInfo> gunCabInfolist = null;

    public void saveGunCab() {
        fileCache.writeJsonFile(F_GUNCABINFOLIST,
                JSON.toJSONString(gunCabInfolist));
    }

    /**
     * 出警任务列表
     */
    public ArrayList<RequestPoliceTaskInfo> p_task_list = null;

    public void savep_task_list() {
        fileCache.writeJsonFile(F_REQUESTPOLICETASKLIST,
                JSON.toJSONString(p_task_list));
    }

    /**
     * 出警任务
     */
    public RequestPoliceTaskInfo go_taskinfo = null;

    /**
     * 当前管理员信息
     */
    public MenberInfo sMenberInfo = null;

    public void saveMenberInfo() {
        fileCache.writeJsonFile(F_MENBERINFO, JSON.toJSONString(sMenberInfo));
    }

    public void saveData() {
        saveMenber_Datas();
        saveGunCab();
        saveMenberInfo();
        savep_task_list();
        // saveLogList();
    }

    /**
     * 当前登录的警员信息
     */
    public MenberInfo sPMenberInfo = null;

    private DataCache() {
        sMenber_Datas = new ArrayList<MenberRecordInfo>();
        gunCabInfolist = new ArrayList<GunCabInfo>();
        p_task_list = new ArrayList<RequestPoliceTaskInfo>();
        // LogList = new ArrayList<SystemMessageInfo>();
    }

    public static DataCache getOne() {
        if (instance == null) {
            instance = new DataCache();
        }
        return instance;
    }

    public static void takeGun(OperGunInfo Oper_Guns) {
        ArrayList<GunCabInfo> gunCabInfolist = DataCache
                .getOne().gunCabInfolist;
        if (gunCabInfolist != null) {
            for (int i = 0; i < gunCabInfolist.size(); i++) {
                GunCabInfo gunCabInfo = gunCabInfolist.get(i);
                if (gunCabInfo.Sub_Cabs != null) {
                    for (int j = 0; j < gunCabInfo.Sub_Cabs.size(); j++) {
                        SubCabInfo subCabInfo = gunCabInfo.Sub_Cabs.get(j);
                        if (subCabInfo != null
                                && subCabInfo.Store_Objects != null) {
                            for (int k = 0; k < subCabInfo.Store_Objects
                                    .size(); k++) {
                                StoreObjectInfo storeObjectInfo = subCabInfo.Store_Objects
                                        .get(k);
                                if (storeObjectInfo.Object_ID
                                        .equals(Oper_Guns.Gun_ID)) {
                                    storeObjectInfo.IsOper = true;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static List<OperGunInfo> findGuns() {
        int size;
        List<OperGunInfo> Oper_Guns = new ArrayList<OperGunInfo>();
        ArrayList<GunCabInfo> gunCabInfolist = DataCache
                .getOne().gunCabInfolist;
        if (gunCabInfolist != null) {
            for (int i = 0; i < gunCabInfolist.size(); i++) {
                GunCabInfo gunCabInfo = gunCabInfolist.get(i);
                if (gunCabInfo.Sub_Cabs != null) {
                    for (int j = 0; j < gunCabInfo.Sub_Cabs.size(); j++) {
                        SubCabInfo subCabInfo = gunCabInfo.Sub_Cabs.get(j);
                        if (subCabInfo != null
                                && subCabInfo.Store_Objects != null) {
                            if (getOne().sPMenberInfo.Identity == 2) {
                                size = 1;
                            } else {
                                size = subCabInfo.Store_Objects.size();
                            }
                            for (int k = 0; k < size; k++) {
                                StoreObjectInfo storeObjectInfo = subCabInfo.Store_Objects
                                        .get(k);
                                // if (storeObjectInfo.IsOper == false) {
                                OperGunInfo ggun = new OperGunInfo();
                                if (storeObjectInfo.Object_Type.equals("枪")) {
                                    ggun.Sub_Sequence = storeObjectInfo.Sub_Sequence;
                                    ggun.Own_Sub_ID = storeObjectInfo.Own_Sub_ID;
                                    ggun.Own_Cab_ID = storeObjectInfo.Own_Cab_ID;
                                    ggun.Gun_ID = storeObjectInfo.Object_ID;
                                    ggun.Draw_People = getOne().sPMenberInfo.User_ID;
                                    ggun.PoliceName = getOne().sPMenberInfo.User_Name;

                                } else if (storeObjectInfo.Object_Type
                                        .equals("子弹弹药")) {
                                    ggun.Oper_Ammunition = new OperAmmunitionInfo();
                                    ggun.Oper_Ammunition.Ammunition_ID = storeObjectInfo.Object_ID;
                                    ggun.Oper_Ammunition.Ammo_Nums = storeObjectInfo.Current_size;

                                } else if (storeObjectInfo.Object_Type
                                        .equals("弹夹")) {
                                    ggun.Oper_Box = new OperAmmunitionBoxInfo();
                                    // null
                                }
                                Oper_Guns.add(ggun);
                            }

                            // }

                        }
                    }
                }
            }
        }
        return Oper_Guns;
    }

    /**
     * 执行到的任务
     */
    private long current = 0;
    /**
     * 执行成功的任务
     */
    // private long s_current = 0;
    /**
     * 总任务数
     */
    private long total = 0;
    private IDataCacheStatus iDataCacheStatus;

    public interface IDataCacheStatus {
        void onLoading(long current, long total);

        void didLoading(boolean success);
    }

    Context context;

    public void fackload() {

        MenberRecordInfo info = new MenberRecordInfo();
        info.User_ID = "0";
        info.User_Name = "李小龙";
        info.Police_Number = "0000";
        info.Duty = "反黑组";
        info.Identity = 3;
        info.Department = "香港九龙警局";
        GunLicenceInfo Licence_Info = new GunLicenceInfo();
        Licence_Info.Hold_Type = 1;
        info.Licence_Info = Licence_Info;
        info.Rankid = 6;
        sMenber_Datas.add(info);

        MenberRecordInfo info1 = new MenberRecordInfo();
        info1.User_ID = "1";
        info1.User_Name = "甄子丹";
        info1.Police_Number = "0001";
        info1.Duty = "反黑组";
        info1.Identity = 2;
        info1.Department = "香港九龙警局";
        GunLicenceInfo Licence_Info1 = new GunLicenceInfo();
        Licence_Info1.Hold_Type = 1;
        info1.Licence_Info = Licence_Info1;
        info1.Rankid = 4;
        sMenber_Datas.add(info1);
        sMenberInfo = info1;
        MenberRecordInfo info2 = new MenberRecordInfo();
        info2.User_ID = "2";
        info2.User_Name = "成龙";
        info2.Police_Number = "0002";
        info2.Duty = "反黑组";
        info2.Identity = 2;
        info2.Department = "香港九龙警局";
        GunLicenceInfo Licence_Info2 = new GunLicenceInfo();
        Licence_Info2.Hold_Type = 1;
        info2.Licence_Info = Licence_Info2;
        info2.Rankid = 4;
        sMenber_Datas.add(info2);

        MenberRecordInfo info3 = new MenberRecordInfo();
        info3.User_ID = "3";
        info3.User_Name = "释小龙";
        info3.Police_Number = "0003";
        info3.Duty = "反黑组";
        info3.Identity = 2;
        info3.Department = "香港九龙警局";
        GunLicenceInfo Licence_Info3 = new GunLicenceInfo();
        Licence_Info3.Hold_Type = 1;
        info3.Licence_Info = Licence_Info3;
        info3.Rankid = 2;
        sMenber_Datas.add(info3);

        RequestPoliceTaskInfo task = new RequestPoliceTaskInfo();
        task.is_done = false;
        task.User_ID = "0";
        task.Task_ID = "0";
        task.Apply_Base = 1;
        task.ApprovTime = "2014-11-25";
        task.AllowBeginTime = "2014-11-25";
        task.AllowEndTime = "2014-11-29";
        task.ApprovPoliceName = "周星星";

        task.Remark = "快准狠";
        task.Task_Begin_Time = RTools.getTimeToM();

        List<OperGunInfo> Oper_Guns = new ArrayList<OperGunInfo>();
        for (int i = 0; i < 8; i++) {
            OperGunInfo ggun = new OperGunInfo();
            ggun.Sub_Sequence = i + 1;
            ggun.Own_Sub_ID = "1";
            ggun.Own_Cab_ID = "1";
            ggun.Gun_ID = (i + 1) + "";
            ggun.Draw_People = "0001";
            ggun.GunTypeName = "手枪";
            ggun.PoliceName = "甄子丹";
            ggun.Oper_Ammunition = new OperAmmunitionInfo();
            ggun.Oper_Ammunition.Ammunition_ID = (i + 1) + "";
            ggun.Oper_Ammunition.Ammo_Nums = 9;
            ggun.Oper_Ammunition.Return_Nums = 9;
            Oper_Guns.add(ggun);
        }
        task.Oper_Guns = Oper_Guns;
        p_task_list.add(task);

        RequestPoliceTaskInfo task1 = new RequestPoliceTaskInfo();
        task1.User_ID = "3";
        task1.Task_ID = "3";
        task1.Apply_Base = 4;
        task1.ApprovTime = "2014-11-25";
        task1.AllowBeginTime = "2014-11-25";
        task1.AllowEndTime = "2014-11-30";
        task1.ApprovPoliceName = "李连杰";

        task1.Remark = "快准狠";
        task1.Task_Begin_Time = RTools.getTimeToM();

        List<OperGunInfo> Oper_Guns1 = new ArrayList<OperGunInfo>();
        Oper_Guns1.addAll(Oper_Guns);
        task1.Oper_Guns = Oper_Guns1;
        p_task_list.add(task1);

        gunCabInfolist = new ArrayList<GunCabInfo>();
        // 1柜子
        GunCabInfo g = new GunCabInfo();
        g.Cab_ID = "1";
        // 1子柜
        List<SubCabInfo> sg = new ArrayList<SubCabInfo>();
        SubCabInfo sc = new SubCabInfo();
        sc.Sub_Cab_ID = "1";

        // 8把枪
        List<StoreObjectInfo> ol = new ArrayList<StoreObjectInfo>();
        for (int i = 0; i < 8; i++) {
            StoreObjectInfo o = new StoreObjectInfo();
            o.Object_ID = (i + 1) + "";
            // o.Object_Type = 1;
            o.Sub_Sequence = i + 1;
            o.Own_Sub_ID = sc.Sub_Cab_ID;
            o.Own_Cab_ID = g.Cab_ID;
            ol.add(o);
        }

        sc.Store_Objects = ol;

        // 四堆子弹
        /*
         * List<StoreObjectInfo> dol = new ArrayList<StoreObjectInfo>();
         * StoreObjectInfo ddo = new StoreObjectInfo(); ddo.Object_Type = 2;
         * ddo.Sub_Sequence = 0; StoreObjectInfo do1 = new StoreObjectInfo();
         * do1.Object_Type = 2; do1.Sub_Sequence = 1; StoreObjectInfo do2 = new
         * StoreObjectInfo(); do2.Object_Type = 2; do2.Sub_Sequence = 2;
         * StoreObjectInfo do3 = new StoreObjectInfo(); do3.Object_Type = 2;
         * do3.Sub_Sequence = 3; dol.add(ddo); dol.add(do1); dol.add(do2);
         * dol.add(do3); sc1.Store_Objects = dol;
         */

        sg.add(sc);
        sg.add(sc);
        g.Sub_Cabs = sg;
        // sg.add(sc1);
        gunCabInfolist.add(g);
        // DataFunc.findPolice(info.User_ID);
        // DataFunc.findPoliceTask(info.User_ID);
    }

    public void loadData(Context context, IDataCacheStatus iDataCacheStatus) {
        this.context = context;
        this.iDataCacheStatus = iDataCacheStatus;
        fileCache = new FileCache(context);
         fackload();
        // 读取本地缓存
        String Ver = fileCache.readJsonFile("Current_Ver");
        if (!Ver.equals("")) {
            localVer = Integer.parseInt(Ver.trim());
        }

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                total++;
                Log.i(TAG, "onPreExecute: "+total);
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                Log.i(TAG, "doInBackground: ");
                loadLoacal();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                checkData();
                Log.i(TAG, "onPostExecute: ");
                TLog.v("loadLoacal Done", "");
            }

        }.execute();

        getMenberInfos(context, localVer, MainConfig.Terminal_ID);
        getGunCabLlist(context, MainConfig.Terminal_ID);
        getMemberCurrent(context, MainConfig.Terminal_ID);
        // 加载日志(可以加载不到)
        // getSystemMessage(context, MainConfig.Terminal_ID, 1, 1, -1, 100);

    }

    /**
     * 读取本地文件内容
     */
    private void loadLoacal() {
        // 加载人员
        try {
            String J_MenberRecordInfo = fileCache.readJsonFile(F_MENBERRECORDLIST);
            if (!J_MenberRecordInfo.equals("")) {
                sMenber_Datas.addAll(JSON.parseObject(J_MenberRecordInfo,
                        new TypeReference<ArrayList<MenberRecordInfo>>() {}));
            }

            // 加载出警任务(可以加载不到)
            String J_RequestPoliceTaskInfo = fileCache.readJsonFile(F_REQUESTPOLICETASKLIST);
            if (!J_RequestPoliceTaskInfo.equals("")) {
                p_task_list.addAll(JSON.parseObject(J_RequestPoliceTaskInfo,
                        new TypeReference<ArrayList<RequestPoliceTaskInfo>>() {}));
            }

        } catch (Exception e) {
            checkData();
        }

        //
    }

    /**
     * 检查是否加载成功
     */
    private void checkData() {
        Log.i(TAG, "checkData: ");
        current++;
        iDataCacheStatus.onLoading(current, total);

        if (current == total) {
            if (iDataCacheStatus != null) {
                boolean flag = true;
                if (sMenber_Datas.size() <= 0 && isneedupdate == false) {
                    flag = false;
                }
                if (gunCabInfolist.size() <= 0) {
                    flag = false;
                }
                if (sMenberInfo == null) {
                    flag = false;
                }
                if (flag) {
                    // 加载成功
                    current = 0;
                    total = 0;
                    if (!MainConfig.isDebug) {
                        ArrayList<VerifyRecordInfo> Record_Datass = new ArrayList<VerifyRecordInfo>();
                        for (int i = 0; i < sMenber_Datas.size(); i++) {
                            if (sMenber_Datas.get(i).Record_Datas != null) {
                                Record_Datass.addAll(
                                        sMenber_Datas.get(i).Record_Datas);
                            }
                        }
                        if (Record_Datass.size() > 0) {
                            // 清除指纹仪的数据
                            try {
                                FingerMG.getOne().OnOpenDeviceBtn();
                                FingerMG.getOne().OnDeleteAllBtn();

                                for (int i = 0; i < Record_Datass.size(); i++) {
                                    VerifyRecordInfo info = Record_Datass.get(i);
                                    short fingerid = (short) (info.User_ID * 10
                                            + info.Data_Type);
                                    if (info.Data != null) {
                                        // 录入指纹
                                        FingerMG.getOne().OnSetTemplate(
                                                fingerid, info.Data.getBytes());
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    iDataCacheStatus.didLoading(true);
                } else {
                    iDataCacheStatus.didLoading(false);
                }

            }
        }
    }

    // public Handler mHandler = new Handler() {
    // @Override
    // public void handleMessage(Message msg) {
    // if (msg.what == 10) {
    // sendLogList();
    // }
    // }
    // };

    boolean isneedupdate = false;

    /**
     * 从服务器获取最新用户数据集
     *
     */
    private void getMenberInfos(final Context context, int current_Ver,
                                String Terminal_ID) {
        total++;
        ApiClient.get(context,
                "api/member/new2/" + Terminal_ID + "/" + current_Ver,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        TLog.v("getMenberInfos", responseString);
                        checkData();
                    }

                    public void onSuccess(int statusCode, Header[] headers,
                                          String responseString) {
                        TLog.v("getMenberInfos", responseString);
                        JSONObject json;
                        try {
                            json = new JSONObject(responseString);
                            String Code = json.getString("Re_Code");
                            String Msg = json.getString("Re_Message");
                            int ver = json.getInt("Current_Ver");
                            if (localVer < ver) {
                                if (Code.equals("0")) {
                                    localVer = ver;
                                    fileCache.writeJsonFile("Current_Ver",
                                            localVer + "");

                                    String Menber_Datas = json
                                            .getString("Menber_Datas");

                                    List<MenberRecordInfo> menberRecordInfos =
                                            JSON.parseArray(Menber_Datas, MenberRecordInfo.class);
                                    sMenber_Datas.addAll(JSON.parseObject(
                                            Menber_Datas,new TypeReference<ArrayList<MenberRecordInfo>>() {}));
                                    saveMenber_Datas();
                                    isneedupdate = true;
                                    // DataCache.getOne().sPMenberInfo =
                                    // sMenber_Datas.get(0);
                                }
                            }

                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                        checkData();
                    }
                });
    }

    /**
     * 从服务器获取最新更新枪柜信息
     *
     * @param context
     * @param Terminal_ID
     *            终端设备编号
     */
    public void getGunCabLlist(final Context context, String Terminal_ID) {
        total++;
        ApiClient.get(context, "api/guncab/list/" + Terminal_ID,
                new TextHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        TLog.v("getGunCabLlist", responseString+"; error msg: "+throwable.getMessage());

                        // 加载枪
                        String J_GunCabInfo = fileCache
                                .readJsonFile(F_GUNCABINFOLIST);
                        if (!J_GunCabInfo.equals("")) {
                            gunCabInfolist.addAll(JSON.parseObject(J_GunCabInfo,
                                    new TypeReference<ArrayList<GunCabInfo>>() {}));
                        }
                        checkData();
                    }

                    public void onSuccess(int statusCode, Header[] headers,
                                          String responseString) {

                        TLog.v("getGunCabLlist", responseString);
                        JSONObject json;
                        try {
                            json = new JSONObject(responseString);
                            String Code = json.getString("Re_Code");
                            String Msg = json.getString("Re_Message");

                            if (Code.equals("0")) {
                                String Data = json.getString("Currrnt_Time");
                                gunCabInfolist.clear();
                                gunCabInfolist.addAll(JSON.parseObject(Data,
                                        new TypeReference<ArrayList<GunCabInfo>>() {}));
                                saveGunCab();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        checkData();
                    }
                });
    }

    /**
     * 获取当前管理员信息
     *
     * @param context
     * @param Terminal_ID
     *            终端设备编号
     */
    private void getMemberCurrent(final Context context, String Terminal_ID) {

        total++;
        ApiClient.get(context, "api/member/current/" + Terminal_ID,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        TLog.v("getMemberCurrent", responseString);
                        // 加载管理员
                        String J_MenberInfo = fileCache
                                .readJsonFile(F_MENBERINFO);
                        if (!J_MenberInfo.equals("")) {
                            sMenberInfo = JSON.parseObject(J_MenberInfo,
                                    MenberInfo.class);
                        }
                        checkData();
                    }

                    public void onSuccess(int statusCode, Header[] headers,
                                          String responseString) {
                        TLog.v("getMemberCurrent", responseString);
                        JSONObject json;
                        try {
                            json = new JSONObject(responseString);
                            String Code = json.getString("Re_Code");
                            String Msg = json.getString("Re_Message");
                            String Token = json.getString("Token");
                            String Body = json.getString("Body");
                            if (Code.equals("0")) {
                                sMenberInfo = JSON.parseObject(Body,
                                        MenberInfo.class);
                                saveMenberInfo();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        checkData();
                    }
                });
    }

    /**
     * 获取最新和历史系统消息
     *
     * @param context
     * @param Terminal_ID
     *            终端ID
     * @param Message_Type
     *            消息类型1、 默认全部类型消息2、 报警消息3、 系统操作消息
     * @param Message_State
     *            消息状态：1、 获取最新消息2、 获取历史消息
     * @param Page
     *            分页数，-1为不分页
     * @param Size
     *            每页条数
     */
    private void getSystemMessage(final Context context, String Terminal_ID,
                                  int Message_Type, int Message_State, int Page, int Size) {

        ApiClient.get(context,
                "api/systemmessage/list/" + Terminal_ID + "/" + Message_Type
                        + "/" + Message_State + "/" + Page + "/" + Size,
                new TextHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        TLog.v("getSystemMessage", responseString);
                        String J_SystemMessageInfo = fileCache
                                .readJsonFile(F_LOGLIST);
                        if (!J_SystemMessageInfo.equals("")) {
                            // LogList.clear();
                            // LogList.addAll(JSON.parseObject(J_SystemMessageInfo,
                            // new TypeReference<ArrayList<SystemMessageInfo>>()
                            // {}));
                        }
                    }

                    public void onSuccess(int statusCode, Header[] headers,
                                          String responseString) {

                        TLog.v("getSystemMessage", responseString);
                        JSONObject json;
                        try {
                            json = new JSONObject(responseString);
                            String Code = json.getString("Re_Code");
                            String Msg = json.getString("Re_Message");
                            String Data = json.getString("System_Msgs");
                            if (Code.equals("0")) {
                                // LogList.clear();
                                // LogList.addAll(JSON.parseObject(Data,
                                // new
                                // TypeReference<ArrayList<SystemMessageInfo>>()
                                // {}));
                                // saveLogList();
                                /*
                                 * adapter = new MainSysListAdapter(context,
                                 * list);
                                 * ac_main_sysinfo_listview.setAdapter(adapter);
                                 */
                            }
                        } catch (Exception e) {

                        }
                    }
                });
    }

    public void sendWarm(String s) {

        final ArrayList<SystemMessageInfo> didLogList = new ArrayList<SystemMessageInfo>();
        SystemMessageInfo info = new SystemMessageInfo();
        info.Message_Type = 1;
        info.Other = s + "被劫持";

        info.Message_State = false;
        info.Create_Time = System.currentTimeMillis();
        didLogList.add(info);
        // if (didLogList.size() > 0) {
        RequestSystemMessage request = new RequestSystemMessage();
        request.System_Msgs = didLogList;
        ApiClient.post(context,
                "/api/systemmessage/submit/" + MainConfig.Terminal_ID, request,
                null, new TextHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {

                    }

                    public void onSuccess(int statusCode, Header[] headers,
                                          String responseString) {
                        // TLog.v("sendSystemMessage", responseString);
                        JSONObject json;
                        try {
                            json = new JSONObject(responseString);
                            String Code = json.getString("Re_Code");
                            String Msg = json.getString("Re_Message");

                        } catch (Exception e) {

                        }
                    }
                });
    }

}
