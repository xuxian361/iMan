package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.sundy.iman.R;
import com.sundy.iman.entity.MemberInfoEntity;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.view.TitleBarView;

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

public class MemberInfoActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.iv_header)
    ImageView ivHeader;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.iv_gender)
    ImageView ivGender;
    @BindView(R.id.tv_introduction)
    TextView tvIntroduction;
    @BindView(R.id.iv_notification)
    ImageView ivNotification;
    @BindView(R.id.btn_chat)
    Button btnChat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_member_info);
        ButterKnife.bind(this);
    }

    //获取个人用户信息
    private void getMemberInfo(String mid, String session_key) {
        Map<String, String> param = new HashMap<>();
        param.put("mid", mid);
        param.put("session_key", session_key);
        param.put("profile_id", "");
        Call<MemberInfoEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .getMemberInfo(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<MemberInfoEntity>() {
            @Override
            public void onSuccess(Call<MemberInfoEntity> call, Response<MemberInfoEntity> response) {
                MemberInfoEntity memberInfoEntity = response.body();
                if (memberInfoEntity != null) {
                    MemberInfoEntity.DataEntity dataEntity = memberInfoEntity.getData();
                    if (dataEntity != null) {
                        Logger.i("------->phone=" + dataEntity.getPhone());
                    }
                }
            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<MemberInfoEntity> call, Throwable t) {

            }
        });
    }


    @OnClick({R.id.iv_notification, R.id.btn_chat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_notification:
                break;
            case R.id.btn_chat:
                goChat();
                break;
        }
    }

    //跳转聊天页面
    private void goChat() {
        UIHelper.jump(this, ChatActivity.class);
    }
}
