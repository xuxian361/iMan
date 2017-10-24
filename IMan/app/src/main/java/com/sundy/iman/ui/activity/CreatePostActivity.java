package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.sundy.iman.R;
import com.sundy.iman.entity.CreatePostEntity;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.view.TitleBarView;
import com.sundy.iman.view.dialog.CommonDialog;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sundy on 17/10/6.
 */

public class CreatePostActivity extends BaseActivity {


    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.et_subject)
    EditText etSubject;
    @BindView(R.id.et_detail)
    EditText etDetail;
    @BindView(R.id.tv_bytes)
    TextView tvBytes;
    @BindView(R.id.rv_media)
    RecyclerView rvMedia;
    @BindView(R.id.et_tags)
    EditText etTags;
    @BindView(R.id.btn_add_tag)
    TextView btnAddTag;
    @BindView(R.id.fl_tags)
    TagFlowLayout flTags;
    @BindView(R.id.btn_more_tag)
    TextView btnMoreTag;
    @BindView(R.id.iv_arrow_expire_time)
    ImageView ivArrowExpireTime;
    @BindView(R.id.tv_expire_time)
    TextView tvExpireTime;
    @BindView(R.id.rel_expire_time)
    RelativeLayout relExpireTime;
    @BindView(R.id.btn_confirm)
    TextView btnConfirm;
    @BindView(R.id.ll_content)
    LinearLayout llContent;

    private String community_id;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_create_post);
        ButterKnife.bind(this);

        EventBus.getDefault().register(this);
        initData();
        initTitle();
//        getLocationPermission();
//        init();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            community_id = bundle.getString("community_id");
        }
    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.post_message));
        titleBar.setOnClickListener(new OnTitleBarClickListener() {
            @Override
            public void onLeftImgClick() {
                showBackTipsDialog();
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

    //返回上一页提示弹框
    private void showBackTipsDialog() {
        final CommonDialog dialog = new CommonDialog(CreatePostActivity.this);
        dialog.getTitle().setVisibility(View.GONE);
        dialog.getContent().setText(getString(R.string.if_return));
        dialog.setCancelable(true);
        dialog.getBtnOk().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });
    }


    //创建post
    private void createPost() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("title", ""); //post标题
        param.put("detail", ""); //post详情
        param.put("tags", ""); //标签
        param.put("location", ""); //位置
        param.put("latitude", "");
        param.put("longitude", "");
        param.put("community_id", community_id); //社区ID
        param.put("aging", ""); //时效
        param.put("attachment", ""); //附件 json格式数据:att_type为附件类型，1-图片，2-视频 url：附件存放路径
        Call<CreatePostEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .createPost(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<CreatePostEntity>() {
            @Override
            public void onSuccess(Call<CreatePostEntity> call, Response<CreatePostEntity> response) {

            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<CreatePostEntity> call, Throwable t) {

            }
        });
    }

    @OnClick({R.id.btn_add_tag, R.id.btn_more_tag, R.id.rel_expire_time, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add_tag:
                break;
            case R.id.btn_more_tag:
                break;
            case R.id.rel_expire_time:
                break;
            case R.id.btn_confirm:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Logger.i("-------->onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            showBackTipsDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
