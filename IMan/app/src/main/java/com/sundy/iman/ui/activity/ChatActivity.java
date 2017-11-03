package com.sundy.iman.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.util.EasyUtils;
import com.sundy.iman.R;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.utils.permission_utils.PermissionsManager;

/**
 * Created by sundy on 17/10/4.
 */

public class ChatActivity extends BaseActivity {
    private EaseChatFragment chatFragment;
    private String userId;
    private String easemod_id;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.act_chat);

        initData();
        init();

    }

    private void init() {
        chatFragment = new EaseChatFragment();
        //pass parameters to chat fragment
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = getIntent().getExtras().getString("userId");
            easemod_id = getIntent().getExtras().getString("easemod_id");
        }
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
}
