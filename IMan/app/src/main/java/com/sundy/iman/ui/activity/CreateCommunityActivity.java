package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sundy.iman.R;
import com.sundy.iman.entity.CreateCommunityEntity;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.view.TitleBarView;
import com.zhy.view.flowlayout.TagFlowLayout;

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

public class CreateCommunityActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.fl_tab)
    TagFlowLayout flTab;
    @BindView(R.id.btn_more_tag)
    Button btnMoreTag;
    @BindView(R.id.et_tags)
    EditText etTags;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.ll_location)
    LinearLayout llLocation;
    @BindView(R.id.btn_create)
    Button btnCreate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_create_community);
        ButterKnife.bind(this);

        initTitle();
    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.create_community));
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

    @OnClick({R.id.btn_more_tag, R.id.ll_location, R.id.btn_create})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_more_tag:
                goSelectTags();
                break;
            case R.id.ll_location:
                break;
            case R.id.btn_create:
                createCommunity();
                break;
        }
    }

    //跳转选择标签
    private void goSelectTags() {
        UIHelper.jump(this, SelectTagsActivity.class);
    }

    //创建社区
    private void createCommunity() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", ""); //用户ID
        param.put("session_key", "");
        param.put("name", "");
        param.put("introduction", "");
        param.put("tags", "");
        param.put("province", "");
        param.put("city", "");
        param.put("location", "");
        param.put("latitude", "");
        param.put("longitude", "");
        Call<CreateCommunityEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .createCommunity(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<CreateCommunityEntity>() {
            @Override
            public void onSuccess(Call<CreateCommunityEntity> call, Response<CreateCommunityEntity> response) {

            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<CreateCommunityEntity> call, Throwable t) {

            }
        });
    }

}
