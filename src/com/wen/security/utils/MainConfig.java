package com.wen.security.utils;

/**
 * 配置更新地址
 * @author Administrator
 *
 */
public class MainConfig {
    public static boolean appRuning;

    public static  String Terminal_ID = "1"; //终端设备号(2)
//    public static  String Room_ID = "00000000001"; //监控号(11位)
    public static  String Room_ID = "db0829d2-8f10-4072-bbb5-0662ab4f17c7"; //监控号(11位)
    public static  String current_Ver = "2.0"; //当前版本号
//    public static  String BASE_URL = "http://120.24.56.223:9000/";
    public static  String BASE_URL = "http://192.168.1.104:86/";
//    public static  String BASE_URL = "http://120.24.56.223:1001/";
    public static final boolean logEnable = true;
    public static final boolean isDebug = true;
}
