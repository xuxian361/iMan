package com.sundy.iman.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.sundy.iman.MainApp;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.CommunityInfoEntity;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.utils.DateUtils;
import com.sundy.iman.utils.FileUtils;
import com.sundy.iman.utils.QRCodeUtils;
import com.sundy.iman.view.TitleBarView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sundy on 17/10/20.
 */

public class CommunityDetailActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;
    @BindView(R.id.tv_community_name)
    TextView tvCommunityName;
    @BindView(R.id.tv_community_id)
    TextView tvCommunityId;
    @BindView(R.id.tv_create_date)
    TextView tvCreateDate;
    @BindView(R.id.tv_acquired_uesrs)
    TextView tvAcquiredUesrs;

    private String community_id;
    private String type;

    private CommunityInfoEntity.DataEntity dataEntity;
    private String qrCodePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_community_detail);
        ButterKnife.bind(this);

        initData();
        initTitle();
        getCommunityInfo();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            community_id = bundle.getString("community_id");
            type = bundle.getString("type");
        }
    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.promoting));
        titleBar.setRightIvVisibility(View.GONE);
        titleBar.setRightIvBg(R.mipmap.icon_send_blue);
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
                sendEmail();
            }

            @Override
            public void onRightTxtClick() {

            }

            @Override
            public void onTitleClick() {

            }
        });
    }

    //发送邮件
    private void sendEmail() {
        if (dataEntity == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] tos = {""};
        String body = getString(R.string.app_name) + " " + getString(R.string.invite_you) + " " + dataEntity.getName();
        String subject = getString(R.string.app_name) + " " + getString(R.string.invitation);
        if (!TextUtils.isEmpty(qrCodePath)) {
            if (!qrCodePath.startsWith("file://"))
                qrCodePath = "file://" + qrCodePath;
        }

        intent.putExtra(Intent.EXTRA_EMAIL, tos);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(qrCodePath));
        intent.setType("image/*");
        intent.setType("message/rfc882");
        Intent.createChooser(intent, getString(R.string.choose_email_client));
        startActivity(intent);
    }

    //社区详情
    private void getCommunityInfo() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("community_id", community_id); //社区ID
        param.put("type", type); //类型: 1-普通社区，2-推广社区
        Call<CommunityInfoEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .getCommunityInfo(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<CommunityInfoEntity>() {
            @Override
            public void onSuccess(Call<CommunityInfoEntity> call, Response<CommunityInfoEntity> response) {
                CommunityInfoEntity communityInfoEntity = response.body();
                if (communityInfoEntity != null) {
                    int code = communityInfoEntity.getCode();
                    String msg = communityInfoEntity.getMsg();
                    if (code == Constants.CODE_SUCCESS) {
                        dataEntity = communityInfoEntity.getData();
                        if (dataEntity != null) {
                            titleBar.setRightIvVisibility(View.VISIBLE);
                            showData(dataEntity);
                        }
                    } else {
                        MainApp.getInstance().showToast(msg);
                    }
                }
            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<CommunityInfoEntity> call, Throwable t) {

            }
        });
    }

    private void showData(CommunityInfoEntity.DataEntity dataEntity) {
        tvCommunityName.setText(dataEntity.getName());
        tvCommunityId.setText(getString(R.string.id_str) + ": " + dataEntity.getId());

        String create_time = dataEntity.getCreate_time();
        if (create_time != null) {
            Date date = DateUtils.formatTimeStamp2Date(Long.parseLong(create_time) * 1000);
            tvCreateDate.setText(getString(R.string.since) + DateUtils.formatDate2String(date, "yyyy/MM/dd"));
        } else {
            tvCreateDate.setText("");
        }

        tvAcquiredUesrs.setText(getString(R.string.acquired_users) + " " + dataEntity.getMembers());

        String url = dataEntity.getUrl();
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        try {
            Bitmap qrCode = QRCodeUtils.createQRCode(url, bmp, BarcodeFormat.QR_CODE);
            ivQrCode.setImageBitmap(qrCode);

            //保存二维码到SD card
            qrCodePath = FileUtils.saveBitmapToSD(qrCode);
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
}