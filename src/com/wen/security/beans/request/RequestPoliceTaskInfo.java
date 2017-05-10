package com.wen.security.beans.request;

import com.wen.security.beans.OperGunInfo;

import java.util.List;

/**
 * 警员出警任务申报数据结构
 * @author Administrator
 *
 */
public class RequestPoliceTaskInfo extends RequestBase{
    public boolean is_done = false; //是否已还枪
    public boolean is_get = false; //是否已领枪
    public String Task_ID;//出警任务ID
    public String  User_ID ;//申请警员的用户ID
    public String Task_Begin_Time ;//String  计划出警时间
    public String Task_End_Time;// 计划结束时间
    /**1、 涉黄
     2、  涉赌
     3、  刑事案件
     4、  紧急出警
     5、  其他
     */
    public int Apply_Base ; //申请理由:

    public  List<OperGunInfo>  Oper_Guns;//申请枪支列表数组

    /// <summary>
    /// 批准出警时间
    /// </summary>
    public String AllowBeginTime ;

    /// <summary>
    /// 批准结束时间
    /// </summary>
    public String AllowEndTime ;

    /// <summary>
    /// 审批人ID
    /// </summary>
    public String ApprovPoliceID ;

    /// <summary>
    /// 审批人姓名
    /// </summary>
    public String ApprovPoliceName ;

    /// <summary>
    /// 审批时间
    /// </summary>
    public String ApprovTime ;

    /// <summary>
    /// 备注、任务总结
    /// </summary>
    public String Remark ;

}
