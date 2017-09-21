package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import com.sundy.iman.R;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.paperdb.PaperUtils;

/**
 * Created by sundy on 17/9/14.
 */

public class LoadingActivity extends BaseActivity {

    private int second = 2; //倒计时2秒
    private final int MSG_START = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_START) {
                second--;
                if (second == 0) {
                    goMain();
                } else {
                    startHandler();
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_loading);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!PaperUtils.isFirstLaunchApp()) {
            goGuide();
        } else {
            startHandler();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopHandler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            stopHandler();
            mHandler = null;
        }
    }

    //开启倒计时
    private void startHandler() {
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(MSG_START, 1000);
        }
    }

    //停止倒计时
    private void stopHandler() {
        if (mHandler != null) {
            mHandler.removeMessages(MSG_START);
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

}
