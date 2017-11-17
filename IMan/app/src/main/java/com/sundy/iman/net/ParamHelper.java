package com.sundy.iman.net;

import android.util.Log;

import com.orhanobut.logger.Logger;
import com.sundy.iman.MainApp;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.utils.DeviceUtils;
import com.sundy.iman.utils.EncryptorUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求参数Helper
 * Created by sundy on 17/9/21.
 */

public class ParamHelper {

    private static final String TAG = "ParamHelper";

    public static Map<String, String> formatData(Map<String, String> map) {
        try {
            if (map == null)
                map = new HashMap<>();
            map.putAll(getCommonData());

            //排序
            List<Map.Entry<String, String>> list = new ArrayList<>(map.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
                //升序排序
                public int compare(Map.Entry<String, String> o1,
                                   Map.Entry<String, String> o2) {
                    return o1.getKey().compareTo(o2.getKey());
                }
            });

            JSONObject object = new JSONObject();
            for (int i = 0; i < list.size(); i++) {
                Map.Entry<String, String> stringObjectEntry = list.get(i);
                String key = stringObjectEntry.getKey();
                Object value = map.get(key);
                object.put(key, value);
            }
            map.put("signature", getSign(object));
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Log.e(TAG, entry.getKey() + "=" + entry.getValue());
            }
            return map;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取签名
    public static String getSign(JSONObject object) {
        String jsonStr = object.toString();
        Logger.t("json").json(jsonStr);
//        Log.e(TAG,"--->jsonStr =" + jsonStr);
        String signature = EncryptorUtils.md5(EncryptorUtils.md5(jsonStr + EncryptorUtils.KEY_SIGN)).toUpperCase();
//        Log.e(TAG,"----->signature = " + signature);
        return signature;
    }

    //获取公共参数
    public static Map<String, String> getCommonData() {
        Map<String, String> commonMap = new HashMap<>();
        commonMap.put("version", DeviceUtils.getAppVersion(MainApp.getInstance()));//版本号
        commonMap.put("source", "2");//来源: 1-ios，2-Android
        commonMap.put("device_id", PaperUtils.getDeviceId());//设备号
        commonMap.put("language", PaperUtils.getLanguage());//en-英文，sc-简体中文，tc-繁体中文
        commonMap.put("appkey", "im_android"); // app key 用于验签 android：im_android ios：im_iphone
        return commonMap;
    }

}