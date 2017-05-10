package com.wen.security.beans;

import java.util.List;

/**
 * 枪柜对象数据结构
 * @author Administrator
 *
 */
public class GunCabInfo {
    public boolean isGet;//
    public String Cab_ID;//枪柜ID(唯一) *


    public String Cab_No;//枪柜编号
    public String Cab_Name;//枪柜名称
    public String Cab_Address;//枪柜地址编号
    public String IP_Address;//监控地址
    public int Cab_Type;//
 /** 1：公用枪柜（默认）
    2：专用枪柜
    3：弹药柜
    4、公用混合枪柜
    5、手枪柜
    6、长枪柜
    7、公用手枪柜
    8、公用冲锋枪柜
    9、公用自动步枪柜
    10、公用班用机枪柜
    11、公用狙击步枪柜
    12、公用防暴枪柜
    13、专用手枪柜
    14、专用冲锋枪柜
    15、专用自动步枪柜
    16、专用班用机枪柜
    17、专用狙击步枪柜
    18、专用防暴枪柜
    19、手枪弹柜
    20、步枪弹柜
    21、防爆弹柜
    22、弹夹柜*/
    public List<SubCabInfo> Sub_Cabs;//以JSON格式序列化的子柜列表
    public String Room_ID;//所属监控室
    public String Terminal_ID;//所属控制终端号


}
