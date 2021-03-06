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
import android.widget.FrameLayout;
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
import com.sundy.iman.utils.ViewAnimUtils;

import org.greenrobot.eventbus.EventBus;

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


    private final int MSG_COUNT_DOWN_BY_MOBILE = 1;
    private final int MSG_STOP_COUNT_BY_MOBILE = 0;

    private final int MSG_COUNT_DOWN_BY_EMAIL = 3;
    private final int MSG_STOP_COUNT_BY_EMAIL = 2;

    private final int DURATION = 60; //间隔时间：秒
    private static final int duration = 300;

    @BindView(R.id.btn_close)
    ImageView btnClose;
    @BindView(R.id.rel_title_bar)
    RelativeLayout relTitleBar;
    @BindView(R.id.et_mobile)
    EditText etMobile;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.btn_get_code)
    TextView btnGetCode;
    @BindView(R.id.btn_login)
    TextView btnLogin;
    @BindView(R.id.ll_front)
    LinearLayout llFront;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_code2)
    EditText etCode2;
    @BindView(R.id.btn_get_code2)
    TextView btnGetCode2;
    @BindView(R.id.btn_login2)
    TextView btnLogin2;
    @BindView(R.id.ll_back)
    LinearLayout llBack;
    @BindView(R.id.frame)
    FrameLayout frame;
    @BindView(R.id.tv_email_login)
    TextView tvEmailLogin;
    @BindView(R.id.tv_mobile_login)
    TextView tvMobileLogin;
    private int second_by_mobile = DURATION;
    private int second_by_email = DURATION;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_COUNT_DOWN_BY_MOBILE:
                    if (second_by_mobile == 1) {
                        stopCountDownByMobile();
                    } else {
                        second_by_mobile--;
                        btnGetCode.setText(second_by_mobile + "s");
                        startCountDownByMobile();
                    }
                    break;
                case MSG_STOP_COUNT_BY_MOBILE:
                    second_by_mobile = DURATION;
                    btnGetCode.setEnabled(true);
                    btnGetCode.setSelected(true);
                    btnGetCode.setText(getString(R.string.get_code));
                    break;
            }
        }
    };

    private Handler mHandler_Email = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_COUNT_DOWN_BY_EMAIL:
                    if (second_by_email == 1) {
                        stopCountDownByEmail();
                    } else {
                        second_by_email--;
                        btnGetCode2.setText(second_by_email + "s");
                        startCountDownByEmail();
                    }
                    break;
                case MSG_STOP_COUNT_BY_EMAIL:
                    second_by_email = DURATION;
                    btnGetCode2.setEnabled(true);
                    btnGetCode2.setSelected(true);
                    btnGetCode2.setText(getString(R.string.get_code));
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        etMobile.addTextChangedListener(watcherMobile);
        etCode.addTextChangedListener(watcherMobileCode);
        etEmail.addTextChangedListener(watcherEmail);
        etCode2.addTextChangedListener(watcherEmailCode);
        btnGetCode.setSelected(false);
        btnGetCode.setEnabled(false);
        btnLogin.setSelected(false);
        btnLogin.setEnabled(false);
        btnGetCode2.setSelected(false);
        btnGetCode2.setEnabled(false);
        btnLogin2.setSelected(false);
        btnLogin2.setEnabled(false);
    }

    private TextWatcher watcherMobile = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String mobile = etMobile.getText().toString().trim();
            if (PhoneFormatCheckUtils.isPhoneLegal(mobile)) {
                stopCountDownByMobile();
            } else {
                btnGetCode.setSelected(false);
                btnGetCode.setEnabled(false);
            }

            checkBtnStateByMobile();
        }
    };

    private TextWatcher watcherMobileCode = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            checkBtnStateByMobile();
        }
    };

    private TextWatcher watcherEmail = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String email = etEmail.getText().toString().trim();
            if (CommonUtils.isEmail(email)) {
                stopCountDownByEmail();
            } else {
                btnGetCode2.setSelected(false);
                btnGetCode2.setEnabled(false);
            }

            checkBtnStateByEmail();
        }
    };

    private TextWatcher watcherEmailCode = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            checkBtnStateByEmail();
        }
    };

    //开始计时 - mobile
    private void startCountDownByMobile() {
        if (mHandler != null) {
            mHandler.removeMessages(MSG_STOP_COUNT_BY_MOBILE);
            mHandler.sendEmptyMessageDelayed(MSG_COUNT_DOWN_BY_MOBILE, 1000);
        }
    }

    //停止计时 - mobile
    private void stopCountDownByMobile() {
        if (mHandler != null) {
            mHandler.removeMessages(MSG_COUNT_DOWN_BY_MOBILE);
            mHandler.sendEmptyMessage(MSG_STOP_COUNT_BY_MOBILE);
        }
    }

    //开始计时 - email
    private void startCountDownByEmail() {
        if (mHandler_Email != null) {
            mHandler_Email.removeMessages(MSG_STOP_COUNT_BY_EMAIL);
            mHandler_Email.sendEmptyMessageDelayed(MSG_COUNT_DOWN_BY_EMAIL, 1000);
        }
    }

    //停止计时 - email
    private void stopCountDownByEmail() {
        if (mHandler_Email != null) {
            mHandler_Email.removeMessages(MSG_COUNT_DOWN_BY_EMAIL);
            mHandler_Email.sendEmptyMessage(MSG_STOP_COUNT_BY_EMAIL);
        }
    }

    //查看手机登录按钮状态
    private void checkBtnStateByMobile() {
        String mobile = etMobile.getText().toString().trim();
        String verification_code = etCode.getText().toString().trim();
        if (TextUtils.isEmpty(verification_code) || verification_code.length() < 4
                || TextUtils.isEmpty(mobile)
                || !PhoneFormatCheckUtils.isPhoneLegal(mobile)) {
            btnLogin.setSelected(false);
            btnLogin.setEnabled(false);
        } else {
            btnLogin.setSelected(true);
            btnLogin.setEnabled(true);
        }
    }

    //查看邮箱登陆按钮状态
    private void checkBtnStateByEmail() {
        String email = etEmail.getText().toString().trim();
        String verification_code = etCode2.getText().toString().trim();
        if (TextUtils.isEmpty(verification_code) || verification_code.length() < 4
                || TextUtils.isEmpty(email)
                || !CommonUtils.isEmail(email)) {
            btnLogin2.setSelected(false);
            btnLogin2.setEnabled(false);
        } else {
            btnLogin2.setSelected(true);
            btnLogin2.setEnabled(true);
        }
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
        stopCountDownByMobile();
        stopCountDownByEmail();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            stopCountDownByMobile();
            mHandler = null;
        }
        if (mHandler_Email != null) {
            stopCountDownByEmail();
            mHandler_Email = null;
        }
    }

    @OnClick({R.id.btn_close, R.id.btn_get_code, R.id.btn_login, R.id.btn_get_code2, R.id.btn_login2, R.id.tv_email_login, R.id.tv_mobile_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_close:
                finish();
                break;
            case R.id.btn_get_code:
                getMobileCode();
                break;
            case R.id.btn_login:
                loginByMobile();
                break;
            case R.id.btn_get_code2:
                getEmailCode();
                break;
            case R.id.btn_login2:
                loginByEmail();
                break;
            case R.id.tv_email_login:
                ViewAnimUtils.flip(frame, duration, -1);
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switchViewVisibility();
                    }
                }, duration);
                break;
            case R.id.tv_mobile_login:
                ViewAnimUtils.flip(frame, duration, 1);
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switchViewVisibility();
                    }
                }, duration);
                break;
        }
    }

    //获取手机验证码
    private void getMobileCode() {
        if (!NetWorkUtils.isNetAvailable(this)) {
            MainApp.getInstance().showToast(getString(R.string.network_not_available));
            return;
        }

        String phone = etMobile.getText().toString().trim();
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
                            startCountDownByMobile();
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

    //获取邮箱验证码
    private void getEmailCode() {
        if (!NetWorkUtils.isNetAvailable(this)) {
            MainApp.getInstance().showToast(getString(R.string.network_not_available));
            return;
        }

        String phone = etEmail.getText().toString().trim();
        Map<String, String> param = new HashMap<>();
        param.put("area_code", "");
        param.put("phone", phone);
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
                            btnGetCode2.setEnabled(false);
                            btnGetCode2.setSelected(false);
                            btnGetCode2.setText(DURATION + "s");
                            startCountDownByEmail();
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

    //通过邮箱登陆
    private void loginByEmail() {
        if (!EaseCommonUtils.isNetWorkConnected(this)) {
            MainApp.getInstance().showToast(getString(R.string.network_not_available));
            return;
        }

        String phone = etEmail.getText().toString().trim();
        String verification_code = etCode2.getText().toString().trim();
        Map<String, String> param = new HashMap<>();
        param.put("area_code", "");
        param.put("phone", phone);
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

    //通过手机号码登陆
    private void loginByMobile() {
        if (!EaseCommonUtils.isNetWorkConnected(this)) {
            MainApp.getInstance().showToast(getString(R.string.network_not_available));
            return;
        }

        String phone = etMobile.getText().toString().trim();
        String verification_code = etCode.getText().toString().trim();
        Map<String, String> param = new HashMap<>();
        param.put("area_code", "86");
        param.put("phone", phone);
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

    private void switchViewVisibility() {
        if (llBack.isShown()) { //切换到邮箱登录
            llBack.setVisibility(View.GONE);
            llFront.setVisibility(View.VISIBLE);
            tvEmailLogin.setVisibility(View.VISIBLE);
            tvMobileLogin.setVisibility(View.GONE);
        } else { //切换到手机号码登录
            llBack.setVisibility(View.VISIBLE);
            llFront.setVisibility(View.GONE);
            tvEmailLogin.setVisibility(View.GONE);
            tvMobileLogin.setVisibility(View.VISIBLE);
        }
    }
}
