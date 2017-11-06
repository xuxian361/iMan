package com.sundy.iman.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by sundy on 17/11/6.
 */
@Data
public class LastPostItemEntity implements Serializable {

    private String id; //社区ID
    private String name; //社区名字
    private PostInfo post_info;

    @Data
    public static class PostInfo implements Serializable {
        private String id; //post ID
        private String title; //post 标题
        private String type; //post 类型： 1 - 普通；2 - 广告
        private String create_time;
    }

}
