package com.sundy.iman.paperdb;

import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.LocationEntity;

import io.paperdb.Paper;

/**
 * 定位信息Paper
 * Created by sundy on 17/9/27.
 */

public class LocationPaper {

    private static final String PAPER_KEY = "LocationPaper";

    /**
     * 保存定位信息
     */
    public static void saveLocation(LocationEntity locationEntity) {
        Paper.book().write(PAPER_KEY, locationEntity);
    }

    /**
     * 清除定位信息
     */
    public static void deleteLocation() {
        Paper.book().delete(PAPER_KEY);
    }

    /**
     * 获取定位信息
     */
    public static LocationEntity getLocation() {
        //Default Location
        LocationEntity locationEntity = new LocationEntity();
        locationEntity.setLng(Constants.DEFAULT_LOCATION_LNG);
        locationEntity.setLat(Constants.DEFAULT_LOCATION_LAT);
        return Paper.book().read(PAPER_KEY, locationEntity);
    }

}
