package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.BillRecordItemEntity;
import com.sundy.iman.entity.BillRecordListEntity;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.utils.DateUtils;
import com.sundy.iman.utils.NetWorkUtils;
import com.sundy.iman.utils.cache.CacheData;
import com.sundy.iman.view.CustomLoadMoreView;
import com.sundy.iman.view.DividerItemDecoration;
import com.sundy.iman.view.TitleBarView;
import com.sundy.iman.view.WrapContentLinearLayoutManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.DatePicker;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sundy on 17/11/8.
 */

public class MyImcoinActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.iv_calendar)
    ImageView ivCalendar;
    @BindView(R.id.tv_expenditure)
    TextView tvExpenditure;
    @BindView(R.id.tv_income)
    TextView tvIncome;
    @BindView(R.id.rv_record)
    RecyclerView rvRecord;
    @BindView(R.id.ll_null_tips)
    LinearLayout llNullTips;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;

    private int page = 1; //当前页码
    private int perpage = 10; //每页显示条数
    private boolean canLoadMore = true;
    private RecordAdapter mAdapter;
    private List<BillRecordItemEntity> listRecord = new ArrayList<>();
    private WrapContentLinearLayoutManager linearLayoutManager;
    private String year_month = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_imcoin);
        ButterKnife.bind(this);

        initTitle();
        init();
        if (listRecord != null)
            listRecord.clear();
        getRecord();
    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.record));
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
        Calendar calendar = Calendar.getInstance();
        year_month = DateUtils.formatDate2String(calendar.getTime(), "yyyy-MM");
        tvDate.setText(year_month);

        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                page = 1;
                if (listRecord != null)
                    listRecord.clear();
                mAdapter.notifyDataSetChanged();
                getRecord();
            }
        });

        linearLayoutManager = new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvRecord.setLayoutManager(linearLayoutManager);
        rvRecord.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new RecordAdapter(R.layout.item_record, listRecord);
        mAdapter.setLoadMoreView(new CustomLoadMoreView());
        mAdapter.setEnableLoadMore(true);
        mAdapter.setOnLoadMoreListener(onLoadMoreListener, rvRecord);
        rvRecord.setAdapter(mAdapter);
        rvRecord.addOnScrollListener(onScrollListener);
    }

    //获取记录
    private void getRecord() {
        if (TextUtils.isEmpty(year_month))
            return;

        String yearMonth = "";
        if (year_month.contains("-")) {
            String[] dateArr = year_month.split("-");
            if (dateArr != null && dateArr.length > 0) {
                for (String dateStr : dateArr) {
                    yearMonth = yearMonth + dateStr;
                }
            }
        }

        BillRecordListEntity.DataEntity dataEntity = CacheData.getInstance().getBillRecordList(yearMonth, page);
        if (dataEntity != null) {
            showData(dataEntity);
        }

        if (NetWorkUtils.isNetAvailable(this)) {
            final Map<String, String> param = new HashMap<>();
            param.put("mid", PaperUtils.getMId());
            param.put("session_key", PaperUtils.getSessionKey());
            param.put("year_month", yearMonth); //年月: 四位年份+两位月份
            param.put("page", page + "");
            param.put("perpage", perpage + "");
            Call<BillRecordListEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                    .getBillRecord(ParamHelper.formatData(param));
            final String finalYearMonth = yearMonth;
            call.enqueue(new RetrofitCallback<BillRecordListEntity>() {
                @Override
                public void onSuccess(Call<BillRecordListEntity> call, Response<BillRecordListEntity> response) {
                    BillRecordListEntity billRecordListEntity = response.body();
                    if (billRecordListEntity != null) {
                        int code = billRecordListEntity.getCode();
                        String msg = billRecordListEntity.getMsg();
                        if (code == Constants.CODE_SUCCESS) {
                            BillRecordListEntity.DataEntity dataEntity = billRecordListEntity.getData();
                            if (dataEntity != null) {
                                CacheData.getInstance().saveBillRecordList(dataEntity, finalYearMonth, page);
                                showData(dataEntity);
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
                public void onFailure(Call<BillRecordListEntity> call, Throwable t) {
                    if (swipeRefresh != null)
                        swipeRefresh.setRefreshing(false);
                }
            });
        } else {
            if (swipeRefresh != null)
                swipeRefresh.setRefreshing(false);
        }
    }

    private void showData(BillRecordListEntity.DataEntity dataEntity) {
        try {
            BillRecordListEntity.SummaryEntity summaryEntity = dataEntity.getSummary();
            if (summaryEntity != null) {
                tvExpenditure.setText(summaryEntity.getTotal_expenditure());
                tvIncome.setText(summaryEntity.getTotal_income());
            }

            List<BillRecordItemEntity> listData = dataEntity.getList();
            if (listData.size() == 0 && page == 1) {
                canLoadMore = false;
                mAdapter.loadMoreEnd();

                llNullTips.setVisibility(View.VISIBLE);
                rvRecord.setVisibility(View.GONE);

            } else {
                llNullTips.setVisibility(View.GONE);
                rvRecord.setVisibility(View.VISIBLE);

                if (listData.size() == 0) {
                    canLoadMore = false;
                    mAdapter.loadMoreEnd();
                } else {
                    page = page + 1;
                    canLoadMore = true;
                    mAdapter.loadMoreComplete();
                    for (int i = 0; i < listData.size(); i++) {
                        BillRecordItemEntity item = listData.get(i);
                        if (item != null) {
                            listRecord.add(item);
                        }
                    }
                    mAdapter.setNewData(listRecord);
                    mAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.iv_calendar)
    public void onViewClicked() {
        showDataPicker();
    }

    //显示时间选择器
    private void showDataPicker() {
        Calendar calendar = Calendar.getInstance();
        DatePicker picker = new DatePicker(this, DatePicker.YEAR_MONTH);
        picker.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        picker.setLabel(getString(R.string.year), getString(R.string.month), getString(R.string.day));
        picker.setWidth((int) (picker.getScreenWidthPixels()));
        picker.setRangeStart(2017, 6, 1);
        picker.setRangeEnd(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, 1);
        picker.setSelectedItem(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthPickListener() {
            @Override
            public void onDatePicked(String year, String month) {
                Logger.e("select date =" + year + "-" + month);
                year_month = year + "-" + month;
                tvDate.setText(year_month);
                page = 1;
                if (listRecord != null)
                    listRecord.clear();
                mAdapter.notifyDataSetChanged();
                getRecord();
            }
        });
        picker.show();
        picker.getCancelButton().setText(getString(R.string.cancel));
        picker.getSubmitButton().setText(getString(R.string.confirm));
    }

    private class RecordAdapter extends BaseQuickAdapter<BillRecordItemEntity, BaseViewHolder> {
        public RecordAdapter(@LayoutRes int layoutResId, @Nullable List<BillRecordItemEntity> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, BillRecordItemEntity item) {
            TextView tv_title = helper.getView(R.id.tv_title);
            TextView tv_date = helper.getView(R.id.tv_date);
            TextView tv_cost = helper.getView(R.id.tv_cost);

            String create_time = item.getCreate_time();
            String type = item.getType();
            String income = item.getIncome();

            BillRecordItemEntity.ItemEntity itemEntity = item.getItem();

            try {
                Date date = DateUtils.formatTimeStamp2Date(Long.parseLong(create_time) * 1000);
                tv_date.setText(DateUtils.formatDate2String(date, "MM-dd HH:mm"));

                if (type.equals("1")) { //创建广告支出
                    tv_cost.setText("-" + income);
                    tv_title.setText(getString(R.string.record_title_type_1));
                } else if (type.equals("2")) {//个人转出
                    tv_cost.setText("-" + income);
                    tv_title.setText(getString(R.string.record_title_type_2) + " " + itemEntity.getUsername());
                } else if (type.equals("3")) {//个人转入
                    tv_cost.setText("+" + income);
                    tv_title.setText(getString(R.string.record_title_type_3) + " " + itemEntity.getUsername());
                } else if (type.equals("4")) {//官方充值
                    tv_cost.setText("+" + income);
                    tv_title.setText(getString(R.string.record_title_type_4));
                } else if (type.equals("5")) {//阅览广告收人
                    tv_cost.setText("+" + income);
                    tv_title.setText(getString(R.string.record_title_type_5));
                } else if (type.equals("6")) {//推广社区收人
                    tv_cost.setText("+" + income);
                    tv_title.setText(getString(R.string.record_title_type_6));
                } else if (type.equals("7")) {//注册奖励
                    tv_cost.setText("+" + income);
                    tv_title.setText(getString(R.string.record_title_type_7));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private BaseQuickAdapter.RequestLoadMoreListener onLoadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            Logger.e("----->onLoadMoreRequested ");
            Logger.e("--->page = " + page);
            if (canLoadMore) {
                getRecord();
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
            rvRecord.setEnabled(FirstVisibleItemPosition == 0);
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
