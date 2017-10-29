package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;
import com.sundy.iman.MainApp;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.CollectAdvertisingEntity;
import com.sundy.iman.entity.CommunityInfoEntity;
import com.sundy.iman.entity.JoinCommunityEntity;
import com.sundy.iman.entity.MsgEvent;
import com.sundy.iman.entity.PostItemEntity;
import com.sundy.iman.entity.PostListEntity;
import com.sundy.iman.helper.ImageHelper;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.utils.DateUtils;
import com.sundy.iman.view.CustomLoadMoreView;
import com.sundy.iman.view.TitleBarView;
import com.sundy.iman.view.WrapContentLinearLayoutManager;
import com.sundy.iman.view.dialog.CommonDialog;
import com.sundy.iman.view.popupwindow.CommunityMenuPopup;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import lombok.Data;
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
    @BindView(R.id.view_line)
    ImageView viewLine;
    @BindView(R.id.tv_post)
    TextView tvPost;
    @BindView(R.id.tv_tips)
    TextView tvTips;

    private String community_id;
    private CommunityMenuPopup communityMenuPopup;

    private int page = 1; //当前页码
    private int perpage = 10; //每页显示条数
    private boolean canLoadMore = true;
    private PostAdapter myPostAdapter;
    private List<PostItemEntity> listPost = new ArrayList<>();
    private WrapContentLinearLayoutManager linearLayoutManager;

    private List<String> listExpands = new ArrayList<>();

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

        linearLayoutManager = new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvMsg.setLayoutManager(linearLayoutManager);

        myPostAdapter = new PostAdapter(R.layout.item_post, listPost);
        myPostAdapter.openLoadAnimation();
        myPostAdapter.isFirstOnly(false);
        myPostAdapter.setLoadMoreView(new CustomLoadMoreView());
        myPostAdapter.setEnableLoadMore(true);
        myPostAdapter.setOnLoadMoreListener(onLoadMoreListener, rvMsg);
        rvMsg.setAdapter(myPostAdapter);

        rvMsg.addOnScrollListener(onScrollListener);
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
            if (listData.size() == 0 && page == 1) {
                canLoadMore = false;
                myPostAdapter.loadMoreEnd();

                tvTips.setVisibility(View.VISIBLE);
                tvPost.setVisibility(View.VISIBLE);
                viewLine.setVisibility(View.GONE);
                rvMsg.setVisibility(View.GONE);

            } else {
                tvTips.setVisibility(View.GONE);
                tvPost.setVisibility(View.GONE);
                viewLine.setVisibility(View.VISIBLE);
                rvMsg.setVisibility(View.VISIBLE);

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

    @OnClick(R.id.tv_post)
    public void onViewClicked() {
        goAddPost();
    }

    private class PostAdapter extends BaseQuickAdapter<PostItemEntity, BaseViewHolder>
            implements View.OnClickListener {

        public PostAdapter(@LayoutRes int layoutResId, @Nullable List<PostItemEntity> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, PostItemEntity item) {
            try {
                RelativeLayout rel_item = helper.getView(R.id.rel_item);
                ImageView view_top = helper.getView(R.id.view_top);
                CircleImageView iv_dot = helper.getView(R.id.iv_dot);
                TextView tv_time = helper.getView(R.id.tv_time);
                ImageView iv_arrow = helper.getView(R.id.iv_arrow);
                ImageView iv_tag_coin = helper.getView(R.id.iv_tag_coin);
                TextView tv_title = helper.getView(R.id.tv_title);
                LinearLayout ll_detail = helper.getView(R.id.ll_detail);
                RelativeLayout rel_media = helper.getView(R.id.rel_media);
                TextView tv_content = helper.getView(R.id.tv_content);
                final TagFlowLayout flTab = helper.getView(R.id.fl_tab);
                CircleImageView iv_header = helper.getView(R.id.iv_header);
                TextView tv_creator_name = helper.getView(R.id.tv_creator_name);
                ImageView iv_collect = helper.getView(R.id.iv_collect);
                ImageView iv_chat = helper.getView(R.id.iv_chat);
                ImageView iv_more = helper.getView(R.id.iv_more);

                String post_id = item.getId();
                String title = item.getTitle();
                String type = item.getType(); //类型: 1-普通post，2-广告
                String create_time = item.getCreate_time();
                String content = item.getDetail();
                String tagStr = item.getTags();
                PostItemEntity.MemberEntity memberEntity = item.getMembers();
                String username = memberEntity.getUsername();
                String profileImg = memberEntity.getProfile_image();
                List<PostItemEntity.AttachmentEntity> attachment = item.getAttachment();
                String is_collect = item.getIs_collect(); //是否领取过奖励: 1-是，0-否

                ItemData itemData = new ItemData();
                itemData.setItem(item);
                itemData.setPosition(helper.getLayoutPosition());

                tv_title.setText(title);
                if (type.equals("1")) { //普通post
                    iv_tag_coin.setVisibility(View.GONE);
                    iv_collect.setVisibility(View.GONE);
                } else { //广告
                    iv_tag_coin.setVisibility(View.VISIBLE);

                    if (is_collect.equals("1")) {
                        iv_collect.setVisibility(View.GONE);
                    } else {
                        iv_collect.setVisibility(View.VISIBLE);
                    }
                }

                if (!TextUtils.isEmpty(create_time)) {
                    Date date = DateUtils.formatTimeStamp2Date(Long.parseLong(create_time) * 1000);
                    tv_time.setText(DateUtils.formatDate2String(date, "MM/dd HH:mm"));
                } else {
                    tv_time.setText("");
                }

                if (TextUtils.isEmpty(content)) {
                    tv_content.setVisibility(View.GONE);
                } else {
                    tv_content.setVisibility(View.VISIBLE);
                    tv_content.setText(content);
                }

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

                if (TextUtils.isEmpty(username)) {
                    tv_creator_name.setText(getString(R.string.iman));
                } else {
                    tv_creator_name.setText(username);
                }
                ImageHelper.displayPortrait(CommunityMsgListActivity.this, profileImg, iv_header);


                if (attachment != null && attachment.size() > 0) {

                } else {

                }

                boolean isRead = PaperUtils.isPostRead(community_id, post_id);
                if (isRead) {
                    view_top.setBackgroundColor(ContextCompat.getColor(CommunityMsgListActivity.this, R.color.bg_gray));
                } else {
                    view_top.setBackgroundColor(ContextCompat.getColor(CommunityMsgListActivity.this, R.color.bg_blue));
                }

                if (listExpands.contains(post_id)) {
                    ll_detail.setVisibility(View.VISIBLE);
                    iv_arrow.setImageResource(R.mipmap.icon_graytriangle_up);
                } else {
                    ll_detail.setVisibility(View.GONE);
                    iv_arrow.setImageResource(R.mipmap.icon_graytriangle);
                }

                helper.setOnClickListener(R.id.rel_item, this);
                helper.setTag(R.id.rel_item, R.id.item_tag, itemData);

                helper.setOnClickListener(R.id.iv_collect, this);
                helper.setTag(R.id.iv_collect, R.id.item_tag, itemData);

                helper.setOnClickListener(R.id.iv_header, this);
                helper.setTag(R.id.iv_header, R.id.item_tag, itemData);
                helper.setOnClickListener(R.id.tv_creator_name, this);
                helper.setTag(R.id.tv_creator_name, R.id.item_tag, itemData);
                helper.setOnClickListener(R.id.iv_chat, this);
                helper.setTag(R.id.iv_chat, R.id.item_tag, itemData);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View view) {
            ItemData itemData = (ItemData) view.getTag(R.id.item_tag);
            switch (view.getId()) {
                case R.id.rel_item:
                    Logger.e("----->展开Item");
                    if (itemData != null) {
                        String post_id = itemData.getItem().getId();
                        PaperUtils.savePostReadID(community_id, post_id);
                        if (listExpands.contains(post_id)) {
                            listExpands.remove(post_id);
                        } else {
                            listExpands.add(post_id);
                        }

                        myPostAdapter.notifyItemChanged(itemData.getPosition());
                    }
                    break;
                case R.id.iv_collect:
                    Logger.e("----->领取广告奖励");
                    if (itemData != null) {
                        collectAdvertising(itemData);
                    }
                    break;
                case R.id.iv_header:
                case R.id.tv_creator_name:
                case R.id.iv_chat:
                    Logger.e("----->跳转聊天");
                    if (itemData != null) {
                        PostItemEntity postItemEntity = itemData.getItem();
                        if (postItemEntity != null) {
                            PostItemEntity.MemberEntity memberEntity = postItemEntity.getMembers();
                            if (memberEntity != null) {
                                goChat(memberEntity);
                            }
                        }
                    }
                    break;

            }
        }

        @Data
        public class ItemData implements Serializable {

            private int position;
            private PostItemEntity item;

        }
    }

    //领取广告奖励
    private void collectAdvertising(final PostAdapter.ItemData itemData) {
        final PostItemEntity postItemEntity = itemData.getItem();
        if (postItemEntity != null) {
            Map<String, String> param = new HashMap<>();
            param.put("mid", PaperUtils.getMId());
            param.put("session_key", PaperUtils.getSessionKey());
            param.put("post_id", postItemEntity.getId()); //post id
            param.put("creator_id", postItemEntity.getCreator_id()); //post的作者ID
            param.put("community_id", community_id); //社区ID
            param.put("income", postItemEntity.getPar_amount()); //奖励金额
            Call<CollectAdvertisingEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                    .collectAdvertising(ParamHelper.formatData(param));
            call.enqueue(new RetrofitCallback<CollectAdvertisingEntity>() {
                @Override
                public void onSuccess(Call<CollectAdvertisingEntity> call, Response<CollectAdvertisingEntity> response) {
                    CollectAdvertisingEntity collectAdvertisingEntity = response.body();
                    if (collectAdvertisingEntity != null) {
                        int code = collectAdvertisingEntity.getCode();
                        String msg = collectAdvertisingEntity.getMsg();
                        if (code == Constants.CODE_SUCCESS) {
                            MainApp.getInstance().showToast(getString(R.string.you_have_collect_imcoin));

                            postItemEntity.setIs_collect("1");
                            listPost.set(itemData.getPosition(), postItemEntity);
                            myPostAdapter.notifyItemChanged(itemData.getPosition());

                        } else {
                            MainApp.getInstance().showToast(msg);
                        }
                    }
                }

                @Override
                public void onAfter() {

                }

                @Override
                public void onFailure(Call<CollectAdvertisingEntity> call, Throwable t) {

                }
            });
        }
    }

    //跳转聊天页面
    private void goChat(PostItemEntity.MemberEntity memberEntity) {
        Bundle bundle = new Bundle();
        bundle.putString("userId", memberEntity.getId());
        bundle.putString("easemod_id", memberEntity.getEasemob_account());
        UIHelper.jump(this, ChatActivity.class, bundle);
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

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            Logger.e("----->onLoadMoreRequested ");
            switch (newState) {
                case 0:
                    Logger.e("----->已经停止滚动 ");
                    break;
                case 1:
                    Logger.e("----->正在被拖拽 ");
                    break;
                case 2:
                    Logger.e("----->正在依靠惯性滚动 ");
                    break;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();//可见范围内的第一项的位置
            int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();//可见范围内的最后一项的位置
            int itemCount = linearLayoutManager.getItemCount();//recyclerview中的item的所有的数目

            Logger.e("----->firstVisibleItemPosition " + firstVisibleItemPosition);
            Logger.e("----->lastVisibleItemPosition " + lastVisibleItemPosition);
            Logger.e("----->itemCount " + itemCount);
            Logger.e("----->dy " + dy);
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
