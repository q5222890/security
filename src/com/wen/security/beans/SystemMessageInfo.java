package com.wen.security.beans;

import com.wen.security.utils.MainConfig;

/**
 * 系统信息数据结构
 *
 * @author Administrator
 *
 */
public class SystemMessageInfo {


    public int _id ;  //主键
    public String Message_ID ;//信息ID
    public String  Terminal_ID = MainConfig.Terminal_ID;//所属控制终端号
    public int Message_Type ;//信息类型：
    /* 1、  报警信息
     2、  系统操作信息*/
    public long Create_Time ;//信息产生时间
    public long Dispose_Time ;//信息处理时间
    public boolean Message_State = false;//信息处理状态
    public String Other ;//其他提示信息
    public boolean isUpload = false;//是否上传

}
