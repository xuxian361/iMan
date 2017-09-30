package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.orhanobut.logger.Logger;
import com.sundy.iman.R;
import com.sundy.iman.entity.ChangeLanguageEntity;
import com.sundy.iman.entity.LoginEntity;
import com.sundy.iman.entity.LogoutEntity;
import com.sundy.iman.entity.MemberInfoEntity;
import com.sundy.iman.entity.UpdateTransferPwdEntity;
import com.sundy.iman.entity.VerificationCodeEntity;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
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
 * Created by sundy on 17/9/27.
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.btn_get_code)
    Button btnGetCode;
    @BindView(R.id.btn_login)
    Button btnLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
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


    @OnClick({R.id.btn_get_code, R.id.btn_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_get_code:
                getCode();

                break;
            case R.id.btn_login:
                login();
                break;
        }
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
//                        logout(userId, dataEntity.getSession_key());
//                        updateTransferPwd(userId, dataEntity.getSession_key());
                        changeLanguage(userId, dataEntity.getSession_key());
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

    //登出
    private void logout(String mid, String session_key) {
        Map<String, String> param = new HashMap<>();
        param.put("mid", mid);
        param.put("session_key", session_key);
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

    //更新支付密码接口
    private void updateTransferPwd(String mid, String session_key) {
        Map<String, String> param = new HashMap<>();
        param.put("mid", mid);
        param.put("session_key", session_key);
        param.put("password", "123456");
        param.put("confirm_password", "123456");
        Call<UpdateTransferPwdEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .updateTransferPwd(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<UpdateTransferPwdEntity>() {
            @Override
            public void onSuccess(Call<UpdateTransferPwdEntity> call, Response<UpdateTransferPwdEntity> response) {
                UpdateTransferPwdEntity updateTransferPwdEntity = response.body();
                if (updateTransferPwdEntity != null) {
                    int code = updateTransferPwdEntity.getCode();
                    Logger.e("------>code = " + code);
                }
            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<UpdateTransferPwdEntity> call, Throwable t) {

            }
        });
    }

    //修改语言接口
    private void changeLanguage(String mid, String session_key) {
        Map<String, String> param = new HashMap<>();
        param.put("mid", mid);
        param.put("session_key", session_key);
        param.put("language", "en");//语言 en-英文,sc-简体中文,tc-繁体中文
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


}
