package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.orhanobut.logger.Logger;
import com.sundy.iman.MainApp;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.LoginEntity;
import com.sundy.iman.entity.MemberInfoEntity;
import com.sundy.iman.entity.MsgEvent;
import com.sundy.iman.entity.VerificationCodeEntity;
import com.sundy.iman.helper.ChatHelper;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.utils.CommonUtils;
import com.sundy.iman.utils.NetWorkUtils;
import com.sundy.iman.utils.PhoneFormatCheckUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sundy on 17/11/15.
 */

public class LoginType2Activity extends BaseActivity {

    private final int DURATION = 60; //间隔时间：秒

    @BindView(R.id.btn_close)
    ImageView btnClose;
    @BindView(R.id.rel_title_bar)
    RelativeLayout relTitleBar;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.btn_get_code)
    TextView btnGetCode;
    @BindView(R.id.btn_login)
    TextView btnLogin;
    @BindView(R.id.ll_front)
    LinearLayout llFront;

    private final int MSG_COUNT_DOWN = 1;
    private final int MSG_STOP_COUNT = 0;
    private int second_by_mobile = DURATION;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_COUNT_DOWN:
                    if (second_by_mobile == 1) {
                        stopCountDown();
                    } else {
                        second_by_mobile--;
                        btnGetCode.setText(second_by_mobile + "s");
                        startCountDown();
                    }
                    break;
                case MSG_STOP_COUNT:
                    second_by_mobile = DURATION;
                    btnGetCode.setEnabled(true);
                    btnGetCode.setSelected(true);
                    btnGetCode.setText(getString(R.string.get_code));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login2);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        etAccount.addTextChangedListener(watcherAccount);
        etCode.addTextChangedListener(watcherCode);
        btnGetCode.setSelected(false);
        btnGetCode.setEnabled(false);
        btnLogin.setSelected(false);
        btnLogin.setEnabled(false);
    }

    private TextWatcher watcherAccount = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String account = etAccount.getText().toString().trim();
            if (account.contains("@")) { //邮箱
                if (CommonUtils.isEmail(account)) {
                    stopCountDown();
                } else {
                    btnGetCode.setSelected(false);
                    btnGetCode.setEnabled(false);
                }
            } else { //手机
                if (PhoneFormatCheckUtils.isPhoneLegal(account)) {
                    stopCountDown();
                }else {
                    btnGetCode.setSelected(false);
                    btnGetCode.setEnabled(false);
                }
            }

            checkBtnState();
        }
    };

    private void startCountDown() {
        if (mHandler != null) {
            mHandler.removeMessages(MSG_STOP_COUNT);
            mHandler.sendEmptyMessageDelayed(MSG_COUNT_DOWN, 1000);
        }
    }

    private void stopCountDown() {
        if (mHandler != null) {
            mHandler.removeMessages(MSG_COUNT_DOWN);
            mHandler.sendEmptyMessage(MSG_STOP_COUNT);
        }
    }

    private TextWatcher watcherCode = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            checkBtnState();
        }

    };

    private void checkBtnState() {
        String account = etAccount.getText().toString().trim();
        String verification_code = etCode.getText().toString().trim();
        if (TextUtils.isEmpty(verification_code) || verification_code.length() < 4
                || TextUtils.isEmpty(account)) {
            btnLogin.setSelected(false);
            btnLogin.setEnabled(false);
            if (account.contains("@")) { //邮箱
                if (!CommonUtils.isEmail(account)) {
                    btnLogin.setSelected(false);
                    btnLogin.setEnabled(false);
                }
            } else { //手机号码
                if (!PhoneFormatCheckUtils.isPhoneLegal(account)) {
                    btnLogin.setSelected(false);
                    btnLogin.setEnabled(false);
                }
            }
        } else {
            btnLogin.setSelected(true);
            btnLogin.setEnabled(true);
        }
    }


    @OnClick({R.id.btn_close, R.id.btn_get_code, R.id.btn_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_close:
                finish();
                break;
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
        if (!NetWorkUtils.isNetAvailable(this)) {
            MainApp.getInstance().showToast(getString(R.string.network_not_available));
            return;
        }

        String account = etAccount.getText().toString().trim();
        Map<String, String> param = new HashMap<>();
        param.put("area_code", "86");
        param.put("phone", account);
        param.put("type", "1"); //类型 1-登录
        Call<VerificationCodeEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .sendVerificationCode(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<VerificationCodeEntity>() {
            @Override
            public void onSuccess(Call<VerificationCodeEntity> call, Response<VerificationCodeEntity> response) {
                VerificationCodeEntity verificationCodeEntity = response.body();
                if (verificationCodeEntity != null) {
                    int code = verificationCodeEntity.getCode();
                    String msg = verificationCodeEntity.getMsg();
                    if (code == Constants.CODE_SUCCESS) {
                        VerificationCodeEntity.DataEntity dataEntity = verificationCodeEntity.getData();
                        if (dataEntity != null) {
                            String verification_code = dataEntity.getVerification_code();
                            Logger.i("------->验证码=" + verification_code);
                            btnGetCode.setEnabled(false);
                            btnGetCode.setSelected(false);
                            btnGetCode.setText(DURATION + "s");
                            startCountDown();
                        }
                    } else {
                        MainApp.getInstance().showToast(msg);
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

    //通过手机号码登陆
    private void login() {
        if (!EaseCommonUtils.isNetWorkConnected(this)) {
            MainApp.getInstance().showToast(getString(R.string.network_not_available));
            return;
        }

        String account = etAccount.getText().toString().trim();
        String verification_code = etCode.getText().toString().trim();
        Map<String, String> param = new HashMap<>();
        param.put("area_code", "86");
        param.put("phone", account);
        param.put("verification_code", verification_code); //类型 1-登录
        Call<LoginEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .login(ParamHelper.formatData(param));
        showProgress();
        call.enqueue(new RetrofitCallback<LoginEntity>() {
            @Override
            public void onSuccess(Call<LoginEntity> call, Response<LoginEntity> response) {
                LoginEntity loginEntity = response.body();
                if (loginEntity != null) {
                    int code = loginEntity.getCode();
                    String msg = loginEntity.getMsg();
                    if (code == Constants.CODE_SUCCESS) {
                        LoginEntity.DataEntity dataEntity = loginEntity.getData();
                        if (dataEntity != null) {
                            String userId = dataEntity.getId();
                            String session_key = dataEntity.getSession_key();
                            //保存用户Session Key
                            PaperUtils.setSessionKey(session_key);
                            getMemberInfo(userId, session_key);
                        }
                    } else {
                        MainApp.getInstance().showToast(msg);
                        hideProgress();
                    }
                }
            }

            @Override
            public void onAfter() {
            }

            @Override
            public void onFailure(Call<LoginEntity> call, Throwable t) {
                hideProgress();
            }
        });
    }

    //获取个人用户信息
    private void getMemberInfo(final String mid, String session_key) {
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
                    int code = memberInfoEntity.getCode();
                    String msg = memberInfoEntity.getMsg();
                    if (code == Constants.CODE_SUCCESS) {
                        MemberInfoEntity.DataEntity dataEntity = memberInfoEntity.getData();
                        if (dataEntity != null) {
                            //保存登录用户信息
                            PaperUtils.saveUserInfo(memberInfoEntity); //保存到Paper
                            ChatHelper.getInstance().updateSelfUserInfo(memberInfoEntity); //保存到数据库
                            //登录环信
                            loginHx(dataEntity.getEasemob_account(), dataEntity.getEasemob_password());
                        }
                    } else {
                        MainApp.getInstance().showToast(msg);
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

    //登录环信
    private void loginHx(String userName, String password) {
        EMClient.getInstance().login(userName, password, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                hideProgress();
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Logger.e("登录聊天服务器成功！");

                sendLoginEvent();
                finish();
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Logger.e("登录聊天服务器失败！");
                hideProgress();
            }
        });
    }

    //发送登录成功事件
    private void sendLoginEvent() {
        MsgEvent msgEvent = new MsgEvent();
        msgEvent.setMsg(MsgEvent.EVENT_LOGIN_SUCCESS);
        EventBus.getDefault().post(msgEvent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopCountDown();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            stopCountDown();
            mHandler = null;
        }
    }
}
