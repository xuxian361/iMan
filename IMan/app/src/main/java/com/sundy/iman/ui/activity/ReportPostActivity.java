package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;

import com.sundy.iman.R;
import com.sundy.iman.entity.ReportPostEntity;
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
 * Created by sundy on 17/10/4.
 */

public class ReportPostActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_report_post);
        ButterKnife.bind(this);

        initTitle();
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
        param.put("mid", "");
        param.put("session_key", "");
        param.put("post_id", "");
        param.put("creator_id", "");
        param.put("email", email);
        param.put("content", content);
        Call<ReportPostEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .reportPost(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<ReportPostEntity>() {
            @Override
            public void onSuccess(Call<ReportPostEntity> call, Response<ReportPostEntity> response) {

            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<ReportPostEntity> call, Throwable t) {

            }
        });
    }

}
