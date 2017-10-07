package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sundy.iman.R;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.view.TitleBarView;

import butterknife.BindView;
import butterknife.ButterKnife;

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
}
