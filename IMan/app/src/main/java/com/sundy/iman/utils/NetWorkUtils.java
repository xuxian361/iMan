package com.sundy.iman.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络工具类
 * Created by sundy on 17/9/26.
 */

public class NetWorkUtils {

    /**
     * 没有网络
     */
    public static final int NETWORK_TYPE_INVALID = 0;
    /**
     * 移动网络
     */
    public static final int NETWORK_TYPE_MOBILE = 1;
    /**
     * wifi网络
     */
    public static final int NETWORK_TYPE_WIFI = 2;

    //网络是否连接
    public static boolean isNetAvailable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity == null) {
                return false;
            } else {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //是否Wifi连接
    public static boolean isWifiAvailable(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi != null) {
            return mWifi.isAvailable();
        }
        return false;
    }

    //获取网络状态
    public static int getNetType(Context context) {
        int mNetWorkType = NETWORK_TYPE_WIFI;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                mNetWorkType = NETWORK_TYPE_WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                mNetWorkType = NETWORK_TYPE_MOBILE;
            }
        } else {
            mNetWorkType = NETWORK_TYPE_INVALID;
        }
        return mNetWorkType;
    }

    //获取网络状态
    public static String getNetworkType(Context context) {
        String typeStr = "WIFI";
        int type = getNetType(context);
        switch (type) {
            case NETWORK_TYPE_WIFI:
                typeStr = "WIFI";
                break;
            case NETWORK_TYPE_MOBILE:
                typeStr = "MOBILE";
                break;
            case NETWORK_TYPE_INVALID:
                typeStr = "INVALID";
                break;
        }
        return typeStr;
    }

}
