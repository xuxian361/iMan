package com.sundy.iman.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.orhanobut.logger.Logger;
import com.sundy.iman.R;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.interfaces.OnBaseListener;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.ui.fragment.MainFragment;

public class MainActivity extends BaseActivity implements OnBaseListener {

    private static final String TAG = "MainActivity";
    private Fragment mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        init();
        switchContent(new MainFragment());
    }

    private void init() {
        //设置第一次启动APP标志，设置后表示启动过APP
        PaperUtils.setFirstLaunchApp();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Logger.e(TAG, "-------->onNewIntent");
        super.onNewIntent(intent);
        if (intent.getAction() == null) {
            Logger.e(TAG, "-------->null");
            UIHelper.jump(this, MainActivity.class);
            overridePendingTransition(R.anim.in_alpha, R.anim.out_alpha);
            finish();
        } else {
            Logger.e(TAG, "-------->not null");
            //其他逻辑
        }
    }

    @Override
    public void switchContent(Fragment fragment, int id) {
        Logger.i("------------->switchContent");
        try {
            if (fragment == null && mContent == fragment) {
                return;
            } else {
                mContent = fragment;
                if (fragment.isAdded()) {
                    getSupportFragmentManager().beginTransaction().show(fragment).commitAllowingStateLoss();
                } else {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(id, fragment)
                            .commitAllowingStateLoss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchContent(Fragment fragment) {
        Logger.i("------------->switchContent");
        switchContent(fragment, R.id.frameContent);
    }

    @Override
    public void addContent(Fragment fragment, int id) {
        Logger.i("------------->addContent");
        try {
            if (fragment == null && mContent == fragment) {
                return;
            } else {
                mContent = fragment;
                if (fragment.isAdded()) {
                    getSupportFragmentManager().beginTransaction().show(fragment).commitAllowingStateLoss();
                } else {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(id, fragment)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addContent(Fragment fragment) {
        Logger.i("------------->addContent");
        addContent(fragment, R.id.frameContent);
    }

    @Override
    public void onBack() {
        Logger.i("-------->onBack");
        try {
            int count = getSupportFragmentManager().getBackStackEntryCount();
            if (count > 0) {
                getSupportFragmentManager().popBackStack();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mContent = getSupportFragmentManager().findFragmentById(R.id.frameContent);
                    }
                }, 350);
            } else if (mContent instanceof MainFragment) {
                exitApp();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Logger.i("-------->onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            int count = getSupportFragmentManager().getBackStackEntryCount();
            if (count > 0) {
                getSupportFragmentManager().popBackStack();
                return true;
            }
            exitApp();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mContent != null) {
            mContent.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
