package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;
import com.sundy.iman.R;
import com.sundy.iman.entity.CountryCodeEntity;
import com.sundy.iman.entity.MsgEvent;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.view.DividerItemDecoration;
import com.sundy.iman.view.TitleBarView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sundy on 17/10/9.
 */

public class SelectCountryCodeActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.rv_country_code)
    RecyclerView rvCountryCode;
    private CountryCodeAdapter codeAdapter;
    private List<CountryCodeEntity.DataEntity> listCode = new ArrayList<>();

    private CountryCodeEntity.DataEntity curCountryCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_select_country_code);
        ButterKnife.bind(this);

        initData();
        initTitle();
        initView();
        setData();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            curCountryCode = (CountryCodeEntity.DataEntity) bundle.getSerializable("Country_Code");
            if (curCountryCode == null) {
                curCountryCode = new CountryCodeEntity.DataEntity();
                curCountryCode.setId("1");
                curCountryCode.setCode("+86");
                curCountryCode.setName("中国");
            }
        }
    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.country_code));
        titleBar.setRightTvVisibility(View.VISIBLE);
        titleBar.setRightTvText(getString(R.string.confirm));
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
                if (curCountryCode == null)
                    return;
                MsgEvent msgEvent = new MsgEvent();
                msgEvent.setMsg(MsgEvent.EVENT_GET_COUNTRY_CODE);
                msgEvent.setObj(curCountryCode);
                EventBus.getDefault().post(msgEvent);
                finish();
            }

            @Override
            public void onTitleClick() {

            }
        });
    }

    private void initView() {
        rvCountryCode.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvCountryCode.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        codeAdapter = new CountryCodeAdapter(R.layout.item_country_code, listCode);
        codeAdapter.setOnItemClickListener(onItemClickListener);
        rvCountryCode.setAdapter(codeAdapter);
    }

    private void setData() {
        listCode.clear();
        CountryCodeEntity.DataEntity dataEntity1 = new CountryCodeEntity.DataEntity();
        dataEntity1.setId("1");
        dataEntity1.setCode("+86");
        dataEntity1.setName("中国");
        listCode.add(dataEntity1);
        CountryCodeEntity.DataEntity dataEntity2 = new CountryCodeEntity.DataEntity();
        dataEntity2.setId("2");
        dataEntity2.setCode("+852");
        dataEntity2.setName("香港");
        listCode.add(dataEntity2);

        codeAdapter.setNewData(listCode);
        codeAdapter.notifyDataSetChanged();
    }

    private class CountryCodeAdapter extends BaseQuickAdapter<CountryCodeEntity.DataEntity, BaseViewHolder> {

        public CountryCodeAdapter(@LayoutRes int layoutResId, @Nullable List<CountryCodeEntity.DataEntity> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, CountryCodeEntity.DataEntity item) {
            TextView tv_name = helper.getView(R.id.tv_name);
            ImageView iv_check = helper.getView(R.id.iv_check);
            tv_name.setText(item.getName() + "(" + item.getCode() + ")");
            if (curCountryCode.equals(item)) {
                iv_check.setVisibility(View.VISIBLE);
                tv_name.setSelected(true);
            } else {
                iv_check.setVisibility(View.GONE);
                tv_name.setSelected(false);
            }
        }
    }

    private BaseQuickAdapter.OnItemClickListener onItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            Logger.i("--->position = " + position);
            CountryCodeEntity.DataEntity dataEntity = listCode.get(position);
            curCountryCode = dataEntity;
            codeAdapter.notifyDataSetChanged();
        }
    };
}
