package com.wen.security.beans;

import java.util.List;

/**
 * 本地用户持久化保存数据继承MenberInfo
 * @author Administrator
 *
 */
public class MenberRecordInfo extends MenberInfo{
    public String Nation  ;  //民族
    public String Departy ;// 政治面貌
    public String Birthday ; //出生日期
    public String PassPort ; //身份证号码
    public String Address ; //联系地址
    public String WorkTime ; //参加工作时间
    public String Mobile_Phone ; //移动电话
    public String Home_Phone ; //家庭电话
    public String Office_Phone  ; //办公电话
    public List< VerifyRecordInfo> Record_Datas ;//以JSON格式序列化的验证数据录入数据组（单个数据为Base64格式数据流）
    public String Task_ID ; //当前警员的出警任务ID、可为空
}
