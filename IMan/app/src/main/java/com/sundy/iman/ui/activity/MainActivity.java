package com.sundy.iman.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.orhanobut.logger.Logger;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.AppVersionEntity;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.interfaces.OnBaseListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.ui.fragment.MainFragment;
import com.sundy.iman.view.dialog.CommonDialog;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements OnBaseListener {

    private static final String TAG = "MainActivity";
    private Fragment mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        init();
        switchContent(new MainFragment());
        updateVersion();
    }

    private void init() {
        //设置第一次启动APP标志，设置后表示启动过APP
        PaperUtils.setFirstLaunchApp();
        //设置App 语言
        PaperUtils.setLanguage(PaperUtils.getLanguage());
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
                            //保存App 版本信息
                            PaperUtils.setAppVersion(appVersionEntity);
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
            if (forced_update.equals("1")) {
                final CommonDialog dialog = new CommonDialog(this);
                dialog.getTitle().setText(getString(R.string.a_new_version) + version);
                dialog.getContent().setText(description);
                dialog.getBtnCancel().setVisibility(View.GONE);
                dialog.setCancelable(false);
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

    //跳转下载链接
    private void goDownloadUrl(String download_url) {
        if (TextUtils.isEmpty(download_url))
            return;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(download_url));
        startActivity(intent);
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
