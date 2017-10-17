package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.orhanobut.logger.Logger;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.CommunityItemEntity;
import com.sundy.iman.entity.CommunityListEntity;
import com.sundy.iman.entity.LocationEntity;
import com.sundy.iman.entity.MsgEvent;
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
import com.sundy.iman.view.popupwindow.SelectedCommunityPopup;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
 * Created by sundy on 17/10/5.
 */

public class SelectCommunityActivity extends BaseActivity {

    private static final int REQUEST_CODE_PERMISSION_LOCATION = 100;

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
    @BindView(R.id.iv_detail)
    ImageView ivDetail;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;

    private ArrayList<String> selectedCommunity = new ArrayList<>(); //已选择的社区列表
    private List<CommunityItemEntity> listCommunity = new ArrayList<>();
    private String keyword = "";
    private int page = 1; //当前页码
    private int perpage = 10; //每页显示条数
    private boolean canLoadMore = true;

    private CommunityAdapter communityAdapter;

    private LocationEntity locationEntity;

    private SelectedCommunityPopup selectedCommunityPopup;
    private boolean isBottomShow = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_select_community);
        ButterKnife.bind(this);

        EventBus.getDefault().register(this);
        initData();
        initTitle();
        init();
        if (listCommunity != null)
            listCommunity.clear();
        getCommunityList();
    }

    private void init() {
        ivDetail.setSelected(true);
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

        selectedCommunityPopup = new SelectedCommunityPopup(this);
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
            communityAdapter.notifyDataSetChanged();
            getCommunityList();
        }
    };

    //获取社区列表
    private void getCommunityList() {
        String country = "";
        String province = "";
        String city = "";
        String latitude = "";
        String longitude = "";
        String addressStr = "";
        if (locationEntity != null) {
            country = locationEntity.getCountry();
            province = locationEntity.getProvince();
            city = locationEntity.getCity();
            latitude = locationEntity.getLat() + "";
            longitude = locationEntity.getLng() + "";
            addressStr = locationEntity.getAddress();
        }

        Map<String, String> param = new HashMap<>();
        param.put("type", "3"); //1-全部社区, 2-我的社区, 3-发布广告的社区搜索, 4-加入推广社区搜索，5-我的推广社区
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("keyword", keyword);
        param.put("tags", "");
        param.put("province", province);
        param.put("city", city);
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

    @PermissionYes(REQUEST_CODE_PERMISSION_LOCATION)
    private void getPermissionLocationYes(@NonNull List<String> grantedPermissions) {
        Logger.e("位置权限申请成功!");
        goSelectLocationByMap();
    }

    @PermissionNo(REQUEST_CODE_PERMISSION_LOCATION)
    private void getPermissionLocationNo(@NonNull List<String> deniedPermissions) {
        Logger.e("位置权限申请失败!");

    }

    //获取定位全向
    private void getLocationPermission() {
        AndPermission.with(this)
                .requestCode(REQUEST_CODE_PERMISSION_LOCATION)
                .permission(Permission.LOCATION)
                .callback(this)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                        AndPermission.rationaleDialog(SelectCommunityActivity.this, rationale).show();
                    }
                })
                .start();
    }

    //跳转地图获取位置信息
    private void goSelectLocationByMap() {
        UIHelper.jump(this, SelectLocationByMapActivity.class);
    }

    @OnClick({R.id.rel_location, R.id.ll_bottom})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rel_location:
                getLocationPermission();
                break;
            case R.id.ll_bottom:
                if (!isBottomShow) {
                    showBottom();
                } else {
                    hideBottom();
                }
                break;
        }
    }

    //显示底部View
    private void showBottom() {
        if (selectedCommunityPopup != null) {
            isBottomShow = true;
            ivDetail.setSelected(false);
            selectedCommunityPopup.showUp(llBottom);
        }
    }

    //关闭底部View
    private void hideBottom() {
        if (selectedCommunityPopup != null) {
            isBottomShow = false;
            ivDetail.setSelected(true);
            selectedCommunityPopup.dismiss();
        }
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
            if (create_time != null) {
                Date date = DateUtils.formatTimeStamp2Date(Long.parseLong(create_time) * 1000);
                tv_create_date.setText(getString(R.string.created) + DateUtils.formatDate2String(date, "yyyy/MM/dd"));
            } else {
                tv_create_date.setText("");
            }

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MsgEvent event) {
        if (event != null) {
            String msg = event.getMsg();
            switch (msg) {
                case MsgEvent.EVENT_GET_LOCATION:
                    locationEntity = (LocationEntity) event.getObj();
                    setLocation(locationEntity);
                    page = 1;
                    if (listCommunity != null)
                        listCommunity.clear();
                    communityAdapter.notifyDataSetChanged();
                    getCommunityList();
                    break;
            }
        }
    }

    //设置选择回来的位置
    private void setLocation(LocationEntity locationEntity) {
        if (locationEntity != null) {
            String country = locationEntity.getCountry();
            String province = locationEntity.getProvince();
            String city = locationEntity.getCity();
            if (!TextUtils.isEmpty(province) && !TextUtils.isEmpty(city)) {
                tvLocation.setText(province + " " + city);
            } else if (TextUtils.isEmpty(province) && TextUtils.isEmpty(city)) {
                tvLocation.setText(country);
            } else {
                if (TextUtils.isEmpty(province)) {
                    tvLocation.setText(city);
                } else {
                    tvLocation.setText(province);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (selectedCommunityPopup != null) {
            isBottomShow = false;
            selectedCommunityPopup.dismiss();
            selectedCommunityPopup = null;
        }
    }

}
