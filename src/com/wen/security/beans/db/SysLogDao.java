package com.wen.security.beans.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wen.security.beans.SystemMessageInfo;

import java.util.ArrayList;

public class SysLogDao {

    private SysLogLiteOpenHelper mOpenHelper; // 数据库的帮助类对象

    public SysLogDao(Context context) {
        mOpenHelper = new SysLogLiteOpenHelper(context);
    }

    public Context context;
    public static SysLogDao instance;

    public static SysLogDao getInstance(Context context) {
        if (instance == null) {
            instance = new SysLogDao(context);
        }
        return instance;
    }

    /**
     * 插入数据
     * @param log
     */
    public void insert(SystemMessageInfo log) {
        if (log == null) {
            return;
        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        if (db.isOpen()) { // 如果数据库打开, 执行添加的操作
            // String sql = "create table " + SYSTABLENAME
            // + "(_id integer primary key AUTOINCREMENT, Message_ID
            // varchar(20),"
            // + "Terminal_ID varchar(20),Message_Type integer,Create_Time
            // integer,Dispose_Time integer,"
            // + "Message_State integer,Other varchar(500),isUpload integer);";
            // 执行添加到数据库的操作
            db.execSQL(
                    "insert into " + SysLogLiteOpenHelper.SYSTABLENAME
                            + "(Message_ID, Terminal_ID,Message_Type,Create_Time,Dispose_Time"
                            + ",Message_State,Other,isUpload) values(?, ? ,? , ?, ?, ? ,?,?);",
                    new Object[] { log.Message_ID, log.Terminal_ID,
                            log.Message_Type, log.Create_Time, log.Dispose_Time,
                            log.Message_State == true ? 1 : 0, log.Other,
                            log.isUpload == true ? 1 : 0 });

            db.close(); // 数据库关闭
        }
    }

    /**
     * 查询数据库总数
     * @return
     */
    public int total() {
        int total = 0;
        SQLiteDatabase db = mOpenHelper.getReadableDatabase(); // 获得一个只读的数据库对象
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("SELECT COUNT(*) from "
                    + SysLogLiteOpenHelper.SYSTABLENAME + ";", null);
            if (cursor != null && cursor.getCount() > 0) {

                while (cursor.moveToNext()) {
                    SystemMessageInfo log = new SystemMessageInfo();
                    total = cursor.getInt(0);
                }
                db.close();
            }
        }
        return total;
    }

    /**
     * 删除系统日志
     * @param log
     */
    public void delete(SystemMessageInfo log) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase(); // 获得可写的数据库对象
        if (db.isOpen()) { // 如果数据库打开, 执行添加的操作

            db.execSQL("delete from " + SysLogLiteOpenHelper.SYSTABLENAME
                    + " where isUpload = " + (log.isUpload == true ? 1 : 0)
                    + ";", new Object[]{});

            db.close(); // 数据库关闭
        }
    }

    /**
     * 删除所有数据
     */
    public void deleteAll(){
        SQLiteDatabase db =mOpenHelper.getWritableDatabase();
        if(db.isOpen()){
            db.execSQL("delete from"+SysLogLiteOpenHelper.SYSTABLENAME);
        }
    }

    /**
     * 更新数据库
     * @param Create_Time
     */
    public void updateLoad(long Create_Time) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        if (db.isOpen()) { // 如果数据库打开, 执行添加的操作

            db.execSQL(
                    "update " + SysLogLiteOpenHelper.SYSTABLENAME
                            + " set isUpload = 1 where Create_Time <= ?;",
                    new Object[] { Create_Time });

            db.close(); // 数据库关闭
        }
    }

    public void disposeLog() {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        if (db.isOpen()) { // 如果数据库打开, 执行添加的操作

            db.execSQL(
                    "update " + SysLogLiteOpenHelper.SYSTABLENAME
                            + " set Message_State = 1 , Dispose_Time =  ? ;",
                    new Object[] { System.currentTimeMillis() });

            db.close(); // 数据库关闭
        }
    }

    public ArrayList<SystemMessageInfo> queryAll() {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase(); // 获得一个只读的数据库对象
        ArrayList<SystemMessageInfo> personList = new ArrayList<SystemMessageInfo>();
        if (db.isOpen()) {

            Cursor cursor = db
                    .rawQuery(
                            "select Message_ID, Terminal_ID,Message_Type,Create_Time,Dispose_Time"
                                    + ",Message_State,Other,isUpload ,_id from "
                                    + SysLogLiteOpenHelper.SYSTABLENAME
                                    + " order by Create_Time desc limit 0,100;",
                            null);

            if (cursor != null && cursor.getCount() > 0) {

                while (cursor.moveToNext()) {
                    SystemMessageInfo log = new SystemMessageInfo();
                    log.Message_ID = cursor.getString(0);
                    log.Terminal_ID = cursor.getString(1);
                    log.Message_Type = cursor.getInt(2);
                    log.Create_Time = cursor.getLong(3);
                    log.Dispose_Time = cursor.getLong(4);
                    log.Message_State = cursor.getInt(5) == 0 ? false : true;
                    log.Other = cursor.getString(6);
                    log.isUpload = cursor.getInt(7) == 0 ? false : true;
                    log._id = cursor.getInt(8);
                    personList.add(log);
                }
            }
            db.close();
        }
        return personList;
    }

    public ArrayList<SystemMessageInfo> queryAllLog(int Type) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase(); // 获得一个只读的数据库对象
        ArrayList<SystemMessageInfo> personList = new ArrayList<SystemMessageInfo>();
        if (db.isOpen()) {

            Cursor cursor = db.rawQuery(
                    "select Message_ID, Terminal_ID,Message_Type,Create_Time,Dispose_Time"
                            + ",Message_State,Other,isUpload ,_id from "
                            + SysLogLiteOpenHelper.SYSTABLENAME
                            + " where Message_Type = ? order by Create_Time desc limit 0,500;",
                    new String[] { Type + "" });

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    SystemMessageInfo log = new SystemMessageInfo();
                    log.Message_ID = cursor.getString(0);
                    log.Terminal_ID = cursor.getString(1);
                    log.Message_Type = cursor.getInt(2);
                    log.Create_Time = cursor.getLong(3);
                    log.Dispose_Time = cursor.getLong(4);
                    log.Message_State = cursor.getInt(5) == 0 ? false : true;
                    log.Other = cursor.getString(6);
                    log.isUpload = cursor.getInt(7) == 0 ? false : true;
                    log._id = cursor.getInt(8);
                    personList.add(log);
                }
            }
            db.close();
        }
        return personList;
    }

    /**
     * 查询所有数据
     * @return
     */
    public ArrayList<SystemMessageInfo> queryUploadAll() {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase(); // 获得一个只读的数据库对象
        ArrayList<SystemMessageInfo> personList = new ArrayList<SystemMessageInfo>();
        if (db.isOpen()) {

            Cursor cursor = db.rawQuery(
                    "select Message_ID, Terminal_ID,Message_Type,Create_Time,Dispose_Time"
                            + ",Message_State,Other,isUpload ,_id from "
                            + SysLogLiteOpenHelper.SYSTABLENAME
                            + " where  isUpload = 1;",
                    null);

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    SystemMessageInfo log = new SystemMessageInfo();
                    log.Message_ID = cursor.getString(0);
                    log.Terminal_ID = cursor.getString(1);
                    log.Message_Type = cursor.getInt(2);
                    log.Create_Time = cursor.getLong(3);
                    log.Dispose_Time = cursor.getLong(4);
                    log.Message_State = cursor.getInt(5) == 0 ? false : true;
                    log.Other = cursor.getString(6);
                    log.isUpload = cursor.getInt(7) == 0 ? false : true;
                    log._id = cursor.getInt(8);
                    personList.add(log);
                }

            }
            db.close();
        }
        return personList;
    }

}
