package com.sundy.iman.ui.activity;

import android.location.Address;
import android.location.Geocoder;
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

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
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
import com.sundy.iman.view.dialog.CommonDialog;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
    @BindView(R.id.btn_add_tag)
    TextView btnAddTag;
    @BindView(R.id.fl_tags)
    TagFlowLayout flTags;

    //声明AMapLocationClient类对象
    public AMapLocationClient locationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption locationOption = null;
    private Geocoder geocoder;

    private LocationEntity locationEntity;
    private boolean permissionLocationOK = false; //是否开启了定位权限
    private ArrayList<String> selectedTags = new ArrayList<>(); //已选择的标签列表

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
        geocoder = new Geocoder(this, Locale.getDefault());
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocationPermission();
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
                        AndPermission.rationaleDialog(CreateCommunityActivity.this, rationale).show();
                    }
                })
                .start();
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
        String location = tvLocation.getText().toString().trim();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(location)
                || selectedTags == null || selectedTags.size() == 0) {
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

    @OnClick({R.id.btn_more_tag, R.id.rel_location, R.id.btn_confirm, R.id.btn_add_tag})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_more_tag:
                goSelectTags();
                break;
            case R.id.btn_add_tag:
                addTag();
                break;
            case R.id.rel_location:
                if (permissionLocationOK) {
                    goSelectLocationByMap();
                } else {
                    getLocationPermission();
                }
                break;
            case R.id.btn_confirm:
                createCommunity();
                break;
        }
    }

    //添加标签
    private void addTag() {
        String tag = etTags.getText().toString().trim();
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        if (selectedTags != null) {
            if (selectedTags.contains(tag)) {
                return;
            }
            selectedTags.add(tag);
        }
        setTagsList();
        etTags.setText("");
        closeKeyboard();
    }

    //设置标签列表
    private void setTagsList() {
        final TagAdapter<String> adapter_Tag = new TagAdapter<String>(selectedTags) {
            @Override
            public View getView(FlowLayout parent, int position, String item) {
                View view = getLayoutInflater().inflate(R.layout.item_tag_can_remove,
                        null, false);
                TextView tv = (TextView) view.findViewById(R.id.tv_tag);
                if (!TextUtils.isEmpty(item)) {
                    tv.setText(item);
                }
                return view;
            }
        };
        flTags.setAdapter(adapter_Tag);
        flTags.setEnabled(true);
        flTags.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, final int position, FlowLayout parent) {
                selectedTags.remove(selectedTags.get(position));
                adapter_Tag.notifyDataChanged();
                canBtnClick();
                return false;
            }
        });
    }

    @PermissionYes(REQUEST_CODE_PERMISSION_LOCATION)
    private void getPermissionLocationYes(@NonNull List<String> grantedPermissions) {
        Logger.e("位置权限申请成功!");
        permissionLocationOK = true;
        initLocation();
        startLocation();
    }

    @PermissionNo(REQUEST_CODE_PERMISSION_LOCATION)
    private void getPermissionLocationNo(@NonNull List<String> deniedPermissions) {
        Logger.e("位置权限申请失败!");
        permissionLocationOK = false;
    }

    //初始化定位
    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(this);
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    //默认的定位参数
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    //开始定位
    private void startLocation() {
        if (locationClient != null)
            locationClient.startLocation();
    }

    //停止定位
    private void stopLocation() {
        if (locationClient != null)
            locationClient.stopLocation();
    }

    //销毁定位
    private void destroyLocation() {
        if (null != locationClient) {
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    //定位监听
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    Logger.i("定位成功");
                    String address = location.getAddress();
                    if (!TextUtils.isEmpty(address)) {
                        Logger.i("获取定位信息成功");
                        stopLocation();
                        saveLocation(location.getLatitude(), location.getLongitude());
                    } else {
                        Logger.w("获取定位信息失败");
                    }
                } else {
                    //定位失败
                    Logger.e("定位失败");
                }
            } else {
                Logger.e("定位失败，loc is null");
            }
        }
    };

    //保存定位信息
    private void saveLocation(double latitude, double longitude) {
        Logger.e("---->lat = " + latitude);
        Logger.e("---->lng = " + longitude);
        try {
            List<Address> locationList = geocoder.getFromLocation(latitude, longitude, 1);
            Logger.e("---->size =" + locationList.size());
            if (locationList != null && locationList.size() > 0) {
                Address address = locationList.get(0);
                if (address != null) {
                    String country = address.getCountryName();
                    String province = address.getAdminArea();
                    String city = address.getLocality();
                    String district = address.getSubLocality();
                    String addressStr = country + " " + province + " " + city + " " + district + " " + address.getFeatureName();
                    Logger.e("----->国家 = " + country);
                    Logger.e("----->省份 = " + province);
                    Logger.e("----->城市 = " + city);
                    Logger.e("----->区域 = " + district);
                    Logger.e("----->门牌号 = " + addressStr);


                    locationEntity = new LocationEntity();
                    locationEntity.setCountry(address.getCountryName());
                    locationEntity.setProvince(address.getAdminArea());
                    locationEntity.setCity(address.getLocality());
                    locationEntity.setDistrict(address.getSubLocality());
                    locationEntity.setAddress(addressStr);
                    locationEntity.setLat(address.getLatitude());
                    locationEntity.setLng(address.getLongitude());

                    setLocation(locationEntity);
                    canBtnClick();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //跳转地图获取位置信息
    private void goSelectLocationByMap() {
        UIHelper.jump(this, SelectLocationByMapActivity.class);
    }

    //跳转选择标签
    private void goSelectTags() {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("selectedTags", selectedTags);
        UIHelper.jump(this, SelectTagsActivity.class, bundle);
    }

    //创建社区
    private void createCommunity() {
        String name = etName.getText().toString().trim();
        String introduction = etContent.getText().toString().trim();

        String tags = "";
        if (selectedTags != null && selectedTags.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < selectedTags.size(); i++) {
                String title = selectedTags.get(i);
                if (!TextUtils.isEmpty(title)) {
                    sb.append(title + ",");
                }
            }
            if (sb.length() > 0) {
                tags = sb.toString();
                if (tags.endsWith(",")) {
                    tags = tags.substring(0, tags.length() - 1);
                }
            }
        }

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
                        final CommonDialog dialog = new CommonDialog(CreateCommunityActivity.this);
                        dialog.getTitle().setText(getString(R.string.congratulations));
                        dialog.getBtnCancel().setVisibility(View.GONE);
                        dialog.getContent().setText(getString(R.string.create_community_success));
                        dialog.getBtnOk().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                finish();
                            }
                        });
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
                    setLocation(locationEntity);
                    canBtnClick();
                    break;
                case MsgEvent.EVENT_GET_TAGS:
                    selectedTags = (ArrayList<String>) event.getObj();
                    if (selectedTags != null && selectedTags.size() > 0) {
                        setTagsList();
                    }
                    canBtnClick();
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
    public void onPause() {
        super.onPause();
        stopLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        destroyLocation();
    }
}
