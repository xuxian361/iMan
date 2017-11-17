package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hyphenate.util.DateUtils;
import com.orhanobut.logger.Logger;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.LastPostItemEntity;
import com.sundy.iman.entity.LastPostListEntity;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.utils.NetWorkUtils;
import com.sundy.iman.view.CustomLoadMoreView;
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
 * Created by sundy on 17/11/6.
 */

public class LatestPostActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.rv_post)
    RecyclerView rvPost;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.tv_try_again)
    TextView tvTryAgain;
    @BindView(R.id.ll_no_net_content)
    LinearLayout llNoNetContent;

    private int page = 1; //当前页码
    private int perpage = 10; //每页显示条数
    private boolean canLoadMore = true;
    private LastPostAdapter myPostAdapter;
    private List<LastPostItemEntity> listPost = new ArrayList<>();
    private WrapContentLinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_last_post);
        ButterKnife.bind(this);

        initTitle();
        init();
        page = 1;
        if (listPost != null)
            listPost.clear();
        getLastPost();
    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.message));
        titleBar.setRightIvBg(R.mipmap.icon_msg_add_small);
        titleBar.setRightIvVisibility(View.VISIBLE);
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
                goAddCommunity();
            }

            @Override
            public void onRightTxtClick() {

            }

            @Override
            public void onTitleClick() {

            }
        });
    }

    //跳转添加社区
    private void goAddCommunity() {
        UIHelper.jump(this, AddCommunityActivity.class);
    }

    private void init() {
        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                page = 1;
                if (listPost != null)
                    listPost.clear();
                myPostAdapter.notifyDataSetChanged();
                getLastPost();
            }
        });

        linearLayoutManager = new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvPost.setLayoutManager(linearLayoutManager);

        myPostAdapter = new LastPostAdapter(R.layout.item_community_msg, listPost);
        myPostAdapter.setLoadMoreView(new CustomLoadMoreView());
        myPostAdapter.setPreLoadNumber(perpage);
        myPostAdapter.setOnLoadMoreListener(onLoadMoreListener, rvPost);
        rvPost.setAdapter(myPostAdapter);

        rvPost.addOnScrollListener(onScrollListener);
    }

    //获取最新消息列表
    private void getLastPost() {
        if (NetWorkUtils.isNetAvailable(this)) {
            llNoNetContent.setVisibility(View.GONE);

            Map<String, String> param = new HashMap<>();
            param.put("mid", PaperUtils.getMId());
            param.put("session_key", PaperUtils.getSessionKey());
            param.put("page", page + "");
            param.put("perpage", perpage + "");
            Call<LastPostListEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                    .getLastPostList(ParamHelper.formatData(param));
            call.enqueue(new RetrofitCallback<LastPostListEntity>() {
                @Override
                public void onSuccess(Call<LastPostListEntity> call, Response<LastPostListEntity> response) {
                    LastPostListEntity lastPostListEntity = response.body();
                    if (lastPostListEntity != null) {
                        int code = lastPostListEntity.getCode();
                        String msg = lastPostListEntity.getMsg();
                        if (code == Constants.CODE_SUCCESS) {
                            LastPostListEntity.DataEntity dataEntity = lastPostListEntity.getData();
                            if (dataEntity != null) {
                                showData(dataEntity.getList());
                            }
                        }
                    }
                }

                @Override
                public void onAfter() {
                    if (swipeRefresh != null)
                        swipeRefresh.setRefreshing(false);
                }

                @Override
                public void onFailure(Call<LastPostListEntity> call, Throwable t) {
                    if (swipeRefresh != null)
                        swipeRefresh.setRefreshing(false);
                }
            });
        } else {
            if (swipeRefresh != null)
                swipeRefresh.setRefreshing(false);
            myPostAdapter.loadMoreEnd();

            llNoNetContent.setVisibility(View.VISIBLE);
        }
    }

    private void showData(List<LastPostItemEntity> listData) {
        try {
            if (listData.size() < perpage) {
                canLoadMore = false;
                myPostAdapter.loadMoreEnd();
            } else {
                page = page + 1;
                canLoadMore = true;
                myPostAdapter.loadMoreComplete();
            }
            for (int i = 0; i < listData.size(); i++) {
                LastPostItemEntity item = listData.get(i);
                if (item != null) {
                    listPost.add(item);
                }
            }
            myPostAdapter.setNewData(listPost);
            myPostAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.tv_try_again)
    public void onViewClicked() {
        swipeRefresh.setRefreshing(true);
        page = 1;
        if (listPost != null)
            listPost.clear();
        myPostAdapter.notifyDataSetChanged();
        getLastPost();
    }

    private class LastPostAdapter extends BaseQuickAdapter<LastPostItemEntity, BaseViewHolder> {

        public LastPostAdapter(@LayoutRes int layoutResId, @Nullable List<LastPostItemEntity> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, LastPostItemEntity item) {
            TextView tv_community_name = helper.getView(R.id.tv_community_name);
            TextView tv_time = helper.getView(R.id.tv_time);
            TextView tv_title = helper.getView(R.id.tv_title);
            ImageView iv_imcoin = helper.getView(R.id.iv_imcoin);
            LinearLayout ll_item = helper.getView(R.id.ll_item);


            final String community_id = item.getId();
            String community_name = item.getName();
            LastPostItemEntity.PostInfo postInfo = item.getPost_info();
            if (postInfo != null) {
                String post_title = postInfo.getTitle();
                String create_time = postInfo.getCreate_time();
                String type = postInfo.getType(); //post 类型： 1 - 普通；2 - 广告

                tv_community_name.setText(community_name);
                if (!TextUtils.isEmpty(create_time)) {
                    try {
                        long msgTime = Long.parseLong(create_time) * 1000;
                        tv_time.setText(DateUtils.getTimestampString(new Date(msgTime)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                tv_title.setText(post_title);
                if (type.equals("1")) {
                    iv_imcoin.setVisibility(View.GONE);
                } else {
                    iv_imcoin.setVisibility(View.VISIBLE);
                }

                ll_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goCommunityDetail(community_id);
                    }
                });
            }
        }
    }

    //跳转社区详情/社区消息列表
    private void goCommunityDetail(String community_id) {
        Bundle bundle = new Bundle();
        bundle.putString("community_id", community_id);
        UIHelper.jump(this, CommunityMsgListActivity.class, bundle);
    }

    private BaseQuickAdapter.RequestLoadMoreListener onLoadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            if (canLoadMore) {
                Logger.e("----->onLoadMoreRequested ");
                Logger.e("--->page = " + page);
                getLastPost();
            } else {
                myPostAdapter.loadMoreEnd();
            }
        }
    };

    //解决滑动冲突
    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        private int lastVisibleItemPosition;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            //第一个可视View 的位置
            int FirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            rvPost.setEnabled(FirstVisibleItemPosition == 0);
            //最后一个可视View 的位置
            lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }
    };

}
