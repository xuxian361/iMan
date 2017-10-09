package com.sundy.iman.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 静态内容实体
 * Created by sundy on 17/10/9.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StaticContentEntity extends BaseResEntity {

    private DataEntity data;

    @Data
    public static class DataEntity implements Serializable {
        private String id;
        private String type; //类型: 1-使用条款，2-隐私条例，3-联系我们
        private String url; //h5页面链接
    }
}
