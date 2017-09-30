package com.sundy.iman.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by sundy on 17/9/30.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SaveMemberEntity extends BaseResEntity {

    private DataEntity data;

    @Data
    public static class DataEntity implements Serializable {
        private String id; //用户ID
        private String username; //用户名
        private String profile_image;//用户头像地址
        private String language; //语言en-英文,sc-简体中文,tc-繁体中文
        private String gender;//性别1-男，2-女
        private String location; //位置
        private String latitude; //纬度
        private String longitude; //经度
        private String introduction; //个人介绍
    }

}
