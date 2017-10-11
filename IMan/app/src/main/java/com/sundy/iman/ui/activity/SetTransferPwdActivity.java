package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sundy.iman.MainApp;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.UpdateTransferPwdEntity;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.view.TitleBarView;
import com.sundy.iman.view.dialog.CommonDialog;

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

public class SetTransferPwdActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.et_confirm_pwd)
    EditText etConfirmPwd;
    @BindView(R.id.btn_confirm)
    TextView btnConfirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_set_transfer_pwd);
        ButterKnife.bind(this);

        initTitle();
        init();
    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.set_transfer_pwd));
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
        etPwd.addTextChangedListener(textWatcher);
        etConfirmPwd.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String password = etPwd.getText().toString().trim();
            String confirmPwd = etConfirmPwd.getText().toString().trim();
            if (password.length() == 6 && confirmPwd.length() == 6 && password.equals(confirmPwd)) {
                btnConfirm.setSelected(true);
                btnConfirm.setEnabled(true);
            } else {
                btnConfirm.setSelected(false);
                btnConfirm.setEnabled(false);
            }
        }
    };

    @OnClick(R.id.btn_confirm)
    public void onViewClicked() {
        updateTransferPwd();
    }

    //更新支付密码接口
    private void updateTransferPwd() {
        String password = etPwd.getText().toString().trim();
        String confirm_password = etConfirmPwd.getText().toString().trim();
        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("password", password);
        param.put("confirm_password", confirm_password);
        showProgress();
        Call<UpdateTransferPwdEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .updateTransferPwd(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<UpdateTransferPwdEntity>() {
            @Override
            public void onSuccess(Call<UpdateTransferPwdEntity> call, Response<UpdateTransferPwdEntity> response) {
                hideProgress();
                UpdateTransferPwdEntity updateTransferPwdEntity = response.body();
                if (updateTransferPwdEntity != null) {
                    int code = updateTransferPwdEntity.getCode();
                    String msg = updateTransferPwdEntity.getMsg();
                    if (code == Constants.CODE_SUCCESS) {
                        showSuccessDialog();
                    } else {
                        MainApp.getInstance().showToast(msg);
                    }
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

    private void showSuccessDialog() {
        final CommonDialog dialog = new CommonDialog(this);
        dialog.getTitle().setVisibility(View.GONE);
        dialog.getContent().setText(getString(R.string.success));
        dialog.getBtnCancel().setVisibility(View.GONE);
        dialog.setCancelable(false);
        dialog.setOnBtnClick(new CommonDialog.OnBtnClick() {
            @Override
            public void onOkClick() {
                dialog.dismiss();
                finish();
            }
        });
    }
}
