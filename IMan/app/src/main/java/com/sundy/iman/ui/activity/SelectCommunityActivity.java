package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.CommunityItemEntity;
import com.sundy.iman.entity.CommunityListEntity;
import com.sundy.iman.entity.MsgEvent;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.view.CustomLoadMoreView;
import com.sundy.iman.view.DividerItemDecoration;
import com.sundy.iman.view.TitleBarView;
import com.sundy.iman.view.WrapContentLinearLayoutManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sundy on 17/10/5.
 */

public class SelectCommunityActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.rel_search)
    RelativeLayout relSearch;
    @BindView(R.id.tv_location_title)
    TextView tvLocationTitle;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.iv_location)
    ImageView ivLocation;
    @BindView(R.id.rel_location)
    RelativeLayout relLocation;
    @BindView(R.id.rv_community)
    RecyclerView rvCommunity;
    @BindView(R.id.tv_total_user)
    TextView tvTotalUser;

    private ArrayList<String> selectedCommunity = new ArrayList<>(); //已选择的社区列表
    private List<CommunityItemEntity> listCommunity = new ArrayList<>();
    private String keyword = "";
    private int page = 1; //当前页码
    private int perpage = 10; //每页显示条数
    private boolean canLoadMore = true;

    private CommunityAdapter communityAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_select_community);
        ButterKnife.bind(this);

        initData();
        initTitle();
        init();
        if (listCommunity != null)
            listCommunity.clear();
        getCommunityList();
    }

    private void init() {
        etSearch.setHint(getString(R.string.search_community_hint));
        etSearch.addTextChangedListener(textWatcher);

        rvCommunity.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvCommunity.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        communityAdapter = new CommunityAdapter(R.layout.item_select_community, listCommunity);
        communityAdapter.openLoadAnimation();
        communityAdapter.isFirstOnly(false);
        communityAdapter.setLoadMoreView(new CustomLoadMoreView());
        communityAdapter.setEnableLoadMore(true);
        communityAdapter.setOnLoadMoreListener(onLoadMoreListener, rvCommunity);
        rvCommunity.setAdapter(communityAdapter);
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            selectedCommunity = bundle.getStringArrayList("selectedCommunity");
            Logger.e("----->selectedCommunity = " + selectedCommunity.size());
        }
    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.select_communities));
        titleBar.setRightTvText(getString(R.string.confirm));
        titleBar.setRightTvVisibility(View.VISIBLE);
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
                sendEvent();
            }

            @Override
            public void onTitleClick() {

            }
        });
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
            getCommunityList();
        }
    };

    //获取社区列表
    private void getCommunityList() {
        Map<String, String> param = new HashMap<>();
        param.put("type", "3"); //1-全部社区, 2-我的社区, 3-发布广告的社区搜索, 4-加入推广社区搜索，5-我的推广社区
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

    //发送Msg Event通知选择好社区了
    private void sendEvent() {
        MsgEvent msgEvent = new MsgEvent();
        msgEvent.setMsg(MsgEvent.EVENT_GET_COMMUNITIES);
        msgEvent.setObj(selectedCommunity);
        EventBus.getDefault().post(msgEvent);

        finish();
    }

    @OnClick(R.id.rel_location)
    public void onViewClicked() {
    }

    private class CommunityAdapter extends BaseQuickAdapter<CommunityItemEntity, BaseViewHolder> {

        public CommunityAdapter(@LayoutRes int layoutResId, @Nullable List<CommunityItemEntity> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, CommunityItemEntity item) {
            TextView tv_community_name = helper.getView(R.id.tv_community_name);
            TextView tv_desc = helper.getView(R.id.tv_desc);
            TextView tv_num = helper.getView(R.id.tv_num);
            TextView tv_create_date = helper.getView(R.id.tv_create_date);
            ImageView iv_check = helper.getView(R.id.iv_check);

            String community_name = item.getName();
            String introduction = item.getIntroduction();
            String create_time = item.getCreate_time();
            String members = item.getMembers();

            tv_community_name.setText(community_name);
            tv_desc.setText(introduction);
            tv_create_date.setText(create_time);
            tv_num.setText(members);

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


}
