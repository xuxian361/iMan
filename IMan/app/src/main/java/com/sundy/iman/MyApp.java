package com.sundy.iman;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

/**
 * Created by sundy on 17/9/14.
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化Logger日志打印
        Logger.addLogAdapter(new AndroidLogAdapter());

    }
}
