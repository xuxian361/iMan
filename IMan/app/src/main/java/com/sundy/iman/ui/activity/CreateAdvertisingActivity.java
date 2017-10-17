package com.sundy.iman.ui.activity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.sundy.iman.entity.CreateAdvertisingEntity;
import com.sundy.iman.entity.LocationEntity;
import com.sundy.iman.entity.MsgEvent;
import com.sundy.iman.entity.StaticContentEntity;
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
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sundy on 17/10/5.
 */

public class CreateAdvertisingActivity extends BaseActivity {

    private static final int REQUEST_CODE_PERMISSION_LOCATION = 100;

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
    @BindView(R.id.tv_select_community)
    TextView tvSelectCommunity;
    @BindView(R.id.rel_select_community)
    RelativeLayout relSelectCommunity;
    @BindView(R.id.rv_community)
    RecyclerView rvCommunity;
    @BindView(R.id.tv_total_user_title)
    TextView tvTotalUserTitle;
    @BindView(R.id.tv_total_user_value)
    TextView tvTotalUserValue;
    @BindView(R.id.tv_total_cost_title)
    TextView tvTotalCostTitle;
    @BindView(R.id.tv_total_cost_value)
    TextView tvTotalCostValue;
    @BindView(R.id.ll_state)
    LinearLayout llState;
    @BindView(R.id.et_tags)
    EditText etTags;
    @BindView(R.id.btn_add_tag)
    TextView btnAddTag;
    @BindView(R.id.fl_tags)
    TagFlowLayout flTags;
    @BindView(R.id.btn_more_tag)
    TextView btnMoreTag;
    @BindView(R.id.btn_confirm)
    TextView btnConfirm;
    @BindView(R.id.ll_how_get_imcoin)
    LinearLayout llHowGetImcoin;

    //声明AMapLocationClient类对象
    public AMapLocationClient locationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption locationOption = null;
    private Geocoder geocoder;
    private LocationEntity locationEntity;

    private final static int AGING = 48; //广告默认48小时消失
    private ArrayList<String> listCommunities = new ArrayList<>(); //选择的社区
    private ArrayList<String> selectedTags = new ArrayList<>(); //已选择的标签列表


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_create_ad);
        ButterKnife.bind(this);

        EventBus.getDefault().register(this);
        initTitle();
        init();
    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.advertise));
        titleBar.setOnClickListener(new OnTitleBarClickListener() {
            @Override
            public void onLeftImgClick() {
                final CommonDialog dialog = new CommonDialog(CreateAdvertisingActivity.this);
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
        btnConfirm.setSelected(false);
        btnConfirm.setEnabled(false);

        etSubject.addTextChangedListener(textWatcher);
        etDetail.addTextChangedListener(etDetailWatcher);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocationPermission();
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

    private TextWatcher etDetailWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String content = etDetail.getText().toString().trim();
            if (content.length() > 144) {
                tvBytes.setTextColor(ContextCompat.getColor(CreateAdvertisingActivity.this, R.color.main_red));
            } else {
                tvBytes.setTextColor(ContextCompat.getColor(CreateAdvertisingActivity.this, R.color.txt_normal));
            }

            tvBytes.setText("(" + content.length() + "/" + 144 + ")");

            canBtnClick();
        }
    };

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
                        AndPermission.rationaleDialog(CreateAdvertisingActivity.this, rationale).show();
                    }
                })
                .start();
    }

    //判断可否点击确认按钮
    private void canBtnClick() {
        String subject = etSubject.getText().toString().trim();
        if (TextUtils.isEmpty(subject) || listCommunities == null || listCommunities.size() == 0) {
            btnConfirm.setSelected(false);
            btnConfirm.setEnabled(false);
        } else {
            btnConfirm.setSelected(true);
            btnConfirm.setEnabled(true);
        }
    }

    @OnClick({R.id.rel_select_community, R.id.btn_add_tag, R.id.btn_more_tag, R.id.btn_confirm, R.id.ll_how_get_imcoin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rel_select_community:
                goSelectCommunity();
                break;
            case R.id.btn_add_tag:
                addTag();
                break;
            case R.id.btn_more_tag:
                goSelectTags();
                break;
            case R.id.ll_how_get_imcoin:
                getStaticContent(4);
                break;
            case R.id.btn_confirm:
                createAd();
                break;
        }
    }

    //跳转选择社区
    private void goSelectCommunity() {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("selectedCommunity", listCommunities);
        UIHelper.jump(this, SelectCommunityActivity.class, bundle);
    }

    //添加标签
    private void addTag() {
        String tag = etTags.getText().toString().trim();
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        if (tag.contains(",")) {
            MainApp.getInstance().showToast(getString(R.string.tag_cannot_contain_dot));
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

    //跳转选择标签
    private void goSelectTags() {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("selectedTags", selectedTags);
        UIHelper.jump(this, SelectTagsActivity.class, bundle);
    }

    //获取静态内容
    private void getStaticContent(int type) { //类型:1-使用条款，2-隐私条例，3-联系我们, 4-How to get imcoin?
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
        String title = "";
        String type = dataEntity.getType(); //类型 1-​使用条款 ,2-隐私条 例,3-联系 我们
        if (type.equals("1")) {
            title = getString(R.string.terms_of_use);
        } else if (type.equals("2")) {
            title = getString(R.string.privacy);
        } else if (type.equals("3")) {
            title = getString(R.string.contact_us);
        }
        if (TextUtils.isEmpty(url))
            return;
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("title", title);
        UIHelper.jump(this, WebActivity.class, bundle);
    }


    @PermissionYes(REQUEST_CODE_PERMISSION_LOCATION)
    private void getPermissionLocationYes(@NonNull List<String> grantedPermissions) {
        Logger.e("位置权限申请成功!");
        initLocation();
        startLocation();
    }

    @PermissionNo(REQUEST_CODE_PERMISSION_LOCATION)
    private void getPermissionLocationNo(@NonNull List<String> deniedPermissions) {
        Logger.e("位置权限申请失败!");
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

                    canBtnClick();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //设置显示选择的社区列表
    private void setCommunityList() {

    }

    //创建广告
    private void createAd() {
        String title = etSubject.getText().toString().trim();
        String detail = etDetail.getText().toString().trim();

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

        String communitys = "";
        if (listCommunities != null && listCommunities.size() > 0) {
            for (int i = 0; i < listCommunities.size(); i++) {
                String communityId = listCommunities.get(i);
                if (i == listCommunities.size() - 1) {
                    communitys = communityId;
                } else {
                    communitys = communityId + ",";
                }
            }
        }
        Logger.e("---->communitys = " + communitys);

        String tags = "";
        if (selectedTags != null && selectedTags.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < selectedTags.size(); i++) {
                String tag = selectedTags.get(i);
                if (!TextUtils.isEmpty(tag)) {
                    if (i == selectedTags.size() - 1) {
                        sb.append(tag);
                    } else {
                        sb.append(tag + ",");
                    }
                }
            }
            tags = sb.toString();
        }

        Logger.e("---->tags = " + tags);


        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("title", title); //post标题
        param.put("detail", detail); //post详情
        param.put("tags", tags); //标签
        param.put("location", longitude);
        param.put("latitude", latitude);
        param.put("longitude", addressStr);
        param.put("communitys", communitys); //社区ID:多个社区ID以“,”作为分隔符
        param.put("aging", AGING + ""); //时效
        param.put("attachment", ""); //附件 json格式数据:att_type为附件类型，1-图片，2-视频 url：附件存放路径
        Call<CreateAdvertisingEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .createAdvertising(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<CreateAdvertisingEntity>() {
            @Override
            public void onSuccess(Call<CreateAdvertisingEntity> call, Response<CreateAdvertisingEntity> response) {

            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<CreateAdvertisingEntity> call, Throwable t) {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MsgEvent event) {
        if (event != null) {
            String msg = event.getMsg();
            switch (msg) {
                case MsgEvent.EVENT_GET_COMMUNITIES:
                    listCommunities = (ArrayList<String>) event.getObj();
                    if (listCommunities != null && listCommunities.size() > 0) {
                        setCommunityList();
                    }
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
