package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;
import com.sundy.iman.MainApp;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.CommunityInfoEntity;
import com.sundy.iman.entity.JoinCommunityEntity;
import com.sundy.iman.entity.MsgEvent;
import com.sundy.iman.entity.PostItemEntity;
import com.sundy.iman.entity.PostListEntity;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.view.CustomLoadMoreView;
import com.sundy.iman.view.DividerItemDecoration;
import com.sundy.iman.view.TitleBarView;
import com.sundy.iman.view.WrapContentLinearLayoutManager;
import com.sundy.iman.view.dialog.CommonDialog;
import com.sundy.iman.view.popupwindow.CommunityMenuPopup;

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
 * 社区消息列表页面
 * Created by sundy on 17/10/23.
 */

public class CommunityMsgListActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.rv_msg)
    RecyclerView rvMsg;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;

    private String community_id;
    private CommunityMenuPopup communityMenuPopup;

    private int page = 1; //当前页码
    private int perpage = 10; //每页显示条数
    private boolean canLoadMore = true;
    private PostAdapter myPostAdapter;
    private List<PostItemEntity> listPost = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_community_msg_list);
        ButterKnife.bind(this);

        initData();
        initTitle();
        init();
        getCommunityInfo();

        if (listPost != null)
            listPost.clear();
        getPostList();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            community_id = bundle.getString("community_id");
        }
    }

    private void initTitle() {
        titleBar.setRightIvBg(R.mipmap.icon_dot_more_black);
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
                showMenuPopup();
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
        communityMenuPopup = new CommunityMenuPopup(this);
        communityMenuPopup.setOnClickListener(clickListener);

        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                page = 1;
                if (listPost != null)
                    listPost.clear();
                getPostList();
            }
        });

        rvMsg.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvMsg.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        myPostAdapter = new PostAdapter(R.layout.item_my_post, listPost);
        myPostAdapter.openLoadAnimation();
        myPostAdapter.isFirstOnly(false);
        myPostAdapter.setLoadMoreView(new CustomLoadMoreView());
        myPostAdapter.setEnableLoadMore(true);
        myPostAdapter.setOnLoadMoreListener(onLoadMoreListener, rvMsg);
        rvMsg.setAdapter(myPostAdapter);
    }

    //社区详情
    private void getCommunityInfo() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("community_id", community_id); //社区ID
        param.put("type", "1"); //类型: 1-普通社区，2-推广社区
        Call<CommunityInfoEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .getCommunityInfo(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<CommunityInfoEntity>() {
            @Override
            public void onSuccess(Call<CommunityInfoEntity> call, Response<CommunityInfoEntity> response) {
                CommunityInfoEntity communityInfoEntity = response.body();
                if (communityInfoEntity != null) {
                    int code = communityInfoEntity.getCode();
                    String msg = communityInfoEntity.getMsg();
                    if (code == Constants.CODE_SUCCESS) {
                        CommunityInfoEntity.DataEntity dataEntity = communityInfoEntity.getData();
                        if (dataEntity != null) {
                            titleBar.setBackMode(dataEntity.getName());
                            titleBar.setRightIvVisibility(View.VISIBLE);
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
            public void onFailure(Call<CommunityInfoEntity> call, Throwable t) {

            }
        });
    }

    //获取post列表
    private void getPostList() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("type", "1"); //类型 1-某个社区的 post,2-我的 post
        param.put("community_id", community_id); //社区ID​(​类型为1时必填​)
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
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<PostListEntity> call, Throwable t) {

            }
        });
    }

    private void showData(List<PostItemEntity> listData) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //显示菜单Popup
    private void showMenuPopup() {
        communityMenuPopup.showAtLocation(llContent, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private CommunityMenuPopup.OnClickListener clickListener = new CommunityMenuPopup.OnClickListener() {
        @Override
        public void quitClick() {
            communityMenuPopup.dismiss();
            showQuitDialog(community_id);
        }

        @Override
        public void postClick() {
            communityMenuPopup.dismiss();
            goAddPost();
        }

        @Override
        public void infoClick() {
            communityMenuPopup.dismiss();
            goCommunityDetail(community_id);
        }

        @Override
        public void shareClick() {
            communityMenuPopup.dismiss();
        }
    };

    //跳转发布Post
    private void goAddPost() {
        Bundle bundle = new Bundle();
        bundle.putString("community_id", community_id);
        UIHelper.jump(this, CreatePostActivity.class, bundle);
    }

    //退出社区弹框提醒
    private void showQuitDialog(final String community_id) {
        final CommonDialog dialog = new CommonDialog(this);
        dialog.getTitle().setVisibility(View.GONE);
        dialog.getContent().setText(getString(R.string.if_confirm_quit_community));
        dialog.getBtnOk().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                quitCommunity(community_id);
            }
        });
    }

    //退出社区
    private void quitCommunity(String community_id) {
        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("type", "1"); //类型: 0-加入，1-退出
        param.put("community_id", community_id);
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
                        sendQuitEvent();
                        finish();
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

    //发送退出社区成功Event, 刷新我的社区列表
    private void sendQuitEvent() {
        MsgEvent msgEvent = new MsgEvent();
        msgEvent.setMsg(MsgEvent.EVENT_QUIT_COMMUNITY_SUCCESS);
        EventBus.getDefault().post(msgEvent);
    }

    //跳转社区详情
    private void goCommunityDetail(String community_id) {
        Bundle bundle = new Bundle();
        bundle.putString("community_id", community_id);
        bundle.putString("type", "1"); //类型 1-普通社区,2- 推广社区
        UIHelper.jump(this, CommunityDetailActivity.class, bundle);
    }

    private class PostAdapter extends BaseQuickAdapter<PostItemEntity, BaseViewHolder> {

        public PostAdapter(@LayoutRes int layoutResId, @Nullable List<PostItemEntity> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, PostItemEntity item) {

        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (communityMenuPopup != null) {
            communityMenuPopup.dismiss();
            communityMenuPopup = null;
        }
    }
}
