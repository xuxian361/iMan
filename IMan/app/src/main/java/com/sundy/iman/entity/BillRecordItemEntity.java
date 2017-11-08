package com.sundy.iman.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by sundy on 17/11/8.
 */
@Data
public class BillRecordItemEntity implements Serializable {

    private String id;
    private String type; //类型: 1-创建广告支出，2-个人转出，3-个人转入，4-官方充值，5-阅览广告收人，6-推广社区收人，7-注册奖励
    private String income; //金额
    private String create_time; //创建时间
    private String remark; //备注
    private ItemEntity item;

    @Data
    public static class ItemEntity implements Serializable {
        private String id;
        private String username;
        private String profile_image;
        private String easemob_account;
    }
}
