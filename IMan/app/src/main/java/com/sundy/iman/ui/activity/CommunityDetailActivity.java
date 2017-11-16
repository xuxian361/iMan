package com.sundy.iman.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.orhanobut.logger.Logger;
import com.sundy.iman.BuildConfig;
import com.sundy.iman.MainApp;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.CommunityInfoEntity;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.utils.DateUtils;
import com.sundy.iman.utils.FileUtils;
import com.sundy.iman.utils.NetWorkUtils;
import com.sundy.iman.utils.QRCodeUtils;
import com.sundy.iman.utils.cache.CacheData;
import com.sundy.iman.view.TitleBarView;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sundy on 17/10/20.
 */

public class CommunityDetailActivity extends BaseActivity {

    private final int REQUEST_CODE_PERMISSION_STORAGE = 100;

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
    @BindView(R.id.tv_introduction)
    TextView tvIntroduction;

    private String community_id;
    private String type;
    private String qrCodePath;

    private CommunityInfoEntity.DataEntity dataEntity;

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
                getFilePermission();
            }

            @Override
            public void onRightTxtClick() {

            }

            @Override
            public void onTitleClick() {

            }
        });
    }

    @PermissionYes(REQUEST_CODE_PERMISSION_STORAGE)
    private void getPermissionStorageYes(@NonNull List<String> grantedPermissions) {
        Logger.e("文件操作权限申请成功!");
        sendEmail();
    }

    @PermissionNo(REQUEST_CODE_PERMISSION_STORAGE)
    private void getPermissionStorageNo(@NonNull List<String> deniedPermissions) {
        Logger.e("文件操作权限申请失败!");

    }

    //获取操作文件权限
    private void getFilePermission() {
        if (!NetWorkUtils.isNetAvailable(this)) {
            MainApp.getInstance().showToast(getString(R.string.network_not_available));
            return;
        }
        AndPermission.with(this)
                .requestCode(REQUEST_CODE_PERMISSION_STORAGE)
                .permission(Permission.STORAGE)
                .callback(this)
                .start();
    }

    //发送邮件
    private void sendEmail() {
        if (dataEntity == null) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] tos = {""};
        String body = getString(R.string.app_name) + " " + getString(R.string.invite_you) + " " + dataEntity.getName() + "\n\n" + Constants.DOWNLOAD_LINK;
        String subject = getString(R.string.app_name) + " " + getString(R.string.invitation);
        String path = "";

        intent.putExtra(Intent.EXTRA_EMAIL, tos);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.setType("image/*");
        intent.setType("message/rfc882");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //截取二维码
            ivQrCode.setDrawingCacheEnabled(true);
            ivQrCode.buildDrawingCache();
            Bitmap bitmap = ivQrCode.getDrawingCache();
            if (bitmap != null) {
                //保存二维码到SD card
                qrCodePath = FileUtils.saveBitmapToSD(Environment.getExternalStorageDirectory().getPath(), bitmap);

                if (!TextUtils.isEmpty(qrCodePath)) {
                    path = qrCodePath;

                    File file = new File(path);
                    Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", file);
                    intent.putExtra(Intent.EXTRA_STREAM, contentUri);
                }
            }
        } else {
            //截取二维码
            ivQrCode.setDrawingCacheEnabled(true);
            ivQrCode.buildDrawingCache();
            Bitmap bitmap = ivQrCode.getDrawingCache();
            if (bitmap != null) {
                //保存二维码到SD card
                qrCodePath = FileUtils.saveBitmapToSD(bitmap);

                if (!TextUtils.isEmpty(qrCodePath)) {
                    path = qrCodePath;
                    if (!qrCodePath.startsWith("file://"))
                        path = "file://" + qrCodePath;
                }

                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
            }
        }

        Intent.createChooser(intent, getString(R.string.choose_email_client));
        startActivity(intent);
    }

    //社区详情
    private void getCommunityInfo() {
        final boolean hasPermission = AndPermission.hasPermission(this, Permission.STORAGE);
        if (hasPermission) {
            dataEntity = CacheData.getInstance().getCommunityInfo(community_id);
            if (dataEntity != null) {
                titleBar.setRightIvVisibility(View.VISIBLE);
                showData(dataEntity);
            }
        }

        if (NetWorkUtils.isNetAvailable(this)) {
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
                                if (hasPermission) {
                                    CacheData.getInstance().saveCommunityInfo(community_id, dataEntity);
                                }
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
    }

    private void showData(CommunityInfoEntity.DataEntity dataEntity) {
        tvCommunityName.setText(dataEntity.getName());
        tvCommunityId.setText(getString(R.string.id_str) + " " + dataEntity.getId());
        tvIntroduction.setText(dataEntity.getIntroduction());

        String create_time = dataEntity.getCreate_time();
        if (create_time != null) {
            Date date = DateUtils.formatTimeStamp2Date(Long.parseLong(create_time) * 1000);
            tvCreateDate.setText(getString(R.string.since) + " " + DateUtils.formatDate2String(date, "yyyy/MM/dd"));
        } else {
            tvCreateDate.setText("");
        }

        if (type.equals("1")) //普通社区
        {
            tvAcquiredUesrs.setText(getString(R.string.members) + " " + dataEntity.getMembers());
        } else { //推广社区
            tvAcquiredUesrs.setText(getString(R.string.acquired_users) + " " + dataEntity.getMembers());
        }

        String url = dataEntity.getUrl();
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        try {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!TextUtils.isEmpty(qrCodePath)) {
            File file = new File(qrCodePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @OnClick(R.id.iv_qr_code)
    public void onViewClicked() {
        showScaleQRCode();
    }

    //显示放大的QR code
    private void showScaleQRCode() {
        if (dataEntity == null)
            return;
        Bundle bundle = new Bundle();
        bundle.putString("url", dataEntity.getUrl());
        UIHelper.jump(this, ScaleQRCodeActivity.class, bundle);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }
}
