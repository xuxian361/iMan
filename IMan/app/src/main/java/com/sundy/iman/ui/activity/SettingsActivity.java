package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sundy.iman.R;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.view.TitleBarView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    Button btnLogout;

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

    @OnClick({R.id.rel_set_transfer_pwd, R.id.rel_language, R.id.rel_term_of_use, R.id.rel_privacy, R.id.rel_contact_us, R.id.rel_version, R.id.btn_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rel_set_transfer_pwd:
                goSetTransferPwd();
                break;
            case R.id.rel_language:
                break;
            case R.id.rel_term_of_use:
                break;
            case R.id.rel_privacy:
                break;
            case R.id.rel_contact_us:
                break;
            case R.id.rel_version:
                break;
            case R.id.btn_logout:
                break;
        }
    }

    //跳转设置交易密码
    private void goSetTransferPwd() {
        if (PaperUtils.isLogin()) {
            UIHelper.jump(this, SetTransferPwdActivity.class);
        } else {
            UIHelper.jump(this, LoginActivity.class);
        }
    }
}
