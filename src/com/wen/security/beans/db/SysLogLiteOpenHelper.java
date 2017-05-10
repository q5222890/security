package com.wen.security.beans.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author andong 数据库帮助类, 用于创建和管理数据库的.
 */
public class SysLogLiteOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "SysLogLiteOpenHelper";
    public static final String SYSTABLENAME = "logTable"; //数据库表名

    /**
     * 数据库的构造函数
     *
     * @param context
     *
     *            name 数据库名称 factory 游标工程 version 数据库的版本号 不可以小于1
     */
    public SysLogLiteOpenHelper(Context context) {
        super(context, "SysLog.db", null, 2);
    }

    /**
     * 数据库第一次创建时回调此方法. 初始化一些表
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // public String Message_ID ;//信息ID
        // public String Terminal_ID = MainConfig.Terminal_ID;//所属控制终端号
        // public int Message_Type ;//信息类型：
        // /* 1、 报警信息
        // 2、 系统操作信息*/
        // public String Create_Time ;//信息产生时间
        // public String Dispose_Time ;//信息处理时间
        // public boolean Message_State ;//信息处理状态
        // public String Other ;//其他提示信息
        // public boolean isUpload = false;//是否上传
        // 操作数据库
        String sql = "create table " + SYSTABLENAME
                + "(_id integer primary key AUTOINCREMENT, Message_ID varchar(20),"
                + "Terminal_ID varchar(20),Message_Type integer,Create_Time long,Dispose_Time long,"
                + "Message_State integer,Other varchar(500),isUpload integer);";
        db.execSQL(sql); // 创建表
    }

    /**
     * 数据库的版本号更新时回调此方法, 更新数据库的内容(删除表, 添加表, 修改表)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion == 1 && newVersion == 2) {
            Log.i(TAG, "数据库更新啦");
            // 在person表中添加一个余额列balance
            // db.execSQL("alter table "+TABLENAME+" add balance integer;");
        }
    }

}
