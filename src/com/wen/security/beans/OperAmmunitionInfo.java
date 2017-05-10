package com.wen.security.beans;

/**
 * 警员领还弹数据结构
 *
 * @author Administrator
 *
 */
public class OperAmmunitionInfo {
    public String addr;
    public String Oper_ID;// 领还弹编号
    public String Task_ID;// 出警任务ID
    public String Ammunition_ID;// 子弹物件编号
    public int Ammo_Nums;// 计划借弹数
    public String Commissioner;// 指定代领人（默认为警员的用户ID）
    public String Draw_Time;// 领弹时间
    public String Return_Time;// 还弹时间
    public String Draw_People;// 实际领弹人用户ID
    public String Return_People;// 实际还弹人用户ID
    public int Return_Nums;// 归还弹药数
    public String Draw_Oper;// 领弹操作员用户ID
    public String Return_Oper;// 还弹操作员用户ID
    public String Remark;// 备注

}
