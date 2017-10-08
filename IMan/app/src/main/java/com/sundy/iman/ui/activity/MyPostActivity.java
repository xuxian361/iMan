package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sundy.iman.R;
import com.sundy.iman.entity.PostListEntity;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.view.TitleBarView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sundy on 17/10/5.
 */

public class MyPostActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.rv_post)
    RecyclerView rvPost;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_post);
        ButterKnife.bind(this);

        initTitle();
        init();
    }

    private void init() {
        rvPost.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.my_post));
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

    //获取post列表
    private void getPostList() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", "");
        param.put("session_key", "");
        param.put("type", ""); //类型: 0-加入，1-退出
        param.put("community_id", "");
        param.put("page", "");
        param.put("perpage", "");
        Call<PostListEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .getPostList(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<PostListEntity>() {
            @Override
            public void onSuccess(Call<PostListEntity> call, Response<PostListEntity> response) {

            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<PostListEntity> call, Throwable t) {

            }
        });
    }

}
