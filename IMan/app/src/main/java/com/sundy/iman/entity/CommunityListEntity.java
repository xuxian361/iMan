package com.sundy.iman.entity;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by sundy on 17/10/2.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CommunityListEntity extends BaseResEntity {

    private DataEntity data;

    @Data
    public static class DataEntity implements Serializable {
        private String total; //总记录数
        private String page; //当前页码
        private String perpage; //每页显示条数
        private List<CommunityItemEntity> list;
    }

}
