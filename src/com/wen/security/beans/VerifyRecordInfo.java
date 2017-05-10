package com.wen.security.beans;

/**
 *
 * 验证录入数据结构（指纹或虹膜等）
 * @author Administrator
 *
 */
public class VerifyRecordInfo {
    public int User_ID; //用户ID
    public int Data_Type ;  //数据类型：
    /*1、  左手大拇指
    2、  左手食指
    3、  左手中指
    4、  左手无名指
    5、  左手小指
    6、  右手大拇指
    7、  右手食指
    8、  右手中指
    9、  右手无名指
    10、 右手小指
    11、 左眼虹膜
    12、 右眼虹膜
    13、 其他*/
    public int Check_Mode; //检查方式:
    /* 1、  正常验证
     2、  非正常验证*/
    public String Data ; //以JSON格式序列化的验证数据组（单个数据为Base64格式数据流）

}
