package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sundy.iman.R;
import com.sundy.iman.entity.MyPromoteCommunityListEntity;
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
 * Created by sundy on 17/10/6.
 */

public class MyPromoteCommunityActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBarView titleBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_promote_community);
        ButterKnife.bind(this);

        initTitle();

    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.promote_community));
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

    //我的推广社区列表
    private void getMyPromoteCommunity() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", "");
        param.put("session_key", "");
        param.put("page", "");
        param.put("perpage", "");
        Call<MyPromoteCommunityListEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .getMyPromoteCommunityList(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<MyPromoteCommunityListEntity>() {
            @Override
            public void onSuccess(Call<MyPromoteCommunityListEntity> call, Response<MyPromoteCommunityListEntity> response) {

            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<MyPromoteCommunityListEntity> call, Throwable t) {

            }
        });
    }

}
