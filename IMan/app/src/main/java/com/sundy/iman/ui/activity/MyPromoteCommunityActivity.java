package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.MyPromoteCommunityItemEntity;
import com.sundy.iman.entity.MyPromoteCommunityListEntity;
import com.sundy.iman.entity.StaticContentEntity;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.utils.DateUtils;
import com.sundy.iman.view.CustomLoadMoreView;
import com.sundy.iman.view.DividerItemDecoration;
import com.sundy.iman.view.TitleBarView;
import com.sundy.iman.view.WrapContentLinearLayoutManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sundy on 17/10/6.
 */

public class MyPromoteCommunityActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.rel_search)
    RelativeLayout relSearch;
    @BindView(R.id.rv_community)
    RecyclerView rvCommunity;

    private int page = 1; //当前页码
    private int perpage = 10; //每页显示条数
    private boolean canLoadMore = true;
    private MyCommunityAdapter communityAdapter;
    private List<MyPromoteCommunityItemEntity> listCommunity = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_promote_community);
        ButterKnife.bind(this);

        initTitle();
        init();
        if (listCommunity != null)
            listCommunity.clear();
        getMyPromoteCommunity();
    }

    private void init() {
        rvCommunity.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvCommunity.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        communityAdapter = new MyCommunityAdapter(R.layout.item_my_promote_community, listCommunity);
        communityAdapter.openLoadAnimation();
        communityAdapter.isFirstOnly(false);
        communityAdapter.setLoadMoreView(new CustomLoadMoreView());
        communityAdapter.setEnableLoadMore(true);
        communityAdapter.setOnLoadMoreListener(onLoadMoreListener, rvCommunity);
        rvCommunity.setAdapter(communityAdapter);
    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.promote_community));
        titleBar.setRightIvVisibility(View.VISIBLE);
        titleBar.setRightIvBg(R.mipmap.icon_question);
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
                getStaticContent(Constants.TYPE_PROMOTE_COMMUNITY_QUESTION);
            }

            @Override
            public void onRightTxtClick() {

            }

            @Override
            public void onTitleClick() {

            }
        });
    }

    //获取静态内容
    private void getStaticContent(int type) {
        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("type", type + "");
        Call<StaticContentEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .getStaticContent(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<StaticContentEntity>() {
            @Override
            public void onSuccess(Call<StaticContentEntity> call, Response<StaticContentEntity> response) {
                StaticContentEntity staticContentEntity = response.body();
                if (staticContentEntity != null) {
                    int code = staticContentEntity.getCode();
                    if (code == Constants.CODE_SUCCESS) {
                        StaticContentEntity.DataEntity dataEntity = staticContentEntity.getData();
                        if (dataEntity != null) {
                            goWebView(dataEntity);
                        }
                    }
                }
            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<StaticContentEntity> call, Throwable t) {

            }
        });
    }

    //跳转Web View显示H5
    private void goWebView(StaticContentEntity.DataEntity dataEntity) {
        String url = dataEntity.getUrl();
        String title = getString(R.string.specification);
        if (TextUtils.isEmpty(url))
            return;
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("title", title);
        UIHelper.jump(this, WebActivity.class, bundle);
    }

    //我的推广社区列表
    private void getMyPromoteCommunity() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("page", page + "");
        param.put("perpage", perpage + "");
        Call<MyPromoteCommunityListEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .getMyPromoteCommunityList(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<MyPromoteCommunityListEntity>() {
            @Override
            public void onSuccess(Call<MyPromoteCommunityListEntity> call, Response<MyPromoteCommunityListEntity> response) {
                MyPromoteCommunityListEntity communityListEntity = response.body();
                if (communityListEntity != null) {
                    int code = communityListEntity.getCode();
                    String msg = communityListEntity.getMsg();
                    if (code == Constants.CODE_SUCCESS) {
                        MyPromoteCommunityListEntity.DataEntity dataEntity = communityListEntity.getData();
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
            public void onFailure(Call<MyPromoteCommunityListEntity> call, Throwable t) {

            }
        });
    }

    private void showData(List<MyPromoteCommunityItemEntity> listData) {
        try {
            if (listData.size() == 0) {
                canLoadMore = false;
                communityAdapter.loadMoreEnd();
            } else {
                page = page + 1;
                canLoadMore = true;
                communityAdapter.loadMoreComplete();
                for (int i = 0; i < listData.size(); i++) {
                    MyPromoteCommunityItemEntity item = listData.get(i);
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

    @OnClick(R.id.rel_search)
    public void onViewClicked() {
        goJoinPromoteCommunity();
    }

    //跳转搜索加入推广社区
    private void goJoinPromoteCommunity() {
        UIHelper.jump(this, JoinPromoteCommunityActivity.class);
    }

    private class MyCommunityAdapter extends BaseQuickAdapter<MyPromoteCommunityItemEntity, BaseViewHolder> {

        public MyCommunityAdapter(@LayoutRes int layoutResId, @Nullable List<MyPromoteCommunityItemEntity> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final MyPromoteCommunityItemEntity item) {
            TextView tv_community_name = helper.getView(R.id.tv_community_name);
            TextView tv_users = helper.getView(R.id.tv_users);
            TextView tv_id = helper.getView(R.id.tv_id);
            TextView tv_create_date = helper.getView(R.id.tv_create_date);
            RelativeLayout rel_item = helper.getView(R.id.rel_item);

            try {
                String create_time = item.getCreate_time();
                if (create_time != null) {
                    Date date = DateUtils.formatTimeStamp2Date(Long.parseLong(create_time) * 1000);
                    tv_create_date.setText(getString(R.string.since) + " " + DateUtils.formatDate2String(date, "yyyy/MM/dd"));
                } else {
                    tv_create_date.setText("");
                }

                tv_community_name.setText(item.getName());
                tv_id.setText(getString(R.string.id_str) + " " + item.getId());
                tv_users.setText(getString(R.string.acquired_users) + " " + item.getMembers());

                rel_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goCommunityDetail(item.getCommunity_id());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    //跳转社区详情
    private void goCommunityDetail(String community_id) {
        Bundle bundle = new Bundle();
        bundle.putString("community_id", community_id);
        bundle.putString("type", "2"); //类型 1-普通社区,2- 推广社区
        UIHelper.jump(this, CommunityDetailActivity.class, bundle);
    }

    private BaseQuickAdapter.RequestLoadMoreListener onLoadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            Logger.e("----->onLoadMoreRequested ");
            Logger.e("--->page = " + page);
            if (canLoadMore) {
                getMyPromoteCommunity();
            }
        }
    };


}
