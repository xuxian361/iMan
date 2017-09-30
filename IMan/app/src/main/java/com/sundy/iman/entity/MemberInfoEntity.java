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
public class MemberInfoEntity extends BaseResEntity {

    private DataEntity data;

    @Data
    public static class DataEntity implements Serializable {
        private String id; //用户ID
        private String area_code; //地区编码
        private String phone; //手机号码
        private String email; //邮箱
        private String username; //用户名
        private String profile_image;//用户头像地址
        private String language; //语言en-英文,sc-简体中文,tc-繁体中文
        private String gender;//性别1-男，2-女
        private String location; //位置
        private String latitude; //纬度
        private String longitude; //经度
        private String introduction; //个人介绍
        private String balance; //账户余额
        private String easemob_account; //环信账号
        private String easemob_password; //环信密码
        private String easemob_uuid; //环信ID

    }
}
