package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl;
import com.daimajia.swipe.interfaces.SwipeAdapterInterface;
import com.daimajia.swipe.interfaces.SwipeItemMangerInterface;
import com.daimajia.swipe.util.Attributes;
import com.orhanobut.logger.Logger;
import com.sundy.iman.MainApp;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.CommunityItemEntity;
import com.sundy.iman.entity.CommunityListEntity;
import com.sundy.iman.entity.JoinCommunityEntity;
import com.sundy.iman.entity.MsgEvent;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.utils.NetWorkUtils;
import com.sundy.iman.view.CustomLoadMoreView;
import com.sundy.iman.view.DividerItemDecoration;
import com.sundy.iman.view.TitleBarView;
import com.sundy.iman.view.WrapContentLinearLayoutManager;
import com.sundy.iman.view.dialog.CommonDialog;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.Data;
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
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.tv_add_community)
    TextView tvAddCommunity;
    @BindView(R.id.tv_create_community)
    TextView tvCreateCommunity;
    @BindView(R.id.ll_null_tips)
    LinearLayout llNullTips;
    @BindView(R.id.tv_try_again)
    TextView tvTryAgain;
    @BindView(R.id.ll_no_net_content)
    LinearLayout llNoNetContent;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;

    private String keyword = "";
    private int page = 1; //当前页码
    private int perpage = 10; //每页显示条数
    private boolean canLoadMore = true;
    private MyCommunityAdapter communityAdapter;
    private List<CommunityItemEntity> listCommunity = new ArrayList<>();
    private WrapContentLinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_community);
        ButterKnife.bind(this);

        EventBus.getDefault().register(this);
        initTitle();
        init();
        page = 1;
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
        etSearch.addTextChangedListener(textWatcher);

        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                page = 1;
                if (listCommunity != null)
                    listCommunity.clear();
                communityAdapter.notifyDataSetChanged();
                getCommunityList();
            }
        });

        linearLayoutManager = new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvCommunity.setLayoutManager(linearLayoutManager);
        rvCommunity.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        communityAdapter = new MyCommunityAdapter(R.layout.item_my_community, listCommunity);
        communityAdapter.setLoadMoreView(new CustomLoadMoreView());
        communityAdapter.setPreLoadNumber(perpage);
        communityAdapter.setOnLoadMoreListener(onLoadMoreListener, rvCommunity);
        rvCommunity.setAdapter(communityAdapter);

        rvCommunity.addOnScrollListener(onScrollListener);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            keyword = etSearch.getText().toString().trim();
            page = 1;
            if (listCommunity != null)
                listCommunity.clear();
            communityAdapter.notifyDataSetChanged();
            getCommunityList();
        }
    };

    //获取社区列表
    private void getCommunityList() {
        if (NetWorkUtils.isNetAvailable(this)) {
            llNoNetContent.setVisibility(View.GONE);

            final Map<String, String> param = new HashMap<>();
            param.put("type", "2"); //1-全部社区, 2-我的社区, 3-发布广告的社区搜索, 4-加入推广社区搜索，5-我的推广社区
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
                    if (swipeRefresh != null)
                        swipeRefresh.setRefreshing(false);
                }

                @Override
                public void onFailure(Call<CommunityListEntity> call, Throwable t) {
                    if (swipeRefresh != null)
                        swipeRefresh.setRefreshing(false);
                }
            });
        } else {
            if (swipeRefresh != null)
                swipeRefresh.setRefreshing(false);
            communityAdapter.loadMoreEnd();

            llNoNetContent.setVisibility(View.VISIBLE);
        }
    }

    private void showData(List<CommunityItemEntity> listData) {
        try {
            if (listData.size() == 0 && page == 1) {
                canLoadMore = false;
                communityAdapter.loadMoreEnd();

                llNullTips.setVisibility(View.VISIBLE);
                rvCommunity.setVisibility(View.GONE);

            } else {
                llNullTips.setVisibility(View.GONE);
                rvCommunity.setVisibility(View.VISIBLE);

                if (listData.size() < perpage) {
                    canLoadMore = false;
                    communityAdapter.loadMoreEnd();
                } else {
                    page = page + 1;
                    canLoadMore = true;
                    communityAdapter.loadMoreComplete();
                }
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

    @OnClick({R.id.tv_add_community, R.id.tv_create_community, R.id.tv_try_again})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_add_community:
                goAddCommunity();
                break;
            case R.id.tv_create_community:
                goCreateCommunity();
                break;
            case R.id.tv_try_again:
                swipeRefresh.setRefreshing(true);
                page = 1;
                if (listCommunity != null)
                    listCommunity.clear();
                communityAdapter.notifyDataSetChanged();
                getCommunityList();
                break;
        }
    }

    //跳转添加社区
    private void goAddCommunity() {
        UIHelper.jump(this, AddCommunityActivity.class);
    }

    //创建社区
    private void goCreateCommunity() {
        UIHelper.jump(this, CreateCommunityActivity.class);
    }

    private class MyCommunityAdapter extends BaseQuickAdapter<CommunityItemEntity, BaseViewHolder>
            implements SwipeItemMangerInterface, SwipeAdapterInterface, View.OnClickListener {

        public SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);

        public MyCommunityAdapter(@LayoutRes int layoutResId, @Nullable List<CommunityItemEntity> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, CommunityItemEntity item) {
            LinearLayout ll_item = helper.getView(R.id.ll_item);
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

            mItemManger.bindView(helper.getConvertView(), helper.getLayoutPosition());

            ItemData itemData = new ItemData();
            itemData.setPosition(helper.getLayoutPosition());
            itemData.setItem(item);

            helper.setOnClickListener(R.id.tv_item_del, this);
            helper.setTag(R.id.tv_item_del, R.id.item_tag, itemData);

            helper.setOnClickListener(R.id.ll_item, this);
            helper.setTag(R.id.ll_item, R.id.item_tag, itemData);
        }

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return R.id.swipe;
        }

        @Override
        public void openItem(int position) {
            mItemManger.openItem(position);
        }

        @Override
        public void closeItem(int position) {
            mItemManger.closeItem(position);
        }

        @Override
        public void closeAllExcept(SwipeLayout layout) {
            mItemManger.closeAllExcept(layout);
        }

        @Override
        public void closeAllItems() {
            mItemManger.closeAllItems();
        }

        @Override
        public List<Integer> getOpenItems() {
            return mItemManger.getOpenItems();
        }

        @Override
        public List<SwipeLayout> getOpenLayouts() {
            return mItemManger.getOpenLayouts();
        }

        @Override
        public void removeShownLayouts(SwipeLayout layout) {
            mItemManger.removeShownLayouts(layout);
        }

        @Override
        public boolean isOpen(int position) {
            return mItemManger.isOpen(position);
        }

        @Override
        public Attributes.Mode getMode() {
            return mItemManger.getMode();
        }

        @Override
        public void setMode(Attributes.Mode mode) {
            mItemManger.setMode(mode);
        }

        @Override
        public void onClick(View view) {
            ItemData itemData = (ItemData) view.getTag(R.id.item_tag);
            switch (view.getId()) {
                case R.id.tv_item_del:
                    Logger.e("----->退出Item");
                    if (NetWorkUtils.isNetAvailable(MyCommunityActivity.this)) {
                        //退出社区
                        if (itemData != null) {
                            showQuitDialog(itemData);
                        }
                    } else {
                        MainApp.getInstance().showToast(getString(R.string.network_not_available));
                    }
                    break;
                case R.id.ll_item:
                    Logger.e("----->点击Item");
                    if (isOpen(itemData.getPosition())) {
                        closeItem(itemData.getPosition());
                        return;
                    }
                    goCommunityMsgList(itemData.getItem().getId());
                    break;
            }
        }

        @Data
        public class ItemData implements Serializable {

            private int position;
            private CommunityItemEntity item;

        }
    }

    //跳转该社区消息列表页面
    private void goCommunityMsgList(String community_id) {
        Bundle bundle = new Bundle();
        bundle.putString("community_id", community_id);
        UIHelper.jump(this, CommunityMsgListActivity.class, bundle);
    }

    //退出社区弹框提醒
    private void showQuitDialog(final MyCommunityAdapter.ItemData itemData) {
        final CommonDialog dialog = new CommonDialog(this);
        dialog.getTitle().setVisibility(View.GONE);
        dialog.getContent().setText(getString(R.string.if_confirm_quit_community));
        dialog.getBtnOk().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                quitCommunity(itemData);
            }
        });
    }

    //退出社区
    private void quitCommunity(final MyCommunityAdapter.ItemData itemData) {
        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("type", "1"); //类型: 0-加入，1-退出
        param.put("community_id", itemData.getItem().getId());
        Call<JoinCommunityEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .joinCommunity(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<JoinCommunityEntity>() {
            @Override
            public void onSuccess(Call<JoinCommunityEntity> call, Response<JoinCommunityEntity> response) {
                JoinCommunityEntity joinCommunityEntity = response.body();
                if (joinCommunityEntity != null) {
                    int code = joinCommunityEntity.getCode();
                    String msg = joinCommunityEntity.getMsg();
                    if (code == Constants.CODE_SUCCESS) {
                        try {
                            listCommunity.remove(itemData.getItem());
                            communityAdapter.notifyDataSetChanged();
                            communityAdapter.closeAllItems();

                            if (listCommunity.size() == 0) {
                                llNullTips.setVisibility(View.VISIBLE);
                                rvCommunity.setVisibility(View.GONE);
                            } else {
                                llNullTips.setVisibility(View.GONE);
                                rvCommunity.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        MainApp.getInstance().showToast(msg);
                    }
                }
            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<JoinCommunityEntity> call, Throwable t) {

            }
        });
    }

    //解决滑动冲突
    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        private int lastVisibleItemPosition;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            //第一个可视View 的位置
            int FirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            rvCommunity.setEnabled(FirstVisibleItemPosition == 0);
            //最后一个可视View 的位置
            lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }
    };

    private BaseQuickAdapter.RequestLoadMoreListener onLoadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            if (canLoadMore) {
                Logger.e("----->onLoadMoreRequested ");
                Logger.e("--->page = " + page);
                getCommunityList();
            } else {
                communityAdapter.loadMoreEnd();
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MsgEvent event) {
        if (event != null) {
            String msg = event.getMsg();
            switch (msg) {
                case MsgEvent.EVENT_QUIT_COMMUNITY_SUCCESS:
                    page = 1;
                    if (listCommunity != null)
                        listCommunity.clear();
                    communityAdapter.notifyDataSetChanged();
                    getCommunityList();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
