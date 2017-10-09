package com.sundy.iman.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.AppVersionEntity;
import com.sundy.iman.entity.ChangeLanguageEntity;
import com.sundy.iman.entity.LogoutEntity;
import com.sundy.iman.entity.StaticContentEntity;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
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
            btnLogout.setVisibility(View.GONE);
        } else {
            btnLogout.setVisibility(View.VISIBLE);
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
                changeLanguage();
                break;
            case R.id.rel_term_of_use:
                getStaticContent(1);
                break;
            case R.id.rel_privacy:
                getStaticContent(2);
                break;
            case R.id.rel_contact_us:
                getStaticContent(3);
                break;
            case R.id.rel_version:
                updateVersion();
                break;
            case R.id.btn_logout:
                logout();
                break;
        }
    }

    //获取静态内容
    private void getStaticContent(int type) { //类型:1-使用条款，2-隐私条例，3-联系我们
        Map<String, String> param = new HashMap<>();
        param.put("mid", "");
        param.put("session_key", "");
        param.put("type", type + "");
        Call<StaticContentEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .getStaticContent(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<StaticContentEntity>() {
            @Override
            public void onSuccess(Call<StaticContentEntity> call, Response<StaticContentEntity> response) {
                StaticContentEntity staticContentEntity = response.body();
                if (staticContentEntity != null) {
                    int code = staticContentEntity.getCode();
                    if (code == Constants.CODE_SUCCESS) {
                        StaticContentEntity.DataEntity dataEntity = staticContentEntity.getData();
                        if (dataEntity != null) {
                            goWebView(dataEntity);
                        }
                    }
                }
            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<StaticContentEntity> call, Throwable t) {

            }
        });
    }

    //跳转Web View显示H5
    private void goWebView(StaticContentEntity.DataEntity dataEntity) {
        Bundle bundle = new Bundle();
        bundle.putString("url", dataEntity.getUrl());
        UIHelper.jump(this, WebActivity.class, bundle);
    }

    //更新版本
    private void updateVersion() {
        Map<String, String> param = new HashMap<>();
        Call<AppVersionEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .getAppVersion(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<AppVersionEntity>() {
            @Override
            public void onSuccess(Call<AppVersionEntity> call, Response<AppVersionEntity> response) {
                AppVersionEntity appVersionEntity = response.body();
                if (appVersionEntity != null) {
                    int code = appVersionEntity.getCode();
                    if (code == Constants.CODE_SUCCESS) {
                        AppVersionEntity.DataEntity dataEntity = appVersionEntity.getData();
                        if (dataEntity != null) {
                            showUpdateDialog(dataEntity);
                        }
                    }
                }
            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<AppVersionEntity> call, Throwable t) {

            }
        });
    }

    //显示版本更新Dialog
    private void showUpdateDialog(AppVersionEntity.DataEntity dataEntity) {
        String version = dataEntity.getVersion();
        String description = dataEntity.getDescription();
        final String download_url = dataEntity.getDownload_url();
        String is_update = dataEntity.getIs_update(); //是否提示更新:1-是，0-否
        String forced_update = dataEntity.getForced_update(); //是否强制升级:1-是，0-否

        if (is_update.equals("1")) {
            final CommonDialog dialog = new CommonDialog(this);
            dialog.getTitle().setText(version);
            dialog.getContent().setText(description);
            if (forced_update.equals("1")) {
                dialog.getBtnCancel().setVisibility(View.GONE);
                dialog.setCancelable(false);
            }
            dialog.setOnBtnClick(new CommonDialog.OnBtnClick() {
                @Override
                public void onOkClick() {
                    dialog.dismiss();
                    goDownloadUrl(download_url);
                }
            });
        }
    }

    //跳转下载链接
    private void goDownloadUrl(String download_url) {
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

    //修改语言接口
    private void changeLanguage() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", "");
        param.put("session_key", "");
        Call<ChangeLanguageEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .changeLanguage(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<ChangeLanguageEntity>() {
            @Override
            public void onSuccess(Call<ChangeLanguageEntity> call, Response<ChangeLanguageEntity> response) {
                ChangeLanguageEntity changeLanguageEntity = response.body();
                if (changeLanguageEntity != null) {
                    int code = changeLanguageEntity.getCode();
                    Logger.e("------>code = " + code);
                }
            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<ChangeLanguageEntity> call, Throwable t) {

            }
        });
    }

    //登出
    private void logout() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", "");
        param.put("session_key", "");
        Call<LogoutEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .logout(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<LogoutEntity>() {
            @Override
            public void onSuccess(Call<LogoutEntity> call, Response<LogoutEntity> response) {
                LogoutEntity logoutEntity = response.body();
                if (logoutEntity != null) {
                    int code = logoutEntity.getCode();
                    Logger.e("------>code = " + code);
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

}
