package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;

import com.sundy.iman.R;
import com.sundy.iman.entity.CommunityListEntity;
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

public class MyCommunityActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.rel_search)
    RelativeLayout relSearch;
    @BindView(R.id.rv_community)
    RecyclerView rvCommunity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_community);
        ButterKnife.bind(this);

        initTitle();
        init();
    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.my_community));
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
        rvCommunity.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    //获取社区列表
    private void getCommunityList() {
        Map<String, String> param = new HashMap<>();
        param.put("type", ""); //类型: 1-全部社区，2-我的社区
        param.put("mid", "");
        param.put("session_key", "");
        param.put("keyword", "");
        param.put("page", ""); //当前页码
        param.put("perpage", ""); //每页显示条数
        Call<CommunityListEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .getCommunityList(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<CommunityListEntity>() {
            @Override
            public void onSuccess(Call<CommunityListEntity> call, Response<CommunityListEntity> response) {

            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<CommunityListEntity> call, Throwable t) {

            }
        });
    }

}
