package com.sundy.iman.entity;

import java.util.HashMap;

/**
 * Created by sundy on 17/10/9.
 */

public class MsgEvent {

    private String msg;
    private String data;
    private HashMap<String, String> map;
    private Object obj;

    public MsgEvent() {
    }

    public MsgEvent(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public HashMap<String, String> getMap() {
        return map;
    }

    public void setMap(HashMap<String, String> map) {
        this.map = map;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    /*********************** Code ***********************/
    //获取国家码/区号
    public static final String EVENT_GET_COUNTRY_CODE = "Event_Get_Country_Code";
    //获取位置信息成功
    public static final String EVENT_GET_LOCATION = "Event_Get_Location";
    //登录成功
    public static final String EVENT_LOGIN_SUCCESS = "Event_Login_Success";
    //登出成功
    public static final String EVENT_LOGOUT_SUCCESS = "Event_Logout_Success";
    //更新用户信息成功
    public static final String EVENT_UPDATE_USER_INFO = "Event_Update_UserInfo";

}
