package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;
import com.sundy.iman.R;
import com.sundy.iman.entity.ChangeLanguageEntity;
import com.sundy.iman.entity.LanguageEntity;
import com.sundy.iman.entity.MsgEvent;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.view.DividerItemDecoration;
import com.sundy.iman.view.TitleBarView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * 切换语言
 * Created by sundy on 17/10/10.
 */

public class ChangeLanguageActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.rv_language)
    RecyclerView rvLanguage;

    private LanguageAdapter languageAdapter;
    private List<LanguageEntity.DataEntity> listLanguage = new ArrayList();
    private String curLanguage = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_change_language);
        ButterKnife.bind(this);

        initTitle();
        initView();
        setData();
    }

    private void initView() {
        rvLanguage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvLanguage.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        languageAdapter = new LanguageAdapter(R.layout.item_language, listLanguage);
        languageAdapter.setOnItemClickListener(onItemClickListener);
        rvLanguage.setAdapter(languageAdapter);
    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.language));
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
                if (TextUtils.isEmpty(curLanguage))
                    return;
                PaperUtils.setLanguage(curLanguage);
                if (PaperUtils.isLogin()) {
                    changeLanguage();
                }
                //发送切换语言成功事件
                MsgEvent msgEvent = new MsgEvent();
                msgEvent.setMsg(MsgEvent.EVENT_CHANGE_LANGUAGE);
                EventBus.getDefault().post(msgEvent);
                finish();
            }

            @Override
            public void onTitleClick() {

            }
        });
    }

    //修改语言接口
    private void changeLanguage() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        Call<ChangeLanguageEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .changeLanguage(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<ChangeLanguageEntity>() {
            @Override
            public void onSuccess(Call<ChangeLanguageEntity> call, Response<ChangeLanguageEntity> response) {

            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<ChangeLanguageEntity> call, Throwable t) {

            }
        });
    }


    private void setData() {
        curLanguage = PaperUtils.getLanguage();

        listLanguage.clear();
        LanguageEntity.DataEntity dataEntity1 = new LanguageEntity.DataEntity();
        dataEntity1.setId("1");
        dataEntity1.setTitle("中文简体");
        dataEntity1.setLang("sc");
        listLanguage.add(dataEntity1);
        LanguageEntity.DataEntity dataEntity2 = new LanguageEntity.DataEntity();
        dataEntity2.setId("2");
        dataEntity2.setTitle("中文繁体");
        dataEntity2.setLang("tc");
        listLanguage.add(dataEntity2);
        LanguageEntity.DataEntity dataEntity3 = new LanguageEntity.DataEntity();
        dataEntity3.setId("3");
        dataEntity3.setTitle("English");
        dataEntity3.setLang("en");
        listLanguage.add(dataEntity3);

        languageAdapter.setNewData(listLanguage);
        languageAdapter.notifyDataSetChanged();
    }

    private class LanguageAdapter extends BaseQuickAdapter<LanguageEntity.DataEntity, BaseViewHolder> {

        public LanguageAdapter(@LayoutRes int layoutResId, @Nullable List<LanguageEntity.DataEntity> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, LanguageEntity.DataEntity item) {
            TextView tv_name = helper.getView(R.id.tv_name);
            ImageView iv_check = helper.getView(R.id.iv_check);
            tv_name.setText(item.getTitle());
            if (curLanguage.equals(item.getLang())) {
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
            LanguageEntity.DataEntity dataEntity = listLanguage.get(position);
            curLanguage = dataEntity.getLang();
            languageAdapter.notifyDataSetChanged();
        }
    };
}
