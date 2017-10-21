package com.sundy.iman.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by sundy on 17/10/2.
 */

@Data
public class CommunityItemEntity implements Serializable {

    private String id; //社区id
    private String name; //社区名字
    private String introduction; //社区介绍
    private String province; //省份
    private String city; //城市
    private String location; //位置
    private String latitude; //纬度
    private String longitude; //经度
    private String members; //成员数
    private String tags; //标签
    private String is_creator; //是否是作者: 1-是，0-否
    private String is_join; //是否加入社区: 1-是，0-否
    private String create_time; //创建时间
    private String url;

}
