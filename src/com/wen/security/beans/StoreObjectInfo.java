package com.wen.security.beans;

/**
 * 装载物件对象基础数据结构  每个柜子里面放置的物件
 * @author Administrator
 *
 */
public class StoreObjectInfo {
    /**
     *  1、  枪
     *  2、  子弹弹药
     *  3、  弹夹
     */
    public String Object_Type;//参照子柜Store_Type
    public String Object_ID;//物件ID
    public int Sub_Sequence;//物件装载在子柜内的序列位置（子弹的话这个项的值为NULL）子地址 *
    public String Own_Sub_ID;//所属子柜ID（子柜总控地址） *
    public String Own_Cab_ID;//所属枪柜ID（枪柜总控地址）*
    public String Change_Date;//最后变更时间
    public int Object_Size;//物件总数量
    public int Current_size;//当前数量
    public String Producer;//生产厂家
    public String Product_Date;//出厂日期
    public String Buy_Date;//购买日期
    public String Maintain_Date;//保养日期
    public String Remark;//备注
    public Boolean IsOper = false;

}
