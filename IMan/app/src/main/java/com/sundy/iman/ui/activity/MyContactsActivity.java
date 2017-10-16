package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sundy.iman.R;
import com.sundy.iman.entity.MemberInfoEntity;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.view.TitleBarView;

import java.util.ArrayList;
import java.util.List;

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

    private ContactAdapter contactAdapter;
    private List<MemberInfoEntity> listContact = new ArrayList<>();

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

        contactAdapter = new ContactAdapter(R.layout.item_contact, listContact);
        View view_community = getLayoutInflater().inflate(R.layout.view_my_community_header, null);
        view_community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goMyCommunity();
            }
        });

        contactAdapter.addHeaderView(view_community);
        rvContact.setAdapter(contactAdapter);
    }

    //跳转我的社区列表
    private void goMyCommunity() {
        UIHelper.jump(this, MyCommunityActivity.class);
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


    private class ContactAdapter extends BaseQuickAdapter<MemberInfoEntity, BaseViewHolder> {

        public ContactAdapter(@LayoutRes int layoutResId, @Nullable List<MemberInfoEntity> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, MemberInfoEntity item) {

        }
    }
}
