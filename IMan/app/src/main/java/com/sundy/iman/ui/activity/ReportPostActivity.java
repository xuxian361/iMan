package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sundy.iman.MainApp;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.ReportPostEntity;
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

public class ReportPostActivity extends BaseActivity {


    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.tv_bytes)
    TextView tvBytes;
    @BindView(R.id.btn_confirm)
    TextView btnConfirm;

    private String post_id;
    private String creator_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_report_post);
        ButterKnife.bind(this);

        initData();
        initTitle();
        init();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            post_id = bundle.getString("post_id");
            creator_id = bundle.getString("creator_id");
        }
    }

    private void init() {
        etEmail.addTextChangedListener(textWatcher);
        etContent.addTextChangedListener(textWatcher);

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
            canBtnClick();
        }
    };

    //判断可否点击确认按钮
    private void canBtnClick() {
        String email = etEmail.getText().toString().trim();
        String content = etContent.getText().toString().trim();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(content)) {
            btnConfirm.setSelected(false);
            btnConfirm.setEnabled(false);
        } else {
            btnConfirm.setSelected(true);
            btnConfirm.setEnabled(true);
        }
    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.report));
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

    @OnClick(R.id.btn_confirm)
    public void onViewClicked() {
        reportPost();
    }

    //举报Post
    private void reportPost() {
        String email = etEmail.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("post_id", post_id);
        param.put("creator_id", creator_id);
        param.put("email", email);
        param.put("content", content);
        showProgress();
        Call<ReportPostEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .reportPost(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<ReportPostEntity>() {
            @Override
            public void onSuccess(Call<ReportPostEntity> call, Response<ReportPostEntity> response) {
                ReportPostEntity reportPostEntity = response.body();
                if (reportPostEntity != null) {
                    int code = reportPostEntity.getCode();
                    String msg = reportPostEntity.getMsg();
                    if (code == Constants.CODE_SUCCESS) {
                        showSuccessDialog();
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
            public void onFailure(Call<ReportPostEntity> call, Throwable t) {

            }
        });
    }

    //显示提交成功弹框
    private void showSuccessDialog() {
        final CommonDialog dialog = new CommonDialog(this);
        dialog.getTitle().setVisibility(View.GONE);
        dialog.getContent().setText(getString(R.string.report_success));
        dialog.getBtnCancel().setVisibility(View.GONE);
        dialog.getBtnOk().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });
    }
}
