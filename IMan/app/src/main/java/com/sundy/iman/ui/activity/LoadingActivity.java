package com.sundy.iman.ui.activity;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;

import com.orhanobut.logger.Logger;
import com.sundy.iman.R;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;

import java.util.List;

/**
 * Created by sundy on 17/9/14.
 */

public class LoadingActivity extends BaseActivity {

    private final static int MSG_GO_INTENT = 1;
    private final static int MSG_GO_GUIDE = 2;
    private final static long DELAY_TIME = 3000;
    private Handler handler;
    private static final int REQUEST_CODE_PERMISSION = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_loading);

        AndPermission.with(this)
                .requestCode(REQUEST_CODE_PERMISSION)
                .permission(Manifest.permission.READ_PHONE_STATE)
                .callback(this)
                .start();
    }

    /**
     * <p>权限全部申请成功才会回调这个方法，否则回调失败的方法。</p>
     *
     * @param grantedPermissions AndPermission回调过来的申请成功的权限。
     */
    @PermissionYes(REQUEST_CODE_PERMISSION)
    private void getPermissionYes(@NonNull List<String> grantedPermissions) {
        Logger.e("权限申请成功!");
        PaperUtils.setDeviceId();
        goIndex();
    }

    /**
     * <p>只要有一个权限申请失败就会回调这个方法，并且不会回调成功的方法。</p>
     *
     * @param deniedPermissions AndPermission回调过来的申请失败的权限。
     */
    @PermissionNo(REQUEST_CODE_PERMISSION)
    private void getPermissionNo(@NonNull List<String> deniedPermissions) {
        Logger.e("权限申请失败01!");
        goIndex();
    }

    private void goIndex() {
        if (handler == null) {
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case MSG_GO_INTENT:
                            goMain();
                            break;
                        case MSG_GO_GUIDE:
                            goGuide();
                            break;
                    }
                }
            };
        }
        if (!PaperUtils.isFirstLaunchApp()) {
            handler.sendEmptyMessageDelayed(MSG_GO_GUIDE, DELAY_TIME);
        } else {
            handler.sendEmptyMessageDelayed(MSG_GO_INTENT, DELAY_TIME);
        }
    }

    //跳转主页
    private void goMain() {
        UIHelper.jump(this, MainActivity.class);
        finish();
    }

    //跳转引导页
    private void goGuide() {
        UIHelper.jump(this, GuideActivity.class);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (handler != null) {
                handler.removeMessages(MSG_GO_INTENT);
                handler = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
