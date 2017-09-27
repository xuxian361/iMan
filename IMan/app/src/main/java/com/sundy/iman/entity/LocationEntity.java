package com.sundy.iman.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * 定位信息实体
 * Created by sundy on 17/9/27.
 */

@Data
public class LocationEntity implements Serializable {

    private double lat; //纬度
    private double lng; //经度
    private String country; //国家
    private String province; //省
    private String city; //市
    private String district; //区
    private String address; //地址

}
