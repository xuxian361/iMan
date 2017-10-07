package com.sundy.iman.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by sundy on 17/10/3.
 */

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CommunityInfoEntity extends BaseResEntity {

    private DataEntity data;

    @Data
    public static class DataEntity implements Serializable {
        private String id; //社区ID
        private String members; //加入社区人数或推广人数
        private String create_time; //创建时间
        private String name; //社区名字
        private String url; //社区链接或推广链接
        private String introduction; //社区介绍
    }
}
