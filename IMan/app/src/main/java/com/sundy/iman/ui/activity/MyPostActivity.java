package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.sundy.iman.entity.CancelPostEntity;
import com.sundy.iman.entity.DeletePostEntity;
import com.sundy.iman.entity.PostItemEntity;
import com.sundy.iman.entity.PostListEntity;
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
import com.sundy.iman.view.dialog.CommonDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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

public class MyPostActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.rv_post)
    RecyclerView rvPost;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.tv_create_ad)
    TextView tvCreateAd;
    @BindView(R.id.ll_null_tips)
    LinearLayout llNullTips;

    private int page = 1; //当前页码
    private int perpage = 10; //每页显示条数
    private boolean canLoadMore = true;
    private MyPostAdapter myPostAdapter;
    private List<PostItemEntity> listPost = new ArrayList<>();
    private WrapContentLinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_post);
        ButterKnife.bind(this);

        initTitle();
        init();
        if (listPost != null)
            listPost.clear();
        getPostList();
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
                getPostList();
            }
        });

        linearLayoutManager = new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvPost.setLayoutManager(linearLayoutManager);
        rvPost.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        myPostAdapter = new MyPostAdapter(R.layout.item_my_post, listPost);
        myPostAdapter.setLoadMoreView(new CustomLoadMoreView());
        myPostAdapter.setEnableLoadMore(true);
        myPostAdapter.setOnLoadMoreListener(onLoadMoreListener, rvPost);
        rvPost.setAdapter(myPostAdapter);
        rvPost.addOnScrollListener(onScrollListener);
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

    //获取post列表
    private void getPostList() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("type", "2"); //类型 1-某个社区的 post,2-我的 post
        param.put("community_id", ""); //社区ID​(​类型为1时必填​)
        param.put("page", page + "");
        param.put("perpage", perpage + "");
        Call<PostListEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .getPostList(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<PostListEntity>() {
            @Override
            public void onSuccess(Call<PostListEntity> call, Response<PostListEntity> response) {
                PostListEntity postListEntity = response.body();
                if (postListEntity != null) {
                    int code = postListEntity.getCode();
                    String msg = postListEntity.getMsg();
                    if (code == Constants.CODE_SUCCESS) {
                        PostListEntity.DataEntity dataEntity = postListEntity.getData();
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
            public void onFailure(Call<PostListEntity> call, Throwable t) {
                if (swipeRefresh != null)
                    swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void showData(List<PostItemEntity> listData) {
        try {
            if (listData.size() == 0 && page == 1) {
                canLoadMore = false;
                myPostAdapter.loadMoreEnd();

                llNullTips.setVisibility(View.VISIBLE);
                rvPost.setVisibility(View.GONE);

            } else {
                llNullTips.setVisibility(View.GONE);
                rvPost.setVisibility(View.VISIBLE);

                if (listData.size() == 0) {
                    canLoadMore = false;
                    myPostAdapter.loadMoreEnd();
                } else {
                    page = page + 1;
                    canLoadMore = true;
                    myPostAdapter.loadMoreComplete();
                    for (int i = 0; i < listData.size(); i++) {
                        PostItemEntity item = listData.get(i);
                        if (item != null) {
                            listPost.add(item);
                        }
                    }
                    myPostAdapter.setNewData(listPost);
                    myPostAdapter.notifyDataSetChanged();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.tv_create_ad)
    public void onViewClicked() {
        goCreateAd();
    }

    //创建广告
    private void goCreateAd() {
        UIHelper.jump(this, CreateAdvertisementActivity.class);
    }

    private class MyPostAdapter extends BaseQuickAdapter<PostItemEntity, BaseViewHolder>
            implements SwipeItemMangerInterface, SwipeAdapterInterface, View.OnClickListener {

        public SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);

        public MyPostAdapter(@LayoutRes int layoutResId, @Nullable List<PostItemEntity> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, PostItemEntity item) {
            TextView tv_post_name = helper.getView(R.id.tv_post_name);
            TextView tv_status = helper.getView(R.id.tv_status);
            TextView tv_post_time = helper.getView(R.id.tv_post_time);
            TextView tv_end_time = helper.getView(R.id.tv_end_time);
            ImageView iv_ad = helper.getView(R.id.iv_ad);
            TextView tv_item_cancel = helper.getView(R.id.tv_item_cancel);

            String title = item.getTitle();
            String create_time = item.getCreate_time();
            String effective_time = item.getEffective_time();

            tv_post_name.setText(title);

            if (create_time != null) {
                Date date = DateUtils.formatTimeStamp2Date(Long.parseLong(create_time) * 1000);
                tv_post_time.setText(getString(R.string.posted_title) + " " + DateUtils.formatDate2String(date, "yyyy/MM/dd"));
            } else {
                tv_post_time.setText("");
            }

            if (effective_time != null) {
                Date date = DateUtils.formatTimeStamp2Date(Long.parseLong(effective_time) * 1000);
                tv_end_time.setText(getString(R.string.expired_title) + " " + DateUtils.formatDate2String(date, "yyyy/MM/dd"));
            } else {
                tv_end_time.setText("");
            }

            String type = item.getType(); //类型: 1-普通post，2-广告
            String status = item.getStatus(); //post状态 1-有效,2-过期, 3-取消
            if (type.equals("1")) {
                iv_ad.setVisibility(View.INVISIBLE);
                if (status.equals("1")) {
                    tv_status.setText(getString(R.string.active));
                    tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.txt_blue));
                    tv_item_cancel.setVisibility(View.VISIBLE);
                } else if (status.equals("2")) {
                    tv_status.setText(getString(R.string.expired));
                    tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.txt_light_gray));
                    tv_item_cancel.setVisibility(View.GONE);
                } else if (status.equals("3")) {
                    tv_status.setText(getString(R.string.cancelled));
                    tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.txt_orange));
                    tv_item_cancel.setVisibility(View.GONE);
                }
            } else {
                iv_ad.setVisibility(View.VISIBLE);
                tv_item_cancel.setVisibility(View.GONE);
                if (status.equals("1")) {
                    tv_status.setText(getString(R.string.active));
                    tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.txt_blue));
                } else if (status.equals("2")) {
                    tv_status.setText(getString(R.string.expired));
                    tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.txt_light_gray));
                } else if (status.equals("3")) {
                    tv_status.setText(getString(R.string.cancelled));
                    tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.txt_orange));
                }
            }

            mItemManger.bindView(helper.getConvertView(), helper.getLayoutPosition());

            ItemData itemData = new ItemData();
            itemData.setPosition(helper.getLayoutPosition());
            itemData.setItem(item);

            helper.setOnClickListener(R.id.tv_item_del, this);
            helper.setTag(R.id.tv_item_del, R.id.item_tag, itemData);

            helper.setOnClickListener(R.id.tv_item_cancel, this);
            helper.setTag(R.id.tv_item_cancel, R.id.item_tag, itemData);

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
                    Logger.e("----->删除Item : " + itemData.getPosition());
                    //删除消息
                    if (itemData != null) {
                        showDeleteDialog(itemData);
                    }
                    break;
                case R.id.tv_item_cancel:
                    Logger.e("----->取消Item : " + itemData.getPosition());
                    //取消发布
                    if (itemData != null) {
                        showCancelPostDialog(itemData);
                    }
                    break;
                case R.id.ll_item:
                    Logger.e("----->点击Item");
                    if (isOpen(itemData.getPosition())) {
                        closeItem(itemData.getPosition());
                        return;
                    }
                    goMsgDetail(itemData);
                    break;
            }
        }

        @Data
        public class ItemData implements Serializable {

            private int position;
            private PostItemEntity item;

        }
    }

    //跳转消息详情
    private void goMsgDetail(MyPostAdapter.ItemData itemData) {
        PostItemEntity postItemEntity = itemData.getItem();
        if (postItemEntity != null) {
            String status = postItemEntity.getStatus();
            String type = postItemEntity.getType(); //类型: 1-普通post，2-广告
            if (type.equals("1")) {
                goEditPost(itemData);
            } else {
                goEditAd(itemData);
            }
        }
    }

    //跳转编辑 广告 页面
    private void goEditAd(MyPostAdapter.ItemData itemData) {
        if (itemData == null)
            return;
        Bundle bundle = new Bundle();
        bundle.putString("post_id", itemData.getItem().getId());
        bundle.putString("creator_id", itemData.getItem().getCreator_id());
        UIHelper.jump(this, EditAdvertisementActivity.class, bundle);
    }

    //跳转编辑 Post 页面
    private void goEditPost(MyPostAdapter.ItemData itemData) {
        if (itemData == null)
            return;
        Bundle bundle = new Bundle();
        bundle.putString("post_id", itemData.getItem().getId());
        bundle.putString("creator_id", itemData.getItem().getCreator_id());
        UIHelper.jump(this, EditPostActivity.class, bundle);
    }

    //显示删除提醒弹框
    private void showDeleteDialog(final MyPostAdapter.ItemData itemData) {
        final CommonDialog dialog = new CommonDialog(this);
        dialog.getTitle().setVisibility(View.GONE);
        dialog.getContent().setText(getString(R.string.if_confirm_delete_post));
        dialog.getBtnOk().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                deletePost(itemData);
            }
        });
    }

    //删除Post
    private void deletePost(final MyPostAdapter.ItemData itemData) {
        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("post_id", itemData.getItem().getId());
        Call<DeletePostEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .deletePost(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<DeletePostEntity>() {
            @Override
            public void onSuccess(Call<DeletePostEntity> call, Response<DeletePostEntity> response) {
                DeletePostEntity deletePostEntity = response.body();
                if (deletePostEntity != null) {
                    int code = deletePostEntity.getCode();
                    String msg = deletePostEntity.getMsg();
                    if (code == Constants.CODE_SUCCESS) {
                        try {
                            listPost.remove(itemData.getItem());
                            myPostAdapter.notifyDataSetChanged();
                            myPostAdapter.closeAllItems();

                            if (listPost.size() == 0) {
                                llNullTips.setVisibility(View.VISIBLE);
                                rvPost.setVisibility(View.GONE);
                            } else {
                                llNullTips.setVisibility(View.GONE);
                                rvPost.setVisibility(View.VISIBLE);
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
            public void onFailure(Call<DeletePostEntity> call, Throwable t) {

            }
        });
    }

    //显示取消发布弹框
    private void showCancelPostDialog(final MyPostAdapter.ItemData itemData) {
        final CommonDialog dialog = new CommonDialog(this);
        dialog.getTitle().setVisibility(View.GONE);
        dialog.getContent().setText(getString(R.string.if_confirm_cancel_post));
        dialog.getBtnCancel().setVisibility(View.VISIBLE);
        dialog.getBtnOk().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                cancelPost(itemData);
            }
        });
    }

    //取消Post
    private void cancelPost(final MyPostAdapter.ItemData itemData) {
        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("post_id", itemData.getItem().getId());
        showProgress();
        Call<CancelPostEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .cancelPost(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<CancelPostEntity>() {
            @Override
            public void onSuccess(Call<CancelPostEntity> call, Response<CancelPostEntity> response) {
                CancelPostEntity cancelPostEntity = response.body();
                if (cancelPostEntity != null) {
                    int code = cancelPostEntity.getCode();
                    String msg = cancelPostEntity.getMsg();
                    if (code == Constants.CODE_SUCCESS) {
                        try {
                            PostItemEntity itemEntity = itemData.getItem();
                            itemEntity.setStatus("3");
                            listPost.set(itemData.getPosition(), itemEntity);
                            myPostAdapter.notifyItemChanged(itemData.getPosition());
                            myPostAdapter.closeAllItems();
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
                hideProgress();
            }

            @Override
            public void onFailure(Call<CancelPostEntity> call, Throwable t) {

            }
        });
    }

    private BaseQuickAdapter.RequestLoadMoreListener onLoadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            Logger.e("----->onLoadMoreRequested ");
            Logger.e("--->page = " + page);
            if (canLoadMore) {
                getPostList();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
