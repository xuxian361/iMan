package com.sundy.iman.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 分享内容实体
 * Created by sundy on 17/10/31.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ShareInfoEntity extends BaseResEntity {

    private DataEntity data;

    @Data
    public static class DataEntity implements Serializable {
        private String title;
        private String description;
        private String image;
        private String url;
    }
}
