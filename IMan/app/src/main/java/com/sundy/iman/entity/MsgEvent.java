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
    //获取标签列表成功
    public static final String EVENT_GET_TAGS = "Event_Get_Tags";
    //选择社区列表成功
    public static final String EVENT_GET_COMMUNITIES = "Event_Get_Communities";
    //退出某一个社区成功
    public static final String EVENT_QUIT_COMMUNITY_SUCCESS = "Event_Quit_Community_Success";
    //登录成功
    public static final String EVENT_LOGIN_SUCCESS = "Event_Login_Success";
    //登出成功
    //public static final String EVENT_LOGOUT_SUCCESS = "Event_Logout_Success";
    //更新用户信息成功
    public static final String EVENT_UPDATE_USER_INFO = "Event_Update_UserInfo";
    //更新未读消息数
    public static final String EVENT_UPDATE_UNREAD_MSG_COUNT = "Event_update_unread_msg_count";
    //发送Imcoin 成功
    public static final String EVENT_SEND_IMCOIN_SUCCESS = "Event_send_imcoin_success";
    //更新社区消息列表
    public static final String EVENT_REFRESH_COMMUNITY_MSG_LIST = "Event_refresh_community_msg_list";


}
