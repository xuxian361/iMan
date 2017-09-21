package com.sundy.iman.net;

import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求参数Helper
 * Created by sundy on 17/9/21.
 */

public class ParamHelper {

    public static Map<String, String> formatData(Map<String, Object> map) {
        try {
            JSONObject object = new JSONObject();
            for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
                String key = stringObjectEntry.getKey();
                Object value = map.get(key);
                object.put(key, value);
            }
            return formatData(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> formatData(JSONObject object) {
        Map<String, String> result = new HashMap<>();
        result.put("data", object.toString());
        Logger.json(object.toString());
        return result;
    }
}
