package com.wen.security.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017-02-25.
 */

public class SPUtils {

    private static String name ="Security";
//    private static SPUtils instance;
//    private SPUtils(){};
//    private Context context;
//    public static SPUtils getInstance(){
//        if(instance ==null){
//            instance =new SPUtils();
//        }
//        return instance;
//    }

    private static SharedPreferences getShared(Context context){
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp;
    }

    private static SharedPreferences.Editor getEditor(Context context){
        SharedPreferences.Editor edit = getShared(context).edit();
        return edit;
    }

    public static void saveConfig(Context context,String url,String room_id,String terminal_id){

        getEditor(context).putString("BASE_URL", url)
        .putString("Room_ID", room_id)
        .putString("Terminal_ID", terminal_id)
        .commit();
    }

    public static String getConfigUrl(Context context){

        return getShared(context).getString("BASE_URL",MainConfig.BASE_URL);
    }

    public static String getRoomID(Context context){

        return getShared(context).getString("Room_ID",MainConfig.Room_ID);
    }

    public static String getTerminalID(Context context){

        return getShared(context).getString("Terminal_ID",MainConfig.Terminal_ID);
    }


}
