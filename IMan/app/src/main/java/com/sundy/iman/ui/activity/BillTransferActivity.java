package com.sundy.iman.ui.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.sundy.iman.MainApp;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.BillTransferEntity;
import com.sundy.iman.entity.CheckTransferPwdEntity;
import com.sundy.iman.entity.MsgEvent;
import com.sundy.iman.greendao.ImUserInfo;
import com.sundy.iman.helper.DbHelper;
import com.sundy.iman.helper.ImageHelper;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.utils.NetWorkUtils;
import com.sundy.iman.view.TitleBarView;
import com.sundy.iman.view.dialog.CommonDialog;
import com.sundy.iman.view.dialog.PayBillPasswordDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sundy on 17/11/8.
 */

public class BillTransferActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.tv_rel_title)
    TextView tvRelTitle;
    @BindView(R.id.iv_header)
    CircleImageView ivHeader;
    @BindView(R.id.tv_send_name)
    TextView tvSendName;
    @BindView(R.id.et_amount)
    EditText etAmount;
    @BindView(R.id.et_note)
    EditText etNote;
    @BindView(R.id.btn_send)
    TextView btnSend;
    private String toChatUsername;

    private static final int DEFAULT_MAX_INTEGER_LENGTH = 10;
    private static final int DEFAULT_DECIMAL_NUMBER = 2;

    private static final InputFilter[] INPUT_FILTER_ARRAY = new InputFilter[1];

    private PayBillPasswordDialog passwordDialog;

    /**
     * 保留小数点后多少位
     */
    private int mDecimalNumber = DEFAULT_DECIMAL_NUMBER;
    /**
     * 允许最大的整数多少位
     */
    private int mMaxIntegralLength = DEFAULT_MAX_INTEGER_LENGTH;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_bill_transfer);
        ButterKnife.bind(this);

        initTitle();
        initData();
        init();
    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.send_imcoin));
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

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            toChatUsername = bundle.getString("toChatUsername");
        }
    }

    private void init() {
        passwordDialog = new PayBillPasswordDialog(this);

        ImUserInfo imUserInfoEntity = DbHelper.getInstance().getUserInfoByHxId(toChatUsername);
        if (imUserInfoEntity != null) {
            String name = imUserInfoEntity.getUsername();
            String avatar = imUserInfoEntity.getProfile_image();

            ImageHelper.displayPortrait(this, avatar, ivHeader);
            tvSendName.setText(name);
        }

        etAmount.addTextChangedListener(watcher);
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.length() > 0) {
                String inputContent = charSequence.toString();
                if (inputContent.contains(".")) {
                    int maxLength = inputContent.indexOf(".") + mDecimalNumber + 1;
                    INPUT_FILTER_ARRAY[0] = new InputFilter.LengthFilter(maxLength);
                } else {
                    INPUT_FILTER_ARRAY[0] = new InputFilter.LengthFilter(mMaxIntegralLength);
                }
                etAmount.setFilters(INPUT_FILTER_ARRAY);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String amount = etAmount.getText().toString().trim();
            if (!TextUtils.isEmpty(amount) && !amount.endsWith(".") && !amount.startsWith(".")) {
                btnSend.setSelected(true);
                btnSend.setEnabled(true);
            } else {
                btnSend.setSelected(false);
                btnSend.setEnabled(false);
            }
        }
    };

    @OnClick(R.id.btn_send)
    public void onViewClicked() {
        if (!NetWorkUtils.isNetAvailable(this)) {
            MainApp.getInstance().showToast(getString(R.string.network_not_available));
            return;
        }
        showEnterPwdDialog();
    }

    //显示输入支付密码弹框
    private void showEnterPwdDialog() {
        if (passwordDialog != null) {
            passwordDialog.setCancelable(false);
            passwordDialog.setCanceledOnTouchOutside(false);
            passwordDialog.setOnPwdListener(new PayBillPasswordDialog.OnPwdListener() {
                @Override
                public void onPwdVerify(String pwd) {
                    Logger.e("------>输入的支付密码是: " + pwd);
                    verifyPwd(pwd);
                }
            });
            passwordDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    autoPopupSoftInput(passwordDialog.getViewPwd());
                }
            });
            passwordDialog.show();
            passwordDialog.getViewPwd().setText("");
            final String amount = etAmount.getText().toString().trim();
            passwordDialog.setAmount(amount);
        }
    }

    //隐藏支付密码弹框
    private void hidePwdDialog() {
        if (passwordDialog != null) {
            passwordDialog.dismiss();
        }
    }

    //校验支付密码
    private void verifyPwd(String pwd) {
        if (NetWorkUtils.isNetAvailable(this)) {
            Map<String, String> param = new HashMap<>();
            param.put("mid", PaperUtils.getMId());
            param.put("session_key", PaperUtils.getSessionKey());
            param.put("password", pwd);
            showProgress();
            Call<CheckTransferPwdEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                    .checkTransferPwd(ParamHelper.formatData(param));
            call.enqueue(new RetrofitCallback<CheckTransferPwdEntity>() {
                @Override
                public void onSuccess(Call<CheckTransferPwdEntity> call, Response<CheckTransferPwdEntity> response) {
                    CheckTransferPwdEntity checkTransferPwdEntity = response.body();
                    if (checkTransferPwdEntity != null) {
                        int code = checkTransferPwdEntity.getCode();
                        String msg = checkTransferPwdEntity.getMsg();
                        if (code == Constants.CODE_SUCCESS) {
                            closeKeyboard();
                            sendTransfer();
                        } else {
                            passwordDialog.getViewPwd().setText("");
                            MainApp.getInstance().showToast(msg);
                        }
                    }
                }

                @Override
                public void onAfter() {
                    hideProgress();
                }

                @Override
                public void onFailure(Call<CheckTransferPwdEntity> call, Throwable t) {
                    hideProgress();
                }
            });
        }
    }

    //转账
    private void sendTransfer() {
        ImUserInfo imUserInfoEntity = DbHelper.getInstance().getUserInfoByHxId(toChatUsername);
        if (imUserInfoEntity != null) {

            final String remark = etNote.getText().toString().trim();
            String goal_id = imUserInfoEntity.getUserId();
            final String income = etAmount.getText().toString().trim();
            final String gold_name = imUserInfoEntity.getUsername();

            Map<String, String> param = new HashMap<>();
            param.put("mid", PaperUtils.getMId());
            param.put("session_key", PaperUtils.getSessionKey());
            param.put("goal_id", goal_id); //转账对象ID
            param.put("income", income); //转账金额
            param.put("remark", remark); //备注
            Call<BillTransferEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                    .billTransfer(ParamHelper.formatData(param));
            call.enqueue(new RetrofitCallback<BillTransferEntity>() {
                @Override
                public void onSuccess(Call<BillTransferEntity> call, Response<BillTransferEntity> response) {
                    BillTransferEntity billTransferEntity = response.body();
                    if (billTransferEntity != null) {
                        int code = billTransferEntity.getCode();
                        String msg = billTransferEntity.getMsg();
                        if (code == Constants.CODE_SUCCESS) {
                            hidePwdDialog();
                            sendImcoinSuccessEvent(income);
                            showSuccessView(gold_name, income);
                        } else {
                            showFailDialog(msg);
                        }
                    }
                }

                @Override
                public void onAfter() {
                }

                @Override
                public void onFailure(Call<BillTransferEntity> call, Throwable t) {
                }
            });
        }
    }

    //跳转显示成功转账页面
    private void showSuccessView(String gold_name, String income) {
        finish();
        Bundle bundle = new Bundle();
        bundle.putString("amount", income);
        bundle.putString("username", gold_name);
        UIHelper.jump(this, SendImcoinSuccessActivity.class, bundle);
    }

    //发送Event 通知刷新消息列表
    private void sendImcoinSuccessEvent(String income) {
        MsgEvent msgEvent = new MsgEvent();
        msgEvent.setMsg(MsgEvent.EVENT_SEND_IMCOIN_SUCCESS);
        msgEvent.setData(income);
        EventBus.getDefault().post(msgEvent);
    }

    //显示失败弹框
    private void showFailDialog(String msg) {
        final CommonDialog dialog = new CommonDialog(this);
        dialog.getTitle().setVisibility(View.GONE);
        dialog.getContent().setText(msg);
        dialog.getBtnCancel().setVisibility(View.GONE);
        dialog.getBtnOk().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (passwordDialog != null) {
            passwordDialog.dismiss();
            passwordDialog = null;
        }
    }
}
