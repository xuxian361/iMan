package com.sundy.iman.ui.activity.guide;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
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
import com.sundy.iman.entity.JoinCommunityEntity;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.ui.activity.BaseActivity;
import com.sundy.iman.ui.activity.LoginActivity;
import com.sundy.iman.ui.activity.MainActivity;
import com.sundy.iman.utils.DateUtils;
import com.sundy.iman.view.CustomLoadMoreView;
import com.sundy.iman.view.DividerItemDecoration;
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
 * Created by sundy on 17/11/10.
 */

public class GuideSelectCommunityActivity extends BaseActivity {

    @BindView(R.id.rel_top)
    RelativeLayout relTop;
    @BindView(R.id.rv_community)
    RecyclerView rvCommunity;
    @BindView(R.id.tv_next)
    TextView tvNext;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;

    private ArrayList<String> selectedTags = new ArrayList<>();
    private int page = 1; //当前页码
    private int perpage = 10; //每页显示条数
    private boolean canLoadMore = true;
    private CommunityAdapter communityAdapter;
    private List<CommunityItemEntity> listCommunity = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_guide_select_community);
        ButterKnife.bind(this);

        initData();
        init();
        if (listCommunity != null)
            listCommunity.clear();
        getCommunityList();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            selectedTags = bundle.getStringArrayList("selectedTags");
        }
    }

    private void init() {
        rvCommunity.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvCommunity.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        communityAdapter = new CommunityAdapter(R.layout.item_add_community, listCommunity);
        communityAdapter.setLoadMoreView(new CustomLoadMoreView());
        communityAdapter.setEnableLoadMore(true);
        communityAdapter.setOnLoadMoreListener(onLoadMoreListener, rvCommunity);
        rvCommunity.setAdapter(communityAdapter);
    }

    //获取社区列表
    private void getCommunityList() {
        String tags = "";
        if (selectedTags != null && selectedTags.size() > 0) {
            for (int i = 0; i < selectedTags.size(); i++) {
                String tag = selectedTags.get(i);
                if (!TextUtils.isEmpty(tag)) {
                    if (i == selectedTags.size() - 1) {
                        tags = tags + tag;
                    } else {
                        tags = tags + tag + ",";
                    }
                }
            }
        }

        Map<String, String> param = new HashMap<>();
        param.put("type", "1"); //1-全部社区, 2-我的社区, 3-发布广告的社区搜索, 4-加入推广社区搜索，5-我的推广社区
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("keyword", "");
        param.put("tags", tags);
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

    @OnClick(R.id.tv_next)
    public void onViewClicked() {
        goMain();
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
            if (!TextUtils.isEmpty(create_time)) {
                Date date = DateUtils.formatTimeStamp2Date(Long.parseLong(create_time) * 1000);
                tv_create_date.setText(getString(R.string.created) + " " + DateUtils.formatDate2String(date, "yyyy/MM/dd"));
            } else {
                tv_create_date.setText("");
            }
            tv_members.setText(getString(R.string.members) + " " + item.getMembers());

            String is_creator = item.getIs_creator(); //是否是作者 1-是,0-否
            String is_join = item.getIs_join(); //是否加入社区 1-是,0-否
            if (is_join.equals("1") || is_creator.equals("1")) {
//                tv_join.setBackgroundResource(0);
//                tv_join.setTextColor(ContextCompat.getColor(mContext, R.color.txt_gray));
//                tv_join.setEnabled(false);
//                tv_join.setText(getString(R.string.joined));
                tv_join.setVisibility(View.INVISIBLE);
            } else {
                tv_join.setBackgroundResource(R.drawable.semi_bg_blue);
                tv_join.setTextColor(ContextCompat.getColor(mContext, R.color.txt_white));
                tv_join.setEnabled(true);
                tv_join.setText(getString(R.string.join));
                tv_join.setVisibility(View.VISIBLE);
            }

            tv_join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (PaperUtils.isLogin()) {
                        joinCommunity(helper.getPosition(), item);
                    } else {
                        goLogin();
                    }
                }
            });
        }
    }

    //加入社区
    private void joinCommunity(final int position, final CommunityItemEntity item) {
        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("type", "0"); //类型: 0-加入，1-退出
        param.put("community_id", item.getId());
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
            }

            @Override
            public void onFailure(Call<JoinCommunityEntity> call, Throwable t) {

            }
        });
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

    //跳转登录
    private void goLogin() {
        UIHelper.jump(this, LoginActivity.class);
    }

    //跳转首页
    private void goMain() {
        UIHelper.jump(this, MainActivity.class);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Logger.i("-------->onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
