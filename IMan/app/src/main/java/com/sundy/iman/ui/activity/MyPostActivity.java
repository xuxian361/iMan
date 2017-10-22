package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.PostItemEntity;
import com.sundy.iman.entity.PostListEntity;
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

    private int page = 1; //当前页码
    private int perpage = 10; //每页显示条数
    private boolean canLoadMore = true;
    private MyPostAdapter myPostAdapter;
    private List<PostItemEntity> listPost = new ArrayList<>();


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
        rvPost.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvPost.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        myPostAdapter = new MyPostAdapter(R.layout.item_my_post, listPost);
        myPostAdapter.openLoadAnimation();
        myPostAdapter.isFirstOnly(false);
        myPostAdapter.setLoadMoreView(new CustomLoadMoreView());
        myPostAdapter.setEnableLoadMore(true);
        myPostAdapter.setOnLoadMoreListener(onLoadMoreListener, rvPost);
        rvPost.setAdapter(myPostAdapter);
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

    private class MyPostAdapter extends BaseQuickAdapter<PostItemEntity, BaseViewHolder> {

        public MyPostAdapter(@LayoutRes int layoutResId, @Nullable List<PostItemEntity> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, PostItemEntity item) {
            TextView tv_post_name = helper.getView(R.id.tv_post_name);
            TextView tv_status = helper.getView(R.id.tv_status);
            TextView tv_post_time = helper.getView(R.id.tv_post_time);
            TextView tv_end_time = helper.getView(R.id.tv_end_time);

            String title = item.getTitle();
            String create_time = item.getCreate_time();
            String effective_time = item.getEffective_time();
            String status = item.getStatus(); //post状态 1-有效,2-过期, 3-取消

            tv_post_name.setText(title);

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

}
