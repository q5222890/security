package com.wen.security.beans;

/**
 * 警员领还枪数据结构
 * @author Administrator
 *
 */
public class OperGunInfo {

    public int Sub_Sequence;//物件装载在子柜内的序列位置（子弹的话这个项的值为NULL）子地址 *
    public String Own_Sub_ID;//所属子柜ID（子柜总控地址）*
    public String Own_Cab_ID;//所属枪柜ID（枪柜总控地址）*

    public String Oper_ID;//领还枪编号
    public String Task_ID;//出警任务ID
    public String Gun_ID;//枪编号
    public OperAmmunitionInfo Oper_Ammunition;//枪支配套弹药对象
    public OperAmmunitionBoxInfo Oper_Box ;//      枪支配套弹夹对象
    public String Commissioner;//指定代领人（默认为警员的用户ID）
    public boolean IsOper = false;//是否领用
    public long Draw_Time;//实际领枪时间
    public long Return_Time;//实际还枪时间
    public String Draw_People;//实际领枪人用户ID
    public String  Return_People;//实际还枪人用户ID
    public String Draw_Oper;// 领枪操作员用户ID
    public String Return_Oper;//还枪操作员用户ID

    /// <summary>
    /// 枪编号
    /// </summary>
    public String GunNo ;

    /// <summary>
    /// 枪类型ID
    /// </summary>
    public int GunTypeID ;

    /// <summary>
    /// 枪类型名称
    /// </summary>
    public String GunTypeName ;

    /// <summary>
    /// 警号
    /// </summary>
    public String PoliceNo ;

    /// <summary>
    /// 警员姓名
    /// </summary>
    public String PoliceName ;

    /// <summary>
    /// 实际弹药数量
    /// </summary>
    public String AmmoNum ;


}
