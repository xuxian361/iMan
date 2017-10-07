package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sundy.iman.R;
import com.sundy.iman.entity.CreatePostEntity;
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
 * Created by sundy on 17/10/6.
 */

public class CreatePostActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.et_subject)
    EditText etSubject;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.fl_tab)
    TagFlowLayout flTab;
    @BindView(R.id.btn_more_tag)
    Button btnMoreTag;
    @BindView(R.id.et_tags)
    EditText etTags;
    @BindView(R.id.btn_expire_time)
    Button btnExpireTime;
    @BindView(R.id.btn_post)
    Button btnPost;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_create_post);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_more_tag, R.id.btn_expire_time, R.id.btn_post})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_more_tag:
                break;
            case R.id.btn_expire_time:
                break;
            case R.id.btn_post:
                createPost();
                break;
        }
    }

    //创建post
    private void createPost() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", "");
        param.put("session_key", "");
        param.put("title", ""); //post标题
        param.put("detail", ""); //post详情
        param.put("tags", ""); //标签
        param.put("location", ""); //位置
        param.put("latitude", "");
        param.put("longitude", "");
        param.put("community_id", ""); //社区ID
        param.put("aging", ""); //时效
        param.put("attachment", ""); //附件 json格式数据:att_type为附件类型，1-图片，2-视频 url：附件存放路径
        Call<CreatePostEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .createPost(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<CreatePostEntity>() {
            @Override
            public void onSuccess(Call<CreatePostEntity> call, Response<CreatePostEntity> response) {

            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<CreatePostEntity> call, Throwable t) {

            }
        });
    }

}
