package com.sundy.iman.entity;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 标签列表实体
 * Created by sundy on 17/9/26.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TagListEntity extends BaseResEntity {

    private DataEntity data;

    @Data
    public static class DataEntity implements Serializable {
        private List<TagListItemEntity> list;
    }

}
