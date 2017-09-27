package com.sundy.iman.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.orhanobut.logger.Logger;
import com.sundy.iman.R;
import com.sundy.iman.entity.LocationEntity;
import com.sundy.iman.paperdb.LocationPaper;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.List;

/**
 * Created by sundy on 17/9/14.
 */

public class MsgFragment extends BaseFragment {

    private static final int REQUEST_CODE_PERMISSION_LOCATION = 100;

    //声明AMapLocationClient类对象
    public AMapLocationClient locationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption locationOption = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_msg, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        AndPermission.with(this)
                .requestCode(REQUEST_CODE_PERMISSION_LOCATION)
                .permission(Permission.LOCATION)
                .callback(this)
                // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                // 这样避免用户勾选不再提示，导致以后无法申请权限。
                // 你也可以不设置。
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                        AndPermission.rationaleDialog(mContext, rationale).show();
                    }
                })
                .start();
    }

    @PermissionYes(REQUEST_CODE_PERMISSION_LOCATION)
    private void getPermissionYes(@NonNull List<String> grantedPermissions) {
        Logger.e("位置权限申请成功!");
        initLocation();
        startLocation();
    }

    @PermissionNo(REQUEST_CODE_PERMISSION_LOCATION)
    private void getPermissionNo(@NonNull List<String> deniedPermissions) {
        Logger.e("位置权限申请失败!");
        //获取默认定位
        LocationEntity locationEntity = LocationPaper.getLocation();
    }

    //初始化定位
    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(mContext);
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
                        saveLocation(location);
                    } else {
                        Logger.w("获取定位信息失败");

                    }
                } else {
                    //定位失败
                    Logger.e("定位失败");

                }
            } else {
                Logger.e("定位失败，loc is null");
                //获取默认定位
                LocationEntity locationEntity = LocationPaper.getLocation();

            }
        }
    };

    //保存定位信息
    private void saveLocation(AMapLocation location) {
        LocationEntity locationEntity = new LocationEntity();
        locationEntity.setAddress(location.getAddress());
        locationEntity.setCountry(location.getCountry());
        locationEntity.setProvince(location.getProvince());
        locationEntity.setCity(location.getCity());
        locationEntity.setDistrict(location.getDistrict());
        locationEntity.setLat(location.getLatitude());
        locationEntity.setLng(location.getLongitude());

        LocationPaper.saveLocation(locationEntity);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyLocation();
    }

}
