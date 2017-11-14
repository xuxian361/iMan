package com.sundy.iman.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.sundy.iman.MainApp;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.AppVersionEntity;
import com.sundy.iman.entity.LogoutEntity;
import com.sundy.iman.helper.ChatHelper;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.utils.FileUtils;
import com.sundy.iman.utils.NetWorkUtils;
import com.sundy.iman.utils.cache.CacheUtils;
import com.sundy.iman.view.TitleBarView;
import com.sundy.iman.view.dialog.CommonDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sundy on 17/10/4.
 */

public class SettingsActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.tv_set_transfer_pwd)
    TextView tvSetTransferPwd;
    @BindView(R.id.rel_set_transfer_pwd)
    RelativeLayout relSetTransferPwd;
    @BindView(R.id.tv_language)
    TextView tvLanguage;
    @BindView(R.id.rel_language)
    RelativeLayout relLanguage;
    @BindView(R.id.tv_term_of_use)
    TextView tvTermOfUse;
    @BindView(R.id.rel_term_of_use)
    RelativeLayout relTermOfUse;
    @BindView(R.id.tv_privacy)
    TextView tvPrivacy;
    @BindView(R.id.rel_privacy)
    RelativeLayout relPrivacy;
    @BindView(R.id.tv_contact_us)
    TextView tvContactUs;
    @BindView(R.id.rel_contact_us)
    RelativeLayout relContactUs;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.rel_version)
    RelativeLayout relVersion;
    @BindView(R.id.btn_logout)
    TextView btnLogout;
    @BindView(R.id.iv_dot_version)
    ImageView ivDotVersion;
    @BindView(R.id.tv_version_value)
    TextView tvVersionValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_settings);
        ButterKnife.bind(this);

        initTitle();
    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.settings));
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
    protected void onResume() {
        super.onResume();
        if (PaperUtils.isLogin()) {
            btnLogout.setVisibility(View.VISIBLE);
        } else {
            btnLogout.setVisibility(View.GONE);
        }
        showVersion();
    }

    //显示版本
    private void showVersion() {
        AppVersionEntity appVersionEntity = PaperUtils.getAppVersion();
        if (appVersionEntity != null) {
            AppVersionEntity.DataEntity dataEntity = appVersionEntity.getData();
            if (dataEntity != null) {
                String version = dataEntity.getVersion();
                String description = dataEntity.getDescription();
                final String download_url = dataEntity.getDownload_url();
                String is_update = dataEntity.getIs_update(); //是否提示更新:1-是，0-否
                String forced_update = dataEntity.getForced_update(); //是否强制升级:1-是，0-否
                tvVersionValue.setText(version);
                if (is_update.equals("1")) {
                    ivDotVersion.setVisibility(View.VISIBLE);
                } else {
                    ivDotVersion.setVisibility(View.GONE);
                }
            }
        }
    }

    @OnClick({R.id.rel_set_transfer_pwd, R.id.rel_language, R.id.rel_term_of_use, R.id.rel_privacy,
            R.id.rel_contact_us, R.id.rel_version, R.id.btn_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rel_set_transfer_pwd:
                goSetTransferPwd();
                break;
            case R.id.rel_language:
                goChangeLanguage();
                break;
            case R.id.rel_term_of_use:
                goWebView(Constants.TYPE_TERMS_OF_CONDITION);
                break;
            case R.id.rel_privacy:
                goWebView(Constants.TYPE_PRIVACY);
                break;
            case R.id.rel_contact_us:
                goWebView(Constants.TYPE_CONTACT_US);
                break;
            case R.id.rel_version:
                showUpdateDialog();
                break;
            case R.id.btn_logout:
                logout();
                break;
        }
    }

    //跳转切换语言
    private void goChangeLanguage() {
        UIHelper.jump(this, ChangeLanguageActivity.class);
    }

    //跳转Web View显示H5
    private void goWebView(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt("static_content_type", type);
        UIHelper.jump(this, WebActivity.class, bundle);
    }

    //显示版本更新Dialog
    private void showUpdateDialog() {
        AppVersionEntity appVersionEntity = PaperUtils.getAppVersion();
        if (appVersionEntity != null) {
            AppVersionEntity.DataEntity dataEntity = appVersionEntity.getData();
            if (dataEntity != null) {
                String version = dataEntity.getVersion();
                String description = dataEntity.getDescription();
                final String download_url = dataEntity.getDownload_url();
                String is_update = dataEntity.getIs_update(); //是否提示更新:1-是，0-否
                String forced_update = dataEntity.getForced_update(); //是否强制升级:1-是，0-否
                if (is_update.equals("1")) {
                    final CommonDialog dialog = new CommonDialog(this);
                    dialog.getTitle().setText(getString(R.string.a_new_version) + version);
                    dialog.getContent().setText(description);
                    dialog.setOnBtnClick(new CommonDialog.OnBtnClick() {
                        @Override
                        public void onOkClick() {
                            dialog.dismiss();
                            goDownloadUrl(download_url);
                        }
                    });
                }
            }
        }
    }

    //跳转下载链接
    private void goDownloadUrl(String download_url) {
        if (TextUtils.isEmpty(download_url))
            return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(download_url));
        startActivity(intent);
    }

    //跳转设置交易密码
    private void goSetTransferPwd() {
        if (PaperUtils.isLogin()) {
            UIHelper.jump(this, SetTransferPwdActivity.class);
        } else {
            UIHelper.jump(this, LoginActivity.class);
        }
    }

    //登出
    private void logout() {
        if (!NetWorkUtils.isNetAvailable(this)) {
            MainApp.getInstance().showToast(getString(R.string.network_not_available));
            return;
        }
        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        Call<LogoutEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .logout(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<LogoutEntity>() {
            @Override
            public void onSuccess(Call<LogoutEntity> call, Response<LogoutEntity> response) {
                LogoutEntity logoutEntity = response.body();
                if (logoutEntity != null) {
                    int code = logoutEntity.getCode();
                    String msg = logoutEntity.getMsg();
                    //清除登录用户本地信息
                    PaperUtils.clearUserInfo();
                    PaperUtils.clearPostReadRecord();
                    //清除缓存
                    FileUtils.clearFileCache(FileUtils.getHttpCache());
                    boolean clearFinish = CacheUtils.getInstance().clear();
                    Logger.e("------清除缓存是否成功:" + clearFinish);

                    //登出环信
                    ChatHelper.getInstance().logout(false, null);

                    if (code == Constants.CODE_SUCCESS) {

                    } else {
                        MainApp.getInstance().showToast(msg);
                    }
                    btnLogout.setVisibility(View.GONE);

                    goLogin();
                }
            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<LogoutEntity> call, Throwable t) {

            }
        });
    }

    //跳转登录
    private void goLogin() {
        UIHelper.jump(this, LoginActivity.class);
    }

}
