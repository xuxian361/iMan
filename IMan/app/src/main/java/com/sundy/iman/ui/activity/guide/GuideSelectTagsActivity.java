package com.sundy.iman.ui.activity.guide;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.TagListEntity;
import com.sundy.iman.entity.TagListItemEntity;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.ui.activity.BaseActivity;
import com.sundy.iman.ui.activity.MainActivity;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sundy on 17/11/10.
 */

public class GuideSelectTagsActivity extends BaseActivity {

    @BindView(R.id.tv_skip)
    TextView tvSkip;
    @BindView(R.id.rv_tags)
    RecyclerView rvTags;
    @BindView(R.id.tv_next)
    TextView tvNext;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;

    private TagsListAdapter tagsListAdapter;
    private List<TagListEntity.ListEntity> listTags = new ArrayList<>(); //所有列表数据
    private ArrayList<String> selectedTags = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_guide_select_tags);
        ButterKnife.bind(this);

        init();
        getTagList();
    }

    private void init() {
        tvNext.setSelected(false);
        tvNext.setEnabled(false);
        //设置第一次启动APP标志，设置后表示启动过APP
        PaperUtils.setFirstLaunchApp();

        rvTags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        tagsListAdapter = new TagsListAdapter(R.layout.item_tags_list, listTags);
        // Add the sticky headers decoration
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(tagsListAdapter);
        rvTags.addItemDecoration(headersDecor);

        rvTags.setAdapter(tagsListAdapter);

    }

    //获取标签列表
    private void getTagList() {
        Call<TagListEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .getTagList(ParamHelper.formatData(new HashMap<String, String>()));
        call.enqueue(new RetrofitCallback<TagListEntity>() {
            @Override
            public void onSuccess(Call<TagListEntity> call, Response<TagListEntity> response) {
                try {
                    if (response != null) {
                        TagListEntity entity = response.body();
                        if (entity != null) {
                            int code = entity.getCode();
                            if (code == Constants.CODE_SUCCESS) {
                                TagListEntity.DataEntity dataEntity = entity.getData();
                                if (dataEntity != null) {
                                    List<TagListEntity.ListEntity> list = dataEntity.getList();
                                    if (list != null && list.size() > 0) {
                                        if (listTags != null)
                                            listTags.clear();
                                        listTags.addAll(list);
                                        setData();
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<TagListEntity> call, Throwable t) {

            }
        });
    }

    //设置数据
    private void setData() {
        tagsListAdapter.setNewData(listTags);
        tagsListAdapter.notifyDataSetChanged();

    }

    private class TagsListAdapter extends BaseQuickAdapter<TagListEntity.ListEntity, BaseViewHolder>
            implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

        public TagsListAdapter(@LayoutRes int layoutResId, @Nullable List<TagListEntity.ListEntity> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final TagListEntity.ListEntity item) {
            try {
                final List<TagListItemEntity> listItemEntities = item.getItems();
                final TagFlowLayout flTab = helper.getView(R.id.fl_tab);
                final TagAdapter<TagListItemEntity> adapter_Tag = new TagAdapter<TagListItemEntity>(listItemEntities) {
                    @Override
                    public View getView(FlowLayout parent, int position, TagListItemEntity item) {
                        TextView tv = (TextView) getLayoutInflater().inflate(R.layout.item_tag_can_select,
                                flTab, false);
                        String title = item.getTitle();
                        if (!TextUtils.isEmpty(title)) {
                            tv.setText(title);
                        }

                        if (selectedTags != null && selectedTags.size() > 0) {
                            for (int i = 0; i < selectedTags.size(); i++) {
                                String selectStr = selectedTags.get(i);
                                if (!TextUtils.isEmpty(selectStr)) {
                                    if (selectStr.equals(title)) {
                                        tv.setBackgroundResource(R.drawable.corner_tag_blue);
                                        tv.setTextColor(ContextCompat.getColor(GuideSelectTagsActivity.this, R.color.txt_white));
                                    }
                                }
                            }
                        }
                        return tv;
                    }
                };
                flTab.setAdapter(adapter_Tag);
                flTab.setEnabled(true);
                flTab.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
                    @Override
                    public boolean onTagClick(View view, int position, FlowLayout parent) {
                        TagListItemEntity selectItem = listItemEntities.get(position);
                        String title = selectItem.getTitle();
                        if (!selectedTags.contains(title)) {
                            selectedTags.add(title);
                        } else {
                            selectedTags.remove(title);
                        }
                        Logger.e("==>" + selectedTags.size());
                        if (selectedTags.size() == 0) {
                            tvNext.setSelected(false);
                            tvNext.setEnabled(false);
                        } else {
                            tvNext.setSelected(true);
                            tvNext.setEnabled(true);
                        }
                        adapter_Tag.notifyDataChanged();
                        return false;
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public long getHeaderId(int position) {
            return Long.parseLong(getItem(position).getId());
        }

        @Override
        public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_tags_header, parent, false);
            return new RecyclerView.ViewHolder(view) {
            };
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
            TextView textView = (TextView) holder.itemView;
            textView.setText(getItem(position).getTitle());
            textView.setBackgroundColor(Color.parseColor("#D8FFFFFF"));
        }
    }

    @OnClick({R.id.tv_skip, R.id.tv_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_skip:
                goMain();
                break;
            case R.id.tv_next:
                saveTags();
                goSelectCommunity();
                break;
        }
    }

    //保存选择的标签
    private void saveTags() {
        PaperUtils.saveFavourTags(selectedTags);
    }

    //跳转选择社区列表
    private void goSelectCommunity() {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("selectedTags", selectedTags);
        UIHelper.jump(this, GuideSelectCommunityActivity.class, bundle);
        finish();
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
