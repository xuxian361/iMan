package com.sundy.iman.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.transition.Explode;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;
import com.previewlibrary.GPreviewBuilder;
import com.previewlibrary.enitity.ThumbViewInfo;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.sundy.iman.MainApp;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.CollectAdvertisingEntity;
import com.sundy.iman.entity.CommunityInfoEntity;
import com.sundy.iman.entity.DeletePostEntity;
import com.sundy.iman.entity.JoinCommunityEntity;
import com.sundy.iman.entity.MsgEvent;
import com.sundy.iman.entity.PostItemEntity;
import com.sundy.iman.entity.PostListEntity;
import com.sundy.iman.entity.ShareInfoEntity;
import com.sundy.iman.helper.ImageHelper;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.impl.VideoSimpleListener;
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
import com.sundy.iman.view.popupwindow.PostItemMenuPopup;
import com.sundy.iman.view.popupwindow.PostItemMenuSelfPopup;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
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
    RelativeLayout llContent;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.view_line)
    ImageView viewLine;
    @BindView(R.id.tv_post)
    TextView tvPost;
    @BindView(R.id.tv_tips)
    TextView tvTips;
    @BindView(R.id.video_full_container)
    FrameLayout videoFullContainer;

    private String community_id;
    private CommunityMenuPopup communityMenuPopup;

    private int page = 1; //当前页码
    private int perpage = 10; //每页显示条数
    private boolean canLoadMore = true;
    private PostAdapter myPostAdapter;
    private List<PostItemEntity> listPost = new ArrayList<>();
    private WrapContentLinearLayoutManager linearLayoutManager;

    private List<String> listExpands = new ArrayList<>();
    private PostItemMenuPopup postItemMenuPopup;
    private PostItemMenuSelfPopup postItemMenuSelfPopup;

    private boolean mFull = false;
    private UMWeb umWeb; //分享出去的带网页的友盟控件

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }
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
                myPostAdapter.notifyDataSetChanged();
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

        public final static String TAG = "PostAdapter";
        public GSYVideoOptionBuilder gsyVideoOptionBuilder;

        public PostAdapter(@LayoutRes int layoutResId, @Nullable List<PostItemEntity> data) {
            super(layoutResId, data);
            gsyVideoOptionBuilder = new GSYVideoOptionBuilder();
        }

        @Override
        protected void convert(final BaseViewHolder helper, final PostItemEntity item) {
            try {
                RelativeLayout rel_item = helper.getView(R.id.rel_item);
                ImageView view_top = helper.getView(R.id.view_top);
                CircleImageView iv_dot = helper.getView(R.id.iv_dot);
                TextView tv_time = helper.getView(R.id.tv_time);
                final ImageView iv_arrow = helper.getView(R.id.iv_arrow);
                ImageView iv_tag_coin = helper.getView(R.id.iv_tag_coin);
                TextView tv_title = helper.getView(R.id.tv_title);
                LinearLayout ll_detail = helper.getView(R.id.ll_detail);
                TextView tv_content = helper.getView(R.id.tv_content);
                final TagFlowLayout flTab = helper.getView(R.id.fl_tab);
                CircleImageView iv_header = helper.getView(R.id.iv_header);
                TextView tv_creator_name = helper.getView(R.id.tv_creator_name);
                ImageView iv_collect = helper.getView(R.id.iv_collect);
                ImageView iv_chat = helper.getView(R.id.iv_chat);
                final ImageView iv_more = helper.getView(R.id.iv_more);
                FrameLayout frame_media = helper.getView(R.id.frame_media);
                ImageView iv_img = helper.getView(R.id.iv_img);
                final StandardGSYVideoPlayer gsyVideoPlayer = helper.getView(R.id.video_item_player);
                RecyclerView rv_media = helper.getView(R.id.rv_media);

                View include_single = helper.getView(R.id.include_single);
                View include_multiple = helper.getView(R.id.include_multiple);

                final String post_id = item.getId();
                final String creator_id = item.getCreator_id();
                final String title = item.getTitle();
                String type = item.getType(); //类型: 1-普通post，2-广告
                String create_time = item.getCreate_time();
                String content = item.getDetail();
                String tagStr = item.getTags();
                PostItemEntity.MemberEntity memberEntity = item.getMembers();
                String username = memberEntity.getUsername();
                String profileImg = memberEntity.getProfile_image();
                List<PostItemEntity.AttachmentEntity> attachment = item.getAttachment();
                String is_collect = item.getIs_collect(); //是否领取过奖励: 1-是，0-否

                final ItemData itemData = new ItemData();
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

                //标签
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

                //用户信息
                if (TextUtils.isEmpty(username)) {
                    tv_creator_name.setText(getString(R.string.iman));
                } else {
                    tv_creator_name.setText(username);
                }
                ImageHelper.displayPortrait(CommunityMsgListActivity.this, profileImg, iv_header);

                //附件
                if (attachment != null && attachment.size() > 0) {
                    frame_media.setVisibility(View.VISIBLE);

                    int size = attachment.size();
                    if (size == 1) {
                        include_single.setVisibility(View.VISIBLE);
                        include_multiple.setVisibility(View.GONE);

                        PostItemEntity.AttachmentEntity attachmentEntity = attachment.get(0);
                        final String url = attachmentEntity.getUrl();
//                        final String url = "http://baobab.wdjcdn.com/14564977406580.mp4";

                        final String att_type = attachmentEntity.getAtt_type();//附件类型: 1-图片，2-视频
                        final String thumbnail = attachmentEntity.getThumbnail();
                        Logger.e("------->url = " + url);

                        if (att_type.equals("1")) //图片
                        {
                            gsyVideoPlayer.setVisibility(View.GONE);
                            iv_img.setVisibility(View.VISIBLE);

                            Glide.with(CommunityMsgListActivity.this)
                                    .load(url)
                                    .dontAnimate()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(iv_img);
                            iv_img.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ArrayList<ThumbViewInfo> listImg = new ArrayList<ThumbViewInfo>();
                                    ThumbViewInfo thumbViewInfo = new ThumbViewInfo(url);
                                    listImg.add(thumbViewInfo);
                                    scaleImg(listImg, 0);
                                }
                            });
                        } else {
                            gsyVideoPlayer.setVisibility(View.VISIBLE);
                            iv_img.setVisibility(View.GONE);

                            ImageView imageView = new ImageView(CommunityMsgListActivity.this);
                            //增加封面
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            ImageHelper.displayImageNet(CommunityMsgListActivity.this, thumbnail, imageView);

                            gsyVideoOptionBuilder
                                    .setIsTouchWiget(false)
                                    .setThumbImageView(imageView)
                                    .setUrl(url)
                                    .setVideoTitle(title)
                                    .setCacheWithPlay(true)
                                    .setRotateViewAuto(true)
                                    .setLockLand(true)
                                    .setPlayTag(TAG)
                                    .setShowFullAnimation(true)
                                    .setNeedLockFull(true)
                                    .setPlayPosition(helper.getLayoutPosition())
                                    .setStandardVideoAllCallBack(new VideoSimpleListener() {
                                        @Override
                                        public void onPrepared(String url, Object... objects) {
                                            super.onPrepared(url, objects);
                                            /*if (!gsyVideoPlayer.isIfCurrentIsFullscreen()) {
                                                //静音
                                                GSYVideoManager.instance().setNeedMute(true);
                                            }*/
                                            GSYVideoManager.instance().setNeedMute(false);
                                        }

                                        @Override
                                        public void onQuitFullscreen(String url, Object... objects) {
                                            super.onQuitFullscreen(url, objects);
                                            //全屏不静音
//                                            GSYVideoManager.instance().setNeedMute(true);
                                            GSYVideoManager.instance().setNeedMute(false);
                                        }

                                        @Override
                                        public void onEnterFullscreen(String url, Object... objects) {
                                            super.onEnterFullscreen(url, objects);
                                            GSYVideoManager.instance().setNeedMute(false);
                                        }
                                    }).build(gsyVideoPlayer);
                            //增加title
                            gsyVideoPlayer.getTitleTextView().setVisibility(View.GONE);

                            //设置返回键
                            gsyVideoPlayer.getBackButton().setVisibility(View.GONE);

                            //设置全屏按键功能
                            gsyVideoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    gsyVideoPlayer.startWindowFullscreen(CommunityMsgListActivity.this, true, true);
                                }
                            });
                        }
                    } else {
                        include_single.setVisibility(View.GONE);
                        include_multiple.setVisibility(View.VISIBLE);

                        if (size == 2 || size == 4)
                            rv_media.setLayoutManager(new GridLayoutManager(CommunityMsgListActivity.this, 2));
                        else
                            rv_media.setLayoutManager(new GridLayoutManager(CommunityMsgListActivity.this, 3));

                        PostMediaAdapter mediaAdapter = new PostMediaAdapter(R.layout.item_media, attachment);
                        rv_media.setAdapter(mediaAdapter);
                        rv_media.setNestedScrollingEnabled(false);
                    }
                } else {
                    frame_media.setVisibility(View.GONE);
                }

                //是否已读
                boolean isRead = PaperUtils.isPostRead(community_id, post_id);
                if (isRead) {
                    view_top.setBackgroundColor(ContextCompat.getColor(CommunityMsgListActivity.this, R.color.bg_gray));
                    iv_dot.setImageResource(R.mipmap.icon_dot_gray);
                } else {
                    view_top.setBackgroundColor(ContextCompat.getColor(CommunityMsgListActivity.this, R.color.bg_blue));
                    iv_dot.setImageResource(R.mipmap.icon_dot_blue);
                }

                //判断是否已展开
                if (listExpands.contains(post_id)) {
                    ll_detail.setVisibility(View.VISIBLE);
                    iv_arrow.setImageResource(R.mipmap.icon_graytriangle_up);
                    iv_tag_coin.setVisibility(View.GONE);
                } else {
                    ll_detail.setVisibility(View.GONE);
                    iv_arrow.setImageResource(R.mipmap.icon_graytriangle);

                    if (type.equals("1")) { //普通post
                        iv_tag_coin.setVisibility(View.GONE);
                    } else { //广告
                        if (is_collect.equals("1")) {
                            iv_tag_coin.setVisibility(View.GONE);
                        } else {
                            iv_tag_coin.setVisibility(View.VISIBLE);
                        }
                    }
                }

                //是否是自己发的
                if (creator_id.equals(PaperUtils.getMId())) //自己发的
                {
                    iv_chat.setVisibility(View.GONE);
                } else {
                    iv_chat.setVisibility(View.VISIBLE);
                }

                //点击事件监听
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

                //点击"more"
                iv_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (PaperUtils.isLogin()) { //已登录
                            String userId = PaperUtils.getMId();
                            if (userId.equals(creator_id)) {
                                if (postItemMenuSelfPopup == null) {
                                    postItemMenuSelfPopup = new PostItemMenuSelfPopup(CommunityMsgListActivity.this);
                                }
                                postItemMenuSelfPopup.setOnClickListener(new PostItemMenuSelfPopup.OnClickListener() {

                                    @Override
                                    public void deleteClick() {
                                        postItemMenuSelfPopup.dismiss();
                                        showDeleteDialog(itemData);
                                    }

                                    @Override
                                    public void shareClick(int shareType) {
                                        Logger.e("----->分享");
                                        postItemMenuSelfPopup.dismiss();
                                        getShareInfo(1, post_id, creator_id, shareType);
                                    }
                                });
                                postItemMenuSelfPopup.showPopup(iv_more);
                            } else {
                                if (postItemMenuPopup == null) {
                                    postItemMenuPopup = new PostItemMenuPopup(CommunityMsgListActivity.this);
                                }
                                postItemMenuPopup.setOnClickListener(new PostItemMenuPopup.OnClickListener() {
                                    @Override
                                    public void reportClick() {
                                        postItemMenuPopup.dismiss();
                                        goReportMsg(post_id, creator_id);
                                    }

                                    @Override
                                    public void shareClick(int shareType) {
                                        Logger.e("----->分享");
                                        postItemMenuPopup.dismiss();
                                        getShareInfo(1, post_id, creator_id, shareType);

                                    }
                                });
                                postItemMenuPopup.showPopup(iv_more);
                            }
                        } else {
                            if (postItemMenuPopup == null) {
                                postItemMenuPopup = new PostItemMenuPopup(CommunityMsgListActivity.this);
                            }
                            postItemMenuPopup.setOnClickListener(new PostItemMenuPopup.OnClickListener() {
                                @Override
                                public void reportClick() {
                                    postItemMenuPopup.dismiss();
                                    goLogin();
                                }

                                @Override
                                public void shareClick(int shareType) {
                                    Logger.e("----->分享");
                                    postItemMenuPopup.dismiss();
                                    getShareInfo(1, post_id, creator_id, shareType);
                                }
                            });
                            postItemMenuPopup.showPopup(iv_more);
                        }
                    }
                });

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
                            linearLayoutManager.scrollToPositionWithOffset(itemData.getPosition(), 0);
                        }

                        myPostAdapter.notifyItemChanged(itemData.getPosition());
                    }
                    break;
                case R.id.iv_collect:
                    Logger.e("----->领取广告奖励");
                    if (itemData != null) {
                        if (PaperUtils.isLogin()) {
                            collectAdvertising(itemData);
                        } else {
                            goLogin();
                        }
                    }
                    break;
                case R.id.iv_header:
                case R.id.tv_creator_name:
                    Logger.e("----->跳转用户信息");
                    if (itemData != null) {
                        PostItemEntity postItemEntity = itemData.getItem();
                        if (postItemEntity != null) {
                            PostItemEntity.MemberEntity memberEntity = postItemEntity.getMembers();
                            if (memberEntity != null) {
                                goMemberInfo(memberEntity);
                            }
                        }
                    }
                    break;
                case R.id.iv_chat:
                    Logger.e("----->跳转聊天");
                    if (itemData != null) {
                        if (PaperUtils.isLogin()) {
                            PostItemEntity postItemEntity = itemData.getItem();
                            if (postItemEntity != null) {
                                PostItemEntity.MemberEntity memberEntity = postItemEntity.getMembers();
                                if (memberEntity != null) {
                                    goChat(memberEntity);
                                }
                            }
                        } else {
                            goLogin();
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

    //删除消息弹框
    private void showDeleteDialog(final PostAdapter.ItemData itemData) {
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

    //跳转登录
    private void goLogin() {
        UIHelper.jump(this, LoginActivity.class);
    }

    //获取分享信息
    private void getShareInfo(int type, String post_id, String creator_id, final int shareType) {
        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("type", type + "");
        param.put("post_id", post_id);
        param.put("creator_id", creator_id);
        Call<ShareInfoEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .getShareInfo(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<ShareInfoEntity>() {
            @Override
            public void onSuccess(Call<ShareInfoEntity> call, Response<ShareInfoEntity> response) {
                ShareInfoEntity shareInfoEntity = response.body();
                if (shareInfoEntity != null) {
                    int code = shareInfoEntity.getCode();
                    if (code == Constants.CODE_SUCCESS) {
                        ShareInfoEntity.DataEntity dataEntity = shareInfoEntity.getData();
                        if (dataEntity != null) {
                            final String shareUrl = dataEntity.getUrl();
                            final String title = dataEntity.getTitle();
                            final String desc = dataEntity.getDescription();
                            String img = dataEntity.getImage();

                            umWeb = new UMWeb(shareUrl);
                            umWeb.setTitle(title);
                            umWeb.setThumb(new UMImage(CommunityMsgListActivity.this, img));
                            umWeb.setDescription(desc);
                            doShare(shareType);
                        }
                    }
                }
            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<ShareInfoEntity> call, Throwable t) {

            }
        });
    }

    //分享
    private void doShare(int share_type) {
        if (umWeb == null) {
            return;
        }
        SHARE_MEDIA share_media = SHARE_MEDIA.SINA;
        if (share_type == 1) {
            share_media = SHARE_MEDIA.WEIXIN;
        } else if (share_type == 2) {
            share_media = SHARE_MEDIA.WEIXIN_CIRCLE;
        } else if (share_type == 3) {
            share_media = SHARE_MEDIA.QQ;
        } else if (share_type == 4) {
            share_media = SHARE_MEDIA.QZONE;
        }
        new ShareAction(this)
                .withMedia(umWeb)
                .setPlatform(share_media)
                .setCallback(shareListener)
                .share();

    }

    private UMShareListener shareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            Logger.e("------>onStart");
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Logger.e("------>onResult");
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Logger.e("------>onError");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Logger.e("------>onCancel");
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    //跳转用户信息
    private void goMemberInfo(PostItemEntity.MemberEntity memberEntity) {
        String profile_id = memberEntity.getId();
        if (profile_id.equals(PaperUtils.getMId()))
            return;
        Bundle bundle = new Bundle();
        bundle.putString("profile_id", profile_id);
        UIHelper.jump(this, ContactInfoActivity.class, bundle);
    }

    //举报消息
    private void goReportMsg(String post_id, String create_id) {
        Bundle bundle = new Bundle();
        bundle.putString("post_id", post_id);
        bundle.putString("creator_id", create_id);
        UIHelper.jump(this, ReportPostActivity.class, bundle);
    }

    //删除Post
    private void deletePost(final PostAdapter.ItemData itemData) {
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

                            if (listPost.size() == 0) {
                                tvTips.setVisibility(View.VISIBLE);
                                tvPost.setVisibility(View.VISIBLE);
                                viewLine.setVisibility(View.GONE);
                                rvMsg.setVisibility(View.GONE);
                            } else {
                                tvTips.setVisibility(View.GONE);
                                tvPost.setVisibility(View.GONE);
                                viewLine.setVisibility(View.VISIBLE);
                                rvMsg.setVisibility(View.VISIBLE);
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
        bundle.putString("easemod_id", memberEntity.getEasemob_account());
        UIHelper.jump(this, ChatActivity.class, bundle);
    }

    private class PostMediaAdapter extends BaseQuickAdapter<PostItemEntity.AttachmentEntity, BaseViewHolder> {

        private List<PostItemEntity.AttachmentEntity> data = new ArrayList<>();
        private ArrayList<ThumbViewInfo> listImg = new ArrayList<>();

        public PostMediaAdapter(@LayoutRes int layoutResId, @Nullable List<PostItemEntity.AttachmentEntity> data) {
            super(layoutResId, data);
            this.data = data;

            for (int i = 0; i < data.size(); i++) {
                PostItemEntity.AttachmentEntity entity = data.get(i);
                if (entity != null) {
                    String att_type = entity.getAtt_type();
                    String url = entity.getUrl();
                    String thumbnail = entity.getThumbnail();

                    if (att_type.equals("1")) {
                        ThumbViewInfo thumbViewInfo = new ThumbViewInfo(url);
                        listImg.add(thumbViewInfo);
                    } else {
                        ThumbViewInfo thumbViewInfo = new ThumbViewInfo(thumbnail);
                        listImg.add(thumbViewInfo);
                    }
                }
            }
        }

        @Override
        protected void convert(final BaseViewHolder helper, PostItemEntity.AttachmentEntity item) {
            ImageView iv_photo = helper.getView(R.id.iv_photo);
            ImageView iv_video = helper.getView(R.id.iv_video);
            final String url = item.getUrl();
            String thumbnail = item.getThumbnail();
            final String att_type = item.getAtt_type();

            if (att_type.equals("1")) { //图片
                iv_video.setVisibility(View.GONE);
                ImageHelper.displayImageLocal(mContext, url, iv_photo);
            } else {
                iv_video.setVisibility(View.VISIBLE);
                ImageHelper.displayImageLocal(mContext, thumbnail, iv_photo);
            }

            iv_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (att_type.equals("1")) //图片
                    {
                        int curPosition = helper.getLayoutPosition();
                        scaleImg(listImg, curPosition);
                    } else {
                        playVideo(url);
                    }
                }
            });
        }
    }

    //查看大图
    private void scaleImg(ArrayList<ThumbViewInfo> thumbViewInfoList, int position) {
        GPreviewBuilder.from(this)
                .setData(thumbViewInfoList)
                .setCurrentIndex(position)
                .setType(GPreviewBuilder.IndicatorType.Number)
                .start();
    }

    //播放视频
    private void playVideo(String videoPath) {
        try {
            if (videoPath.startsWith("http")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(videoPath);
                intent.setDataAndType(uri, "video/*");
                startActivity(intent);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse("file://" + videoPath);
                intent.setDataAndType(uri, "video/mp4");
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

        int firstVisibleItem, lastVisibleItem;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case 0:
//                    Logger.e("----->已经停止滚动 ");
                    break;
                case 1:
//                    Logger.e("----->正在被拖拽 ");
                    break;
                case 2:
//                    Logger.e("----->正在依靠惯性滚动 ");
                    break;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            //大于0说明有播放
            if (GSYVideoManager.instance().getPlayPosition() >= 0) {
                //当前播放的位置
                int position = GSYVideoManager.instance().getPlayPosition();
                //对应的播放列表TAG
                if (GSYVideoManager.instance().getPlayTag().equals(PostAdapter.TAG)
                        && (position < firstVisibleItem || position > lastVisibleItem)) {

                    //如果滑出去了上面和下面就是否，和今日头条一样
                    //是否全屏
                    if (!mFull) {
                        GSYVideoPlayer.releaseAllVideos();
                        myPostAdapter.notifyDataSetChanged();
                    }
                }
            }

            rvMsg.setEnabled(firstVisibleItem == 0);

        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (newConfig.orientation != ActivityInfo.SCREEN_ORIENTATION_USER) {
            mFull = false;
        } else {
            mFull = true;
        }
    }

    @Override
    public void onBackPressed() {
        if (StandardGSYVideoPlayer.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GSYVideoManager.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (communityMenuPopup != null) {
            communityMenuPopup.dismiss();
            communityMenuPopup = null;
        }
        if (postItemMenuPopup != null) {
            postItemMenuPopup.dismiss();
            postItemMenuPopup = null;
        }
        if (postItemMenuSelfPopup != null) {
            postItemMenuSelfPopup.dismiss();
            postItemMenuSelfPopup = null;
        }
        GSYVideoPlayer.releaseAllVideos();
        UMShareAPI.get(this).release();
    }
}
