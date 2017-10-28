package com.sundy.iman.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.sundy.iman.R;
import com.sundy.iman.utils.QRCodeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sundy on 17/10/21.
 */

public class ScaleQRCodeActivity extends BaseActivity {

    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;
    @BindView(R.id.rel_content)
    RelativeLayout relContent;
    private String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_scale_qr_code);
        ButterKnife.bind(this);

        initData();
        init();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString("url");
        }
    }

    private void init() {
        try {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

            Bitmap qrCode = QRCodeUtils.createQRCode(url, bmp, BarcodeFormat.QR_CODE);
            ivQrCode.setImageBitmap(qrCode);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        brightScreen();
    }

    //调亮屏幕
    private void brightScreen() {
        Window localWindow = getWindow();
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
        float f = 230 / 255.0F;
        localLayoutParams.screenBrightness = f;
        localWindow.setAttributes(localLayoutParams);
    }

    @OnClick(R.id.rel_content)
    public void onViewClicked() {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }
}
