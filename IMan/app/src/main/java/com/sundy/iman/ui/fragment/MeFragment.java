package com.sundy.iman.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.sundy.iman.R;
import com.sundy.iman.entity.TagListEntity;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.ui.activity.LoginActivity;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sundy on 17/9/14.
 */

public class MeFragment extends BaseFragment {

    @BindView(R.id.iv_header)
    ImageView ivHeader;
    @BindView(R.id.btn_login)
    Button btnLogin;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    private void getTagList() {
        Call<TagListEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .getTagList(ParamHelper.formatData(new HashMap<String, String>()));
        call.enqueue(new RetrofitCallback<TagListEntity>() {
            @Override
            public void onSuccess(Call<TagListEntity> call, Response<TagListEntity> response) {
                try {
                    if (response != null) {
                        TagListEntity entity = response.body();
                        if (entity != null) {
                            TagListEntity.DataEntity dataEntity = entity.getData();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<TagListEntity> call, Throwable t) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_header, R.id.btn_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_header:
                break;
            case R.id.btn_login:
                goLogin();
                break;
        }
    }

    private void goLogin() {
        UIHelper.jump(mContext, LoginActivity.class);
    }
}
