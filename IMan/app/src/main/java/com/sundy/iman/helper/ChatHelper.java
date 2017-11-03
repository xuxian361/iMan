package com.sundy.iman.helper;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.sundy.iman.MainApp;
import com.sundy.iman.R;
import com.sundy.iman.entity.MemberInfoEntity;
import com.sundy.iman.greendao.ImUserInfo;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.ui.activity.ChatActivity;

import java.util.List;

/**
 * Created by sundy on 17/9/14.
 */

public class ChatHelper {

    private static final String TAG = "ChatHelper";
    private static final ChatHelper ourInstance = new ChatHelper();
    private EMOptions emOptions;

    public static ChatHelper getInstance() {
        return ourInstance;
    }

    public EMOptions getEmOptions() {
        if (emOptions == null) {
            emOptions = new EMOptions();
            emOptions.setAutoLogin(true);
            emOptions.setAcceptInvitationAlways(false);
        }
        return emOptions;
    }

    //初始化环信
    public void init(Application application) {
        EaseUI.getInstance().init(application, ChatHelper.getInstance().getEmOptions());
        registerMessageListener();
        initUserProvider();
        initNotify();
    }

    //添加/更新环信用户自己信息：
    public void updateSelfUserInfo(MemberInfoEntity user) {
        if (user == null)
            return;
        MemberInfoEntity.DataEntity dataEntity = user.getData();
        if (dataEntity == null)
            return;

        ImUserInfo entity = DbHelper.getInstance().getUserInfoByHxId(dataEntity.getEasemob_account());
        if (entity == null) {
            entity = new ImUserInfo();
        }
        entity.setEasemob_account(dataEntity.getEasemob_account());
        entity.setProfile_image(dataEntity.getProfile_image());
        entity.setUsername(dataEntity.getUsername());
        entity.setUserId(dataEntity.getId());
        entity.setGender(dataEntity.getGender());
        DbHelper.getInstance().addUserInfoEntity(entity);
    }

    //初始化环信用户提供者
    private void initUserProvider() {
        EaseUI.getInstance().setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {
            @Override
            public EaseUser getUser(String hxId) {
                ImUserInfo entity = DbHelper.getInstance().getUserInfoByHxId(hxId);
                if (entity != null) {
                    EaseUser easeUser = new EaseUser(hxId);
                    easeUser.setAvatar(entity.getProfile_image());
                    easeUser.setNick(entity.getUsername());
                    easeUser.setNickname(entity.getUsername());
                    easeUser.setGender(entity.getGender());
                    return easeUser;
                } else {
                    MemberInfoEntity memberInfoEntity = PaperUtils.getUserInfo();
                    if (memberInfoEntity != null) {
                        MemberInfoEntity.DataEntity dataEntity = memberInfoEntity.getData();
                        if (dataEntity != null && TextUtils.equals(dataEntity.getEasemob_account(), hxId)) {
                            EaseUser easeUser = new EaseUser(hxId);
                            easeUser.setAvatar(dataEntity.getProfile_image());
                            easeUser.setNick(dataEntity.getUsername());
                            easeUser.setNickname(dataEntity.getUsername());
                            easeUser.setGender(dataEntity.getGender());
                            return easeUser;
                        }
                    }
                }
                return null;
            }
        });
    }

    //初始化通知
    private void initNotify() {
        EaseUI.getInstance().getNotifier().setNotificationInfoProvider(new EaseNotifier.EaseNotificationInfoProvider() {
            private Context appContext = MainApp.getInstance();

            @Override
            public String getTitle(EMMessage message) {
                //you can update title here
                return null;
            }

            @Override
            public int getSmallIcon(EMMessage message) {
                //you can update icon here
                return 0;
            }

            @Override
            public String getDisplayedText(EMMessage message) {
                // be used on notification bar, different text according the message type.
                String ticker = EaseCommonUtils.getMessageDigest(message, appContext);
                if (message.getType() == EMMessage.Type.TXT) {
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }
                EaseUser user = EaseUI.getInstance().getUserProfileProvider().getUser(message.getFrom());
                if (user != null) {
                    return user.getNick() + ": " + ticker;
                } else {
                    return message.getFrom() + ": " + ticker;
                }
            }

            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                // here you can customize the text.
                // return fromUsersNum + "contacts send " + messageNum + "messages to you";
                return null;
            }

            @Override
            public Intent getLaunchIntent(EMMessage message) {
                // you can set what activity you want display when user click the notification
                Intent intent = new Intent(appContext, ChatActivity.class);
                EMMessage.ChatType chatType = message.getChatType();
                if (chatType == EMMessage.ChatType.Chat) { // single chat message
                    intent.putExtra("userId", message.getFrom());
                    intent.putExtra("chatType", EaseConstant.CHATTYPE_SINGLE);
                } else {
                    intent.putExtra("userId", message.getTo());
                    if (chatType == EMMessage.ChatType.GroupChat) {
                        intent.putExtra("chatType", EaseConstant.CHATTYPE_GROUP);
                    } else {
                        intent.putExtra("chatType", EaseConstant.CHATTYPE_CHATROOM);
                    }
                }
                return intent;
            }
        });
    }

    /**
     * get instance of EaseNotifier
     *
     * @return
     */
    public EaseNotifier getNotifier() {
        return EaseUI.getInstance().getNotifier();
    }

    /**
     * Global listener
     * If this event already handled by an activity, you don't need handle it again
     * activityList.size() <= 0 means all activities already in background or not in Activity Stack
     */
    protected void registerMessageListener() {
        EMMessageListener messageListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                try {
                    if (messages != null) {
                        for (int i = 0; i < messages.size(); i++) {
                            EMMessage message = messages.get(i);
                            // in background, do not refresh UI, notify it in notification bar
                            if (!EaseUI.getInstance().hasForegroundActivies()) {
                                getNotifier().onNewMsg(message);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {
                    //get message body
                    EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
                    final String action = cmdMsgBody.action();//获取自定义action
                }
            }

            @Override
            public void onMessageRead(List<EMMessage> list) {

            }

            @Override
            public void onMessageDelivered(List<EMMessage> list) {

            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
            }

            @Override
            public void onMessageRecalled(List<EMMessage> messages) {
                for (EMMessage msg : messages) {
                    if(msg.getChatType() == EMMessage.ChatType.GroupChat && EaseAtMessageHelper.get().isAtMeMsg(msg)){
                        EaseAtMessageHelper.get().removeAtMeGroup(msg.getTo());
                    }
                    EMMessage msgNotification = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                    EMTextMessageBody txtBody = new EMTextMessageBody(
                            String.format(MainApp.getInstance().getString(R.string.msg_recall_by_user), msg.getFrom()));
                    msgNotification.addBody(txtBody);
                    msgNotification.setFrom(msg.getFrom());
                    msgNotification.setTo(msg.getTo());
                    msgNotification.setUnread(false);
                    msgNotification.setMsgTime(msg.getMsgTime());
                    msgNotification.setLocalTime(msg.getMsgTime());
                    msgNotification.setChatType(msg.getChatType());
                    msgNotification.setAttribute(EaseConstant.MESSAGE_TYPE_RECALL, true);
                    EMClient.getInstance().chatManager().saveMessage(msgNotification);
                }
            }
        };

        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    /**
     * if ever logged in
     *
     * @return
     */
    public boolean isLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }

    //保存接收消息者用户信息
    public void saveReceiverEntity(String userId, String username,String hxId, String gender, String avatar) {
        ImUserInfo imUserInfoEntity = DbHelper.getInstance().getUserInfoByHxId(hxId);
        if (imUserInfoEntity == null)
            imUserInfoEntity = new ImUserInfo();
        imUserInfoEntity.setProfile_image(avatar);
        imUserInfoEntity.setUsername(username);
        imUserInfoEntity.setGender(gender);
        imUserInfoEntity.setEasemob_account(hxId);
        imUserInfoEntity.setUserId(userId);
        DbHelper.getInstance().addUserInfoEntity(imUserInfoEntity);
    }

    /**
     * logout
     *
     * @param unbindDeviceToken
     *            whether you need unbind your device token
     * @param callback
     *            callback
     */
    public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
        Log.d(TAG, "logout: " + unbindDeviceToken);
        EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "logout: onSuccess");
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onProgress(int progress, String status) {
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.d(TAG, "logout: onError");
                if (callback != null) {
                    callback.onError(code, error);
                }
            }
        });
    }

}
