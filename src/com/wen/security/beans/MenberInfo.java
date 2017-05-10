package com.wen.security.beans;

/**
 * 登陆用户信息
 * @author Administrator
 *
 */
public class MenberInfo {

    public String User_ID = "";//用户ID
    public String User_Name = "";//用户姓名
    public String Password = "";//用户密码
    public int Identity;//用户身份：
    /* 0为超级管理员
     1为枪械管理员
     2为普通警员
     3为值班领导*/
    public String Police_Number = "";//警号
    public String Duty = "";//职务
    public int Rankid;//
    /*0：无警衔（管理员）
    1：一级警员
    2：二级警员
    3：一级警司
    4：二级警司
    5：三级警司
    6：一级警督
    7：二级警督
    8：三级警督
    9：一级警监
    10：二级警监
    11：三级警监*/
    public int Sex;//
    /*1、男性（默认），
    2、女性*/
    public String Department = "";//部门名称
    public String Department_ID = "";//部门ID
    public String Department_Code = "";//部门编号，编号规则为4级每级以’|’区分：    0000|0000|0000|0000
    public String Register_Date = "";//注册日期
    public GunLicenceInfo Licence_Info ;//持枪证对象,没有为NULL
    public String User_Photo = "";//用户照片（Base64格式数据流）
    public String Record_Date  = "";//验证信息录入日期
    public int Record_Type ;//验证信息录入类型
    /* 1、指纹信息、
     2、视网膜信息、
     3、3、其他*/
    public String Last_Login_Date = "";//用户上次登陆时间
    public String Last_Terminal_ID = "";//用户上次登陆的终端ID

}
