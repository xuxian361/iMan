package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.sundy.iman.MainApp;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.MemberInfoEntity;
import com.sundy.iman.helper.ImageHelper;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.view.TitleBarView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sundy on 17/10/4.
 */

public class ContactInfoActivity extends BaseActivity {


    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.iv_header)
    CircleImageView ivHeader;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.iv_gender)
    ImageView ivGender;
    @BindView(R.id.ll_name)
    LinearLayout llName;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_introduction)
    TextView tvIntroduction;
    @BindView(R.id.switch_notification)
    Switch switchNotification;
    @BindView(R.id.btn_chat)
    TextView btnChat;

    private String profile_id;
    private MemberInfoEntity.DataEntity dataEntity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_contact_info);
        ButterKnife.bind(this);

        initData();
        initTitle();
        init();
        getMemberInfo();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            profile_id = bundle.getString("profile_id");
        }
    }

    private void initTitle() {
        titleBar.setBackMode();
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

    private void init() {
        switchNotification.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {

            } else {

            }
        }
    };

    //获取个人用户信息
    private void getMemberInfo() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("profile_id", profile_id);
        showProgress();
        Call<MemberInfoEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .getMemberInfo(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<MemberInfoEntity>() {
            @Override
            public void onSuccess(Call<MemberInfoEntity> call, Response<MemberInfoEntity> response) {
                MemberInfoEntity memberInfoEntity = response.body();
                if (memberInfoEntity != null) {
                    int code = memberInfoEntity.getCode();
                    String msg = memberInfoEntity.getMsg();
                    if (code == Constants.CODE_SUCCESS) {
                        dataEntity = memberInfoEntity.getData();
                        if (dataEntity != null) {
                            showData(dataEntity);
                        }
                    } else {
                        MainApp.getInstance().showToast(msg);
                    }
                }
            }

            @Override
            public void onAfter() {
                hideProgress();
            }

            @Override
            public void onFailure(Call<MemberInfoEntity> call, Throwable t) {

            }
        });
    }

    private void showData(MemberInfoEntity.DataEntity dataEntity) {
        if (dataEntity != null) {
            String username = dataEntity.getUsername();
            if (TextUtils.isEmpty(username)) {
                tvUsername.setText(getString(R.string.iman));
            } else {
                tvUsername.setText(username);
            }
            String location = dataEntity.getLocation();
            if (TextUtils.isEmpty(location)) {
                tvLocation.setText(getString(R.string.location_default));
            } else {
                String country = dataEntity.getCountry();
                String province = dataEntity.getProvince();
                String city = dataEntity.getCity();

                if (!TextUtils.isEmpty(province) && !TextUtils.isEmpty(city)) {
                    tvLocation.setText(province + "  " + city);
                } else if (TextUtils.isEmpty(province) && TextUtils.isEmpty(city)) {
                    tvLocation.setText(country);
                } else {
                    if (TextUtils.isEmpty(city)) {
                        tvLocation.setText(country + " " + province);
                    } else {
                        tvLocation.setText(country + " " + city);
                    }
                }
            }
            String introduction = dataEntity.getIntroduction();
            if (TextUtils.isEmpty(introduction)) {
                tvIntroduction.setText(getString(R.string.introduction_default));
            } else {
                tvIntroduction.setText(introduction);
            }

            ImageHelper.displayPortrait(this, dataEntity.getProfile_image(), ivHeader);

            String gender = dataEntity.getGender();
            if (gender.equals("1")) { //1-男，2-女
                ivGender.setImageResource(R.mipmap.icon_male);
            } else {
                ivGender.setImageResource(R.mipmap.icon_female);
            }
        }
    }

    //跳转聊天页面
    private void goChat() {
        if (dataEntity == null)
            return;
        Bundle bundle = new Bundle();
        bundle.putString("userId", dataEntity.getId());
        bundle.putString("easemod_id", dataEntity.getEasemob_account());
        UIHelper.jump(this, ChatActivity.class, bundle);
    }

    @OnClick(R.id.btn_chat)
    public void onViewClicked() {
        if (PaperUtils.isLogin())
            goChat();
        else
            goLogin();
    }

    //跳转登录
    private void goLogin() {
        UIHelper.jump(this, LoginActivity.class);
    }
}
