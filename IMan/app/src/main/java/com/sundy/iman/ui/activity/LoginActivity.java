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
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.sundy.iman.MainApp;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.CountryCodeEntity;
import com.sundy.iman.entity.LoginEntity;
import com.sundy.iman.entity.MemberInfoEntity;
import com.sundy.iman.entity.MsgEvent;
import com.sundy.iman.entity.VerificationCodeEntity;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.utils.PhoneFormatCheckUtils;

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
    TextView btnGetCode;
    @BindView(R.id.btn_login)
    TextView btnLogin;
    @BindView(R.id.btn_close)
    ImageView btnClose;
    @BindView(R.id.tv_area)
    TextView tvArea;
    @BindView(R.id.ll_area)
    LinearLayout llArea;

    private final int MSG_COUNT_DOWN = 1;
    private final int MSG_STOP_COUNT = 0;
    private final int DURATION = 60; //间隔时间：秒
    private int second = DURATION;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_COUNT_DOWN:
                    if (second == 1) {
                        stopCountDown();
                    } else {
                        second--;
                        btnGetCode.setText(second + "s");
                        startCountDown();
                    }
                    break;
                case MSG_STOP_COUNT:
                    second = DURATION;
                    btnGetCode.setEnabled(true);
                    btnGetCode.setSelected(true);
                    btnGetCode.setText(getString(R.string.get_code));
                    break;
            }
        }
    };

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
        tvArea.setText("+86");
        etAccount.addTextChangedListener(textWatcherAccount);
        etCode.addTextChangedListener(textWatcherCode);
        btnGetCode.setSelected(false);
        btnGetCode.setEnabled(false);
        btnLogin.setSelected(false);
        btnLogin.setEnabled(false);
    }

    private TextWatcher textWatcherAccount = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String mobile = etAccount.getText().toString().trim();
            String verification_code = etCode.getText().toString().trim();
            if (PhoneFormatCheckUtils.isPhoneLegal(mobile)) {
                stopCountDown();
            } else {
                btnGetCode.setSelected(false);
                btnGetCode.setEnabled(false);
            }

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
    };

    private TextWatcher textWatcherCode = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String mobile = etAccount.getText().toString().trim();
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
    };

    //开始计时
    private void startCountDown() {
        if (mHandler != null) {
            mHandler.removeMessages(MSG_STOP_COUNT);
            mHandler.sendEmptyMessageDelayed(MSG_COUNT_DOWN, 1000);
        }
    }

    //停止计时
    private void stopCountDown() {
        if (mHandler != null) {
            mHandler.removeMessages(MSG_COUNT_DOWN);
            mHandler.sendEmptyMessage(MSG_STOP_COUNT);
        }
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
        String area_code = tvArea.getText().toString().trim();
        if (TextUtils.isEmpty(area_code)) {
            area_code = "86";
        } else {
            if (area_code.startsWith("+")) {
                area_code = area_code.substring(1, area_code.length());
            }
        }
        Map<String, String> param = new HashMap<>();
        param.put("area_code", area_code);
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

    //登录
    private void login() {
        String phone = etAccount.getText().toString().trim();
        String verification_code = etCode.getText().toString().trim();
        String area_code = tvArea.getText().toString().trim();
        if (TextUtils.isEmpty(area_code)) {
            area_code = "86";
        } else {
            if (area_code.startsWith("+")) {
                area_code = area_code.substring(1, area_code.length());
            }
        }
        Map<String, String> param = new HashMap<>();
        param.put("area_code", area_code);
        param.put("phone", phone);
        param.put("verification_code", verification_code); //类型 1-登录
        Call<LoginEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .login(ParamHelper.formatData(param));
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
                            PaperUtils.saveUserInfo(memberInfoEntity);
                            sendLoginEvent();
                            finish();
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

    //发送登录成功事件
    private void sendLoginEvent() {
        MsgEvent msgEvent = new MsgEvent();
        msgEvent.setMsg(MsgEvent.EVENT_LOGIN_SUCCESS);
        EventBus.getDefault().post(msgEvent);
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
    protected void onStop() {
        super.onStop();
        stopCountDown();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mHandler != null) {
            stopCountDown();
            mHandler = null;
        }
    }

}
