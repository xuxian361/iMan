package com.sundy.iman.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.MemberInfoEntity;
import com.sundy.iman.helper.ImageHelper;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.ui.activity.CreateAdvertisingActivity;
import com.sundy.iman.ui.activity.CreateCommunityActivity;
import com.sundy.iman.ui.activity.EditProfileActivity;
import com.sundy.iman.ui.activity.LoginActivity;
import com.sundy.iman.ui.activity.MyContactsActivity;
import com.sundy.iman.ui.activity.MyPostActivity;
import com.sundy.iman.ui.activity.MyPromoteCommunityActivity;
import com.sundy.iman.ui.activity.SettingsActivity;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sundy on 17/9/14.
 */

public class MeFragment extends BaseFragment {

    @BindView(R.id.iv_header)
    CircleImageView ivHeader;
    Unbinder unbinder;
    @BindView(R.id.tv_login)
    TextView tvLogin;
    @BindView(R.id.rel_not_login)
    RelativeLayout relNotLogin;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_introduction)
    TextView tvIntroduction;
    @BindView(R.id.rel_logined)
    RelativeLayout relLogined;
    @BindView(R.id.tv_title_wallet)
    TextView tvTitleWallet;
    @BindView(R.id.tv_my_imcoin)
    TextView tvMyImcoin;
    @BindView(R.id.iv_arrow_my_imcoin)
    ImageView ivArrowMyImcoin;
    @BindView(R.id.rel_imcoin)
    RelativeLayout relImcoin;
    @BindView(R.id.tv_use_imcoin)
    TextView tvUseImcoin;
    @BindView(R.id.iv_arrow_use_imcoin)
    ImageView ivArrowUseImcoin;
    @BindView(R.id.rel_use_of_imcoin)
    RelativeLayout relUseOfImcoin;
    @BindView(R.id.ll_wallet)
    LinearLayout llWallet;
    @BindView(R.id.tv_title_use_my_wallet)
    TextView tvTitleUseMyWallet;
    @BindView(R.id.tv_create_ad)
    TextView tvCreateAd;
    @BindView(R.id.iv_arrow_create_ad)
    ImageView ivArrowCreateAd;
    @BindView(R.id.rel_create_ad)
    RelativeLayout relCreateAd;
    @BindView(R.id.tv_promote_community)
    TextView tvPromoteCommunity;
    @BindView(R.id.iv_arrow_promote_community)
    ImageView ivArrowPromoteCommunity;
    @BindView(R.id.rel_promote_community)
    RelativeLayout relPromoteCommunity;
    @BindView(R.id.tv_create_community)
    TextView tvCreateCommunity;
    @BindView(R.id.iv_arrow_create_community)
    ImageView ivArrowCreateCommunity;
    @BindView(R.id.rel_create_community)
    RelativeLayout relCreateCommunity;
    @BindView(R.id.ll_use_my_wallet)
    LinearLayout llUseMyWallet;
    @BindView(R.id.tv_title_account)
    TextView tvTitleAccount;
    @BindView(R.id.tv_my_contacts)
    TextView tvMyContacts;
    @BindView(R.id.iv_arrow_my_contacts)
    ImageView ivArrowMyContacts;
    @BindView(R.id.rel_my_contacts)
    RelativeLayout relMyContacts;
    @BindView(R.id.tv_my_post)
    TextView tvMyPost;
    @BindView(R.id.iv_arrow_my_post)
    ImageView ivArrowMyPost;
    @BindView(R.id.rel_my_post)
    RelativeLayout relMyPost;
    @BindView(R.id.tv_settings)
    TextView tvSettings;
    @BindView(R.id.iv_arrow_settings)
    ImageView ivArrowSettings;
    @BindView(R.id.rel_settings)
    RelativeLayout relSettings;
    @BindView(R.id.ll_account)
    LinearLayout llAccount;
    @BindView(R.id.tv_my_imcoin_num)
    TextView tvMyImcoinNum;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        unbinder = ButterKnife.bind(this, view);

        init();
        return view;
    }

    private void init() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                Logger.e("------>onRefresh");
                getMemberInfo();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PaperUtils.isLogin()) {
            relNotLogin.setVisibility(View.GONE);
            relLogined.setVisibility(View.VISIBLE);
            showMemberInfo(PaperUtils.getUserInfo());
        } else {
            relNotLogin.setVisibility(View.VISIBLE);
            relLogined.setVisibility(View.GONE);
        }
    }

    //获取个人用户信息
    private void getMemberInfo() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("profile_id", PaperUtils.getMId());
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
                        MemberInfoEntity.DataEntity dataEntity = memberInfoEntity.getData();
                        if (dataEntity != null) {
                            PaperUtils.saveUserInfo(memberInfoEntity);
                            showMemberInfo(memberInfoEntity);
                        }
                    }
                }
            }

            @Override
            public void onAfter() {
                refreshLayout.finishRefresh(2000);
            }

            @Override
            public void onFailure(Call<MemberInfoEntity> call, Throwable t) {

            }
        });
    }

    //显示用户信息
    private void showMemberInfo(MemberInfoEntity memberInfoEntity) {
        if (memberInfoEntity != null) {
            MemberInfoEntity.DataEntity dataEntity = memberInfoEntity.getData();
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
                String coin = dataEntity.getBalance();
                if (TextUtils.isEmpty(coin)) {
                    tvMyImcoinNum.setVisibility(View.GONE);
                } else {
                    tvMyImcoinNum.setVisibility(View.VISIBLE);
                    tvMyImcoinNum.setText(coin);
                }
                ImageHelper.displayPortrait(getActivity(), dataEntity.getProfile_image(), ivHeader);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_login, R.id.rel_logined, R.id.rel_imcoin, R.id.rel_use_of_imcoin, R.id.rel_create_ad, R.id.rel_promote_community, R.id.rel_create_community, R.id.rel_my_contacts, R.id.rel_my_post, R.id.rel_settings})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_login:
                goLogin();
                break;
            case R.id.rel_logined:
                goEditProfile();
                break;
            case R.id.rel_imcoin:
                goMyImcoin();
                break;
            case R.id.rel_use_of_imcoin:
                goUseOfImcoin();
                break;
            case R.id.rel_create_ad:
                goCreateAd();
                break;
            case R.id.rel_promote_community:
                goPromoteCommunity();
                break;
            case R.id.rel_create_community:
                goCreateCommunity();
                break;
            case R.id.rel_my_contacts:
                goMyContacts();
                break;
            case R.id.rel_my_post:
                goMyPost();
                break;
            case R.id.rel_settings:
                goSettings();
                break;
        }
    }

    //跳转设置
    private void goSettings() {
        UIHelper.jump(mContext, SettingsActivity.class);
    }

    //跳转我的发布列表
    private void goMyPost() {
        if (PaperUtils.isLogin()) {
            UIHelper.jump(mContext, MyPostActivity.class);
        } else {
            goLogin();
        }
    }

    //跳转我的联系人
    private void goMyContacts() {
        if (PaperUtils.isLogin()) {
            UIHelper.jump(mContext, MyContactsActivity.class);
        } else {
            goLogin();
        }
    }

    //创建社区
    private void goCreateCommunity() {
        if (PaperUtils.isLogin()) {
            UIHelper.jump(mContext, CreateCommunityActivity.class);
        } else {
            goLogin();
        }
    }

    //跳转推广社区
    private void goPromoteCommunity() {
        if (PaperUtils.isLogin()) {
            UIHelper.jump(mContext, MyPromoteCommunityActivity.class);
        } else {
            goLogin();
        }
    }

    //创建广告
    private void goCreateAd() {
        if (PaperUtils.isLogin()) {
            UIHelper.jump(mContext, CreateAdvertisingActivity.class);
        } else {
            goLogin();
        }
    }

    //跳转Use of Imcoin
    private void goUseOfImcoin() {

    }

    //跳转My Imcoin
    private void goMyImcoin() {

    }

    //跳转编辑用户信息
    private void goEditProfile() {
        if (PaperUtils.isLogin()) {
            UIHelper.jump(mContext, EditProfileActivity.class);
        } else {
            goLogin();
        }
    }

    //跳转登陆
    private void goLogin() {
        UIHelper.jump(mContext, LoginActivity.class);
    }

}
