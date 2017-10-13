package com.sundy.iman.ui.activity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.MyLocationStyle;
import com.orhanobut.logger.Logger;
import com.sundy.iman.R;
import com.sundy.iman.entity.LocationEntity;
import com.sundy.iman.entity.MsgEvent;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.view.TitleBarView;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sundy on 17/10/12.
 */

public class SelectLocationByMapActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.map)
    MapView mMapView;
    private AMap aMap;
    private Geocoder geocoder;
    private LocationEntity locationEntity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_select_location_by_map);
        ButterKnife.bind(this);

        mMapView.onCreate(savedInstanceState);
        initTitle();
        initMap();

    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.map));
        titleBar.setRightTvVisibility(View.VISIBLE);
        titleBar.setRightTvText(getString(R.string.save));
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
                sendLocation();
                finish();
            }

            @Override
            public void onTitleClick() {

            }
        });
    }

    //发送位置信息Event
    private void sendLocation() {
        if (locationEntity != null) {
            String country = locationEntity.getCountry();
            if (!TextUtils.isEmpty(country)) {
                MsgEvent msgEvent = new MsgEvent();
                msgEvent.setMsg(MsgEvent.EVENT_GET_LOCATION);
                msgEvent.setObj(locationEntity);
                EventBus.getDefault().post(msgEvent);
            }
        }
    }

    private void initMap() {
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        //设置定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        myLocationStyle.interval(2000);
        aMap.setMyLocationStyle(myLocationStyle);
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setMyLocationEnabled(true);
        aMap.setOnCameraChangeListener(onCameraChangeListener);
        geocoder = new Geocoder(this, Locale.getDefault());
        locationEntity = new LocationEntity();

    }

    private AMap.OnCameraChangeListener onCameraChangeListener = new AMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {

        }

        @Override
        public void onCameraChangeFinish(CameraPosition cameraPosition) {
            Logger.e("---->lat = " + cameraPosition.target.latitude);
            Logger.e("---->lng = " + cameraPosition.target.longitude);
            try {
                List<Address> locationList = geocoder.getFromLocation(cameraPosition.target.latitude, cameraPosition.target.longitude, 1);
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

                        locationEntity.setCountry(address.getCountryName());
                        locationEntity.setProvince(address.getAdminArea());
                        locationEntity.setCity(address.getLocality());
                        locationEntity.setDistrict(address.getSubLocality());
                        locationEntity.setAddress(addressStr);
                        locationEntity.setLat(address.getLatitude());
                        locationEntity.setLng(address.getLongitude());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

}
