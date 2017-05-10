package com.wen.security;

import android.content.Context;
import android.util.Log;

import com.wen.security.beans.MenberInfo;
import com.wen.security.beans.MenberRecordInfo;
import com.wen.security.beans.OperGunInfo;
import com.wen.security.beans.SystemMessageInfo;
import com.wen.security.beans.db.SysLogDao;
import com.wen.security.beans.request.RequestPoliceTaskInfo;
import com.wen.security.serial.TaskMG;
import com.wen.security.utils.RTools;
import com.wen.security.utils.TLog;

import java.util.ArrayList;
import java.util.List;

public class DataFunc {

    /**
     * 添加系统操作日志
     *
     * @param content
     */
    public static void addSysLog(Context context,MenberInfo inf, String content) {
//        if (content != null && inf != null) {
//            ArrayList<SystemMessageInfo> LogList = DataCache.getOne().LogList;
//            SystemMessageInfo info = new SystemMessageInfo();
//            info.Message_Type = 2;
//            info.Other = RTools.convertRankid(inf.Rankid) + "  "
//                    + inf.User_Name + "  " + content;
//            info.Create_Time = System.currentTimeMillis();
//            LogList.add(0, info);
//            if (LogList.size() > 1000) {
//                LogList.remove(LogList.size() - 1);
//            }
//            // DataCache.getOne().saveLogList();
//        }
        if (content != null && inf != null) {

            SystemMessageInfo info = new SystemMessageInfo(); //系统信息
            info.Message_Type = 2; //系统信息
            info.Other = RTools.convertRankid(inf.Rankid) + "  "
                    + inf.User_Name + "  " + content;
            info.Create_Time = System.currentTimeMillis();
            SysLogDao da0 = new SysLogDao(context);
            da0.insert(info); //插入数据库
            // DataCache.getOne().saveLogList();
        }
    }

    /**
     * 添加报警日志
     *
     * @param content
     */
    public static void addAlarmLog(Context context,String content) {
        if (content != null) {
            TaskMG.getOne().taskOpenAlarm();
            TLog.v(content);
            //ArrayList<SystemMessageInfo> LogList = DataCache.getOne().LogList;
//            if (LogList.size() > 1000) {
//                LogList.remove(LogList.size() - 1);
//            }
//            for (int i = 0; i < LogList.size(); i++) {
//                if (LogList.get(i).Other.equals(content)) {
//                    if (LogList.get(i).Message_State == false) {
//                        return;
//                    }
//                }
//            }

            SystemMessageInfo info = new SystemMessageInfo();
            info.Message_Type = 1;
            info.Other = content;
            info.Message_State = false;
            info.Create_Time = System.currentTimeMillis();
            //LogList.add(0, info);
            SysLogDao da0 = new SysLogDao(context);
            da0.insert(info);
            // DataCache.getOne().saveLogList();
            // DataCache.getOne().sendLogList();
        }
    }

//    public static void disposeLog() {
//        ArrayList<SystemMessageInfo> LogList = DataCache.getOne().LogList;
//        for (int i = 0; i < LogList.size(); i++) {
//            LogList.get(i).Message_State = true;
//            LogList.get(i).Dispose_Time = System.currentTimeMillis();
//        }
//
//    }

    /**
     * 发送日志
     */
//    public static void saveAndSendLog() {
//
//        DataCache.getOne().saveLogList();
//        DataCache.getOne().sendLogList();
//    }

    /**
     * 查找警员
     *
     * @param ids
     *            User_ID
     * @return
     */
    public static MenberInfo findPolice(String ids) {
        int fids = Integer.parseInt(ids)%10;
        String id = Integer.parseInt(ids)/10+"";
        if (id != null && !id.equals("")) {
            ArrayList<MenberRecordInfo> list = DataCache.getOne().sMenber_Datas;
            for (int i = 0; i < list.size(); i++) {
                MenberRecordInfo sPMenberInfo = list.get(i);
                if (id.equals(sPMenberInfo.User_ID)) {

                    if (sPMenberInfo.Record_Datas != null) {
                        for (int j = 0; j < sPMenberInfo.Record_Datas.size(); j++) {
                            if (sPMenberInfo.Record_Datas.get(j).Data_Type == fids && sPMenberInfo.Record_Datas.get(j).Check_Mode == 2 ) {
                                DataCache.getOne().sendWarm(id);
                            }
                        }
                    }

                    DataCache.getOne().sPMenberInfo = sPMenberInfo;
                    return sPMenberInfo;
                }
            }
        }
        return null;
    }

    /**
     * 换班
     *
     * @param id
     *            User_ID
     * @return
     */
    public static MenberInfo changeManager(String id) {
        if (id != null && !id.equals("")) {
            ArrayList<MenberRecordInfo> list = DataCache.getOne().sMenber_Datas;
            for (int i = 0; i < list.size(); i++) {
                MenberInfo sMenberInfo = list.get(i);
                if (id.equals(sMenberInfo.User_ID)) {
                    // DataCache.getOne().sMenberInfo = sMenberInfo;
                    return sMenberInfo;
                }
            }
        }
        return null;
    }

    /**
     * 查找出警任务
     *
     * @param id
     *            User_ID
     * @return
     */
    public static RequestPoliceTaskInfo findPoliceTask(String id) {
        if (id != null && !id.equals("")) {
            ArrayList<RequestPoliceTaskInfo> list = DataCache.getOne().p_task_list;
            for (int i = 0; i < list.size(); i++) {
                RequestPoliceTaskInfo task = list.get(i);
                Log.v("task", task.is_done + " all " + task.User_ID);
                if (id.equals(task.User_ID)) {
                    Log.v("find task", task.is_done + ":::::::::"
                            + task.User_ID);
                    DataCache.getOne().go_taskinfo = task;
                    return task;
                }
            }
        }
        return null;
    }

    /**
     * 添加出警任务到缓存
     *
     * @param info
     */
    public static void addPoliceTaskInfo(RequestPoliceTaskInfo info) {
        if (info != null) {
            ArrayList<RequestPoliceTaskInfo> list = DataCache.getOne().p_task_list;
            for (int i = 0; i < list.size(); i++) {

                RequestPoliceTaskInfo task = list.get(i);
                Log.v("task", task.is_done + "  all  " + task.User_ID);
                if (info.User_ID.equals(task.User_ID)) {
                    Log.v(" remove task", task.is_done + ":::::::::"
                            + task.User_ID);
                    list.remove(task);
                    break;
                }
            }
            // info.is_done = false;
            DataCache.getOne().go_taskinfo = info;
            DataCache.getOne().p_task_list.add(info);
            DataCache.getOne().savep_task_list();
        }
    }

    /**
     * 出警任务到缓存
     *
     * @param info
     */
    public static void removePoliceTaskInfo(RequestPoliceTaskInfo info) {
        if (info != null) {
            ArrayList<RequestPoliceTaskInfo> list = DataCache.getOne().p_task_list;
            for (int i = 0; i < list.size(); i++) {

                RequestPoliceTaskInfo task = list.get(i);
                Log.v("task", task.is_done + "  all  " + task.User_ID);
                if (info.User_ID.equals(task.User_ID)) {
                    Log.v(" remove task", task.is_done + ":::::::::"
                            + task.User_ID);
                    list.remove(task);
                    break;
                }
            }

            DataCache.getOne().savep_task_list();
        }
    }
    public static long gunbacktimeout = 3*60*1000;

    /**
     * 超时还枪报警
     * @param context
     */
    public static void watchTimeOutGunBack(Context context) {

        // TODO Auto-generated method stub
        ArrayList<RequestPoliceTaskInfo> list = DataCache.getOne().p_task_list;
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {

                RequestPoliceTaskInfo task = list.get(i);
                List<OperGunInfo> Oper_Guns = task.Oper_Guns;
                if (Oper_Guns != null) {
                    for (int j = 0; j < Oper_Guns.size(); j++) {
                        OperGunInfo gunInfo = Oper_Guns.get(j);
                        if (gunInfo.Return_Time == 0 && gunInfo.Draw_Time != 0) {
                            long offset = System.currentTimeMillis()
                                    - gunInfo.Draw_Time;
                            if (offset > gunbacktimeout) {
                                addAlarmLog(context,"警员" + gunInfo.PoliceName
                                        + "超时未归还枪支  枪号：" + gunInfo.Gun_ID);
                            }
                        }
                    }
                }

            }

        }
        // timer.schedule(task, delay, period)
    }

}
