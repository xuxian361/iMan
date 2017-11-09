package com.sundy.iman.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.util.EasyUtils;
import com.orhanobut.logger.Logger;
import com.sundy.iman.R;
import com.sundy.iman.entity.MemberInfoEntity;
import com.sundy.iman.greendao.ImUserInfo;
import com.sundy.iman.helper.DbHelper;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.ui.fragment.ChatFragment;
import com.sundy.iman.utils.permission_utils.PermissionsManager;

/**
 * Created by sundy on 17/10/4.
 */

public class ChatActivity extends BaseActivity implements ChatFragment.EaseChatFragmentHelper {
    private ChatFragment chatFragment;
    private String easemod_id;
    private String user_id;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.act_chat);

        initData();
        init();

    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            easemod_id = bundle.getString("easemod_id");
            user_id = bundle.containsKey("user_id") ? bundle.getString("user_id") : "";
        }
        Logger.e("------>easemod_id = " + easemod_id);
        Logger.e("------>user_id = " + user_id);
    }

    private void init() {
        chatFragment = new ChatFragment();
        //pass parameters to chat fragment
        Bundle bundle = new Bundle();
        bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        bundle.putString(EaseConstant.EXTRA_USER_ID, easemod_id);
        bundle.putString(EaseConstant.EXTRA_MEMBER_ID, user_id);
        chatFragment.setArguments(bundle);
        chatFragment.setChatFragmentHelper(this);
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // make sure only one chat activity is opened
        String easemod_id = intent.getStringExtra("easemod_id");
        if (easemod_id.equals(easemod_id))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        chatFragment.onBackPressed();
        if (EasyUtils.isSingleActivity(this)) {
            UIHelper.jump(this, MainActivity.class);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    @Override
    public void onSetMessageAttributes(EMMessage message) {
        Logger.e("------>onSetMessageAttributes: easemod_id= " + easemod_id);
        //发送自己的信息
        MemberInfoEntity memberInfoEntity = PaperUtils.getUserInfo();
        if (memberInfoEntity == null)
            return;
        MemberInfoEntity.DataEntity user = memberInfoEntity.getData();
        if (user != null) {
            String photo = user.getProfile_image();
            String username = user.getUsername();
            String memberId = user.getId();
            Logger.e("---------------------------------->user");
            Logger.e("-------->user_avatar =" + photo);
            Logger.e("-------->username =" + username);
            Logger.e("-------->memberId =" + memberId);
            Logger.e("---------------------------------->");
            message.setAttribute(EaseConstant.CONS_ATTR_AVATAR, photo);
            message.setAttribute(EaseConstant.CONS_ATTR_NICK_NAME, username);
            message.setAttribute(EaseConstant.CONS_ATTR_MEMBER_ID, memberId);
        }
    }

    @Override
    public void onEnterToChatDetails() {

    }

    @Override
    public void onAvatarClick(String username) {
        ImUserInfo imUserInfoEntity = DbHelper.getInstance().getUserInfoByHxId(username);
        if (imUserInfoEntity != null) {
            goUserDetail(imUserInfoEntity.getUserId());
        }
    }

    @Override
    public void onAvatarLongClick(String username) {

    }

    @Override
    public boolean onMessageBubbleClick(EMMessage message) {
        return false;
    }

    @Override
    public void onMessageBubbleLongClick(EMMessage message) {

    }

    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
        return false;
    }

    @Override
    public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
        return null;
    }

    //跳转用户详情
    private void goUserDetail(String profile_id) {
        String meID = PaperUtils.getMId();
        if (meID.equals(profile_id)) {
            UIHelper.jump(this, EditProfileActivity.class);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("profile_id", profile_id);
            bundle.putString("type", "1");
            bundle.putString("goal_id", "");
            UIHelper.jump(this, ContactInfoActivity.class, bundle);
        }
    }

}
