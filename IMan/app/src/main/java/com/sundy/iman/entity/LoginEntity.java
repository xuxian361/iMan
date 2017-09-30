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
public class LoginEntity extends BaseResEntity {

    private LoginEntity.DataEntity data;

    @Data
    public static class DataEntity implements Serializable {
        private String id; //用户ID
        private String username; //用户名
        private String profile_image; //头像
        private String language;//语言en-英文, sc-简体中文, tc-繁体中文
        private String session_key; //Session key
        private String easemob_account;//环信账号
        private String easemob_password;//环信密码
        private String easemob_uuid; //环信ID

    }

}
