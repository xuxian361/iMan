package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;

import com.sundy.iman.R;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.view.TitleBarView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sundy on 17/10/5.
 */

public class MyContactsActivity extends BaseActivity {

    @BindView(R.id.rel_search)
    RelativeLayout relSearch;
    @BindView(R.id.rv_contact)
    RecyclerView rvContact;
    @BindView(R.id.title_bar)
    TitleBarView titleBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_contacts);
        ButterKnife.bind(this);

        initTitle();
        init();
    }

    private void init() {
        rvContact.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.contacts));
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
