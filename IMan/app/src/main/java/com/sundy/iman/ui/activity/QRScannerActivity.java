package com.sundy.iman.ui.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.chat.EMClient;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.orhanobut.logger.Logger;
import com.sundy.iman.R;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.view.TitleBarView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sundy on 17/10/4.
 */

public class QRScannerActivity extends BaseActivity implements DecoratedBarcodeView.TorchListener {

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.btn_switch)
    Button switchLight;
    @BindView(R.id.dbv_custom)
    DecoratedBarcodeView mDBV;
    @BindView(R.id.tv_net_tips)
    TextView tvNetTips;

    private CaptureManager captureManager;
    private boolean isLightOn = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_qr_scanner);
        ButterKnife.bind(this);

        initTitle();

        mDBV.setTorchListener(this);

        // 如果没有闪光灯功能，就去掉相关按钮
        if (!hasFlash()) {
            switchLight.setVisibility(View.GONE);
        }

        //重要代码，初始化捕获
        captureManager = new CaptureManager(this, mDBV);
        captureManager.initializeFromIntent(getIntent(), savedInstanceState);
        captureManager.decode();

    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.scan));
        titleBar.setOnClickListener(new OnTitleBarClickListener() {
            @Override
            public void onLeftImgClick() {
                finish();
            }

            @Override
            public void onLeftTxtClick() {

            }

            @Override
            public void onRightImgClick() {

            }

            @Override
            public void onRightTxtClick() {

            }

            @Override
            public void onTitleClick() {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        captureManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EMClient.getInstance().addConnectionListener(connectionListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EMClient.getInstance().removeConnectionListener(connectionListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        captureManager.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        captureManager.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mDBV.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    // torch 手电筒
    @Override
    public void onTorchOn() {
        isLightOn = true;
    }

    @Override
    public void onTorchOff() {
        isLightOn = false;
    }

    // 判断是否有闪光灯功能
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    // 点击切换闪光灯
    @OnClick(R.id.btn_switch)
    public void switchLight() {
        if (isLightOn) {
            switchLight.setText(getString(R.string.light_on));
            mDBV.setTorchOff();
        } else {
            switchLight.setText(getString(R.string.light_off));
            mDBV.setTorchOn();
        }
    }

    private EMConnectionListener connectionListener = new EMConnectionListener() {
        @Override
        public void onDisconnected(int error) {
            Logger.e("------>onDisconnected");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    captureManager.onPause();
                    tvNetTips.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        public void onConnected() {
            Logger.e("------>onConnected");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvNetTips.setVisibility(View.GONE);
                    captureManager.onResume();
                }
            });
        }
    };

}
