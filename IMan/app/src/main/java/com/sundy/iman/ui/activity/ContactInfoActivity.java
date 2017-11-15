package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.orhanobut.logger.Logger;
import com.sundy.iman.MainApp;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.AddContactEntity;
import com.sundy.iman.entity.MemberInfoEntity;
import com.sundy.iman.helper.ImageHelper;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.utils.NetWorkUtils;
import com.sundy.iman.utils.cache.CacheData;
import com.sundy.iman.view.TitleBarView;

import java.util.HashMap;
import java.util.List;
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
    @BindView(R.id.btn_add_contact)
    TextView btnAddContact;

    private String profile_id;
    private MemberInfoEntity.DataEntity dataEntity;

    private String contact_id; //用户ID
    private String goal_id; //目标ID: 当类型为0时，则为社区ID
    private String type; //类型: 0-社区；1-单聊 ；2-群聊（目前没有） ；3-扫二维码 （目前没有）
    private String easemod_id;

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
            type = bundle.containsKey("type") ? bundle.getString("type") : "";
            goal_id = bundle.containsKey("goal_id") ? bundle.getString("goal_id") : "";
        }
    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.contact_info));
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
                if (TextUtils.isEmpty(easemod_id))
                    return;
                try {
                    Logger.e("---->设置能接收消息");
                    EMClient.getInstance().contactManager().removeUserFromBlackList(easemod_id);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            } else {
                if (TextUtils.isEmpty(easemod_id))
                    return;
                try {
                    Logger.e("---->设置不能接收消息");
                    EMClient.getInstance().contactManager().addUserToBlackList(easemod_id, true);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    //获取个人用户信息
    private void getMemberInfo() {
        dataEntity = CacheData.getInstance().getContactInfo(profile_id);
        if (dataEntity != null) {
            showData(dataEntity);
        }

        if (NetWorkUtils.isNetAvailable(this)) {
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
                                CacheData.getInstance().saveContactInfo(profile_id, dataEntity);
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
    }

    private void showData(MemberInfoEntity.DataEntity dataEntity) {
        if (dataEntity != null) {
            easemod_id = dataEntity.getEasemob_account();
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

            String is_contact = dataEntity.getIs_contact(); //是否是联系人:1-是，0-否
            if (is_contact.equals("1")) {
                btnAddContact.setVisibility(View.GONE);
            } else {
                btnAddContact.setVisibility(View.VISIBLE);
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<String> listBlack = EMClient.getInstance().contactManager().getBlackListFromServer();
                        if (listBlack != null && listBlack.size() > 0) {
                            Logger.e("---->listBlack size = " + listBlack.size());
                            if (listBlack.contains(easemod_id)) {
                                Logger.e("---->不接收消息");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        switchNotification.setChecked(false);
                                    }
                                });
                            } else {
                                Logger.e("---->能接收消息");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        switchNotification.setChecked(true);
                                    }
                                });
                            }
                        }
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    //跳转聊天页面
    private void goChat() {
        if (dataEntity == null)
            return;
        Bundle bundle = new Bundle();
        bundle.putString("easemod_id", dataEntity.getEasemob_account());
        bundle.putString("user_id", dataEntity.getId());
        UIHelper.jump(this, ChatActivity.class, bundle);
    }

    //跳转登录
    private void goLogin() {
        UIHelper.login(this);
    }

    @OnClick({R.id.btn_chat, R.id.btn_add_contact})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_chat:
                if (PaperUtils.isLogin())
                    goChat();
                else
                    goLogin();
                break;
            case R.id.btn_add_contact:
                if (PaperUtils.isLogin())
                    addContact();
                else
                    goLogin();
                break;
        }
    }

    //添加联系人
    private void addContact() {
        if (!NetWorkUtils.isNetAvailable(this)) {
            MainApp.getInstance().showToast(getString(R.string.network_not_available));
            return;
        }

        contact_id = profile_id;

        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("contact_id", contact_id); //联系人ID
        param.put("type", type); //类型: 0-社区；1-单聊 ；2-群聊（目前没有） ；3-扫二维码 （目前没有）
        param.put("goal_id", goal_id); //目标ID: 当类型为0时，则为社区ID
        showProgress();
        Call<AddContactEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .addContact(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<AddContactEntity>() {
            @Override
            public void onSuccess(Call<AddContactEntity> call, Response<AddContactEntity> response) {
                AddContactEntity addContactEntity = response.body();
                if (addContactEntity != null) {
                    int code = addContactEntity.getCode();
                    String msg = addContactEntity.getMsg();
                    if (code == Constants.CODE_SUCCESS) {
                        btnAddContact.setVisibility(View.GONE);
                        MainApp.getInstance().showToast(getString(R.string.add_contact_success));
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
            public void onFailure(Call<AddContactEntity> call, Throwable t) {

            }
        });
    }
}
