package com.wen.security;

import android.app.Application;
import android.content.Context;
import android.os.PowerManager;

import com.wen.security.utils.ImageLoader;

public class SecurityApplication extends Application {

    private static SecurityApplication instance;

    PowerManager.WakeLock wl;
    public static SecurityApplication getInstance() {
        return instance;
    }
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        instance =this;
        ImageLoader.getOne(this);
        DataCache.getOne();
        //如何让android的屏幕保持常亮
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        // MyTag可以随便写,可以写应用名称等
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, getPackageName());
        // 在释放之前，屏幕一直亮着（有可能会变暗,但是还可以看到屏幕内容,换成PowerManager.SCREEN_BRIGHT_WAKE_LOCK不会变暗）
        wl.acquire();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        wl.release();

        super.onTerminate();
    }

}
