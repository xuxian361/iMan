package com.sundy.iman;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.previewlibrary.ZoomMediaLoader;
import com.sundy.iman.impl.GlideImageLoader;
import com.sundy.iman.utils.ToastUtil;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.paperdb.Paper;

/**
 * Created by sundy on 17/9/14.
 */

public class MainApp extends MultiDexApplication {

    private static MainApp instance;
    private ToastUtil toastUtil;    //用做Toast显示，注意这个虽然不是单例，但是最好做为单例使用
    private ExecutorService executorService;    //线程池

    {
        PlatformConfig.setWeixin("wx967daebe835fbeac", "5bb696d9ccd75a38c8a0bfe0675559b3");
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
    }

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

        ZoomMediaLoader.getInstance().init(new GlideImageLoader());

        //友盟分享
        UMShareAPI.get(this);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1)//非默认值
            getResources();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                createConfigurationContext(newConfig);
            } else {
                res.updateConfiguration(newConfig, res.getDisplayMetrics());
            }
        }
        return res;
    }
}
