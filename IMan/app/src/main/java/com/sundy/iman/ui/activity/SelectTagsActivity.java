package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.TagListEntity;
import com.sundy.iman.entity.TagListItemEntity;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.view.TitleBarView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sundy on 17/10/5.
 */

public class SelectTagsActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.fl_tab)
    TagFlowLayout flTab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_select_tags);
        ButterKnife.bind(this);

        initTitle();
        getTagList();
    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.select_tags));
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
                                    List<TagListItemEntity> list = dataEntity.getList();
                                    if (list != null && list.size() > 0) {
                                        setData(list);
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
    private void setData(List<TagListItemEntity> list) {
        TagAdapter<TagListItemEntity> adapter_Tag = new TagAdapter<TagListItemEntity>(list) {
            @Override
            public View getView(FlowLayout parent, int position, TagListItemEntity item) {
                TextView tv = (TextView) getLayoutInflater().inflate(R.layout.item_tag_can_select,
                        flTab, false);
                String title = item.getTitle();
                if (!TextUtils.isEmpty(title)) {
                    tv.setText(title);
                }
                return tv;
            }
        };
        flTab.setAdapter(adapter_Tag);
        flTab.setEnabled(true);

    }

    @OnClick(R.id.btn_confirm)
    public void onViewClicked() {
    }
}
