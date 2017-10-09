package com.sundy.iman.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * App Version 实体
 * Created by sundy on 17/9/20.
 */

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AppVersionEntity extends BaseResEntity {


    private DataEntity data;

    @Data
    public static class DataEntity implements Serializable {
        private String version; //版本号
        private String download_url; //下载链接
        private String description; //升级说明
        private String is_update; //是否提示更新:1-是，0-否
        private String forced_update; //是否强制升级:1-是，0-否

    }

}
