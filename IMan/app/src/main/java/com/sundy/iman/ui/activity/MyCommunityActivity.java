package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.CommunityItemEntity;
import com.sundy.iman.entity.CommunityListEntity;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.view.CustomLoadMoreView;
import com.sundy.iman.view.TitleBarView;
import com.sundy.iman.view.WrapContentLinearLayoutManager;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private String keyword = "";
    private int page = 1; //当前页码
    private int perpage = 10; //每页显示条数
    private boolean canLoadMore = true;
    private MyCommunityAdapter communityAdapter;
    private List<CommunityItemEntity> listCommunity = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_community);
        ButterKnife.bind(this);

        initTitle();
        init();
        if (listCommunity != null)
            listCommunity.clear();
        getCommunityList();
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
        rvCommunity.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        communityAdapter = new MyCommunityAdapter(R.layout.item_my_community, listCommunity);

        communityAdapter.openLoadAnimation();
        communityAdapter.isFirstOnly(false);
        communityAdapter.setLoadMoreView(new CustomLoadMoreView());
        communityAdapter.setEnableLoadMore(true);
        communityAdapter.setOnLoadMoreListener(onLoadMoreListener, rvCommunity);

        rvCommunity.setAdapter(communityAdapter);
    }

    //获取社区列表
    private void getCommunityList() {
        Map<String, String> param = new HashMap<>();
        param.put("type", "2"); //1-全部社区, 2-我的社区, 3-发布广告的社区搜索, 4-加入推广社区搜索
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("keyword", keyword);
        param.put("tags", "");
        param.put("province", "");
        param.put("city", "");
        param.put("page", page + ""); //当前页码
        param.put("perpage", perpage + ""); //每页显示条数
        Call<CommunityListEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .getCommunityList(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<CommunityListEntity>() {
            @Override
            public void onSuccess(Call<CommunityListEntity> call, Response<CommunityListEntity> response) {
                CommunityListEntity communityListEntity = response.body();
                if (communityListEntity != null) {
                    int code = communityListEntity.getCode();
                    String msg = communityListEntity.getMsg();
                    if (code == Constants.CODE_SUCCESS) {
                        CommunityListEntity.DataEntity dataEntity = communityListEntity.getData();
                        if (dataEntity != null) {
                            showData(dataEntity.getList());
                        }
                    }
                }
            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<CommunityListEntity> call, Throwable t) {

            }
        });
    }

    private void showData(List<CommunityItemEntity> listData) {
        try {
            if (listData.size() == 0) {
                canLoadMore = false;
                communityAdapter.loadMoreEnd();
            } else {
                page = page + 1;
                canLoadMore = true;
                communityAdapter.loadMoreComplete();
                for (int i = 0; i < listData.size(); i++) {
                    CommunityItemEntity item = listData.get(i);
                    if (item != null) {
                        listCommunity.add(item);
                    }
                }
                communityAdapter.setNewData(listCommunity);
                communityAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyCommunityAdapter extends BaseQuickAdapter<CommunityItemEntity, BaseViewHolder> {

        public MyCommunityAdapter(@LayoutRes int layoutResId, @Nullable List<CommunityItemEntity> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, CommunityItemEntity item) {
            TextView tvCommunityName = helper.getView(R.id.tv_community_name);
            TextView tvIntroduction = helper.getView(R.id.tv_community_introduction);
            tvCommunityName.setText(item.getName());
            String introduction = item.getIntroduction();
            if (TextUtils.isEmpty(introduction)) {
                tvIntroduction.setText(getString(R.string.community_introduction_default));
            } else {
                tvIntroduction.setText(introduction);
            }
            final TagFlowLayout flTab = helper.getView(R.id.fl_tab);
            String tagStr = item.getTags();
            if (TextUtils.isEmpty(tagStr)) {
                flTab.setVisibility(View.GONE);
            } else {
                flTab.setVisibility(View.VISIBLE);
                String[] tagArr = tagStr.split(",");
                if (tagArr != null && tagArr.length > 0) {
                    final TagAdapter<String> adapter_Tag = new TagAdapter<String>(tagArr) {
                        @Override
                        public View getView(FlowLayout parent, int position, String item) {
                            TextView tv = (TextView) getLayoutInflater().inflate(R.layout.item_tag_cannot_select,
                                    flTab, false);
                            if (!TextUtils.isEmpty(item)) {
                                tv.setText(item);
                            }
                            return tv;
                        }
                    };
                    flTab.setAdapter(adapter_Tag);
                }
            }
        }
    }

    private BaseQuickAdapter.RequestLoadMoreListener onLoadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            Logger.e("----->onLoadMoreRequested ");
            Logger.e("--->page = " + page);
            if (canLoadMore) {
                getCommunityList();
            }
        }
    };

}
