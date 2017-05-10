package com.wen.security.beans;

import java.util.List;

/**
 * 子柜对象数据结构
 * @author Administrator
 *
 */
public class SubCabInfo {
    public String Sub_Cab_ID;//子柜ID（唯一） *
    public String Sub_Cab_No ;//子柜编号
    public String Sub_Cab_Name;//子柜名称
    public String Sub_Cab_Address;//子柜地址编号
    public String Sub_IP_Address ;//子柜监控地址
    public int Store_Type;//物件类型：
    /** 1、  枪
     2、  子弹弹药
     3、  弹夹 **/
    public List<StoreObjectInfo> Store_Objects   ;//以JSON格式序列化的装载物件列表根据Store_Type的定义类型为以下几个数据结构：
    /**
     1、  枪（GunInfo）
    2、  子弹（）
    3、  弹夹（）**/
    public String Own_Cab_ID;//所属枪柜ID
    public int Total_Size;//最大存放物件数量
    public int Surplus_Size;//剩余空间数量

}
