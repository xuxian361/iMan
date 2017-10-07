package com.sundy.iman.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by sundy on 17/10/3.
 */
@Data
public class MyPromoteCommunityItemEntity implements Serializable {

    private String id;
    private String community_id; //社区ID
    private String members; //推广人数
    private String create_time; //创建时间
    private String name; //社区名字
    private String url; //推广链接

}
