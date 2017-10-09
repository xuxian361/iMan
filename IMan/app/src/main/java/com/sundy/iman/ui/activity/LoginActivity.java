package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.sundy.iman.R;
import com.sundy.iman.entity.CancelPostEntity;
import com.sundy.iman.entity.CollectAdvertisingEntity;
import com.sundy.iman.entity.CommunityInfoEntity;
import com.sundy.iman.entity.CountryCodeEntity;
import com.sundy.iman.entity.DeletePostEntity;
import com.sundy.iman.entity.GetPostInfoEntity;
import com.sundy.iman.entity.JoinCommunityEntity;
import com.sundy.iman.entity.JoinPromoteCommunityEntity;
import com.sundy.iman.entity.LoginEntity;
import com.sundy.iman.entity.MemberInfoEntity;
import com.sundy.iman.entity.MsgEvent;
import com.sundy.iman.entity.UpdatePostEntity;
import com.sundy.iman.entity.VerificationCodeEntity;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sundy on 17/9/27.
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.btn_get_code)
    Button btnGetCode;
    @BindView(R.id.btn_login)
    TextView btnLogin;
    @BindView(R.id.btn_close)
    ImageView btnClose;
    @BindView(R.id.tv_area)
    TextView tvArea;
    @BindView(R.id.ll_area)
    LinearLayout llArea;

    private CountryCodeEntity.DataEntity curCountryCode;//当前选择的国家码

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        init();
    }

    private void init() {

    }

    @OnClick({R.id.btn_get_code, R.id.btn_login, R.id.ll_area, R.id.btn_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_get_code:
                getCode();
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.ll_area:
                goSelectCountryCode();
                break;
            case R.id.btn_close:
                finish();
                break;
        }
    }

    //跳转选择国家码
    private void goSelectCountryCode() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("Country_Code", curCountryCode);
        UIHelper.jump(this, SelectCountryCodeActivity.class, bundle);
    }

    //获取手机验证码
    private void getCode() {
        String phone = etAccount.getText().toString().trim();
        Map<String, String> param = new HashMap<>();
        param.put("area_code", "86");
        param.put("phone", phone);
        param.put("type", "1"); //类型 1-登录
        Call<VerificationCodeEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .sendVerificationCode(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<VerificationCodeEntity>() {
            @Override
            public void onSuccess(Call<VerificationCodeEntity> call, Response<VerificationCodeEntity> response) {
                VerificationCodeEntity verificationCodeEntity = response.body();
                if (verificationCodeEntity != null) {
                    VerificationCodeEntity.DataEntity dataEntity = verificationCodeEntity.getData();
                    if (dataEntity != null) {
                        String code = dataEntity.getVerification_code();
                        Logger.i("------->验证码=" + code);
                    }
                }
            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<VerificationCodeEntity> call, Throwable t) {

            }
        });
    }

    //登录
    private void login() {
        String phone = etAccount.getText().toString().trim();
        String verification_code = etCode.getText().toString();
        Map<String, String> param = new HashMap<>();
        param.put("area_code", "86");
        param.put("phone", phone);
        param.put("verification_code", verification_code); //类型 1-登录
        Call<LoginEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .login(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<LoginEntity>() {
            @Override
            public void onSuccess(Call<LoginEntity> call, Response<LoginEntity> response) {
                LoginEntity loginEntity = response.body();
                if (loginEntity != null) {
                    LoginEntity.DataEntity dataEntity = loginEntity.getData();
                    if (dataEntity != null) {
                        String userId = dataEntity.getId();
                        String easemob_account = dataEntity.getEasemob_account();
                        Logger.i("----->用户ID=" + userId);
                        Logger.i("----->easemob_account=" + easemob_account);
//                        getMemberInfo(userId, dataEntity.getSession_key());


                    }
                }
            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<LoginEntity> call, Throwable t) {

            }
        });
    }

    //获取个人用户信息
    private void getMemberInfo(String mid, String session_key) {
        Map<String, String> param = new HashMap<>();
        param.put("mid", mid);
        param.put("session_key", session_key);
        param.put("profile_id", mid);
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

    //加入或退出社区
    private void joinCommunity() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", "");
        param.put("session_key", "");
        param.put("type", ""); //类型: 0-加入，1-退出
        param.put("community_id", "");
        Call<JoinCommunityEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .joinCommunity(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<JoinCommunityEntity>() {
            @Override
            public void onSuccess(Call<JoinCommunityEntity> call, Response<JoinCommunityEntity> response) {

            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<JoinCommunityEntity> call, Throwable t) {

            }
        });
    }

    //删除Post
    private void deletePost() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", "");
        param.put("session_key", "");
        param.put("post_id", "");
        Call<DeletePostEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .deletePost(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<DeletePostEntity>() {
            @Override
            public void onSuccess(Call<DeletePostEntity> call, Response<DeletePostEntity> response) {

            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<DeletePostEntity> call, Throwable t) {

            }
        });
    }

    //取消Post
    private void cancelPost() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", "");
        param.put("session_key", "");
        param.put("post_id", "");
        Call<CancelPostEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .cancelPost(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<CancelPostEntity>() {
            @Override
            public void onSuccess(Call<CancelPostEntity> call, Response<CancelPostEntity> response) {

            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<CancelPostEntity> call, Throwable t) {

            }
        });
    }

    //更新Post
    private void updatePost() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", "");
        param.put("session_key", "");
        param.put("title", ""); //post标题
        param.put("detail", ""); //post详情
        param.put("tags", ""); //标签
        param.put("location", "");
        param.put("latitude", "");
        param.put("longitude", "");
        param.put("aging", ""); //时效
        param.put("attachment", ""); //附件 json格式数据:att_type为附件类型，1-图片，2-视频 url：附件存放路径
        param.put("post_id", "");
        Call<UpdatePostEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .updatePost(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<UpdatePostEntity>() {
            @Override
            public void onSuccess(Call<UpdatePostEntity> call, Response<UpdatePostEntity> response) {

            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<UpdatePostEntity> call, Throwable t) {

            }
        });
    }

    //获取Post 信息
    private void getPostInfo() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", "");
        param.put("session_key", "");
        param.put("post_id", ""); //post id
        param.put("creator_id", ""); //post的作者ID
        Call<GetPostInfoEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .getPostInfo(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<GetPostInfoEntity>() {
            @Override
            public void onSuccess(Call<GetPostInfoEntity> call, Response<GetPostInfoEntity> response) {

            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<GetPostInfoEntity> call, Throwable t) {

            }
        });
    }

    //领取广告奖励
    private void collectAdvertising() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", "");
        param.put("session_key", "");
        param.put("post_id", ""); //post id
        param.put("creator_id", ""); //post的作者ID
        param.put("community_id", ""); //社区ID
        param.put("income", ""); //奖励金额
        Call<CollectAdvertisingEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .collectAdvertising(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<CollectAdvertisingEntity>() {
            @Override
            public void onSuccess(Call<CollectAdvertisingEntity> call, Response<CollectAdvertisingEntity> response) {

            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<CollectAdvertisingEntity> call, Throwable t) {

            }
        });
    }

    //加入推广社区
    private void joinPromoteCommunity() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", "");
        param.put("session_key", "");
        param.put("community_id", ""); //社区ID
        param.put("promoter_id", ""); //推广者ID
        Call<JoinPromoteCommunityEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .joinPromoteCommunity(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<JoinPromoteCommunityEntity>() {
            @Override
            public void onSuccess(Call<JoinPromoteCommunityEntity> call, Response<JoinPromoteCommunityEntity> response) {

            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<JoinPromoteCommunityEntity> call, Throwable t) {

            }
        });
    }

    //社区详情
    private void getCommunityInfo() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", "");
        param.put("session_key", "");
        param.put("community_id", ""); //社区ID
        param.put("type", ""); //类型: 1-普通社区，2-推广社区
        Call<CommunityInfoEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .getCommunityInfo(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<CommunityInfoEntity>() {
            @Override
            public void onSuccess(Call<CommunityInfoEntity> call, Response<CommunityInfoEntity> response) {

            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<CommunityInfoEntity> call, Throwable t) {

            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MsgEvent event) {
        if (event != null) {
            String msg = event.getMsg();
            switch (msg) {
                case MsgEvent.EVENT_GET_COUNTRY_CODE:
                    curCountryCode = (CountryCodeEntity.DataEntity) event.getObj();
                    if (curCountryCode != null) {
                        tvArea.setText(curCountryCode.getCode());
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
