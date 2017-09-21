package com.sundy.iman;

import android.app.Application;
import android.support.annotation.StringRes;
import android.widget.Toast;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.sundy.iman.utils.ToastUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.paperdb.Paper;

/**
 * Created by sundy on 17/9/14.
 */

public class MainApp extends Application {

    private static MainApp instance;
    private ToastUtil toastUtil;    //用做Toast显示，注意这个虽然不是单例，但是最好做为单例使用
    private ExecutorService executorService;    //线程池

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        initConfig();
    }

    //初始化配置
    private void initConfig() {
        //第三方缓存功能初始化
        Paper.init(this);

        //初始化Logger日志打印
        Logger.addLogAdapter(new AndroidLogAdapter());

    }

    /******************************* Method *******************************/
    public static MainApp getInstance() {
        return instance;
    }

    public synchronized ToastUtil getToastUtil() {
        if (toastUtil == null) {
            toastUtil = new ToastUtil(this);
        }
        return toastUtil;
    }

    /**
     * 显示Toast
     *
     * @param str String,需要显示的内容
     */
    public void showToast(String str) {
        getToastUtil();
        toastUtil.showToast(str, Toast.LENGTH_SHORT);
    }

    /**
     * 显示Toast
     *
     * @param stringRes StringRes,需要显示的字符串ID
     */
    public void showToast(@StringRes int stringRes) {
        getToastUtil();
        toastUtil.showToast(stringRes, Toast.LENGTH_SHORT);
    }

    /**
     * 获取线程池
     *
     * @return ExecutorService
     */
    public ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = Executors.newCachedThreadPool();
        }
        return executorService;
    }

}
