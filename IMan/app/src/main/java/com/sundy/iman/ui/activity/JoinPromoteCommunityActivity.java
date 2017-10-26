package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;
import com.sundy.iman.MainApp;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.CommunityItemEntity;
import com.sundy.iman.entity.CommunityListEntity;
import com.sundy.iman.entity.JoinPromoteCommunityEntity;
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
import retrofit2.Call;
import retrofit2.Response;

/**
 * 加入推广社区
 * Created by sundy on 17/10/20.
 */

public class JoinPromoteCommunityActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.rel_search)
    RelativeLayout relSearch;
    @BindView(R.id.rv_community)
    RecyclerView rvCommunity;

    private int page = 1; //当前页码
    private int perpage = 10; //每页显示条数
    private boolean canLoadMore = true;
    private CommunityAdapter communityAdapter;
    private List<CommunityItemEntity> listCommunity = new ArrayList<>();
    private String keyword = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_join_promote_community);
        ButterKnife.bind(this);

        initTitle();
        init();
        if (listCommunity != null)
            listCommunity.clear();
        getCommunityList();
    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.join_and_promote));
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

    private void init() {
        etSearch.setHint(getString(R.string.search_promote_community_hint));
        etSearch.addTextChangedListener(textWatcher);

        rvCommunity.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvCommunity.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        communityAdapter = new CommunityAdapter(R.layout.item_join_promote_community, listCommunity);
        communityAdapter.openLoadAnimation();
        communityAdapter.isFirstOnly(false);
        communityAdapter.setLoadMoreView(new CustomLoadMoreView());
        communityAdapter.setEnableLoadMore(true);
        communityAdapter.setOnLoadMoreListener(onLoadMoreListener, rvCommunity);
        rvCommunity.setAdapter(communityAdapter);
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

    //获取社区列表
    private void getCommunityList() {
        Map<String, String> param = new HashMap<>();
        param.put("type", "4"); //1-全部社区, 2-我的社区, 3-发布广告的社区搜索, 4-加入推广社区搜索，5-我的推广社区
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

    private class CommunityAdapter extends BaseQuickAdapter<CommunityItemEntity, BaseViewHolder> {
        public CommunityAdapter(@LayoutRes int layoutResId, @Nullable List<CommunityItemEntity> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final CommunityItemEntity item) {
            TextView tv_community_name = helper.getView(R.id.tv_community_name);
            TextView tv_join = helper.getView(R.id.tv_join);
            TextView tv_members = helper.getView(R.id.tv_members);
            TextView tv_create_date = helper.getView(R.id.tv_create_date);

            tv_community_name.setText(item.getName());

            String create_time = item.getCreate_time();
            if (create_time != null) {
                Date date = DateUtils.formatTimeStamp2Date(Long.parseLong(create_time) * 1000);
                tv_create_date.setText(getString(R.string.created) + " " + DateUtils.formatDate2String(date, "yyyy/MM/dd"));
            } else {
                tv_create_date.setText("");
            }
            tv_members.setText(getString(R.string.members) + " " + item.getMembers());

            String is_creator = item.getIs_creator(); //是否是作者 1-是,0-否
            String is_join = item.getIs_join(); //是否加入社区 1-是,0-否
            if (is_join.equals("1") || is_creator.equals("1")) {
                tv_join.setBackgroundResource(0);
                tv_join.setTextColor(ContextCompat.getColor(mContext, R.color.txt_gray));
                tv_join.setEnabled(false);
                tv_join.setText(getString(R.string.joined));
                tv_join.setVisibility(View.INVISIBLE);
            } else {
                tv_join.setBackgroundResource(R.drawable.semi_bg_blue);
                tv_join.setTextColor(ContextCompat.getColor(mContext, R.color.txt_white));
                tv_join.setEnabled(true);
                tv_join.setText(getString(R.string.join_and_promote));
                tv_join.setVisibility(View.VISIBLE);
            }

            tv_join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    joinPromoteCommunity(helper.getPosition(), item);
                }
            });
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

    //加入推广社区
    private void joinPromoteCommunity(final int position, final CommunityItemEntity item) {
        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("community_id", item.getId()); //社区ID
        param.put("promoter_id", ""); //推广者ID
        Call<JoinPromoteCommunityEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .joinPromoteCommunity(ParamHelper.formatData(param));
        showProgress();
        call.enqueue(new RetrofitCallback<JoinPromoteCommunityEntity>() {
            @Override
            public void onSuccess(Call<JoinPromoteCommunityEntity> call, Response<JoinPromoteCommunityEntity> response) {
                JoinPromoteCommunityEntity joinPromoteCommunityEntity = response.body();
                if (joinPromoteCommunityEntity != null) {
                    int code = joinPromoteCommunityEntity.getCode();
                    String msg = joinPromoteCommunityEntity.getMsg();
                    if (code == Constants.CODE_SUCCESS) {
                        item.setIs_join("1");
                        listCommunity.set(position, item);
                        communityAdapter.notifyItemChanged(position);
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
            public void onFailure(Call<JoinPromoteCommunityEntity> call, Throwable t) {

            }
        });
    }

}
