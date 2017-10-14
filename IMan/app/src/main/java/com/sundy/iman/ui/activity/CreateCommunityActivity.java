package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.sundy.iman.MainApp;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.CreateCommunityEntity;
import com.sundy.iman.entity.LocationEntity;
import com.sundy.iman.entity.MsgEvent;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.view.TitleBarView;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sundy on 17/10/4.
 */

public class CreateCommunityActivity extends BaseActivity {

    private static final int REQUEST_CODE_PERMISSION_LOCATION = 100;

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.tv_bytes)
    TextView tvBytes;
    @BindView(R.id.fl_tab)
    TagFlowLayout flTab;
    @BindView(R.id.btn_more_tag)
    TextView btnMoreTag;
    @BindView(R.id.et_tags)
    EditText etTags;
    @BindView(R.id.iv_location)
    ImageView ivLocation;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.line_location)
    View lineLocation;
    @BindView(R.id.rel_location)
    RelativeLayout relLocation;
    @BindView(R.id.btn_confirm)
    TextView btnConfirm;

    private LocationEntity locationEntity;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_create_community);
        ButterKnife.bind(this);

        EventBus.getDefault().register(this);
        initTitle();
        init();
    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.create_community));
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
        etName.addTextChangedListener(textWatcher);
        etContent.addTextChangedListener(etAboutWatcher);
        etTags.addTextChangedListener(textWatcher);

        btnConfirm.setSelected(false);
        btnConfirm.setEnabled(false);

        flTab.setMaxSelectCount(5);
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
            canBtnClick();
        }
    };

    //判断可否点击确认按钮
    private void canBtnClick() {
        String name = etName.getText().toString().trim();
        String tags = etTags.getText().toString().trim();
        String location = tvLocation.getText().toString().trim();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(tags) || TextUtils.isEmpty(location)) {
            btnConfirm.setSelected(false);
            btnConfirm.setEnabled(false);
        } else {
            btnConfirm.setSelected(true);
            btnConfirm.setEnabled(true);
        }
    }

    private TextWatcher etAboutWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String content = etContent.getText().toString().trim();
            if (content.length() > 144) {
                tvBytes.setTextColor(ContextCompat.getColor(CreateCommunityActivity.this, R.color.main_red));
            } else {
                tvBytes.setTextColor(ContextCompat.getColor(CreateCommunityActivity.this, R.color.txt_normal));
            }

            tvBytes.setText("(" + content.length() + "/" + 144 + ")");

            canBtnClick();
        }
    };

    @OnClick({R.id.btn_more_tag, R.id.rel_location, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_more_tag:
                goSelectTags();
                break;
            case R.id.rel_location:
                locationClick();
                break;
            case R.id.btn_confirm:
                createCommunity();
                break;
        }
    }

    //点击定位
    private void locationClick() {
        AndPermission.with(this)
                .requestCode(REQUEST_CODE_PERMISSION_LOCATION)
                .permission(Permission.LOCATION)
                .callback(this)
                .start();
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

    //跳转地图获取位置信息
    private void goSelectLocationByMap() {
        UIHelper.jump(this, SelectLocationByMapActivity.class);
    }

    //跳转选择标签
    private void goSelectTags() {
        UIHelper.jump(this, SelectTagsActivity.class);
    }

    //创建社区
    private void createCommunity() {
        String name = etName.getText().toString().trim();
        String introduction = etContent.getText().toString().trim();
        String tags = etTags.getText().toString().trim();

        if (!TextUtils.isEmpty(tags)) {
            if (tags.contains("，")) {
                tags = tags.replace("，", ",");
            }
            Logger.e("---->tags 000=" + tags);

            if (tags.endsWith(",")) {
                tags = tags.substring(0, tags.length() - 2);
            }
        }

        Logger.e("---->tags=" + tags);
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
        param.put("mid", PaperUtils.getMId()); //用户ID
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("name", name);
        param.put("introduction", introduction);
        param.put("tags", tags);
        param.put("province", province);
        param.put("city", city);
        param.put("location", addressStr);
        param.put("latitude", latitude);
        param.put("longitude", longitude);
        Call<CreateCommunityEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .createCommunity(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<CreateCommunityEntity>() {
            @Override
            public void onSuccess(Call<CreateCommunityEntity> call, Response<CreateCommunityEntity> response) {
                CreateCommunityEntity createCommunityEntity = response.body();
                if (createCommunityEntity != null) {
                    int code = createCommunityEntity.getCode();
                    String msg = createCommunityEntity.getMsg();
                    if (code == Constants.CODE_SUCCESS) {

                    } else {
                        MainApp.getInstance().showToast(msg);
                    }
                }
            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<CreateCommunityEntity> call, Throwable t) {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MsgEvent event) {
        if (event != null) {
            String msg = event.getMsg();
            switch (msg) {
                case MsgEvent.EVENT_GET_LOCATION:
                    locationEntity = (LocationEntity) event.getObj();
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
                    canBtnClick();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
