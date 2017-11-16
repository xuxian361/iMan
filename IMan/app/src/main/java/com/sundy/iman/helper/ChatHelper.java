package com.sundy.iman.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.domain.EaseAvatarOptions;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.EMLog;
import com.sundy.iman.BuildConfig;
import com.sundy.iman.MainApp;
import com.sundy.iman.R;
import com.sundy.iman.entity.MemberInfoEntity;
import com.sundy.iman.greendao.ImUserInfo;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.ui.activity.MainActivity;

import java.util.List;

/**
 * Created by sundy on 17/9/14.
 */

public class ChatHelper {

    private static final String TAG = "ChatHelper";
    private Context appContext;
    private static final ChatHelper ourInstance = new ChatHelper();
    private EMOptions emOptions;
    private EaseUI easeUI;

    public static ChatHelper getInstance() {
        return ourInstance;
    }

    public EMOptions getEmOptions() {
        if (emOptions == null) {
            emOptions = new EMOptions();
            emOptions.setAutoLogin(true);
            emOptions.setAcceptInvitationAlways(false);
            emOptions.setRequireAck(false);
            emOptions.setRequireDeliveryAck(false);
        }
        return emOptions;
    }

    //初始化环信
    public void init(Context context) {
        this.appContext = context;
        easeUI = EaseUI.getInstance();
        easeUI.init(context, getEmOptions());
        EMClient.getInstance().setDebugMode(BuildConfig.DEBUG);
        registerMessageListener();
        registerConnectionListener();
        initUIProvider();
        initUserProvider();
        initNotify();
    }

    private void initUIProvider() {
        //设置聊天用户头像为圆形
        EaseAvatarOptions avatarOptions = new EaseAvatarOptions();
        avatarOptions.setAvatarShape(1);
        easeUI.setAvatarOptions(avatarOptions);
    }

    private void registerConnectionListener() {
        EMConnectionListener connectionListener = new EMConnectionListener() {
            @Override
            public void onDisconnected(int error) {
                Log.e("global listener", "onDisconnect" + error);
                if (error == EMError.USER_REMOVED) {
                    onUserException(EaseConstant.ACCOUNT_REMOVED);
                } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    onUserException(EaseConstant.ACCOUNT_CONFLICT);
                } else if (error == EMError.SERVER_SERVICE_RESTRICTED) {
                    onUserException(EaseConstant.ACCOUNT_FORBIDDEN);
                } else if (error == EMError.USER_KICKED_BY_CHANGE_PASSWORD) {
                    onUserException(EaseConstant.ACCOUNT_KICKED_BY_CHANGE_PASSWORD);
                } else if (error == EMError.USER_KICKED_BY_OTHER_DEVICE) {
                    onUserException(EaseConstant.ACCOUNT_KICKED_BY_OTHER_DEVICE);
                }
            }

            @Override
            public void onConnected() {
                // in case group and contact were already synced, we supposed to notify sdk we are ready to receive the events
            }
        };
        EMClient.getInstance().addConnectionListener(connectionListener);
    }


    protected void onUserException(String exception) {
        EMLog.e(TAG, "onUserException: " + exception);
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.putExtra(exception, true);
        intent.setAction("hx_conflict");
        appContext.startActivity(intent);
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

    //通过用户ID 查找本地数据库
    public ImUserInfo getUserInfoByID(String user_id) {
        return DbHelper.getInstance().getUserInfoByUserId(user_id);
    }

    //初始化环信用户提供者
    private void initUserProvider() {
        easeUI.setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {
            @Override
            public EaseUser getUser(String hxId) {
                ImUserInfo entity = DbHelper.getInstance().getUserInfoByHxId(hxId);
                if (entity != null) {
                    EaseUser easeUser = new EaseUser(hxId);
                    easeUser.setAvatar(entity.getProfile_image());
                    easeUser.setNick(entity.getUsername());
                    easeUser.setNickname(entity.getUsername());
                    easeUser.setGender(entity.getGender());
                    easeUser.setMemberID(entity.getUserId());
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
                            easeUser.setMemberID(dataEntity.getId());
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
        easeUI.getNotifier().setNotificationInfoProvider(new EaseNotifier.EaseNotificationInfoProvider() {
            private Context appContext = MainApp.getInstance();

            @Override
            public String getTitle(EMMessage message) {
                //you can update title here
                return null;
            }

            @Override
            public int getSmallIcon(EMMessage message) {
                //you can update icon here
                return R.mipmap.ic_launcher;
            }

            @Override
            public String getDisplayedText(EMMessage message) {
                // be used on notification bar, different text according the message type.
                String ticker = EaseCommonUtils.getMessageDigest(message, appContext);
                if (message.getType() == EMMessage.Type.TXT) {
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }
                EaseUser user = easeUI.getUserProfileProvider().getUser(message.getFrom());
                if (user != null) {
                    return user.getNickname() + ": " + ticker;
                } else {
                    return ticker;
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
              /*  Intent intent = new Intent(appContext, ChatActivity.class);
                EMMessage.ChatType chatType = message.getChatType();
                if (chatType == EMMessage.ChatType.Chat) { // single chat message
                    intent.putExtra("easemod_id", message.getFrom());
                }
                return intent;*/
                Intent intent = new Intent(appContext, MainActivity.class);
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
        return easeUI.getNotifier();
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
                            if (!easeUI.hasForegroundActivies()) {
                                getNotifier().onNewMsg(message);
                            }

                            if (i == 0) {
                                Log.e(TAG, "----------------------------->");
                                String avatar = message.getStringAttribute(EaseConstant.CONS_ATTR_AVATAR, "");
                                String nickname = message.getStringAttribute(EaseConstant.CONS_ATTR_NICK_NAME, "");
                                String memberId = message.getStringAttribute(EaseConstant.CONS_ATTR_MEMBER_ID, "");
                                Log.e(TAG, "-------->message body =" + message.getBody().toString());
                                Log.e(TAG, "-------->user_avatar =" + avatar);
                                Log.e(TAG, "-------->name =" + nickname);
                                Log.e(TAG, "-------->memberId =" + memberId);
                                Log.e(TAG, "-----------------------------<");
                                ImUserInfo imUserInfoEntity = DbHelper.getInstance().getUserInfoByHxId(message.getFrom());
                                if (imUserInfoEntity == null) {
                                    imUserInfoEntity = new ImUserInfo();
                                }
                                imUserInfoEntity.setEasemob_account(message.getFrom());
                                imUserInfoEntity.setProfile_image(avatar);
                                imUserInfoEntity.setUserId(memberId);
                                imUserInfoEntity.setUsername(nickname);
                                DbHelper.getInstance().addUserInfoEntity(imUserInfoEntity);
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
                    if (msg.getChatType() == EMMessage.ChatType.GroupChat && EaseAtMessageHelper.get().isAtMeMsg(msg)) {
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

    /**
     * logout
     *
     * @param unbindDeviceToken whether you need unbind your device token
     * @param callback          callback
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


    public void pushActivity(Activity activity) {
        easeUI.pushActivity(activity);
    }

    public void popActivity(Activity activity) {
        easeUI.popActivity(activity);
    }


}
