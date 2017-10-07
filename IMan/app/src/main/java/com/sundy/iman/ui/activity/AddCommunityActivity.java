package com.sundy.iman.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.orhanobut.logger.Logger;
import com.sundy.iman.R;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.view.TitleBarView;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by sundy on 17/10/4.
 */

public class AddCommunityActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBarView titleBar;

    private static final int REQUEST_CODE_PERMISSION = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_add_community);
        ButterKnife.bind(this);

        initTitle();
    }

    private void initTitle() {
        titleBar.setBackMode();
        titleBar.setRightIvVisibility(View.VISIBLE);
        titleBar.setRightIvBg(R.mipmap.ic_launcher);
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
                AndPermission.with(AddCommunityActivity.this)
                        .requestCode(REQUEST_CODE_PERMISSION)
                        .permission(Permission.CAMERA)
                        .callback(AddCommunityActivity.this)
                        .start();
            }

            @Override
            public void onRightTxtClick() {

            }

            @Override
            public void onTitleClick() {

            }
        });
    }

    @PermissionYes(REQUEST_CODE_PERMISSION)
    private void getPermissionYes(@NonNull List<String> grantedPermissions) {
        Logger.e("权限申请成功!");
        goScannerCode();
    }

    @PermissionNo(REQUEST_CODE_PERMISSION)
    private void getPermissionNo(@NonNull List<String> deniedPermissions) {
        Logger.e("权限申请失败!");

    }

    //跳转扫描二维码
    private void goScannerCode() {
        new IntentIntegrator(this)
                .setOrientationLocked(true)
                .setPrompt(getResources().getString(R.string.scanner_promt))
                .setCaptureActivity(QRScannerActivity.class)
                .initiateScan(); // 初始化扫描
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Logger.e("-------->扫描失败");
            } else {
                // ScanResult 为 获取到的字符串
                String ScanResult = intentResult.getContents();
                Logger.e("-------->扫描成功 =" + ScanResult);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
